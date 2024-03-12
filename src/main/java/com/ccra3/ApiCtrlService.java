/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ccra3;




import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class ApiCtrlService {

    @Autowired
    private ApiCtrlRepository apiCtrlRepository;
    
    
     public String getFileDBConfigPath(){
        List<ApiCtrl> resultList = apiCtrlRepository.findByCtrlType("DBConfigPath");
        if(!resultList.isEmpty()){
            if(!resultList.get(0).getCtrlValue().trim().equals("")){
                return resultList.get(0).getCtrlValue();
            }
        }
        return "";
    }
     
    public String getCtrlValueByCtrlType(String value){
        List<ApiCtrl> resultList = apiCtrlRepository.findByCtrlType(value);
        if(!resultList.isEmpty()){
            if(!resultList.get(0).getCtrlValue().trim().equals("")){
                return resultList.get(0).getCtrlValue();
            }
        }

        return "";
    } 
     
}
