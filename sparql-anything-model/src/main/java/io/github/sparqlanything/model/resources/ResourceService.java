package io.github.sparqlanything.model.resources;

import io.github.sparqlanything.model.TriplifierHTTPException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;

public interface ResourceService {
	InputStream getInputStream(Properties properties) throws IOException, TriplifierHTTPException;
}
