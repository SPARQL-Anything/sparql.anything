/*
 * Copyright (c) 2023 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sparqlanything.model.test;

import io.github.sparqlanything.model.HTTPHelper;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class HTTPHelperTest {

    Properties p;
    URL url;

    private HttpClientBuilder test(){
        // If it works, no exception is thrown
        try {
            return HTTPHelper.setupClientBuilder(url, p);
        } catch(Exception e){
            e.printStackTrace();
            Assert.assertTrue(false);
            return null;
        }
    }

    @Before
    public void before() throws MalformedURLException {
        // Clean properties
        p = new Properties();
        url = new File("./example.txt").toURI().toURL();
    }

    @Test
    public void testUseSystemProperties(){
        p.setProperty("http.client.useSystemProperties", "false");
        test();
    }

    @Test
    public void testSetSocketTimeout(){
        p.setProperty("http.client.setSocketTimeout", "5000");
        test();
    }

    @Test
    public void testSetStaleConnectionCheckEnabled(){
        p.setProperty("http.client.setStaleConnectionCheckEnabled", "true");
        test();
    }

    @Test
    public void testSetProxy(){
        p.setProperty("http.client.setProxy", "myhost.net,80");
        test();
    }

    @Test
    public void testSetUserAgent(){
        p.setProperty("http.client.setUserAgent", "SPARQL Anything");
        test();
    }

    @Test
    public void testX() throws Exception {
        System.out.println(url.getProtocol());
    }
}
