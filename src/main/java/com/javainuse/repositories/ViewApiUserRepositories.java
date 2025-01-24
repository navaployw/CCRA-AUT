/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javainuse.repositories;

import com.javainuse.model.SpmUser;
import com.javainuse.model.ViewApiUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author kumpeep
 */
@Repository
public interface ViewApiUserRepositories extends JpaRepository<ViewApiUser, Long> {
        public List<ViewApiUser> findByaiCode(String aiCode);
        
        public List<ViewApiUser> findByaiCodeAndPasswordAndUserID(String aiCode,String password,String userID);
        
        public List<ViewApiUser> findByaiCodeAndUserID(String aiCode,String userID);
        
        public List<ViewApiUser> findByflagActive(Boolean active);
}
