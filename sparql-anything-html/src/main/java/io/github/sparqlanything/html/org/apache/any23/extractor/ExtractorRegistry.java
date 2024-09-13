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

import java.util.List;

/**
 * An interface to the enable a registry for extractors to be implemented by different implementors of this API.
 *
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface ExtractorRegistry {

    /**
     * Registers an {@link ExtractorFactory}.
     *
     * @param factory
     *            an {@link ExtractorFactory} to register.
     *
     * @throws IllegalArgumentException
     *             if trying to register a {@link ExtractorFactory} that already exists in the registry.
     */
    void register(ExtractorFactory<?> factory);

    /**
     *
     * Retrieves a {@link ExtractorFactory} given its name
     *
     * @param name
     *            The name of the desired factory
     *
     * @return The {@link ExtractorFactory} associated to the provided name
     *
     * @throws IllegalArgumentException
     *             If there is not an {@link ExtractorFactory} associated to the provided name.
     */
    ExtractorFactory<?> getFactory(String name);

    /**
     * @return An {@link ExtractorGroup} with all the registered {@link Extractor}.
     */
    ExtractorGroup getExtractorGroup();

    /**
     * Returns an {@link ExtractorGroup} containing the {@link ExtractorFactory} mathing the names provided as input.
     *
     * @param names
     *            A {@link List} containing the names of the desired {@link ExtractorFactory}.
     *
     * @return the extraction group.
     */
    ExtractorGroup getExtractorGroup(List<String> names);

    /**
     *
     * @param name
     *            The name of the {@link ExtractorFactory}
     *
     * @return <code>true</code> if is there a {@link ExtractorFactory} associated to the provided name.
     */
    boolean isRegisteredName(String name);

    /**
     * Returns the names of all registered extractors, sorted alphabetically.
     *
     * @return an alphabetically sorted {@link List}
     */
    List<String> getAllNames();

    void unregister(String name);

}
