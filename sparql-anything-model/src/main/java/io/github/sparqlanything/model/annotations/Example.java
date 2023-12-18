package io.github.sparqlanything.model.annotations;

import io.github.sparqlanything.model.IRIArgument;
import org.apache.jena.ext.com.google.common.collect.Lists;

import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Repeatable(Examples.class)
public @interface Example {

	String getResource();

	String getQuery();

	String getDescription() default "";
}
