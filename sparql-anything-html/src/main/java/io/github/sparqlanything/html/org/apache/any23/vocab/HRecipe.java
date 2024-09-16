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
 * Vocabulary to map the <a href="http://microformats.org/wiki/hrecipe">hRecipe</a> microformat.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class HRecipe extends Vocabulary {

    public static final String NS = SINDICE.NS + "hrecipe/";

    private static HRecipe instance;

    public static HRecipe getInstance() {
        if (instance == null) {
            instance = new HRecipe();
        }
        return instance;
    }

    // Resources.
    public IRI Recipe = createClass(NS, "Recipe");
    public IRI Duration = createClass(NS, "Duration");
    public IRI Ingredient = createClass(NS, "Ingredient");
    public IRI Nutrition = createClass(NS, "Nutrition");

    // Properties.
    public IRI fn = createProperty(NS, "fn");
    public IRI duration = createProperty(NS, "duration");
    public IRI durationTitle = createProperty(NS, "durationTitle");
    public IRI durationTime = createProperty(NS, "durationTime");
    public IRI photo = createProperty(NS, "photo");
    public IRI summary = createProperty(NS, "summary");
    public IRI author = createProperty(NS, "author");
    public IRI published = createProperty(NS, "published");
    public IRI nutrition = createProperty(NS, "nutrition");
    public IRI nutritionValue = createProperty(NS, "nutritionValue");
    public IRI nutritionValueType = createProperty(NS, "nutritionValueType");
    public IRI tag = createProperty(NS, "tag");
    public IRI ingredient = createProperty(NS, "ingredient");
    public IRI ingredientName = createProperty(NS, "ingredientName");
    public IRI ingredientQuantity = createProperty(NS, "ingredientQuantity");
    public IRI ingredientQuantityType = createProperty(NS, "ingredientQuantityType");
    public IRI instructions = createProperty(NS, "instructions");
    public IRI yield = createProperty(NS, "yield");

    private HRecipe() {
        super(NS);
    }
}
