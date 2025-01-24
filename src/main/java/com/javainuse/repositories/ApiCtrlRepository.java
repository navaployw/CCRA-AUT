/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javainuse.repositories;


import com.javainuse.model.ApiCtrl;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author kumpeep
 */
@Repository
public interface ApiCtrlRepository extends JpaRepository<ApiCtrl, Long>{
    
    public List<ApiCtrl> findByCtrlType(String ctrlType);
}
