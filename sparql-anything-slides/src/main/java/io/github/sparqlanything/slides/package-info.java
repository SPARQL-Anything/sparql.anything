@Format(
		getName = "Slides",
		getDescription = "A slide is a single page of a presentation. Collectively, a group of slides may be known as a slide deck.\n" +
		"We can interpret a slide deck as a sequence of slides where each slide is a sequence of blocks (e.g. title, text boxes etc.), called shapes.\n" +
		"Each shape may have multiple paragraphs, where each paragraph can be seen as a sequence of text runs (i.e. pieces of text).\n" +
		"Each piece of text is a container for the text and possibly other annotations on the text (e.g. hyperlinks).\n",
		getExtensions = {"pptx"},
		getMediaTypes = {"application/vnd.openxmlformats-officedocument.presentationml.presentation"},
		getTriplifiers = {PptxTriplifier.class},
		getResourceExample = "https://sparql-anything.cc/examples/Presentation3.pptx",
		showGraphs = false
)
package io.github.sparqlanything.slides;

import io.github.sparqlanything.model.annotations.Format;
