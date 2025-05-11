package com.cognito.pkce.state;

public class PKCEState {
	
	private String state;
	private String codeVerifier;
	
	public String getCodeVerifier() {
		return codeVerifier;
	}
	
	public String getState() {
		return state;
	}

	public PKCEState(String state, String codeVerifier) {
		super();
		this.state = state;
		this.codeVerifier = codeVerifier;
	}
	

}
