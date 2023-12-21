package io.github.sparqlanything.documentationgenerator;

import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.annotations.Example;
import io.github.sparqlanything.model.annotations.Option;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class OptionSection {
	private final Option option;

	private List<ExampleSection> examples;

	private Field field;

	public OptionSection(Field field, Option option, Example[] es) {
		this.option = option;
		examples = new ArrayList<>();
		for (Example e : es) {
			examples.add(new ExampleSection(e));
		}
		this.field = field;
	}

	public Option getOption() {
		return option;
	}

	public List<ExampleSection> getExamples() {
		return this.examples;
	}

	public String getName() throws NoSuchMethodException, IllegalAccessException {
		return this.field.get(null).toString();
	}

	public String getDescription() {
		return option.description();
	}

	public String getValidValues() {
		return option.validValues();
	}

	public String getDefaultValue() throws IllegalAccessException {
		return ((IRIArgument)this.field.get(null)).getDefaultValue();
	}

}
