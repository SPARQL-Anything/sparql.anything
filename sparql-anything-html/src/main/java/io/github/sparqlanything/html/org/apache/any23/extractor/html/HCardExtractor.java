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
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.extractor.IssueReport;
import io.github.sparqlanything.html.org.apache.any23.extractor.TagSoupExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.annotations.Includes;
import io.github.sparqlanything.html.org.apache.any23.vocab.VCard;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLDocument.TextField;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hcard">hCard</a> microformat.
 *
 * @author Gabriele Renzi
 */
@Includes(extractors = AdrExtractor.class)
public class HCardExtractor extends EntityBasedMicroformatExtractor {

    private static final VCard vCARD = VCard.getInstance();

    private HCardName name = new HCardName();

    private HTMLDocument fragment;

    @Override
    public ExtractorDescription getDescription() {
        return HCardExtractorFactory.getDescriptionInstance();
    }

    @Override
    protected String getBaseClassName() {
        return "vcard";
    }

    @Override
    protected void resetExtractor() {
        name.reset(); // Cleanup of the HCardName content.
    }

    private void fixIncludes(HTMLDocument document, Node node, IssueReport report) {
        NamedNodeMap attributes = node.getAttributes();
        // header case test 32
        if ("TD".equals(node.getNodeName()) && (null != attributes.getNamedItem("headers"))) {
            String id = attributes.getNamedItem("headers").getNodeValue();
            Node header = document.findNodeById(id);
            if (null != header) {
                node.appendChild(header.cloneNode(true));
                attributes.removeNamedItem("headers");
            }
        }

        // include pattern, test 31
        for (Node current : DomUtils.findAllByAttributeName(document.getDocument(), "class")) {
            if (!DomUtils.hasClassName(current, "include"))
                continue;
            // we have to remove the field soon to avoid infinite loops
            // no null check, we know it's there or we won't be in the loop
            current.getAttributes().removeNamedItem("class");
            ArrayList<TextField> res = new ArrayList<TextField>();
            HTMLDocument.readUrlField(res, current);
            if (res.isEmpty())
                continue;
            TextField id = res.get(0);
            if (null == id)
                continue;
            TextField refId = new TextField(StringUtils.substringAfter(id.value(), "#"), id.source());
            Node included = document.findNodeById(refId.value());
            if (null == included)
                continue;
            if (DomUtils.isAncestorOf(included, current)) {
                final int[] nodeLocation = DomUtils.getNodeLocation(current);
                report.notifyIssue(IssueReport.IssueLevel.WARNING, "Current node tries to include an ancestor node.",
                        nodeLocation == null ? -1 : nodeLocation[0], nodeLocation == null ? -1 : nodeLocation[1]);
                continue;
            }
            current.appendChild(included.cloneNode(true));
        }
    }

    @Override
    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        this.fragment = new HTMLDocument(node);
        fixIncludes(getHTMLDocument(), node, out);
        final BNode card = getBlankNodeFor(node);
        boolean foundSomething = false;

        readFn();
        readNames();
        readOrganization();
        foundSomething |= addFn(card);
        foundSomething |= addNames(card);
        foundSomething |= addOrganizationName(card);
        foundSomething |= addStringProperty("sort-string", card, vCARD.sort_string);
        foundSomething |= addUrl(card);
        foundSomething |= addEmail(card);
        foundSomething |= addPhoto(card);
        foundSomething |= addLogo(card);
        foundSomething |= addUid(card);
        foundSomething |= addClass(card);
        foundSomething |= addStringProperty("bday", card, vCARD.bday);
        foundSomething |= addStringProperty("rev", card, vCARD.rev);
        foundSomething |= addStringProperty("tz", card, vCARD.tz);
        foundSomething |= addCategory(card);
        foundSomething |= addStringProperty("card", card, vCARD.class_);
        foundSomething |= addSubMicroformat("adr", card, vCARD.adr);
        foundSomething |= addTelephones(card);
        foundSomething |= addStringProperty("title", card, vCARD.title);
        foundSomething |= addStringProperty("role", card, vCARD.role);
        foundSomething |= addStringMultiProperty("note", card, vCARD.note);
        foundSomething |= addSubMicroformat("geo", card, vCARD.geo);

        if (!foundSomething)
            return false;
        out.writeTriple(card, RDF.TYPE, vCARD.VCard);

        final TagSoupExtractionResult tser = (TagSoupExtractionResult) out;
        tser.addResourceRoot(DomUtils.getXPathListForNode(node), card, this.getClass());

        return true;
    }

    private boolean addTelephones(Resource card) {
        boolean found = false;
        for (Node node : DomUtils.findAllByAttributeContains(fragment.getDocument(), "class", "tel")) {
            HTMLDocument telFragment = new HTMLDocument(node);
            TextField[] values = telFragment.getPluralUrlField("value");
            if (values.length == 0) {
                // no sub values
                String[] typeAndValue = telFragment.getSingularUrlField("tel").value().split(":");
                // modem:goo fax:foo tel:bar
                if (typeAndValue.length > 1) {
                    found |= addTel(card, "tel", typeAndValue[1]);
                } else {
                    found |= addTel(card, "tel", typeAndValue[0]);
                }
            } else {
                final String[] valuesStr = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    valuesStr[i] = values[i].value();
                }
                HTMLDocument.TextField[] types = telFragment.getPluralTextField("type");
                if (types.length == 0) {
                    found |= addTel(card, "tel", StringUtils.join(valuesStr));
                }
                for (HTMLDocument.TextField type : types) {
                    found |= addTel(card, type.value(), StringUtils.join(valuesStr));
                }
            }
        }
        return found;
    }

    private boolean addTel(Resource card, String type, String value) {
        IRI tel = super.fixLink(value, "tel");
        IRI composed = vCARD.getProperty(type + "Tel", null);
        if (composed == null) {
            IRI simple = vCARD.getProperty(type, null);
            if (simple == null) {
                return conditionallyAddResourceProperty(card, vCARD.tel, tel);
            }
            return conditionallyAddResourceProperty(card, simple, tel);
        }
        return conditionallyAddResourceProperty(card, composed, tel);
    }

    private boolean addSubMicroformat(String className, Resource resource, IRI property) {
        List<Node> nodes = fragment.findAllByClassName(className);
        if (nodes.isEmpty())
            return false;
        for (Node node : nodes) {
            addBNodeProperty(node, resource, property, getBlankNodeFor(node));
        }
        return true;
    }

    private boolean addStringProperty(String className, Resource resource, IRI property) {
        final HTMLDocument.TextField textField = fragment.getSingularTextField(className);
        return conditionallyAddStringProperty(textField.source(), resource, property, textField.value());
    }

    /**
     * Adds a property that can be associated to multiple values.
     *
     * @param className
     * @param resource
     * @param property
     *
     * @return <code>true</code> if the multi property has been added, <code>false</code> otherwise.
     */
    private boolean addStringMultiProperty(String className, Resource resource, IRI property) {
        HTMLDocument.TextField[] fields = fragment.getPluralTextField(className);
        boolean found = false;
        for (HTMLDocument.TextField field : fields) {
            found |= conditionallyAddStringProperty(field.source(), resource, property, field.value());
        }
        return found;
    }

    private boolean addCategory(Resource card) {
        HTMLDocument.TextField[] categories = fragment.getPluralTextField("category");
        boolean found = false;
        for (HTMLDocument.TextField category : categories) {
            found |= conditionallyAddStringProperty(category.source(), card, vCARD.category, category.value());
        }
        return found;
    }

    private boolean addUid(Resource card) {
        TextField uid = fragment.getSingularUrlField("uid");
        return conditionallyAddStringProperty(fragment.getDocument(), card, vCARD.uid, uid.value());
    }

    private boolean addClass(Resource card) {
        TextField class_ = fragment.getSingularUrlField("class");
        return conditionallyAddStringProperty(fragment.getDocument(), card, vCARD.class_, class_.value());
    }

    private boolean addLogo(Resource card) throws ExtractionException {
        TextField[] links = fragment.getPluralUrlField("logo");
        boolean found = false;
        for (TextField link : links) {
            found |= conditionallyAddResourceProperty(card, vCARD.logo, getHTMLDocument().resolveIRI(link.value()));
        }
        return found;
    }

    private boolean addPhoto(Resource card) throws ExtractionException {
        TextField[] links = fragment.getPluralUrlField("photo");
        boolean found = false;
        for (TextField link : links) {
            found |= conditionallyAddResourceProperty(card, vCARD.photo, getHTMLDocument().resolveIRI(link.value()));
        }
        return found;
    }

    private boolean addEmail(Resource card) {
        String email = dropSubject(fragment.getSingularUrlField("email").value());
        return conditionallyAddResourceProperty(card, vCARD.email, fixLink(email, "mailto"));
    }

    private String dropSubject(String mail) {
        if (mail == null)
            return null;
        return mail.split("\\?")[0];
    }

    private void readNames() {
        for (String field : HCardName.FIELDS) {
            HTMLDocument.TextField[] values = fragment.getPluralTextField(field);
            for (HTMLDocument.TextField text : values) {
                if ("".equals(text.value()))
                    continue;
                name.setField(field, text);
            }
        }
    }

    private void addFieldTriple(Node n, BNode bn, String fieldName, String fieldValue) {
        conditionallyAddLiteralProperty(n, bn, vCARD.getProperty(fieldName), valueFactory.createLiteral(fieldValue));
    }

    private boolean addNames(Resource card) {
        BNode n = valueFactory.createBNode();
        addBNodeProperty(this.fragment.getDocument(), card, vCARD.n, n);
        addIRIProperty(n, RDF.TYPE, vCARD.Name);

        for (String fieldName : HCardName.FIELDS) {
            if (!name.containsField(fieldName)) {
                continue;
            }
            if (name.isMultiField(fieldName)) {
                Collection<HTMLDocument.TextField> values = name.getFields(fieldName);
                for (TextField value : values) {
                    addFieldTriple(value.source(), n, fieldName, value.value());
                }
            } else {
                TextField value = name.getField(fieldName);
                if (value == null) {
                    continue;
                }
                addFieldTriple(value.source(), n, fieldName, value.value());
            }
        }
        return true;
    }

    private void readFn() {
        name.setFullName(fragment.getSingularTextField("fn"));
    }

    private boolean addFn(Resource card) {
        final TextField fullNameTextField = name.getFullName();
        if (fullNameTextField == null) {
            return false;
        }
        return conditionallyAddStringProperty(fullNameTextField.source(), card, vCARD.fn, fullNameTextField.value());
    }

    private void readOrganization() {
        Node node = fragment.findMicroformattedObjectNode("*", "org");
        if (node == null)
            return;
        HTMLDocument doc = new HTMLDocument(node);
        String nodeText = doc.getText();
        if (nodeText != null) {
            name.setOrganization(new HTMLDocument.TextField(nodeText, node));
        }
        nodeText = doc.getSingularTextField("organization-name").value();
        if (nodeText == null || "".equals(nodeText)) {
            nodeText = HTMLDocument.readTextField(node).value();
        }
        name.setOrganization(new TextField(nodeText, node));

        name.setOrganizationUnit(doc.getSingularTextField("organization-unit"));
    }

    private boolean addOrganizationName(Resource card) {
        if (name.getOrganization() == null)
            return false;
        BNode org = valueFactory.createBNode();
        addBNodeProperty(this.fragment.getDocument(), card, vCARD.org, org);
        addIRIProperty(org, RDF.TYPE, vCARD.Organization);
        final TextField organizationTextField = name.getOrganization();
        conditionallyAddLiteralProperty(organizationTextField.source(), org, vCARD.organization_name,
                valueFactory.createLiteral(organizationTextField.value()));
        final TextField organizationUnitTextField = name.getOrganizationUnit();
        if (organizationUnitTextField != null) {
            conditionallyAddStringProperty(organizationUnitTextField.source(), org, vCARD.organization_unit,
                    organizationUnitTextField.value());
        }
        return true;
    }

    private boolean addUrl(Resource card) throws ExtractionException {
        TextField[] links = fragment.getPluralUrlField("url");
        boolean found = false;
        for (TextField link : links) {
            found |= conditionallyAddResourceProperty(card, vCARD.url, getHTMLDocument().resolveIRI(link.value()));
        }
        return found;
    }

}
