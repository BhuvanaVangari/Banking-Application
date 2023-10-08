package com.dnb.accountservice.exceptions;

public class AccountCloseException extends Exception {
	public AccountCloseException(String msg) {
		super(msg);
	}

	@Override
	public String toString() {
		return super.toString() + super.getMessage();
	}
}
