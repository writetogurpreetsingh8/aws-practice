package com.cognito.config;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cognito.config.utilities.PKCEStore;
import com.cognito.config.utilities.PKCEUtil;
import com.cognito.pkce.state.PKCEState;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CognitoAuthorizationFilter extends OncePerRequestFilter {
	
	@Value("${cognito.domain}")
    private String cognitoDomain;
    
	@Value("${cognito.clientId}")
    private String clientId;
    
    @Value("${cognito.callbackUri}")
    private String calBackUri;
    
    @Value("${cognito.redirectUrl}")
    private String redirectUrl;
    
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
			
			if(isLoginRequest((HttpServletRequest)request)){
				redirectToCognitoHostedUI(response);
				return;
			}
			else if(isAuthRequest(request)) {
				try {
					cognitoTokensExchange(request,response);
				} catch (URISyntaxException | IOException | InterruptedException | IllegalAccessException e) {
					throw new RuntimeException("Error: occurred while retrieving token ",e);
				}
				return;
			}
			else if(isRefreshTokenReqeust(request)) {
				try {
					cognitoTokensByRefreshTokenExchange(request,response);
				} catch (URISyntaxException | IOException | InterruptedException e) {
					throw new RuntimeException("Error: occurred while retrieving token ",e);
				}
				return;
			}
			filterChain.doFilter(request, response);
	}
	

	private boolean isRefreshTokenReqeust(HttpServletRequest request) {
		return request.getServletPath().equalsIgnoreCase("/auth/refresh-token");
	}

	private boolean isAuthRequest(HttpServletRequest request) {
		
		return request.getServletPath().equalsIgnoreCase("/auth/callback");
	}

	private boolean isLoginRequest(HttpServletRequest request){
		
		if(request.getServletPath().equalsIgnoreCase("/") 
				&& request.getMethod().equalsIgnoreCase("GET")){
			return true;	
		}
		if(request.getServletPath().equalsIgnoreCase("/**") 
				&& request.getMethod().equalsIgnoreCase("GET")){
			return true;	
		}
		return false;
		
	}
	
	//1) When the browser hits http://localhost:8080 → redirect to Hosted UI
	private void redirectToCognitoHostedUI(HttpServletResponse response) throws IOException {
		String scope = "email openid phone aws.cognito.signin.user.admin";
    	String encodedScope = URLEncoder.encode(scope, StandardCharsets.UTF_8); // still gives `+`
    	
    	String codeVerifier = PKCEUtil.generateCodeVerifier();
        String codeChallenge = PKCEUtil.generateCodeChallenge(codeVerifier);
        String state = PKCEUtil.generateState();
        
        PKCEStore.put(state, new PKCEState(state, codeVerifier));
        encodedScope = encodedScope.replace("+", "%20");
        
        String redirectUri = URLEncoder.encode(
        		calBackUri, StandardCharsets.UTF_8);
        
        String url = String.format(
          "%s/oauth2/authorize?response_type=code&client_id=%s"
          + "&scope=%s&state=%s&code_challenge=%s&code_challenge_method=S256&redirect_uri=%s",
          cognitoDomain, clientId, encodedScope,state,codeChallenge,redirectUri);
        
        response.sendRedirect(url);
	}
	
	private void cognitoTokensExchange(HttpServletRequest request, HttpServletResponse response) throws URISyntaxException, IOException, InterruptedException, IllegalAccessException {
	    
	    String code = request.getParameter("code");
	    String state = request.getParameter("state");
	    
	    PKCEState pkceState = PKCEStore.consume(state);
	    
	    if(Objects.isNull(state) || !pkceState.getState().equals(state)) {
	    	throw new IllegalAccessException("invalid or forged request, state parameter either not available or doesn't matched!");
	    }
	    
	    String form = "grant_type=authorization_code"
                + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(calBackUri, StandardCharsets.UTF_8)
                + "&code_verifier=" + URLEncoder.encode(pkceState.getCodeVerifier(), StandardCharsets.UTF_8);
	    
	    Map<String, Object> tokens = invokeHttpClient(form);
	    setTokensInHttpCookie(tokens,response,false);
	    response.sendRedirect(redirectUrl);//redirect to our UI
	}
	
	private void cognitoTokensByRefreshTokenExchange(HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException, InterruptedException {
		
		String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> jsonMap = mapper.readValue(body, new TypeReference<Map<String,String>>() {});
		
		String refreshToken = jsonMap.get("refresh_token");
		if (Objects.isNull(refreshToken) || refreshToken.isBlank()) {
	        throw new RuntimeException("Missing refresh_token");
	    }
		
		String form = "grant_type=refresh_token"
		        + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
		        + "&refresh_token=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
		
		Map<String, Object> tokens = invokeHttpClient(form);
	    setTokensInHttpCookie(tokens,response,true);
	    response.setStatus(HttpServletResponse.SC_OK);
	}
	
	private Map<String, Object> invokeHttpClient(String form) throws URISyntaxException, IOException, InterruptedException{
	
		final HttpClient httpClient = HttpClient.newHttpClient();
	    final ObjectMapper objectMapper = new ObjectMapper();
	    
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

	private void setTokensInHttpCookie(Map<String, Object> tokens,HttpServletResponse response, boolean exemptSettingRefreshToken) {
		
		 ResponseCookie idCookie = ResponseCookie.from("id_token", String.valueOf(tokens.get("id_token")))
	                .httpOnly(false)
	                .secure(true)
	                .sameSite("None")
	                .path("/")
	                .build();
	    	
	    	ResponseCookie accessCookie = ResponseCookie.from("access_token", String.valueOf(tokens.get("access_token")))
	                .httpOnly(false)
	                .secure(true)
	                .sameSite("None")
	                .path("/")
	                .build();
	    	
	    	if(!exemptSettingRefreshToken) {
	    		ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", String.valueOf(tokens.get("refresh_token")))
	    				.httpOnly(false)
	    				.secure(true)
	    				.sameSite("None")
	    				.path("/")
	    				.build();	    		
	    		response.addHeader("Set-Cookie", refreshCookie.toString());
	    	}
	    	
	    	response.addHeader("Set-Cookie", idCookie.toString());
	    	response.addHeader("Set-Cookie", accessCookie.toString());
	}
}
