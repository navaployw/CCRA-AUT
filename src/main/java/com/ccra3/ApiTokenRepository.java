/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccra3;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
@Transactional
public interface ApiTokenRepository extends JpaRepository<ApiToken, Long> {
    
    public String deleteByuid(Long uId);
    
   
}
