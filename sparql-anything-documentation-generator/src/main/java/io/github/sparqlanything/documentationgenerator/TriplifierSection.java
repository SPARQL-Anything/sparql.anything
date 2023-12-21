package io.github.sparqlanything.documentationgenerator;

public class TriplifierSection {

	private String className;
	private Class<?> klazz;

	public TriplifierSection(String className, Class<?> klazz) {
		this.className = className;
		this.klazz = klazz;
	}

	public String getClassName() {
		return className;
	}

	public Class<?> getKlazz() {
		return klazz;
	}
}
