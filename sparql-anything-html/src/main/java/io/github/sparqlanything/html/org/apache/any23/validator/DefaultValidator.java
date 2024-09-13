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

import io.github.sparqlanything.html.org.apache.any23.validator.*;
import io.github.sparqlanything.html.org.apache.any23.validator.DOMDocument;
import io.github.sparqlanything.html.org.apache.any23.validator.Fix;
import io.github.sparqlanything.html.org.apache.any23.validator.Rule;
import io.github.sparqlanything.html.org.apache.any23.validator.RuleContext;
import io.github.sparqlanything.html.org.apache.any23.validator.Validator;
import io.github.sparqlanything.html.org.apache.any23.validator.rule.AboutNotURIRule;
import io.github.sparqlanything.html.org.apache.any23.validator.rule.MetaNameMisuseFix;
import io.github.sparqlanything.html.org.apache.any23.validator.rule.MetaNameMisuseRule;
import io.github.sparqlanything.html.org.apache.any23.validator.rule.MissingItemscopeAttributeValueFix;
import io.github.sparqlanything.html.org.apache.any23.validator.rule.MissingItemscopeAttributeValueRule;
import io.github.sparqlanything.html.org.apache.any23.validator.rule.MissingOpenGraphNamespaceRule;
import io.github.sparqlanything.html.org.apache.any23.validator.rule.OpenGraphNamespaceFix;
import org.w3c.dom.Document;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link io.github.sparqlanything.html.org.apache.any23.validator.Validator}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class DefaultValidator implements Validator {

    private Map<Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Rule>, List<Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Fix>>> rulesToFixes;

    private List<Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Rule>> rulesOrder;

    public DefaultValidator() {
        rulesToFixes = new HashMap<>();
        rulesOrder = new ArrayList<>();
        loadDefaultRules();
    }

    @Override
    public ValidationReport validate(DOMDocument document, boolean applyFix) throws ValidatorException {
        final ValidationReportBuilder validationReportBuilder = new DefaultValidationReportBuilder();
        for (Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Rule> cRule : rulesOrder) {
            io.github.sparqlanything.html.org.apache.any23.validator.Rule rule = newRuleInstance(cRule);
            @SuppressWarnings("rawtypes")
            final RuleContext ruleContext = new DefaultRuleContext();
            boolean applyOn;
            try {
                applyOn = rule.applyOn(document, ruleContext, validationReportBuilder);
            } catch (Exception e) {
                validationReportBuilder.reportRuleError(rule, e, "Error while processing rule.");
                continue;
            }
            if (applyFix && applyOn) {
                validationReportBuilder.traceRuleActivation(rule);
                List<Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Fix>> cFixes = getFixes(cRule);
                for (Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Fix> cFix : cFixes) {
                    io.github.sparqlanything.html.org.apache.any23.validator.Fix fix = newFixInstance(cFix);
                    try {
                        fix.execute(rule, ruleContext, document);
                    } catch (Exception e) {
                        validationReportBuilder.reportFixError(fix, e, "Error while processing fix.");
                    }
                }
            }
        }
        return validationReportBuilder.getReport();
    }

    @Override
    public ValidationReport validate(URI documentIRI, Document document, boolean applyFix) throws ValidatorException {
        return validate(new DefaultDOMDocument(documentIRI, document), applyFix);
    }

    @Override
    public synchronized void addRule(Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Rule> rule, Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Fix> fix) {
        List<Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Fix>> fixes = rulesToFixes.get(rule);
        if (fixes == null) {
            fixes = new ArrayList<>();
        }
        rulesOrder.add(rule);
        rulesToFixes.put(rule, fixes);
        if (fix != null) {
            fixes.add(fix);
        }
    }

    @Override
    public void addRule(Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Rule> rule) {
        addRule(rule, null);
    }

    @Override
    public synchronized void removeRule(Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Rule> rule) {
        rulesOrder.remove(rule);
        rulesToFixes.remove(rule);
    }

    @Override
    public List<Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Rule>> getAllRules() {
        return Collections.unmodifiableList(rulesOrder);
    }

    @Override
    public List<Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Fix>> getFixes(Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Rule> rule) {
        List<Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Fix>> fixes = rulesToFixes.get(rule);
        return fixes == null ? Collections.<Class<? extends io.github.sparqlanything.html.org.apache.any23.validator.Fix>> emptyList()
                : Collections.unmodifiableList(rulesToFixes.get(rule));
    }

    private void loadDefaultRules() {
        addRule(MetaNameMisuseRule.class, MetaNameMisuseFix.class);
        addRule(MissingOpenGraphNamespaceRule.class, OpenGraphNamespaceFix.class);
        addRule(AboutNotURIRule.class);
        addRule(MissingItemscopeAttributeValueRule.class, MissingItemscopeAttributeValueFix.class);
    }

    private io.github.sparqlanything.html.org.apache.any23.validator.Fix newFixInstance(Class<? extends Fix> cFix) throws ValidatorException {
        try {
            return cFix.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ValidatorException("An error occurred while instantiating a fix.", e);
        }
    }

    private io.github.sparqlanything.html.org.apache.any23.validator.Rule newRuleInstance(Class<? extends Rule> cRule) throws ValidatorException {
        try {
            return cRule.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ValidatorException("An error occurred while instantiating a rule.", e);
        }
    }

}
