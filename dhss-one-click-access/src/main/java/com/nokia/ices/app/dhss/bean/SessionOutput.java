package com.nokia.ices.app.dhss.bean;

/**
 * Output from ssh session
 */
public class SessionOutput {
	String sessionId;
	StringBuilder output = new StringBuilder();

	public SessionOutput() {

	}

	public SessionOutput(String sessionId) {
		this.sessionId = sessionId;

	}

	public StringBuilder getOutput() {
		return output;
	}

	public void setOutput(StringBuilder output) {
		this.output = output;
	}

}