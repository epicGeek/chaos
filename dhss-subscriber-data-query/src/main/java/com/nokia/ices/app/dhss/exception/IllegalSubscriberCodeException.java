package com.nokia.ices.app.dhss.exception;

public class IllegalSubscriberCodeException extends SubscriberQueryException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1963334465527067762L;
	public static final String CODE = "401";
	public static final String MESSAGE = "code 401 : Illegal subscriber code.Please input IMSI or MSISDN.";

	public IllegalSubscriberCodeException() {
		super();
		this.fillInStackTrace();
	}
}
