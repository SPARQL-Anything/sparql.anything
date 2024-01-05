package io.github.sparqlanything.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE})
public @interface Format {

	String description() default "";

	String query() default "";

	String name();

	String resourceExample();

	boolean binary() default false;


}
