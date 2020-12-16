package com.github.spiceh2020.sparql.anything.it;

import com.github.spiceh2020.sparql.anything.engine.FacadeX;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

public class ItTest {
    private static final Logger log = LoggerFactory.getLogger(ItTest.class);

    @Test
    public void RegistryTest(){
        log.info("jpg {}",FacadeX.Registry.getTriplifierForExtension("jpg"));
        log.info("png {}",FacadeX.Registry.getTriplifierForExtension("png"));
        log.info("json {}",FacadeX.Registry.getTriplifierForExtension("json"));
        log.info("bin {}",FacadeX.Registry.getTriplifierForExtension("bin"));
        log.info("xml {}",FacadeX.Registry.getTriplifierForExtension("xml"));
        log.info("html {}",FacadeX.Registry.getTriplifierForExtension("html"));
//        log.info("{}",FacadeX.Registry.getTriplifierForExtension("jpg"));
    }
    @Test
    public void CSV1() throws URISyntaxException {
        Dataset kb = DatasetFactory.createGeneral();
        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
        String location = getClass().getClassLoader().getResource("test1.csv").toURI().toString();
        Query query = QueryFactory.create("SELECT * WHERE { SERVICE <facade-x:location=" + location + "> { ?s ?p ?o }}");
        QueryExecutionFactory.create(query, kb).execSelect();
    }


    @Test
    public void JSON1() throws URISyntaxException {
        // a01009-14709.json
        Dataset kb = DatasetFactory.createGeneral();
        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
        String location = getClass().getClassLoader().getResource("tate-gallery/a01009-14709.json").toURI().toString();
        Query query = QueryFactory.create("SELECT * WHERE { SERVICE <facade-x:location=" + location + "> { ?s ?p ?o }}");
        QueryExecutionFactory.create(query, kb).execSelect();
    }

    @Test
    public void JPG1()throws URISyntaxException {
        // A01009_8.jpg
        Dataset kb = DatasetFactory.createGeneral();
        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
        String location = getClass().getClassLoader().getResource("tate-gallery/A01009_8.jpg").toURI().toString();
        Query query = QueryFactory.create("SELECT * WHERE { SERVICE <facade-x:location=" + location + "> { ?s ?p ?o }}");
        QueryExecutionFactory.create(query, kb).execSelect();
    }
}
