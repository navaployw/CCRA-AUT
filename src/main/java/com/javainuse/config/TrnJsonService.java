/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javainuse.config;

import ch.qos.logback.classic.Logger;
import com.javainuse.model.TrnJson;
import com.javainuse.repositories.TrnJsonRepository;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import com.arg.util.SymmetricCipher;
/**
 *
 * @author navaployw
 */
@Service
public class TrnJsonService {

    @Autowired
    private TrnJsonRepository trnJsonRepository;
    TrnJson objSave;
    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
    public TrnJson saveJsonRequest(TrnJson data) throws Exception {
       
        trnJsonRepository.save(data);
        objSave = data;
        String infoLog = String.format("trnJsonRepository>>> %s",data);
        logger.info(infoLog);
        return data;
    }
     public TrnJson saveJsonResponse(TrnJson data) throws Exception {
        String infoLog = String.format("TrnJson:data:>>> %s",data);
        logger.info(infoLog);
        trnJsonRepository.save(data);
        return data;
    }
     public TrnJson getSaveObj(){
        String infoLog = String.format("objSave>>> %s",objSave);
        logger.info(infoLog);
        return objSave;
     }
}
