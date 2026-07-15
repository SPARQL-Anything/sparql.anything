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

import java.io.FilterInputStream;
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
		AwsBasicCredentials credentials = AwsBasicCredentials.create(
			PropertyUtils.getStringProperty(properties, IRIArgument.S3_ACCESS_KEY),
			PropertyUtils.getStringProperty(properties, IRIArgument.S3_SECRET_KEY));
		Region region = Region.of(PropertyUtils.getStringProperty(properties, IRIArgument.S3_REGION));

		S3Client s3;
		try {
			s3 = S3Client.builder()
				.endpointOverride(new URI(PropertyUtils.getStringProperty(properties, IRIArgument.S3_ENDPOINT)))
				.credentialsProvider(StaticCredentialsProvider.create(credentials))
				.region(region)
				.serviceConfiguration(
					S3Configuration.builder()
						.pathStyleAccessEnabled(true)
						.build())
				.build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		// The client is NOT closed here: it must stay open until the caller
		// has finished reading the stream. If something goes wrong after the
		// client is created, it must be closed explicitly to avoid a leak.
		try {
			GetObjectRequest request = GetObjectRequest.builder()
				.bucket(PropertyUtils.getStringProperty(properties, IRIArgument.S3_BUCKET_NAME))
				.key(PropertyUtils.getStringProperty(properties, IRIArgument.S3_KEY))
				.build();

			InputStream objectStream = s3.getObject(request);

			// Wrap the stream so that closing it also closes the underlying
			// S3 client. This lets the caller manage the lifecycle with a
			// plain try-with-resources on the InputStream, without needing
			// to know that an S3Client even exists.
			return new S3ClientClosingInputStream(objectStream, s3);
		} catch (RuntimeException e) {
			// If getObject() fails, close the client here because nobody
			// else will (we never handed it off to the caller).
			s3.close();
			throw e;
		}
	}

	/**
	 * InputStream that also closes the associated S3Client on close().
	 * Ensures the HTTP connection stays alive for the whole read, and is
	 * released correctly once the caller is done with it.
	 */
	static final class S3ClientClosingInputStream extends FilterInputStream {

		private final S3Client s3Client;
		private volatile boolean closed = false;

		S3ClientClosingInputStream(InputStream in, S3Client s3Client) {
			super(in);
			this.s3Client = s3Client;
		}

		@Override
		public void close() throws IOException {
			if (closed) {
				return;
			}
			closed = true;
			try {
				super.close();
			} finally {
				try {
					s3Client.close();
				} catch (RuntimeException e) {
					log.warn("Error closing S3Client after stream close", e);
				}
			}
		}
	}
}
