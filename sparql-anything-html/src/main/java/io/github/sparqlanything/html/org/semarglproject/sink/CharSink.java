/**
 * Copyright 2012-2013 the Semargl contributors. See AUTHORS for more details.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.sparqlanything.html.org.semarglproject.sink;

import io.github.sparqlanything.html.org.semarglproject.rdf.ParseException;
import io.github.sparqlanything.html.org.semarglproject.sink.DataSink;

/**
 * Interface for handling events from CharSource
 */
public interface CharSink extends DataSink {

    /**
     * Callback for string processing
     *
     * @param str string for processing
     * @throws ParseException
     */
    CharSink process(String str) throws ParseException;

    /**
     * Callback for char processing
     *
     * @param ch char for processing
     * @throws ParseException
     */
    CharSink process(char ch) throws ParseException;

    /**
     * Callback for buffer processing
     *
     * @param buffer char buffer for processing
     * @param start position to start
     * @param count count of chars to process
     * @throws ParseException
     */
    CharSink process(char[] buffer, int start, int count) throws ParseException;
}
