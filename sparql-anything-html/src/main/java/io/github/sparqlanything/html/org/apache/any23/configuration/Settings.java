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

package io.github.sparqlanything.html.org.apache.any23.configuration;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This class represents an <i>immutable</i> {@link Set} of {@link Setting} objects, with the additional property that
 * no two settings having the same {@link Setting#getIdentifier() identifier} can be simultaneously present in a
 * {@code Settings} object.
 *
 * @author Hans Brende (hansbrende@apache.org)
 */
public final class Settings extends AbstractSet<Setting<?>> {

    private static final Settings EMPTY_SETTINGS = new Settings(Collections.emptyMap());

    private final Map<String, Setting<?>> values;

    private Settings(Map<String, Setting<?>> values) {
        this.values = values;
    }

    /**
     * @param identifier
     *            the identifier of the setting to find
     *
     * @return the setting with the identifier supplied, if present
     */
    public Optional<Setting<?>> find(String identifier) {
        return Optional.ofNullable(values.get(identifier));
    }

    /**
     * This method is semantically equivalent to: <br>
     * <br>
     *
     * <pre>
     * {@code find(setting.getIdentifier()).flatMap(s -> s.as(setting))}
     * </pre>
     *
     * @param <S>
     *            generic setting type
     * @param setting
     *            a setting key
     *
     * @return the setting with the same setting key as the supplied setting, if present.
     */
    public <S extends Setting<?>> Optional<S> find(S setting) {
        Setting<?> found = values.get(setting.getIdentifier());
        return found == null ? Optional.empty() : found.as(setting);
    }

    /**
     * This method is semantically equivalent to: <br>
     * <br>
     *
     * <pre>
     * {@code find(defaultSetting).orElse(defaultSetting).getValue()}
     * </pre>
     *
     * @param <E>
     *            generic setting type
     * @param defaultSetting
     *            a default key for which to obtain a value set
     *
     * @return the value set for {@code defaultSetting}'s key, if present. Otherwise, returns {@code defaultSetting}'s
     *         value.
     */
    public <E> E get(Setting<E> defaultSetting) {
        return find(defaultSetting).orElse(defaultSetting).getValue();
    }

    ///////////////////////////////////////
    // AbstractSet overrides
    ///////////////////////////////////////

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Setting<?>)) {
            return false;
        }
        return o.equals(values.get(((Setting<?>) o).getIdentifier()));
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public Iterator<Setting<?>> iterator() {
        return values.values().iterator();
    }

    ///////////////////////////////////////
    // public constructors
    ///////////////////////////////////////

    /**
     * Returns an empty {@link Settings} object.
     *
     * @return an empty {@link Settings} object
     */
    public static Settings of() {
        return EMPTY_SETTINGS;
    }

    /**
     * Returns a singleton {@link Settings} object, containing only the supplied Setting.
     *
     * @param s
     *            one {@link io.github.sparqlanything.html.org.apache.any23.configuration.Setting}
     *
     * @return a {@link Settings} object containing the supplied Setting.
     */
    public static Settings of(Setting<?> s) {
        return new Settings(Collections.singletonMap(s.getIdentifier(), s));
    }

    /**
     * @param settings
     *            one or more {@link io.github.sparqlanything.html.org.apache.any23.configuration.Setting}'s
     *
     * @return a {@link Settings} object containing the supplied settings. For any two settings having the same key, the
     *         first will be overwritten by the second.
     *
     * @throws IllegalArgumentException
     *             if any two settings have the same identifier
     */
    public static Settings of(Setting<?>... settings) {
        Map<String, Setting<?>> map = mapForSize(settings.length);
        for (Setting<?> s : settings)
            put(map, s);
        return ofModifiable(map);
    }

    /**
     * @param c
     *            a populated {@link Collection} of {@link io.github.sparqlanything.html.org.apache.any23.configuration.Setting}'s
     *
     * @return a {@link Settings} object containing the supplied settings.
     *
     * @throws IllegalArgumentException
     *             if any two settings have the same identifier
     */
    public static Settings of(Collection<? extends Setting<?>> c) {
        if (c instanceof Settings) {
            return (Settings) c;
        }
        int size = c.size();
        if (size == 0) {
            return EMPTY_SETTINGS;
        }
        Map<String, Setting<?>> map = mapForSize(size);
        for (Setting<?> s : c)
            put(map, s);
        return ofModifiable(map);
    }

    ///////////////////////////////////////
    // Private static helpers
    ///////////////////////////////////////

    private static Settings ofModifiable(Map<String, Setting<?>> map) {
        return new Settings(Collections.unmodifiableMap(map));
    }

    private static void put(Map<String, Setting<?>> map, Setting<?> setting) {
        Setting<?> existing = map.put(setting.getIdentifier(), setting);
        if (existing != null) {
            throw new IllegalArgumentException(setting.getIdentifier() + " is already defined");
        }
    }

    private static final float loadFactor = 0.75f;

    private static Map<String, Setting<?>> mapForSize(int size) {
        return new HashMap<>((int) (size / loadFactor) + 1, loadFactor);
    }

}
