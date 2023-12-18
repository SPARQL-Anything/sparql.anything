package io.github.sparqlanything.model.annotations;

public @interface Option {

	String getDescription();

	String getValidValues();

	String getDefaultValue();

	String getName();

}
