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

/*
 */

package io.github.sparqlanything.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for documenting SPARQL Anything functions.
 * Used to generate YASGUI snippets and documentation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FXFunctionDoc {

	/**
	 * Human-readable description of what the function does.
	 */
	String description();

	/**
	 * Example usage of the function in a SPARQL query snippet.
	 */
	String example();

	/**
	 * Group/category for organizing snippets.
	 * Examples: "FUNCTIONS", "String Functions", "Collection Functions"
	 */
	String group() default "FUNCTIONS";

	/**
	 * Display label for the snippet.
	 * If empty, derived from the function URI.
	 */
	String label() default "";
}
