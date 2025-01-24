/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javainuse.config;

import ch.qos.logback.classic.Logger;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author navaployw
 */
public class MyJwt extends JwtAccessTokenConverter {

    @Autowired
    SpmUserService spmUser;

    @Autowired
    ViewApiUserService viewApiUser;

    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

   

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        try {
            logger.info(">>>enhance<<<");
            String infoLog = String.format(">>>accessToken::  %s",accessToken);
            logger.info(infoLog);
            infoLog = String.format(">>>authentication::  %s",authentication);
            logger.info(infoLog);

            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            if (requestAttributes == null || request.getParameter("ai_code")==null) {
                return null;
            }
            
            String aiCode = request.getParameter("ai_code");
            infoLog = String.format(">>>>aiCode::  %s",aiCode);
            logger.info(infoLog);
            Map<String, Object> additionalInformation = new LinkedHashMap<>();
            additionalInformation.put("ai_code", aiCode);
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
            ((DefaultOAuth2AccessToken) accessToken).setExpiration(new Date(new Date().getTime() + (1000 * 60 * 60 * 24)));
            accessToken = super.enhance(accessToken, authentication);
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(new LinkedHashMap<>());
            ((DefaultOAuth2AccessToken) accessToken).setScope(null);

        } catch (Exception ex) {
            String errorLog = String.format("ex>>>  %s",ex);
            logger.error(errorLog);
        }

        return accessToken;
    }
}
