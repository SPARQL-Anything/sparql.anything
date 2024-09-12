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
package io.github.sparqlanything.html.org.semarglproject.source;

import io.github.sparqlanything.html.org.semarglproject.rdf.ParseException;
import io.github.sparqlanything.html.org.semarglproject.sink.DataSink;

import java.io.InputStream;
import java.io.Reader;

abstract class AbstractSource<S extends DataSink> {

    protected final S sink;

    protected AbstractSource(S sink) {
        this.sink = sink;
    }

    protected abstract void process(Reader reader, String mimeType, String baseUri) throws ParseException;

    protected abstract void process(InputStream inputStream, String mimeType, String baseUri) throws ParseException;

}
