package com.scm.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scm.entities.TwilioExtension;
import com.scm.entities.User;

@Repository
public interface TwilioRepo extends JpaRepository<TwilioExtension, String> {

	Optional<TwilioExtension> findByUser(User user);
}
