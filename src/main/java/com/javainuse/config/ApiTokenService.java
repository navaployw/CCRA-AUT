/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javainuse.config;


import com.javainuse.model.ApiToken;
import com.javainuse.repositories.ApiTokenRepository;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author navaployw
 */
@Service
public class ApiTokenService {

    @Autowired
    private ApiTokenRepository apiTokenRepository;

    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
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
