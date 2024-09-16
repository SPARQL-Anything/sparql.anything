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

import io.github.sparqlanything.html.org.apache.any23.configuration.Settings;

/**
 * Base interface used for constructors of decorating {@link TripleHandler} implementations.
 *
 * @author Hans Brende (hansbrende@apache.org)
 */
public interface DecoratingWriterFactory extends BaseWriterFactory<TripleHandler> {

    /**
     *
     * @return the settings supported by handlers produced by this factory
     */
    @Override
    Settings getSupportedSettings();

    /**
     * @param delegate
     *            the {@link TripleWriter} to delegate input to
     * @param settings
     *            the settings with which to configure the returned handler
     *
     * @return a {@link TripleHandler} which writes to the specified delegate
     *
     * @throws NullPointerException
     *             if the delegate or settings is null
     * @throws IllegalArgumentException
     *             if the settings are not correctly configured
     */
    @Override
    TripleHandler getTripleWriter(TripleHandler delegate, Settings settings);

}
