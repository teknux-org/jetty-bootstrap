package org.genux.jettybootstrap;

public class JettyException extends Exception {

	private static final long serialVersionUID = 3855898924513137363L;

	public JettyException(String s) {
		super(s);
	}

	public JettyException(Throwable throwable) {
		super(throwable);
	}

	public JettyException(String s, Throwable throwable) {
		super(s, throwable);
	}
}
