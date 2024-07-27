/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
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

package io.github.sparqlanything.html;

import com.microsoft.playwright.*;
import io.github.sparqlanything.model.*;
import io.github.sparqlanything.model.annotations.Example;
import io.github.sparqlanything.model.annotations.Option;
import org.apache.any23.Any23;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.*;

@io.github.sparqlanything.model.annotations.Triplifier
public class HTMLTriplifier implements Triplifier {

	private static final Logger log = LoggerFactory.getLogger(HTMLTriplifier.class);

	@Example(resource = "https://sparql-anything.cc/examples/simple.html", description = "Selecting text contained in elements of the class \"paragraph\"", query = "PREFIX whatwg: <https://html.spec.whatwg.org/#> SELECT ?text WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.html,html.selector=.paragraph> { ?s whatwg:innerText ?text } }")
	@Option(description = "A CSS selector that restricts the HTML tags to consider for the triplification.", validValues = "Any valid CSS selector.")
	public static final IRIArgument PROPERTY_SELECTOR = new IRIArgument("html.selector", ":root");

	@Example(resource = "https://sparql-anything.cc/examples/Microdata1.html", description = "Extract triples embedded in the web page at the following address https://sparql-anything.cc/examples/Microdata1.html", query = "CONSTRUCT { ?s ?p ?o . } WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/Microdata1.html,html.metadata=true> { GRAPH ?g { ?s ?p ?o } } }")
	@Option(description = "It tells the triplifier to extract inline RDF from HTML pages. The triples extracted will be included in the default graph. -- See #164", validValues = "true/false")
	public static final IRIArgument PROPERTY_METADATA = new IRIArgument("html.metadata", "false");


	@Option(description = "It tells the triplifier to use the specified browser to navigate to the page to obtain HTML. By default a browser is not used. The use of a browser has some dependencies -- see [BROWSER](https://github.com/SPARQL-Anything/sparql.anything/blob/v1.0-DEV/BROWSER.md) and [justin2004's blogpost](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql).", validValues = "chromium|webkit|firefox")
	public static final IRIArgument PROPERTY_BROWSER = new IRIArgument("html.browser");

	@Example(resource = "https://sparql-anything.cc/examples/simple.xml", description = "The element names are case-sensitive when using the XML parser.", query = "CONSTRUCT {?s ?p ?o} WHERE { SERVICE <x-sparql-anything:location=https://sparql-anything.cc/examples/simple.xml,triplifier=io.github.sparqlanything.html.HTMLTriplifier,html.parser=xml> { ?s ?p ?o } }")
	@Option(description = "It tells the triplifier to use the specified JSoup parser (default: html).", validValues = "xml html")
	public static final IRIArgument PROPERTY_PARSER = new IRIArgument("html.parser", "html");
	private enum ParamParser {
		HTML("html"), XML("xml");
		private String str;
		ParamParser(String str){
			this.str = str;
		}
		boolean is(String str){
			return str.equals(this.str);
		}
	}
	@Option(description = "When using a browser to navigate, it tells the triplifier to wait for the specified number of seconds (after telling the browser to navigate to the page) before attempting to obtain HTML. -- See See [justin2004's blogpost](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql).", validValues = "Any integer")
	public static final IRIArgument PROPERTY_BROWSER_WAIT = new IRIArgument("html.browser.wait");

	@Option(description = "When using a browser to navigate, take a screenshot of the webpage (perhaps for troubleshooting) and save it here. See [justin2004's blogpost](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql).", validValues = "Any valid URL")
	public static final IRIArgument PROPERTY_BROWSER_SCREENSHOT = new IRIArgument("html.browser.screenshot");

	@Option(description = "When using a browser to navigate, it tells the browser if it spends longer than this amount of time (in milliseconds) until a load event is emitted then the operation will timeout -- See [justin2004's blogpost](https://github.com/justin2004/weblog/tree/master/scraping_with_sparql).", validValues = "Any integer")
	public static final IRIArgument PROPERTY_BROWSER_TIMEOUT = new IRIArgument("html.browser.timeout", "30000");
	private static final String HTML_NS = "http://www.w3.org/1999/xhtml#";
	private static final String DOM_NS = "https://html.spec.whatwg.org/#";

	private static String localName(Element element) {
		String tagName = element.tagName().replace(':', '|');
		StringBuilder selector = new StringBuilder(tagName);
		String classes = StringUtil.join(element.classNames(), ".");
		if (classes.length() > 0) {
			selector.append('.').append(classes);
		}

		if (element.parent() != null && !(element.parent() instanceof Document)) {
			selector.insert(0, " > ");
			if (element.parent().select(selector.toString()).size() > 1) {
				selector.append(String.format(":nth-child(%d)", element.elementSiblingIndex() + 1));
			}

			selector.insert(0, localName(element.parent()));
		}
		return selector.toString().replaceAll(" > ", "/").replaceAll(":nth-child\\(([0-9]+)\\)", ":$1");
	}
	@Override
	public void triplify(Properties properties, FacadeXGraphBuilder builder) throws IOException, TriplifierHTTPException {

		Charset charset = Triplifier.getCharsetArgument(properties);
		boolean blank_nodes = PropertyUtils.getBooleanProperty(properties, IRIArgument.BLANK_NODES);
		String namespace = PropertyUtils.getStringProperty(properties, IRIArgument.NAMESPACE);
		String selector = PropertyUtils.getStringProperty(properties, PROPERTY_SELECTOR);
		String dataSourceId = SPARQLAnythingConstants.DATA_SOURCE_ID;
		String parser = PropertyUtils.getStringProperty(properties, PROPERTY_PARSER, "html");
		log.trace(properties.toString());
		boolean extractMetadata = PropertyUtils.getBooleanProperty(properties, PROPERTY_METADATA);
		if (extractMetadata) {
			log.trace("Extracting metadata (needs HTTP location)");
			try {
				URL url = Triplifier.getLocation(properties);
				extractMetadata(Objects.requireNonNull(url), builder);
			} catch (IOException | URISyntaxException | ExtractionException | TripleHandlerException e) {
				log.error(e.getMessage());
			}
		}

		log.trace("namespace {}\n root {}\ncharset {}\nselector {}", namespace, SPARQLAnythingConstants.ROOT_ID, charset, selector);

		Document doc;
		// If location is a http or https, raise exception if status is not 200

		URL url = Triplifier.getLocation(properties);
		if (properties.containsKey(PROPERTY_BROWSER.toString())) {
			log.debug("Browser used (needs an HTTP location): {}", url);
			log.debug("Loading URL: {}", url);
			if(!ParamParser.HTML.is(parser)){
				log.warn("Parser not supported in browser mode: {} (ignored)", parser);
			}
			doc = Jsoup.parse(useBrowserToNavigate(Objects.requireNonNull(url).toString(), properties));
		} else {
			Parser p;
			if(ParamParser.XML.is(parser)){
				p = Parser.xmlParser();
			}else if(ParamParser.HTML.is(parser)){
				p = Parser.htmlParser();
			}else{
				log.warn("Parser not supported: {} (ignored)", parser);
				p = Parser.htmlParser();
			}
			try (InputStream is = Triplifier.getInputStream(properties)) {
				doc = Jsoup.parse(is, charset.toString(), Triplifier.getResourceId(properties), p);
			}
		}

		Elements elements = doc.select(selector);
		String rootResourceId = null;

		if (elements.size() > 1) {
			// Create a root container
			rootResourceId = SPARQLAnythingConstants.ROOT_ID;
			builder.addRoot(dataSourceId);
		}

		int counter = 0;
		for (Element element : elements) {
			counter++;
			String resourceId = toResourceId(element, blank_nodes, builder, dataSourceId);
			if (elements.size() > 1) {
				// link to root container
				builder.addContainer(dataSourceId, rootResourceId, counter, toResourceId(element, blank_nodes, builder, dataSourceId));
			} else {
				// Is root container
				rootResourceId = SPARQLAnythingConstants.ROOT_ID;
				resourceId = SPARQLAnythingConstants.ROOT_ID;
				builder.addRoot(dataSourceId);
			}
			try {
				populate(builder, dataSourceId, element, blank_nodes, resourceId);
			} catch (URISyntaxException e) {
				throw new IOException(e);
			}
		}

	}

	@Override
	public Set<String> getMimeTypes() {
		Set<String> r = new HashSet<>();
		r.add("text/html");
		return  r;
	}

	@Override
	public Set<String> getExtensions() {
		Set<String> r = new HashSet<>();
		r.add("html");
		return  r;
	}

	private void extractMetadata(URL url, FacadeXGraphBuilder builder) throws IOException, URISyntaxException, ExtractionException, TripleHandlerException {
		Any23 runner = new Any23();
		runner.setHTTPUserAgent("test-user-agent");
		DocumentSource source = runner.createDocumentSource(url.toString());
		try (TripleHandler handler = new MetadataWriter(builder)) {
			runner.extract(source, handler);
		}
	}

	private String makeURI(String tagOrAttribute, Element currentElement){
		// If name is prefixed, try to find the declaration from the given element upwards
// Is the TagName with a prefix? See #452 #466
		String ns = null;
		String localName = null;
		if(tagOrAttribute.split(":").length == 2){
			String[] elarr = tagOrAttribute.split(":");
			String prefixName =  elarr[0];
			localName =  elarr[1];
			// Check if namespace is declared in current attribute
			Attribute nsAttr = null;
			Element parent = currentElement;
			while (nsAttr == null){
				// Check if namespace is declared in any parent attributes
				nsAttr = parent.attributes().attribute("xmlns:" + prefixName);
				if(nsAttr == null && parent.hasParent()){
					parent = parent.parent();
					continue;
				}
				break; // root was reached
			}

			if (nsAttr != null){
				// Namespace was declared
				ns = nsAttr.getValue();
			}
		}
		if(ns == null){
			// Namespace was not declared (ignore prefix)
			// Default namespace
			ns = HTML_NS;
			localName = tagOrAttribute;
		}
		return (ns.endsWith("#")? ns: ns+"#") + localName;
	}
	private void populate(FacadeXGraphBuilder builder, String dataSourceId, Element element, boolean blank_nodes, String resourceId) throws URISyntaxException {

		//String tagName = element.tagName(); // tagname is the type
		String typeURI = makeURI(element.tagName(), element);

//		String resourceId = toResourceId(element, blank_nodes);
		String innerHtml = element.html();
		if (!innerHtml.trim().equals("")) {
			builder.addValue(dataSourceId, resourceId, new URI(DOM_NS + "innerHTML"), innerHtml);
		}
		String innerText = element.text();
		if (!innerText.trim().equals("")) {
			builder.addValue(dataSourceId, resourceId, new URI(DOM_NS + "innerText"), innerText);
		}
		builder.addType(dataSourceId, resourceId, new URI(typeURI));
		// attributes
		for (Attribute attribute : element.attributes()) {
			String key = attribute.getKey();
			String attributeURI = makeURI(key, element);
			String value = attribute.getValue();
			builder.addValue(dataSourceId, resourceId, new URI(attributeURI), value);
		}
		// Children
		int counter = 0;

		for (Node child : element.childNodes()) {
			if (child.outerHtml().trim().equals("")) continue;
			counter++;
			if (child instanceof Element) {
				builder.addContainer(dataSourceId, resourceId, counter, toResourceId((Element) child, blank_nodes, builder, dataSourceId));
				populate(builder, dataSourceId, (Element) child, blank_nodes, toResourceId((Element) child, blank_nodes, builder, dataSourceId));
			} else {
				builder.addValue(dataSourceId, resourceId, counter, child.outerHtml());
			}
		}

	}

	private String toResourceId(Element element, boolean blankNodes, FacadeXGraphBuilder builder, String dataSourceId) {
		if (blankNodes) {
			return Integer.toHexString(element.hashCode());
		} else {
			String ln = localName(element);
			log.debug(ln);
			return "/".concat(ln);
		}
	}

	private String useBrowserToNavigate(String url, Properties properties) {
		// navigate to the page at url and return the HTML as a string
		String browserProperty = PropertyUtils.getStringProperty(properties, PROPERTY_BROWSER);

		// first pull out http headers that we need to pass to the browser
		Map<String, String> props = new HashMap<String, String>((Map) properties);
		Map<String, String> headers = new HashMap<>();
		for (Map.Entry<String, String> entry : props.entrySet()) {
			if (entry.getKey().matches("^" + HTTPHelper.HTTPHEADER_PREFIX + ".*")) { // TODO the dots need to be escaped
				// in the regex
				headers.put(entry.getKey().replaceAll("^" + HTTPHelper.HTTPHEADER_PREFIX, ""), entry.getValue());
			}
		}
		log.debug("HTTP headers passed to headless browser: {}", headers);

		Playwright playwright = Playwright.create();
		Browser browser;
		switch (browserProperty) {
			case "chromium":
				log.debug("using chromium");
				browser = playwright.chromium().launch();
				break;
			case "firefox":
				log.debug("using firefox");
				browser = playwright.firefox().launch();
				break;
			case "webkit":
				log.debug("using webkit");
				browser = playwright.webkit().launch();
				break;
			default:
				log.warn("\"" + browserProperty + "\"" + " is not a valid browser -- defaulting to chromium");
				browser = playwright.chromium().launch();
				break;
		}
		BrowserContext context = browser.newContext();
		Page page = context.newPage();
		page.setExtraHTTPHeaders(headers);
		Page.NavigateOptions options = new Page.NavigateOptions();
		if (properties.containsKey(PROPERTY_BROWSER_TIMEOUT.toString())) {
			int timeoutMilliseconds = PropertyUtils.getIntegerProperty(properties, PROPERTY_BROWSER_TIMEOUT);
			log.debug("headless browser navigating to url with timeout of {} milliseconds", timeoutMilliseconds);
			options.setTimeout(timeoutMilliseconds);
			page.navigate(url, options);
		} else {
			page.navigate(url);
		}
		try {
			if (properties.containsKey(PROPERTY_BROWSER_WAIT.toString())) {
				int waitSeconds = PropertyUtils.getIntegerProperty(properties, PROPERTY_BROWSER_WAIT);
				log.debug("headless browser navigated to url and now will wait for {} seconds...", waitSeconds);
				// sleep before we try to pull the HTML content out the the browser
				java.util.concurrent.TimeUnit.SECONDS.sleep(waitSeconds);
			}
			if (properties.containsKey(PROPERTY_BROWSER_SCREENSHOT.toString())) {
				page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(new URI(PropertyUtils.getStringProperty(properties, PROPERTY_BROWSER_SCREENSHOT)))));
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		String htmlFromBrowser = page.content() + getFrames(page.mainFrame());
		// ^ TODO it would be better to put the iframes in the right place rather than
		// simply appending them
		// e.g. with Frame's setContent(String html, Frame.SetContentOptions options)
		// OR
		// it might be easier to have useBrowserToNavigate() return a List of strings
		// so that when we triplify we can return a root node for each iframe + one for
		// the actual page
		browser.close();
		log.debug("HTML content: {}", htmlFromBrowser);
		return htmlFromBrowser;
	}

	private String getFrames(Frame frame) {
		// get the content from all of the iframes
		String allFramesContent = "";
		if (!frame.childFrames().isEmpty()) {
			for (Frame child : frame.childFrames()) {
				allFramesContent = allFramesContent + child.content() + getFrames(child);
			}
		}
		return allFramesContent;
	}
}
