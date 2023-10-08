package com.dnb.apigatewayservice.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenMalformedException extends AuthenticationException {
private static final long serialVersionUid= 1L;
	public JwtTokenMalformedException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}
