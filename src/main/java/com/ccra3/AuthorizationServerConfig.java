/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccra3;

import ch.qos.logback.classic.Logger;

import com.javainuse.config.ClientModel;


import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.logging.Level;
import javax.sql.DataSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author navaployw
 */
@Configuration
@Import(OAuth2AuthorizationServerConfiguration.class)
public class AuthorizationServerConfig {

    @Value("${oauth.authen.privateky}")
    private String privateKy;
    @Value("${oauth.authen.publicky}")
    private String publicKy;
    @Value("${spring.security.oauth2.authorizationserver.client.articles-client.registration.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.authorizationserver.client.articles-client.registration.client-secret}")
    private String clientSecret;

    private final Logger logger = (Logger) LoggerFactory.getLogger(AuthorizationServerConfig.class);

    @Autowired
    private ViewApiUserService userService;
    private List<ViewApiUser> userList = new ArrayList<>();
    private AuthenticationManager authenticationManager;

    @Qualifier("dataSource")
    DataSource dataSource;
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>RegisteredClientRepository<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        List<RegisteredClient> registeredClients = new ArrayList<>();

        try {
            userList = userService.findByflagActive(Boolean.TRUE);;
            SymmetricCipher cy = SymmetricCipher.getInstance();
            AesEcbEncryptDecrypt encryptObject = new AesEcbEncryptDecrypt();
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getPassword() != null && userList.get(i).getUserID() != null) {
                    logger.info("userList.get(i).getUserID():" + userList.get(i).getUserID());
                    logger.info("userList.get(i).getPassword():" + userList.get(i).getPassword());
                    String passDecode = cy.decrypt(userList.get(i).getPassword());
                    String infoLog = String.format(">>>passDecode::  %s", passDecode);
                    logger.info(infoLog);
                    AesEcbEncryptDecrypt.setKey(userList.get(i).getSecretKey());
                    ClientModel cc1 = new ClientModel();
                    infoLog = String.format(">>>userList.get(i).getUserId()::  %s", userList.get(i).getUserID());
                    logger.info(infoLog);
                    cc1.setClientId(userList.get(i).getUserID());
                    cc1.setClientSecret(passDecode);
                    infoLog = String.format(">>>clientKey.getClientId():::  %s", cc1.getClientId());
                    logger.info(infoLog);
                    String usrOAuth = encryptObject.encrypt(cc1.getClientId()).replaceAll(java.util.regex.Pattern.quote("+"), " ");
                    String pwdOAuth = encryptObject.encrypt(cc1.getClientSecret());
                    infoLog = String.format("usrOAuth>>>  %s", usrOAuth);
                    logger.info(infoLog);
                    infoLog = String.format("pwdOAuth>>>  %s", pwdOAuth);
                    logger.info(infoLog);
                    this.setClientId(usrOAuth);
                    this.setClientSecret(pwdOAuth);
                    logger.info(">>>clientId::"+this.clientId);
                    logger.info(">>>clientSecret::"+this.clientSecret);
                    RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                            .clientId(this.clientId)
                            .clientSecret(this.clientSecret)
                            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                            .scope("articles.read")
                            .build();

                    registeredClients.add(registeredClient);
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AuthorizationServerConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new InMemoryRegisteredClientRepository(registeredClients);
    }

//@Bean
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
//    logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>authServerSecurityFilterChain<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
//    return http.formLogin(Customizer.withDefaults()).build();
//}
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    private static RSAKey generateRsa() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    private static KeyPair generateRsaKey() {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);

        } catch (NoSuchAlgorithmException ex) {
            java.util.logging.Logger.getLogger(AuthorizationServerConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        return keyPairGenerator.generateKeyPair();
    }

}
