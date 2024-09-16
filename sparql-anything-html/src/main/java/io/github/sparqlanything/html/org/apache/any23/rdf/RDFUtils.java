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

package io.github.sparqlanything.html.org.apache.any23.rdf;

import io.github.sparqlanything.html.org.apache.any23.util.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFParserRegistry;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

/**
 * Basic class providing a set of utility methods when dealing with <i>RDF</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (dpalmisano@gmail.com)
 * @author Jacek Grzebyta (jgrzebyta@apache.org)
 */
public class RDFUtils {

    private static int nodeId = 0;

    private static final ValueFactory valueFactory = SimpleValueFactory.getInstance();

    private static final Logger LOG = LoggerFactory.getLogger(RDFUtils.class);

    private static final Statement[] EMPTY_STATEMENTS = new Statement[0];

    private RDFUtils() {
    }

    /**
     * Fixes typical errors in an absolute org.eclipse.rdf4j.model.IRI, such as unescaped spaces.
     *
     * @param uri
     *            An absolute org.eclipse.rdf4j.model.IRI, can have typical syntax errors
     *
     * @return An absolute org.eclipse.rdf4j.model.IRI that is valid against the org.eclipse.rdf4j.model.IRI syntax
     *
     * @throws IllegalArgumentException
     *             if org.eclipse.rdf4j.model.IRI is not fixable
     */
    public static String fixAbsoluteIRI(String uri) {
        String fixed = fixIRIWithException(uri);
        if (!fixed.matches("[a-zA-Z0-9]+:/.*"))
            throw new IllegalArgumentException("not a absolute org.eclipse.rdf4j.model.IRI: " + uri);
        // Add trailing slash if org.eclipse.rdf4j.model.IRI has only authority but no path.
        if (fixed.matches("https?://[a-zA-Z0-9.-]+(:[0-9+])?")) {
            fixed = fixed + "/";
        }
        return fixed;
    }

    /**
     * This method allows to obtain an <a href="http://www.w3.org/TR/xmlschema-2/#date">XML Schema</a> compliant date
     * providing a textual representation of a date and textual a pattern for parsing it.
     *
     * @param dateToBeParsed
     *            the String containing the date.
     * @param format
     *            the pattern as descibed in {@link SimpleDateFormat}
     *
     * @return a {@link String} representing the date
     *
     * @throws ParseException
     *             if there is an error parsing the given date.
     * @throws DatatypeConfigurationException
     *             if there is a serious configuration error.
     */
    public static String getXSDDate(String dateToBeParsed, String format)
            throws ParseException, DatatypeConfigurationException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ROOT);
        Date date = simpleDateFormat.parse(dateToBeParsed);
        GregorianCalendar gc = new GregorianCalendar(TimeZone.getDefault(), Locale.ROOT);
        gc.setTime(date);
        XMLGregorianCalendar xml = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        xml.setTimezone(0);
        return xml.toString();
    }

    /**
     * Prints a <code>date</code> to the XSD datetime format.
     *
     * @param date
     *            date to be printed.
     *
     * @return the string representation of the input date.
     */
    public static String toXSDDateTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ROOT);
        String s = simpleDateFormat.format(date);
        StringBuilder sb = new StringBuilder(s);
        sb.insert(22, ':');
        return sb.toString();
    }

    /**
     * <p>
     * Tries to fix a potentially broken relative or absolute URI.
     * </p>
     * These appear to be good rules: Remove whitespace or '\' or '"' in beginning and end Replace space with %20 Drop
     * the triple if it matches this regex (only protocol): ^[a-zA-Z0-9]+:(//)?$ Drop the triple if it matches this
     * regex: ^javascript: Truncate "&gt;.*$ from end of lines (Neko didn't quite manage to fix broken markup) Drop the
     * triple if any of these appear in the URL: &lt;&gt;[]|*{}"&lt;&gt;\
     *
     * @param unescapedIRI
     *            uri string to be unescaped.
     *
     * @return the unescaped string.
     */
    public static String fixIRIWithException(String unescapedIRI) {
        if (unescapedIRI == null)
            throw new IllegalArgumentException("org.eclipse.rdf4j.model.IRI was null");

        // Remove starting and ending whitespace
        String escapedIRI = unescapedIRI.trim();

        // Replace space with %20
        escapedIRI = escapedIRI.replaceAll(" ", "%20");

        // strip linebreaks
        escapedIRI = escapedIRI.replaceAll("\n", "");

        // 'Remove starting "\" or '"'
        if (escapedIRI.startsWith("\\") || escapedIRI.startsWith("\""))
            escapedIRI = escapedIRI.substring(1);
        // Remove ending "\" or '"'
        if (escapedIRI.endsWith("\\") || escapedIRI.endsWith("\""))
            escapedIRI = escapedIRI.substring(0, escapedIRI.length() - 1);

        // Drop the triple if it matches this regex (only protocol): ^[a-zA-Z0-9]+:/?/?$
        if (escapedIRI.matches("^[a-zA-Z0-9]+:/?/?$"))
            throw new IllegalArgumentException("no authority in org.eclipse.rdf4j.model.IRI: " + unescapedIRI);

        // Drop the triple if it matches this regex: ^javascript:
        if (escapedIRI.matches("^javascript:"))
            throw new IllegalArgumentException("org.eclipse.rdf4j.model.IRI starts with javascript: " + unescapedIRI);

        // stripHTML
        // escapedIRI = escapedIRI.replaceAll("\\<.*?\\>", "");

        // >.*$ from end of lines (Neko didn't quite manage to fix broken markup)
        escapedIRI = escapedIRI.replaceAll(">.*$", "");

        // Drop the triple if any of these appear in the URL: <>[]|*{}"<>\
        if (escapedIRI.contains("<")||escapedIRI.contains(">")||escapedIRI.matches("[\\[\\]|\\*\\{\\}\"\\\\]"))
            throw new IllegalArgumentException("Invalid character in org.eclipse.rdf4j.model.IRI: " + unescapedIRI);

        return escapedIRI;
    }

    /**
     * Creates a {@link IRI}.
     *
     * @param iri
     *            a base string for the {@link IRI}
     *
     * @return a valid {@link IRI}
     */
    public static IRI iri(String iri) {
        return valueFactory.createIRI(iri);
    }

    /**
     * Creates a {@link IRI}.
     *
     * @param namespace
     *            a base namespace for the {@link IRI}
     * @param localName
     *            a local name to associate with the namespace
     *
     * @return a valid {@link IRI}
     */
    public static IRI iri(String namespace, String localName) {
        return valueFactory.createIRI(namespace, localName);
    }

    /**
     * Creates a {@link Literal}.
     *
     * @param s
     *            string representation of the {@link Literal}
     *
     * @return valid {@link Literal}
     */
    public static Literal literal(String s) {
        return valueFactory.createLiteral(s);
    }

    /**
     * Creates a {@link Literal}.
     *
     * @param b
     *            boolean representation of the {@link Literal}
     *
     * @return valid {@link Literal}
     */
    public static Literal literal(boolean b) {
        return valueFactory.createLiteral(b);
    }

    /**
     * Creates a {@link Literal}.
     *
     * @param b
     *            byte representation of the {@link Literal}
     *
     * @return valid {@link Literal}
     */
    public static Literal literal(byte b) {
        return valueFactory.createLiteral(b);
    }

    /**
     * Creates a {@link Literal}.
     *
     * @param s
     *            short representation of the {@link Literal}
     *
     * @return valid {@link Literal}
     */
    public static Literal literal(short s) {
        return valueFactory.createLiteral(s);
    }

    /**
     * Creates a {@link Literal}.
     *
     * @param i
     *            int representation of the {@link Literal}
     *
     * @return valid {@link Literal}
     */
    public static Literal literal(int i) {
        return valueFactory.createLiteral(i);
    }

    /**
     * Creates a {@link Literal}.
     *
     * @param l
     *            long representation of the {@link Literal}
     *
     * @return valid {@link Literal}
     */
    public static Literal literal(long l) {
        return valueFactory.createLiteral(l);
    }

    /**
     * Creates a {@link Literal}.
     *
     * @param f
     *            float representation of the {@link Literal}
     *
     * @return valid {@link Literal}
     */
    public static Literal literal(float f) {
        return valueFactory.createLiteral(f);
    }

    /**
     * Creates a {@link Literal}.
     *
     * @param d
     *            double representation of the {@link Literal}
     *
     * @return valid {@link Literal}
     */
    public static Literal literal(double d) {
        return valueFactory.createLiteral(d);
    }

    /**
     * Creates a {@link Literal}.
     *
     * @param s
     *            the literal's label
     * @param l
     *            the literal's language
     *
     * @return valid {@link Literal}
     */
    public static Literal literal(String s, String l) {
        if (l == null) {
            // HACK: Workaround for ANY23 code that passes null in for language tag
            return valueFactory.createLiteral(s);
        } else {
            return valueFactory.createLiteral(s, l);
        }
    }

    /**
     * Creates a {@link Literal}.
     *
     * @param s
     *            the literal's label
     * @param datatype
     *            the literal's datatype
     *
     * @return valid {@link Literal}
     */
    public static Literal literal(String s, IRI datatype) {
        return valueFactory.createLiteral(s, datatype);
    }

    /**
     * Creates a {@link BNode}.
     *
     * @param id
     *            string representation of the {@link BNode}
     *
     * @return the valid {@link BNode}
     */
    // TODO: replace this with all occurrences of #getBNode()
    public static BNode bnode(String id) {
        return valueFactory.createBNode(id);
    }

    /**
     * @return a <code>bnode</code> with unique id.
     */
    public static BNode bnode() {
        return valueFactory.createBNode();
    }

    /**
     * Creates a {@link BNode} with an MD5 digest as part of the ID.
     *
     * @param id
     *            string representation of the {@link BNode} name for which we will create a md5
     *            hash.
     *
     * @return the valid {@link BNode}
     */
    public static BNode getBNode(String id) {
        return valueFactory.createBNode("node" + DigestUtils.md5Hex(id));
    }

    /**
     * Creates a {@link Statement}.
     *
     * @param s
     *            subject {@link Resource}
     * @param p
     *            predicate {@link org.eclipse.rdf4j.model.URI}
     * @param o
     *            object {@link Value}
     *
     * @return valid {@link Statement}
     */
    public static Statement triple(Resource s, IRI p, Value o) {
        return valueFactory.createStatement(s, p, o);
    }

    /**
     * Creates a statement of type: <code>toValue(s), toValue(p), toValue(o)</code>
     *
     * @param s
     *            subject.
     * @param p
     *            predicate.
     * @param o
     *            object.
     *
     * @return a statement instance.
     */
    public static Statement triple(String s, String p, String o) {
        return valueFactory.createStatement((Resource) toValue(s), (IRI) toValue(p),
                toValue(o));
    }

    /**
     * Creates a {@link Statement}.
     *
     * @param s
     *            subject.
     * @param p
     *            predicate.
     * @param o
     *            object.
     * @param g
     *            quad resource
     *
     * @return a statement instance.
     */
    public static Statement quad(Resource s, IRI p, Value o, Resource g) {
        return valueFactory.createStatement(s, p, o, g);
    }

    /**
     * Creates a statement of type: <code>toValue(s), toValue(p), toValue(o), toValue(g)</code>
     *
     * @param s
     *            subject.
     * @param p
     *            predicate.
     * @param o
     *            object.
     * @param g
     *            quad resource
     *
     * @return a statement instance.
     */
    public static Statement quad(String s, String p, String o, String g) {
        return valueFactory.createStatement((Resource) toValue(s), (IRI) toValue(p), toValue(o),
                (Resource) toValue(g));
    }

    /**
     * Creates a {@link Value}. If <code>s == 'a'</code> returns an {@link RDF#TYPE}. If
     * <code> s.matches('[a-z0-9]+:.*')</code> expands the corresponding prefix using {@link PopularPrefixes}.
     *
     * @param s
     *            string representation of value.
     *
     * @return a value instance.
     */
    public static Value toValue(String s) {
        if ("a".equals(s))
            return RDF.TYPE;
        if (s.matches("[a-z0-9]+:.*")) {
            return PopularPrefixes.get().expand(s);
        }
        return valueFactory.createLiteral(s);
    }

    /**
     *
     * Returns all the available {@link RDFFormat}s.
     *
     * @return an unmodifiable collection of formats.
     */
    public static Collection<RDFFormat> getFormats() {
        return RDFParserRegistry.getInstance().getKeys();
    }

    /**
     * Creates a new {@link RDFParser} instance.
     *
     * @param format
     *            parser format.
     *
     * @return parser instance.
     *
     * @throws IllegalArgumentException
     *             if format is not supported.
     */
    public static RDFParser getParser(RDFFormat format) {
        return Rio.createParser(format);
    }

    /**
     * Creates a new {@link RDFWriter} instance.
     *
     * @param format
     *            output format.
     * @param writer
     *            data output writer.
     *
     * @return writer instance.
     *
     * @throws IllegalArgumentException
     *             if format is not supported.
     */
    public static RDFWriter getWriter(RDFFormat format, Writer writer) {
        return Rio.createWriter(format, writer);
    }

    /**
     * Creates a new {@link RDFWriter} instance.
     *
     * @param format
     *            output format.
     * @param os
     *            output stream.
     *
     * @return writer instance.
     *
     * @throws IllegalArgumentException
     *             if format is not supported.
     */
    public static RDFWriter getWriter(RDFFormat format, OutputStream os) {
        return Rio.createWriter(format, os);
    }

    /**
     * Returns a parser type from the given extension.
     *
     * @param ext
     *            input extension.
     *
     * @return parser matching the extension.
     *
     * @throws IllegalArgumentException
     *             if no extension matches.
     */
    public static Optional<RDFFormat> getFormatByExtension(String ext) {
        if (!ext.startsWith("."))
            ext = "." + ext;
        return Rio.getParserFormatForFileName(ext);
    }

    /**
     * Parses the content of <code>is</code> input stream with the specified parser <code>p</code> using
     * <code>baseIRI</code>.
     *
     * @param format
     *            input format type.
     * @param is
     *            input stream containing <code>RDF</code>.
     * @param baseIRI
     *            base uri.
     *
     * @return list of statements detected within the input stream.
     *
     * @throws IOException
     *             if there is an error reading the {@link InputStream}
     */
    public static Statement[] parseRDF(RDFFormat format, InputStream is, String baseIRI) throws IOException {
        final StatementCollector handler = new StatementCollector();
        final RDFParser parser = getParser(format);
        parser.getParserConfig().set(BasicParserSettings.VERIFY_DATATYPE_VALUES, true);
        parser.setPreserveBNodeIDs(true);
        parser.setRDFHandler(handler);
        parser.parse(is, baseIRI);
        return handler.getStatements().toArray(EMPTY_STATEMENTS);
    }

    /**
     * Parses the content of <code>is</code> input stream with the specified parser <code>p</code> using <code>''</code>
     * as base org.eclipse.rdf4j.model.IRI.
     *
     * @param format
     *            input format type.
     * @param is
     *            input stream containing <code>RDF</code>.
     *
     * @return list of statements detected within the input stream.
     *
     * @throws IOException
     *             if there is an error reading the {@link InputStream}
     */
    public static Statement[] parseRDF(RDFFormat format, InputStream is) throws IOException {
        return parseRDF(format, is, "");
    }

    /**
     * Parses the content of <code>in</code> string with the specified parser <code>p</code> using <code>''</code> as
     * base org.eclipse.rdf4j.model.IRI.
     *
     * @param format
     *            input format type.
     * @param in
     *            input string containing <code>RDF</code>.
     *
     * @return list of statements detected within the input string.
     *
     * @throws IOException
     *             if there is an error reading the {@link InputStream}
     */
    public static Statement[] parseRDF(RDFFormat format, String in) throws IOException {
        return parseRDF(format, new ByteArrayInputStream(in.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Parses the content of the <code>resource</code> file guessing the content format from the extension.
     *
     * @param resource
     *            resource name.
     *
     * @return the statements declared within the resource file.
     *
     * @throws IOException
     *             if an error occurs while reading file.
     */
    public static Statement[] parseRDF(String resource) throws IOException {
        final int extIndex = resource.lastIndexOf('.');
        if (extIndex == -1)
            throw new IllegalArgumentException("Error while detecting the extension in resource name " + resource);
        final String extension = resource.substring(extIndex + 1);
        return parseRDF(getFormatByExtension(extension).orElseThrow(Rio.unsupportedFormat(extension)),
                RDFUtils.class.getResourceAsStream(resource));
    }

    /**
     * Checks if <code>href</code> is absolute or not.
     *
     * @param href
     *            candidate org.eclipse.rdf4j.model.IRI.
     *
     * @return <code>true</code> if <code>href</code> is absolute, <code>false</code> otherwise.
     */
    public static boolean isAbsoluteIRI(String href) {
        try {
            SimpleValueFactory.getInstance().createIRI(href.trim());
            new java.net.URI(href.trim());
            return true;
        } catch (IllegalArgumentException e) {
            LOG.trace("Error processing href: {}", href, e);
            return false;
        } catch (URISyntaxException e) {
            LOG.trace("Error interpreting href: {} as URI.", href, e);
            return false;
        }
    }

    /**
     * {@link #makeIRI(String, IRI, boolean) }.
     *
     * @param docUri
     *            It is a namespace. If it ends with '/' character than stays unchanged otherwise the hash character '#'
     *            is added to the end.
     *
     * @return instance of {@link Resource}.
     */
    public static Resource makeIRI(IRI docUri) {
        return makeIRI("node", docUri);
    }

    /**
     * {@link #makeIRI(String, IRI, boolean) }.
     *
     * @param type
     *            This argument is converted following Java naming conventions with
     *            {@link StringUtils#implementJavaNaming(String) }.
     * @param docIRI
     *            It is a namespace. If it ends with '/' character than stays unchanged otherwise the hash character '#'
     *            is added to the end.
     *
     * @return instance of {@link Resource}.
     */
    public static Resource makeIRI(String type, IRI docIRI) {
        return makeIRI(type, docIRI, false);
    }

    /**
     * Creates implementation of {@link Resource} from given arguments: <i>type</i> and <i>docIRI</i>.
     *
     * <b>NB:</b> The Java Naming Conventions is described by
     * <a href='http://www.geeksforgeeks.org/java-naming-conventions/'>GeeksForGeeks</a>.
     *
     * @param type
     *            This argument is converted following Java naming conventions with
     *            {@link StringUtils#implementJavaNaming(String) }.
     * @param docIRI
     *            It is a namespace. If it ends with '/' character than stays unchanged otherwise the hash character '#'
     *            is added to the end.
     * @param addId
     *            If argument is <b>TRUE</b> than the node identifier is added to the end formated
     *            <code>'_{int}'</code>.
     *
     * @return instance of {@link Resource}.
     */
    public static Resource makeIRI(String type, IRI docIRI, boolean addId) {

        // preprocess string: converts - -> _
        // converts <space>: word1 word2 -> word1Word2
        String newType = StringUtils.implementJavaNaming(type);

        String iriString;
        if (docIRI.toString().endsWith("/") || docIRI.toString().endsWith("#")) {
            iriString = docIRI.toString() + newType;
        } else {
            iriString = docIRI.toString() + "#" + newType;
        }

        if (addId) {
            iriString = iriString + "_" + Integer.toString(nodeId);
        }

        Resource node = RDFUtils.iri(iriString);
        if (addId) {
            nodeId++;
        }
        return node;
    }

    /**
     * Convert string to either IRI or Literal.
     *
     * If string value expresses valid IRI than {@link IRI} is created. Otherwise method creates simple {@link Literal}
     * xsd:string.
     *
     * @param inString
     *            an input string to manifest as {@link Value}
     *
     * @return either {@link IRI} or {@link Literal}.
     */
    public static Value makeIRI(String inString) {
        if (RDFUtils.isAbsoluteIRI(inString)) {
            return RDFUtils.iri(inString);
        } else {
            return RDFUtils.literal(inString);
        }
    }

    public static Value makeIRI() {
        BNode bnode = bnode(Integer.toString(nodeId));
        nodeId++;
        return bnode;
    }

}
