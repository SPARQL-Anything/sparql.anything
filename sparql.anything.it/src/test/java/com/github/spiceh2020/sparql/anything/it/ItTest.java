package com.github.spiceh2020.sparql.anything.it;

import com.github.spiceh2020.sparql.anything.engine.FacadeX;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Test;

import java.net.URISyntaxException;

public class ItTest {
    @Test
    public void test() throws URISyntaxException {
        Dataset kb = DatasetFactory.createGeneral();
        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
        String location = getClass().getClassLoader().getResource("test1.csv").toURI().toString();
        Query query = QueryFactory.create("SELECT * WHERE { SERVICE <facade-x:location=" + location + "> { ?s ?p ?o }}");
        QueryExecutionFactory.create(query, kb).execSelect();
    }
}
