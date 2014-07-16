package org.teknux.jettybootstrap.utils;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.util.security.Constraint;

public class JettyConstraintUtil {
    /**
     * Create constraint which redirect to Secure Port
     * 
     * @return @ConstraintSecurityHandler
     */
    public static ConstraintSecurityHandler getConstraintSecurityHandlerConfidential() {
        Constraint constraint = new Constraint();
        constraint.setDataConstraint(Constraint.DC_CONFIDENTIAL);

        ConstraintMapping constraintMapping = new ConstraintMapping();
        constraintMapping.setConstraint(constraint);
        constraintMapping.setPathSpec("/*");

        ConstraintSecurityHandler constraintSecurityHandler = new ConstraintSecurityHandler();
        constraintSecurityHandler.addConstraintMapping(constraintMapping);

        return constraintSecurityHandler;
    }
}
