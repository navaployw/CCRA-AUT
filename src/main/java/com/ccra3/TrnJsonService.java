/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccra3;



import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class TrnJsonService {

    @Autowired
    private TrnJsonRepository trnJsonRepository;
    TrnJson objSave;
    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
    public TrnJson saveJsonRrequest(TrnJson data) throws Exception {
       
        trnJsonRepository.save(data);
        objSave = data;
        String infoLog = String.format("trnJsonRepository>>> %s",data);
        logger.info(infoLog);
        return data;
    }
     public TrnJson saveJsonResponse(TrnJson data) throws Exception {
       
        trnJsonRepository.save(data);
        return data;
    }
     public TrnJson getSaveObj(){
         
         return objSave;
     }
}
