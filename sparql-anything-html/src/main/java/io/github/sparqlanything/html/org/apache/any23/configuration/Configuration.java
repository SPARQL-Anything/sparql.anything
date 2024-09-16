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

/**
 * Defines the main <i>Any23</i> configuration.
 */
public interface Configuration {

    /**
     * Returns all the defined configuration properties.
     *
     * @return list of defined properties.
     */
    String[] getProperties();

    /**
     * Checks whether a property is defined or not in configuration.
     *
     * @param propertyName
     *            name of property to check.
     *
     * @return <code>true</code> if defined, <code>false</code> otherwise.
     */
    boolean defineProperty(String propertyName);

    /**
     * Returns the value of a specified property, of the default value if property is not defined.
     *
     * @param propertyName
     *            name of property
     * @param defaultValue
     *            default value if not found.
     *
     * @return the value associated to <i>propertyName</i>.
     */
    String getProperty(String propertyName, String defaultValue);

    /**
     * Returns the value of the specified <code>propertyName</code> or raises an exception if <code>propertyName</code>
     * is not defined.
     *
     * @param propertyName
     *            name of property to be returned.
     *
     * @return property value.
     *
     * @throws IllegalArgumentException
     *             if the property name is not defined or the found property value is blank or empty.
     */
    String getPropertyOrFail(String propertyName);

    /**
     * Returns the {@link Integer} value of the specified <code>propertyName</code> or raises an exception if
     * <code>propertyName</code> is not defined.
     *
     * @param propertyName
     *            name of property to be returned.
     *
     * @return property value.
     *
     * @throws NullPointerException
     *             if the property name is not defined.
     * @throws IllegalArgumentException
     *             if the found property value is blank or empty.
     * @throws NumberFormatException
     *             if the found property value is not a valid {@link Integer}.
     */
    int getPropertyIntOrFail(String propertyName);

    /**
     * Returns the value of a <i> flag property</i>. Such properties can assume only two values:
     * <ul>
     * <li><code>on</code> if flag is active (<code>true</code> is returned).
     * <li><code>off</code> if flag is inactive (<code>false</code> is returned).
     * </ul>
     *
     * @param propertyName
     *            name of property flag.
     *
     * @return <code>true</code> for <code>on</code>, <code>false</code> for <code>off</code>.
     *
     * @throws IllegalArgumentException
     *             if the <code>propertyName</code> is not declared.
     */
    boolean getFlagProperty(final String propertyName);

    /**
     * Returns a human readable string containing the configuration dump.
     *
     * @return a string describing the configuration options.
     */
    String getConfigurationDump();

}
