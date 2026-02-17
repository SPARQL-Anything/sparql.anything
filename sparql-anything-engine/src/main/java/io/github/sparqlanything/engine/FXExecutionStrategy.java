package io.github.sparqlanything.engine;

import io.github.sparqlanything.model.TriplifierHTTPException;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface FXExecutionStrategy {
	public QueryIterator execute(Op op, QueryIterator input) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, TriplifierHTTPException, IOException;
}
