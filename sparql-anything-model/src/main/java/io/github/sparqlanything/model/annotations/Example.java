package io.github.sparqlanything.model.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(Examples.class)
public @interface Example {

	String resource();

	String query();

	String description() default "";
}
