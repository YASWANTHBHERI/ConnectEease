package com.scm.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scm.entities.MarketPlace;
import com.scm.entities.User;


@Repository
public interface MarketPlaceRepo extends JpaRepository<MarketPlace, String> {
	
	Optional<MarketPlace>findByAppName(String appName);
	List<MarketPlace>findByUser(User user);
	Optional<MarketPlace>findByAppNameAndUser(String appName, User user);

}
