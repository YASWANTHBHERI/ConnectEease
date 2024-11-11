package com.scm.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.scm.services.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${spring.mail.properties.domain}")
	private String domainName;
	
	private Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Override
	public void sendEmail(String to, String subject, String body) {
		logger.info("from {}",domainName);
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		message.setFrom(domainName);
		mailSender.send(message);
		logger.info("message sent {}", message);
		
	}

}
