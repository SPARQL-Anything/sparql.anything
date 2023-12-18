package io.github.sparqlanything.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE})
public @interface Format {

	String getDescription() default "";

	String getName();

	String[] getExtensions();

	String[] getMediaTypes();

	Class<?>[] getTriplifiers();

	String getResourceExample();

	boolean showGraphs();

}
