/*
 * Copyright (c) 2025 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sparqlanything.engine;

import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Internal query-scoped cache for SPARQL Anything.
 * This cache is automatically enabled during query execution to prevent redundant
 * triplification of the same sources in nested queries.
 * 
 * Unlike the user-level cache (use-cache option), this cache:
 * - Is always enabled (not user-configurable)
 * - Is scoped to a single query execution
 * - Is automatically cleared after query completion
 * - Prevents performance issues with nested SERVICE clauses
 * 
 * See issue #585 for more context.
 */
public class InternalQueryCache {
    
    private static final Logger logger = LoggerFactory.getLogger(InternalQueryCache.class);
    
    /**
     * Symbol used to store the internal cache in the execution context
     */
    public static final Symbol INTERNAL_CACHE_SYMBOL = Symbol.create("sparql-anything:internal-query-cache");
    
    /**
     * The actual cache storage
     */
    private final Map<String, DatasetGraph> cache;
    
    /**
     * Creates a new internal query cache
     */
    public InternalQueryCache() {
        this.cache = new HashMap<>();
    }
    
    /**
     * Gets or creates an internal cache from the execution context
     * 
     * @param context The execution context
     * @return The internal cache instance
     */
    public static InternalQueryCache get(Context context) {
        if (context == null) {
            return new InternalQueryCache();
        }
        
        InternalQueryCache cache = context.get(INTERNAL_CACHE_SYMBOL);
        if (cache == null) {
            cache = new InternalQueryCache();
            context.set(INTERNAL_CACHE_SYMBOL, cache);
            logger.debug("Created new internal query cache");
        }
        return cache;
    }
    
    /**
     * Checks if a key exists in the cache
     * 
     * @param key The cache key
     * @return true if the key exists
     */
    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }
    
    /**
     * Gets a dataset graph from the cache
     * 
     * @param key The cache key
     * @return The cached dataset graph, or null if not found
     */
    public DatasetGraph get(String key) {
        DatasetGraph dg = cache.get(key);
        if (dg != null) {
            logger.debug("Internal cache HIT for key: {}", key.substring(0, Math.min(50, key.length())));
        }
        return dg;
    }
    
    /**
     * Puts a dataset graph into the cache
     * 
     * @param key The cache key
     * @param datasetGraph The dataset graph to cache
     */
    public void put(String key, DatasetGraph datasetGraph) {
        if (!cache.containsKey(key)) {
            cache.put(key, datasetGraph);
            logger.debug("Internal cache STORE for key: {}", key.substring(0, Math.min(50, key.length())));
        }
    }
    
    /**
     * Clears the internal cache
     */
    public void clear() {
        int size = cache.size();
        cache.clear();
        logger.debug("Internal cache CLEARED ({} entries)", size);
    }
    
    /**
     * Gets the current size of the cache
     * 
     * @return The number of entries in the cache
     */
    public int size() {
        return cache.size();
    }
}
