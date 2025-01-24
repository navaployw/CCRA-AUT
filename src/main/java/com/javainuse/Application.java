package com.javainuse;

import ch.qos.logback.classic.Logger;
import com.javainuse.config.ApiCtrlService;
import com.javainuse.config.AuthExceptionEntryPoint;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.web.cors.CorsConfiguration;

@EnableResourceServer
@SpringBootApplication
public class Application extends ResourceServerConfigurerAdapter {

    private final static Logger logger = (Logger) LoggerFactory.getLogger(Application.class);
    private static ConfigurableApplicationContext context;
    @Autowired
    private ApiCtrlService apiCtrlService;
    private final String DEFAULT_PATH = "/home/kumpeep/Project/auth2resource/Seperate Server/boot-oauth2-authorization/ccraapidb.properties";//fix to local path;
    public static void main(String[] args) {
        context = SpringApplication.run(Application.class, args);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        // format message
        resources.authenticationEntryPoint(new AuthExceptionEntryPoint());

    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        
        Properties aProp = new Properties();
        String urlPath = apiCtrlService.getFileDBConfigPath();
//      String urlPath = DEFAULT_PATH;
        String infoLog = String.format("URLPATH::: %s", urlPath);
        logger.info(infoLog);

        if(urlPath!=null){
            try(InputStream fileName = new FileInputStream(urlPath)) {
                aProp.load(fileName);
            }catch(FileNotFoundException ex){
                logger.error(ex.getMessage());
            }
            logger.info(infoLog);
        }
        logger.info(">>>>configure<<<<<<");
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));//aProp.getProperty("api.allowheaders")
        corsConfiguration.setAllowedOrigins(List.of(aProp.getProperty("api.allowadminorigin"),aProp.getProperty("api.allowreportorigin"))); //,"http://localhost:4200/","http://localhost:8082/","http://localhost:8080/"
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT","OPTIONS","PATCH", "DELETE"));//aProp.getProperty("api.allowmethods")
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization"));
        http.cors().configurationSource(request -> corsConfiguration).and()
                .authorizeRequests()
                .antMatchers("/ccraapiauth/registerUser", "/ccraapiauth/restartUser")
                .permitAll()
                .antMatchers("/ccraapiauth/oauth/token")
                .authenticated();

    }

    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        Thread thread = new Thread(() -> {
            context.close();
            context = SpringApplication.run(Application.class, args.getSourceArgs());
        });

        thread.setDaemon(false);
        thread.start();
    }
}
