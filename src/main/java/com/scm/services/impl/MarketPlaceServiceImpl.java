package com.scm.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scm.entities.MarketPlace;
import com.scm.entities.User;
import com.scm.repositories.MarketPlaceRepo;
import com.scm.services.MarketPlaceService;

@Service
public class MarketPlaceServiceImpl implements MarketPlaceService {

	@Autowired
	private MarketPlaceRepo marketPlaceRepo;

	private Logger logger = LoggerFactory.getLogger(MarketPlaceServiceImpl.class);

	@Override
	public MarketPlace save(MarketPlace marketPlace) {

		MarketPlace newMarketPlace = marketPlaceRepo.findByAppNameAndUser(marketPlace.getAppName(),marketPlace.getUser()).orElse(null);
		if (newMarketPlace != null) {
			logger.info("already exists {}", newMarketPlace.getAppName());
			return newMarketPlace;
		}

		String marketPlaceId = UUID.randomUUID().toString();

		newMarketPlace = new MarketPlace();
		newMarketPlace.setId(marketPlaceId);
		newMarketPlace.setAppName(marketPlace.getAppName());
		newMarketPlace.setAppType(marketPlace.getAppType());
		newMarketPlace.setInstalled(marketPlace.isInstalled());
		newMarketPlace.setProduct(marketPlace.getProduct());
		newMarketPlace.setUser(marketPlace.getUser());

		return marketPlaceRepo.save(newMarketPlace);
	}

	@Override
	public Optional<MarketPlace> getByUserAndAppcode(String appcode, User user) {
		return marketPlaceRepo.findByAppNameAndUser(appcode, user);
	}

	@Override
	public List<MarketPlace> getByUser(User user) {

		return marketPlaceRepo.findByUser(user);
	}

	@Override
	public List<String> getInstalledMarketPlaceAppsByUser(User user) {
		List<MarketPlace> marketPlaceAppsObjList = marketPlaceRepo.findByUser(user);
		List<String>installedMarketPlaceApp = new ArrayList<>();
		if(marketPlaceAppsObjList.size()>0) {
			for (MarketPlace app : marketPlaceAppsObjList) {
				installedMarketPlaceApp.add(app.getAppName());
			}
		}
		return installedMarketPlaceApp;
	}

}
