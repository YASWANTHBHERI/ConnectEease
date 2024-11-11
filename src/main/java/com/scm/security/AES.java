package com.scm.security;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class AES {

	private static final String AES_ALGORITHM = "AES";
	private static final String TRANSFORMATION =  "AES/CBC/PKCS5Padding";

	public static String encrypt(String data,String base64key,String base64IV) throws Exception {
		IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(base64IV));
		SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(base64key), AES_ALGORITHM);
		
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey,iv);
		
		byte[] encrypted = cipher.doFinal(data.getBytes());
		return Base64.getEncoder().encodeToString(encrypted);
	}

	public static String decrypt(String encryptedData, String base64Key, String base64IV) throws Exception {
		IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(base64IV));
		SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(base64Key), AES_ALGORITHM);
		
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, secretKey,iv);
		
		byte[] decodeBytes = Base64.getDecoder().decode(encryptedData);
		byte[] orginal = cipher.doFinal(decodeBytes);
		
		return new String(orginal);
	}

}
