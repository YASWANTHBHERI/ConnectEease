package com.scm.forms;

import org.springframework.web.multipart.MultipartFile;

import com.scm.validators.ValidFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContactForm {

	@NotBlank(message = "Name is required")
	@Size(min = 3, message = "Minimum 3 characters are required")
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email")
	private String email;

	@NotBlank(message = "Phone Number is required")
	@Pattern(regexp = "^\\+[0-9]{12,15}$", message = "Invalid Phone Number, Phone Number must include country code and 10 digits")
	private String phoneNumber;

	@NotBlank(message = "Address is required")
	private String address;

	private String description;

	private boolean favourite;

	private String websiteLink;

	private String linkedInLink;
	
	@ValidFile
	private MultipartFile contactImage;
	
	private String picture;

}
