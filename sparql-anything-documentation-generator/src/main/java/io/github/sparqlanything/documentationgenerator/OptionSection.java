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

package io.github.sparqlanything.documentationgenerator;

import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.annotations.Example;
import io.github.sparqlanything.model.annotations.Option;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OptionSection {
	private final Option option;

	private final List<ExampleSection> examples;

	private final Field field;

	public OptionSection(Field field, Option option, Example[] es) {
		this.option = option;
		examples = new ArrayList<>();
		for (Example e : es) {
			examples.add(new ExampleSection(e));
		}
		this.field = field;
	}

	public List<ExampleSection> getExamples() {
		return this.examples;
	}

	public String getLink() throws NoSuchMethodException, IllegalAccessException {
		return getName().toLowerCase().replace(".", "");
	}

	public String getName() throws NoSuchMethodException, IllegalAccessException {
		return this.field.get(null).toString();
	}

	public String getDescription() {
		return Utils.addLinkToIssues(option.description());
	}

	public String getValidValues() {
		return option.validValues();
	}

	public String getDefaultValue() throws IllegalAccessException {
		String defaultValue = ((IRIArgument) this.field.get(null)).getDefaultValue();
		if (defaultValue == null) return "Not set";
		return String.format("`%s`", defaultValue);
	}



}
