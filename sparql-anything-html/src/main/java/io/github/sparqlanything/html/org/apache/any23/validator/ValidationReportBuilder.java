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
import io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport;
import org.w3c.dom.Node;

/**
 * The report interface is used to generate diagnostics about validation.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public interface ValidationReportBuilder {

    /**
     * @return Returns the validation report.
     */
    io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport getReport();

    /**
     * Reports an issue detected on a specified node.
     *
     * @param issueLevel
     *            issue level classifier.
     * @param message
     *            human readable message connected to the issue.
     * @param n
     *            the node affected by the issue.
     */
    void reportIssue(io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport.IssueLevel issueLevel, String message, Node n);

    /**
     * Reports a detected issue.
     *
     * @param issueLevel
     *            issue level classifier.
     * @param message
     *            human readable message connected to the issue.
     */
    void reportIssue(ValidationReport.IssueLevel issueLevel, String message);

    /**
     * Traces that a rule has been applied.
     *
     * @param r
     *            activated rule.
     */
    void traceRuleActivation(io.github.sparqlanything.html.org.apache.any23.validator.Rule r);

    /**
     * Reports an error occurred while executing a {@link io.github.sparqlanything.html.org.apache.any23.validator.Rule}.
     *
     * @param r
     *            rule originating the error.
     * @param e
     *            exception raised.
     * @param msg
     *            human readable message.
     */
    void reportRuleError(Rule r, Exception e, String msg);

    /**
     * Reports an error occurred while executing a {@link io.github.sparqlanything.html.org.apache.any23.validator.Fix}.
     *
     * @param f
     *            fix originating the error.
     * @param e
     *            exception raised.
     * @param msg
     *            human readable message.
     */
    void reportFixError(Fix f, Exception e, String msg);

}
