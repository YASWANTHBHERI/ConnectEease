package com.scm.services;


import com.scm.entities.MailStrapExtension;
import com.scm.entities.User;

public interface MailstrapService {

	public MailStrapExtension saveMailstrapCredentials(MailStrapExtension mailStrapExtension);
	
	public MailStrapExtension getMailStrapExtensionByUser(User user);
	
	public MailStrapExtension updateMailstrapCredentials(MailStrapExtension mailStrapExtension);
	
	public boolean verifymailstrapCredentials(String username, String password);
	
	public boolean sendMail(String fromMail, String[] recipients, String subject, String body, User user);
	
	
}
