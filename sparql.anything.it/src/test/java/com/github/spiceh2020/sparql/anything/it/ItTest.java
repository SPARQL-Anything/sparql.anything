package com.github.spiceh2020.sparql.anything.it;

import com.github.spiceh2020.sparql.anything.engine.FacadeX;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.main.QC;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.*;

public class ItTest {
    private static final Logger log = LoggerFactory.getLogger(ItTest.class);

    @Test
    public void RegistryExtensionsTest(){
        for (String ext : new String[]{"json", "html", "xml", "csv", "bin", "png","jpeg","jpg","bmp","tiff","tif", "ico", "txt"}){
            Assert.assertNotNull(ext, FacadeX.Registry.getTriplifierForExtension(ext));
        }
    }
    @Test
    public void RegistryMimeTypesTest(){
        for (String mt : new String[]{
                "application/json", "text/html", "application/xml", "text/csv", "application/octet-stream",
                "image/png","image/jpeg","image/bmp", "image/tiff", "image/vnd.microsoft.icon"}){
            Assert.assertNotNull(mt, FacadeX.Registry.getTriplifierForMimeType(mt));
        }
    }

    @Test
    public void CSV1() throws URISyntaxException {
        Dataset kb = DatasetFactory.createGeneral();
        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
        String location = getClass().getClassLoader().getResource("test1.csv").toURI().toString();
        log.info("{}", location);
        Query query = QueryFactory.create("SELECT distinct ?p WHERE { SERVICE <facade-x:location=" + location + "> { ?s ?p ?o }} ORDER BY ?p");
        ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
        Map<Integer,String> expected = new HashMap<Integer,String>();
        expected.put(1,"http://www.w3.org/1999/02/22-rdf-syntax-ns#_1");
        expected.put(2,"http://www.w3.org/1999/02/22-rdf-syntax-ns#_2");
        expected.put(3,"http://www.w3.org/1999/02/22-rdf-syntax-ns#_3");
        expected.put(4,"http://www.w3.org/1999/02/22-rdf-syntax-ns#_4");
        while (rs.hasNext()){
            int rowId = rs.getRowNumber() + 1;
            QuerySolution qs = rs.next();
            log.trace("{} {} {}", rowId, qs.get("p").toString(), expected.get(rowId));
            Assert.assertTrue(expected.get(rowId).equals(qs.get("p").toString()));
        }
    }

    @Test
    public void CSV1headers() throws URISyntaxException {
        Dataset kb = DatasetFactory.createGeneral();
        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
        String location = getClass().getClassLoader().getResource("test1.csv").toURI().toString();
        log.info("{}", location);
        Query query = QueryFactory.create("SELECT distinct ?p WHERE { SERVICE <facade-x:csv.headers=true,namespace=http://www.example.org/csv#,location=" + location + "> { ?s ?p ?o }} ORDER BY ?p");
        ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
        Map<Integer,String> expected = new HashMap<Integer,String>();

        expected.put(1,"http://www.example.org/csv#A");
        expected.put(2,"http://www.example.org/csv#B");
        expected.put(3,"http://www.example.org/csv#C");
        expected.put(4,"http://www.example.org/csv#D");
        expected.put(5,"http://www.w3.org/1999/02/22-rdf-syntax-ns#_1");
        expected.put(6,"http://www.w3.org/1999/02/22-rdf-syntax-ns#_2");

        while (rs.hasNext()){
            int rowId = rs.getRowNumber() + 1;
            QuerySolution qs = rs.next();
            log.trace("{} {} {}", rowId, qs.get("p").toString(), expected.get(rowId));
            Assert.assertTrue(expected.get(rowId).equals(qs.get("p").toString()));
        }
    }

    @Test
    public void JSON1() throws URISyntaxException {
        // a01009-14709.json
        Dataset kb = DatasetFactory.createGeneral();
        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
        String location = getClass().getClassLoader().getResource("tate-gallery/a01009-14709.json").toURI().toString();
        Query query = QueryFactory.create("SELECT DISTINCT ?p WHERE { SERVICE <facade-x:namespace=http://www.example.org#,location=" + location + "> { ?s ?p ?o }} order by ?p");
        ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
        List<String> mustInclude = new ArrayList<String>(Arrays.asList(new String[]{
                "http://www.example.org#thumbnailUrl",
                "http://www.example.org#title",
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#_1",
                "http://www.example.org#text",
                "http://www.example.org#subjects",
                "http://www.example.org#subjectCount"
        }));
        while (rs.hasNext()){
            int rowId = rs.getRowNumber() + 1;
            QuerySolution qs = rs.next();
            log.trace("{} {}", rowId, qs.get("p").toString());
            mustInclude.remove(qs.get("p").toString());
        }
        Assert.assertTrue(mustInclude.isEmpty());
    }

    @Test
    public void JPG1()throws URISyntaxException {
        // A01009_8.jpg
        Dataset kb = DatasetFactory.createGeneral();
        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
        String location = getClass().getClassLoader().getResource("tate-gallery/A01009_8.jpg").toURI().toString();
        Query query = QueryFactory.create("SELECT * WHERE { SERVICE <facade-x:location=" + location + "> { ?s ?p ?o }}");
        ResultSet rs = QueryExecutionFactory.create(query, kb).execSelect();
        // Should only contain 1 triple
        while (rs.hasNext()){
            int rowId = rs.getRowNumber() + 1;
            QuerySolution qs = rs.next();
            Assert.assertTrue(qs.get("s").asNode().isBlank());
            Assert.assertTrue(qs.get("p").toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#_1"));
            Assert.assertTrue(qs.get("o").isLiteral());
        }
    }
}
