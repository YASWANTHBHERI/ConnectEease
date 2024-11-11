package com.scm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scm.entities.User;
import java.util.Optional;


@Repository
public interface UserRepo extends JpaRepository<User, String> {

	Optional<User>findByEmail(String email);
	Optional<User>findByEmailAndPassword(String email, String password);
	Optional<User>findByEmailVerificationToken(String emailVerificationToken);
}
