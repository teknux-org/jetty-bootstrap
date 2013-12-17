package org.genux.jettybootstrap;

public interface IJettyConnector {

	public static final int DEFAULT = Integer.parseInt("00000001", 2);
	public static final int SSL = Integer.parseInt("00000010", 2);
	public static final int REDIRECT_DEFAULT_TO_SSL = Integer.parseInt("00000100", 2);
}
