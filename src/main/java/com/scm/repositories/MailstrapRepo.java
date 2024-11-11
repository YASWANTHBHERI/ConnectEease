package com.scm.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scm.entities.MailStrapExtension;
import com.scm.entities.User;

@Repository
public interface MailstrapRepo extends JpaRepository<MailStrapExtension, String> {
	Optional<MailStrapExtension>findByUser(User user);
}
