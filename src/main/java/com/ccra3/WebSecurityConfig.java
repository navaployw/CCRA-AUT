/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccra3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 *
 * @author navaployw
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.ccra3", "com.javainuse.config", "com.javainuse", "com.javainuse.model", "com.javainuse.repositories"})
@ComponentScan(basePackages = {"com.ccra3", "com.javainuse.config", "com.javainuse", "com.javainuse.model", "com.javainuse.repositories"})
@EntityScan(basePackages = {"com.ccra3", "com.javainuse.config", "com.javainuse", "com.javainuse.model", "com.javainuse.repositories"})
public class WebSecurityConfig {

    private final static Logger logger = LogManager.getLogger(WebSecurityConfig.class);
    private static ConfigurableApplicationContext context;

    private final String DEFAULT_PATH = "/home/kumpeep/Project/N.poii/ccra3-api-auth/ccraapidb.properties";
   
    public static void main(String[] args) {
        context = SpringApplication.run(WebSecurityConfig.class, args);
    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
////    @Override
////    public void configure(ResourceServerSecurityConfigurer resources) {
////        resources.authenticationEntryPoint(new AuthExceptionEntryPoint());
////
////    }
//
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        
//        
//        
//        Properties aProp = new Properties();
////        String urlPath = apiCtrlService.getFileDBConfigPath();
//      String urlPath = DEFAULT_PATH;
//        String infoLog = String.format("URLPATH::: %s", urlPath);
//        logger.info(infoLog);
//
//        if(urlPath!=null){
//            try(InputStream fileName = new FileInputStream(urlPath)) {
//                aProp.load(fileName);
//            }catch(FileNotFoundException ex){
//                logger.error(ex.getMessage());
//            }
//            logger.info("URLPATH::: "+urlPath);
//        }
//        logger.info(">>>>configure<<<<<<");
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));//aProp.getProperty("api.allowheaders")
//        corsConfiguration.setAllowedOrigins(List.of(aProp.getProperty("api.allowadminorigin"),aProp.getProperty("api.allowreportorigin")));
//        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT","OPTIONS","PATCH", "DELETE"));//aProp.getProperty("api.allowmethods")
//        corsConfiguration.setAllowCredentials(true);
//        corsConfiguration.setExposedHeaders(List.of("Authorization"));
//        http.cors().configurationSource(request -> corsConfiguration).and()
//                .authorizeRequests()
//                .requestMatchers("/ccraapiauth/registerUser", "/ccraapiauth/restartUser")
//                .permitAll()
//                .requestMatchers("/ccraapiauth/oauth/token")
//                .authenticated();
//    }
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
//        // We don't need CSRF for this example
//        httpSecurity.csrf().disable()
//        // don't authenticate this particular request
//        .authorizeHttpRequests()
//        
//        .requestMatchers("/ccraapiauth/registerUser").permitAll()
//        .requestMatchers("/ccraapiauth/restartUser").permitAll()
//        .requestMatchers("/ccraapiauth/oauth/token").authenticated();
//        
//         // all other requests need to be authenticated
//         //.anyRequest().authenticated().and()
//        // make sure we use stateless session; session won't be used to
//        // store user's state.
////        .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
////        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//        // Add a filter to validate the tokens with every request
//        //httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//        
//        httpSecurity.cors();
//        
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
//        corsConfiguration.setAllowedOrigins(List.of("*"));
//        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE","OPTIONS", "PUT", "PATCH", "DELETE"));
//        corsConfiguration.setAllowCredentials(true);
//        corsConfiguration.setExposedHeaders(List.of("Authorization"));
//        
//        return httpSecurity.build();
//    }
//
    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        Thread thread = new Thread(() -> {
            context.close();
            context = SpringApplication.run(WebSecurityConfig.class, args.getSourceArgs());
        });

        thread.setDaemon(false);
        thread.start();
    }
}
