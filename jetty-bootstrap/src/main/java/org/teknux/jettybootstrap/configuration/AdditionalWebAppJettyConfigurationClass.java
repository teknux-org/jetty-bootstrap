package org.teknux.jettybootstrap.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teknux.jettybootstrap.utils.ClassUtil;

public class AdditionalWebAppJettyConfigurationClass {

    private final static Logger logger = LoggerFactory.getLogger(AdditionalWebAppJettyConfigurationClass.class);
    
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
	
	public static String[] addConfigurationClasses(String[] defaultConfigurationClasses, AdditionalWebAppJettyConfigurationClass[] additionalsWebappConfigurationClasses) {
        List<String> configurationClasses = new ArrayList<String>(Arrays.asList(defaultConfigurationClasses));
        
        for (AdditionalWebAppJettyConfigurationClass additionalWebappConfigurationClass : additionalsWebappConfigurationClasses) {
            if (additionalWebappConfigurationClass.getClasses() == null || additionalWebappConfigurationClass.getPosition() == null) {
                logger.warn("Bad support class name");
            } else {
                if (ClassUtil.classesExists(additionalWebappConfigurationClass.getClasses())) {
                    int index = 0;
                    
                    if (additionalWebappConfigurationClass.getReferenceClass() == null) {
                        if (additionalWebappConfigurationClass.getPosition() == Position.AFTER) {
                            index = configurationClasses.size();
                        }
                    } else {
                        index = configurationClasses.indexOf(additionalWebappConfigurationClass.getReferenceClass());
                        
                        if (index == -1) {
                            if (additionalWebappConfigurationClass.getPosition() == Position.AFTER) {
                                logger.warn("[{}] reference unreachable, add at the end", additionalWebappConfigurationClass.getReferenceClass());
                                index = configurationClasses.size();
                            } else {
                                logger.warn("[{}] reference unreachable, add at the top", additionalWebappConfigurationClass.getReferenceClass());
                                index = 0;
                            }
                        } else {
                            if (additionalWebappConfigurationClass.getPosition() == Position.AFTER) {
                                index++;
                            }
                        }
                    }
                    
                    configurationClasses.addAll(index, additionalWebappConfigurationClass.getClasses());
                    
                    for (String className : additionalWebappConfigurationClass.getClasses()) {
                        logger.debug("[{}] support added", className);
                    }
                } else {
                    for (String className : additionalWebappConfigurationClass.getClasses()) {
                        logger.debug("[{}] not available", className);
                    }
                }
            }
        }

        // List configurations
        for (String configurationClasse : configurationClasses) {
            logger.trace("Jetty WebAppContext Configuration => " + configurationClasse);
        }

        return configurationClasses.toArray(new String[configurationClasses.size()]);
    }
}
