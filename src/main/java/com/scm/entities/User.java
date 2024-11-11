package com.scm.entities;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "user")
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

	@Id
	private String userId;
	@Column(name = "user_name", nullable = false)
	private String name;
	@Column(unique = true, nullable = false)
	private String email;

	@Getter(value = AccessLevel.NONE)
	private String password;

	@Column(length = 1000)
	private String about;
	@Column(length = 1000)
	private String profilePic;
	private String phoneNumber;

//	information
	@Getter(value = AccessLevel.NONE)
	private boolean enabled = false;

	private boolean emailVerified = false;
	private boolean phoneVerified = false;
	
	private String emailVerificationToken;

//	self,google,facebook,github twitter, linkedin,
	@Enumerated(value = EnumType.STRING)
	private Providers provider = Providers.SELF;
	private String providerId;

//  adding more fields
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<Contact> contacts = new ArrayList<>();
	
// MarketPlace Apps
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<MarketPlace> marketPlace = new ArrayList<>();
	
	@ElementCollection(fetch = FetchType.EAGER)
	List<String>roleList = new ArrayList<>();
	
	
//	twilio extension
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private TwilioExtension twilioExtension;
	
// mailstrap extension
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private MailStrapExtension mailStrapExtension;

	// unimplemented methods of UserDetails
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		//	List of roles [USER,ADMIN]
		//  collection of SimpleGrantedAuthority(roles[USER,ADMIN]) 
		Collection<SimpleGrantedAuthority> roles=roleList.stream().map(role-> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
		return roles;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

}
