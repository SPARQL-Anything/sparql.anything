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

package io.github.sparqlanything.html.org.apache.any23.vocab;

import org.eclipse.rdf4j.model.IRI;

/**
 * The <a href="http://ogp.me/#type_book">Open Graph Protocol Book Type</a> vocabulary.
 */
public class OGPBook extends Vocabulary {

    private OGPBook() {
        super(NS);
    }

    public static final String NS = "http://ogp.me/ns/book#";

    /* BEGIN: http://ogp.me/#type_book */

    /** Who wrote this book. */
    public static final String BOOK__AUTHOR = "book:author";

    /** The ISBN */
    public static final String BOOK__ISBN = "book:isbn";

    /** The date the book was released. */
    public static final String BOOK__RELEASE_DATE = "book:release_date";

    /** Tag words associated with this book. */
    public static final String BOOK__TAG = "book:tag";

    /* END: http://ogp.me/#type_book */

    private static OGPBook instance;

    public static OGPBook getInstance() {
        if (instance == null) {
            instance = new OGPBook();
        }
        return instance;
    }

    public final IRI NAMESPACE = createIRI(NS);

    public final IRI bookAuthor = createProperty(BOOK__AUTHOR);
    public final IRI bookIsbn = createProperty(BOOK__ISBN);
    public final IRI bookReleaseDate = createProperty(BOOK__RELEASE_DATE);
    public final IRI bookTag = createProperty(BOOK__TAG);

    @SuppressWarnings("unused")
    private IRI createClass(String localName) {
        return createClass(NS, localName);
    }

    private IRI createProperty(String localName) {
        return createProperty(NS, localName);
    }

}
