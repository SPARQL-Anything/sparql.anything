package io.github.sparqlanything.model.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(Examples.class)
public @interface Example {

	String resource() default "Inline content";

	String query();

	String description() default "";
}
