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

package io.github.sparqlanything.html.org.apache.any23.extractor.html;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.extractor.TagSoupExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.rdf.RDFUtils;
import io.github.sparqlanything.html.org.apache.any23.vocab.ICAL;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Node;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.List;

import static io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLDocument.TextField;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hcalendar">hCalendar</a> microformat.
 *
 * @author Gabriele Renzi
 */
public class HCalendarExtractor extends MicroformatExtractor {

    private static final ICAL vICAL = ICAL.getInstance();

    private static final String[] Components = { "Vevent", "Vtodo", "Vjournal", "Vfreebusy" };

    private static final String DATE_FORMAT = "yyyyMMdd'T'HHmm'Z'";

    private String[] textSingularProps = { "summary", "class", "transp", "description", "status", "location" };

    private String[] textDateProps = { "dtstart", "dtstamp", "dtend", };

    @Override
    public ExtractorDescription getDescription() {
        return HCalendarExtractorFactory.getDescriptionInstance();
    }

    @Override
    protected boolean extract() throws ExtractionException {
        final HTMLDocument document = getHTMLDocument();
        List<Node> calendars = document.findAllByClassName("vcalendar");
        if (calendars.size() == 0)
            // vcal allows to avoid top name, in which case whole document is
            // the calendar, let's try
            if (document.findAllByClassName("vevent").size() > 0)
                calendars.add(document.getDocument());

        boolean foundAny = false;
        for (Node node : calendars)
            foundAny |= extractCalendar(node);

        return foundAny;
    }

    private boolean extractCalendar(Node node) throws ExtractionException {
        IRI cal = getDocumentIRI();
        addIRIProperty(cal, RDF.TYPE, vICAL.Vcalendar);
        return addComponents(node, cal);
    }

    private boolean addComponents(Node node, Resource cal) throws ExtractionException {
        boolean foundAny = false;
        for (String component : Components) {
            List<Node> events = DomUtils.findAllByClassName(node, component);
            if (events.size() == 0)
                continue;
            for (Node evtNode : events)
                foundAny |= extractComponent(evtNode, cal, component);
        }
        return foundAny;
    }

    private boolean extractComponent(Node node, Resource cal, String component) throws ExtractionException {
        HTMLDocument compoNode = new HTMLDocument(node);
        BNode evt = valueFactory.createBNode();
        addIRIProperty(evt, RDF.TYPE, vICAL.getClass(component));
        addTextProps(compoNode, evt);
        addUrl(compoNode, evt);
        addRRule(compoNode, evt);
        addOrganizer(compoNode, evt);
        addUid(compoNode, evt);
        addBNodeProperty(cal, vICAL.component, evt);

        final TagSoupExtractionResult tser = (TagSoupExtractionResult) getCurrentExtractionResult();
        tser.addResourceRoot(compoNode.getPathToLocalRoot(), evt, this.getClass());

        return true;
    }

    private void addUid(HTMLDocument compoNode, Resource evt) {
        TextField url = compoNode.getSingularUrlField("uid");
        conditionallyAddStringProperty(compoNode.getDocument(), evt, vICAL.uid, url.value());
    }

    private void addUrl(HTMLDocument compoNode, Resource evt) throws ExtractionException {
        TextField url = compoNode.getSingularUrlField("url");
        if ("".equals(url.value()))
            return;
        addIRIProperty(evt, vICAL.url, getHTMLDocument().resolveIRI(url.value()));
    }

    private void addRRule(HTMLDocument compoNode, Resource evt) {
        for (Node rule : compoNode.findAllByClassName("rrule")) {
            BNode rrule = valueFactory.createBNode();
            addIRIProperty(rrule, RDF.TYPE, vICAL.DomainOf_rrule);
            TextField freq = new HTMLDocument(rule).getSingularTextField("freq");
            conditionallyAddStringProperty(freq.source(), rrule, vICAL.freq, freq.value());
            addBNodeProperty(rule, evt, vICAL.rrule, rrule);
        }
    }

    private void addOrganizer(HTMLDocument compoNode, Resource evt) {
        for (Node organizer : compoNode.findAllByClassName("organizer")) {
            // untyped
            BNode blank = valueFactory.createBNode();
            TextField mail = new HTMLDocument(organizer).getSingularUrlField("organizer");
            conditionallyAddStringProperty(compoNode.getDocument(), blank, vICAL.calAddress, mail.value());
            addBNodeProperty(organizer, evt, vICAL.organizer, blank);
        }
    }

    private void addTextProps(HTMLDocument node, Resource evt) {
        for (String date : textSingularProps) {
            HTMLDocument.TextField val = node.getSingularTextField(date);
            conditionallyAddStringProperty(val.source(), evt, vICAL.getProperty(date), val.value());
        }

        for (String date : textDateProps) {
            HTMLDocument.TextField val = node.getSingularTextField(date);
            try {
                conditionallyAddStringProperty(val.source(), evt, vICAL.getProperty(date),
                        RDFUtils.getXSDDate(val.value(), DATE_FORMAT));
            } catch (ParseException e) {
                // Unparsable date format just leave it as it is.
                conditionallyAddStringProperty(val.source(), evt, vICAL.getProperty(date), val.value());
            } catch (DatatypeConfigurationException e) {
                // Unparsable date format just leave it as it is
                conditionallyAddStringProperty(val.source(), evt, vICAL.getProperty(date), val.value());
            }
        }

        HTMLDocument.TextField[] values = node.getPluralTextField("category");
        for (TextField val : values) {
            conditionallyAddStringProperty(val.source(), evt, vICAL.categories, val.value());
        }
    }

}
