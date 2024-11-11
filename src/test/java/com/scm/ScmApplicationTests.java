package com.scm;

import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//import com.scm.security.AES;
//import com.scm.security.AESIVGenerator;
//import com.scm.security.AESKeyGenerator;
//import com.scm.services.EmailService;
//import com.scm.services.MailstrapService;

@SpringBootTest
class ScmApplicationTests {
//	@Autowired
//	private EmailService emailService;

//	@Test
//	void sendMail() {
//		emailService.sendEmail("bheriyaswanth@gmail.com", "This is test message from Mailstrap",
//				"This is SCM project working on email service");
//	}
	
//	@Test
//	void aesEncryptionAndDecryption() throws Exception {
//		String secretKey = AESKeyGenerator.generateKey(128);
//		String initVector = AESIVGenerator.generateInitVector();
//		
//		String orginalText = "ACf8cd50c4164412c3419f1fa5de563899";
//		
//		String encryptedText = AES.encrypt(orginalText, secretKey, initVector);
//		String decryptedText = AES.decrypt(encryptedText, secretKey, initVector);
//		
//		System.out.println("orginal: "+orginalText);
//		System.out.println("encryptedText: "+encryptedText);
//		System.out.println("decryptedText: "+decryptedText);
//		
//	}
//	@Autowired
//	private MailstrapService mailstrapService;
//
//	@Test
//	void verifyMailstrapCredentials() throws Exception {
//		String username = "92bc70fd1a4dbf";
//		String password = "1eb05e073527bc";
//
//		boolean verifyMailstrapCredentials = mailstrapService.verifymailstrapCredentials(username,password);
//		System.out.print(verifyMailstrapCredentials);
//	}

}
