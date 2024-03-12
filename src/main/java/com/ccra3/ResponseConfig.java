/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccra3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonInclude(Include.NON_NULL)
public class ResponseConfig {
    private Object entries;
      private String error_code;
    private String error_message;

    public Object getEntries() {
        return entries;
    }

    public void setEntries(Object entries) {
        this.entries = entries;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    @Override
    public String toString() {
        return "ResponseConfig{" + "entries=" + entries + ", error_code=" + error_code + ", error_message=" + error_message + '}';
    }



    
    
}
