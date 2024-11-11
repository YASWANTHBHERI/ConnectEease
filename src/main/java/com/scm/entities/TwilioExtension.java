package com.scm.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TwilioExtension {

	@Id
	private String id;
	private String twilio_accountSid;
	private String twilio_authToken;
	private String secretKey;
	private String initVector;
	
	@OneToOne
	@JsonIgnore
	private User user;
}
