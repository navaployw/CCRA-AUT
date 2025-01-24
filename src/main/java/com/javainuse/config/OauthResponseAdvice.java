/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javainuse.config;

import ch.qos.logback.classic.Logger;
import com.javainuse.model.ApiToken;
import com.javainuse.model.TrnJson;
import com.javainuse.model.ViewApiUser;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author kumpeep
 */
@ControllerAdvice
public class OauthResponseAdvice implements ResponseBodyAdvice<Object> {

    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
    final String ai_code = "ai_code";
    final String err00041 = "00041";
    final String Invalid_credentials = "Invalid credentials.";
    final String usernameSt = "username";
    final String passwordSt = "password";

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Autowired
    SpmGroupService spmGroup;
    @Autowired
    TrnJsonService trnJson;
    @Autowired
    ApiTokenService apiToken;
    private String userId;
    private String password;
    private String userIdEnc;
    private String passwordEnc;
    @Autowired
    private ViewApiUserService userService;

    @Override
    public ResponseConfig beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        /// Modify body here
        ResponseConfig res = new ResponseConfig();
        try {
            AesEcbEncryptDecrypt encryptObject = new AesEcbEncryptDecrypt();
            String infoLog = String.format("body::  %s", body);
            logger.info(infoLog);
            infoLog = String.format("returnType::  %s", returnType);
            logger.info(infoLog);
            infoLog = String.format("selectedContentType::  %s", selectedContentType);
            logger.info(infoLog);
            infoLog = String.format("selectedConverterType::  %s", body);
            logger.info(infoLog);
            infoLog = String.format("request::  %s", request);
            logger.info(infoLog);
            infoLog = String.format("response::  %s", response);
            logger.info(infoLog);

            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                return null;
            }
            HttpServletRequest requestHttp = ((ServletRequestAttributes) requestAttributes).getRequest();
            infoLog = String.format("IP::  %s", requestHttp.getRemoteAddr());
            logger.info(infoLog);
            infoLog = String.format("Content-type::  %s", requestHttp.getContentType());
            logger.info(infoLog);
            infoLog = String.format("Body::  %s", requestHttp.getParameter("Body"));
            logger.info(infoLog);
            String aiCode = requestHttp.getParameter(ai_code);
            logger.info("::beforeBodyWrite::");
            infoLog = String.format("ai_code::  %s", aiCode);
            logger.info(infoLog);

            //Insert Transaction request
            String authorization = requestHttp.getHeader("Authorization");
            infoLog = String.format("authorization::  %s", authorization);
            logger.info(infoLog);
            SymmetricCipher cy = SymmetricCipher.getInstance();
            TrnJson trnJsonObjRequest = new TrnJson();
            TrnJson trnJsonObjResponse = new TrnJson();

            trnJsonObjRequest.setRequestTime(new Date());
            if (aiCode != null) {
                if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
                    logger.info("AI_CODE:110:" + aiCode);
                    List<ViewApiUser> userList = userService.getUserByAICode(requestHttp.getParameter(ai_code));
                    trnJsonObjRequest.setModule(1L);
                    String base64Credentials = authorization.substring("Basic".length()).trim();
                    byte[] credDecoded = new Base64().decode(base64Credentials);
                    String credentials = new String(credDecoded, "UTF-8");
                    final String[] values = credentials.split(":", 2);
                    infoLog = String.format("value[0]::  %s", values[0]);
                    logger.info(infoLog);
                    infoLog = String.format("value[1]::  %s", values[1]);
                    logger.info(infoLog);
                    userIdEnc = values[0];
                    passwordEnc = values[1];
                    if (userList.size() == 1) {
                        infoLog = String.format("secretkey::  %s", userList.get(0).getSecretKey());
                        logger.info(infoLog);
                        AesEcbEncryptDecrypt.setKey(userList.get(0).getSecretKey());
                        userId = encryptObject.decrypt(values[0]);
                        password = encryptObject.decrypt(values[1]);
                        infoLog = String.format("userId::  %s", userId);
                        logger.info(infoLog);
                        infoLog = String.format("password::  %s", cy.encrypt(password));
                        logger.info(infoLog);
                        infoLog = String.format("userList:::  %s", userList);
                        logger.info(infoLog);
                        trnJsonObjRequest.setJsonRequest("authorization:{username:" + values[0] + ",password:" + values[1] + "},body:{ai_code:" + requestHttp.getParameter(ai_code) + ",grant_type:" + requestHttp.getParameter("grant_type") + "}");
                        trnJsonObjResponse = trnJson.saveJsonRequest(trnJsonObjRequest); //receive id
                        if (userId.equals(userList.get(0).getUserID())) {
                            if (body.toString().contains("error")) {
                                if (body.toString().contains("error")) {
                                    res.setError_code(err00041);
                                    res.setError_message(Invalid_credentials);
                                    //Insert Transaction response
                                    trnJsonObjResponse.setResponseTime(new Date());
                                    trnJsonObjResponse.setErrorCode(err00041);
                                    trnJsonObjResponse.setJsonResponse("{\"message\":\"".concat(Invalid_credentials).concat("\",\"code\":\"").concat(err00041).concat("\"}"));
                                }
                                trnJson.saveJsonResponse(trnJsonObjResponse);

                            } else {//case success
                                logger.info(">>Enter else");
                                res.setEntries(body);
                                String decode = deCodeJWT(body.toString());
                                JSONObject decodeJsonObj = new JSONObject(decode);
                                infoLog = String.format("decode>>>  %s", decode);
                                logger.info(infoLog);
                                ApiToken apiTokenObj = new ApiToken();
                                //Insert Transaction response
                                trnJsonObjResponse.setResponseTime(new Date());
                                trnJsonObjResponse.setStatusCode(200L);
                                trnJsonObjResponse.setaId(userList.get(0).getaID());
                                trnJsonObjResponse.setuId(Long.parseLong(userList.get(0).getuID()));
                                trnJsonObjResponse.setGroupId(Long.parseLong(userList.get(0).getGroupID()));
                                trnJson.saveJsonResponse(trnJsonObjResponse);
                                apiToken.deleteApiToken(userList.get(0).getuID());
                                apiTokenObj.setAccessToken(body.toString());
                                apiTokenObj.setCreateTime(new Date());
                                apiTokenObj.setExp(Integer.valueOf(decodeJsonObj.get("exp").toString()));
                                apiTokenObj.setExpireTime(new Date(Long.valueOf(decodeJsonObj.get("exp").toString()) * 1000));
                                apiTokenObj.setAid(userList.get(0).getaID());
                                apiTokenObj.setUid(Long.parseLong(userList.get(0).getuID()));
                                apiToken.saveApiToken(apiTokenObj);
                            }
                        } else {
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            res.setError_code(err00041);
                            res.setError_message(Invalid_credentials);
                            trnJsonObjResponse.setResponseTime(new Date());
                            trnJsonObjResponse.setStatusCode(401L);
                            trnJsonObjResponse.setErrorCode(err00041);
                            trnJsonObjResponse.setJsonResponse("{\"message\":\"".concat(Invalid_credentials).concat("\",\"code\":\"").concat(err00041).concat("\"}"));
                            trnJson.saveJsonResponse(trnJsonObjResponse);
                        }
                    } else if (userList.isEmpty()) {
                        logger.info(">>>userList.isEmpty()<<<");
                        response.setStatusCode(HttpStatus.UNAUTHORIZED);
                        res.setError_code(err00041);
                        res.setError_message(Invalid_credentials);
                        trnJsonObjRequest.setJsonRequest("authorization:{username:" + userIdEnc + ",password:" + passwordEnc + "},body:{ai_code:" + requestHttp.getParameter(ai_code) + ",grant_type:" + requestHttp.getParameter("grant_type") + "}");
                        trnJsonObjResponse = trnJson.saveJsonRequest(trnJsonObjRequest); //receive id
                        trnJsonObjResponse.setResponseTime(new Date());
                        trnJsonObjResponse.setStatusCode(401L);
                        trnJsonObjResponse.setErrorCode(err00041);
                        trnJsonObjResponse.setJsonResponse("{\"message\":\"".concat(Invalid_credentials).concat("\",\"code\":\"").concat(err00041).concat("\"}"));
                        trnJson.saveJsonResponse(trnJsonObjResponse);
                    }
                } else {
                    if (authorization != null && authorization.toLowerCase().startsWith("bearer")) {
                        List<ViewApiUser> userList = userService.getUserByAICode(requestHttp.getParameter(ai_code));
                        if (userList.size() == 1) {
                            infoLog = String.format("value[0]::  %s", requestHttp.getParameter(usernameSt));
                            logger.info(infoLog);
                            infoLog = String.format("value[1]::  %s", requestHttp.getParameter(passwordSt));
                            logger.info(infoLog);
                            userId = encryptObject.decrypt(requestHttp.getParameter(usernameSt));
                            password = encryptObject.decrypt(requestHttp.getParameter(passwordSt));
                            infoLog = String.format("userId::  %s", userId);
                            logger.info(infoLog);
                            infoLog = String.format("password::  %s", cy.encrypt(password));
                            logger.info(infoLog);
                            infoLog = String.format("userList:::  %s", userList);
                            logger.info(infoLog);
                            if (userId.equals(userList.get(0).getUserID())) {
                                if (requestHttp.getParameter(usernameSt) != null) {
                                    String accessToken = authorization.split("Bearer ")[1];
                                    infoLog = String.format("accessToken::  %s", accessToken);
                                    logger.info(infoLog);
                                    trnJsonObjRequest.setModule(3L);
                                    trnJsonObjRequest.setaId(userList.get(0).getaID());
                                    trnJsonObjRequest.setuId(Long.parseLong(userList.get(0).getuID()));
                                    trnJsonObjRequest.setGroupId(Long.parseLong(userList.get(0).getGroupID()));
                                    trnJsonObjRequest.setAccessToken(accessToken);
                                    trnJsonObjRequest.setJsonRequest("token: {" + authorization + "},body:{ai_code:" + requestHttp.getParameter(ai_code) + ",username:" + requestHttp.getParameter(usernameSt) + ",password:" + requestHttp.getParameter(passwordSt) + "}");
                                    trnJsonObjResponse = trnJson.saveJsonRequest(trnJsonObjRequest); //receive id
                                    trnJsonObjResponse.setResponseTime(new Date());
                                }
                                if (body.toString().contains("error")) {
                                    logger.info(">>>group==null<<<");
                                    if (body.toString().contains("error")) {
                                        res.setError_code(err00041);
                                        res.setError_message(Invalid_credentials);
                                        //Insert Transaction response
                                        trnJsonObjRequest.setModule(3L);
                                        trnJsonObjRequest.setJsonRequest("token: {" + authorization + "},body:{ai_code:" + requestHttp.getParameter(ai_code) + ",username:" + requestHttp.getParameter(usernameSt) + ",password:" + requestHttp.getParameter(passwordSt) + "}");
                                        trnJsonObjResponse = trnJson.saveJsonRequest(trnJsonObjRequest); //receive id
                                        trnJsonObjResponse.setResponseTime(new Date());
                                        trnJsonObjResponse.setErrorCode(err00041);
                                        trnJsonObjResponse.setJsonResponse("{\"message\":\"".concat(Invalid_credentials).concat("\",\"code\":\"").concat(err00041).concat("\"}"));
                                    }
                                    trnJson.saveJsonResponse(trnJsonObjResponse);

                                } else {//case success
                                    logger.info(">>Enter if");
                                    res.setEntries(body);
                                    String decode = deCodeJWT(body.toString());
                                    JSONObject decodeJsonObj = new JSONObject(decode);
                                    infoLog = String.format("decode>>>  %s", decode);
                                    logger.info(infoLog);
                                    ApiToken apiTokenObj = new ApiToken();
                                    for (int i = 0; i < userList.size(); i++) {
                                        //Insert Transaction response
                                        trnJsonObjResponse.setResponseTime(new Date());
                                        trnJsonObjResponse.setStatusCode(200L);
                                        trnJsonObjResponse.setaId(userList.get(i).getaID());
                                        trnJsonObjResponse.setuId(Long.parseLong(userList.get(i).getuID()));
                                        trnJsonObjResponse.setGroupId(Long.parseLong(userList.get(i).getGroupID()));
                                        trnJson.saveJsonResponse(trnJsonObjResponse);
                                        apiToken.deleteApiToken(userList.get(i).getuID());
                                        apiTokenObj.setAccessToken(body.toString());
                                        apiTokenObj.setCreateTime(new Date());
                                        apiTokenObj.setExp(Integer.valueOf(decodeJsonObj.get("exp").toString()));
                                        apiTokenObj.setExpireTime(new Date(Long.valueOf(decodeJsonObj.get("exp").toString()) * 1000));
                                        apiTokenObj.setAid(userList.get(i).getaID());
                                        apiTokenObj.setUid(Long.parseLong(userList.get(i).getuID()));
                                        apiToken.saveApiToken(apiTokenObj);
                                    }
                                }
                            } else {
                                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                                res.setError_code(err00041);
                                res.setError_message(Invalid_credentials);
                                trnJsonObjRequest.setModule(3L);
                                trnJsonObjRequest.setJsonRequest("token: {" + authorization + "},body:{ai_code:" + requestHttp.getParameter(ai_code) + ",username:" + requestHttp.getParameter(usernameSt) + ",password:" + requestHttp.getParameter(passwordSt) + "}");
                                trnJsonObjResponse = trnJson.saveJsonRequest(trnJsonObjRequest); //receive id
                                trnJsonObjResponse.setResponseTime(new Date());
                                trnJsonObjResponse.setStatusCode(401L);
                                trnJsonObjResponse.setErrorCode(err00041);
                                trnJsonObjResponse.setJsonResponse("{\"message\":\"".concat(Invalid_credentials).concat("\",\"code\":\"").concat(err00041).concat("\"}"));
                                trnJson.saveJsonResponse(trnJsonObjResponse);
                            }
                        } else if (userList.isEmpty()) {
                            logger.info(">>>>bearer:userlist:empty");
                            infoLog = String.format("authorization::  %s", authorization);
                            logger.info(infoLog);
                            infoLog = String.format("ai_code::  %s", requestHttp.getParameter(ai_code));
                            logger.info(infoLog);
                            infoLog = String.format("username::  %s", requestHttp.getParameter(usernameSt));
                            logger.info(infoLog);
                            infoLog = String.format("password::  %s", requestHttp.getParameter(passwordSt));
                            logger.info(infoLog);

                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            res.setError_code(err00041);
                            res.setError_message(Invalid_credentials);
                            trnJsonObjRequest.setModule(3L);
                            trnJsonObjRequest.setJsonRequest("token: {" + authorization + "},body:{ai_code:" + requestHttp.getParameter(ai_code) + ",username:" + requestHttp.getParameter(usernameSt) + ",password:" + requestHttp.getParameter(passwordSt) + "}");
                            trnJsonObjResponse = trnJson.saveJsonRequest(trnJsonObjRequest);
                            trnJsonObjResponse.setResponseTime(new Date());
                            trnJsonObjResponse.setStatusCode(401L);
                            trnJsonObjResponse.setErrorCode(err00041);
                            trnJsonObjResponse.setJsonResponse("{\"message\":\"".concat(Invalid_credentials).concat("\",\"code\":\"").concat(err00041).concat("\"}"));
                            trnJson.saveJsonResponse(trnJsonObjResponse);
                        }
                    } else {
                        List<ViewApiUser> userList = userService.getUserByAICode(requestHttp.getParameter(ai_code));
                        if (userList.size() == 1) {
                            infoLog = String.format("value[0]::  %s", requestHttp.getParameter(usernameSt));
                            logger.info(infoLog);
                            infoLog = String.format("value[1]::  %s", requestHttp.getParameter(passwordSt));
                            logger.info(infoLog);
                            userId = requestHttp.getParameter(usernameSt);
                            password = requestHttp.getParameter(passwordSt);
                            infoLog = String.format("userId::  %s", userId);
                            logger.info(infoLog);
                            infoLog = String.format("password::  %s", password);
                            logger.info(infoLog);
                            infoLog = String.format("userList:::  %s", userList);
                            logger.info(infoLog);
                            if (userId.equals(userList.get(0).getUserID())) {
                                if (requestHttp.getParameter(usernameSt) != null) {
                                    trnJsonObjRequest.setModule(3L);
                                    trnJsonObjRequest.setaId(userList.get(0).getaID());
                                    trnJsonObjRequest.setuId(Long.parseLong(userList.get(0).getuID()));
                                    trnJsonObjRequest.setGroupId(Long.parseLong(userList.get(0).getGroupID()));
                                    trnJsonObjRequest.setJsonRequest("token: {" + authorization + "},body:{ai_code:" + requestHttp.getParameter(ai_code) + ",username:" + requestHttp.getParameter(usernameSt) + ",password:" + requestHttp.getParameter(passwordSt) + "}");
                                    trnJsonObjResponse = trnJson.saveJsonRequest(trnJsonObjRequest); //receive id
                                    trnJsonObjResponse.setResponseTime(new Date());
                                }
                                if (body.toString().contains("error")) {
                                    logger.info(">>>group==null<<<");
                                    if (body.toString().contains("error")) {
                                        res.setError_code(err00041);
                                        res.setError_message(Invalid_credentials);
                                        //Insert Transaction response
                                        trnJsonObjRequest.setModule(3L);
                                        trnJsonObjRequest.setJsonRequest("token: {" + authorization + "},body:{ai_code:" + requestHttp.getParameter(ai_code) + ",username:" + requestHttp.getParameter(usernameSt) + ",password:" + requestHttp.getParameter(passwordSt) + "}");
                                        trnJsonObjResponse = trnJson.saveJsonRequest(trnJsonObjRequest); //receive id
                                        trnJsonObjResponse.setResponseTime(new Date());
                                        trnJsonObjResponse.setErrorCode(err00041);
                                        trnJsonObjResponse.setJsonResponse("{\"message\":\"".concat(Invalid_credentials).concat("\",\"code\":\"").concat(err00041).concat("\"}"));
                                    }
                                    trnJson.saveJsonResponse(trnJsonObjResponse);

                                } else {//case success
                                    res.setEntries(body);
                                    String decode = deCodeJWT(body.toString());
                                    JSONObject decodeJsonObj = new JSONObject(decode);
                                    infoLog = String.format("decode>>>  %s", decode);
                                    logger.info(infoLog);
                                    ApiToken apiTokenObj = new ApiToken();
                                    for (int i = 0; i < userList.size(); i++) {
                                        //Insert Transaction response
                                        trnJsonObjResponse.setResponseTime(new Date());
                                        trnJsonObjResponse.setStatusCode(200L);
                                        trnJsonObjResponse.setaId(userList.get(i).getaID());
                                        trnJsonObjResponse.setuId(Long.parseLong(userList.get(i).getuID()));
                                        trnJsonObjResponse.setGroupId(Long.parseLong(userList.get(i).getGroupID()));
                                        trnJson.saveJsonResponse(trnJsonObjResponse);
                                        apiToken.deleteApiToken(userList.get(i).getuID());
                                        apiTokenObj.setAccessToken(body.toString());
                                        apiTokenObj.setCreateTime(new Date());
                                        apiTokenObj.setExp(Integer.valueOf(decodeJsonObj.get("exp").toString()));
                                        apiTokenObj.setExpireTime(new Date(Long.valueOf(decodeJsonObj.get("exp").toString()) * 1000));
                                        apiTokenObj.setAid(userList.get(i).getaID());
                                        apiTokenObj.setUid(Long.parseLong(userList.get(i).getuID()));
                                        apiToken.saveApiToken(apiTokenObj);
                                    }
                                }
                            } else {
                                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                                res.setError_code(err00041);
                                res.setError_message(Invalid_credentials);
                                trnJsonObjRequest.setModule(3L);
                                trnJsonObjRequest.setJsonRequest("token: {" + authorization + "},body:{ai_code:" + requestHttp.getParameter(ai_code) + ",username:" + requestHttp.getParameter(usernameSt) + ",password:" + requestHttp.getParameter(passwordSt) + "}");
                                trnJsonObjResponse = trnJson.saveJsonRequest(trnJsonObjRequest); //receive id
                                trnJsonObjResponse.setResponseTime(new Date());
                                trnJsonObjResponse.setStatusCode(401L);
                                trnJsonObjResponse.setErrorCode(err00041);
                                trnJsonObjResponse.setJsonResponse("{\"message\":\"".concat(Invalid_credentials).concat("\",\"code\":\"").concat(err00041).concat("\"}"));
                                trnJson.saveJsonResponse(trnJsonObjResponse);
                            }
                        }
                    }
                }

            } else if (aiCode == null && !body.toString().contains("Register user success")) {
                if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
                    logger.error("Ai code is null");
                    res.setError_code(err00041);
                    res.setError_message("Missing AI CODE");
                }
            } else if (body != null) {
                infoLog = String.format("body:403: %s", body);
                logger.error(infoLog);
                res.setEntries(body);
            }
        } catch (Exception ex) {
            String errorLog = String.format("Error: %s", ex);
            logger.error(errorLog);
            res.setError_code(err00041);
            res.setError_message(Invalid_credentials);
        }

        return res;

    }

    public String deCodeJWT(String token) {

        String jwtToken = token;
        logger.info("------------ Decode JWT ------------");
        String[] split_string = jwtToken.split("\\.");
        String base64EncodedHeader = split_string[0];
        String base64EncodedBody = split_string[1];
        String base64EncodedSignature = split_string[2];

        logger.info("~~~~~~~~~ JWT Header ~~~~~~~");
        Base64 base64Url = new Base64(true);
        String header = new String(base64Url.decode(base64EncodedHeader));
        String infoLog = String.format("JWT Header :  %s", header);
        logger.info(infoLog);

        logger.info("~~~~~~~~~ JWT Body ~~~~~~~");
        String body = new String(base64Url.decode(base64EncodedBody));
        infoLog = String.format("JWT Body :  %s", body);
        logger.info(infoLog);
        return body;

    }

}
