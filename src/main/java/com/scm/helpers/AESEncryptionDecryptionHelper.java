package com.scm.helpers;

import com.scm.security.AES;

public class AESEncryptionDecryptionHelper {

	
	public static String encryptData(String data,String secretKey, String initVector) throws Exception {
		return AES.encrypt(data, secretKey, initVector);
		
	}
	
	public static String decryptData(String encryptedData, String secretKey, String initVector) throws Exception{
		return AES.decrypt(encryptedData, secretKey, initVector);
		
	}
	
	
	
}
