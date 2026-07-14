package io.github.sparqlanything.model.resources;

import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.resources.annotations.TargetOption;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

@TargetOption(IRIArgument.CONTENT_NAME)
public class ContentInputService implements ResourceService{
	@Override
	public InputStream getInputStream(Properties properties) {
		return new ByteArrayInputStream(properties.get(IRIArgument.CONTENT.toString()).toString().getBytes());
	}
}
