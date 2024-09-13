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

package io.github.sparqlanything.html.org.apache.any23.configuration;

import java.util.Properties;

/**
 * Default implementation of {@link ModifiableConfiguration}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultModifiableConfiguration extends DefaultConfiguration implements ModifiableConfiguration {

    protected DefaultModifiableConfiguration(Properties properties) {
        super(properties);
    }

    @Override
    public synchronized String setProperty(String propertyName, String propertyValue) {
        if (!defineProperty(propertyName))
            throw new IllegalArgumentException(String.format(java.util.Locale.ROOT,
                    "Property '%s' is not defined in configuration.", propertyName));
        return (String) properties.setProperty(propertyName, propertyValue);
    }

}
