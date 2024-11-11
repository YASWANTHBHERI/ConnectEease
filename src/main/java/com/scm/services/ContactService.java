package com.scm.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.scm.entities.Contact;
import com.scm.entities.User;

public interface ContactService {

	Contact save(Contact contact);

	Contact updateContact(Contact contact);

	List<Contact> getAll();

	Contact getById(String id);

	void delete(String id);

	Page<Contact> searchByName(User user, String nameKeyword, int size, int page, String sortBy, String order);

	Page<Contact> searchByEmail(User user, String emailKeyword,int size, int page, String sortBy, String order);

	Page<Contact> searchByPhoneNumber(User user,String phoneKeyword,  int size, int page, String sortBy, String order);

	List<Contact> getByUserId(String userId);

	Page<Contact> getByUser(User user, int page, int size, String sortField, String sortDirection);
	
	List<String>getRecipientsList(User user, List<String>contactIds);
	
	List<String>getEmailsList(User user, List<String>contactIds);
	

}
