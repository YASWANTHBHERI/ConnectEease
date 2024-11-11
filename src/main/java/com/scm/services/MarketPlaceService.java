package com.scm.services;

import java.util.List;
import java.util.Optional;

import com.scm.entities.MarketPlace;
import com.scm.entities.User;

public interface MarketPlaceService {

	MarketPlace save(MarketPlace marketPlace);
	
	Optional<MarketPlace> getByUserAndAppcode(String appcode, User user);
	List<MarketPlace> getByUser(User user);
	List<String> getInstalledMarketPlaceAppsByUser(User user);
	
	
}
