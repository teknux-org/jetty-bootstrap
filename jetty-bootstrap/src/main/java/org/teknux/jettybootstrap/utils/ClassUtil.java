package org.teknux.jettybootstrap.utils;

import java.util.List;

public class ClassUtil {
    
    public static boolean classExists(String className) {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            return false;
        }
        
        return true;
    }
    
    public static boolean classesExists(List<String> classNames) {
        for (String className : classNames) {
            if (! classExists(className)) {
                return false;
            }
        }
        
        return true;
    }
}
