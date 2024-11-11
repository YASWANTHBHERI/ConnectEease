package com.scm.forms;

import org.springframework.web.multipart.MultipartFile;

import com.scm.validators.ValidFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class UserUpdateForm {
	@NotBlank(message = "Name is Required")
	@Size(min = 3, message = "Min 3 characters are required")
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid Email")
	private String email;

	@Size(min = 6, message = "password length should be more than 6")
	private String password;

	@NotBlank(message = "About is required")
	private String about;

	@NotBlank(message = "Phone Number is required")
	@Size(min = 10, max = 10, message = "Phone Number must be 10 digits")
	private String phoneNumber;
	
	private String profilePic;
	
	@ValidFile
	private MultipartFile userProfileImage;
}
