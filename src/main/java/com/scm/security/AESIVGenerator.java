package com.scm.security;

import java.security.SecureRandom;
import java.util.Base64;

public class AESIVGenerator {
	
	public static String generateInitVector() {
		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);
		return Base64.getEncoder().encodeToString(iv);
	}

}
