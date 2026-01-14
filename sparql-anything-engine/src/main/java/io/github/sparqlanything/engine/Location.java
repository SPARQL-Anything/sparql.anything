/*
 * Copyright (c) 2026 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package io.github.sparqlanything.engine;

import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class Location {
	private static final Logger log = LoggerFactory.getLogger(Location.class);

	public static boolean isHTTP(URL url) {
		return (url.getProtocol().equalsIgnoreCase("http") || url.getProtocol().equalsIgnoreCase("https"));
	}

	public static boolean isFile(URL url) {
		return url.getProtocol().equalsIgnoreCase("file");
	}

	public static InputStream getInputStreamFromS3Bucket(URL url, Properties properties) {

		AwsBasicCredentials credentials = AwsBasicCredentials.create(PropertyUtils.getStringProperty(properties, IRIArgument.S3_ACCESS_KEY), PropertyUtils.getStringProperty(properties, IRIArgument.S3_SECRET_KEY));
		Region region = Region.of(PropertyUtils.getStringProperty(properties, IRIArgument.S3_REGION));

		try (S3Client s3 = S3Client.builder()
			.endpointOverride(url.toURI())
			.credentialsProvider(StaticCredentialsProvider.create(credentials))
			.region(region)
			.serviceConfiguration(
				S3Configuration.builder()

					.pathStyleAccessEnabled(true)
					.build())
			.build()) {

			GetObjectRequest request = GetObjectRequest.builder()
				.bucket(PropertyUtils.getStringProperty(properties, IRIArgument.S3_BUCKET_NAME))
				.key(PropertyUtils.getStringProperty(properties, IRIArgument.S3_KEY))
				.build();


			return s3.getObject(request);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

	}


}
