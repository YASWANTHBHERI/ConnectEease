package com.scm.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scm.entities.Contact;
import com.scm.entities.User;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {
//	custom finder method
	Page<Contact> findByUser(User user, Pageable pageable);
	
// custom query method
	@Query("SELECT c FROM Contact c WHERE c.user.id=:userId")
	List<Contact>findByUserId(@Param("userId") String userId);
	
	Page<Contact> findByUserAndNameContaining(User user, String nameKeyword, Pageable pageable);
	Page<Contact> findByUserAndEmailContaining(User user,String emailKeyword, Pageable pageable);
	Page<Contact> findByUserAndPhoneNumberContaining(User user,String phoneNumberKeyword, Pageable pageable);
	
	@Query("SELECT c.phoneNumber FROM Contact c WHERE c.user.id=:userId AND c.id IN :contactIds")
	List<String>findphoneNumbersByUserAndIds(@Param("userId")String userId,@Param("contactIds") List<String>contactIds);
	
	
	@Query("SELECT c.email FROM Contact c WHERE c.user.id=:userId AND c.id IN :contactIds")
	List<String>findemailByUserAndIds(@Param("userId")String userId, @Param("contactIds") List<String>contactIds);
}
