package org.teknux.jettybootstrap.configuration;

import java.util.Arrays;
import java.util.List;

public class AdditionalWebAppJettyConfigurationClass {

	public enum Position {
		BEFORE,
		AFTER
	}
	
	public static AdditionalWebAppJettyConfigurationClass[] getAdditionalsWebAppJettyConfigurationClasses() {
		return new AdditionalWebAppJettyConfigurationClass[] {
			new AdditionalWebAppJettyConfigurationClass("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", Position.BEFORE, "org.eclipse.jetty.annotations.AnnotationConfiguration")
		};
	};
	
	private static final Position DEFAULT_POSITION = Position.AFTER; 
	
	private List<String> classes = null;
	private Position position = null;
	private String referenceClass = null;
	
	public AdditionalWebAppJettyConfigurationClass(String... classes) {
		this(null, DEFAULT_POSITION, classes);
	}
	
	public AdditionalWebAppJettyConfigurationClass(Position position, String... classes) {
		this(null, position, classes);
	}
	
	public AdditionalWebAppJettyConfigurationClass(String referenceClass, Position position, String... classes) {
		this.classes = Arrays.asList(classes);
		this.position = position;
		this.referenceClass = referenceClass;
	}

	public List<String> getClasses() {
		return classes;
	}

	public void setClasses(List<String> classes) {
		this.classes = classes;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public String getReferenceClass() {
		return referenceClass;
	}

	public void setReferenceClass(String referenceClass) {
		this.referenceClass = referenceClass;
	}
}
