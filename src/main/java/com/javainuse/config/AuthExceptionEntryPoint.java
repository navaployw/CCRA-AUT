/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javainuse.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import ch.qos.logback.classic.Logger;
import com.javainuse.model.TrnJson;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 *
 * @author navaployw
 */
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    TrnJsonService trnJson;

    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    final String er00041 = "00041";
    final String invalidCredential = "{error_code:00041,error_message:Invalid credentials}";
    AuthorizationServer authServer = new AuthorizationServer();
    String userId;
    String password;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2) throws IOException, ServletException {
        logger.info(">>>authen error<<<<");
        saveErrorLog(request, response);
        final Map<String, Object> mapBodyException = new LinkedHashMap<>();
        mapBodyException.put("error_code", er00041);
        mapBodyException.put("error_message", "Invalid credentials");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), mapBodyException);

    }

    private void saveErrorLog(HttpServletRequest request, HttpServletResponse response) {
        AesEcbEncryptDecrypt encryptObject = new AesEcbEncryptDecrypt();

        TrnJson trnJsonObjRequest = new TrnJson();
        TrnJson trnJsonObjResponse = new TrnJson();
        trnJsonObjRequest.setRequestTime(new Date());

        try {
            SymmetricCipher cy = SymmetricCipher.getInstance();
            String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
                String base64Credentials = authorization.substring("Basic".length()).trim();
                byte[] credDecoded = new Base64().decode(base64Credentials);
                String credentials = new String(credDecoded, "UTF-8");
                // credentials = username:password
                final String[] values = credentials.split(":", 2);
                userId = encryptObject.decrypt(values[0]);
                password = encryptObject.decrypt(values[1]);
                String infoLog = String.format("userId:: %s", userId);
                logger.info(infoLog);
                infoLog = String.format("password:: %s", cy.encrypt(password));
                logger.info(infoLog);
                trnJsonObjRequest.setModule(1L);
                trnJsonObjRequest.setRequestTime(new Date());
                trnJsonObjRequest.setJsonRequest("authorization:{username:" + values[0] + ",password:" + values[1] + "},body:{ai_code:" + request.getParameter("ai_code") + ",grant_type:" + request.getParameter("grant_type") + "}");
                trnJsonObjResponse = trnJson.saveJsonRequest(trnJsonObjRequest); //receive id
                trnJsonObjResponse.setResponseTime(new Date());
                trnJsonObjResponse.setJsonResponse(invalidCredential);
                trnJsonObjResponse.setStatusCode(Long.parseLong(Integer.toString(HttpServletResponse.SC_UNAUTHORIZED)));
                trnJsonObjResponse.setErrorCode(er00041);
                trnJsonObjResponse.setJsonResponse(invalidCredential);
                trnJson.saveJsonResponse(trnJsonObjResponse);

            }
        } catch (Exception e) {
            String errorLog = String.format("Error: %s", e);
            logger.error(errorLog);
        }
    }

}
