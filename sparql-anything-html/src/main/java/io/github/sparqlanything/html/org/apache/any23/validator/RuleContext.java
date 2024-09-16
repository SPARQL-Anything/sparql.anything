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

package io.github.sparqlanything.html.org.apache.any23.validator;

import io.github.sparqlanything.html.org.apache.any23.validator.Fix;
import io.github.sparqlanything.html.org.apache.any23.validator.Rule;

/**
 * This context is used to pass data from a {@link io.github.sparqlanything.html.org.apache.any23.validator.Rule} to a {@link Fix}.
 *
 * @see Rule
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public interface RuleContext<T> {

    /**
     * Puts a data within the context.
     *
     * @param name
     *            rule key
     * @param value
     *            rule value
     */
    void putData(String name, T value);

    /**
     * Retrieves a registered object.
     *
     * @param name
     *            rule key
     *
     * @return a registered object, <code>null</code> if not found.
     */
    T getData(String name);

    /**
     * Removes a data from the context.
     *
     * @param name
     *            remove entry with this name
     */
    void removeData(String name);

}
