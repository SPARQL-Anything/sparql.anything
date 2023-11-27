package io.github.sparqlanything.slides.test;

import io.github.sparqlanything.model.BaseFacadeXGraphBuilder;
import io.github.sparqlanything.model.FacadeXGraphBuilder;
import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.slides.PptxTriplifier;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.junit.Test;

import java.net.URL;
import java.util.Properties;

public class PptxTest {

	public static void main(String[] args) {
		System.out.println("test");
	}

	@Test
	public void test(){

		PptxTriplifier st = new PptxTriplifier();
		URL doc = st.getClass().getClassLoader().getResource("./Presentation1.pptx");
		Properties p = new Properties();
		DatasetGraph dg;
		try {
			p.setProperty(IRIArgument.LOCATION.toString(), doc.toString());
			FacadeXGraphBuilder b = new BaseFacadeXGraphBuilder(p);
			st.triplify(p, b);
			dg = b.getDatasetGraph();
			RDFDataMgr.write(System.out, dg, Lang.TRIG);
		}catch (Exception ignored){

		}

	}
}
