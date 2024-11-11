package com.scm.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scm.dto.TwilioMessageBatchResponse;
import com.scm.dto.TwilioMessageResponse;
import com.scm.entities.TwilioExtension;
import com.scm.entities.User;
import com.scm.helpers.AESEncryptionDecryptionHelper;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.repositories.TwilioRepo;
import com.scm.security.AESIVGenerator;
import com.scm.security.AESKeyGenerator;
import com.scm.services.TwilioService;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.Account;
import com.twilio.rest.api.v2010.account.IncomingPhoneNumber;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class TwilioServiceImpl implements TwilioService {

	@Autowired
	private TwilioRepo twilioRepo;

	private Logger logger = LoggerFactory.getLogger(TwilioServiceImpl.class);

	private String getTwilioAccountSid(User user) {
		try {
			TwilioExtension twilioExtension = twilioRepo.findByUser(user).orElse(null);
			logger.info("getTwilioAccountSid: {}", twilioExtension);
			String accountSid = twilioExtension.getTwilio_accountSid();
			String secretKey = twilioExtension.getSecretKey();
			logger.info("secretKey: {}", secretKey);
			String initVector = twilioExtension.getInitVector();
			logger.info("initVector: {}", initVector);
			logger.info("after decryption account sid {}",
					AESEncryptionDecryptionHelper.decryptData(accountSid, secretKey, initVector));
			return AESEncryptionDecryptionHelper.decryptData(accountSid, secretKey, initVector);
		} catch (Exception e) {
			return null;
		}

	}

	private String getTwilioAuthToken(User user) {
		try {
			TwilioExtension twilioExtension = twilioRepo.findByUser(user).orElse(null);
			String authToken = twilioExtension.getTwilio_authToken();
			String secretKey = twilioExtension.getSecretKey();
			String initVector = twilioExtension.getInitVector();
			return AESEncryptionDecryptionHelper.decryptData(authToken, secretKey, initVector);
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public TwilioExtension saveTwilioCredentials(TwilioExtension twilioExtension) {

		try {
			String secretKey = AESKeyGenerator.generateKey(128);
			String initVector = AESIVGenerator.generateInitVector();

			TwilioExtension newTwilioExtension = new TwilioExtension();
			String twilioId = UUID.randomUUID().toString();
			newTwilioExtension.setId(twilioId);
			newTwilioExtension.setTwilio_accountSid(AESEncryptionDecryptionHelper
					.encryptData(twilioExtension.getTwilio_accountSid(), secretKey, initVector));
			newTwilioExtension.setTwilio_authToken(AESEncryptionDecryptionHelper
					.encryptData(twilioExtension.getTwilio_authToken(), secretKey, initVector));
			newTwilioExtension.setSecretKey(secretKey);
			newTwilioExtension.setInitVector(initVector);
			logger.info("after encryption accountsid: {}", AESEncryptionDecryptionHelper
					.encryptData(twilioExtension.getTwilio_accountSid(), secretKey, initVector));

			newTwilioExtension.setUser(twilioExtension.getUser());
			return twilioRepo.save(newTwilioExtension);
		} catch (Exception e) {

			throw new RuntimeException("Failed to save Twilio credentials", e);

		}

	}

//	update twilio credentials
	@Override
	public TwilioExtension updateTwilioCredentials(TwilioExtension twilioExtension) {
		TwilioExtension oldTwilioExtension = twilioRepo.findById(twilioExtension.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Twilio Extension not found"));
		try {
			oldTwilioExtension.setId(twilioExtension.getId());
			oldTwilioExtension.setUser(twilioExtension.getUser());
			oldTwilioExtension.setSecretKey(twilioExtension.getSecretKey());
			oldTwilioExtension.setInitVector(twilioExtension.getInitVector());
			oldTwilioExtension.setTwilio_accountSid(
					AESEncryptionDecryptionHelper.encryptData(twilioExtension.getTwilio_accountSid(),
							twilioExtension.getSecretKey(), twilioExtension.getInitVector()));
			oldTwilioExtension.setTwilio_authToken(
					AESEncryptionDecryptionHelper.encryptData(twilioExtension.getTwilio_authToken(),
							twilioExtension.getSecretKey(), twilioExtension.getInitVector()));

			return twilioRepo.save(oldTwilioExtension);

		} catch (Exception e) {

			throw new RuntimeException("Failed to update Twilio credentials", e);

		}

	}

	// sending twilio message

	@Override
	public TwilioMessageBatchResponse sendMessage(String from, List<String> to, String message, User user) {
		logger.info("from number {}", from);
		String accountsid = getTwilioAccountSid(user);
		String authToken = getTwilioAuthToken(user);
		Twilio.init(accountsid, authToken);

		List<TwilioMessageResponse> twilioMessageResponseslist = new ArrayList<>();
		TwilioMessageBatchResponse twilioMessageResponses = new TwilioMessageBatchResponse();

		for (String recipient : to) {
			logger.info("sending message to: {}", recipient);
			try {
				PhoneNumber fromNumber = new PhoneNumber(from);
				PhoneNumber toNumber = new PhoneNumber(recipient);
				Message twilioMessage = Message.creator(toNumber, fromNumber, message).create();
				logger.info("Twilio Response {}", twilioMessage.toString());
				TwilioMessageResponse response = new TwilioMessageResponse();
				response.setAccountSid(twilioMessage.getAccountSid());
				response.setDateCreated(twilioMessage.getDateCreated());
				response.setDirection(twilioMessage.getDirection().toString());
				response.setSid(twilioMessage.getSid());
				response.setTo(twilioMessage.getTo());
				response.setStatus(twilioMessage.getStatus().toString());
				twilioMessageResponseslist.add(response);
				twilioMessageResponses.setMessageBatchResponse(twilioMessageResponseslist);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return twilioMessageResponses;

	}

	@Override
	public TwilioExtension getTwilioExtensionByUser(User user) {
		return twilioRepo.findByUser(user).orElse(null);

	}

	@Override
	public Account fetchTwilioAccountInformation(String accountSid, String authToken, User user) {
		try {
			Twilio.init(accountSid, authToken);
			Account account = Account.fetcher(accountSid).fetch();
			logger.info("twilio account info {}", account);
			return account;
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public List<String> fetchTwilioAccountIncomingPhoneNumbers(User user) {

		logger.info("after decryption accountsid {}", getTwilioAccountSid(user));

		String accountSid = getTwilioAccountSid(user);
		String authToken = getTwilioAuthToken(user);
		logger.info(accountSid);
		logger.info(authToken);

		Twilio.init(accountSid, authToken);
		ResourceSet<IncomingPhoneNumber> incomingPhoneNumbeResourceSet = IncomingPhoneNumber.reader().read();

		List<String> allIncomingPhoneNumbers = new ArrayList<>();

		for (IncomingPhoneNumber number : incomingPhoneNumbeResourceSet) {
			logger.info("phoneNumber: {}", number.getPhoneNumber());
			allIncomingPhoneNumbers.add(number.getPhoneNumber().toString());
		}
		logger.info("allIncomingPhoneNumbers {}", allIncomingPhoneNumbers.toString());
		return allIncomingPhoneNumbers;
	}

}
