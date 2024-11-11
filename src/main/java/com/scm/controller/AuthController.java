package com.scm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.User;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/verify-email")
	public String verifyEmail(@RequestParam String token, HttpSession httpSession) {
		
		User user = userService.verifyEmail(token);
		if(user!=null) {
			httpSession.setAttribute("message", Message.builder()
					.content("Email Verified Successfully")
					.type(MessageType.green)
					.build());
			return "success_page";
		}
		
		httpSession.setAttribute("message", Message.builder()
				.content("Something went wrong Please verify you email")
				.type(MessageType.red)
				.build());
		
		return "error_page";
	}

}
