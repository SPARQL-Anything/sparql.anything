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

package io.github.sparqlanything.html.org.apache.any23.extractor.html;

import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionException;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractionResult;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorDescription;
import io.github.sparqlanything.html.org.apache.any23.vocab.HRecipe;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Node;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hrecipe">hRecipe</a> microformat.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class HRecipeExtractor extends EntityBasedMicroformatExtractor {

    private static final HRecipe vHRECIPE = HRecipe.getInstance();

    @Override
    public ExtractorDescription getDescription() {
        return HRecipeExtractorFactory.getDescriptionInstance();
    }

    @Override
    protected String getBaseClassName() {
        return "hrecipe";
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
        addFN(fragment, recipe);
        addIngredients(fragment, recipe);
        addYield(fragment, recipe);
        addInstructions(fragment, recipe);
        addDurations(fragment, recipe);
        addPhoto(fragment, recipe);
        addSummary(fragment, recipe);
        addAuthors(fragment, recipe);
        addPublished(fragment, recipe);
        addNutritions(fragment, recipe);
        addTags(fragment, recipe);
        return true;
    }

    /**
     * Maps a field text with a property.
     *
     * @param fragment
     * @param recipe
     * @param fieldClass
     * @param property
     */
    private void mapFieldWithProperty(HTMLDocument fragment, BNode recipe, String fieldClass, IRI property) {
        HTMLDocument.TextField title = fragment.getSingularTextField(fieldClass);
        conditionallyAddStringProperty(title.source(), recipe, property, title.value());
    }

    /**
     * Adds the <code>fn</code> triple.
     *
     * @param fragment
     * @param recipe
     */
    private void addFN(HTMLDocument fragment, BNode recipe) {
        mapFieldWithProperty(fragment, recipe, "fn", vHRECIPE.fn);
    }

    /**
     * Adds the <code>ingredient</code> triples.
     *
     * @param fragment
     * @param ingredient
     *
     * @return
     */
    private BNode addIngredient(HTMLDocument fragment, HTMLDocument.TextField ingredient) {
        final BNode ingredientBnode = getBlankNodeFor(ingredient.source());
        addIRIProperty(ingredientBnode, RDF.TYPE, vHRECIPE.Ingredient);
        conditionallyAddStringProperty(ingredient.source(), ingredientBnode, vHRECIPE.ingredientName,
                HTMLDocument.readNodeContent(ingredient.source(), true));
        mapFieldWithProperty(fragment, ingredientBnode, "value", vHRECIPE.ingredientQuantity);
        mapFieldWithProperty(fragment, ingredientBnode, "type", vHRECIPE.ingredientQuantityType);
        return ingredientBnode;
    }

    /**
     * Adds the <code>ingredients</code>list triples.
     *
     * @param fragment
     * @param recipe
     *
     * @return
     */
    private void addIngredients(HTMLDocument fragment, BNode recipe) {
        final HTMLDocument.TextField[] ingredients = fragment.getPluralTextField("ingredient");
        for (HTMLDocument.TextField ingredient : ingredients) {
            addBNodeProperty(recipe, vHRECIPE.ingredient, addIngredient(fragment, ingredient));
        }
    }

    /**
     * Adds the <code>instruction</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    private void addInstructions(HTMLDocument fragment, BNode recipe) {
        mapFieldWithProperty(fragment, recipe, "instructions", vHRECIPE.instructions);

    }

    /**
     * Adds the <code>yield</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    private void addYield(HTMLDocument fragment, BNode recipe) {
        mapFieldWithProperty(fragment, recipe, "yield", vHRECIPE.yield);
    }

    /**
     * Adds the <code>duration</code> triples.
     *
     * @param fragment
     * @param duration
     *
     * @return
     */
    // TODO: USE http://microformats.org/wiki/value-class-pattern to read correct date format.
    private BNode addDuration(HTMLDocument fragment, HTMLDocument.TextField duration) {
        final BNode durationBnode = getBlankNodeFor(duration.source());
        addIRIProperty(durationBnode, RDF.TYPE, vHRECIPE.Duration);
        conditionallyAddStringProperty(duration.source(), durationBnode, vHRECIPE.durationTime, duration.value());
        mapFieldWithProperty(fragment, durationBnode, "value-title", vHRECIPE.durationTitle);
        return durationBnode;
    }

    /**
     * Adds the <code>yield</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    private void addDurations(HTMLDocument fragment, BNode recipe) {
        final HTMLDocument.TextField[] durations = fragment.getPluralTextField("duration");
        for (HTMLDocument.TextField duration : durations) {
            addBNodeProperty(recipe, vHRECIPE.duration, addDuration(fragment, duration));
        }
    }

    /**
     * Adds the <code>photo</code> triples.
     *
     * @param fragment
     * @param recipe
     *
     * @throws ExtractionException
     */
    private void addPhoto(HTMLDocument fragment, BNode recipe) throws ExtractionException {
        final HTMLDocument.TextField[] photos = fragment.getPluralUrlField("photo");
        for (HTMLDocument.TextField photo : photos) {
            addIRIProperty(recipe, vHRECIPE.photo, fragment.resolveIRI(photo.value()));
        }
    }

    /**
     * Adds the <code>summary</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    private void addSummary(HTMLDocument fragment, BNode recipe) {
        mapFieldWithProperty(fragment, recipe, "summary", vHRECIPE.summary);
    }

    /**
     * Adds the <code>authors</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    private void addAuthors(HTMLDocument fragment, BNode recipe) {
        final HTMLDocument.TextField[] authors = fragment.getPluralTextField("author");
        for (HTMLDocument.TextField author : authors) {
            conditionallyAddStringProperty(author.source(), recipe, vHRECIPE.author, author.value());
        }
    }

    /**
     * Adds the <code>published</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    // TODO: USE http://microformats.org/wiki/value-class-pattern to read correct date format.
    private void addPublished(HTMLDocument fragment, BNode recipe) {
        mapFieldWithProperty(fragment, recipe, "published", vHRECIPE.published);
    }

    /**
     * Adds the <code>nutrition</code> triples.
     *
     * @param fragment
     * @param nutrition
     *
     * @return
     */
    private BNode addNutrition(HTMLDocument fragment, HTMLDocument.TextField nutrition) {
        final BNode nutritionBnode = getBlankNodeFor(nutrition.source());
        addIRIProperty(nutritionBnode, RDF.TYPE, vHRECIPE.Nutrition);
        conditionallyAddStringProperty(nutrition.source(), nutritionBnode, vHRECIPE.nutritionValue, nutrition.value());
        mapFieldWithProperty(fragment, nutritionBnode, "value", vHRECIPE.nutritionValue);
        mapFieldWithProperty(fragment, nutritionBnode, "type", vHRECIPE.nutritionValueType);
        return nutritionBnode;
    }

    /**
     * Adds the <code>nutritions</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    private void addNutritions(HTMLDocument fragment, BNode recipe) {
        HTMLDocument.TextField[] nutritions = fragment.getPluralTextField("nutrition");
        for (HTMLDocument.TextField nutrition : nutritions) {
            addBNodeProperty(recipe, vHRECIPE.nutrition, addNutrition(fragment, nutrition));
        }
    }

    /**
     * Adds the <code>tags</code> triples.
     *
     * @param fragment
     * @param recipe
     */
    private void addTags(HTMLDocument fragment, BNode recipe) {
        HTMLDocument.TextField[] tags = fragment.extractRelTagNodes();
        for (HTMLDocument.TextField tag : tags) {
            conditionallyAddStringProperty(tag.source(), recipe, vHRECIPE.tag, tag.value());
        }
    }

}
