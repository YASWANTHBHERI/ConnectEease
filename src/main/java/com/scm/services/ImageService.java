package com.scm.services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

	String uploadingImage(MultipartFile contactImage, String fileName);
	
	String getURLFromPublicId(String publicId);
	
	String uploadImageFromUrl(String imageUrl, String fileName);
	
	

}
