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
import java.util.function.Function;

@TargetOption(IRIArgument.S3_ENDPOINT_NAME)
public class S3InputService implements ResourceService {

	private static final Logger log = LoggerFactory.getLogger(S3InputService.class);

	// How to build the S3Client from the given Properties. Extracted behind a
	// Function so that tests can inject a fake client without needing to mock
	// the static S3Client.builder() factory method (which requires bytecode
	// instrumentation and is fragile across JDK/Mockito version combinations).
	private final Function<Properties, S3Client> clientFactory;

	public S3InputService() {
		this(S3InputService::buildDefaultClient);
	}

	// Package-visible for testing: allows injecting a fake/mock S3Client
	// factory, so unit tests don't need to mock the static
	// S3Client.builder() factory method (which would require bytecode
	// instrumentation and is fragile across JDK/Mockito version
	// combinations). Public visibility is used only because the test class
	// lives in a separate io.github.sparqlanything.s3.test sub-package, not
	// because this is meant as a general-purpose public API.
	public S3InputService(Function<Properties, S3Client> clientFactory) {
		this.clientFactory = clientFactory;
	}

	private static S3Client buildDefaultClient(Properties properties) {
		String accessKey = requireProperty(properties, IRIArgument.S3_ACCESS_KEY);
		String secretKey = requireProperty(properties, IRIArgument.S3_SECRET_KEY);
		String regionName = requireProperty(properties, IRIArgument.S3_REGION);
		String endpoint = requireProperty(properties, IRIArgument.S3_ENDPOINT);

		AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
		Region region = Region.of(regionName);

		try {
			return S3Client.builder()
				.endpointOverride(new URI(endpoint))
				.credentialsProvider(StaticCredentialsProvider.create(credentials))
				.region(region)
				.serviceConfiguration(
					S3Configuration.builder()
						.pathStyleAccessEnabled(true)
						.build())
				.build();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(
				"Invalid value for '" + IRIArgument.S3_ENDPOINT + "': '" + endpoint + "' is not a valid URI.", e);
		}
	}

	/**
	 * Returns the given required {@code s3.*} property, or throws a clear,
	 * SPARQL-Anything-specific {@link IllegalArgumentException} naming the
	 * missing property if it is absent or blank. Without this check, a
	 * missing property surfaces later as an unclear, low-level AWS SDK
	 * exception (e.g. a {@code NullPointerException} from
	 * {@code AwsBasicCredentials.create} or an {@code IllegalArgumentException}
	 * from {@code Region.of} that doesn't name the offending property).
	 */
	private static String requireProperty(Properties properties, IRIArgument argument) {
		String value = PropertyUtils.getStringProperty(properties, argument, null);
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(
				"Missing required property '" + argument + "': it must be set when '"
					+ IRIArgument.S3_ENDPOINT + "' is used to read a resource from S3.");
		}
		return value;
	}

	@Override
	public InputStream getInputStream(Properties properties) throws IOException {
		S3Client s3 = clientFactory.apply(properties);

		// The client is NOT closed here: it must stay open until the caller
		// has finished reading the stream. If something goes wrong after the
		// client is created, it must be closed explicitly to avoid a leak.
		try {
			String bucketName = requireProperty(properties, IRIArgument.S3_BUCKET_NAME);
			String key = requireProperty(properties, IRIArgument.S3_KEY);

			GetObjectRequest request = GetObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
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
