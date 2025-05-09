package com.cognito.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cognito.config.utilities.PKCEStore;
import com.cognito.config.utilities.PKCEUtil;
import com.cognito.pkce.state.PKCEState;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class CognitoController {
	
	
	@Value("${cognito.domain}")
    private String cognitoDomain;
    
	@Value("${cognito.clientId}")
    private String clientId;
    
    @Value("${cognito.callbackUri}")
    private String calBackUri;
    
    @Value("${cognito.redirectUrl}")
    private String redirectUrl;
	
	// 1) When the browser hits http://localhost:8080 → redirect to Hosted UI
    //@GetMapping("/")
    public void rootRedirect(HttpServletResponse resp) throws IOException {
    	
    	String scope = "email openid phone";
    	String encodedScope = URLEncoder.encode(scope, StandardCharsets.UTF_8); // still gives `+`
    	
    	String codeVerifier = PKCEUtil.generateCodeVerifier();
        String codeChallenge = PKCEUtil.generateCodeChallenge(codeVerifier);
        String state = PKCEUtil.generateState();
        
        PKCEStore.put(state, new PKCEState(state, codeVerifier));
     // Replace '+' with '%20' manually
        encodedScope = encodedScope.replace("+", "%20");
        
        String redirectUri = URLEncoder.encode(
        		calBackUri, StandardCharsets.UTF_8);
        
        String url = String.format(
          "%s/oauth2/authorize?response_type=code&client_id=%s"
          + "&scope=%s&state=%s&code_challenge=%s&code_challenge_method=S256&code_verifier=%s&redirect_uri=%s",
          cognitoDomain, clientId, encodedScope,state,codeChallenge, codeVerifier,redirectUri);
        resp.sendRedirect(url);
    }
    
    
 // 2) callback handler stays the same
    //@GetMapping("/auth/callback")
    public void callback(@RequestParam String code, @RequestParam String state, HttpServletResponse resp) throws Exception {
    	// exchange code for tokens
    	CognitoTokenExchange cognitoTokenExchange = new CognitoTokenExchange();
    	
    	Map<String, Object> tokens = cognitoTokenExchange.exchangeCodeForTokens(
    	        code,
    	        clientId,
    	        calBackUri,
    	        cognitoDomain,
    	        state
    	    );
    	
    	ResponseCookie idCookie = ResponseCookie.from("id_token", String.valueOf(tokens.get("id_token")))
                .httpOnly(false)
                .secure(true)
                .path("/")
                .build();
    	
    	ResponseCookie accessCookie = ResponseCookie.from("access_token", String.valueOf(tokens.get("access_token")))
                .httpOnly(false)
                .secure(true)
                .path("/")
                .build();
    	
    	ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", String.valueOf(tokens.get("refresh_token")))
                .httpOnly(false)
                .secure(true)
                .path("/")
                .build();
    	
    	resp.addHeader("Set-Cookie", idCookie.toString());
    	resp.addHeader("Set-Cookie", accessCookie.toString());
    	resp.addHeader("Set-Cookie", refreshCookie.toString());
    	resp.sendRedirect(redirectUrl);//redirect to our UI
    }
    
    
	@GetMapping("/me")
    public Map<String, Object> user(Authentication authentication) {
		OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
		 OidcUser oidcUser = (OidcUser) oauthToken.getPrincipal();
        return Map.of(
            "email", oidcUser.getEmail()
        );
    }
	
	@GetMapping("/data")
    public ResponseEntity<Authentication> getData(HttpServletRequest  httpRequest, Authentication authentication) {
		return ResponseEntity.ok(authentication);
	}
}


class CognitoTokenExchange {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> exchangeCodeForTokens(String code, String clientId, 
    		String redirectUri, String cognitoDomain, String state) throws Exception {
    	
    	 PKCEState pkceState = PKCEStore.consume(state);
    	 
    	 if(Objects.isNull(state) || !pkceState.getState().equals(state)) {
 	    	throw new IllegalAccessException("invalid or forged request, state parameter either not available or doesn't matched!");
 	    }
    	 
        String form = "grant_type=authorization_code"
                    + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                    + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                    + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                    + "&code_verifier=" + URLEncoder.encode(pkceState.getCodeVerifier(), StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(cognitoDomain + "/oauth2/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Token request failed: " + response.body());
        }

        return objectMapper.readValue(response.body(), Map.class); // contains access_token, id_token, refresh_token
    }
}
