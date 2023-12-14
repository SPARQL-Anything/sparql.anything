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
import org.apache.commons.compress.utils.Sets;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPresentation;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class PptxTriplifier implements Triplifier {


	public static final IRIArgument EXTRACT_SECTIONS = new IRIArgument("slides.extract.sections", "false");

	/**
	 * See https://stackoverflow.com/questions/72947727/apache-poi-java-get-section-name-powerpoint
	 */
	private static void extractSections(FacadeXGraphBuilder builder, String dataSourceId, String rootId, XMLSlideShow presentation) {
		int sectionNumber = 1;
		Map<Long, XSLFSlide> sldIDToSlide = getSlideById(presentation);
		CTPresentation ctPresentation = presentation.getCTPresentation();
		CTExtensionList extList = ctPresentation.getExtLst();
		for (XmlObject section : getSections(extList)) {
			XmlObject[] sectionSldIds = getSectionSldIds(section);
			String sectionName = getSectionName(section);
			String sectionId = "/Section_" + sectionNumber;

			builder.addContainer(dataSourceId, rootId, sectionNumber, sectionId);
			builder.addType(dataSourceId, sectionId, "Section");

			int sectionSlideNumber = 1;
			for (XmlObject sectionSldId : sectionSldIds) {
				Long sldIdL = getSectionSldId(sectionSldId);
				XSLFSlide slide = sldIDToSlide.get(sldIdL);
				addSlide(builder, dataSourceId, sectionId, slide, sectionSlideNumber++, sectionId);
			}

			addValue(builder, dataSourceId, sectionId, new AtomicInteger(sectionSlideNumber++), "SectionName", sectionName);
			sectionNumber++;
		}
	}

	private static void addSlide(FacadeXGraphBuilder builder, String dataSourceId, String parentContainer, XSLFSlide slide) {
		addSlide(builder, dataSourceId, parentContainer, slide, slide.getSlideNumber(), null);
	}

	private static void addSlide(FacadeXGraphBuilder builder, String dataSourceId, String parentContainer, XSLFSlide slide, int slideNumber, String sectionId) {

		String slideId = "/Slide_" + slideNumber;
		if (sectionId != null) {
			slideId = sectionId + slideId;
		}
		builder.addContainer(dataSourceId, parentContainer, slideNumber, slideId);
		AtomicInteger slideSlotNumber = new AtomicInteger(1);
//		addValue(builder, dataSourceId, slideId, slideSlotNumber, "SlideNumber", slideNumber);
		builder.addType(dataSourceId, slideId, "Slide");
		for (XSLFTextShape shape : slide.getPlaceholders()) {
			addOptionalValue(builder, dataSourceId, slideId, slideSlotNumber, shape.getTextType().toString(), shape.getText());
		}
	}

	/**
	 * See https://stackoverflow.com/questions/72947727/apache-poi-java-get-section-name-powerpoint
	 */
	private static Long getSlideId(XSLFSlide slide) {
		if (slide == null) return null;
		Long slideId = null;
		XMLSlideShow presentation = slide.getSlideShow();
		String slideRId = presentation.getRelationId(slide);
		org.openxmlformats.schemas.presentationml.x2006.main.CTPresentation ctPresentation = presentation.getCTPresentation();
		org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdList sldIdLst = ctPresentation.getSldIdLst();
		for (org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdListEntry sldId : sldIdLst.getSldIdList()) {
			if (sldId.getId2().equals(slideRId)) {
				slideId = sldId.getId();
				break;
			}
		}
		return slideId;
	}

	private static Map<Long, XSLFSlide> getSlideById(XMLSlideShow presentation) {
		Map<Long, XSLFSlide> result = new HashMap<>();
		for (XSLFSlide slide : presentation.getSlides()) {
			result.put(getSlideId(slide), slide);
		}
		return result;
	}
	/**
	 * See https://stackoverflow.com/questions/72947727/apache-poi-java-get-section-name-powerpoint
	 */
	private static XmlObject[] getSections(org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList extList) {
		if (extList == null) return new XmlObject[0];
		return extList.selectPath("declare namespace p14='http://schemas.microsoft.com/office/powerpoint/2010/main' " + ".//p14:section");
	}

	/**
	 * See https://stackoverflow.com/questions/72947727/apache-poi-java-get-section-name-powerpoint
	 */
	private static XmlObject[] getSectionSldIds(XmlObject section) {
		if (section == null) return new XmlObject[0];
		return section.selectPath("declare namespace p14='http://schemas.microsoft.com/office/powerpoint/2010/main' " + ".//p14:sldId");
	}

	/**
	 * See https://stackoverflow.com/questions/72947727/apache-poi-java-get-section-name-powerpoint
	 */
	private static Long getSectionSldId(XmlObject sectionSldId) {
		if (sectionSldId == null) return null;
		Long sldIdL = null;
		XmlObject sldIdO = sectionSldId.selectAttribute(new QName("id"));
		if (sldIdO instanceof org.apache.xmlbeans.impl.values.XmlObjectBase) {
			String sldIsS = ((org.apache.xmlbeans.impl.values.XmlObjectBase) sldIdO).getStringValue();
			try {
				sldIdL = Long.valueOf(sldIsS);
			} catch (Exception ex) {
				// do nothing
			}
		}
		return sldIdL;
	}

	private static String getSectionName(XmlObject section) {
		if (section == null) return null;
		String sectionName = null;
		XmlObject name = section.selectAttribute(new QName("name"));
		if (name instanceof org.apache.xmlbeans.impl.values.XmlObjectBase) {
			sectionName = ((org.apache.xmlbeans.impl.values.XmlObjectBase) name).getStringValue();
		}
		return sectionName;
	}

	private static void addOptionalValue(FacadeXGraphBuilder builder, String dataSourceId, String containerId, AtomicInteger slotKey, String type, Object value) {
		if (value != null) addValue(builder, dataSourceId, containerId, slotKey, type, value);
	}

	private static void addValue(FacadeXGraphBuilder builder, String dataSourceId, String containerId, AtomicInteger slotKey, String type, Object value) {
		String newContainer = containerId + "/" + type;
		builder.addContainer(dataSourceId, containerId, slotKey.getAndIncrement(), newContainer);
		builder.addType(dataSourceId, newContainer, type);
		builder.addValue(dataSourceId, newContainer, 1, value);
	}

	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException, TriplifierHTTPException {
		URL url = Triplifier.getLocation(properties);
		if (url == null) return;
		String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID;
		String rootId = SPARQLAnythingConstants.ROOT_ID;
		boolean extractSections = PropertyUtils.getBooleanProperty(properties, EXTRACT_SECTIONS);

		builder.addRoot(dataSourceId);

		try (InputStream is = url.openStream(); XMLSlideShow presentation = new XMLSlideShow(is)) {
			builder.addType(dataSourceId, rootId, "Presentation");
			if (extractSections) extractSections(builder, dataSourceId, rootId, presentation);
			else presentation.getSlides().forEach(slide -> addSlide(builder, dataSourceId, rootId, slide));
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
