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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import io.github.sparqlanything.html.org.apache.any23.configuration.Settings;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry class for {@link WriterFactory}s.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Hans Brende (hansbrende@apache.org)
 */
public class WriterFactoryRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(WriterFactoryRegistry.class);

    /**
     * Singleton instance.
     */
    private static class InstanceHolder {
        private static final WriterFactoryRegistry instance = new WriterFactoryRegistry();
    }

    private static final WriterFactory[] EMPTY_WRITERS = new WriterFactory[0];

    /**
     * List of registered writers.
     */
    private final List<WriterFactory> writers = new CopyOnWriteArrayList<>();

    /**
     * MIME Type to {@link WriterFactory} class.
     */
    private final Map<String, List<WriterFactory>> mimeToWriter = Collections.synchronizedMap(new HashMap<>());

    /**
     * Identifier to {@link WriterFactory} class.
     */
    private final Map<String, WriterFactory> idToWriter = new HashMap<>();

    private final List<String> identifiers = new CopyOnWriteArrayList<>();

    private final Collection<String> mimeTypes = new CopyOnWriteArraySet<>();

    public WriterFactoryRegistry() {
        ServiceLoader<WriterFactory> serviceLoader = ServiceLoader.load(WriterFactory.class,
                this.getClass().getClassLoader());

        Iterator<WriterFactory> iterator = serviceLoader.iterator();

        // use while(true) loop so that we can isolate all service loader errors from .next and .hasNext to a single
        // service

        ArrayList<WriterFactory> factories = new ArrayList<>();
        while (true) {
            try {
                if (!iterator.hasNext())
                    break;
                factories.add(iterator.next());
            } catch (ServiceConfigurationError error) {
                LOG.error("Found error loading a WriterFactory", error);
            }
        }

        registerAll(factories.toArray(EMPTY_WRITERS));
    }

    /**
     * Reads the identifier specified for the given {@link WriterFactory}.
     *
     * @param writerClass
     *            writer class.
     *
     * @return identifier.
     */
    public static String getIdentifier(WriterFactory writerClass) {
        return writerClass.getIdentifier();
    }

    /**
     * Reads the <i>MIME Type</i> specified for the given {@link WriterFactory}.
     *
     * @param writerClass
     *            writer class.
     *
     * @return MIME type.
     */
    public static String getMimeType(WriterFactory writerClass) {
        if (writerClass instanceof TripleWriterFactory) {
            return ((TripleWriterFactory) writerClass).getTripleFormat().getMimeType();
        } else if (writerClass instanceof DecoratingWriterFactory) {
            return null;
        } else {
            return reportAndGetCompatFormat(writerClass).getMimeType();
        }
    }

    /**
     * @return the {@link WriterFactoryRegistry} singleton instance.
     */
    public static WriterFactoryRegistry getInstance() {
        return InstanceHolder.instance;
    }

    @SuppressWarnings("deprecation")
    private static TripleFormat reportAndGetCompatFormat(WriterFactory f) {
        LOG.warn("{} must implement either {} or {}.", f.getClass(), TripleWriterFactory.class,
                DecoratingWriterFactory.class);
        final String mimeType = f.getMimeType();
        RDFFormat fmt;
        try {
            fmt = f.getRdfFormat();
        } catch (RuntimeException e) {
            return TripleFormat.of(mimeType, Collections.singleton(mimeType), null, Collections.emptySet(), null,
                    TripleFormat.NONSTANDARD);
        }
        if (mimeType == null || fmt.hasDefaultMIMEType(mimeType)) {
            return TripleFormat.of(fmt);
        }
        // override default MIME type on mismatch
        return TripleFormat.of(fmt.getName(), Collections.singleton(mimeType), fmt.getCharset(),
                fmt.getFileExtensions(), fmt.getStandardURI().stringValue(), TripleFormat.capabilities(fmt));
    }

    private static TripleWriterFactory getCompatFactory(WriterFactory f) {
        final TripleFormat format = reportAndGetCompatFormat(f);
        return new TripleWriterFactory() {
            @Override
            public TripleFormat getTripleFormat() {
                return format;
            }

            @Override
            @SuppressWarnings("deprecation")
            public TripleHandler getTripleWriter(OutputStream os, Settings settings) {
                return f.getRdfWriter(os);
            }

            @Override
            public Settings getSupportedSettings() {
                return Settings.of();
            }

            @Override
            public String getIdentifier() {
                return f.getIdentifier();
            }
        };
    }

    /**
     * Registers a new {@link WriterFactory} to the registry.
     *
     * @param f
     *            the writer factory to be registered.
     *
     * @throws IllegalArgumentException
     *             if the id or the mimetype are null or empty strings or if the identifier has been already defined.
     */
    public void register(WriterFactory f) {
        if (f == null)
            throw new NullPointerException("writerClass cannot be null.");
        registerAll(new WriterFactory[] { f });
    }

    private void registerAll(WriterFactory[] factories) {
        final int count = factories.length;
        if (count == 0) {
            return;
        }
        final HashMap<String, ArrayList<WriterFactory>> mimes = new HashMap<>();
        final String[] ids = new String[count];

        for (int i = 0; i < count; i++) {
            WriterFactory f = factories[i];
            if (!(f instanceof BaseWriterFactory<?>)) {
                // backwards compatibility: view vanilla WriterFactory as TripleWriterFactory
                f = factories[i] = getCompatFactory(f);
            }
            final String id = ids[i] = f.getIdentifier();
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid identifier returned by writer " + f);
            }
            if (f instanceof TripleWriterFactory) {
                String mimeType = ((TripleWriterFactory) f).getTripleFormat().getMimeType();
                if (mimeType == null || mimeType.trim().isEmpty()) {
                    throw new IllegalArgumentException("Invalid MIME type returned by writer " + f);
                }
                mimes.computeIfAbsent(mimeType, k -> new ArrayList<>()).add(f);
            }
        }

        final List<String> idList = Arrays.asList(ids);
        final List<WriterFactory> factoryList = Arrays.asList(factories);
        final Map<String, WriterFactory> idToWriter;
        synchronized (idToWriter = this.idToWriter) {
            for (int i = 0; i < count; i++) {
                String id = ids[i];
                if (idToWriter.putIfAbsent(id, factories[i]) != null) {
                    idToWriter.keySet().removeAll(idList.subList(0, i));
                    throw new IllegalArgumentException("The writer identifier is already declared: " + id);
                }
            }
        }
        // add in bulk to reduce writes to CopyOnWriteArrayList
        writers.addAll(factoryList);
        identifiers.addAll(idList);
        for (Map.Entry<String, ArrayList<WriterFactory>> entry : mimes.entrySet()) {
            String mimeType = entry.getKey();
            mimeTypes.add(mimeType);
            mimeToWriter.computeIfAbsent(mimeType, k -> new CopyOnWriteArrayList<>()).addAll(entry.getValue());
        }
    }

    /**
     * Verifies if a {@link WriterFactory} with given <code>id</code> identifier has been registered.
     *
     * @param id
     *            identifier.
     *
     * @return <code>true</code> if the identifier has been registered, <code>false</code> otherwise.
     */
    public boolean hasIdentifier(String id) {
        synchronized (idToWriter) {
            return idToWriter.containsKey(id);
        }
    }

    /**
     * @return the list of all the specified identifiers.
     */
    public List<String> getIdentifiers() {
        // no synchronized block needed for CopyOnWriteArrayList
        return Collections.unmodifiableList(identifiers);
    }

    /**
     * @return the list of MIME types covered by the registered {@link WriterFactory} instances.
     */
    public Collection<String> getMimeTypes() {
        // no synchronized block needed for CopyOnWriteArraySet
        return Collections.unmodifiableCollection(mimeTypes);
    }

    /**
     * @return the list of all the registered {@link WriterFactory} instances.
     */
    public List<WriterFactory> getWriters() {
        // no synchronized block needed for CopyOnWriteArrayList
        return Collections.unmodifiableList(writers);
    }

    /**
     * Returns the {@link WriterFactory} identified by <code>id</code>.
     *
     * @param id
     *            the writer identifier.
     *
     * @return the {@link WriterFactory} matching the <code>id</code> or <code>null</code> if not found.
     */
    public WriterFactory getWriterByIdentifier(String id) {
        synchronized (idToWriter) {
            return idToWriter.get(id);
        }
    }

    /**
     * Returns all the writers matching the specified <code>mimeType</code>.
     *
     * @param mimeType
     *            a MIMEType.
     *
     * @return a list of matching writers or an empty list.
     */
    public Collection<WriterFactory> getWritersByMimeType(String mimeType) {
        // no synchronized block needed for synchronized map
        // return CopyOnWriteArrayList to avoid ConcurrentModificationExceptions on iteration
        List<WriterFactory> list = mimeToWriter.get(mimeType);
        return list != null ? Collections.unmodifiableList(list) : Collections.emptyList();
    }

    /**
     * Returns an instance of {@link FormatWriter} ready to write on the given {@link OutputStream}.
     *
     * @param id
     *            the identifier of the {@link FormatWriter} to instantiate.
     * @param os
     *            the output stream.
     *
     * @return the not <code>null</code> {@link FormatWriter} instance.
     *
     * @throws NullPointerException
     *             if the <code>id</code> doesn't match any registered writer.
     *
     * @deprecated since 2.3. Use {@link #getWriterByIdentifier(String)} in combination with
     *             {@link TripleWriterFactory#getTripleWriter(OutputStream, Settings)} instead.
     */
    @Deprecated
    public FormatWriter getWriterInstanceByIdentifier(String id, OutputStream os) {
        return Objects.requireNonNull(getWriterByIdentifier(id), "Cannot find writer with id " + id).getRdfWriter(os);
    }

}
