/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ccra3;


import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ViewApiUserRepository extends JpaRepository<ViewApiUser, Long> {
        public List<ViewApiUser> findByaiCode(String aiCode);
        public List<ViewApiUser> findByaiCodeAndPasswordAndUserID(String aiCode,String password,String userID);
        public List<ViewApiUser> findByaiCodeAndUserID(String aiCode,String userID);
        public List<ViewApiUser> findByflagActive(Boolean active);
}
