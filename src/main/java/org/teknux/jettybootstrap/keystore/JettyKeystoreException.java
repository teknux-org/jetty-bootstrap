package org.teknux.jettybootstrap.keystore;

public class JettyKeystoreException extends Exception {

	private static final long serialVersionUID = 3855898924513137363L;

	public JettyKeystoreException(String s) {
		super(s);
	}

	public JettyKeystoreException(Throwable throwable) {
		super(throwable);
	}

	public JettyKeystoreException(String s, Throwable throwable) {
		super(s, throwable);
	}
}
