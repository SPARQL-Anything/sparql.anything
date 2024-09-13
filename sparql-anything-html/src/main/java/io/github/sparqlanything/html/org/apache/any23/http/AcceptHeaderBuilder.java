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

package io.github.sparqlanything.html.org.apache.any23.http;

import io.github.sparqlanything.html.org.apache.any23.mime.MIMEType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Concatenates a collection of MIME specs in "type/subtype;q=x.x" notation into an HTTP Accept header value, and
 * removes duplicates and types covered by wildcards. For example, if the type list contains "text/*;q=0.5", then
 * "text/plain;q=0.1" in the list will be ignored because it's already covered by the wildcard with a higher q value.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class AcceptHeaderBuilder {

    private Collection<MIMEType> mimeTypes;

    private MIMEType highestAnyType = null;

    private Map<String, MIMEType> highestAnySubtype = new HashMap<String, MIMEType>();

    private Map<String, MIMEType> highestSpecificType = new HashMap<String, MIMEType>();

    public static AcceptHeaderBuilder fromStrings(Collection<String> typesAsStrings) {
        Collection<MIMEType> types = new ArrayList<MIMEType>(typesAsStrings.size());
        for (String type : typesAsStrings) {
            types.add(MIMEType.parse(type));
        }
        return new AcceptHeaderBuilder(types);
    }

    public AcceptHeaderBuilder(Collection<MIMEType> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    /**
     * Builds and returns an accept header.
     *
     * @return the accept header.
     */
    public String getAcceptHeader() {
        if (mimeTypes.isEmpty())
            return null;
        for (MIMEType mimeType : mimeTypes) {
            add(mimeType);
        }
        removeSpecificTypesCoveredByWildcard();
        removeTypesCoveredByWildcard();
        List<MIMEType> highest = new ArrayList<MIMEType>();
        if (highestAnyType != null) {
            highest.add(highestAnyType);
        }
        highest.addAll(highestAnySubtype.values());
        highest.addAll(highestSpecificType.values());
        Collections.sort(highest);
        StringBuffer result = new StringBuffer();
        Iterator<MIMEType> it = mimeTypes.iterator();
        while (it.hasNext()) {
            MIMEType a = it.next();
            if (!highest.contains(a))
                continue;
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(a);
        }
        return result.toString();
    }

    private void add(MIMEType newAccept) {
        if (newAccept.isAnyMajorType()) {
            if (highestAnyType == null || newAccept.getQuality() > highestAnyType.getQuality()) {
                highestAnyType = newAccept;
            }
        } else if (newAccept.isAnySubtype()) {
            if (!highestAnySubtype.containsKey(newAccept.getMajorType())
                    || newAccept.getQuality() > highestAnySubtype.get(newAccept.getMajorType()).getQuality()) {
                highestAnySubtype.put(newAccept.getMajorType(), newAccept);
            }
        } else {
            if (!highestSpecificType.containsKey(newAccept.getFullType())
                    || newAccept.getQuality() > highestSpecificType.get(newAccept.getFullType()).getQuality()) {
                highestSpecificType.put(newAccept.getFullType(), newAccept);
            }
        }
    }

    private void removeSpecificTypesCoveredByWildcard() {
        for (MIMEType accept : highestSpecificType.values()) {
            if (highestAnySubtype.containsKey(accept.getMajorType())
                    && accept.getQuality() <= highestAnySubtype.get(accept.getMajorType()).getQuality()) {
                highestSpecificType.remove(accept.getFullType());
            }
        }
        if (highestAnyType == null)
            return;
        for (MIMEType accept : highestSpecificType.values()) {
            if (accept.getQuality() <= highestAnyType.getQuality()) {
                highestSpecificType.remove(accept.getFullType());
            }
        }
    }

    private void removeTypesCoveredByWildcard() {
        if (highestAnyType == null)
            return;
        for (MIMEType accept : highestAnySubtype.values()) {
            if (accept.getQuality() <= highestAnyType.getQuality()) {
                highestAnySubtype.remove(accept.getMajorType());
            }
        }
    }

}
