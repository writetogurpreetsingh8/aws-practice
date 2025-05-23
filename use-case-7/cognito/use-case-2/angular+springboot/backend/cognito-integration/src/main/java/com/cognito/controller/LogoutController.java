package com.cognito.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class LogoutController {
	
	
	@Value("${cognito.logoutUrl}")
	private String logoutUrl;
	
	@Value("${cognito.clientId}")
	private String clientId;
	
	@Value("${cognito.logoutRedirectUrl}")
	private String logoutRedirectUrl;
	
	@Autowired
	private CognitoService cognitoService;
	
	@GetMapping("/logout-me-out")
	public ResponseEntity<String> logout(HttpServletResponse response, @AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) throws IOException {
		//https://us-east-1cchscpcei.auth.us-east-1.amazoncognito.com/logout?client_id=4aul8lfp411crcsnc70ff42kgh&logout_uri=http://localhost:4200/logout
		String sendRedirect = logoutUrl+"?client_id="+clientId+"&"+"logout_uri="+logoutRedirectUrl;
		System.out.println(jwt.getTokenValue());
		cognitoService.globalSignout(jwt.getTokenValue());
		return ResponseEntity.ok(sendRedirect);
	}
}
