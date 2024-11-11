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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

	@Autowired
	private ContactService contactService;

	@Autowired
	private UserService userService;

	@Autowired
	private ImageService imageService;

	private Logger logger = LoggerFactory.getLogger(ContactController.class);

	@RequestMapping("/add")
	public String addContactView(Model model) {
		ContactForm contactForm = new ContactForm();
//		contactForm.setName("Yaswanth");
//		contactForm.setFavourite(true);
		model.addAttribute("contactForm", contactForm);
		return "user/add_contact";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String SaveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult rbindingResult,
			Authentication authentication, HttpSession httpSession) {
//		System.out.println("Printing contact Form...............");
//		System.out.println(contactForm);

		if (rbindingResult.hasErrors()) {
			rbindingResult.getAllErrors().forEach(error -> logger.info(error.toString()));
			httpSession.setAttribute("message",
					Message.builder().content("Please correct the following errors").type(MessageType.red).build());
			return "user/add_contact";
		}

		String userName = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userName);

		Contact contact = new Contact();
		contact.setName(contactForm.getName());
		contact.setAddress(contactForm.getAddress());
		contact.setDescription(contactForm.getDescription());
		contact.setEmail(contactForm.getEmail());
		contact.setFavourite(contactForm.isFavourite());
		contact.setLinkedInLink(contactForm.getLinkedInLink());
		contact.setWebsiteLink(contactForm.getWebsiteLink());
		contact.setPhoneNumber(contactForm.getPhoneNumber());
		contact.setUser(user);

//		Image Processing
		if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
			String fileName = UUID.randomUUID().toString();
			String fileURL = imageService.uploadingImage(contactForm.getContactImage(), fileName);
			contact.setPicture(fileURL);
			contact.setCloudinaryImagePublicId(fileName);
		}

//		saving contact in db

		contactService.save(contact);

		httpSession.setAttribute("message",
				Message.builder().content("Contact added successfully").type(MessageType.green).build());

		return "redirect:/user/contacts/add";
	}

	@RequestMapping
	public String viewContacts(@RequestParam(value = "page", defaultValue = "0") int page,
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
		return "user/contacts";
	}

//	search handler
	@RequestMapping("/search")
	public String searchHandler(@ModelAttribute ContactSearchForm contactSearchForm,
			@RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
			@RequestParam(value = "direction", defaultValue = "asc") String direction, Model model,
			Authentication authentication, BindingResult rbindingResult) {

		logger.info("field: {}, value: {}", contactSearchForm.getField(), contactSearchForm.getField());

		var user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));	

		Page<Contact> pageContact = null;

		if (contactSearchForm.getField().equalsIgnoreCase("name")) {
			logger.info("value {}, size {}, page {}, field {}, direction {}", contactSearchForm.getValue(), size, page,
					sortBy, direction);
			pageContact = contactService.searchByName(user, contactSearchForm.getValue(), size, page, sortBy,
					direction);
		} else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
			pageContact = contactService.searchByEmail(user, contactSearchForm.getValue(), size, page, sortBy,
					direction);
		} else if (contactSearchForm.getField().equalsIgnoreCase("phoneNumber")) {
			pageContact = contactService.searchByPhoneNumber(user, contactSearchForm.getValue(), size, page, sortBy,
					direction);
		}

		model.addAttribute("contactSearchForm", contactSearchForm);
		model.addAttribute("pageContact", pageContact);
		model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

		return "user/search";
	}

//	delete contact
	@RequestMapping("/delete/{contactId}")
	public String deleteContact(@PathVariable String contactId) {
		contactService.delete(contactId);
		return "redirect:/user/contacts";
	}

//	update contact view
	@RequestMapping("/view/{contactId}")
	public String updateContactFormView(@PathVariable String contactId, Model model) {

		var contact = contactService.getById(contactId);
		ContactForm contactForm = new ContactForm();

		contactForm.setAddress(contact.getAddress());
		contactForm.setDescription(contact.getDescription());
		contactForm.setEmail(contact.getEmail());
		contactForm.setFavourite(contact.isFavourite());
		contactForm.setLinkedInLink(contact.getLinkedInLink());
		contactForm.setName(contact.getName());
		contactForm.setWebsiteLink(contact.getWebsiteLink());
		contactForm.setPhoneNumber(contact.getPhoneNumber());
		contactForm.setPicture(contact.getPicture());

		model.addAttribute("contactForm", contactForm);
		model.addAttribute("contactId", contactId);

		return "user/update_contact_view";
	}

	@RequestMapping(value = "/update/{contactId}", method = RequestMethod.POST)
	public String updateContact(@PathVariable String contactId, @Valid @ModelAttribute ContactForm contactForm,
			BindingResult rbindingResult, Model model, HttpSession httpSession) {

		if (rbindingResult.hasErrors()) {
			return "user/update_contact_view";
		}

		var con = contactService.getById(contactId);
		con.setId(contactId);
		con.setAddress(contactForm.getAddress());
		con.setEmail(contactForm.getEmail());
		con.setDescription(contactForm.getDescription());
		con.setPhoneNumber(contactForm.getPhoneNumber());
		con.setName(contactForm.getName());
		con.setFavourite(contactForm.isFavourite());
		con.setLinkedInLink(contactForm.getLinkedInLink());
		con.setWebsiteLink(contactForm.getWebsiteLink());

//		process Image
		if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
			String fileName = UUID.randomUUID().toString();
			String imageUrl = imageService.uploadingImage(contactForm.getContactImage(), fileName);
			con.setCloudinaryImagePublicId(fileName);
			con.setPicture(imageUrl);
			contactForm.setPicture(imageUrl);
		} else {
			logger.info("file is empty");
		}

		var updatedContact = contactService.updateContact(con);
		logger.info("updated contact {}", updatedContact);

		model.addAttribute("message",
				Message.builder().content("Contact Updated " + contactId).type(MessageType.green).build());

		httpSession.setAttribute("message",
				Message.builder().content("Contact Updated successfully").type(MessageType.green).build());

		return "redirect:/user/contacts/view/" + contactId;
	}

}
