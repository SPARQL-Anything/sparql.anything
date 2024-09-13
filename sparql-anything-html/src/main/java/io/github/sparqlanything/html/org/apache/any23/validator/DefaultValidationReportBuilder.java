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

import io.github.sparqlanything.html.org.apache.any23.validator.DefaultValidationReport;
import io.github.sparqlanything.html.org.apache.any23.validator.Fix;
import io.github.sparqlanything.html.org.apache.any23.validator.Rule;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidationReportBuilder;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of {@link io.github.sparqlanything.html.org.apache.any23.validator.ValidationReportBuilder}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class DefaultValidationReportBuilder implements ValidationReportBuilder {

    private List<io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport.Issue> issues;
    private List<io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport.RuleActivation> ruleActivations;
    private List<io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport.Error> errors;

    public DefaultValidationReportBuilder() {
        // default constructor
    }

    public io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport getReport() {
        return new DefaultValidationReport(issues == null ? Collections.<io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport.Issue> emptyList() : issues,
                ruleActivations == null ? Collections.<io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport.RuleActivation> emptyList() : ruleActivations,
                errors == null ? Collections.<io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport.Error> emptyList() : errors);
    }

    public void reportIssue(io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport.IssueLevel issueLevel, String message, Node n) {
        if (issues == null) {
            issues = new ArrayList<>();
        }
        issues.add(new io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport.Issue(issueLevel, message, n));
    }

    public void reportIssue(io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport.IssueLevel issueLevel, String message) {
        reportIssue(issueLevel, message, null);
    }

    public void traceRuleActivation(io.github.sparqlanything.html.org.apache.any23.validator.Rule r) {
        if (ruleActivations == null) {
            ruleActivations = new ArrayList<>();
        }
        ruleActivations.add(new io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport.RuleActivation(r));
    }

    public void reportRuleError(Rule r, Exception e, String msg) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(new io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport.RuleError(r, e, msg));
    }

    public void reportFixError(Fix f, Exception e, String msg) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(new io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport.FixError(f, e, msg));

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (ruleActivations != null) {
            sb.append("Rules {\n");
            for (io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport.RuleActivation ra : ruleActivations) {
                sb.append(ra).append('\n');
            }
            sb.append("}\n");
        }
        if (issues != null) {
            sb.append("Issues {\n");
            for (io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport.Issue issue : issues) {
                sb.append(issue.toString()).append('\n');
            }
            sb.append("}\n");
        }
        if (errors != null) {
            sb.append("Errors {\n");
            for (ValidationReport.Error error : errors) {
                sb.append(error.toString()).append('\n');
            }
            sb.append("}\n");
        }
        return sb.toString();
    }

}
