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

import io.github.sparqlanything.html.org.apache.any23.extractor.html.DomUtils;
import io.github.sparqlanything.html.org.apache.any23.validator.Fix;
import io.github.sparqlanything.html.org.apache.any23.validator.Rule;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidationReportBuilder;
import io.github.sparqlanything.html.org.apache.any23.validator.Validator;
import org.w3c.dom.Node;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * This class contains the report of a validation performed by the {@link io.github.sparqlanything.html.org.apache.any23.validator.Validator} class.
 *
 * @see Validator
 * @see ValidationReportBuilder
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
// TODO: merge with ErrorReporter
public interface ValidationReport extends Serializable {

    /**
     * Defines the different issue levels.
     */
    enum IssueLevel {
        ERROR, WARNING, INFO
    }

    /**
     * Returns the list of detected issues.
     *
     * @return list of detected issues.
     */
    List<Issue> getIssues();

    /**
     * Returns the list of activated rules.
     *
     * @return list of activated rules.
     */
    List<RuleActivation> getRuleActivations();

    /**
     * Returns the list of detected errors.
     *
     * @return list of detected errors.
     */
    List<Error> getErrors();

    /**
     * An issue found during the validation process.
     */
    class Issue implements Serializable {

        /**
        *
        */
        private static final long serialVersionUID = 1L;
        private final IssueLevel level;
        private final String message;
        private final transient Node origin;

        public Issue(IssueLevel level, String message, Node origin) {
            if (level == null) {
                throw new NullPointerException("level cannot be null.");
            }
            if (message == null) {
                throw new NullPointerException("message cannot be null.");
            }
            if (origin == null) {
                throw new NullPointerException("origin cannot be null.");
            }
            this.level = level;
            this.message = message;
            this.origin = origin;
        }

        public String getMessage() {
            return message;
        }

        public IssueLevel getLevel() {
            return level;
        }

        public Node getOrigin() {
            return origin;
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "Issue %s '%s' %s", level, message, DomUtils.getXPathForNode(origin));
        }
    }

    /**
     * This class describes the activation of a rule.
     */
    class RuleActivation implements Serializable {

        /**
        *
        */
        private static final long serialVersionUID = 1L;
        private final String ruleStr;

        public RuleActivation(io.github.sparqlanything.html.org.apache.any23.validator.Rule r) {
            if (r == null) {
                throw new NullPointerException("rule cannot be null.");
            }
            ruleStr = r.getHRName();
        }

        public String getRuleStr() {
            return ruleStr;
        }

        @Override
        public String toString() {
            return ruleStr;
        }
    }

    /**
     * An error occurred while performing the validation process.
     */
    abstract class Error implements Serializable {

        /**
        *
        */
        private static final long serialVersionUID = 1L;
        private final Exception cause;
        private final String message;

        public Error(Exception e, String msg) {
            if (e == null) {
                throw new NullPointerException("exception cannot be null.");
            }
            if (msg == null) {
                throw new NullPointerException("message cannot be null.");
            }
            cause = e;
            message = msg;
        }

        public Exception getCause() {
            return cause;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "%s %s %s", this.getClass().getName(), cause, message);
        }
    }

    /**
     * An error occurred while executing a rule.
     */
    class RuleError extends Error {

        /**
        *
        */
        private static final long serialVersionUID = 1L;
        private final io.github.sparqlanything.html.org.apache.any23.validator.Rule origin;

        public RuleError(io.github.sparqlanything.html.org.apache.any23.validator.Rule r, Exception e, String msg) {
            super(e, msg);
            if (r == null) {
                throw new NullPointerException("rule cannot be null.");
            }
            origin = r;
        }

        public Rule getOrigin() {
            return origin;
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "%s - %s", super.toString(), origin.getHRName());
        }
    }

    /**
     * An error occurred while executing a fix.
     */
    class FixError extends Error {

        /**
        *
        */
        private static final long serialVersionUID = 1L;
        private final io.github.sparqlanything.html.org.apache.any23.validator.Fix origin;

        public FixError(io.github.sparqlanything.html.org.apache.any23.validator.Fix f, Exception e, String msg) {
            super(e, msg);
            origin = f;
        }

        public Fix getOrigin() {
            return origin;
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "%s - %s", super.toString(), origin.getHRName());
        }
    }

}
