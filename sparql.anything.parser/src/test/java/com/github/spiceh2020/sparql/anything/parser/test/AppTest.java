package com.github.spiceh2020.sparql.anything.parser.test;


import com.github.spiceh2020.sparql.anything.tupleurl.TupleURLParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

public class AppTest {

    @Test
    public void test1(){
        String uri = "tuple:mimeType=application/json,location=file://myfile.json";
        Properties p = new TupleURLParser(uri).getProperties();
        Assert.assertTrue(((Properties) p).containsKey("mimeType"));
        Assert.assertTrue(((Properties) p).containsKey("location"));
    }
}
