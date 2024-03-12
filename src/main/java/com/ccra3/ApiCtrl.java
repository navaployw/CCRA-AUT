/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ccra3;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "API_CTRL")
public class ApiCtrl implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CTRLID")
    private Long ctrlId;
    @Column(name = "CTRLTYPE")
    private String ctrlType;
    @Column(name = "CTRLVALUE")
    private String ctrlValue;
    @Column(name = "CTRLDESCRIPTION")
    private String ctrlDescription;
    @Column(name = "CTRL_START")
    private Date ctrlStart;
    @Column(name = "CTRL_END")
    private Date ctrlEnd;
    @Column(name = "CTRL_FLAG")
    private Boolean ctrlFlag;

    public Long getCtrlId() {
        return ctrlId;
    }

    public void setCtrlId(Long ctrlId) {
        this.ctrlId = ctrlId;
    }

    public String getCtrlType() {
        return ctrlType;
    }

    public void setCtrlType(String ctrlType) {
        this.ctrlType = ctrlType;
    }

    public String getCtrlValue() {
        return ctrlValue;
    }

    public void setCtrlValue(String ctrlValue) {
        this.ctrlValue = ctrlValue;
    }

    public String getCtrlDescription() {
        return ctrlDescription;
    }

    public void setCtrlDescription(String ctrlDescription) {
        this.ctrlDescription = ctrlDescription;
    }

    public Date getCtrlStart() {
        return ctrlStart;
    }

    public void setCtrlStart(Date ctrlStart) {
        this.ctrlStart = ctrlStart;
    }

    public Date getCtrlEnd() {
        return ctrlEnd;
    }

    public void setCtrlEnd(Date ctrlEnd) {
        this.ctrlEnd = ctrlEnd;
    }

    public Boolean getCtrlFlag() {
        return ctrlFlag;
    }

    public void setCtrlFlag(Boolean ctrlFlag) {
        this.ctrlFlag = ctrlFlag;
    }

    @Override
    public String toString() {
        return "ApiCtrl{" + "ctrlId=" + ctrlId + ", ctrlType=" + ctrlType + ", ctrlValue=" + ctrlValue + ", ctrlDescription=" + ctrlDescription + ", ctrlStart=" + ctrlStart + ", ctrlEnd=" + ctrlEnd + ", ctrlFlag=" + ctrlFlag + '}';
    }
    
    
}
