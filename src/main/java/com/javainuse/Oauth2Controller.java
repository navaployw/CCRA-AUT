/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.javainuse;

import ch.qos.logback.classic.Logger;
import com.javainuse.config.AesEcbEncryptDecrypt;
import com.javainuse.config.AuthorizationServer;
import com.javainuse.config.ViewApiUserService;
import com.javainuse.model.ResponseModel;
import com.javainuse.model.ViewApiUser;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author kumpeep
 */
@RestController
public class Oauth2Controller {

    @Autowired
    private ClientDetailsServiceConfigurer clients;

    @Autowired
    private ViewApiUserService userService;

    private List<ViewApiUser> userList = new ArrayList<>();

    private final static Logger logger = (Logger) LoggerFactory.getLogger(Oauth2Controller.class);
    String userId;
    String password;

    @RequestMapping("/validateUser")
    @ResponseBody
    public Principal user(Principal user) {
        return user;
    }

    @RequestMapping("/restartUser")
    @ResponseBody
    public ResponseEntity restart() {
        logger.info("<<<<<<<<restart");
        ResponseModel res = new ResponseModel();
        res.setCode("200");
        res.setMessage("Register user success");
        Application.restart();
        return ResponseEntity.status(200).body(res);
    }
    
    @RequestMapping("/registerUser")
    @ResponseBody
    public ResponseEntity registerUser(@RequestBody String requestJson) {
        return restart();
    }
    
//    @RequestMapping("/registerUser")
//    @ResponseBody
//    public ResponseEntity registerUser(@RequestBody String requestJson) {
//        final String ai_code = "ai_code";
//        JSONObject json = new JSONObject(requestJson);
//        logger.info("Request:aicode:: " + json.get("ai_code").toString());
//        logger.info("Request:username:: " + json.get("username").toString());
//        logger.info("Request:password:: " + json.get("password").toString());
//        String aiCode = json.get("ai_code").toString();
//        userId = json.get("username").toString();
//        password = json.get("password").toString();
////        String authorization = request.getHeader("Authorization");
////        String accessToken = "";
////        if (authorization != null && authorization.toLowerCase().startsWith("bearer")) {
////            accessToken = authorization.split("Bearer ")[1];
////            logger.info("accessToken::" + accessToken);
////        }
////        String CCRATokenBody = deCodeCCRAToken();
////        JSONObject jsonObjCCRA = new JSONObject(CCRATokenBody);
//
//        //Check Validate//
//        ResponseModel res = new ResponseModel();
////        logger.info("Info>>>"+jsonObjCCRA.getJSONObject("info"));
//
////        String aiCCRA = jsonObjCCRA.get(ai_code).toString();
////        logger.info("AI_CODE_CCRA>>>" + jsonObjCCRA.get(ai_code));
////        String userIdToken = jsonObjCCRA.get("client_id").toString();
////        logger.info("Username>>>" + userIdToken);
////        Date expCCRA = new Date(Long.valueOf(jsonObjCCRA.get("exp").toString()) * 1000);
////        logger.info("expCCRA>>>" + jsonObjCCRA.get("exp"));
////        logger.info("expCCRA>>>" + expCCRA);
////        if (checkUserValidForAddNewUser(userIdToken, aiCCRA)) {
//            try {
//                AesEcbEncryptDecrypt encryptObject = new AesEcbEncryptDecrypt();
//                InMemoryClientDetailsServiceBuilder inMemoryBuilder = clients.inMemory();
//                SymmetricCipher cy = SymmetricCipher.getInstance();
//                userList = userService.getUserByAICode(aiCode);
//                if (!userList.isEmpty()) {
//                    if (userList.size() > 1) {
//
//                    } else {
//                        logger.info(">>>userList not empty<<<");
//                        if (userList.get(0).getPassword() != null && userList.get(0).getUserID() != null) {
//                            logger.info("userList.get(0).getSecretKey()::"+userList.get(0).getSecretKey());
//                            AesEcbEncryptDecrypt.setKey(userList.get(0).getSecretKey());
//                            // Authorization: Basic base64credentials   
////                            userId = encryptObject.decrypt(userId);
////                            password = encryptObject.decrypt(password);
//                            logger.info("Values:userId:" + userId);
//                            logger.info("Values:password:" + password);
//                            logger.info("userList:userId:" + userList.get(0).getUserID());
//                            logger.info("userList:password:" + userList.get(0).getPassword());
//                            if (userId.equals(userList.get(0).getUserID()) && password.equals(userList.get(0).getPassword())) {
//                                String passDecode = cy.decrypt(userList.get(0).getPassword());
//                                logger.info(">>>passDecode::" + passDecode);
//                                AesEcbEncryptDecrypt.setKey(userList.get(0).getSecretKey());
//                                logger.info(">>>clientKey.getClientId():::" + userId);
//                                String usrOAuth = encryptObject.encrypt(userId);
//                                String pwdOAuth = encryptObject.encrypt(passDecode);
//                                logger.info("usrOAuth>>>" + usrOAuth);
//                                logger.info("pwdOAuth>>>" + pwdOAuth);
//                                inMemoryBuilder
//                                        .withClient(usrOAuth)
//                                        .authorizedGrantTypes("client_credentials")
//                                        .secret(authServer.encoder().encode(pwdOAuth))
//                                        .scopes("user_info", "read", "write");
//                                res.setMessage("Register successful");
//                                res.setCode("200");
//                            } else {
//                                res.setCode("500");
//                                res.setMessage("Credential not match");
//                                return ResponseEntity.status(500).body(res);
//                            }
//
//                        }
//                    }
//                } else {
//                    logger.info(">>>user not found");
//                    res.setMessage("User not found");
//                    return ResponseEntity.status(500).body(res);
//                }
//            } catch (UnsupportedEncodingException ex) {
//                logger.error("Error " + ex.getMessage());
//                res.setMessage(ex.getMessage());
//                return ResponseEntity.status(500).body(res);
//            } catch (Exception ex) {
//                logger.error("Error " + ex);
//                res.setMessage(ex.getMessage());
//                return ResponseEntity.status(500).body(res);
//            }
////        } else {
////            res.setMessage("User is not valid to add new user");
////        }
//        return ResponseEntity.status(200).body(res);
//    }


    public String deCodeCCRAToken() {
        Base64 base64Url = new Base64(true);
        String jwtToken = getBearerTokenHeader();
        logger.info("------------ Decode JWT ------------");
        if (jwtToken != null) {
            String[] split_string = jwtToken.split("\\.");
            String base64EncodedHeader = split_string[0];
            String base64EncodedBody = split_string[1];
            String base64EncodedSignature = split_string[2];

            logger.info("~~~~~~~~~ JWT Header ~~~~~~~");

            String header = new String(base64Url.decode(base64EncodedHeader));
            String infoLog = String.format("JWT Header : %s", header);
            logger.info(infoLog);

            logger.info("~~~~~~~~~ JWT Body ~~~~~~~");
            String body = new String(base64Url.decode(base64EncodedBody));
            infoLog = String.format("JWT Body : %s", body);
            logger.info(infoLog);
            infoLog = String.format("JWT Signature : %s", base64EncodedSignature);
            logger.info(infoLog);
            return body;
        }
        return null;
    }

    public static String getBearerTokenHeader() {
        try {

            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                return null;
            }
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

            return request.getHeader("Authorization").split(" ")[1];

        } catch (Exception ex) {
            String errorLog = String.format("exception>>> %s", ex);
            logger.info(errorLog);
            return ex.toString();
        }
    }

}
