package com.signup.sign.in.signout.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CognitoController {
	
	//secure api by jwt token
	@GetMapping("/data")
    public ResponseEntity<Authentication> getData(HttpServletRequest  httpRequest, Authentication authentication) {
		System.out.println("inside ..data");
		return ResponseEntity.ok(authentication);
	}
}


