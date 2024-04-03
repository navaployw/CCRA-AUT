/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccra3;
        
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arg.util.ErrorMessages;
import com.arg.util.GenericRuntimeException;
import com.ccra3.util.TokenizeTransformationProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Properties;

import javax.crypto.BadPaddingException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.binary.Base64;
@Component
public final class SymmetricCipher
{

    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    @Autowired
    TokenizeTransformationProvider skp;
    
    private static final int[] STREAM = {
        0x724f, 0x3041, 0x4258, 0x4e79, 0x4142, 0x3971, 0x5958, 0x5a68, 0x6543,
        0x356a, 0x636e, 0x6c77, 0x6447, 0x3875, 0x6333, 0x426c, 0x5979, 0x3554,
        0x5a57, 0x4e79, 0x5a58, 0x524c, 0x5a58, 0x6c54, 0x6347, 0x566a, 0x5730,
        0x634c, 0x5a75, 0x4977, 0x5955, 0x3043, 0x4141, 0x4a4d, 0x4141, 0x6c68,
        0x6247, 0x6476, 0x636d, 0x6c30, 0x6147, 0x3130, 0x4142, 0x4a4d, 0x616d,
        0x4632, 0x5953, 0x3973, 0x5957, 0x356e, 0x4c31, 0x4e30, 0x636d, 0x6c75,
        0x5a7a, 0x7462, 0x4141, 0x4e72, 0x5a58, 0x6c30, 0x4141, 0x4a62, 0x516e,
        0x6877, 0x6441, 0x4144, 0x5155, 0x5654, 0x6458, 0x4941, 0x416c, 0x7443,
        0x7250, 0x4d58, 0x2b41, 0x5949, 0x564f, 0x4143, 0x4141, 0x4234, 0x6341,
        0x4141, 0x4143, 0x4230, 0x7357, 0x7a71, 0x2b59, 0x2b74, 0x3755, 0x7044,
        0x4f62, 0x332b, 0x4d71, 0x6a77, 0x7274, 0x3973, 0x684c, 0x4856, 0x4358,
        0x6252, 0x7168, 0x3041, 0x4a4f, 0x6863, 0x3577, 0x3d3d
    };

    private static final String DEFAULT_SECRET_KEY_TEXT;

    static
    {
        char[] buffer = new char[STREAM.length * 2];

        for (int index = 0, i = 0; index < STREAM.length; i = ++index * 2)
        {
            int tmp;

            buffer[i] = (char) ((STREAM[index] & 0xff00) >> 8);
            buffer[i + 1] = (char) ((STREAM[index] & 0x00ff) >> 0);
        }

        DEFAULT_SECRET_KEY_TEXT = new String(buffer);
    }

    private static final String DEFAULT_KEY_ALGORITHM = new String(new char[]{
        0x4100 >> 8, 0x4500 >> 8, 0x5300 >> 8
    });

    private static final String DEFAULT_CIPHER_ALGORITHM = DEFAULT_KEY_ALGORITHM;
    private static Key defaultKey = null;
    private static Cipher defaultCipher = null;

    // private static SymmetricCipher instance = null;

    private Key key = null;
    private Cipher cipher = null;
    private String userKeyAlgor = null;
    private String userCipherAlgor = null;
    private String user_key = null;    
    private  String infoLog  = "";

    // private SymmetricCipher() throws Exception
    // {
        

    //     // instance = this;
    // }

    public SymmetricCipher builder() throws Exception
    {
        defaultKey = decodeKey(DEFAULT_SECRET_KEY_TEXT);
        defaultCipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

        infoLog= String.format("defaultKey>>> %s", defaultKey);
        logger.info(infoLog);

        infoLog = String.format("defaultCipher>>> %s", defaultCipher);
        logger.info(infoLog);

        infoLog = String.format("DEFAULT_SECRET_KEY_TEXT>>> %s", DEFAULT_SECRET_KEY_TEXT);
        logger.info(infoLog);

        if(defaultKey.getEncoded() != null){
            infoLog = String.format("defaultKey>>> %s", new Base64().encodeAsString(defaultKey.getEncoded()));
            logger.info(infoLog);
        }
       
        userKeyAlgor = skp.USER_KEY_ALGOR;
        userCipherAlgor = skp.USER_CIPHER_ALGOR;
        user_key = skp.USER_KEY;

        logger.info("########## CONFIG SC ########");
        logger.info(userKeyAlgor);
        logger.info(userCipherAlgor);
        logger.info(user_key);

        if ((null == userKeyAlgor) || (userKeyAlgor.length() == 0)
            || (null == userCipherAlgor) || (userCipherAlgor.length() == 0)
            || (null == user_key) || (user_key.length() == 0)
            || (null == (user_key = decrypt(defaultCipher, defaultKey, user_key))))
        {
            logger.info("Default secret key loaded.");
            key = defaultKey;
            cipher = defaultCipher;
        }
        else
        {
            logger.info("Enter else");
            key = decodeKey(user_key);
            cipher = Cipher.getInstance(userCipherAlgor);
            logger.info("User's Secret Key loaded.");
        }

        return this;
    }

    public String encrypt(final String text) throws UnsupportedEncodingException
    {
        return encrypt(this.cipher, this.key, text);
    }

    public String encrypt(final Key key, final String text) throws UnsupportedEncodingException
    {
        return encrypt(cipher, key, text);
    }

    private String encrypt(final Cipher cipher, final Key key,
            final String text) throws UnsupportedEncodingException
    {
        
        return new Base64().encodeAsString(encryptStream(cipher, key, text.getBytes(StandardCharsets.UTF_8)));
        
    }

    public byte[] encryptStream(final Cipher cipher, final Key key,
            final byte[] stream)
    {
        if ((null == cipher) || (null == key) || (null == stream))
        {
           
        }

        try
        {
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return cipher.doFinal(stream);
        }
        catch (Exception e)
        {
            return null;
        }
       
    }

    public String decrypt(final String text) throws UnsupportedEncodingException
    {
        return decrypt(this.cipher, this.key, text);
    }

    public String decrypt(final Key key, final String text) throws UnsupportedEncodingException
    {
        return decrypt(this.cipher, key, text);
    }

    private String decrypt(final Cipher cipher, final Key key,
            final String text) throws UnsupportedEncodingException
    {
        infoLog = String.format("195::text:: %s", text);
        logger.info("##### USER_KEY_ALGOR "+ skp.USER_KEY_ALGOR);
        logger.info(infoLog);
        return new String(decryptStream(cipher, key,
                new Base64().decode(text.getBytes())),"UTF-8");
    }

    public byte[] decryptStream(final Cipher cipher, final Key key,
            final byte[] stream)
    {
        infoLog = String.format("cipher:: %s", cipher);
        logger.info(infoLog);
        infoLog = String.format("key:: %s", key);
        logger.info(infoLog);
        infoLog = String.format("stream:: %s", stream);
        logger.info(infoLog);
        
        if ((null == cipher) || (null == key) || (null == stream))
        {
            
        }

        try
        {
            logger.info("211:enter try");
            cipher.init(Cipher.DECRYPT_MODE, key);

            return cipher.doFinal(stream);
        }
        catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e)
        {
            logger.error("222:Exception: "+ e);
           throw new GenericRuntimeException("CMN-R00000",
                    ErrorMessages.getString("CMN-R00000",
                            (null == e)
                    ? e.toString()
                    : e.getMessage()));
        }
    }

    public Key createKey()
            throws NoSuchAlgorithmException
    {
        return createKey("AES", 256);
    }

    public Key createKey(final String algorithm)
            throws NoSuchAlgorithmException
    {
        KeyGenerator key_gen = KeyGenerator.getInstance(algorithm);
        key_gen.init(new SecureRandom());

        return key_gen.generateKey();
    }

    public Key createKey(final String algorithm, final int size)
            throws NoSuchAlgorithmException
    {
        KeyGenerator key_gen = KeyGenerator.getInstance(algorithm);
        key_gen.init(size, new SecureRandom());

        return key_gen.generateKey();
    }

    public static String encodeKey(final Key givenKey)
            throws Exception
    {
        if (null == givenKey)
        {
           
        }

        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        

        try
        {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(givenKey);

            return new Base64().encodeAsString(baos.toByteArray());
        }
        catch (IOException e)
        {
           return null;
        }
        finally
        {
            if (null != oos)
            {
                oos.close();
                oos = null;
            }

            if (null != baos)
            {
                baos.close();
                baos = null;
            }
        }
        
    }

    public static Key decodeKey(final String encodedKey)
            throws Exception
    {
        if ((null == encodedKey) || (encodedKey.length() == 0))
        {
            
        }

        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        

        try
        {
            byte[] raw = new Base64().decode(encodedKey.getBytes());
            bais = new ByteArrayInputStream(raw);
            ois = new ObjectInputStream(bais);
            return ((Key) ois.readObject());
        }
        catch (IOException e)
        {
            throw new Exception(e);
        }
        catch (ClassNotFoundException e)
        {
           return null;
                   
        }
        finally
        {
            if (null != ois)
            {
                ois.close();
                ois = null;
            }

            if (null != bais)
            {
                bais.close();
                bais = null;
            }
        }
        
    }

    public void createSecretKeyFile(final File file)
            throws Exception
    {
        createSecretKeyFile(DEFAULT_KEY_ALGORITHM, 0,
                DEFAULT_CIPHER_ALGORITHM, defaultKey, file);
    }

    public void createSecretKeyFile(final Key key, final File file)
            throws Exception
    {
        createSecretKeyFile(key, this.cipher, this.key, file);
    }

    public void createSecretKeyFile(final String algorithm, final int keyLength,
            final String cipherAlgor, final File file)
            throws Exception
    {
        createSecretKeyFile(algorithm, keyLength, cipherAlgor, defaultKey,
                file);
    }

    private void createSecretKeyFile(final String algorithm, final int keyLength,
            final String cipherAlgor, final Key internalKey, final File file)
            throws Exception
    {
        Key new_key = ((keyLength > 0)
                ? createKey(algorithm, keyLength)
                : createKey(algorithm));

        createSecretKeyFile(new_key, cipherAlgor, internalKey, file);
    }

    private void createSecretKeyFile(final Key key, final String cipherAlgor,
            final Key encKey, final File file)
            throws Exception
    {
        Cipher cipher = Cipher.getInstance(cipherAlgor);
        createSecretKeyFile(key, cipher, encKey, file);
    }

    private void createSecretKeyFile(final Key key, final Cipher cipher,
            final Key encKey, final File file)
            throws Exception
    {
        String key_string = encrypt(cipher, encKey, encodeKey(key));
        FileWriter fw = new FileWriter(file);

        try
        {
            fw.write(key_string);
        }
        finally
        {
            fw.close();
            fw = null;
        }
    }

    public Key loadKey(final File file) throws Exception
    {
        if (null == file)
        {
            
        }

        if (!file.exists())
        {
            
        }

        FileReader fis = new FileReader(file);

        try
        {
            StringBuffer buffer = new StringBuffer();
            char[] ch = new char[100];
            int bytes_read;

            while (-1 != (bytes_read = fis.read(ch)))
            {
                buffer.append(ch, 0, bytes_read);
            }

            return loadKey(buffer.toString());
        }
        finally
        {
            fis.close();
            fis = null;
        }
    }

    public Key loadKey(String encryptedKey)
        throws Exception
    {
        return decodeKey(decrypt(encryptedKey));
    }

    public Cipher getCipher()
    {
        return this.cipher;
    }

    public Cipher getCipher(final Key key) throws NoSuchAlgorithmException,
            NoSuchPaddingException
    {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());

        return cipher;
    }

    // public static synchronized SymmetricCipher getInstance()
    //         throws Exception
    // {
    //     return ((null == instance) ? new SymmetricCipher() : instance);
    // }
}