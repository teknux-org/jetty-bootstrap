package org.genux.jettybootstrap;

public enum JettyConnectors {
	/**
	 * 
	 */
	DEFAULT(IJettyConnector.DEFAULT),

	/**
	 * 
	 */
	SSL(IJettyConnector.SSL),

	/**
	 * 
	 */
	DEFAULT_AND_SSL(IJettyConnector.DEFAULT + IJettyConnector.SSL),

	/**
	 * 
	 */
	REDIRECT_DEFAULT_TO_SSL(IJettyConnector.DEFAULT + IJettyConnector.SSL + IJettyConnector.REDIRECT_DEFAULT_TO_SSL);

	private Integer value;

	/**
	 * Private enum constructor.
	 * 
	 * @param value
	 */
	private JettyConnectors(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public boolean has(int iJettyConnectorValue) {
		return (getValue() & iJettyConnectorValue) != 0;
	}
}
