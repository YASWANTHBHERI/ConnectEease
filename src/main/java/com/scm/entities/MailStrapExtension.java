package com.scm.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MailStrapExtension {

	@Id
	private String mailstrapId;
	
	@NotNull
	private String mailstrapUsername;
	@NotNull
	private String mailstrapPassword;
	
	private String secretKey;
	private String initVector;
	
	@OneToOne
	@JsonIgnore
	private User user;
}
