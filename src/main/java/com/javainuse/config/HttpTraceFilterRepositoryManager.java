/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javainuse.config;

import ch.qos.logback.classic.Logger;
import com.javainuse.model.TrnJson;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 *
 * @author navaployw
 */
@Component
public class HttpTraceFilterRepositoryManager extends OncePerRequestFilter{
    @Autowired
    TrnJsonService trnJson;
    private final Logger loggerS = (Logger) LoggerFactory.getLogger(HttpTraceFilterRepositoryManager.class);
 
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            loggerS.info(">>>doFilterInternal<<<");
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(
                    response);
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(
                    request);
            try {

                filterChain.doFilter(wrappedRequest, wrappedResponse);
                loggerS.info("aa.trnJsonID>>>"+ trnJson.getSaveObj());
                loggerS.info("getResponseBody(wrappedResponse)>>>"+getResponseBody(wrappedResponse));
                
                  TrnJson trnJsonObjResponse = trnJson.getSaveObj();
                  if(trnJsonObjResponse!=null){
                    trnJsonObjResponse.setJsonResponse(getResponseBody(wrappedResponse).toString());
                    trnJsonObjResponse.setStatusCode(Long.parseLong(response.getStatus()+""));
                    trnJson.saveJsonResponse(trnJsonObjResponse);
                  }
                
                  
            } catch (Exception ex) {
                loggerS.error("Error "+ex);
            } finally {
                wrappedResponse.copyBodyToResponse();
            }
    }

        protected StringBuilder getResponseBody(
                ContentCachingResponseWrapper wrappedResponse) {
            try {
                if (wrappedResponse.getContentSize() <= 0) {
                    return null;
                }
                byte[] srcBytes = wrappedResponse.getContentAsByteArray();
                Charset charset = Charset.forName( wrappedResponse.getCharacterEncoding());
                CharsetDecoder decoder = charset.newDecoder();
                ByteBuffer srcBuffer = ByteBuffer.wrap(srcBytes);
                CharBuffer resBuffer = decoder.decode(srcBuffer);
                return new StringBuilder(resBuffer);
            } catch (CharacterCodingException e) {
                loggerS.error("Error"+ trnJson.getSaveObj());
                return null;
            }
        }
        
      
}