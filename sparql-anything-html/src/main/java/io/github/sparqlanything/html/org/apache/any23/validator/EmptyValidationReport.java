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

import io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidationReportBuilder;
import io.github.sparqlanything.html.org.apache.any23.validator.XMLValidationReportSerializer;

import java.util.Collections;
import java.util.List;

/**
 * An implementation of {@link ValidationReportBuilder} with no data.
 *
 * @author Davide Palmisano (palmisano@fbk.eu)
 * @author Michele Mostarda (mostarda@fbk.eu)
 */

@XMLValidationReportSerializer.NodeName("validationReport")
public class EmptyValidationReport implements ValidationReport {

    private static final EmptyValidationReport INSTANCE = new EmptyValidationReport();

    private static final List<Issue> EMPTY_ISSUES = Collections.emptyList();
    private static final List<RuleActivation> EMPTY_RULE_ACTIVATIONS = Collections.emptyList();
    private static final List<Error> EMPTY_ERRORS = Collections.emptyList();

    public static EmptyValidationReport getInstance() {
        return INSTANCE;
    }

    private EmptyValidationReport() {
    }

    public List<Issue> getIssues() {
        return EMPTY_ISSUES;
    }

    public List<RuleActivation> getRuleActivations() {
        return EMPTY_RULE_ACTIVATIONS;
    }

    public List<Error> getErrors() {
        return EMPTY_ERRORS;
    }

    @Override
    public String toString() {
        return "Validation report is empty.";
    }
}
