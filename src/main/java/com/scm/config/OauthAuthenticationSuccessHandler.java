package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.repositories.UserRepo;
import com.scm.services.ImageService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OauthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	Logger logger = LoggerFactory.getLogger(OauthAuthenticationSuccessHandler.class);

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ImageService imageService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		logger.info("OauthAuthenticationSuccessHandler method called");
		
		

//		Identify the provider

		var oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

		String authorizatedClientRegistrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
		logger.info(authorizatedClientRegistrationId);
		
		var oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
		oauthUser.getAttributes().forEach((key, value) -> {
			logger.info("{} => {}", key, value);
		});
		
		User user = new User();
		user.setUserId(UUID.randomUUID().toString());
		user.setRoleList(List.of(AppConstants.ROLE_USER));
		user.setEmailVerified(true);
		user.setEnabled(true);
		user.setPassword("dummy");
		

//		Google
		if (authorizatedClientRegistrationId.equalsIgnoreCase("google")) {
			user.setName(oauthUser.getAttribute("name").toString());
			user.setEmail(oauthUser.getAttribute("email").toString());
//			user.setProfilePic(oauthUser.getAttribute("picture").toString());
			String profilePicUrl = oauthUser.getAttribute("picture").toString();
			user.setProfilePic(handleGoogleProfilePic(profilePicUrl));
			user.setProviderId(oauthUser.getName());
			user.setProvider(Providers.GOOGLE);
			user.setAbout("Login through Google");
		}

//		Github
		else if (authorizatedClientRegistrationId.equalsIgnoreCase("github")) {
				String email = oauthUser.getAttribute("email")!=null?oauthUser.getAttribute("email").toString():
					(oauthUser.getAttribute("login").toString()+"@gmail.com").toLowerCase();
				String picture = oauthUser.getAttribute("avatar_url").toString();
				String name = oauthUser.getAttribute("login").toString();
				String providerId = oauthUser.getName();
				
				user.setEmail(email);
				user.setProfilePic(picture);
				user.setName(name);
				user.setProviderId(providerId);
				user.setProvider(Providers.GITHUB);
				user.setAbout("Login through Github");
		}
		
		else {
			logger.warn("unauthorized provider");
		}


//		logger.info(user.getName());
//
		
//
//		logger.info(user.getAuthorities().toString());
//		logger.info("username: " + user.getName());
//		logger.info("usernameString: " + user.getAttribute("name"));

//		Oauth2 configuration

//		String email = user.getAttribute("email").toString();
//		String name = user.getAttribute("name").toString();
//		String picture = user.getAttribute("picture").toString();
//
//		User user1 = new User();
//		user1.setEmail(email);
//		user1.setName(name);
//		user1.setProfilePic(picture);
//		user1.setPassword("password");
//		user1.setUserId(UUID.randomUUID().toString());
//		user1.setProvider(Providers.GOOGLE);
//		user1.setEnabled(true);
//		user1.setEmailVerified(true);
//		user1.setProviderId(user.getName());
//		user1.setRoleList(List.of(AppConstants.ROLE_USER));
//		user1.setAbout("This user is created by Google Authentication");
//
		User user2 = userRepo.findByEmail(user.getEmail()).orElse(null);
		if (user2 == null) {
			userRepo.save(user);
			logger.info("user saved: "+ user.getEmail());
		}

		new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");

	}
	
	private String handleGoogleProfilePic(String imageUrl) {
		String fileName = UUID.randomUUID().toString();
		String fileUrl = imageService.uploadImageFromUrl(imageUrl, fileName);
		return fileUrl;
		
	}

}
