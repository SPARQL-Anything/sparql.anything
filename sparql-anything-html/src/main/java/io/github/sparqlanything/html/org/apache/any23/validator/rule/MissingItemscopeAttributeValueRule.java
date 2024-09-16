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

import java.util.List;

import io.github.sparqlanything.html.org.apache.any23.extractor.html.DomUtils;
import io.github.sparqlanything.html.org.apache.any23.validator.DOMDocument;
import io.github.sparqlanything.html.org.apache.any23.validator.Rule;
import io.github.sparqlanything.html.org.apache.any23.validator.RuleContext;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidationReportBuilder;
import org.w3c.dom.Node;

/**
 * This fixes missing attribute values for the 'itemscope' attribute Typically when such a snippet of XHTML is fed
 * through the {@link io.github.sparqlanything.html.org.apache.any23.extractor.rdfa.RDFa11Extractor}, and subsequently to Sesame's SesameRDFaParser,
 * it will result in the following behavior.
 *
 * <pre>
 * {@code
 * [Fatal Error] :23:15: Attribute name "itemscope" associated with an element type "div" must be followed by the ' = ' character.
 * }
 * </pre>
 *
 * This Rule identifies that happening.
 *
 */
public class MissingItemscopeAttributeValueRule implements Rule {

    /**
     * Default constructor
     */
    public MissingItemscopeAttributeValueRule() {
        // default costructor
    }

    @Override
    public String getHRName() {
        return "missing-itemscope-value-rule";
    }

    /**
     * @see Rule#applyOn(DOMDocument,
     *      RuleContext, ValidationReportBuilder)
     */
    @Override
    public boolean applyOn(DOMDocument document, @SuppressWarnings("rawtypes") RuleContext context,
            ValidationReportBuilder validationReportBuilder) {
        List<Node> itemNodes = document.getNodesWithAttribute("itemscope");
        boolean foundPrecondition = false;
        String propertyNode;
        Node iNode = null;
        for (Node itemNode : itemNodes) {
            iNode = itemNode;
            propertyNode = iNode.getAttributes().getNamedItem("itemscope").getNodeValue();
            if (propertyNode == null || propertyNode.contentEquals("")) {
                foundPrecondition = true;
                break;
            }
        }
        if (foundPrecondition) {
            validationReportBuilder.reportIssue(ValidationReport.IssueLevel.ERROR,
                    "Located absence of an accompanying value for the the 'itemscope' attribute of element with hashcode: "
                            + iNode.hashCode(),
                    iNode);
            return true;
        }
        return false;
    }

}
