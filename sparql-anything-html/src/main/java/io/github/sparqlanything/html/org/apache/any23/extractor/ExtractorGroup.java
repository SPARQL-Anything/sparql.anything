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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * It simple models a group of {@link ExtractorFactory} providing simple accessing methods.
 */
public class ExtractorGroup implements Iterable<ExtractorFactory<?>> {

    private final Collection<ExtractorFactory<?>> factories;

    public ExtractorGroup(Collection<ExtractorFactory<?>> factories) {
        this.factories = factories;
    }

    public boolean isEmpty() {
        return factories.isEmpty();
    }

    public int getNumOfExtractors() {
        return factories.size();
    }

    /**
     * Returns a {@link ExtractorGroup} with a set of {@link Extractor} able to process the provided mime type.
     *
     * @param mimeType
     *            to perform the selection.
     *
     * @return an {@link ExtractorGroup} able to process the provided mime type.
     */
    public ExtractorGroup filterByMIMEType(MIMEType mimeType) {
        // @@@ wildcards, q values
        Collection<ExtractorFactory<?>> matching = new ArrayList<>();
        for (ExtractorFactory<?> factory : factories) {
            if (supportsAllContentTypes(factory) || supports(factory, mimeType)) {
                matching.add(factory);
            }
        }
        return new ExtractorGroup(matching);
    }

    @Override
    public Iterator<ExtractorFactory<?>> iterator() {
        return factories.iterator();
    }

    /**
     * @return <code>true</code> if all the {@link Extractor} contained in the group supports all the content types.
     */
    public boolean allExtractorsSupportAllContentTypes() {
        for (ExtractorFactory<?> factory : factories) {
            if (!supportsAllContentTypes(factory))
                return false;
        }
        return true;
    }

    private boolean supportsAllContentTypes(ExtractorFactory<?> factory) {
        return factory.getSupportedMIMETypes().contains("*/*");
    }

    private boolean supports(ExtractorFactory<?> factory, MIMEType mimeType) {
        for (MIMEType supported : factory.getSupportedMIMETypes()) {
            if (supported.isAnyMajorType())
                return true;
            if (supported.isAnySubtype() && supported.getMajorType().equals(mimeType.getMajorType()))
                return true;
            if (supported.getFullType().equals(mimeType.getFullType()))
                return true;
        }
        return false;
    }

}
