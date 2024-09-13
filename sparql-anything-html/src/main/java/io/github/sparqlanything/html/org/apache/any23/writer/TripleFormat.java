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

package io.github.sparqlanything.html.org.apache.any23.writer;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Hans Brende (hansbrende@apache.org)
 */
public class TripleFormat {
    private final String name;
    private final IRI standardIRI;
    private final List<String> mimeTypes;
    private final Charset charset;
    private final List<String> fileExtensions;
    private final Capabilities capabilities;
    RDFFormat rdfFormat;

    private static final ValueFactory vf = SimpleValueFactory.getInstance();

    private static final int WRITES_TRIPLES = 1;
    private static final int WRITES_GRAPHS = 1 << 1;
    private static final int WRITES_NAMESPACES = 1 << 2;

    public static final Capabilities NONSTANDARD = new Capabilities(0);
    public static final Capabilities TRIPLES = new Capabilities(WRITES_TRIPLES);
    public static final Capabilities QUADS = new Capabilities(WRITES_TRIPLES | WRITES_GRAPHS);
    public static final Capabilities TRIPLES_AND_NAMESPACES = TRIPLES.withNamespaces();
    public static final Capabilities QUADS_AND_NAMESPACES = QUADS.withNamespaces();

    public static class Capabilities {
        private final int raw;

        private Capabilities(int raw) {
            this.raw = raw;
        }

        public boolean has(Capabilities other) {
            int oraw = other.raw;
            return (this.raw & oraw) == oraw;
        }

        private Capabilities withNamespaces() {
            return new Capabilities(this.raw | WRITES_NAMESPACES);
        }

        // TODO: add "supportsComments()"
    }

    private static IllegalArgumentException mimeTypeErr(String mt) {
        return new IllegalArgumentException(mt + " is not a valid mimetype");
    }

    private static IllegalArgumentException extensionErr(String ext) {
        return new IllegalArgumentException(ext + " is not a valid extension");
    }

    private static <E> E checkNonNull(E object, String name) {
        if (object == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
        return object;
    }

    // see https://tools.ietf.org/html/rfc2045#section-5.1
    private static void checkMimeTypes(List<String> mts) {
        if (checkNonNull(mts, "mimetypes").isEmpty()) {
            throw new IllegalArgumentException("mimetypes must not be empty");
        }
        for (String mt : mts) {
            boolean slash = false;
            for (int i = 0, len = checkNonNull(mt, "mimetype").length(); i < len; i++) {
                char ch = mt.charAt(i);
                if (ch <= ' ' || ch >= 127 || ch == '(' || ch == ')' || ch == '<' || ch == '>' || ch == '@' || ch == ','
                        || ch == ';' || ch == ':' || ch == '\\' || ch == '"' || ch == '[' || ch == ']' || ch == '?'
                        || ch == '='
                        // also disallow wildcards:
                        || ch == '*') {
                    throw mimeTypeErr(mt);
                } else if (ch == '/') {
                    if (slash || i == 0 || i + 1 == len) {
                        throw mimeTypeErr(mt);
                    }
                    slash = true;
                }
            }
            if (!slash) {
                throw mimeTypeErr(mt);
            }
        }
    }

    private static void checkExtensions(List<String> exts) {
        for (String ext : checkNonNull(exts, "extensions")) {
            int illegalDot = 0;
            for (int i = 0, len = checkNonNull(ext, "extension").length(); i < len; i++) {
                char ch = ext.charAt(i);
                if (ch <= ' ' || ch >= 127 || ch == '<' || ch == '>' || ch == ':' || ch == '"' || ch == '/'
                        || ch == '\\' || ch == '|' || ch == '?' || ch == '*') {
                    throw extensionErr(ext);
                } else if (ch == '.') {
                    int next = i + 1;
                    if (i == illegalDot || next == len) {
                        throw extensionErr(ext);
                    }
                    illegalDot = next;
                }
            }
        }
    }

    private static String normalizeMimeType(String mt) {
        return mt.toLowerCase(Locale.ENGLISH);
    }

    private static String normalizeExtension(String ext) {
        return ext.toLowerCase(Locale.ENGLISH);
    }

    private TripleFormat(String name, Collection<String> mimeTypes, Charset charset, Collection<String> fileExtensions,
            String standardIRI, Capabilities capabilities) {
        this.name = checkNonNull(name, "display name");
        checkMimeTypes(this.mimeTypes = Collections.unmodifiableList(
                mimeTypes.stream().map(TripleFormat::normalizeMimeType).distinct().collect(Collectors.toList())));
        if ((this.charset = charset) != null && !charset.canEncode()) {
            throw new IllegalArgumentException(charset + " does not allow encoding");
        }
        checkExtensions(this.fileExtensions = Collections.unmodifiableList(
                fileExtensions.stream().map(TripleFormat::normalizeExtension).distinct().collect(Collectors.toList())));
        this.standardIRI = standardIRI == null ? null : vf.createIRI(standardIRI);
        this.capabilities = checkNonNull(capabilities, "capabilities");
    }

    public static TripleFormat of(String displayName, Collection<String> mimeTypes, Charset defaultCharset,
            Collection<String> fileExtensions, String standardIRI, Capabilities capabilities) {
        return new TripleFormat(displayName, mimeTypes, defaultCharset, fileExtensions, standardIRI, capabilities);
    }

    public Optional<Charset> getCharset() {
        return Optional.ofNullable(this.charset);
    }

    static Capabilities capabilities(RDFFormat format) {
        if (format.supportsContexts()) {
            return format.supportsNamespaces() ? QUADS_AND_NAMESPACES : QUADS;
        } else {
            return format.supportsNamespaces() ? TRIPLES_AND_NAMESPACES : TRIPLES;
        }
    }

    private static String iri(IRI iri) {
        return iri == null ? null : iri.stringValue();
    }

    static TripleFormat of(RDFFormat format) {
        TripleFormat f = of(format.getName(), format.getMIMETypes(), format.getCharset(), format.getFileExtensions(),
                iri(format.getStandardURI()), capabilities(format));
        f.rdfFormat = format;
        return f;
    }

    RDFFormat toRDFFormat() {
        RDFFormat fmt = this.rdfFormat;
        if (fmt != null) {
            return fmt;
        }
        Capabilities capabilities = this.capabilities;
        if (!capabilities.has(TRIPLES)) {
            throw new UnsupportedOperationException("This format does not print RDF triples");
        }
        return this.rdfFormat = new RDFFormat(this.name, this.mimeTypes, this.charset, this.fileExtensions,
                this.standardIRI, capabilities.has(TRIPLES_AND_NAMESPACES), capabilities.has(QUADS), false);
    }

    public Optional<IRI> getStandardIRI() {
        return Optional.ofNullable(this.standardIRI);
    }

    public List<String> getMimeTypes() {
        return this.mimeTypes;
    }

    public String getMimeType() {
        return this.mimeTypes.get(0);
    }

    public List<String> getExtensions() {
        return this.fileExtensions;
    }

    public Optional<String> getExtension() {
        return this.fileExtensions.isEmpty() ? Optional.empty() : Optional.of(this.fileExtensions.get(0));
    }

    public Capabilities getCapabilities() {
        return this.capabilities;
    }

    public String getDisplayName() {
        return this.name;
    }

    public String toString() {
        return this.name + this.mimeTypes.stream().collect(Collectors.joining(", ", " (mimeTypes=", "; "))
                + this.fileExtensions.stream().collect(Collectors.joining(", ", "ext=", ")"));
    }

}
