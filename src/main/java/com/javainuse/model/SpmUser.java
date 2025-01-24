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
@Table(name = "SPM_USER")
public class SpmUser {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "UID")
    Long uID;
    @Column(name = "USERID")
    String userId;
    @Column(name = "PASSWORD")
    String password;
    @Column(name = "DELETED")
    Boolean deleted;
    @Column(name = "DISABLED")
    Boolean disabled;
    
    public Long getuID() {
        return uID;
    }

    public void setuID(Long uID) {
        this.uID = uID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
    
    
    
    public SpmUser() {
    }
    
    
    
}
