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

package io.github.sparqlanything.html.org.apache.any23.vocab;

import org.eclipse.rdf4j.model.IRI;

/**
 * This vocabulary models the structure of a <i>CSV</i> file according the
 * <a href="http://www.ietf.org/rfc/rfc4180.txt">RFC 4180</a>.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class CSV extends Vocabulary {

    public static final String ROW = "row";

    public static final String ROW_POSITION = "rowPosition";

    public static final String NUMBER_OF_ROWS = "numberOfRows";

    public static final String NUMBER_OF_COLUMNS = "numberOfColumns";

    public static final String COLUMN_POSITION = "columnPosition";

    public static final String ROW_TYPE = "Row";

    /**
     * This property links the identifier of a <i>CSV</i> to an entity representing a row.
     */
    public final IRI row = createProperty(ROW);

    /**
     * This property expresses the index of a row in a <i>CSV</i> file.
     */
    public final IRI rowPosition = createProperty(ROW_POSITION);

    /**
     * This property expresses the number of rows in a <i>CSV</i> file.
     */
    public final IRI numberOfRows = createProperty(NUMBER_OF_ROWS);

    /**
     * This property expresses the number of columns in a <i>CSV</i> file.
     */
    public final IRI numberOfColumns = createProperty(NUMBER_OF_COLUMNS);

    /**
     * This resource identifies a <i>Row</i>.
     */
    public final IRI rowType = createResource(ROW_TYPE);

    /**
     * This property expresses the index of a column in a <i>CSV</i> file.
     */
    public final IRI columnPosition = createProperty(COLUMN_POSITION);

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://tools.ietf.org/html/rfc4180";

    private static CSV instance;

    private CSV() {
        super(NS);
    }

    public static CSV getInstance() {
        if (instance == null) {
            instance = new CSV();
        }
        return instance;
    }

    public IRI createResource(String localName) {
        return createProperty(NS, localName);
    }

    /**
     *
     * @param localName
     *            name to assign to namespace.
     *
     * @return the new URI instance.
     */
    public IRI createProperty(String localName) {
        return createProperty(NS, localName);
    }

}
