package org.teknux.jettybootstrap;

public class JettyBootstrapException extends Exception {

	private static final long serialVersionUID = 3855898924513137363L;

	public JettyBootstrapException(String s) {
		super(s);
	}

	public JettyBootstrapException(Throwable throwable) {
		super(throwable);
	}

	public JettyBootstrapException(String s, Throwable throwable) {
		super(s, throwable);
	}
}
