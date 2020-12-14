package com.github.spiceh2020.sparql.anything.engine;

import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.OpExecutorFactory;

public final class FacadeX {
    public final static OpExecutorFactory ExecutorFactory = new OpExecutorFactory() {
        @Override
        public OpExecutor create(ExecutionContext execCxt) {
            return new FacadeXOpExecutor(execCxt);
        }
    };

    public final static TriplifierRegister Registry = TriplifierRegister.getInstance();
}
