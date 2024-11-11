package com.scm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PageController {
	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String index() {
		return "redirect:/home";
	}

	@GetMapping("/home")
	public String homePage(Model model) {
		return "home";
	}

//	@GetMapping("/about")
//	public String aboutPage() {
//		return "about";
//	}

	@GetMapping("/services")
	public String servicesPage() {
		return "services";
	}

	@GetMapping("/contact")
	public String contactPage() {
		return "contact";
	}

	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	@GetMapping("/register")
	public String registerPage(Model model) {
		UserForm userForm = new UserForm();
//		userForm.setName("yash");
		model.addAttribute("userForm", userForm);

//		System.out.print(userForm);
		return "register";
	}

	@RequestMapping(value = "/do-register", method = RequestMethod.POST)
	public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult,
			HttpSession session) {

//		validations
		if (rBindingResult.hasErrors()) {
			System.out.println("validation error returning register page");
			System.out.println(rBindingResult);
			return "register";
		}

//		User user = User.builder()
//				.name(userForm.getName())
//				.email(userForm.getEmail())
//				.password(userForm.getPassword())
//				.about(userForm.getPassword())
//				.phoneNumber(userForm.getPhoneNumber())
//				.profilePic("https://www.google.com/search?q=default+profile+picture&rlz=1C1CHBF_enIN1058IN1058&oq=default+profile+picture&gs_lcrp=EgZjaHJvbWUyDAgAEEUYORixAxiABDIHCAEQABiABDIHCAIQABiABDIHCAMQABiABDIHCAQQABiABDIHCAUQABiABDIHCAYQABiABDIHCAcQABiABDIHCAgQABiABDIHCAkQABiABNIBCDQ2MDJqMGo3qAIAsAIA&sourceid=chrome&ie=UTF-8")
//				.build();

		User user = new User();

//		checking if the user is already exists or not
		String email = userForm.getEmail();
		if (userService.isUserExistByEmail(email)) {
			Message message = Message.builder().content("Email already exists").type(MessageType.red).build();
			session.setAttribute("message", message);
			return "redirect:/register";
		}

		user.setName(userForm.getName());
		user.setEmail(userForm.getEmail());
		user.setAbout(userForm.getAbout());
		user.setPhoneNumber(userForm.getPhoneNumber());
		user.setPassword(userForm.getPassword());
		user.setEnabled(false);
		user.setProfilePic(
				AppConstants.DEFAULT_IMAGE_URL);

		
//		saving user
		User saveUser = userService.saveUser(user);
		System.out.println("user saved" + saveUser);

//		using http session for displaying signup message
		Message message = Message.builder().content("Registration Successfull").type(MessageType.green).build();
		session.setAttribute("message", message);
		return "redirect:/register";
	}
	
}
