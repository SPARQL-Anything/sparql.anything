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

import io.github.sparqlanything.model.*;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

public class PptxTriplifier implements Triplifier {

	public final static String MERGE_PARAGRAPHS = "docs.merge-paragraphs";
	public final static String TABLE_HEADERS = "docs.table-headers";

	private static final Logger logger = LoggerFactory.getLogger(PptxTriplifier.class);

	private static void addOptionalValue(FacadeXGraphBuilder builder, String dataSourceId, String containerId, String slotKey, String type, Object value){
		if(value!=null){
			String newContainer = containerId + "/" +
			builder.addValue(dataSourceId, containerId, slotKey, value);
		}
	}

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException, TriplifierHTTPException {
		URL url = Triplifier.getLocation(properties);
		if (url == null)
			return;
		String dataSourceId = "";
		String namespace = PropertyUtils.getStringProperty(properties, IRIArgument.NAMESPACE);

		builder.addRoot(dataSourceId);

		InputStream is = url.openStream();
		int slideNumber = 1;

		try (XMLSlideShow slides = new XMLSlideShow(is)) {

			builder.addType(dataSourceId, SPARQLAnythingConstants.ROOT_ID, "Presentation");

			for(XSLFSlide slide : slides.getSlides()){
				String slideId = dataSourceId + "_slide_" + slideNumber;
				builder.addContainer(dataSourceId, SPARQLAnythingConstants.ROOT_ID, 1, slideId);
				slideNumber ++;

				builder.addType(dataSourceId, slideId, "Slide");
				addOptionalValue(builder, dataSourceId, slideId, "title", slide.getTitle() );
				addOptionalValue(builder, dataSourceId, slideId, "name", slide.getSlideName() );
				addOptionalValue(builder, dataSourceId, slideId, "number", slide.getSlideNumber() );
				addOptionalValue(builder, dataSourceId, slideId, "number", slide.get );


			}
		}


	}

	@Override
	public Set<String> getMimeTypes() {
//		return Sets.newHashSet("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		return null;
	}

	@Override
	public Set<String> getExtensions() {
//		return Sets.newHashSet("docx");
		return null;
	}

}
