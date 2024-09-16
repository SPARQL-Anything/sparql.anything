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

package io.github.sparqlanything.html.org.apache.any23.extractor.rdfa;

import io.github.sparqlanything.html.org.apache.any23.extractor.rdfa.RDFa11Parser;

/**
 * Exception class raised by {@link RDFa11Parser}.
 *
 * @deprecated since 2.3 the {@link org.eclipse.rdf4j.rio.Rio} implementations are used to parse RDFa.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@Deprecated
public class RDFa11ParserException extends Exception {

    public RDFa11ParserException(String message) {
        super(message);
    }

    public RDFa11ParserException(String message, Throwable cause) {
        super(message, cause);
    }

}
