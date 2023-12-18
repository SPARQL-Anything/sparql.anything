package io.github.sparqlanything.documentationgenerator;

import io.github.sparqlanything.model.annotations.Example;
import io.github.sparqlanything.model.annotations.Option;

import java.util.ArrayList;
import java.util.List;

public class OptionSection {
	private Option option;
	private Example[] examples;

	public OptionSection(Option option, Example[] examples) {
		this.option = option;
		this.examples = examples;
	}

	public Option getOption() {
		return option;
	}

	public List<ExampleSection> getExamples() {
		List<ExampleSection> result = new ArrayList<>();
		for(Example e:this.examples){
			result.add(new ExampleSection(e));
		}
		return result;
	}

}
