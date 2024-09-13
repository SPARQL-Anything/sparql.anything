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
 * The <a href="http://ogp.me/">Open Graph Protocol Article Type</a> vocabulary.
 */
public class OGPArticle extends Vocabulary {

    private OGPArticle() {
        super(NS);
    }

    public static final String NS = "http://ogp.me/ns/article#";

    /* BEGIN: http://ogp.me/#type_article */

    /** When the article was first published. */
    public static final String ARTICLE__PUBLISHED_TIME = "article:published_time";

    /** When the article was last changed. */
    public static final String ARTICLE__MODIFIED_TIME = "article:modified_time";

    /** When the article is out of date after. */
    public static final String ARTICLE__EXPIRATION_TIME = "article:expiration_time";

    /** Writers of the article. */
    public static final String ARTICLE__AUTHOR = "article:author";

    /** A high-level section name. E.g. Technology */
    public static final String ARTICLE__SECTION = "article:section";

    /** Tag words associated with this article. */
    public static final String ARTICLE__TAG = "article:tag";

    /* END: http://ogp.me/#type_article */

    private static OGPArticle instance;

    public static OGPArticle getInstance() {
        if (instance == null) {
            instance = new OGPArticle();
        }
        return instance;
    }

    public final IRI NAMESPACE = createIRI(NS);

    public final IRI articlePublishedTime = createProperty(ARTICLE__PUBLISHED_TIME);
    public final IRI articleModifiedTime = createProperty(ARTICLE__MODIFIED_TIME);
    public final IRI articleExpirationTime = createProperty(ARTICLE__EXPIRATION_TIME);
    public final IRI articleAuthor = createProperty(ARTICLE__AUTHOR);
    public final IRI articleSection = createProperty(ARTICLE__SECTION);
    public final IRI articleTag = createProperty(ARTICLE__TAG);

    @SuppressWarnings("unused")
    private IRI createClass(String localName) {
        return createClass(NS, localName);
    }

    private IRI createProperty(String localName) {
        return createProperty(NS, localName);
    }

}
