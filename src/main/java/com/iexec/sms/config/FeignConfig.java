package com.iexec.sms.config;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Client;
import feign.Logger;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Configuration
public class FeignConfig {

    private SslConfig sslConfig;

    public FeignConfig(SslConfig sslConfig) {
        this.sslConfig = sslConfig;
    }

    public RestTemplate getRestTemplate(){
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setSSLContext(sslConfig.getSslContext());
        clientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(clientBuilder.build());
        return new RestTemplate(factory);
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Client feignClient() {
        return new Client.Default(sslConfig.getSslContext().getSocketFactory(),
                NoopHostnameVerifier.INSTANCE);
    }
}
