package io.github.sparqlanything.documentationgenerator;

import io.github.sparqlanything.model.annotations.Example;
import io.github.sparqlanything.model.annotations.Option;

import java.util.ArrayList;
import java.util.List;

public class OptionSection {
	private final Option option;

	private List<ExampleSection> examples;

	public OptionSection(Option option, Example[] es) {
		this.option = option;
		examples = new ArrayList<>();
		for (Example e : es) {
			examples.add(new ExampleSection(e));
		}
	}

	public Option getOption() {
		return option;
	}

	public List<ExampleSection> getExamples() {
		return this.examples;
	}

	public String getName() {
		return option.name();
	}

	public String getDescription() {
		return option.description();
	}

	public String getValidValues() {
		return option.validValues();
	}

	public String getDefaultValue() {
		return option.defaultValue();
	}

}
