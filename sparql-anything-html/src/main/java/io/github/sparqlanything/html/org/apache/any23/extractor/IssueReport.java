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

package io.github.sparqlanything.html.org.apache.any23.extractor;

import java.io.PrintStream;
import java.util.Collection;

/**
 * This interface models an issue reporter.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public interface IssueReport {

    /**
     * Notifies an issue occurred while performing an extraction on an input stream.
     *
     * @param level
     *            issue level.
     * @param msg
     *            issue message.
     * @param row
     *            issue row.
     * @param col
     *            issue column.
     */
    void notifyIssue(IssueLevel level, String msg, long row, long col);

    /**
     * Prints out the content of the report.
     *
     * @param ps
     *            a {@link PrintStream} to use for generating the report.
     */
    void printReport(PrintStream ps);

    /**
     * Returns all the collected issues.
     *
     * @return a collection of {@link io.github.sparqlanything.html.org.apache.any23.extractor.IssueReport.Issue}s.
     */
    Collection<Issue> getIssues();

    /**
     * Possible issue levels.
     */
    enum IssueLevel {
        WARNING, ERROR, FATAL
    }

    /**
     * This class defines a generic issue traced by this extraction result.
     */
    public class Issue {

        private IssueLevel level;
        private String message;
        private long row, col;

        Issue(IssueLevel l, String msg, long r, long c) {
            level = l;
            message = msg;
            row = r;
            col = c;
        }

        public IssueLevel getLevel() {
            return level;
        }

        public String getMessage() {
            return message;
        }

        public long getRow() {
            return row;
        }

        public long getCol() {
            return col;
        }

        @Override
        public String toString() {
            return String.format(java.util.Locale.ROOT, "%s: \t'%s' \t(%d,%d)", level, message, row, col);
        }
    }

}
