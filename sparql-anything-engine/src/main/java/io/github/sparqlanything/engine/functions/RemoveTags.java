/*
 * Copyright (c) 2026 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package io.github.sparqlanything.engine.functions;

import io.github.sparqlanything.model.annotations.FXFunctionDoc;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

@FXFunctionDoc(
	description = "fx:String.removeTags removes the XML tags from the input string",
	example = "BIND(fx:String.removeTags(?htmlContent) AS ?plainText)",
	group = "STRING FUNCTIONS",
	label = "fx:String.removeTags"
)
public class RemoveTags extends FunctionBase1 implements FXFunction {
	@Override
	public NodeValue exec(NodeValue v) {
		return NodeValue.makeString(Jsoup.clean(v.asString(), "", Safelist.none(), new Document.OutputSettings().prettyPrint(false)));
	}
}
