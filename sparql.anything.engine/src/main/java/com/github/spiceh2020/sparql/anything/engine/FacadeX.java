package com.github.spiceh2020.sparql.anything.engine;

import com.github.spiceh2020.sparql.anything.binary.BinaryTriplifier;
import com.github.spiceh2020.sparql.anything.csv.CSVTriplifier;
import com.github.spiceh2020.sparql.anything.html.HTMLTriplifier;
import com.github.spiceh2020.sparql.anything.json.JSONTriplifier;
import com.github.spiceh2020.sparql.anything.text.TextTriplifier;
import com.github.spiceh2020.sparql.anything.xml.XMLTriplifier;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.OpExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class FacadeX {
    private static final Logger log = LoggerFactory.getLogger(FacadeX.class);
    public final static OpExecutorFactory ExecutorFactory = new OpExecutorFactory() {
        @Override
        public OpExecutor create(ExecutionContext execCxt) {
            return new FacadeXOpExecutor(execCxt);
        }
    };

    public final static TriplifierRegister Registry = TriplifierRegister.getInstance();

    static {
        try {
            log.trace("Registering standard triplifiers");
            Registry.registerTriplifier(new XMLTriplifier());
            Registry.registerTriplifier(new CSVTriplifier());
            Registry.registerTriplifier(new HTMLTriplifier());
            Registry.registerTriplifier(new TextTriplifier());
            Registry.registerTriplifier(new BinaryTriplifier());
            Registry.registerTriplifier(new JSONTriplifier());

            // Common image file types
            Registry.registerTriplifier(new BinaryTriplifier(){
                @Override
                public Set<String> getExtensions() {
                    return Collections.unmodifiableSet(new HashSet(Arrays.asList(
                            "image/png","image/jpeg","image/bmp", "image/tiff", "image/vnd.microsoft.icon"
                    )));
                }

                @Override
                public Set<String> getMimeTypes() {
                    return Collections.unmodifiableSet(new HashSet(Arrays.asList(
                            "png","jpeg","jpg","bmp","tiff","tif", "ico"
                    )));
                }
            });
        } catch (TriplifierRegisterException e) {
            throw new RuntimeException(e);
        }
    }
}
