package com.cognito;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class CognitoIntegrationApplication {

	public static void main(String[] args) throws MalformedURLException, IllegalArgumentException, JwkException, JsonMappingException, JsonProcessingException {
		SpringApplication.run(CognitoIntegrationApplication.class, args);
		
		//System.out.println(verifyToken("eyJraWQiOiJVZG5zOENNZEdOMGVDYklYQUxSWDhzTmV4UFE3Umo1ZVZTNzZab2tEc0lZPSIsImFsZyI6IlJTMjU2In0.eyJhdF9oYXNoIjoiYVFiRHdsWWxiREkwTE5HRG9IVHBfUSIsInN1YiI6IjA0ZThkNGY4LTMwNDEtNzAzZS03OWI0LWE5MjdjYjliZmI1MiIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV9RTWxES25kd2EiLCJjb2duaXRvOnVzZXJuYW1lIjoiZ3VydXByZWV0Iiwibm9uY2UiOiI4ZTgyYTRhYzYyMDZiMDNjZDczNGYzZTVkMGU4MDNmMzZjU3Flc3E0ayIsIm9yaWdpbl9qdGkiOiI4NzRkZTY1ZC1kODhlLTRmNWUtOTEzOC0xZmU4OTViNWExOWEiLCJhdWQiOiI0YXVsOGxmcDQxMWNyY3NuYzcwZmY0MmtnaCIsImV2ZW50X2lkIjoiMWRhOWE4NzQtYjViZi00YWIwLWFhNTQtYTYzYjI3ZWUzZTM2IiwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE3NDY1NTM2NTMsImV4cCI6MTc0NjU1NzI1MywiaWF0IjoxNzQ2NTUzNjUzLCJqdGkiOiJmNDBjZjNhMi0wNDA1LTRmMzctODE0Ny1iODE1ZjE0YWE2NTciLCJlbWFpbCI6IndyaXRldG9ndXJwcmVldHNpbmdoOEBnbWFpbC5jb20ifQ.dAJTTez6yoOgm7CfLMVMI_Hwgvzq3Id-xHlgAJIyfVRosDeZrAO1Sr5Af3KujVZO22BN0e1iGrolwlDY1HXQEZwk0Z8syDN_1bk_NPZ9Plz_jrykXTgDRy_SEM5gHZETnn6uXoPegoPYdgWG4Gvf82E4NR2mnRQ_q4fB635tzSnxzaOn5Ndk1gmGZmM4V8o-442rMpaQBqD2_nnxiYv_DfxErvi-lU-HcpZJNL4d3bzEVPCIQYn72twFB3FQDzrHnmHOai8vZ0XWjm-JUv5so1H8is3xXLzezckCWSYz1uyjcnbV2IQ1uF38bOAV0v0wXSdgTv_6s5Ee6LgynVNocg"));
		//System.out.println(verifyToken("eyJraWQiOiJWbUE2OXM3RG1HRU1wTEs5RSszQzJSaWcwSjQweDA5QzNLdGNFMmFETnR3PSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiIwNGU4ZDRmOC0zMDQxLTcwM2UtNzliNC1hOTI3Y2I5YmZiNTIiLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV9RTWxES25kd2EiLCJ2ZXJzaW9uIjoyLCJjbGllbnRfaWQiOiI0YXVsOGxmcDQxMWNyY3NuYzcwZmY0MmtnaCIsIm9yaWdpbl9qdGkiOiI4NzRkZTY1ZC1kODhlLTRmNWUtOTEzOC0xZmU4OTViNWExOWEiLCJldmVudF9pZCI6IjFkYTlhODc0LWI1YmYtNGFiMC1hYTU0LWE2M2IyN2VlM2UzNiIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoicGhvbmUgb3BlbmlkIGVtYWlsIiwiYXV0aF90aW1lIjoxNzQ2NTUzNjUzLCJleHAiOjE3NDY1NTcyNTMsImlhdCI6MTc0NjU1MzY1MywianRpIjoiNDkxZDcxOTMtNzU2Yy00NWM5LTk1NzctNzkyMDk5MDgzMGViIiwidXNlcm5hbWUiOiJndXJ1cHJlZXQifQ.cJrA2ojW9wr08bueVB4eo1xttM6CZJkpWDRSymv0xNTg2EjJySiZG48z8ddvilsruHvNwzrRtSyvaDQidaewMYkrBJq87Q2f09uW0I4BOQpkJ9iR_ASh7HZTvHKfSDuA5tk6tPDYqcN4M71yDBfgOomkH5O6KA0loIUM9X_Vke7xxOHOIR8YXwxuWJdaxkvTNYjB9_QChuFWCHwVN9LOh558niZB1NqedsgFxCEUiNHWDS41QJ-zIUXKG2L5fOVr1Cib2T8_0JQmNjgDpHZ9EPbBGAh8yOXhquHqbWjeuNsNF_j_GWxVk26ek1IgumzvAFkaVE5WDiCOJJpWJ0o08Q"));
	}
	
	private static DecodedJWT verifyToken(String token) throws MalformedURLException, IllegalArgumentException, JwkException, JsonMappingException, JsonProcessingException {
		String issuer = "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_QMlDKndwa";
		
		URL jwksUrl = new URL(issuer + "/.well-known/jwks.json");
		JwkProvider provider = new UrlJwkProvider(jwksUrl);
		
		DecodedJWT jwt = JWT.decode(token);
		Jwk jwk = provider.get(jwt.getKeyId());
		
		Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
	    JWTVerifier verifier = JWT.require(algorithm)
	        .withIssuer(issuer)
	        .build();

	    DecodedJWT token1 = verifier.verify(token);
	    System.out.println(token1.getPayload());
	    // Decode payload
	    String base64Payload = token1.getPayload();
	    byte[] decodedBytes = Base64.getUrlDecoder().decode(base64Payload);
	    String jsonPayload = new String(decodedBytes);

	    // Parse JSON payload
	    ObjectMapper objectMapper = new ObjectMapper();
	    Map<String, Object> payloadMap = objectMapper.readValue(jsonPayload, Map.class);

	    // Print all claims
	    payloadMap.forEach((k, v) -> System.out.println(k + " : " + v));

	    return token1;
	}

}
