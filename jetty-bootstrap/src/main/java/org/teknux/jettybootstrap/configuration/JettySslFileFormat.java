package org.teknux.jettybootstrap.configuration;

public enum JettySslFileFormat {
    UNKNOWN,
    PKCS8,
    PKCS12;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static JettySslFileFormat getByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
