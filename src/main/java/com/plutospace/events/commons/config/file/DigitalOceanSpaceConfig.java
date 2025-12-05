/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.config.file;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.plutospace.events.commons.definitions.PropertyConstants;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DigitalOceanSpaceConfig {

	private final PropertyConstants propertyConstants;

	@Bean
	public AmazonS3 getAmazonS3Client() {
		final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(propertyConstants.getAccessKeyId(),
				propertyConstants.getAccessKeySecret());
		// Get Amazon S3 client and return the S3 client object
		return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
				// .withRegion(apis.getS3RegionName())
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(propertyConstants.getS3Endpoint(),
						propertyConstants.getS3RegionName()))
				.build();
	}
}
