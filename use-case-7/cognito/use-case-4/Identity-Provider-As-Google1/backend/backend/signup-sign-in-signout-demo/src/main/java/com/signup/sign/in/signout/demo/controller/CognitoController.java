package com.signup.sign.in.signout.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.signup.sign.in.signout.demo.models.AccessTokenRequest;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CognitoController {
	
	@Autowired
	private CognitoService cognitoService;
	//secure api by jwt token
	@GetMapping("/data")
    public ResponseEntity<Authentication> getData(HttpServletRequest  httpRequest, Authentication authentication) {
		System.out.println("inside ..data");
		return ResponseEntity.ok(authentication);
	}
	
	@PostMapping("/logout-me-out")
    public ResponseEntity logout(@RequestBody AccessTokenRequest accessToken ) {
        cognitoService.globalSignout(accessToken.getAccessToken());
        return ResponseEntity.ok().build();
        
    }
}


