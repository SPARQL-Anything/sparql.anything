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
import org.w3c.dom.Node;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This rule is able to detect whether an about value is a valid URL or otherwise is a valid relative URL.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class AboutNotURIRule implements Rule {

    public static final String NODES_WITH_INVALID_ABOUT = "nodes-with-invalid-about";

    @Override
    public String getHRName() {
        return "about-not-uri-rule";
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean applyOn(DOMDocument document, @SuppressWarnings("rawtypes") RuleContext context,
            ValidationReportBuilder validationReportBuilder) {
        final List<Node> nodesWithAbout = document.getNodesWithAttribute("about");
        final List<Node> nodesWithInvalidAbout = new ArrayList<>();
        for (Node nodeWithAbout : nodesWithAbout) {
            if (!aboutIsValid(nodeWithAbout)) {
                validationReportBuilder.reportIssue(ValidationReport.IssueLevel.ERROR,
                        "Invalid about value for node, expected valid URL.", nodeWithAbout);
                nodesWithInvalidAbout.add(nodeWithAbout);
            }
        }
        if (nodesWithInvalidAbout.isEmpty()) {
            return false;
        }
        context.putData(NODES_WITH_INVALID_ABOUT, nodesWithInvalidAbout);
        return true;
    }

    private boolean aboutIsValid(Node n) {
        final String aboutContent = n.getAttributes().getNamedItem("about").getTextContent();
        if (isURL(aboutContent)) {
            return true;
        }
        final char firstChar = aboutContent.charAt(0);
        return firstChar == '#' || firstChar == '/';
    }

    private boolean isURL(String candidateIRIStr) {
        try {
            new URL(candidateIRIStr);
        } catch (MalformedURLException murle) {
            return false;
        }
        return true;
    }

}
