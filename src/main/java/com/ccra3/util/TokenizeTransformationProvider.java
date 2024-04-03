package com.ccra3.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenizeTransformationProvider {
    @Value("${com.arg.ccra.adminonline.utils.secretAlgor}")
    public String PROVIDER_SECRET_KEY;
    @Value("${com.arg.ccra.adminonline.utils.cipherAlgor}")
    public String USER_CIPHER_ALGOR;
    @Value("${com.arg.ccra.adminonline.utils.userAlgor}")
    public String USER_KEY_ALGOR;
    @Value("${com.arg.ccra.adminonline.utils.userKey}")
    public String USER_KEY;
}
