package com.iexec.sms.config;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
//@Getter
public class SslConfig {

    //private SSLContext sslContext;

    private String sslKeystore;
    private String sslKeystoreType;
    private String sslKeyAlias;
    private char[] password;


    public SslConfig(
            @Value("${server.ssl.key-store}") String sslKeystore,
            @Value("${server.ssl.key-store-type}") String sslKeystoreType,
            @Value("${server.ssl.key-alias}") String sslKeyAlias,
            @Value("${server.ssl.key-store-password}") String sslKeystorePassword) throws Exception {
        this.sslKeystore = sslKeystore;
        this.sslKeystoreType = sslKeystoreType;
        this.sslKeyAlias = sslKeyAlias;

        password = sslKeystorePassword.toCharArray();
        //this.sslContext = getSslContext(sslKeystore, sslKeystoreType, sslKeyAlias, password);
    }

    public SSLContext getSslContext() {
        try {
            return SSLContexts.custom()
                .setKeyStoreType(sslKeystoreType)
                .loadKeyMaterial(new File(sslKeystore),
                        password,
                        password,
                        (aliases, socket) -> sslKeyAlias)
                .loadTrustMaterial(null, (chain, authType) -> true)////TODO: Add CAS certificate to truststore
                .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | IOException | CertificateException | UnrecoverableKeyException e) {
            e.printStackTrace();
            return null;
        }
    }
}
