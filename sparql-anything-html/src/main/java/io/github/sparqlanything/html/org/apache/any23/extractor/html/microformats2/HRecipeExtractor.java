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

package io.github.sparqlanything.html.org.apache.any23.extractor.html.microformats2;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.vocab.HRecipe;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Node;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.EntityBasedMicroformatExtractor;
import io.github.sparqlanything.html.org.apache.any23.extractor.html.HTMLDocument;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hrecipe">hRecipe</a> microformat.
 *
 * @author Nisala Nirmana
 */
public class HRecipeExtractor extends EntityBasedMicroformatExtractor {

    private static final HRecipe vHRECIPE = HRecipe.getInstance();

    private static final String[] recipeFields = { "name", "ingredient", "yield", "instructions", "duration", "photo",
            "summary", "author", "published", "nutrition" };

    @Override
    public ExtractorDescription getDescription() {
        return HRecipeExtractorFactory.getDescriptionInstance();
    }

    @Override
    protected String getBaseClassName() {
        return Microformats2Prefixes.CLASS_PREFIX + "recipe";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    @Override
    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        final BNode recipe = getBlankNodeFor(node);
        conditionallyAddResourceProperty(recipe, RDF.TYPE, vHRECIPE.Recipe);
        final HTMLDocument fragment = new HTMLDocument(node);
        addName(fragment, recipe);
        addIngredients(fragment, recipe);
        addYield(fragment, recipe);
        addInstructions(fragment, recipe);
        addDurations(fragment, recipe);
        addPhoto(fragment, recipe);
        addSummary(fragment, recipe);
        addAuthors(fragment, recipe);
        addPublished(fragment, recipe);
        addNutritions(fragment, recipe);
        return true;
    }

    private void mapFieldWithProperty(HTMLDocument fragment, BNode recipe, String fieldClass, IRI property) {
        HTMLDocument.TextField title = fragment.getSingularTextField(fieldClass);
        conditionallyAddStringProperty(title.source(), recipe, property, title.value());
    }

    private void addName(HTMLDocument fragment, BNode recipe) {
        mapFieldWithProperty(fragment, recipe, Microformats2Prefixes.PROPERTY_PREFIX + recipeFields[0], vHRECIPE.fn);
    }

    private void addIngredients(HTMLDocument fragment, BNode recipe) {
        final HTMLDocument.TextField[] ingredients = fragment
                .getPluralTextField(Microformats2Prefixes.PROPERTY_PREFIX + recipeFields[1]);
        for (HTMLDocument.TextField ingredient : ingredients) {
            conditionallyAddStringProperty(ingredient.source(), recipe, vHRECIPE.ingredient, ingredient.value());
        }
    }

    private void addInstructions(HTMLDocument fragment, BNode recipe) {
        mapFieldWithProperty(fragment, recipe, Microformats2Prefixes.EMBEDDED_PROPERTY_PREFIX + recipeFields[2],
                vHRECIPE.instructions);
    }

    private void addYield(HTMLDocument fragment, BNode recipe) {
        mapFieldWithProperty(fragment, recipe, Microformats2Prefixes.PROPERTY_PREFIX + recipeFields[3], vHRECIPE.yield);
    }

    private void addDurations(HTMLDocument fragment, BNode recipe) {
        final HTMLDocument.TextField[] durations = fragment
                .getPluralTextField(Microformats2Prefixes.TIME_PROPERTY_PREFIX + recipeFields[4]);
        for (HTMLDocument.TextField duration : durations) {
            Node attribute = duration.source().getAttributes().getNamedItem("datetime");
            if (attribute == null) {
                conditionallyAddStringProperty(duration.source(), recipe, vHRECIPE.duration, duration.value());
            } else {
                conditionallyAddStringProperty(duration.source(), recipe, vHRECIPE.duration, attribute.getNodeValue());

            }

        }
    }

    private void addPhoto(HTMLDocument fragment, BNode recipe) throws ExtractionException {
        final HTMLDocument.TextField[] photos = fragment
                .getPluralUrlField(Microformats2Prefixes.URL_PROPERTY_PREFIX + recipeFields[5]);
        for (HTMLDocument.TextField photo : photos) {
            addIRIProperty(recipe, vHRECIPE.photo, fragment.resolveIRI(photo.value()));
        }
    }

    private void addSummary(HTMLDocument fragment, BNode recipe) {
        mapFieldWithProperty(fragment, recipe, Microformats2Prefixes.PROPERTY_PREFIX + recipeFields[6],
                vHRECIPE.summary);
    }

    private void addAuthors(HTMLDocument fragment, BNode recipe) {
        final HTMLDocument.TextField[] authors = fragment
                .getPluralTextField(Microformats2Prefixes.PROPERTY_PREFIX + recipeFields[7]);
        for (HTMLDocument.TextField author : authors) {
            conditionallyAddStringProperty(author.source(), recipe, vHRECIPE.author, author.value());
        }
    }

    private void addPublished(HTMLDocument fragment, BNode recipe) {
        final HTMLDocument.TextField[] durations = fragment
                .getPluralTextField(Microformats2Prefixes.TIME_PROPERTY_PREFIX + recipeFields[8]);
        for (HTMLDocument.TextField duration : durations) {
            Node attribute = duration.source().getAttributes().getNamedItem("datetime");
            if (attribute == null) {
                conditionallyAddStringProperty(duration.source(), recipe, vHRECIPE.published, duration.value());
            } else {
                conditionallyAddStringProperty(duration.source(), recipe, vHRECIPE.published, attribute.getNodeValue());
            }
        }
    }

    private void addNutritions(HTMLDocument fragment, BNode recipe) {
        final HTMLDocument.TextField[] nutritions = fragment
                .getPluralTextField(Microformats2Prefixes.PROPERTY_PREFIX + recipeFields[9]);
        for (HTMLDocument.TextField nutrition : nutritions) {
            conditionallyAddStringProperty(nutrition.source(), recipe, vHRECIPE.nutrition, nutrition.value());
        }
    }
}
