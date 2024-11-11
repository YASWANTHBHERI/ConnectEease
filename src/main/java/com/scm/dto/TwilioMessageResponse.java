package com.scm.dto;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TwilioMessageResponse {
	private String accountSid;
	private String sid;
	private String status;
	private ZonedDateTime dateCreated;
	private String to;
	private String direction;
}
