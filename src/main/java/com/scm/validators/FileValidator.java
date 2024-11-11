package com.scm.validators;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

	private static final long MAX_FILE_SIZE = 1024 * 1024 * 2; // 2MB

	private static final Set<String> ALLOWED_FILE_TYPES = Set.of("image/png", "image/jpeg", "image/jpg");

	@Override
	public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

		if (file == null || file.isEmpty()) {
			System.out.print("checking for file in if");
			return true;
		} else {
			
			System.out.print("else in file"+file.getSize());
			if (file.getSize() > MAX_FILE_SIZE) {
				System.out.print("file size " + file.getSize() + "max file size " + MAX_FILE_SIZE + " ");
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("File size should be less than 2MB")
						.addConstraintViolation();
				return false;
			}

			if (!ALLOWED_FILE_TYPES.contains(file.getContentType())) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("Invalid file type, Please upload only PNG, JPEG, JPG")
						.addConstraintViolation();
				return false;
			}
		}

		return true;
	}

}
