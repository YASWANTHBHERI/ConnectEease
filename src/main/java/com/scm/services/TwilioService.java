package com.scm.services;

import java.util.List;

import com.scm.dto.TwilioMessageBatchResponse;
import com.scm.entities.TwilioExtension;
import com.scm.entities.User;
import com.twilio.rest.api.v2010.Account;

public interface TwilioService {
	
	public TwilioExtension saveTwilioCredentials(TwilioExtension twilioExtension);
	
	public TwilioExtension getTwilioExtensionByUser(User user);
	
	public TwilioMessageBatchResponse sendMessage(String from, List<String> to, String message, User user);
	
	Account fetchTwilioAccountInformation(String accountSid, String authToken,User user);
	
	List<String> fetchTwilioAccountIncomingPhoneNumbers(User user);
	
	public TwilioExtension updateTwilioCredentials(TwilioExtension twilioExtension);
}
