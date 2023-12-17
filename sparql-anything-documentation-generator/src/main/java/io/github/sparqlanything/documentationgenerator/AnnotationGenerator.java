package io.github.sparqlanything.documentationgenerator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.github.sparqlanything.engine.FacadeX;
import io.github.sparqlanything.model.annotations.Format;
import io.github.sparqlanything.slides.PptxTriplifier;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.engine.main.QC;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class AnnotationGenerator {

	public static void main(String[] args) throws IOException {
		Configuration freemarkerCfg = getConfiguration();
		Template temp = freemarkerCfg.getTemplate("format.ftlh");
		generateTemplateForFormat(temp, PptxTriplifier.class);
	}

	private static Configuration getConfiguration() {
		// Create your Configuration instance, and specify if up to what FreeMarker
		// version (here 2.3.27) do you want to apply the fixes that are not 100%
		// backward-compatible. See the Configuration JavaDoc for details.
		Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_28);

		// Specify the source where the template files come from. Here I set a
		// plain directory for it, but non-file-system sources are possible too:
		freemarkerCfg.setClassLoaderForTemplateLoading(AnnotationGenerator.class.getClassLoader(), ".");
		freemarkerCfg.setNumberFormat("computer");

		// Set the preferred charset template files are stored in. UTF-8 is
		// a good choice in most applications:
		freemarkerCfg.setDefaultEncoding("UTF-8");

		// Sets how errors will appear.
		// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is
		// better.
		freemarkerCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
		freemarkerCfg.setLogTemplateExceptions(false);

		// Wrap unchecked exceptions thrown during template processing into
		// TemplateException-s.
		freemarkerCfg.setWrapUncheckedExceptions(true);

		freemarkerCfg.setClassForTemplateLoading(AnnotationGenerator.class, "");
		return freemarkerCfg;
	}

	private static void generateTemplateForFormat(Template temp, Class<?> example) {
		Map<String, Object> var = new HashMap<>();

		// This assumes that all the classes in the array are in the same package
		Format f = example.getPackage().getAnnotation(Format.class);
		var.put("format", f);
		Query q = getDefaultTransformationQuery(f);
		var.put("defaultTransformationQuery", q.toString(Syntax.syntaxSPARQL_11));
		var.put("facadeXRdf", getFacadeXRdf(q));

		StringWriter sw = new StringWriter();
		try {
			temp.process(var, sw);
		} catch (TemplateException | IOException ignored) {
		}
		System.out.println(sw.getBuffer().toString());
	}

	public static Query getDefaultTransformationQuery(Format f) {
		if (f.showGraphs()) {
			return QueryFactory.create(String.format("CONSTRUCT {GRAPH ?g {?s ?p ?o}} WHERE {SERVICE<x-sparql-anything:location=%s> { GRAPH ?g { ?s ?p ?o}}}", f.getResourceExample()));
		} else {
			return QueryFactory.create(String.format("CONSTRUCT {?s ?p ?o} WHERE {SERVICE<x-sparql-anything:location=%s> {GRAPH ?g { ?s ?p ?o}}}", f.getResourceExample()));
		}
	}

	private static String getFacadeXRdf(Query q) {
		// Set FacadeX OpExecutor as default executor factory
		QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);

		// Execute the query by using standard Jena ARQ's API
		Dataset kb = DatasetFactory.createGeneral();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (q.isConstructType()) {
			System.out.println(q.toString());
			Model m = QueryExecutionFactory.create(q, kb).execConstruct();

			System.out.println(m.size());
			QueryExecutionFactory.create(q, kb).execConstruct().write(baos, "TTL");
		} else if (q.isConstructQuad()) {
			Dataset d = QueryExecutionFactory.create(q, kb).execConstructDataset();
			RDFDataMgr.write(baos, d, Lang.TRIG);
		}

		System.out.println(baos.toString());
		return baos.toString();

	}
}
