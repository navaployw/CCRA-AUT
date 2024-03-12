/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ccra3;


import com.ccra3.ApiCtrl;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ApiCtrlRepository extends JpaRepository<ApiCtrl, Long>{
    
    public List<ApiCtrl> findByCtrlType(String ctrlType);
}
