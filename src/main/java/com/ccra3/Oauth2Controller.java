///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
package com.ccra3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class Oauth2Controller {

//    private ClientDetailsServiceConfigurer clients;SSS
//    private ViewApiUserService userService = new ViewApiUserService(viewApiUserRepository);
    @Autowired
    private ViewApiUserService userService;
    private List<ViewApiUser> userList = new ArrayList<>();

    private final static Logger logger = LogManager.getLogger(Oauth2Controller.class);
    String userId;
    String password;

    @RequestMapping("/validateUser")
    @ResponseBody
    public Principal user(Principal user) {
        return user;
    }

    public List<ViewApiUser> findByflagActive() {
        try {
            userList = userService.findByflagActive(Boolean.TRUE);
            if (!userList.isEmpty()) {
                logger.info(">>>userList not empty<<<");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        return userList;
    }

   @PostMapping("/restartUser")
   public ResponseEntity<Map<String, Object>> restart(HttpServletRequest request, HttpServletResponse response) {
       logger.info("<<<<<<<<restart>>>>>>>>");
       ResponseModel res = new ResponseModel();
       res.setCode("200");
       res.setMessage("Register user success");
        WebSecurityConfig.restart();
       return new ResponseEntity<>(CommonUtils.response(request, response, null, "restart success", null), HttpStatus.OK);
   }

   @PostMapping("/registerUser")
   public ResponseEntity registerUser(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestJson) {
       return restart(request, response);
   }
   
//    public String deCodeCCRAToken() {
//        Base64 base64Url = new Base64(true);
//        String jwtToken = getBearerTokenHeader();
////        logger.info("------------ Decode JWT ------------");
//        if (jwtToken != null) {
//            String[] split_string = jwtToken.split("\\.");
//            String base64EncodedHeader = split_string[0];
//            String base64EncodedBody = split_string[1];
//            String base64EncodedSignature = split_string[2];
//
////            logger.info("~~~~~~~~~ JWT Header ~~~~~~~");
//
//            String header = new String(base64Url.decode(base64EncodedHeader));
////            logger.info("JWT Header : " + header);
//
////            logger.info("~~~~~~~~~ JWT Body ~~~~~~~");
//            String body = new String(base64Url.decode(base64EncodedBody));
//            String infoLog = String.format("JWT Body : %s", body);
////            logger.info(infoLog);
//            infoLog = String.format("JWT Signature : %s", base64EncodedSignature);
////            logger.info(infoLog);
//            return body;
//        }
//        return null;
//    }
//
//    public static String getBearerTokenHeader() {
//        try {
//
//            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//            if (requestAttributes == null) {
//                return null;
//            }
//            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
//
//            return request.getHeader("Authorization").split(" ")[1];
//
//        } catch (Exception ex) {
//            String errorLog = String.format("exception>>> %s", ex);
////            logger.info(errorLog);
//            return ex.toString();
//        }
//    }
}
