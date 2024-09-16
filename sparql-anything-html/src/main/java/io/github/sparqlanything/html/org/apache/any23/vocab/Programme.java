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
 * <p>
 * The <a href="http://purl.org/ontology/po/">Programmes Ontology</a> is aimed at providing a simple vocabulary for
 * describing programmes.
 * </p>
 * <p>
 * It covers brands, series (seasons), episodes, broadcast events, broadcast services, etc. Its development was funded
 * by the BBC, and is heavily grounded on previous programmes data modeling work done there.
 * </p>
 *
 * @author lewismc
 */
public class Programme extends Vocabulary {

    public static final String NS = "http://purl.org/ontology/po/";

    private static Programme instance;

    public static Programme getInstance() {
        if (instance == null) {
            instance = new Programme();
        }
        return instance;
    }

    // Resources
    /** A version holding an audio description. */
    public final IRI AudioDescribedVersion = createClass(NS, "AudioDescribedVersion");

    /** A brand, e.g. `Top Gea`r */
    public final IRI Brand = createClass(NS, "Brand");

    /**
     * A broadcast event. Subsumes the event concept defined in the Event ontology. A broadcast is associated with a
     * service, and with a particular version of an episode.
     */
    public final IRI Broadcast = createClass(NS, "Broadcast");

    /**
     * An organization responsible of some broadcasting services. It can hold a set of services and outlets.
     */
    public final IRI Broadcaster = createClass(NS, "Broadcaster");

    /**
     * A category provides a way of classifying a set of programmes. Such classifications can be performed according to
     * multiple dimensions and taxonomies, e.g. genre, format, places, people, subjects...
     */
    public final IRI Category = createClass(NS, "Category");

    /**
     * A physical channel on which a broadcast occurs. A single outlet or service can be associated with multiple
     * channels. For example, Radio 4 LW broadcasts on Analogue Long Wave and on Digital Satellite.
     */
    public final IRI Channel = createClass(NS, "Channel");

    /** A particular clip, e.g. `Clip of Top Gear, first series' */
    public final IRI Clip = createClass(NS, "Clip");

    /**
     * Digital Audio Broadcasting
     */
    public final IRI DAB = createClass(NS, "DAB");

    /** Digital Video Broadcasting */
    public final IRI DVB = createClass(NS, "DVB");

    /**
     * A particular episode, e.g. `Top Gear, first episode of the first series' or the film 'A Walk in the Sun'
     * (http://www.bbc.co.uk/programmes/b00gfzdt)
     */
    public final IRI Episode = createClass(NS, "Episode");

    /** The FM broadcast band */
    public final IRI FM = createClass(NS, "FM");

    /** Specifies a broadcast as being the first one of a particular version. */
    public final IRI FirstBroadcast = createClass(NS, "FirstBroadcast");

    /**
     * Anchor point for format taxonomies, similar to po:Genre for genre taxonomies. Instances of this concept include
     * documentaries, talk shows, animation, etc.
     */
    public final IRI Format = createClass(NS, "Format");

    /**
     * An anchor point for a programmes' genre taxonomy, # e.g. 'Drama'/'Biographical'.
     */
    public final IRI Genre = createClass(NS, "Genre");

    /** IP Stream */
    public final IRI IPStream = createClass(NS, "IPStream");

    /** The AM broadcast band. */
    public final IRI LW = createClass(NS, "LW");

    /** Radio services aiming at a local coverage. */
    public final IRI LocalRadio = createClass(NS, "LocalRadio");

    /**
     * Classification of an episode version's region corresponding to a musical track being played..
     */
    public final IRI MusicSegment = createClass(NS, "MusicSegment");

    /** Radio services aiming at a national coverage. */
    public final IRI NationalRadio = createClass(NS, "NationalRadio");

    /** An `original' version, the legacy version of a particular episode. */
    public final IRI OriginalVersion = createClass(NS, "OriginalVersion");

    /**
     * Outlet of a particular service, e.g. Radio 4 LW and FM for Radio 4. Outlets are services which do not have
     * variations. The identity criteria for an outlet is its timeline. For example, Radio 4 LW broadcasts on Analogue
     * Long Wave, but also on Digital Satellite. It corresponds to just one outlet, as they are simulcasts. The two
     * physical channels for broadcasts correspond to po:Channel.
     */
    public final IRI Outlet = createClass(NS, "Outlet");

    /** A Person. */
    public final IRI Person = createClass(NS, "Person");

    /** A physical place. */
    public final IRI Place = createClass(NS, "Place");

    /** A programme, can either be a brand, a series or an episode. */
    public final IRI Programme = createClass(NS, "Programme");

    /**
     * A programme that can have versions, and as such can be broadcast or made available on-demand, e.g. a clip or an
     * episode.
     */
    public final IRI ProgrammeItem = createClass(NS, "ProgrammeItem");

    /** Services that use a radio medium. */
    public final IRI Radio = createClass(NS, "Radio");

    /** Radio services aiming at a regional coverage. */
    public final IRI RegionalRadio = createClass(NS, "RegionalRadio");

    /** Specifies a broadcast as being a repeat. */
    public final IRI RepeatBroadcast = createClass(NS, "RepeatBroadcast");

    /** A season is a group of broadcasts. */
    public final IRI Season = createClass(NS, "Season");

    /**
     * Classification of an episode version's region, e.g. 'this track was played at that time'.
     */
    public final IRI Segment = createClass(NS, "Segment");

    /** A series, e.g. `Top Gear, first season' */
    public final IRI Series = createClass(NS, "Series");

    /**
     * A broadcasting service. Instances of this concept include BBC Radio Wales, BBC Radio 4, BBC News, etc. A service
     * is a collection of outlets which contain common material, but with some variations, e.g. by region. Hence, a
     * service may have multiple outlets (po:Outlet), e.g. BBC Radio 4 has BBC Radio 4 LW and BBC Radio 4 FM. A
     * hierarchy of services types is defined within this ontology, e.g. radio and TV. A service that is a master brand
     * only (a service that only commissions programmes, e.g. BBC Switch) should be an instance of the top-level
     * po:Service.
     */
    public final IRI Service = createClass(NS, "Service");

    /** A shortened version. */
    public final IRI ShortenedVersion = createClass(NS, "ShortenedVersion");

    /** Classification of an episode version's region holding speech content. */
    public final IRI SpeechSegment = createClass(NS, "SpeechSegment");

    /** Anchor point for subject taxonomies. */
    public final IRI SignedVersion = createClass(NS, "SignedVersion");

    /** A version holding sign language. */
    public final IRI Subject = createClass(NS, "Subject");

    /** Classification of an episode version's region corresponding to a subtitle being shown. */
    public final IRI Subtitle = createClass(NS, "Subtitle");

    /** Services that use a television medium. */
    public final IRI TV = createClass(NS, "TV");

    /**
     * A particular version of an episode. Such versions include shortened ones, audio described ones or ones that holds
     * sign language. The version is associated to a timeline.
     */
    public final IRI Version = createClass(NS, "Version");

    /** Services that use a Web medium. */
    public final IRI Web = createClass(NS, "Web");

    // Properties
    /**
     * Relates a programmes to one of its actors - a person who plays the role of a character.
     */
    public final IRI actor = createProperty(NS, "actor");

    /** A television reporter who coordinates a programme. */
    public final IRI anchor = createProperty(NS, "anchor");

    /** The aspect ration of a particular version. */
    public final IRI aspect_ratio = createProperty(NS, "aspect_ratio");

    /** Relates a programme to its author - the person who created the content */
    public final IRI author = createProperty(NS, "author");

    /**
     * Relates a particular broadcast to the version being broadcasted. Sub-property of the event:factor one.
     */
    public final IRI broadcast_of = createProperty(NS, "broadcast_of");

    /**
     * Relates a particular broadcast to the service or outlet on which it was on. Sub-property of the event:factor one.
     */
    public final IRI broadcast_on = createProperty(NS, "broadcast_on");

    /** Associates a service to a broadcaster. */
    public final IRI broadcaster = createProperty(NS, "broadcaster");

    /** Relates a programme to a particular category, e.g. genre, format, place... */
    public final IRI category = createProperty(NS, "category");

    /**
     * Associates a service to a channel, e.g. Radio 4 LW to Radio 4 LW on Analogue Long Wave.
     */
    public final IRI channel = createProperty(NS, "channel");

    /** Associates a brand, a series or an episode to a clip. */
    public final IRI clip = createProperty(NS, "clip");

    /** Relates a programme to one of its commentators */
    public final IRI commentator = createProperty(NS, "commentator");

    /** Relates a programmes to a person who is credited in it */
    public final IRI credit = createProperty(NS, "credit");

    /**
     * Relates a programme to its supervisor. Generally refers to the person responsible for all audience-visible
     * components of a program, film, or show, whereas the producer is responsible for the financial and other
     * behind-the-scenes aspects. A director's duties might also include casting, script editing, shot selection, shot
     * composition, and editing
     */
    public final IRI director = createProperty(NS, "director");

    /** The duration of a version, in seconds. */
    public final IRI duration = createProperty(NS, "duration");

    /** Associates a brand or a series to an episode constituting it. */
    public final IRI episode = createProperty(NS, "episode");

    /**
     * Relates a programme to its executive producer - a producer who is not involved in any technical aspects of the
     * making process, but who is still responsible for the overall production. Typically an executive producer handles
     * business and legal issues
     */
    public final IRI executive_producer = createProperty(NS, "executive_producer");

    /** Relates a programme to a particular format (eg. `Animation', `Documentary', etc.). */
    public final IRI format = createProperty(NS, "format");

    /** Associates a channel to its frequency */
    public final IRI frequency = createProperty(NS, "frequency");

    /** Relates a programme to a particular genre. */
    public final IRI genre = createProperty(NS, "genre");

    /**
     * Associates a service to a geographic location, aiming at capturing what this service aims at covering.
     */
    public final IRI location = createProperty(NS, "location");

    /** A long synopsis of a serie, brand or episode. Sub-property of po:synopsis. */
    public final IRI long_synopsis = createProperty(NS, "long_synopsis");

    /** Associates a programme with its masterbrand (its commissionner) */
    public final IRI masterbrand = createProperty(NS, "masterbrand");

    /** A medium synopsis of a serie, brand or episode. Sub-property of po:synopsis. */
    public final IRI medium_synopsis = createProperty(NS, "medium_synopsis");

    /**
     * Associates a programme to its microsite. For example http://www.bbc.co.uk/programmes/b00fm04s and
     * http://www.bbc.co.uk/eastenders/
     */
    public final IRI microsite = createProperty(NS, "microsite");

    /** Relates a programme to its news reader */
    public final IRI news_reader = createProperty(NS, "news_reader");

    /** Associates a service to an outlet, e.g. Radio 4 to Radio 4 LW. */
    public final IRI outlet = createProperty(NS, "outlet");

    /** Relates a series to a series constituting it (eg. `Waking the dead'). */
    public final IRI parent_series = createProperty(NS, "parent_series");

    /** Relates a service to another service encapsulating it (eg. `BBC One' and `BBC One South') */
    public final IRI parent_service = createProperty(NS, "parent_service");

    /** Relates a programme to one of its participants */
    public final IRI participant = createProperty(NS, "participant");

    /** Relates a programme to an entertainer who performs a dramatic or musical work for audience */
    public final IRI performer = createProperty(NS, "performer");

    /** Relates a programme to a person */
    public final IRI person = createProperty(NS, "person");

    /** Relates a programme to a place (e.g. `London') */
    public final IRI place = createProperty(NS, "place");

    /**
     * The position of a particular series or episode within its containing programme. This property can also be used to
     * give the position of an interval within the containing timeline.
     */
    public final IRI position = createProperty(NS, "position");

    /**
     * Relates a programme to its producer - the manager of an event, show, or other work, usually the individual in
     * charge of finance, personnel, and other non-artistic aspects in the development of commercials, plays, movies,
     * and other works
     */
    public final IRI producer = createProperty(NS, "producer");

    /** The schedule date of a broadcast event. */
    public final IRI schedule_date = createProperty(NS, "schedule_date");

    /** Associates a season to its constituent broadcasts */
    public final IRI season_broadcast = createProperty(NS, "season_broadcast");

    /** Associates a brand or a series to a series constituting it. */
    public final IRI series = createProperty(NS, "series");

    /** Associate a brand, series or episode to the master brand service. */
    public final IRI service = createProperty(NS, "service");

    /** A short synopsis of a serie, brand or episode. Sub-property of po:synopsis. */
    public final IRI short_synopsis = createProperty(NS, "short_synopsis");

    /** The sound format of a particular version. */
    public final IRI sound_format = createProperty(NS, "sound_format");

    /** Relates a programme to a subject (e.g. `easter'). */
    public final IRI subject = createProperty(NS, "subject");

    /** Language of the subtitles emebedded in a particular version. */
    public final IRI subtitle_language = createProperty(NS, "subtitle_language");

    /** The synopsis of a serie, brand or episode. */
    public final IRI synopsis = createProperty(NS, "synopsis");

    /** Associates an episode to a particular tag. */
    public final IRI tag = createProperty(NS, "tag");

    /** Associates a subtitle event to the corresponding text. */
    public final IRI text = createProperty(NS, "text");

    /**
     * Associates an episode's version or a version's segment with a temporal interval. This interval can be associated
     * with a timeline, serving as an anchor for further temporal annotations, e.g. subtitles or played track.
     */
    public final IRI time = createProperty(NS, "time");

    /** Associates a music segment with a track, as defined in MO. */
    public final IRI track = createProperty(NS, "track");

    /**
     * Associate an episode to a version of it. Different versions of a same episode can exist (shortened version,
     * version designed for the hearing impaired, etc.).
     */
    public final IRI version = createProperty(NS, "version");

    private Programme() {
        super(NS);
    }
}
