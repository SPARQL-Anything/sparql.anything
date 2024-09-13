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

import io.github.sparqlanything.html.org.apache.any23.mime.MIMEType;

import java.util.Collection;

/**
 * Interface defining a factory for {@link Extractor}.
 *
 * @param <T>
 *            the type of the {@link Extractor} to be created by this factory.
 */
public interface ExtractorFactory<T extends Extractor<?>> extends ExtractorDescription {

    /**
     * Creates an extractor instance.
     *
     * @return an instance of the extractor associated to this factory.
     */
    T createExtractor();

    /**
     * Supports wildcards, e.g. <code>"*&#47;*"</code> for blind extractors that merely call a web service.
     *
     * @return a {@link Collection} of supported mimetypes.
     */
    Collection<MIMEType> getSupportedMIMETypes();

    /**
     * An example input file for the extractor, to be used in auto-generated documentation. For the
     * {@link Extractor.BlindExtractor}, this is an arbitrary IRI. For extractors that require content, it is the name
     * of a file, relative to the factory's class file's location, it will be opened using
     * factory.getClass().getResourceAsStream(filename). The example should be a short file that produces characteristic
     * output if sent through the extractor. The file will be read as UTF-8, so it should either use that encoding or
     * avoid characters outside of the US-ASCII range.
     *
     * @return a string representing sample input for a particular extractor.
     */
    String getExampleInput();
}
