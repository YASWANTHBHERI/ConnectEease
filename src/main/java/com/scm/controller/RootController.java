package com.scm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.scm.entities.User;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class RootController {
	
	@Autowired
	private UserService userService;
	
	private Logger logger = LoggerFactory.getLogger(RootController.class);
	
	@ModelAttribute
	public void addLoggedInUserInformation(Model model, Authentication authentication) {
		
		if(authentication==null) {
			return;
		}
		
		logger.info("Adding logged in user info to the model");
		String email = Helper.getEmailOfLoggedInUser(authentication);
		logger.info("User logged in: " + email);
		
		User user = userService.getUserByEmail(email);
		logger.info(user.getName());
		logger.info(user.getEmail());
		logger.info("profile pic {}",user.getProfilePic());
		model.addAttribute("loggedInUser", user);
	}
	
	
	@ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(HttpSession httpSession) {
		logger.info("maximum file exceed exception controller called");
		httpSession.setAttribute("message", Message.builder()
				.content("Please upload a small size image, image size must be less than 2MB")
				.type(MessageType.red)
				.build());
        return "redirect:/user/updateprofile";
    }

	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNonExisitingPages(NoHandlerFoundException ex){
		return "404page";
	}
	
	
}
