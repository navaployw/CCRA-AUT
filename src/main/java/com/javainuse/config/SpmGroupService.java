/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javainuse.config;

import com.javainuse.model.SpmGroup;
import com.javainuse.model.SpmUser;
import com.javainuse.repositories.SpmGroupRepository;
import com.javainuse.repositories.SpmUserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import com.arg.util.SymmetricCipher;
/**
 *
 * @author navaployw
 */
@Service
public class SpmGroupService {

    @Autowired
    private SpmGroupRepository spmGroupRepository;

    public SpmGroup getAIFromAICode(String aiCode) throws Exception {

        SpmGroup groupObj = spmGroupRepository.findByaiCode(aiCode);
        return groupObj;
        
    }
}
