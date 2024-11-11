package com.scm.security;



import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;


@Component
public class AESKeyGenerator {

   public static String generateKey(int keySize) throws Exception {
	   KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	   keyGenerator.init(keySize);
	   SecretKey key = keyGenerator.generateKey();
	   return Base64.getEncoder().encodeToString(key.getEncoded());
   }
}

