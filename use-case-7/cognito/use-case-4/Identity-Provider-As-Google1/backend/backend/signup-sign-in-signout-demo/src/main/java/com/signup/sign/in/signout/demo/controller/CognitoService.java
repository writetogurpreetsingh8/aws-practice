package com.signup.sign.in.signout.demo.controller;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GlobalSignOutRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GlobalSignOutResponse;

@Service
public class CognitoService {

	// Create Cognito client
	CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder().region(Region.US_EAST_1) // Change
																													// to
																													// your
																													// User
																													// Pool
																													// region
			.build();

	public void globalSignout(String accessToken) {

		try {
			System.out.println(accessToken);
			GlobalSignOutRequest signOutRequest = GlobalSignOutRequest.builder().accessToken(accessToken).build();

			GlobalSignOutResponse signOutResponse = cognitoClient.globalSignOut(signOutRequest);

			System.out.println("User successfully signed out globally. Response: " + signOutResponse);

		} catch (CognitoIdentityProviderException e) {
			System.err.println("GlobalSignOut failed: " + e.awsErrorDetails().errorMessage());
			e.printStackTrace();
		} finally {
			//cognitoClient.close();
		}
	}

}
