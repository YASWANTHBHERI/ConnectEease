package com.scm.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.Contact;
import com.scm.entities.MailStrapExtension;
import com.scm.entities.TwilioExtension;
import com.scm.entities.User;
import com.scm.forms.ContactSearchForm;
import com.scm.forms.MailstrapForm;
import com.scm.forms.TwilioForm;
import com.scm.forms.UserForm;
import com.scm.forms.UserUpdateForm;
import com.scm.helpers.AESEncryptionDecryptionHelper;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.MailstrapService;
import com.scm.services.TwilioService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private ContactService contactService;

	@Autowired
	private MailstrapService mailstrapService;

	@Autowired
	private TwilioService twilioService;
	
	@Autowired
	private ImageService imageService;

	private Logger logger = LoggerFactory.getLogger(UserController.class);

//	user profile page
	@RequestMapping(value = "/profile")
	public String userProfile() {

		return "user/profile";
	}

	@RequestMapping("/dashboard")
	public String viewDashBoard(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
			@RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
			@RequestParam(value = "direction", defaultValue = "asc") String direction,

			Model model, Authentication authentication) {
		String userName = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userName);

		Page<Contact> pageContact = contactService.getByUser(user, page, size, sortBy, direction);
		model.addAttribute("pageContact", pageContact);
		model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

		model.addAttribute("contactSearchForm", new ContactSearchForm());

		return "user/dashboard";
	}

//	twilio update view
	@RequestMapping("/settings")
	public String extensionSettingsView(Model model, Authentication authentication) {
		String userEmail = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userEmail);

		// Getting Twilio data
		TwilioExtension twilioExtension = twilioService.getTwilioExtensionByUser(user);
		TwilioForm twilioForm = new TwilioForm();
		try {
			if (twilioExtension != null) {
				String secretKey = twilioExtension.getSecretKey();
				String initVector = twilioExtension.getInitVector();
				String twilio_accountSid = AESEncryptionDecryptionHelper
						.decryptData(twilioExtension.getTwilio_accountSid(), secretKey, initVector);
				String twilio_authToken = AESEncryptionDecryptionHelper
						.decryptData(twilioExtension.getTwilio_authToken(), secretKey, initVector);
				twilioForm.setTwilio_accountSid(twilio_accountSid);
				twilioForm.setTwilio_authToken(twilio_authToken);
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("twilioForm", twilioForm);
		}

		// Getting Mailstrap data
		MailStrapExtension mailStrapExtension = mailstrapService.getMailStrapExtensionByUser(user);
		MailstrapForm mailstrapForm = new MailstrapForm();

		try {
			if (mailStrapExtension != null) {
				String secretKey = mailStrapExtension.getSecretKey();
				String initVector = mailStrapExtension.getInitVector();

				String mailstrapUsername = AESEncryptionDecryptionHelper
						.decryptData(mailStrapExtension.getMailstrapUsername(), secretKey, initVector);

				String mailstrapPassword = AESEncryptionDecryptionHelper
						.decryptData(mailStrapExtension.getMailstrapPassword(), secretKey, initVector);

				mailstrapForm.setMailstrapUsername(mailstrapUsername);
				mailstrapForm.setMailstrapPassword(mailstrapPassword);
			}

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("mailstrapForm", mailstrapForm);
		}

		model.addAttribute("mailstrapForm", mailstrapForm);
		model.addAttribute("twilioForm", twilioForm);
		return "user/extension_settings";
	}

	@RequestMapping("/updateprofile")
	public String updateProfileView(Model model, Authentication authentication) {
		String userEmail = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userEmail);
		logger.info("user data {}", user);
		UserUpdateForm userUpdateForm = new UserUpdateForm();
		userUpdateForm.setName(user.getName());
		userUpdateForm.setEmail(user.getEmail());
		userUpdateForm.setPhoneNumber(user.getPhoneNumber());
		userUpdateForm.setAbout(user.getAbout());
		if (user.getProfilePic() != null) {
			userUpdateForm.setProfilePic(user.getProfilePic());
		}

		logger.info("userForm: {}", userUpdateForm);
		model.addAttribute("userForm", userUpdateForm);
		return "user/update_user_view";
	}

	@RequestMapping(value = "/updateprofile", method = RequestMethod.POST)
	public String updateProfile(@Valid @ModelAttribute("userForm") UserUpdateForm userUpdateForm, BindingResult rBindingResult, Model model,
			Authentication authentication,HttpSession httpSession) {
		
		logger.info("user updating postmethod called");
		if(rBindingResult.hasErrors()) {
			logger.info("errors in updating user {}",rBindingResult.getAllErrors());
			return "user/update_user_view";
		}
		
		String userEmail = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userEmail);
		
		user.setUserId(user.getUserId());
		user.setName(userUpdateForm.getName());
		user.setAbout(userUpdateForm.getAbout());
		user.setEmail(userUpdateForm.getEmail());
		user.setPhoneNumber(userUpdateForm.getPhoneNumber());
		if(userUpdateForm.getUserProfileImage()!=null && !userUpdateForm.getUserProfileImage().isEmpty()) {
			String fileName = UUID.randomUUID().toString();
			String imageUrl = imageService.uploadingImage(userUpdateForm.getUserProfileImage(), fileName);
			user.setProfilePic(imageUrl);
		}
		else {
			logger.info("user profile image empty");
		}
		User updatedUser = userService.updateUser(user).orElse(null);
		logger.info("user updated {}",updatedUser);
		
		if(updatedUser!=null) {
			model.addAttribute("message",
					Message.builder().content("User Updated ").type(MessageType.green).build());
			httpSession.setAttribute("message",
					Message.builder().content("User Updated successfully").type(MessageType.green).build());
			
			return "redirect:/user/updateprofile";

		}else {
			httpSession.setAttribute("message",
					Message.builder().content("User Updated Failed.. Try again").type(MessageType.red).build());
			
			return "redirect:/user/updateprofile";
		}
		
		
		
		
		

	}

}
