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
package io.github.sparqlanything.html.org.semarglproject.rdf;

/**
 * Interface for handling processor graph events
 */
public interface ProcessorGraphHandler {

    /**
     * Callback for info events
     * @param infoClass event class URI
     * @param message info message
     */
    void info(String infoClass, String message);

    /**
     * Callback for warning events
     * @param warningClass warning class URI
     * @param message warning message
     */
    void warning(String warningClass, String message);

    /**
     * Callback for error events
     * @param errorClass event class URI
     * @param message error message
     */
    void error(String errorClass, String message);
}
