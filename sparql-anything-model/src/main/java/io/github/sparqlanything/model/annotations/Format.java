package io.github.sparqlanything.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE})
public @interface Format {

	public String getDescription() default "";

	public String getName();

	public String[] getExtensions();

	public String[] getMediaTypes();

	public Class<?>[] getTriplifiers() ;

	public String getResourceExample();

	public boolean showGraphs();

}
