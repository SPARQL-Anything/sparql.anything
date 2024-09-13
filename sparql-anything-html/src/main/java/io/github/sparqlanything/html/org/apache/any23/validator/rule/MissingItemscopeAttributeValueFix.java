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
import io.github.sparqlanything.html.org.apache.any23.validator.Fix;
import io.github.sparqlanything.html.org.apache.any23.validator.Rule;
import io.github.sparqlanything.html.org.apache.any23.validator.RuleContext;
import org.w3c.dom.Node;

/**
 * Fix for the issue described within {@link io.github.sparqlanything.html.org.apache.any23.validator.rule.MissingItemscopeAttributeValueRule}
 */
public class MissingItemscopeAttributeValueFix implements Fix {

    private static final String EMPTY_ITEMSCOPE_VALUE = "itemscope";

    private static final String ITEMSCOPE = "itemscope";

    /**
     * Default constructor
     */
    public MissingItemscopeAttributeValueFix() {
        // default constructor
    }

    @Override
    public String getHRName() {
        return "missing-itemscope-value-fix";
    }

    @Override
    public void execute(Rule rule, @SuppressWarnings("rawtypes") RuleContext context, DOMDocument document) {

        List<Node> itemScopeContainerElements = document.getNodesWithAttribute(ITEMSCOPE);
        for (Node itemScopeContainerElement : itemScopeContainerElements) {
            Node newItemScopeContainerElement = itemScopeContainerElement;
            Node itemScopeNode = newItemScopeContainerElement.getAttributes().getNamedItem(ITEMSCOPE);
            if (itemScopeNode.getTextContent() == null || itemScopeNode.getTextContent() == "") {
                String node = DomUtils.getXPathForNode(itemScopeContainerElement);
                document.addAttribute(node, ITEMSCOPE, EMPTY_ITEMSCOPE_VALUE);
            }
        }
    }
}
