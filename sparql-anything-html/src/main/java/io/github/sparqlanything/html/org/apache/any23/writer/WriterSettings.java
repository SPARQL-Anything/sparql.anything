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

package io.github.sparqlanything.html.org.apache.any23.writer;

import io.github.sparqlanything.html.org.apache.any23.configuration.Setting;
import io.github.sparqlanything.html.org.apache.any23.writer.TripleWriter;

/**
 *
 * This class encapsulates commonly supported settings for {@link TripleWriter} implementations.
 *
 * @author Hans Brende (hansbrende@apache.org)
 */
public class WriterSettings {
    private WriterSettings() {
        throw new AssertionError();
    }

    // Keep identifiers short & sweet for ease of user's CLI usage!
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // (Since each WriterFactory must maintain its own pool of "supported settings",
    // we don't need to worry about identifiers being globally unique.
    // A single identifier could theoretically map to different keys--and
    // therefore to different semantics--under different WriterFactory instances.
    // Note that it is the *memory-based identity of the key*, not the
    // key's textual identifier, that denotes the semantics for a given setting.
    // However, since each Settings object is guaranteed to contain only one setting
    // per identifier, we can be assured that identifiers will be unique on a
    // per-WriterFactory basis.)

    /**
     * Directive to writer that output should be printed in a way to maximize human readability.
     */
    public static final Setting<Boolean> PRETTY_PRINT = Setting.create("pretty", Boolean.TRUE);

    /**
     * Directive to writer that at least the non-ASCII characters should be escaped.
     */
    public static final Setting<Boolean> PRINT_ASCII = Setting.create("ascii", Boolean.FALSE);

}
