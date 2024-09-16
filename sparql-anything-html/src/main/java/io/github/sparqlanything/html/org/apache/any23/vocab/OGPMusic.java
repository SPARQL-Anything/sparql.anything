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
 * The <a href="http://ogp.me/#type_music">Open Graph Protocol Music Type</a> vocabulary.
 *
 */
public class OGPMusic extends Vocabulary {

    private OGPMusic() {
        super(NS);
    }

    public static final String NS = "http://ogp.me/ns/music#";

    /* BEGIN: http://ogp.me/#type_music.song */

    /** The song's length in seconds. */
    public static final String MUSIC__DURATION = "music:duration";

    /** The album this song is from. */
    public static final String MUSIC__ALBUM = "music:album";

    /** Which disc of the album this song is on. */
    public static final String MUSIC__ALBUM_DISC = "music:album:disc";

    /** Which disc of the album this song is on. */
    public static final String MUSIC__ALBUM_TRACK = "music:album:track";

    /** Which disc of the album this song is on. */
    public static final String MUSIC__MUSICIAN = "music:musician";

    /* END: http://ogp.me/#type_music.song */

    /* BEGIN: http://ogp.me/#type_music.album */

    /** The song on this album. */
    public static final String MUSIC__SONG = "music:song";

    /** The same as music:album:disc but in reverse. */
    public static final String MUSIC__SONG_DISC = "music:song:disc";

    /** The same as music:album:track but in reverse. */
    public static final String MUSIC__SONG_TRACK = "music:song:track";

    /** The date the album was released. */
    public static final String MUSIC__RELEASE_DATE = "music:release_date";

    /* END: http://ogp.me/#type_music.album */

    /* BEGIN: http://ogp.me/#type_music.playlist */

    /**
     * The creator of this playlist if 'music.playlist' or the creator of this station if 'music.radio_station'
     */
    public static final String MUSIC__CREATOR = "music:creator";

    /* END: http://ogp.me/#type_music.playlist */

    private static OGPMusic instance;

    public static OGPMusic getInstance() {
        if (instance == null) {
            instance = new OGPMusic();
        }
        return instance;
    }

    public final IRI NAMESPACE = createIRI(NS);

    public final IRI musicDuration = createProperty(MUSIC__DURATION);
    public final IRI musicAlbum = createProperty(MUSIC__ALBUM);
    public final IRI musicAlbumDisc = createProperty(MUSIC__ALBUM_DISC);
    public final IRI musicAlbumTrack = createProperty(MUSIC__ALBUM_TRACK);
    public final IRI musicMusician = createProperty(MUSIC__MUSICIAN);

    public final IRI musicSong = createProperty(MUSIC__SONG);
    public final IRI musicSongDisc = createProperty(MUSIC__SONG_DISC);
    public final IRI musicSongTrack = createProperty(MUSIC__SONG_TRACK);
    public final IRI musicReleaseDate = createProperty(MUSIC__RELEASE_DATE);

    public final IRI musicCreator = createProperty(MUSIC__CREATOR);

    @SuppressWarnings("unused")
    private IRI createClass(String localName) {
        return createClass(NS, localName);
    }

    private IRI createProperty(String localName) {
        return createProperty(NS, localName);
    }
}
