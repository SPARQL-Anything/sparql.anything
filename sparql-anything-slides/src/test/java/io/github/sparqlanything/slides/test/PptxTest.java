package io.github.sparqlanything.slides.test;

import io.github.sparqlanything.model.*;
import io.github.sparqlanything.slides.PptxTriplifier;
import io.github.sparqlanything.testutils.AbstractTriplifierTester;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class PptxTest extends AbstractTriplifierTester {

	public PptxTest() {
		super(new PptxTriplifier(), new Properties(), "pptx");
	}

	@Test
	public void testPresentation1() {
		this.assertResultIsIsomorphicWithExpected();
	}
}
