package com.ccra3;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */



import java.util.List;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
@Service
public class ViewApiUserService {
    
    @Autowired
    private ViewApiUserRepository viewApiUserRepository;
    
    public List<ViewApiUser> getUserByAICode(String aiCode) throws Exception {
       return viewApiUserRepository.findByaiCode(aiCode);
    }

    public List<ViewApiUser> getUserByAICodePasswordUserId(String aiCode,String password,String userId) throws Exception {
       return viewApiUserRepository.findByaiCodeAndPasswordAndUserID(aiCode,password,userId);
    }   

    public List<ViewApiUser> getUserByAICodeAndUserID(String aiCode,String userId) throws Exception {
       return viewApiUserRepository.findByaiCodeAndUserID(aiCode,userId);
    }
    
    public List<ViewApiUser> findByflagActive(Boolean b) throws Exception {
       return this.viewApiUserRepository.findByflagActive(true);
    }      
}
