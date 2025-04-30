package com.easy.task.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3Service {

	
	 @Value("${aws.bucketName}")
	 private String bucketName;
	 
	 @Value("${aws.bucketSuffix}")
	 private String bucketSuffix;
	 
	 @Value("${aws.region}")
	 private String region;
	 
	 @Value("${aws.cloudfront.domain}")
	 private String cloudFrontDomain;

	 @Autowired
	 private S3Client s3Client;
	    
	public String uploadUserAvatarToS3Bucket(MultipartFile file, String userId) throws S3Exception, AwsServiceException, SdkClientException, IOException {
			
		final String key = (bucketSuffix+userId+"/"+file.getOriginalFilename());
		
			PutObjectRequest objectRequest = PutObjectRequest.builder()
					.bucket(bucketName)
					.key(key)
					.contentType(file.getContentType())
					.build();
			
			s3Client.putObject(objectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
			
			 // Construct actual region-aware URL
			String objectUrl = cloudFrontDomain+key;
			
		return objectUrl;
	}

	
}
