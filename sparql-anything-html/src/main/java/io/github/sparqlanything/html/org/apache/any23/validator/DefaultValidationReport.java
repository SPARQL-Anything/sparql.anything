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
import io.github.sparqlanything.html.org.apache.any23.validator.XMLValidationReportSerializer;

import java.util.List;

/**
 * Default implementation of {@link io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@XMLValidationReportSerializer.NodeName("validationReport")
public class DefaultValidationReport implements ValidationReport {

    private final List<Issue> issues;
    private final List<RuleActivation> ruleActivations;
    private final List<Error> errors;

    public DefaultValidationReport(List<Issue> issues, List<RuleActivation> ruleActivations, List<Error> errors) {
        this.issues = issues;
        this.ruleActivations = ruleActivations;
        this.errors = errors;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public List<RuleActivation> getRuleActivations() {
        return ruleActivations;
    }

    public List<Error> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (Issue issue : issues) {
            sb.append(issue).append('\n');
        }
        for (RuleActivation ruleActivation : ruleActivations) {
            sb.append(ruleActivation).append('\n');
        }
        for (Error error : errors) {
            sb.append(error).append('\n');
        }
        return sb.toString();
    }
}
