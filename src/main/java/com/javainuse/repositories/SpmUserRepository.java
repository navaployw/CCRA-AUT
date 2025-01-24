/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javainuse.repositories;

import com.javainuse.model.SpmUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author navaployw
 */
@Repository
public interface SpmUserRepository extends JpaRepository<SpmUser, Long> {
    public List<SpmUser> findByDeletedAndDisabled(Boolean deleted,Boolean disabled);

}
