/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javainuse.model;

import java.util.Date;
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
@Table(name = "API_TOKEN")
public class ApiToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TOKENID")
    Long tokenId;
    @Column(name = "AID")
    Long aid;
    @Column(name = "UID")
    Long uid;
    @Column(name = "ACCESS_TOKEN")
    String accessToken;
    @Column(name = "IAT")
    Integer iat;
    @Column(name = "EXP")
    Integer exp;
    @Column(name = "CREATE_TIME")
    Date createTime;
    @Column(name = "EXPIRE_TIME")
    Date expireTime;

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getIat() {
        return iat;
    }

    public void setIat(Integer iat) {
        this.iat = iat;
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public ApiToken() {
    }

    @Override
    public String toString() {
        return "ApiToken{" + "tokenId=" + tokenId + ", aid=" + aid + ", uid=" + uid + ", accessToken=" + accessToken + ", iat=" + iat + ", exp=" + exp + ", createTime=" + createTime + ", expireTime=" + expireTime + '}';
    }

}
