package com.scm.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailstrapRequestBody {

	
	private String fromEmail;
	private String[] recipients;
	private String subject;
	private String body;
}
