package com.scm.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.MarketPlace;
import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Helper;
import com.scm.services.MarketPlaceService;
import com.scm.services.UserService;

@Controller
@RequestMapping("/user/contacts")
public class MarketPlaceController {

	@Autowired
	private UserService userService;

	@Autowired
	private MarketPlaceService marketPlaceService;

	Logger logger = LoggerFactory.getLogger(MarketPlaceController.class);

	@RequestMapping("/marketplace")
	public String marketPlaceView(Authentication authentication, Model model) {

		String userEmail = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userEmail);
		List<String>installedApps = marketPlaceService.getInstalledMarketPlaceAppsByUser(user);

		logger.info("number of apps {}", installedApps.size());

		model.addAttribute("installedApps", installedApps);

		return "user/marketplace";
	}

	@RequestMapping("/marketplace/install")
	public String installMarketPlaceApps(@RequestParam String appcode, Authentication authentication, Model model) {
		logger.info("Marketplace appcode {}", appcode);

		String userEmail = Helper.getEmailOfLoggedInUser(authentication);
		User user = userService.getUserByEmail(userEmail);

		MarketPlace marketPlace = new MarketPlace();
		marketPlace.setAppName(appcode);
		marketPlace.setAppType(AppConstants.DEFAULT_MARKETPLACE_APP_TYPE);
		marketPlace.setInstalled(true);
		marketPlace.setProduct(appcode);
		marketPlace.setUser(user);

		marketPlaceService.save(marketPlace);
		return "user/marketplace";
	}
	
	
}
