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

package io.github.sparqlanything.html.org.apache.any23.validator;

import io.github.sparqlanything.html.org.apache.any23.validator.RuleContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link io.github.sparqlanything.html.org.apache.any23.validator.RuleContext}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class DefaultRuleContext implements RuleContext<Object> {

    private Map<String, Object> data = new HashMap<String, Object>();

    public void putData(String name, Object value) {
        data.put(name, value);
    }

    public Object getData(String name) {
        return data.get(name);
    }

    public void removeData(String name) {
        data.remove(name);
    }

}
