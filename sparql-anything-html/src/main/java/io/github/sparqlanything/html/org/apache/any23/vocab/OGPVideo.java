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
 * The <a href="http://ogp.me/#type_video">Open Graph Protocol Video Type</a> vocabulary.
 */
public class OGPVideo extends Vocabulary {

    private OGPVideo() {
        super(NS);
    }

    public static final String NS = "http://ogp.me/ns/video#";

    /* BEGIN: ogp.me/#type_video.movie */

    /** Actors in the movie. */
    public static final String VIDEO__ACTOR = "video:actor";

    /** The role they played. */
    public static final String VIDEO__ACTOR_ROLE = "video:actor:role";

    /** Directors of the movie. */
    public static final String VIDEO__DIRECTOR = "video:director";

    /** Writers of the movie. */
    public static final String VIDEO__WRITER = "video:writer";

    /** The movie's length in seconds. */
    public static final String VIDEO__DURATION = "video:duration";

    /** The date the movie was released. */
    public static final String VIDEO__RELEASE_DATE = "video:release_date";

    /** Tag words associated with this movie. */
    public static final String VIDEO__TAG = "video:tag";

    /** Which series this episode belongs to. */
    public static final String VIDEO__SERIES = "video:series";
    /* END: ogp.me/#type_video.movie */

    private static OGPVideo instance;

    public static OGPVideo getInstance() {
        if (instance == null) {
            instance = new OGPVideo();
        }
        return instance;
    }

    public final IRI NAMESPACE = createIRI(NS);

    public final IRI videoActor = createProperty(VIDEO__ACTOR);
    public final IRI videoActorRole = createProperty(VIDEO__ACTOR_ROLE);
    public final IRI videoDirector = createProperty(VIDEO__DIRECTOR);
    public final IRI videoWriter = createProperty(VIDEO__WRITER);
    public final IRI videoDuration = createProperty(VIDEO__DURATION);
    public final IRI videoReleaseDate = createProperty(VIDEO__RELEASE_DATE);
    public final IRI videoTag = createProperty(VIDEO__TAG);
    public final IRI videoSeries = createProperty(VIDEO__SERIES);

    @SuppressWarnings("unused")
    private IRI createClass(String localName) {
        return createClass(NS, localName);
    }

    private IRI createProperty(String localName) {
        return createProperty(NS, localName);
    }

}
