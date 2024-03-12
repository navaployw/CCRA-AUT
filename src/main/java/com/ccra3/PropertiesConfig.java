/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ccra3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author kumpeep
 */
@PropertySource("classpath:application.properties")
public class PropertiesConfig {
    @Value("${spring.security.oauth2.authorizationserver.client.articles-client.registration.client-id}")
    private String clientId;
     @Value("${spring.security.oauth2.authorizationserver.client.articles-client.registration.client-secret}")
    private String clientSecret;
}
