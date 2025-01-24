package com.javainuse.config;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author navaployw
 */
public class CustomTokenEnhancer implements TokenEnhancer {

    private final static Logger logger = (Logger) LoggerFactory.getLogger(CustomTokenEnhancer.class);

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        try {

            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                return null;
            }
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

            String aiCode = request.getParameter("ai_code");
            Map<String, Object> additionalInfo = new HashMap<>();
            additionalInfo.put("ai_code", aiCode);
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            ((DefaultOAuth2AccessToken) accessToken).setExpiration(new Date(new Date().getTime() + (1000 * 60 * 60 * 24)));
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(new LinkedHashMap<>());
            ((DefaultOAuth2AccessToken) accessToken).setScope(null);

        } catch (Exception ex) {
            String errorLog = String.format("ex>>>  %s",ex);
            logger.error(errorLog);
        }
        return accessToken;

    }
}
