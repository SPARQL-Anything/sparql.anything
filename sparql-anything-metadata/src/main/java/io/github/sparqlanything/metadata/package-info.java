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

@Format(name = "Metadata", description = "Metadata is \"data that provides information about other data\", but not the content of the data, such as the text of a message or the image itself [[From Wikipedia]](https://en.wikipedia.org/wiki/Metadata)\n" + "\n" + "According to the Facade-X model, metadata are interpreted as a map associated with the resource triplified.\n" + "\n" + "At the moment SPARQL Anything is able to extract [EXIF metadata](https://en.wikipedia.org/wiki/Exif).\nMetadata transformer has to be invoked explicitly by setting the metadata option as true. In this case metadata is extracted from the data source and stored in the named graph with URI http://sparql.xyz/facade-x/data/metadata\n" + "\n", resourceExample = "https://raw.githubusercontent.com/ianare/exif-samples/master/jpg/Canon_40D.jpg", query = "CONSTRUCT { GRAPH ?g { ?s ?p ?o .} } WHERE { SERVICE <x-sparql-anything:location=https://raw.githubusercontent.com/ianare/exif-samples/master/jpg/Canon_40D.jpg,metadata=true> { GRAPH ?g { ?s ?p ?o } } }", binary = true)
package io.github.sparqlanything.metadata;

import io.github.sparqlanything.model.annotations.Format;
