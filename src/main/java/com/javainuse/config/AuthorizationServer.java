package com.javainuse.config;

import ch.qos.logback.classic.Logger;
import com.javainuse.model.ViewApiUser;
import com.javainuse.repositories.ViewApiUserRepositories;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

  
    @Value("${oauth.authen.privateky}")
    private String privateKy;
    @Value("${oauth.authen.publicky}")
    private String publicKy;

    
    
    private List<ViewApiUser> userList = new ArrayList<>();
    private final Logger logger = (Logger) LoggerFactory.getLogger(AuthorizationServer.class);
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private AuthenticationManager authenticationManager;
    
    @Autowired
    @Qualifier("dataSource")
    DataSource dataSource;

    
    @Autowired
    private ViewApiUserRepositories viewApiUserRepository;
    
    @Bean
    public JwtAccessTokenConverter tokenEnhancer() {
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        JwtAccessTokenConverter converter = new MyJwt();
        converter.setSigningKey(privateKy);
        converter.setVerifierKey(publicKy);
        return converter;
    }

    @Bean
    public JwtTokenStore tokenStore() {
        return new JwtTokenStore(tokenEnhancer());
    }
    
    @Autowired
    private ViewApiUserService userService;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

     
        AesEcbEncryptDecrypt encryptObject = new AesEcbEncryptDecrypt();

        SymmetricCipher cy = SymmetricCipher.getInstance();     
        userList = userService.getAllUser();
//        users = userDetailsService.loadAllUser();
        
        InMemoryClientDetailsServiceBuilder inMemoryBuilder = clients.inMemory();
//        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        if(!userList.isEmpty()){
            logger.info(">>>userList not empty<<<");
            for(int i=0;i<userList.size();i++){
                if(userList.get(i).getPassword()!=null && userList.get(i).getUserID()!=null){
                    String passDecode = cy.decrypt(userList.get(i).getPassword());
                    String infoLog = String.format(">>>passDecode::  %s",passDecode);
                    logger.info(infoLog);
                    AesEcbEncryptDecrypt.setKey(userList.get(i).getSecretKey());
                    ClientModel cc1 = new ClientModel();
                    infoLog = String.format(">>>userList.get(i).getUserId()::  %s",userList.get(i).getUserID());
                    logger.info(infoLog);
                    cc1.setClientId(userList.get(i).getUserID());
                    cc1.setClientSecret(passDecode);
                    infoLog = String.format(">>>clientKey.getClientId():::  %s",cc1.getClientId());
                    logger.info(infoLog);
                    String usrOAuth = encryptObject.encrypt(cc1.getClientId());
                    String pwdOAuth = encryptObject.encrypt(cc1.getClientSecret());
                    infoLog = String.format("usrOAuth>>>  %s",usrOAuth);
                    logger.info(infoLog);
                    infoLog = String.format("pwdOAuth>>>  %s",pwdOAuth);
                    logger.info(infoLog);
                    inMemoryBuilder
                        .withClient(usrOAuth)
                        .authorizedGrantTypes("client_credentials")
                        .secret(encoder().encode(pwdOAuth))
                        .scopes("read", "write").and().build();
//                    clients.jdbc(dataSource).withClient(usrOAuth).authorizedGrantTypes("client_credentials").secret(pwdOAuth).scopes("read", "write").and().build();
//                      clients.jdbc(dataSource).build().loadClientByClientId(usrOAuth);  


//clients.jdbc(dataSource);
//                    logger.info("jdbcClientDetailsService.listClientDetails().get(0):::"+jdbcClientDetailsService.listClientDetails().get(0));
//                    if(!jdbcClientDetailsService.listClientDetails().isEmpty()) {
//                           jdbcClientDetailsService.removeClientDetails(CLIEN_ID);
//                    }
//                    if(jdbcClientDetailsService.listClientDetails().isEmpty() ) {
//                        clients.jdbc(dataSource).withClient(usrOAuth).secret(pwdOAuth)
//                        .authorizedGrantTypes("client_credentials")
//                        .scopes("read", "write").and().build();                
//                    }   
                }
            }
//              logger.info("jdbcClientDetailsService.slistClientDetails():::"+jdbcClientDetailsService.listClientDetails());
        }

//
//    if(jdbcClientDetailsService.listClientDetails().isEmpty() ) {
//        clients.jdbc(dataSource).withClient(CLIEN_ID).secret(encoder.encode(CLIENT_SECRET))
//        .authorizedGrantTypes(GRANT_TYPE_PASSWORD, AUTHORIZATION_CODE, REFRESH_TOKEN, IMPLICIT)
//        .scopes(SCOPE_READ, SCOPE_WRITE, TRUST).accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS)
//        .refreshTokenValiditySeconds(FREFRESH_TOKEN_VALIDITY_SECONDS).and().build();                
//    }       
//            clients.jdbc(dataSource);
    }
    
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        String infoLog = String.format("endpoints::  %s",endpoints);
        logger.info(infoLog);
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(List.of(new CustomTokenEnhancer(), tokenEnhancer()));
        endpoints.authenticationManager(authenticationManager)
            .tokenEnhancer(tokenEnhancerChain)
            .accessTokenConverter(tokenEnhancer())
            .approvalStoreDisabled()
            .tokenStore(tokenStore());
//        endpoints
//                .tokenStore(tokenStore())
//                .tokenEnhancer(tokenEnhancer());
//        endpoints.exceptionTranslator(exception -> {
//            if (exception instanceof OAuth2Exception) {
//                OAuth2Exception oAuth2Exception = (OAuth2Exception) exception;
//                return ResponseEntity
//                        .status(oAuth2Exception.getHttpErrorCode())
//                        .body(new CustomOauthException(oAuth2Exception.getMessage()));
//            } else {
//                throw exception;
//            }
//        });
        //                .authenticationManager(authenticationManager)

    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()").allowFormAuthenticationForClients();
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    
    
}
