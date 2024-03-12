/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccra3;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
@Component
public class HttpTraceFilterRepositoryManager extends OncePerRequestFilter{
    
    @Autowired
    TrnJsonService trnJson;
    private final Logger loggerS = (Logger) LoggerFactory.getLogger(HttpTraceFilterRepositoryManager.class);
 
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
          
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(
                    response);
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(
                    request);
            try {

                filterChain.doFilter(wrappedRequest, wrappedResponse);
                loggerS.info("aa.trnJsonID>>>{0}"+ trnJson.getSaveObj());
                loggerS.info("getResponseBody(wrappedResponse)>>>{0}"+getResponseBody(wrappedResponse));
                loggerS.info("Is restart success>>>{0}"+getResponseBody(wrappedResponse).toString().contains("restart success"));
                Boolean isRestart =   getResponseBody(wrappedResponse).toString().contains("restart success");
                TrnJson trnJsonObjResponse = trnJson.getSaveObj();
                  if(trnJsonObjResponse!=null && !isRestart){
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