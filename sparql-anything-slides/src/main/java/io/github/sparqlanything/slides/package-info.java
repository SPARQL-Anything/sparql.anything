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

@Format(binary = true,
		name = "Slides",
		description = "A slide is a single page of a presentation. Collectively, a group of slides may be known as a slide deck.\n" +
		"We can interpret a slide deck as a sequence of slides where each slide is a sequence of blocks (e.g. title, text boxes etc.), called shapes.\n" +
		"Each shape may have multiple paragraphs, where each paragraph can be seen as a sequence of text runs (i.e. pieces of text).\n" +
		"Each piece of text is a container for the text and possibly other annotations on the text (e.g. hyperlinks).\n",
		resourceExample = "https://sparql-anything.cc/examples/Presentation3.pptx"
)
package io.github.sparqlanything.slides;

import io.github.sparqlanything.model.annotations.Format;
