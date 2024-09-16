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

package io.github.sparqlanything.html.org.apache.any23.vocab;

import org.eclipse.rdf4j.model.IRI;

/**
 * The <a href="http://xmlns.com/foaf/spec/">Friend Of A Friend</a> vocabulary.
 */
public class FOAF extends Vocabulary {

    public static final String NS = "http://xmlns.com/foaf/0.1/";

    private static FOAF instance;

    public static FOAF getInstance() {
        if (instance == null) {
            instance = new FOAF();
        }
        return instance;
    }

    // Properties.
    public final IRI topic_interest = createProperty(NS, "topic_interest");
    public final IRI phone = createProperty(NS, "phone");
    public final IRI icqChatID = createProperty(NS, "icqChatID");
    public final IRI yahooChatID = createProperty(NS, "yahooChatID");
    public final IRI member = createProperty(NS, "member");
    public final IRI givenname = createProperty(NS, "givenname");
    public final IRI birthday = createProperty(NS, "birthday");
    public final IRI img = createProperty(NS, "img");
    public final IRI name = createProperty(NS, "name");
    public final IRI maker = createProperty(NS, "maker");
    public final IRI tipjar = createProperty(NS, "tipjar");
    public final IRI membershipClass = createProperty(NS, "membershipClass");
    public final IRI accountName = createProperty(NS, "accountName");
    public final IRI mbox_sha1sum = createProperty(NS, "mbox_sha1sum");
    public final IRI geekcode = createProperty(NS, "geekcode");
    public final IRI interest = createProperty(NS, "interest");
    public final IRI depicts = createProperty(NS, "depicts");
    public final IRI knows = createProperty(NS, "knows");
    public final IRI homepage = createProperty(NS, "homepage");
    public final IRI firstName = createProperty(NS, "firstName");
    public final IRI surname = createProperty(NS, "surname");
    public final IRI isPrimaryTopicOf = createProperty(NS, "isPrimaryTopicOf");
    public final IRI page = createProperty(NS, "page");
    public final IRI accountServiceHomepage = createProperty(NS, "accountServiceHomepage");
    public final IRI depiction = createProperty(NS, "depiction");
    public final IRI fundedBy = createProperty(NS, "fundedBy");
    public final IRI title = createProperty(NS, "title");
    public final IRI weblog = createProperty(NS, "weblog");
    public final IRI logo = createProperty(NS, "logo");
    public final IRI workplaceHomepage = createProperty(NS, "workplaceHomepage");
    public final IRI based_near = createProperty(NS, "based_near");
    public final IRI thumbnail = createProperty(NS, "thumbnail");
    public final IRI primaryTopic = createProperty(NS, "primaryTopic");
    public final IRI aimChatID = createProperty(NS, "aimChatID");
    public final IRI made = createProperty(NS, "made");
    public final IRI workInfoHomepage = createProperty(NS, "workInfoHomepage");
    public final IRI currentProject = createProperty(NS, "currentProject");
    public final IRI holdsAccount = createProperty(NS, "holdsAccount");
    public final IRI publications = createProperty(NS, "publications");
    public final IRI sha1 = createProperty(NS, "sha1");
    public final IRI gender = createProperty(NS, "gender");
    public final IRI mbox = createProperty(NS, "mbox");
    public final IRI myersBriggs = createProperty(NS, "myersBriggs");
    public final IRI plan = createProperty(NS, "plan");
    public final IRI pastProject = createProperty(NS, "pastProject");
    public final IRI schoolHomepage = createProperty(NS, "schoolHomepage");
    public final IRI family_name = createProperty(NS, "family_name");
    public final IRI msnChatID = createProperty(NS, "msnChatID");
    public final IRI theme = createProperty(NS, "theme");
    public final IRI topic = createProperty(NS, "topic");
    public final IRI dnaChecksum = createProperty(NS, "dnaChecksum");
    public final IRI nick = createProperty(NS, "nick");
    public final IRI jabberID = createProperty(NS, "jabberID");

    // Resources.
    public final IRI Person = createClass(NS, "Person");
    public final IRI PersonalProfileDocument = createClass(NS, "PersonalProfileDocument");
    public final IRI Project = createClass(NS, "Project");
    public final IRI OnlineChatAccount = createClass(NS, "OnlineChatAccount");
    public final IRI OnlineAccount = createClass(NS, "OnlineAccount");
    public final IRI Agent = createClass(NS, "Agent");
    public final IRI Group = createClass(NS, "Group");
    public final IRI OnlineGamingAccount = createClass(NS, "OnlineGamingAccount");
    public final IRI OnlineEcommerceAccount = createClass(NS, "OnlineEcommerceAccount");
    public final IRI Document = createClass(NS, "Document");
    public final IRI Organization = createClass(NS, "Organization");
    public final IRI Image = createClass(NS, "Image");

    private FOAF() {
        super(NS);
    }

}
