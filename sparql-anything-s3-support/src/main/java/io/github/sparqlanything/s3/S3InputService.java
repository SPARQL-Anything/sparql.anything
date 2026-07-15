package io.github.sparqlanything.s3;

import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.PropertyUtils;
import io.github.sparqlanything.model.resources.ResourceService;
import io.github.sparqlanything.model.resources.annotations.TargetOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

@TargetOption(IRIArgument.S3_ENDPOINT_NAME)
public class S3InputService implements ResourceService {

	private static final Logger log = LoggerFactory.getLogger(S3InputService.class);

	@Override
	public InputStream getInputStream(Properties properties) throws IOException {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(PropertyUtils.getStringProperty(properties, IRIArgument.S3_ACCESS_KEY), PropertyUtils.getStringProperty(properties, IRIArgument.S3_SECRET_KEY));
		Region region = Region.of(PropertyUtils.getStringProperty(properties, IRIArgument.S3_REGION));

		try (S3Client s3 = S3Client.builder()
			.endpointOverride(new URI(PropertyUtils.getStringProperty(properties, IRIArgument.S3_ENDPOINT)))
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
