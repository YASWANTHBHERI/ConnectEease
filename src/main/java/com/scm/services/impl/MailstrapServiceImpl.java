package com.scm.services.impl;

import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.scm.entities.MailStrapExtension;
import com.scm.entities.User;
import com.scm.helpers.AESEncryptionDecryptionHelper;
import com.scm.helpers.AppConstants;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.repositories.MailstrapRepo;
import com.scm.security.AESIVGenerator;
import com.scm.security.AESKeyGenerator;
import com.scm.services.MailstrapService;

import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailstrapServiceImpl implements MailstrapService {

	@Autowired
	private MailstrapRepo mailstrapRepo;



	private Logger logger = LoggerFactory.getLogger(MailstrapServiceImpl.class);

	@Override
	public MailStrapExtension saveMailstrapCredentials(MailStrapExtension mailStrapExtension) {
		try {
			String mailstrapId = UUID.randomUUID().toString();
			String secretKey = AESKeyGenerator.generateKey(128);
			String initVector = AESIVGenerator.generateInitVector();

			MailStrapExtension newMailstrapExtension = new MailStrapExtension();
			newMailstrapExtension.setMailstrapId(mailstrapId);
			newMailstrapExtension.setMailstrapUsername(AESEncryptionDecryptionHelper
					.encryptData(mailStrapExtension.getMailstrapUsername(), secretKey, initVector));
			newMailstrapExtension.setMailstrapPassword(AESEncryptionDecryptionHelper
					.encryptData(mailStrapExtension.getMailstrapPassword(), secretKey, initVector));
			newMailstrapExtension.setSecretKey(secretKey);
			newMailstrapExtension.setInitVector(initVector);
			newMailstrapExtension.setUser(mailStrapExtension.getUser());

			logger.info("before encryption mailstrap username: {}", mailStrapExtension.getMailstrapUsername());
			logger.info("after encryption mailstrap username: {}", AESEncryptionDecryptionHelper
					.encryptData(mailStrapExtension.getMailstrapUsername(), secretKey, initVector));

			return mailstrapRepo.save(newMailstrapExtension);
		} catch (Exception e) {
			throw new RuntimeException("Failed to save mailstrap credentials", e);
		}

	}

	@Override
	public boolean verifymailstrapCredentials(String username, String password) {

		Properties properties = new Properties();
		properties.put(AppConstants.MAIL_SMTP_HOST, AppConstants.HOST_DEFAULT_VALUE);
		properties.put(AppConstants.MAIL_SMTP_PORT, AppConstants.PORT_DEFAULT_VALUE);
		properties.put(AppConstants.MAIL_SMTP_AUTH, AppConstants.AUTH_DEFAULT_VALUE);
		properties.put(AppConstants.MAIL_SMTP_STARTTLS_ENABLE, AppConstants.STARTTLS_ENABLE_DEFAULT_VALUE);

		Session session = Session.getInstance(properties);

		try {
			Transport transport = session.getTransport("smtp");
			transport.connect("sandbox.smtp.mailtrap.io", username, password);
			logger.info(transport.toString());
			transport.close();

			return true;

		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}

	}

	@Override
	public MailStrapExtension getMailStrapExtensionByUser(User user) {
		return mailstrapRepo.findByUser(user).orElse(null);
	}

	@Override
	public MailStrapExtension updateMailstrapCredentials(MailStrapExtension mailStrapExtension) {
		MailStrapExtension oldMailStrapExtension = mailstrapRepo.findByUser(mailStrapExtension.getUser())
				.orElseThrow(() -> new ResourceNotFoundException("mailstrap Extension not found"));

		try {
			oldMailStrapExtension.setMailstrapId(mailStrapExtension.getMailstrapId());
			oldMailStrapExtension.setUser(mailStrapExtension.getUser());
			oldMailStrapExtension.setSecretKey(mailStrapExtension.getSecretKey());
			oldMailStrapExtension.setInitVector(mailStrapExtension.getInitVector());
			oldMailStrapExtension.setMailstrapUsername(
					AESEncryptionDecryptionHelper.encryptData(mailStrapExtension.getMailstrapUsername(),
							mailStrapExtension.getSecretKey(), mailStrapExtension.getInitVector()));
			oldMailStrapExtension.setMailstrapPassword(
					AESEncryptionDecryptionHelper.encryptData(mailStrapExtension.getMailstrapPassword(),
							mailStrapExtension.getSecretKey(), mailStrapExtension.getInitVector()));

			return mailstrapRepo.save(oldMailStrapExtension);

		} catch (Exception e) {

			throw new RuntimeException("Failed to update Mailstrap credentials", e);

		}
	}

	@Override
	public boolean sendMail(String fromMail, String[] recipients, String subject, String body, User user) {

		MailStrapExtension mailStrapExtension = mailstrapRepo.findByUser(user).orElse(null);
		if (mailStrapExtension == null) {
			return false;
		}

		try {
			String secretKey = mailStrapExtension.getSecretKey();
			String initVector = mailStrapExtension.getInitVector();
			
			String username = AESEncryptionDecryptionHelper.decryptData(mailStrapExtension.getMailstrapUsername(),
					secretKey, initVector);
			String password = AESEncryptionDecryptionHelper.decryptData(mailStrapExtension.getMailstrapPassword(),
					secretKey, initVector);

			Properties mailProperties = new Properties();
			mailProperties.put(AppConstants.MAIL_SMTP_HOST, AppConstants.HOST_DEFAULT_VALUE);
			mailProperties.put(AppConstants.MAIL_SMTP_PORT, AppConstants.PORT_DEFAULT_VALUE);
			mailProperties.put(AppConstants.MAIL_SMTP_AUTH, AppConstants.AUTH_DEFAULT_VALUE);
			mailProperties.put(AppConstants.MAIL_SMTP_STARTTLS_ENABLE, AppConstants.STARTTLS_ENABLE_DEFAULT_VALUE);
			
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setJavaMailProperties(mailProperties);
			mailSender.setUsername(username);
			mailSender.setPassword(password);

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(fromMail);
			helper.setTo(recipients);
			helper.setSubject(subject);
			helper.setText(body,false);
			
//			sending mail
			mailSender.send(message);
			logger.info("mail sent successfully");
			return true;
		} catch (Exception e) {
			logger.info("mail sending failed {}",e.getMessage());
			return false;
		}

	}

}
