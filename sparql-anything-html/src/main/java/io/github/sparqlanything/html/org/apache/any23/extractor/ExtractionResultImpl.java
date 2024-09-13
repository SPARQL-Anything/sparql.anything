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

package io.github.sparqlanything.html.org.apache.any23.extractor;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionContext;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.Extractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.TagSoupExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.MicroformatExtractor;
import io.github.sparqlanything.html.org.apache.any23.rdf.Prefixes;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandler;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleHandlerException;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * <p>
 * A default implementation of {@link ExtractionResult}; it receives extraction output from one {@link Extractor}
 * working on one document, and passes the output on to a {@link TripleHandler}. It deals with details such as creation
 * of {@link ExtractionContext} objects and closing any open contexts at the end of extraction.
 * </p>
 * <p>
 * The {@link #close()} method must be invoked after the extractor has finished processing.
 * </p>
 * <p>
 * There is usually no need to provide additional implementations of the ExtractionWriter interface.
 * </p>
 *
 * @see TripleHandler
 * @see ExtractionContext
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class ExtractionResultImpl implements TagSoupExtractionResult {

    private final ExtractionContext context;

    private final Extractor<?> extractor;

    private final TripleHandler tripleHandler;

    private final Collection<ExtractionResult> subResults = new ArrayList<>();

    private final Set<Object> knownContextIDs = new HashSet<>();

    private boolean isClosed = false;

    private boolean isInitialized = false;

    private List<Issue> issues;

    private List<ResourceRoot> resourceRoots;

    private List<PropertyPath> propertyPaths;

    public ExtractionResultImpl(ExtractionContext context, Extractor<?> extractor, TripleHandler tripleHandler) {
        this(context, extractor, tripleHandler, new ArrayList<>());
    }

    private ExtractionResultImpl(ExtractionContext context, Extractor<?> extractor, TripleHandler tripleHandler,
            List<Issue> issues) {
        if (context == null) {
            throw new NullPointerException("context cannot be null.");
        }
        if (extractor == null) {
            throw new NullPointerException("extractor cannot be null.");
        }
        if (tripleHandler == null) {
            throw new NullPointerException("triple handler cannot be null.");
        }

        this.extractor = extractor;
        this.tripleHandler = tripleHandler;
        this.context = context;
        this.issues = issues;

        knownContextIDs.add(context.getUniqueID());

        try {
            // openContext() must be called before extraction begins
            // so that BenchmarkTripleHandler can report accurate times.
            // See https://issues.apache.org/jira/browse/ANY23-337
            tripleHandler.openContext(context);
        } catch (TripleHandlerException e) {
            throw new RuntimeException("Error while opening context", e);
        }
    }

    public boolean hasIssues() {
        return !issues.isEmpty();
    }

    public int getIssuesCount() {
        return issues.size();
    }

    @Override
    public void printReport(PrintStream ps) {
        ps.print(String.format(Locale.ROOT, "Context: %s [errors: %d] {\n", context, getIssuesCount()));
        for (Issue issue : issues) {
            ps.print(issue.toString());
            ps.print("\n");
        }
        // Printing sub results.
        for (ExtractionResult er : subResults) {
            er.printReport(ps);
        }
        ps.print("}\n");
    }

    @Override
    public Collection<Issue> getIssues() {
        return issues.isEmpty() ? Collections.<Issue> emptyList() : Collections.unmodifiableList(issues);
    }

    @Override
    public ExtractionResult openSubResult(ExtractionContext context) {
        final String contextID = context.getUniqueID();
        if (knownContextIDs.contains(contextID)) {
            throw new IllegalArgumentException("Duplicate contextID: " + contextID);
        }
        knownContextIDs.add(contextID);

        checkOpen();
        ExtractionResult result = new ExtractionResultImpl(context, extractor, tripleHandler, this.issues);
        subResults.add(result);
        return result;
    }

    public ExtractionContext getExtractionContext() {
        return context;
    }

    @Override
    public void writeTriple(Resource s, IRI p, Value o, IRI g) {
        if (s == null || p == null || o == null)
            return;
        // Check for misconstructed literals or BNodes, Sesame does not catch this.
        if (s.stringValue() == null || p.stringValue() == null || o.stringValue() == null) {
            throw new IllegalArgumentException("The statement arguments must be not null.");
        }
        checkOpen();
        try {
            tripleHandler.receiveTriple(s, p, o, g, context);
        } catch (TripleHandlerException e) {
            throw new RuntimeException(String.format(Locale.ROOT, "Error while receiving triple %s %s %s", s, p, o), e);
        }
    }

    boolean wasTouched() {
        return isInitialized;
    }

    @Override
    public void writeTriple(Resource s, IRI p, Value o) {
        writeTriple(s, p, o, null);
    }

    @Override
    public void writeNamespace(String prefix, String uri) {
        checkOpen();
        try {
            tripleHandler.receiveNamespace(prefix, uri, context);
        } catch (TripleHandlerException e) {
            throw new RuntimeException(String.format(Locale.ROOT, "Error while writing namespace %s:%s", prefix, uri),
                    e);
        }
    }

    @Override
    public void notifyIssue(IssueLevel level, String msg, long row, long col) {
        issues.add(new Issue(level, msg, row, col));
    }

    @Override
    public void close() {
        if (isClosed)
            return;
        isClosed = true;
        for (ExtractionResult subResult : subResults) {
            subResult.close();
        }
        try {
            tripleHandler.closeContext(context);
        } catch (TripleHandlerException e) {
            throw new RuntimeException("Error while opening context", e);
        }
    }

    private void checkOpen() {
        if (!isInitialized) {
            isInitialized = true;
            Prefixes prefixes = extractor.getDescription().getPrefixes();
            for (String prefix : prefixes.allPrefixes()) {
                try {
                    tripleHandler.receiveNamespace(prefix, prefixes.getNamespaceIRIFor(prefix), context);
                } catch (TripleHandlerException e) {
                    throw new RuntimeException(String.format(Locale.ROOT, "Error while writing namespace %s", prefix),
                            e);
                }
            }
        }
        if (isClosed) {
            throw new IllegalStateException("Not open: " + context);
        }
    }

    @Override
    public void addResourceRoot(String[] path, Resource root, Class<? extends MicroformatExtractor> extractor) {
        if (resourceRoots == null) {
            resourceRoots = new ArrayList<>();
        }
        resourceRoots.add(new ResourceRoot(path, root, extractor));
    }

    @Override
    public List<ResourceRoot> getResourceRoots() {
        List<ResourceRoot> allRoots = new ArrayList<>();
        if (resourceRoots != null) {
            allRoots.addAll(resourceRoots);
        }
        for (ExtractionResult er : subResults) {
            ExtractionResultImpl eri = (ExtractionResultImpl) er;
            if (eri.resourceRoots != null) {
                allRoots.addAll(eri.resourceRoots);
            }
        }
        return allRoots;
    }

    @Override
    public void addPropertyPath(Class<? extends MicroformatExtractor> extractor, Resource propertySubject,
            Resource property, BNode object, String[] path) {
        if (propertyPaths == null) {
            propertyPaths = new ArrayList<>();
        }
        propertyPaths.add(new PropertyPath(path, propertySubject, property, object, extractor));
    }

    @Override
    public List<PropertyPath> getPropertyPaths() {
        List<PropertyPath> allPaths = new ArrayList<>();
        if (propertyPaths != null) {
            allPaths.addAll(propertyPaths);
        }
        for (ExtractionResult er : subResults) {
            ExtractionResultImpl eri = (ExtractionResultImpl) er;
            if (eri.propertyPaths != null) {
                allPaths.addAll(eri.propertyPaths);
            }
        }
        return allPaths;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(context.toString());
        sb.append('\n');
        if (issues != null) {
            sb.append("Errors {\n");
            for (Issue issue : issues) {
                sb.append('\t');
                sb.append(issue.toString());
                sb.append('\n');
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

}
