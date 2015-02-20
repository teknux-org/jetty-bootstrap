package org.teknux.jettybootstrap.keystore;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;


public class JettyKeystoreConvertorBuilder extends AbstractJettyKeystore {

    private static final String CERTIFICATE_TYPE_X509 = "X.509";

    private static final String PRIVATE_KEY_REGEX = "-----BEGIN [^-]*PRIVATE KEY-----\n([^-]*)\n-----END [^-]*PRIVATE KEY-----";
    private static final Pattern PRIVATE_KEY_PATTERN = Pattern.compile(PRIVATE_KEY_REGEX, Pattern.DOTALL);

    private static final String KEYSTORE_TYPE_JKS = "JKS";
    private static final String KEYSTORE_TYPE_PKCS12 = "PKCS12";

    private static final String DEFAULT_ALGORITHM = ALGORITHM_RSA;
    private static final String DEFAULT_CERTIFICATE_TYPE = CERTIFICATE_TYPE_X509;

    private PrivateKey privateKey;
    private Certificate certificate;

    public KeyStore build(String alias, String password) throws JettyKeystoreException {
        Objects.requireNonNull(alias, "Alias is required");
        Objects.requireNonNull(password, "Password is required");

        if (privateKey != null && certificate != null) {
            return createKeyStore(privateKey, certificate, alias, password);
        } else {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_CREATE_KEYSTORE, "Can not create keystore file");
        }
    }

    public JettyKeystoreConvertorBuilder setKeystore(InputStream inputStream, String password) throws JettyKeystoreException {
        return setKeystore(inputStream, password, null, KEYSTORE_TYPE_JKS);
    }

    public JettyKeystoreConvertorBuilder setKeystore(InputStream inputStream, String password, String alias, String type) throws JettyKeystoreException {
        KeyStore keystore = loadKeyStore(inputStream, password, type);

        try {
            PrivateKeyEntry privateKeyEntry = getPrivateKeyEntryOfKeyStore(keystore, password, alias);

            privateKey = privateKeyEntry.getPrivateKey();
            certificate = privateKeyEntry.getCertificate();
        } catch (JettyKeystoreException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_LOAD_KEYSTORE, "Can not load file (Keystore)", e);
        }

        return this;
    }

    public JettyKeystoreConvertorBuilder setPKCS8(InputStream inputStream) throws JettyKeystoreException {
        try {
            return setCertificateFromPKCS8(inputStream).setPrivateKeyFromPKCS8(inputStream);
        } catch (JettyKeystoreException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_LOAD_PKCS8, "Can not load file (PKCS8)", e);
        }
    }

    public JettyKeystoreConvertorBuilder setPKCS8(InputStream inputStream, String privateKeyAlgorythm, String certificateType) throws JettyKeystoreException {
        try {
            return setPrivateKeyFromPKCS8(inputStream, privateKeyAlgorythm).setCertificateFromPKCS8(inputStream, certificateType);
        } catch (JettyKeystoreException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_LOAD_PKCS8, "Can not load file (PKCS8)", e);
        }
    }

    public JettyKeystoreConvertorBuilder setPrivateKeyFromPKCS8(InputStream inputStream) throws JettyKeystoreException {
        return setPrivateKeyFromPKCS8(inputStream, DEFAULT_ALGORITHM);
    }

    public JettyKeystoreConvertorBuilder setPrivateKeyFromPKCS8(InputStream inputStream, String algorithm) throws JettyKeystoreException {
        try {
            String contentKeyFile = IOUtils.toString(inputStream);

            Matcher contentKeyMatcher = PRIVATE_KEY_PATTERN.matcher(contentKeyFile);

            String contentKey;
            if (contentKeyMatcher.find()) {
                contentKey = contentKeyMatcher.group(1);
            } else {
                contentKey = contentKeyFile;
            }

            byte[] decodedKeyFile = Base64.decode(contentKey);

            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodedKeyFile);
            privateKey = keyFactory.generatePrivate(privateKeySpec);

            return this;

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | DecoderException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_LOAD_PRIVATE_KEY_PKCS8, "Can not load private key (PKCS8)", e);
        }
    }

    public JettyKeystoreConvertorBuilder setCertificateFromPKCS8(InputStream inputStream) throws JettyKeystoreException {
        return setCertificateFromPKCS8(inputStream, DEFAULT_CERTIFICATE_TYPE);
    }

    public JettyKeystoreConvertorBuilder setCertificateFromPKCS8(InputStream inputStream, String certificateType) throws JettyKeystoreException {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance(certificateType);
            certificate = certificateFactory.generateCertificate(inputStream);

            return this;
        } catch (CertificateException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_LOAD_CERTIFICATE_PKCS8, "Can not load certificate (PKCS8)", e);
        }
    }

    public JettyKeystoreConvertorBuilder setPKCS12(InputStream inputStream, String password) throws JettyKeystoreException {
        return setPKCS12(inputStream, password, null);
    }

    public JettyKeystoreConvertorBuilder setPKCS12(InputStream inputStream, String password, String alias) throws JettyKeystoreException {
        KeyStore keystore = loadKeyStore(inputStream, password, KEYSTORE_TYPE_PKCS12);

        try {
            PrivateKeyEntry privateKeyEntry = getPrivateKeyEntryOfKeyStore(keystore, password, alias);

            privateKey = privateKeyEntry.getPrivateKey();
            certificate = privateKeyEntry.getCertificate();
        } catch (JettyKeystoreException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_LOAD_PKCS12, "Can not load file (PKCS12)", e);
        }

        return this;
    }

    public JettyKeystoreConvertorBuilder setPrivateKeyFromPKCS12(InputStream inputStream, String password) throws JettyKeystoreException {
        return setPrivateKeyFromPKCS12(inputStream, password, null);
    }

    public JettyKeystoreConvertorBuilder setPrivateKeyFromPKCS12(InputStream inputStream, String password, String alias) throws JettyKeystoreException {
        KeyStore keystore = loadKeyStore(inputStream, password, KEYSTORE_TYPE_PKCS12);

        try {
            privateKey = getPrivateKeyEntryOfKeyStore(keystore, password, alias).getPrivateKey();
        } catch (JettyKeystoreException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_LOAD_PRIVATE_KEY_PKCS12, "Can not load private key (PKCS12)", e);
        }

        return this;
    }

    public JettyKeystoreConvertorBuilder setCertificateFromPKCS12(InputStream inputStream, String password) throws JettyKeystoreException {
        return setCertificateFromPKCS12(inputStream, password, null);
    }

    public JettyKeystoreConvertorBuilder setCertificateFromPKCS12(InputStream inputStream, String password, String alias) throws JettyKeystoreException {
        KeyStore keystore = loadKeyStore(inputStream, password, KEYSTORE_TYPE_PKCS12);

        try {
            certificate = getPrivateKeyEntryOfKeyStore(keystore, password, alias).getCertificate();

            return this;
        } catch (JettyKeystoreException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_LOAD_CERTIFICATE_PKCS12, "Can not load certificate (PKCS12)", e);
        }
    }

    private static KeyStore loadKeyStore(InputStream inputStream, String password, String type) throws JettyKeystoreException {
        try {
            KeyStore keystore = KeyStore.getInstance(type);
            keystore.load(inputStream, password.toCharArray());

            return keystore;
        } catch (CertificateException | KeyStoreException | NoSuchAlgorithmException | IOException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_LOAD_KEYSTORE, "Can not load KeyStore (" + type + ")", e);
        }
    }

    private static PrivateKeyEntry getPrivateKeyEntryOfKeyStore(KeyStore keystore, String password, String alias) throws JettyKeystoreException {
        try {
            if (alias == null) {
                Enumeration<String> aliasEnumeration = keystore.aliases();

                while (aliasEnumeration.hasMoreElements()) {
                    String aliasItem = (String) aliasEnumeration.nextElement();

                    if (keystore.isKeyEntry(aliasItem)) {
                        Entry entry = keystore.getEntry(aliasItem, new KeyStore.PasswordProtection(password.toCharArray()));

                        if (entry instanceof PrivateKeyEntry) {
                            return (PrivateKeyEntry) entry;
                        }
                    }
                }

            } else {
                Entry entry = keystore.getEntry(alias, new KeyStore.PasswordProtection(password.toCharArray()));

                if (entry instanceof PrivateKeyEntry) {
                    return (PrivateKeyEntry) entry;
                }
            }

            throw new JettyKeystoreException(JettyKeystoreException.ERROR_UNREACHABLE_PRIVATE_KEY_ENTRY, "Can not find private key entry");
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_UNREACHABLE_PRIVATE_KEY_ENTRY, "Can not find private key entry", e);
        }
    }
}
