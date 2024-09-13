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

import org.eclipse.rdf4j.model.IRI;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;

/**
 * It defines the signature of a generic Extractor.
 *
 * @param <Input>
 *            the type of the input data to be processed.
 */
public interface Extractor<Input> {

    /**
     * This interface specializes an {@link Extractor} able to handle {@link java.net.URI} as input format. Use it if
     * you need to fetch a document before the extraction
     */
    public interface BlindExtractor extends Extractor<IRI> {
    }

    /**
     * This interface specializes an {@link Extractor} able to handle {@link InputStream} as input format.
     */
    public interface ContentExtractor extends Extractor<InputStream> {

        /**
         * If <code>true</code>, the extractor will stop at first parsing error, if<code>false</code> the extractor will
         * attempt to ignore all parsing errors.
         *
         * @param f
         *            tolerance flag.
         */
        void setStopAtFirstError(boolean f);

    }

    /**
     * This interface specializes an {@link Extractor} able to handle {@link Document} as input format.
     */
    public interface TagSoupDOMExtractor extends Extractor<Document> {
    }

    /**
     * Executes the extractor. Will be invoked only once, extractors are not reusable.
     *
     * @param extractionParameters
     *            the parameters to be applied during the extraction.
     * @param context
     *            The document context.
     * @param in
     *            The extractor input data.
     * @param out
     *            the collector for the extracted data.
     *
     * @throws IOException
     *             On error while reading from the input stream.
     * @throws ExtractionException
     *             On other error, such as parse errors.
     */
    void run(ExtractionParameters extractionParameters, ExtractionContext context, Input in, ExtractionResult out)
            throws IOException, ExtractionException;

    /**
     * Returns a {@link ExtractorDescription} of this extractor.
     *
     * @return the object representing the extractor description.
     */
    ExtractorDescription getDescription();

}
