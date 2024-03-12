package com.ccra3;



import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class ApiTokenService {

    @Autowired
    private ApiTokenRepository apiTokenRepository;

     private final Logger logger = (Logger) LoggerFactory.getLogger(ApiTokenService.class);
    public ApiToken saveApiToken(ApiToken data) throws Exception {

        apiTokenRepository.save(data);
        return data;
    }
    
    public String deleteApiToken(String uID) throws Exception {
        String infoLog = String.format("delete:uID: %s", Long.parseLong(uID));
        logger.info(infoLog);
        apiTokenRepository.deleteByuid(Long.parseLong(uID));
        return uID;
    }
}
