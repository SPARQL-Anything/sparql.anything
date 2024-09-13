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
 * The <a href="https://github.com/edumbill/doap/wiki">Description Of A Project</a> vocabulary.
 *
 * @author lewismc
 */
public class DOAP extends Vocabulary {

    public static final String NS = "http://usefulinc.com/ns/doap#";

    private static DOAP instance;

    public static DOAP getInstance() {
        if (instance == null) {
            instance = new DOAP();
        }
        return instance;
    }

    // Resources
    public final IRI Project = createClass(NS, "Project");
    public final IRI Version = createClass(NS, "Version");
    public final IRI Specification = createClass(NS, "Specification");
    public final IRI Repository = createClass(NS, "Repository");
    public final IRI SVNRepository = createClass(NS, "SVNRepository");
    public final IRI BKRepository = createClass(NS, "BKRepository");
    public final IRI CVSRepository = createClass(NS, "CVSRepository");
    public final IRI ArchRepository = createClass(NS, "ArchRepository");
    public final IRI BazaarBranch = createClass(NS, "BazaarBranch");
    public final IRI GitRepository = createClass(NS, "GitRepository");
    public final IRI HgRepository = createClass(NS, "HgRepository");
    public final IRI DarcsRepository = createClass(NS, "DarcsRepository");

    // Properties
    public final IRI name = createProperty(NS, "name");
    public final IRI homepage = createProperty(NS, "homepage");
    public final IRI old_homepage = createProperty(NS, "old-homepage");
    public final IRI created = createProperty(NS, "created");
    public final IRI shortdesc = createProperty(NS, "shortdesc");
    public final IRI description = createProperty(NS, "description");
    public final IRI release = createProperty(NS, "release");
    public final IRI mailing_list = createProperty(NS, "mailing-list");
    public final IRI category = createProperty(NS, "category");
    public final IRI license = createProperty(NS, "license");
    public final IRI repository = createProperty(NS, "repository");
    public final IRI anon_root = createProperty(NS, "anon-root");
    public final IRI browse = createProperty(NS, "browse");
    public final IRI module = createProperty(NS, "module");
    public final IRI location = createProperty(NS, "location");
    public final IRI download_page = createProperty(NS, "download-page");
    public final IRI download_mirror = createProperty(NS, "download-mirror");
    public final IRI revision = createProperty(NS, "revision");
    public final IRI file_release = createProperty(NS, "file-release");
    public final IRI wiki = createProperty(NS, "wiki");
    public final IRI bug_database = createProperty(NS, "bug-database");
    public final IRI screenshots = createProperty(NS, "screenshots");
    public final IRI maintainer = createProperty(NS, "maintainer");
    public final IRI developer = createProperty(NS, "developer");
    public final IRI documenter = createProperty(NS, "documenter");
    public final IRI translator = createProperty(NS, "translator");
    public final IRI tester = createProperty(NS, "tester");
    public final IRI helper = createProperty(NS, "helper");
    public final IRI programming_language = createProperty(NS, "programming-language");
    public final IRI os = createProperty(NS, "os");
    public final IRI implement = createProperty(NS, "implement");
    public final IRI service_endpoint = createProperty(NS, "service-endpoint");
    public final IRI language = createProperty(NS, "language");
    public final IRI vendor = createProperty(NS, "vendor");
    public final IRI platform = createProperty(NS, "platform");
    public final IRI audience = createProperty(NS, "audience");
    public final IRI blog = createProperty(NS, "blog");

    private DOAP() {
        super(NS);
    }
}
