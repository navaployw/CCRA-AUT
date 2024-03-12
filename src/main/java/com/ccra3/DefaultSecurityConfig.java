/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccra3;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import ch.qos.logback.classic.Logger;

import com.ccra3.DefaultSecurityConfig.MyConvertMap;
import com.ccra3.DefaultSecurityConfig.MyConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.DefaultMapOAuth2AccessTokenResponseConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author navaployw
 */
@Configuration
@EnableWebSecurity
public class DefaultSecurityConfig {

    private final Logger logger = (Logger) LoggerFactory.getLogger(DefaultSecurityConfig.class);

    OAuth2AccessTokenResponse customAccessTokenResponseHandler;
    @Autowired
    LoginSuccessHandler loginSuccessHandler;
    @Autowired
    AccessTokenResponseHandler accessTokenResponseHandler;
    @Autowired
    AfterResponse afterResponse;
    @Autowired
    TrnJsonService trnJson;
    AuthenticationFailureHandler errorResponseHandler;
    private ObjectMapper objectMapper = new ObjectMapper();

    
    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        logger.info(">>>>>>>jwtTokenCustomizer<<<<<<<");
        return (context) -> {

            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                String aiCodeParam = request.getParameter("ai_code");
                if (aiCodeParam != null) {
                    context.getClaims().claims((claims) -> {

                        Authentication a = SecurityContextHolder.getContext().getAuthentication();

                        String clientId = a.getPrincipal().toString();

                        claims.put("ai_code", aiCodeParam);
                        claims.put("client_id", clientId.replaceAll(" ","+"));
                        claims.put("exp", new Date(new Date().getTime() + (1000 * 60 * 60 * 24)));
                        claims.remove("sub");
                        claims.remove("aud");
                        claims.remove("nbf");
                        claims.remove("iss");
                        claims.remove("iat");
                    });

                }
            }

        };
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                // .issuer("https://example.com")
                // .authorizationEndpoint("/oauth2/v1/authorize")
                // .deviceAuthorizationEndpoint("/oauth2/v1/device_authorization")
                // .deviceVerificationEndpoint("/oauth2/v1/device_verification")
                .tokenEndpoint("/oauth/token")
                // .tokenIntrospectionEndpoint("/oauth2/v1/introspect")
                // .tokenRevocationEndpoint("/oauth2/v1/revoke")
                // .jwkSetEndpoint("/oauth2/v1/jwks")
                // .oidcLogoutEndpoint("/connect/v1/logout")
                // .oidcUserInfoEndpoint("/connect/v1/userinfo")
                // .oidcClientRegistrationEndpoint("/connect/v1/register")
                .build();
    }

    public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {
                logger.info("#### myAuthenticationSuccessHandler ####");
                logger.info("response.getStatus()::"+response.getStatus());
                OAuth2AccessTokenAuthenticationToken accessTokenAuthentication
                        = (OAuth2AccessTokenAuthenticationToken) authentication;

                OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();
                OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();
                Map<String, Object> additionalParameters = accessTokenAuthentication.getAdditionalParameters();

                OAuth2AccessTokenResponse.Builder builder
                        = OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
                                .tokenType(accessToken.getTokenType())
                                .scopes(accessToken.getScopes());
                // .additionalParameters(additionalParameters);

                if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
                    builder.expiresIn(ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt()));
                }

                if (refreshToken != null) {
                    builder.refreshToken(refreshToken.getTokenValue());
                }

                logger.info("### additionalParameters ");
                logger.info(additionalParameters.toString());
                if (!CollectionUtils.isEmpty(additionalParameters)) {
                    builder.additionalParameters(additionalParameters);
                }

                OAuth2AccessTokenResponse accessTokenResponse = builder.build();
                ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
                logger.info(">>>>>>httpResponse:"+httpResponse);
                // OAuth2AccessTokenResponseHttpMessageConverter accessTokenHttpResponseConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
                OAuth2AccessTokenResponseHttpMessageConverter accessTokenHttpResponseConverter
                        = new MyConverter();
                accessTokenHttpResponseConverter.write(accessTokenResponse, null, httpResponse);
            }
        };
    }

    public AuthenticationFailureHandler authenticationFailureHandler() {

        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                logger.info("#### onAuthenticationFailure ####");
                logger.info("exception:::" + exception);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                Map<String, Object> data = new HashMap<>();
                data.put(
                        "error_code",
                        "00041");
                data.put(
                        "error_message",
                        "Invalid credentials.");
                response.setStatus(401);
                response.getOutputStream()
                        .println(objectMapper.writeValueAsString(data));
                afterResponse.beforeBodyWrite(data.toString());
                TrnJson trnJsonObjResponse = trnJson.getSaveObj();
//                if (trnJsonObjResponse != null) {
                try {
                    trnJsonObjResponse.setModule(1L);
                    trnJsonObjResponse.setJsonResponse(data.toString());
                    trnJsonObjResponse.setStatusCode(Long.valueOf("400"));
                    trnJson.saveJsonResponse(trnJsonObjResponse);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(DefaultSecurityConfig.class.getName()).log(Level.SEVERE, "Get token error", ex);
                }
//                }
            }

        };
    }

    class MyConverter extends OAuth2AccessTokenResponseHttpMessageConverter {

        private GenericHttpMessageConverter<Object> jsonMessageConverter = new MappingJackson2HttpMessageConverter();
        private Converter<Map<String, Object>, OAuth2AccessTokenResponse> accessTokenResponseConverter = new DefaultMapOAuth2AccessTokenResponseConverter();

        /* new DefaultOAuth2AccessTokenResponseMapConverter */
        private Converter<OAuth2AccessTokenResponse, Map<String, Object>> accessTokenResponseParametersConverter = new MyConvertMap();

        private static final ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP = new ParameterizedTypeReference<Map<String, Object>>() {
        };

        @Override
        protected void writeInternal(OAuth2AccessTokenResponse tokenResponse, HttpOutputMessage outputMessage)
                throws HttpMessageNotWritableException {

            try {
                Map<String, Object> tokenResponseParameters = this.accessTokenResponseParametersConverter
                        .convert(tokenResponse);
                logger.info("### MyConverter");
                this.jsonMessageConverter.write(tokenResponseParameters, STRING_OBJECT_MAP.getType(),
                        MediaType.APPLICATION_JSON, outputMessage);
            } catch (Exception ex) {
                throw new HttpMessageNotWritableException(
                        "An error occurred writing the OAuth 2.0 Access Token Response: " + ex.getMessage(), ex);
            }

        }
    }

    class MyConvertMap implements Converter<OAuth2AccessTokenResponse, Map<String, Object>> {

        @Override
        public Map<String, Object> convert(OAuth2AccessTokenResponse tokenResponse) {
            logger.info(">>>>>>>>convert<<<<<<<<<");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(OAuth2ParameterNames.ACCESS_TOKEN, tokenResponse.getAccessToken().getTokenValue());
            parameters.put(OAuth2ParameterNames.TOKEN_TYPE, tokenResponse.getAccessToken().getTokenType().getValue());
            parameters.put(OAuth2ParameterNames.EXPIRES_IN, getExpiresIn(tokenResponse));
            if (!CollectionUtils.isEmpty(tokenResponse.getAccessToken().getScopes())) {
                parameters.put(OAuth2ParameterNames.SCOPE,
                        StringUtils.collectionToDelimitedString(tokenResponse.getAccessToken().getScopes(), " "));
            }
            if (tokenResponse.getRefreshToken() != null) {
                parameters.put(OAuth2ParameterNames.REFRESH_TOKEN, tokenResponse.getRefreshToken().getTokenValue());
            }
            if (!CollectionUtils.isEmpty(tokenResponse.getAdditionalParameters())) {
                for (Map.Entry<String, Object> entry : tokenResponse.getAdditionalParameters().entrySet()) {
                    parameters.put(entry.getKey(), entry.getValue());
                }
            }
            Map<String, Object> entity = new HashMap<>();
            ResponseConfig resFromBeforeBodyWrite = afterResponse.beforeBodyWrite(tokenResponse.getAccessToken().getTokenValue());
            logger.info("entity:: " + entity);
            logger.info("resFromBeforeBodyWrites:: " + resFromBeforeBodyWrite);
            logger.info("resFromBeforeBodyWrites getError_code:: " + resFromBeforeBodyWrite.getError_code());
            TrnJson trnJsonObjResponse = trnJson.getSaveObj();
            
             logger.info(">>>>>>>>trnJsonObjResponse<<<<<<<<<"+trnJsonObjResponse);
           
             if (resFromBeforeBodyWrite.getError_code() == null) {
                 entity.put("entries", parameters);
                 if (trnJsonObjResponse != null) {
                     try {
                         trnJsonObjResponse.setJsonResponse(entity.toString());
                         trnJsonObjResponse.setStatusCode(Long.valueOf("200"));
                         trnJson.saveJsonResponse(trnJsonObjResponse);
                     } catch (Exception ex) {
                         java.util.logging.Logger.getLogger(DefaultSecurityConfig.class.getName()).log(Level.SEVERE,
                                 "Get token error", ex);
                     }
                 }

             } else {
                 parameters = new HashMap<>();
                 entity.put("error_message", resFromBeforeBodyWrite.getError_message());
                 entity.put("error_code", resFromBeforeBodyWrite.getError_code());
                 if (trnJsonObjResponse != null) {
                     try {
                         trnJsonObjResponse.setJsonResponse(entity.toString());
                         trnJsonObjResponse.setStatusCode(Long.valueOf("401"));
                         trnJson.saveJsonResponse(trnJsonObjResponse);
                     } catch (Exception ex) {
                         java.util.logging.Logger.getLogger(DefaultSecurityConfig.class.getName()).log(Level.SEVERE,
                                 "Get token error", ex);
                     }
                 }

             }
            
            return entity;
        }

        private static long getExpiresIn(OAuth2AccessTokenResponse tokenResponse) {
            if (tokenResponse.getAccessToken().getExpiresAt() != null) {
                return tokenResponse.getAccessToken().getExpiresAt().getEpochSecond();
            }
            return -1;
        }
    }

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        // OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer
                = new OAuth2AuthorizationServerConfigurer();
        RequestMatcher endpointsMatcher = authorizationServerConfigurer
                .getEndpointsMatcher();

        http
                .securityMatcher(endpointsMatcher)
                .authorizeRequests(authorize
                        -> authorize
                        .requestMatchers("/registerUser").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .apply(authorizationServerConfigurer);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());	// Enable OpenID Connect 1.0
//        http
//                // Redirect to the login page when not authenticated from the
//                // authorization endpoint
//                .exceptionHandling((exceptions) -> exceptions
//                .defaultAuthenticationEntryPointFor(
//                        new LoginUrlAuthenticationEntryPoint("/login"),
//                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
//                )
//                )
//                // Accept access tokens for User Info and/or Client Registration
//                .oauth2ResourceServer((resourceServer) -> resourceServer
//                .jwt(Customizer.withDefaults()));

        // return http.build();
        authorizationServerConfigurer
                .clientAuthentication(clientAuthentication
                        -> clientAuthentication
//                        .authenticationConverter(authenRequestConverter())
                        //                        .authenticationSuccessHandler(myAuthenticationSuccessHandler())
                        .errorResponseHandler(authenticationFailureHandler()))
                .tokenEndpoint(tokenEndpoint
                        -> tokenEndpoint
                        //                        .accessTokenRequestConverter(accessTokenRequestConverter())
                        .accessTokenResponseHandler(myAuthenticationSuccessHandler())
                        .errorResponseHandler(authenticationFailureHandler())
                        
                );
        return http.build(); 
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MyPasswordEncoder();
        // return new BCryptPasswordEncoder();
    }
    
//    public AuthenticationConverter authenRequestConverter() {
//        return (HttpServletRequest request) -> {
//            logger.info(">>>>>request:userPrincipal:::" + request.getUserPrincipal());
//            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
//            if (header == null) {
//                return null;
//            }
//            header = header.trim();
//            if (!StringUtils.startsWithIgnoreCase(header, "Basic")) {
//                return null;
//            }
//
//            logger.info("header.substring(6)::" + header.substring(6));
//            String base64Token = header.substring(6);
//            logger.info("base64Token::" + base64Token);
//            byte[] decodedBytes = Base64.getDecoder().decode(base64Token);
//            String decodedString = new String(decodedBytes);
//            logger.info("decoded:" + decodedString);
//            String[] credentials = decodedString.split(":");
//            String user = credentials[0];
//            String password = credentials[1];
//            this.clientId = user;
//            this.clientSecret = password.replaceAll("\\+", "a");
//            logger.info("clientId:::"+this.clientId);
//            logger.info("clientSecret:::"+this.clientSecret);
//             Authentication authentication = new UsernamePasswordAuthenticationToken(
//                clientId, // username
//                clientSecret, // password
//                Collections.singleton(new SimpleGrantedAuthority("USER")) // authorities
//            );
//            return authentication;
//        };
//    }
//;

}
