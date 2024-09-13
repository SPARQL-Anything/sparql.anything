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

package io.github.sparqlanything.html.org.apache.any23.extractor.rdfa;

import io.github.sparqlanything.html.org.apache.any23.extractor.rdfa.XSLTStylesheetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * An XSLT stylesheet loaded from an InputStream, can be applied to DOM trees and writes the result to a {@link Writer}.
 *
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class XSLTStylesheet {

    private final static Logger log = LoggerFactory.getLogger(XSLTStylesheet.class);

    private final Transformer transformer;

    public XSLTStylesheet(InputStream xsltFile) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            this.transformer = tf.newTransformer(new StreamSource(xsltFile));
        } catch (TransformerConfigurationException tce) {
            throw new RuntimeException("Should not happen, we use the default configuration", tce);
        }
    }

    /**
     * Applies the XSLT transformation
     *
     * @param document
     *            where apply the transformation
     * @param output
     *            the {@link Writer} where write on
     *
     * @throws XSLTStylesheetException
     *             if there is an error applying the transformation
     */
    public synchronized void applyTo(Document document, Writer output) throws XSLTStylesheetException {
        this.applyTo(document, output, new HashMap<String, String>());
    }

    /**
     * Applies the XSLT transformation
     *
     * @param document
     *            where apply the transformation
     * @param output
     *            the {@link Writer} where write on
     * @param parameters
     *            the parameters to be passed to {@link Transformer}. Pass an empty {@link Map} if no parameters are
     *            foreseen.
     *
     * @throws XSLTStylesheetException
     *             if there is an error applying the transformation
     */
    public synchronized void applyTo(Document document, Writer output, Map<String, String> parameters)
            throws XSLTStylesheetException {
        for (String parameterKey : parameters.keySet()) {
            transformer.setParameter(parameterKey, parameters.get(parameterKey));
        }
        try {
            transformer.transform(new DOMSource(document, document.getBaseURI()), new StreamResult(output));
        } catch (TransformerException te) {
            log.error("------ BEGIN XSLT Transformer Exception ------");
            log.error("Exception in XSLT Stylesheet transformation.", te);
            log.error("Input DOM node:", document);
            log.error("Input DOM node getBaseIRI:", document.getBaseURI());
            log.error("Output writer:", output);
            log.error("------ END XSLT Transformer Exception ------");
            throw new XSLTStylesheetException(" An error occurred during the XSLT transformation", te);
        }
    }

}
