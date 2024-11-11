package com.scm.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="marketplace")
@Table(name="marketplace")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketPlace {
	@Id
	private String id;
	private String appName;
	private String appType;
	private String product;
	private boolean isInstalled = false;
	
	@ManyToOne
	@JsonIgnore
	private User user;
	

}
