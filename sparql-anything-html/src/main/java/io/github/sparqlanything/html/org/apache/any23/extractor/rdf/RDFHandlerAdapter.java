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

package io.github.sparqlanything.html.org.apache.any23.extractor.rdf;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;

/**
 * An RDFHandler that relays statements and prefix definitions to an {@link ExtractionResult}. Used to feed output from
 * Sesame's RDF parsers into Any23.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class RDFHandlerAdapter implements RDFHandler {

    private ExtractionResult target;

    public RDFHandlerAdapter(ExtractionResult target) {
        this.target = target;
    }

    public void startRDF() throws RDFHandlerException {
    }

    public void handleNamespace(String prefix, String uri) {
        target.writeNamespace(prefix, uri);
    }

    public void handleStatement(Statement stmt) {
        if (stmt != null) {
            final Resource context = stmt.getContext();
            if (context instanceof IRI) {
                target.writeTriple(stmt.getSubject(), stmt.getPredicate(), stmt.getObject(), (IRI) context);
            } else {
                target.writeTriple(stmt.getSubject(), stmt.getPredicate(), stmt.getObject());
            }
        }
    }

    public void handleComment(String comment) {
        // Empty.
    }

    public void endRDF() throws RDFHandlerException {
        // Empty.
    }

}
