package com.dnb.apigatewayservice.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenMissingException extends AuthenticationException{
	public JwtTokenMissingException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}
	private static final long serialVersionUid= 1L;
	

}
