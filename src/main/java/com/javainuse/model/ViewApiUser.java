/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javainuse.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author kumpeep
 */
@Entity
@Table(name = "VIEW_API_USER")
public class ViewApiUser implements Serializable {
    @Id
    @Column(name = "AID")
    Long aID;
    @Column(name = "UID")
    String uID;
    @Column(name = "USERID")
    String userID;
    @Column(name = "GROUPID")
    String groupID;
    @Column(name = "PASSWORD")
    String password;
    @Column(name = "AICODE")
    String aiCode;
    @Column(name = "SECRETKEY")
    String secretKey;
    @Column(name = "SECRET_START")
    String secretStart;
    @Column(name = "SECRET_END")
    String secretEnd;
    @Column(name = "FLAG_ACTIVE")
    Boolean flagActive;

    public Long getaID() {
        return aID;
    }

    public void setaID(Long aID) {
        this.aID = aID;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAiCode() {
        return aiCode;
    }

    public void setAiCode(String aiCode) {
        this.aiCode = aiCode;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSecretStart() {
        return secretStart;
    }

    public void setSecretStart(String secretStart) {
        this.secretStart = secretStart;
    }

    public String getSecretEnd() {
        return secretEnd;
    }

    public void setSecretEnd(String secretEnd) {
        this.secretEnd = secretEnd;
    }

    public Boolean getFlagActive() {
        return flagActive;
    }

    public void setFlagActive(Boolean flagActive) {
        this.flagActive = flagActive;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }
    
    
    
}
