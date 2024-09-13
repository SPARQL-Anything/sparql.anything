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

package io.github.sparqlanything.html.org.apache.any23.validator.rule;

import io.github.sparqlanything.html.org.apache.any23.validator.DOMDocument;
import io.github.sparqlanything.html.org.apache.any23.validator.Rule;
import io.github.sparqlanything.html.org.apache.any23.validator.RuleContext;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidationReportBuilder;
import io.github.sparqlanything.html.org.apache.any23.validator.rule.OpenGraphNamespaceFix;
import org.w3c.dom.Node;

import java.util.List;

/**
 * This rule detects the issue of missing Open Graph namespace.
 *
 * @see OpenGraphNamespaceFix
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class MissingOpenGraphNamespaceRule implements Rule {

    @Override
    public String getHRName() {
        return "missing-opengraph-namespace-rule";
    }

    @Override
    public boolean applyOn(DOMDocument document, @SuppressWarnings("rawtypes") RuleContext context,
            ValidationReportBuilder validationReportBuilder) {
        List<Node> metas = document.getNodes("/HTML/HEAD/META");
        boolean foundPrecondition = false;
        for (Node meta : metas) {
            Node propertyNode = meta.getAttributes().getNamedItem("property");
            if (propertyNode != null && propertyNode.getTextContent().indexOf("og:") == 0) {
                foundPrecondition = true;
                break;
            }
        }
        if (foundPrecondition) {
            Node htmlNode = document.getNode("/HTML");
            if (htmlNode.getAttributes().getNamedItem("xmlns:og") == null) {
                validationReportBuilder.reportIssue(ValidationReport.IssueLevel.ERROR,
                        "Missing OpenGraph namespace declaration.", htmlNode);
                return true;
            }
        }
        return false;
    }
}
