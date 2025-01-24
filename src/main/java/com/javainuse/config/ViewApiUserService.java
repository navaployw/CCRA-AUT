/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javainuse.config;

import com.javainuse.model.ViewApiUser;
import com.javainuse.repositories.ViewApiUserRepositories;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author kumpeep
 */
@Service
public class ViewApiUserService {
    
    @Autowired
    private ViewApiUserRepositories viewApiUserRepository;
    
    public List<ViewApiUser> getUserByAICode(String aiCode) throws Exception {
       return viewApiUserRepository.findByaiCode(aiCode);
    }       
    public List<ViewApiUser> getUserByAICodePasswordUserId(String aiCode,String password,String userId) throws Exception {
       return viewApiUserRepository.findByaiCodeAndPasswordAndUserID(aiCode,password,userId);
    }       
    public List<ViewApiUser> getUserByAICodeAndUserID(String aiCode,String userId) throws Exception {
       return viewApiUserRepository.findByaiCodeAndUserID(aiCode,userId);
    }      
    public List<ViewApiUser> getAllUser() throws Exception {
       return viewApiUserRepository.findByflagActive(true);
    }      
}
