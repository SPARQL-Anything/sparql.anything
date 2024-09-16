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
 * Vocabulary to map the <a href="http://microformats.org/wiki/h-event">h-event</a> microformat.
 *
 * @author Nisala Nirmana
 */
public class HEvent extends Vocabulary {
    public static final String NS = SINDICE.NS + "hevent/";

    private static HEvent instance;

    public static HEvent getInstance() {
        if (instance == null) {
            instance = new HEvent();
        }
        return instance;
    }

    public IRI event = createClass(NS, "Event");

    public IRI name = createProperty(NS, "name");
    public IRI summary = createProperty(NS, "summary");
    public IRI start = createProperty(NS, "start");
    public IRI end = createProperty(NS, "end");
    public IRI duration = createProperty(NS, "duration");
    public IRI description = createProperty(NS, "description");
    public IRI url = createProperty(NS, "url");
    public IRI category = createProperty(NS, "category");
    public IRI location = createProperty(NS, "location");
    public IRI attendee = createProperty(NS, "attendee");

    private HEvent() {
        super(NS);
    }
}
