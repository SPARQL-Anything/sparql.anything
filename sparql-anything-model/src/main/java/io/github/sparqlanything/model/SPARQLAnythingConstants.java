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

package io.github.sparqlanything.model;

import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.util.Symbol;

public class SPARQLAnythingConstants {
	public static final PrefixMapping PREFIXES = PrefixMapping.Factory.create().setNsPrefixes(PrefixMapping.Extended).setNsPrefix("xhtml","http://www.w3.org/1999/xhtml#").setNsPrefix("whatwg", "https://html.spec.whatwg.org/#").setNsPrefix("fx", Triplifier.FACADE_X_CONST_NAMESPACE_IRI).setNsPrefix("xyz", Triplifier.XYZ_NS).lock();
	public final static String ROOT_ID = "";
	public final static String DATA_SOURCE_ID = "";
	public final static Symbol NO_SERVICE_MODE = Symbol.create("noservicemode");
}
