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

import io.github.sparqlanything.html.org.apache.any23.configuration.DefaultConfiguration;
import io.github.sparqlanything.html.org.apache.any23.extractor.Extractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorGroup;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorRegistry;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLMetaExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.extractor.rdfa.RDFa11ExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.extractor.rdfa.RDFaExtractorFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton class acting as a register for all the various {@link Extractor}.
 */
@SuppressWarnings("rawtypes")
public class ExtractorRegistryImpl extends
        org.eclipse.rdf4j.common.lang.service.ServiceRegistry<String, ExtractorFactory> implements ExtractorRegistry {

    /**
     * The instance.
     */
    private static ExtractorRegistry instance = null;

    /**
     * Public constructor for ExtractorRegistryImpl. Should normally call getInstance.
     */
    public ExtractorRegistryImpl() {
        super(ExtractorFactory.class);
    }

    /**
     * @return returns the {@link ExtractorRegistry} instance.
     */
    public static ExtractorRegistry getInstance() {
        // Thread-safe
        synchronized (ExtractorRegistry.class) {
            final DefaultConfiguration conf = DefaultConfiguration.singleton();
            if (instance == null) {
                instance = new ExtractorRegistryImpl();

                if (conf.getFlagProperty("any23.extraction.rdfa.programmatic")) {
                    instance.unregister(RDFaExtractorFactory.NAME);
                    // FIXME: Unregister RDFaExtractor if flag is not set
                    // instance.register(RDFa11Extractor.factory);
                } else {
                    instance.unregister(RDFa11ExtractorFactory.NAME);
                    // FIXME: Unregister RDFaExtractor if flag is set
                    // instance.register(RDFaExtractor.factory);
                }
                if (!conf.getFlagProperty("any23.extraction.head.meta")) {
                    instance.unregister(HTMLMetaExtractorFactory.NAME);
                    // FIXME: Unregister HTMLMetaExtractor if this flag is not set
                    // instance.register(HTMLMetaExtractor.factory);
                }
            }
        }
        return instance;
    }

    /**
     * Registers an {@link ExtractorFactory}.
     *
     * @param factory
     *            the {@link ExtractorFactory} to register
     *
     * @throws IllegalArgumentException
     *             if trying to register a {@link ExtractorFactory} with a that already exists in the registry.
     */
    @Override
    public void register(ExtractorFactory<?> factory) {
        this.add(factory);
    }

    /**
     * Unregisters the {@link ExtractorFactory} with the given name.
     *
     * @param name
     *            The name of the ExtractorFactory to unregister.
     */
    @Override
    public void unregister(String name) {
        if (this.has(name)) {
            this.remove(this.get(name).get());
        }
    }

    /**
     *
     * Retrieves a {@link ExtractorFactory} given its name
     *
     * @param name
     *            of the desired factory
     *
     * @return the {@link ExtractorFactory} associated to the provided name
     *
     * @throws IllegalArgumentException
     *             if there is not a {@link ExtractorFactory} associated to the provided name.
     */
    @Override
    public ExtractorFactory<?> getFactory(final String name) {
        return this.get(name).orElseThrow(() -> new IllegalArgumentException("Unregistered extractor name: " + name));
    }

    /**
     * @return an {@link ExtractorGroup} with all the registered {@link Extractor}.
     */
    @Override
    public ExtractorGroup getExtractorGroup() {
        return getExtractorGroup(getAllNames());
    }

    /**
     * Returns an {@link ExtractorGroup} containing the {@link ExtractorFactory} mathing the names provided as input.
     *
     * @param names
     *            a {@link List} containing the names of the desired {@link ExtractorFactory}.
     *
     * @return the extraction group.
     */
    @Override
    public ExtractorGroup getExtractorGroup(List<String> names) {
        List<ExtractorFactory<?>> members = new ArrayList<>(names.size());
        for (String name : names) {
            members.add(getFactory(name));
        }
        return new ExtractorGroup(members);
    }

    /**
     *
     * @param name
     *            of the {@link ExtractorFactory}
     *
     * @return <code>true</code> if is there a {@link ExtractorFactory} associated to the provided name.
     */
    @Override
    public boolean isRegisteredName(String name) {
        return this.has(name);
    }

    /**
     * Returns the names of all registered extractors, sorted alphabetically.
     */
    @Override
    public List<String> getAllNames() {
        List<String> result = new ArrayList<>(this.getKeys());
        Collections.sort(result);
        return result;
    }

    @Override
    protected String getKey(ExtractorFactory service) {
        return service.getExtractorName();
    }

}
