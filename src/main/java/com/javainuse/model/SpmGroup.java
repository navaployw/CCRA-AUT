/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javainuse.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author navaployw
 */
@Entity
@Table(name = "SPM_GROUP")
public class SpmGroup {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "GROUPID")
    Long groupId;
    
    @Column(name = "AICODE")
    String aiCode;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    
   
    public String getAiCode() {
        return aiCode;
    }

    public void setAiCode(String aiCode) {
        this.aiCode = aiCode;
    }

    public SpmGroup() {
    }

    @Override
    public String toString() {
        return "SpmGroup{" + "groupId=" + groupId + ", aiCode=" + aiCode + '}';
    }
    
    
}
