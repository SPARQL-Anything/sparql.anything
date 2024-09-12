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
 * Base class for pipeline procecessing blocks with one source and one sink.
 * @param <S> class of output sink
 */
public abstract class Pipe<S extends DataSink> implements DataSink {

    protected final S sink;

    protected Pipe(S sink) {
        this.sink = sink;
    }

    @Override
    public void startStream() throws ParseException {
        sink.startStream();
    }

    @Override
    public void endStream() throws ParseException {
        sink.endStream();
    }

    @Override
    public final boolean setProperty(String key, Object value) {
        boolean sinkResult = false;
        if (sink != null) {
            sinkResult = sink.setProperty(key, value);
        }
        return setPropertyInternal(key, value) || sinkResult;
    }

    protected abstract boolean setPropertyInternal(String key, Object value);

}
