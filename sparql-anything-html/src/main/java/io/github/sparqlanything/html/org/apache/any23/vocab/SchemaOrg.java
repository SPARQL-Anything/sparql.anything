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

package io.github.sparqlanything.html.org.apache.any23.vocab;

/**
 * Vocabulary definition for <a href="http://schema.org/">schema.org</a>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class SchemaOrg extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://schema.org/";

    private static SchemaOrg instance;

    public static SchemaOrg getInstance() {
        if (instance == null) {
            instance = new SchemaOrg();
        }
        return instance;
    }

    private SchemaOrg() {
        super(NS);
    }

}
