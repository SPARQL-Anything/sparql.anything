/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sparqlanything.slides;

import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.SPARQLAnythingConstants;
import io.github.sparqlanything.model.Triplifier;
import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.commons.compress.utils.Sets;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class PptxTriplifier implements Triplifier {

//	private static final Logger logger = LoggerFactory.getLogger(PptxTriplifier.class);

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException, TriplifierHTTPException {
		URL url = Triplifier.getLocation(properties);
		if (url == null) return;
		String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID;

		builder.addRoot(dataSourceId);

		try (InputStream is = url.openStream(); XMLSlideShow slides = new XMLSlideShow(is)) {
			int slideNumber = 1;

			builder.addType(dataSourceId, SPARQLAnythingConstants.ROOT_ID, "Presentation");

			for (XSLFSlide slide : slides.getSlides()) {
				String slideId = dataSourceId + "/Slide_" + slideNumber;
				builder.addContainer(dataSourceId, SPARQLAnythingConstants.ROOT_ID, slideNumber, slideId);
				slideNumber++;

				AtomicInteger slideSlotNumber = new AtomicInteger(1);

				builder.addType(dataSourceId, slideId, "Slide");

				for (XSLFTextShape shape : slide.getPlaceholders()) {
					addOptionalValue(builder, dataSourceId, slideId, slideSlotNumber, shape.getTextType().toString(), shape.getText());
				}

			}

		}


	}

	private static void addOptionalValue(FacadeXGraphBuilder builder, String dataSourceId, String containerId, AtomicInteger slotKey, String type, Object value) {
		if (value != null) {
			String newContainer = containerId + "/" + type;
			builder.addContainer(dataSourceId, containerId, slotKey.getAndIncrement(), newContainer);
			builder.addType(dataSourceId, newContainer, type);
			builder.addValue(dataSourceId, newContainer, 1, value);

		}
	}

	@Override
	public Set<String> getMimeTypes() {
		return Sets.newHashSet("application/vnd.openxmlformats-officedocument.presentationml.presentation");
	}

	@Override
	public Set<String> getExtensions() {
		return Sets.newHashSet("pptx");
	}

}
