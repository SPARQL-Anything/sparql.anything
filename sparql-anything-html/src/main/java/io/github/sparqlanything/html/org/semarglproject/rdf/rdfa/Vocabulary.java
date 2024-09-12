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
package io.github.sparqlanything.html.org.semarglproject.rdf.rdfa;

import io.github.sparqlanything.html.org.semarglproject.rdf.ParseException;
import io.github.sparqlanything.html.org.semarglproject.rdf.RdfXmlParser;
import io.github.sparqlanything.html.org.semarglproject.rdf.rdfa.RdfaParser;
import io.github.sparqlanything.html.org.semarglproject.ri.RIUtils;
import io.github.sparqlanything.html.org.semarglproject.sink.TripleSink;
import io.github.sparqlanything.html.org.semarglproject.source.StreamProcessor;
import io.github.sparqlanything.html.org.semarglproject.vocab.OWL;
import io.github.sparqlanything.html.org.semarglproject.vocab.RDF;
import io.github.sparqlanything.html.org.semarglproject.vocab.RDFS;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

final class Vocabulary {

    private final String url;
    private Map<String, Collection<String>> expansions = null;
    private Collection<String> terms = null;

    Vocabulary(String url) {
        this.url = url;
    }

    private void addExpansion(String pred, String expansion) {
        if (!expansions.containsKey(pred)) {
            expansions.put(pred, new HashSet<String>());
        }
        expansions.get(pred).add(expansion);
    }

    void load() {
        VocabParser vocabParser = new VocabParser();

        URL vocabUrl;
        try {
            vocabUrl = new URL(url);
        } catch (MalformedURLException e) {
            return;
        }

        if (expansions == null) {
            expansions = new HashMap<String, Collection<String>>();
            terms = new HashSet<String>();
        }

        StreamProcessor rdfaSp = new StreamProcessor(RdfaParser.connect(vocabParser));
        rdfaSp.setProperty(RdfaParser.ENABLE_VOCAB_EXPANSION, false);
        parseVocabWithDp(vocabUrl, rdfaSp);

        if (!terms.isEmpty() || !expansions.isEmpty()) {
            return;
        }

        // TODO: add format detection
        StreamProcessor rdfXmlSp = new StreamProcessor(RdfXmlParser.connect(vocabParser));
        rdfaSp.setProperty(RdfaParser.ENABLE_VOCAB_EXPANSION, false);
        parseVocabWithDp(vocabUrl, rdfXmlSp);

        if (terms.isEmpty() && expansions.isEmpty()) {
            terms = null;
            expansions = null;
        }
    }

    private void parseVocabWithDp(URL vocabUrl, StreamProcessor streamProcessor) {
        InputStream inputStream;
        try {
            inputStream = vocabUrl.openStream();
        } catch (IOException e) {
            return;
        }
        InputStreamReader reader = new InputStreamReader(inputStream);
        try {
            streamProcessor.process(reader, url);
        } catch (ParseException e) {
            // do nothing
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    Collection<String> expand(String uri) {
        if (expansions == null || !expansions.containsKey(uri)) {
            return Collections.EMPTY_LIST;
        }
        return expansions.get(uri);
    }

    String resolveTerm(String term) {
        String termUri = url + term;
        if (terms == null && RIUtils.isAbsoluteIri(termUri) || terms != null && terms.contains(termUri)) {
            return termUri;
        }
        return null;
    }

    private final class VocabParser implements TripleSink {
        @Override
        public void addNonLiteral(String subj, String pred, String obj) {
            if (subj.startsWith(RDF.BNODE_PREFIX) || obj.startsWith(RDF.BNODE_PREFIX)) {
                return;
            }
            if (pred.equals(OWL.EQUIVALENT_PROPERTY) || pred.equals(OWL.EQUIVALENT_CLASS)) {
                addExpansion(subj, obj);
                addExpansion(obj, subj);
                terms.add(obj);
                terms.add(subj);
            } else if (pred.equals(RDFS.SUB_CLASS_OF) || pred.equals(RDFS.SUB_PROPERTY_OF)) {
                addExpansion(subj, obj);
                terms.add(obj);
                terms.add(subj);
            }
            if (pred.equals(RDF.TYPE) && (obj.equals(RDF.PROPERTY) || obj.equals(RDFS.CLASS))) {
                terms.add(subj);
            }
        }

        @Override
        public void addPlainLiteral(String subj, String pred, String content, String lang) {
        }

        @Override
        public void addTypedLiteral(String subj, String pred, String content, String type) {
        }

        @Override
        public void setBaseUri(String baseUri) {
        }

        @Override
        public void startStream() throws ParseException {
        }

        @Override
        public void endStream() throws ParseException {
        }

        @Override
        public boolean setProperty(String key, Object value) {
            return false;
        }
    }
}
