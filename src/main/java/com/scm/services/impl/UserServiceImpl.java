package com.scm.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Helper;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.repositories.UserRepo;
import com.scm.services.EmailService;
import com.scm.services.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private EmailService emailService;

//	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public User saveUser(User user) {
		String userId = UUID.randomUUID().toString();
		user.setUserId(userId);
		// using password encoder to encode password
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRoleList(List.of(AppConstants.ROLE_USER));
		
		String emailToken = UUID.randomUUID().toString();
		user.setEmailVerificationToken(emailToken);
		User savedUser = userRepo.save(user);
		
		String emailVerificationLink = Helper.getEmailVerificationLink(emailToken);
		
		emailService.sendEmail(savedUser.getEmail(), "Verify your Account : Connect-Ease",emailVerificationLink);
		
		return savedUser;
	}

	@Override
	public Optional<User> getUserById(String id) {
		return userRepo.findById(id);
	}

	@Override
	public Optional<User> updateUser(User user) {
		User user2 = userRepo.findById(user.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("user not found"));
		user2.setName(user.getName());
		user2.setEmail(user.getEmail());
		user2.setAbout(user.getAbout());
		user2.setPhoneNumber(user.getPhoneNumber());
		user2.setUserId(user.getUserId());
		if(user.getProfilePic()!=null) {
			user2.setProfilePic(user.getProfilePic());
		}
		
		
//		save the user in db
		User save = userRepo.save(user2);
		return Optional.ofNullable(save);
	}

	@Override
	public void deleteUser(String id) {
		User user2 = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("user not found"));
		userRepo.delete(user2);

	}

	@Override
	public boolean isUserExist(String userId) {
		User user2 = userRepo.findById(userId).orElse(null);
		return user2 != null ? true : false;
	}

	@Override
	public boolean isUserExistByEmail(String email) {
		User user2 = userRepo.findByEmail(email).orElse(null);
		return user2 != null ? true : false;
	}

	@Override
	public List<User> getAllUsers() {

		return userRepo.findAll();
	}

	@Override
	public User getUserByEmail(String email) {
		return userRepo.findByEmail(email).orElse(null);
	}

	@Override
	public User verifyEmail(String verificationToken) {
		
		User user = userRepo.findByEmailVerificationToken(verificationToken).orElse(null);
		if(user!=null) {
			user.setEmailVerified(true);
			user.setEnabled(true);
			return userRepo.save(user);
		}
		
		return null;
	}

}
