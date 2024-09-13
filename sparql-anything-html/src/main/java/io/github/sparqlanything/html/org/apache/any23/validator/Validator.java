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

import io.github.sparqlanything.html.org.apache.any23.validator.DOMDocument;
import io.github.sparqlanything.html.org.apache.any23.validator.Fix;
import io.github.sparqlanything.html.org.apache.any23.validator.Rule;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidatorException;
import org.w3c.dom.Document;

import java.net.URI;
import java.util.List;

/**
 * The validator class allows to perform validation - correction of related to <i>HTML</i> {@link Document}
 * instances.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public interface Validator {

    /**
     * Performs a validation - fixing of the provided document.
     *
     * @param document
     *            the {@link DOMDocument} instance wrapping the original <i>HTML</i> document.
     * @param applyFix
     *            if <code>true</code> tries to fix the document.
     *
     * @return a report of the detected issues.
     *
     * @throws ValidatorException
     *             if an error occurs during the validation process.
     */
    ValidationReport validate(DOMDocument document, boolean applyFix) throws ValidatorException;

    /**
     * Performs a validation - fixing of the provided document.
     *
     * @param documentIRI
     *            the document source IRI.
     * @param document
     *            the original <i>HTML</i> document.
     * @param applyFix
     *            if <code>true</code> tries to fix the document.
     *
     * @return a report of the detected issues.
     *
     * @throws ValidatorException
     *             if an error occurs during the validation process.
     */
    ValidationReport validate(URI documentIRI, Document document, boolean applyFix) throws ValidatorException;

    /**
     * Allows to register a new rule to this validator
     *
     * @param rule
     *            add a configured {@link io.github.sparqlanything.html.org.apache.any23.validator.Rule}
     */
    void addRule(Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Rule> rule);

    /**
     * Allows to register a new rule to this validator and associating it to a fix.
     *
     * @param rule
     *            add a configured {@link io.github.sparqlanything.html.org.apache.any23.validator.Rule}
     * @param fix
     *            add a configured {@link io.github.sparqlanything.html.org.apache.any23.validator.Fix} for the rule
     */
    void addRule(Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Rule> rule, Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Fix> fix);

    /**
     * Allows to remove a rule from the validator and all the related {@link io.github.sparqlanything.html.org.apache.any23.validator.Fix}es.
     *
     * @param rule
     *            {@link io.github.sparqlanything.html.org.apache.any23.validator.Rule} to remove
     */
    void removeRule(Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Rule> rule);

    /**
     * Returns all the registered rules.
     *
     * @return a not null list of rules.
     */
    List<Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Rule>> getAllRules();

    /**
     * Returns all fixes registered for the give rule.
     *
     * @param rule
     *            {@link io.github.sparqlanything.html.org.apache.any23.validator.Rule} to obtain fixes for.
     *
     * @return a not null list of fixes.
     */
    List<Class<? extends Fix>> getFixes(Class<? extends Rule> rule);

}
