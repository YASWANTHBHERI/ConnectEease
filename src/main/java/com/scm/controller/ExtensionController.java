package com.scm.controller;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scm.dto.MailstrapRequestBody;
import com.scm.dto.TwilioMessageBatchResponse;
import com.scm.dto.TwilioMessageRequestBody;
import com.scm.entities.MailStrapExtension;
import com.scm.entities.MarketPlace;
import com.scm.entities.TwilioExtension;
import com.scm.entities.User;
import com.scm.forms.MailstrapForm;
import com.scm.forms.TwilioForm;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.MailstrapService;
import com.scm.services.MarketPlaceService;
import com.scm.services.TwilioService;
import com.scm.services.UserService;
import com.twilio.rest.api.v2010.Account;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts/send")
public class ExtensionController {

	@Autowired
	private TwilioService twilioService;

	@Autowired
	private UserService userService;

	@Autowired
	private ContactService contactService;

	@Autowired
	private MarketPlaceService marketPlaceService;

	@Autowired
	private MailstrapService mailstrapService;

	private Logger logger = LoggerFactory.getLogger(ExtensionController.class);

	@RequestMapping("/twilio")
	public String sendTwilioMessageView(@RequestParam("id") String contactIds, Authentication authentication,
			Model model) {

		String userEmail = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userEmail);

		TwilioExtension twilioExtension = twilioService.getTwilioExtensionByUser(user);
		if (twilioExtension == null) {
			TwilioForm twilioForm = new TwilioForm();
			model.addAttribute("twilioForm", twilioForm);
			model.addAttribute("contactIds", contactIds);
			return "user/twilio_login";
		}

		// fetch incomingphonenumber
		List<String> incomingPhoneNumbers = twilioService.fetchTwilioAccountIncomingPhoneNumbers(user);
		model.addAttribute("IncomingPhoneNumbers", incomingPhoneNumbers);

		// Recipients list
		String[] recipientsArr = contactIds.split(",");
		List<String> contactsList = Arrays.asList(recipientsArr);
		logger.info("contactsList {}", contactsList);
		List<String> recipientsList = contactService.getRecipientsList(user, contactsList);
		logger.info("repo contactlist {}", recipientsList);
		model.addAttribute("recipientsList", recipientsList);

		logger.info("sending twilio message view & contactIds = {}", contactIds);

		return "user/twilio";
	}

	@GetMapping("/twilio/add")
	public String addTwilioLoginView(Model model) {
		logger.info("twilio loginview");
		TwilioForm twilioForm = new TwilioForm();
		model.addAttribute("twilioForm", twilioForm);
		return "user/contacts";

	}

// Twilio Account verfication and Saving
	@RequestMapping(value = "/twilio/add", method = RequestMethod.POST)
	public String addTwilioCredentials(@ModelAttribute TwilioForm twilioForm, Authentication authentication,
			Model model, HttpSession httpSession) {
		try {
			logger.info("twilio credentials {}", twilioForm);
			String userEmail = Helper.getEmailOfLoggedInUser(authentication);
			User user = userService.getUserByEmail(userEmail);

			Account account = twilioService.fetchTwilioAccountInformation(twilioForm.getTwilio_accountSid(),
					twilioForm.getTwilio_authToken(), user);

			if (account == null) {
				httpSession.setAttribute("message",
						Message.builder().content("Invalid Credentials Provided").type(MessageType.red).build());
				return "user/twilio_login";
			}

			TwilioExtension twilioExtension = new TwilioExtension();
			twilioExtension.setTwilio_accountSid(twilioForm.getTwilio_accountSid());
			twilioExtension.setTwilio_authToken(twilioForm.getTwilio_authToken());
			twilioExtension.setUser(user);
			twilioService.saveTwilioCredentials(twilioExtension);
			twilioService.fetchTwilioAccountIncomingPhoneNumbers(user);
			return "redirect:/user/contacts";
		} catch (Exception e) {
			e.printStackTrace();
			httpSession.setAttribute("message",
					Message.builder().content("Unable to login").type(MessageType.red).build());
			return "user/twilio_login";
		}

	}

	@RequestMapping(value = "/twilio/message", method = RequestMethod.POST)
	@ResponseBody
	public TwilioMessageBatchResponse sendMessage(@RequestBody TwilioMessageRequestBody twilioMessageRequest,
			Authentication authentication) {
		String userEmail = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userEmail);
		logger.info("twilio request body: {}", twilioMessageRequest.toString());
		TwilioMessageBatchResponse twilioMessageResponse = twilioService.sendMessage(twilioMessageRequest.getSender(),
				twilioMessageRequest.getRecipientsList(), twilioMessageRequest.getMessage(), user);
		return twilioMessageResponse;
	}

	@RequestMapping(value = "/verify/install", method = RequestMethod.GET)
	@ResponseBody
	public MarketPlace checkMarketPlaceInstallation(@RequestParam("appCode") String appCode,
			Authentication authentication) {
		String userEmail = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userEmail);
		logger.info("appCode {}", appCode);

		MarketPlace installedApp = marketPlaceService.getByUserAndAppcode(appCode, user).orElse(null);

		if (installedApp != null) {
			logger.info("installed {}", installedApp.toString());
			return installedApp;
		}

		return new MarketPlace();
	}

	// Update Twilio Credentials
	@RequestMapping(value = "/twilio/update", method = RequestMethod.POST)
	public String updateTwilioCredentials(@Valid @ModelAttribute TwilioForm twilioForm, BindingResult rBindingResult,
			Model model, Authentication authentication, HttpSession httpSession) {
		String userEmail = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userEmail);

		TwilioExtension twilioExtension = twilioService.getTwilioExtensionByUser(user);


		if(twilioExtension==null){
			logger.info("twilio extension null");
			httpSession.setAttribute("message",Message.builder()
							.content("Something went wrong..Try to send a message using twilio then try updating")
							.type(MessageType.red)
					.build());
			return "redirect:/user/settings";
		}

		if (rBindingResult.hasErrors()) {
			logger.warn("errors in updating twilio credentials");
			return "user/extension_settings";
		}


		twilioExtension.setId(twilioExtension.getId());
		twilioExtension.setUser(twilioExtension.getUser());
		twilioExtension.setSecretKey(twilioExtension.getSecretKey());
		twilioExtension.setInitVector(twilioExtension.getInitVector());
		twilioExtension.setTwilio_accountSid(twilioForm.getTwilio_accountSid());
		twilioExtension.setTwilio_authToken(twilioForm.getTwilio_authToken());

		var updatedTwilioExtension = twilioService.updateTwilioCredentials(twilioExtension);
		logger.info("updated twilio credentials", updatedTwilioExtension.toString());

		model.addAttribute("twilioForm", updatedTwilioExtension);

		model.addAttribute("message",
				Message.builder().content("wilio Credentials updated successfully").type(MessageType.green).build());

		httpSession.setAttribute("message",
				Message.builder().content("Twilio Credentials updated successfully").type(MessageType.green).build());

		return "redirect:/user/settings";
	}

//	@RequestMapping("/twiliopage")
//	public String twilioView(Model model, Authentication authentication) {
//		model.addAttribute("twilioForm",new TwilioForm());
//		return "user/twilio";
//	}

//****************************************************** 	 Mailstrap extension 	******************************************************//

	@RequestMapping("/mailstrap")
	public String mailStrapView(@RequestParam("id") String contactIds, Model model, Authentication authentication,
			HttpSession httpSession) {
		String userEmail = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userEmail);

		// checking whether the user is installed the extension or not
		MarketPlace marketPlace = marketPlaceService.getByUserAndAppcode("mailstrap", user).orElse(null);
		if (marketPlace == null) {
			httpSession.setAttribute("message",
					Message.builder().content("Mailstrap not Installed").type(MessageType.red).build());
			return "redirect:/user/contacts/marketplace";
		}

		MailStrapExtension mailStrapExtension = mailstrapService.getMailStrapExtensionByUser(user);
		if (mailStrapExtension == null) {
			logger.info("mailstrap extension {}", mailStrapExtension);
			model.addAttribute("mailstrapForm", new MailstrapForm());
			return "redirect:/user/contacts/send/mailstrap/add";
		}
		logger.info("sending twilio message view & contactIds = {}", contactIds);

		List<String> contactsIdList = Arrays.asList(contactIds.split(","));
		List<String> recipientsList = contactService.getEmailsList(user, contactsIdList);
		logger.info("emails List {}", recipientsList.toString());
		model.addAttribute("recipientsList", recipientsList);

		return "user/mailstrap_view";
	}

	@RequestMapping(value = "/mailstrap/add", method = RequestMethod.POST)
	public String addMailstrapCredentials(@ModelAttribute MailstrapForm mailstrapForm, BindingResult rBindingResult,
			Model model, Authentication authentication, HttpSession httpSession) {

		if (rBindingResult.hasErrors()) {
			model.addAttribute("mailstrapForm", mailstrapForm);
			return "redirect:user/contacts/send/mailstrap/add";
		}

		String userEmail = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userEmail);

		MailStrapExtension mailStrapExtension = new MailStrapExtension();
		mailStrapExtension.setMailstrapUsername(mailstrapForm.getMailstrapUsername());
		mailStrapExtension.setMailstrapPassword(mailstrapForm.getMailstrapPassword());
		mailStrapExtension.setUser(user);

		// checking whether the user is installed the extension or not
		MarketPlace marketPlace = marketPlaceService.getByUserAndAppcode("mailstrap", user).orElse(null);
		if (marketPlace == null) {
			httpSession.setAttribute("message",
					Message.builder().content("Mailstrap not Installed").type(MessageType.red).build());
			return "redirect:/user/contacts/marketplace";
		}

		// verifying mailstrap account valid or not before saving in db
		boolean verifyMailstrapCredentials = mailstrapService
				.verifymailstrapCredentials(mailstrapForm.getMailstrapUsername(), mailstrapForm.getMailstrapPassword());
		logger.info("check mailtrap credentials {}", verifyMailstrapCredentials);
		if (!verifyMailstrapCredentials) {
			httpSession.setAttribute("message",
					Message.builder().content("Invalid Credentials").type(MessageType.red).build());
			model.addAttribute("mailstrapForm", new MailstrapForm());
			return "redirect:/user/contacts/send/mailstrap/add";
		}
		mailstrapService.saveMailstrapCredentials(mailStrapExtension);
		model.addAttribute("mailstrapForm", mailstrapForm);
		return "redirect:/user/contacts";
	}

	@RequestMapping("/mailstrap/add")
	public String mailstrap_loginView(Model model) {
		model.addAttribute("mailstrapForm", new MailstrapForm());
		return "user/mailstrap_login";
	}

	@RequestMapping("mailstrap/update")
	public String updateMailstrapCredentialsView(Model model, Authentication authentication) {
		String userEmail = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userEmail);
		MailStrapExtension mailStrapExtension = mailstrapService.getMailStrapExtensionByUser(user);
		logger.info("mailstrap extension in update view {}", mailStrapExtension.toString());
		MailstrapForm mailstrapForm = new MailstrapForm();
		if (mailStrapExtension != null) {
			String secretKey = mailStrapExtension.getSecretKey();
			String initVector = mailStrapExtension.getInitVector();

			mailstrapForm.setMailstrapUsername(mailStrapExtension.getMailstrapUsername());
			mailstrapForm.setMailstrapPassword(mailStrapExtension.getMailstrapPassword());
		}
		model.addAttribute("mailstrapForm", mailstrapForm);
		return "user/settings";

	}

	@RequestMapping(value = "mailstrap/update", method = RequestMethod.POST)
	public String updateMailstrapCredentials(@Valid @ModelAttribute MailstrapForm mailstrapForm,
			BindingResult rBindingResult, Model model, Authentication authentication, HttpSession httpSession) {

		String userEmail = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userEmail);

		MailStrapExtension mailStrapExtension = mailstrapService.getMailStrapExtensionByUser(user);


		if(mailStrapExtension==null){
			logger.info("mailStrapExtension extension null");
			httpSession.setAttribute("message",Message.builder()
					.content("Something went wrong..Try to send a mail using mailtrap then try updating")
					.type(MessageType.red)
					.build());
			return "redirect:/user/settings";
		}

		if (rBindingResult.hasErrors()) {
			logger.warn("errors in updating mailstrap credentials");
			return "redirect:/user/settings";
		}

		mailStrapExtension.setMailstrapUsername(mailstrapForm.getMailstrapUsername());
		mailStrapExtension.setMailstrapPassword(mailstrapForm.getMailstrapPassword());
		mailStrapExtension.setUser(user);

		mailstrapService.updateMailstrapCredentials(mailStrapExtension);

		model.addAttribute("message", Message.builder().content("Mailstrap Credentials updated successfully")
				.type(MessageType.green).build());

		httpSession.setAttribute("message", Message.builder().content("Mailstrap Credentials updated successfully")
				.type(MessageType.green).build());

		return "redirect:/user/settings";
	}

//	sending mails
	@RequestMapping(value = "/mailstrap/mail", method = RequestMethod.POST)
	@ResponseBody
	public boolean sendMailsByMailstrap(@RequestBody MailstrapRequestBody mailstrapRequestBody,
			Authentication authentication) {
		String userEmail = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userEmail);
		boolean status = mailstrapService.sendMail(mailstrapRequestBody.getFromEmail(), mailstrapRequestBody.getRecipients(),
				mailstrapRequestBody.getSubject(), mailstrapRequestBody.getBody(), user);
		return status;
	}

}
