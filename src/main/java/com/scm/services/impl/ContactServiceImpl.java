package com.scm.services.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.repositories.ContactRepo;
import com.scm.services.ContactService;

@Service
public class ContactServiceImpl implements ContactService {

	@Autowired
	private ContactRepo contactRepo;

	private Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

	@Override
	public Contact save(Contact contact) {
		String contactId = UUID.randomUUID().toString();
		contact.setId(contactId);
		return contactRepo.save(contact);
	}

	@Override
	public Contact updateContact(Contact contact) {

		var contactOld = contactRepo.findById(contact.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Contact Not Found"));
		
		contactOld.setName(contact.getName());
		contactOld.setEmail(contact.getEmail());
		contactOld.setPhoneNumber(contact.getPhoneNumber());
		contactOld.setDescription(contact.getDescription());
		contactOld.setAddress(contact.getAddress());
		contactOld.setFavourite(contact.isFavourite());
		contactOld.setLinkedInLink(contact.getLinkedInLink());
		contactOld.setPicture(contact.getPicture());
		contactOld.setWebsiteLink(contact.getWebsiteLink());
		contactOld.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());
		logger.info("contact updated successfully");
		return contactRepo.save(contactOld);
	}

	@Override
	public List<Contact> getAll() {
		return contactRepo.findAll();
	}

	@Override
	public Contact getById(String id) {
		return contactRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact Not Found"));
	}

	@Override
	public void delete(String id) {
		var contact = contactRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact Not Found"));
		contactRepo.delete(contact);

	}

	@Override
	public List<Contact> getByUserId(String userId) {
		return contactRepo.findByUserId(userId);
	}

	@Override
	public Page<Contact> getByUser(User user, int page, int size, String sortBy, String direction) {

		Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		var pageable = PageRequest.of(page, size, sort);

		return contactRepo.findByUser(user, pageable);
	}

	@Override
	public Page<Contact> searchByName(User user, String nameKeyword, int size, int page, String sortBy, String order) {
		Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		var pageable = PageRequest.of(page, size, sort);
		return contactRepo.findByUserAndNameContaining(user, nameKeyword, pageable);
	}

	@Override
	public Page<Contact> searchByEmail(User user, String emailKeyword, int size, int page, String sortBy,
			String order) {
		Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		var pageable = PageRequest.of(page, size, sort);
		return contactRepo.findByUserAndEmailContaining(user, emailKeyword, pageable);
	}

	@Override
	public Page<Contact> searchByPhoneNumber(User user, String phoneKeyword, int size, int page, String sortBy,
			String order) {
		Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		var pageable = PageRequest.of(page, size, sort);
		return contactRepo.findByUserAndPhoneNumberContaining(user, phoneKeyword, pageable);
	}

	@Override
	public List<String> getRecipientsList(User user, List<String> contactIds) {
		String userId = user.getUserId();
		List<String>recipientsList = contactRepo.findphoneNumbersByUserAndIds(userId, contactIds);
		return recipientsList;
	}
	
	@Override
	public List<String> getEmailsList(User user, List<String> contactIds) {
		String userId = user.getUserId();
		List<String>recipientsList = contactRepo.findemailByUserAndIds(userId, contactIds);
		return recipientsList;
	}


	
}
