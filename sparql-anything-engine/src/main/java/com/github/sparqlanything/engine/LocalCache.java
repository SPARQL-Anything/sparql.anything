/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.engine;

import com.github.sparqlanything.model.HTTPHelper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class LocalCache {

    private static final Logger log = LoggerFactory.getLogger(LocalCache.class);
    public final static String DIRNAME = ".fx";
    private File location;

    public LocalCache(){
        this(System.getProperty("user.home"));
    }

    public LocalCache(String baseDir){
        File userHome = new File(baseDir);
        if(userHome.isDirectory()){
            location = new File(userHome, DIRNAME);
        }else{
            throw new RuntimeException("Not a directory: " + userHome);
        }
    }

    public File getLocation(){
        return location;
    }

    public File download(URL url, Properties properties) throws IllegalArgumentException, IOException {
        File local = null;
        // If local throw exception
        if(url.getProtocol().equals("file")){
            throw new IllegalArgumentException("Cannot download a local file");
        }
        // If HTTP
        if(url.getProtocol().equals("http")||url.getProtocol().equals("https")){
            log.debug("Downloading via HTTP Client");
            HttpClientBuilder builder = HTTPHelper.setupClientBuilder(url, properties);
            CloseableHttpClient client = builder.build();
            HttpUriRequest request = HTTPHelper.buildRequest(url, properties);
            CloseableHttpResponse response = client.execute(request);
            System.err.println(response.getStatusLine().getStatusCode());
        }

        // If other protocol, try URL and Connection
        return local;
    }

    public File find(URL url, Properties properties) throws IllegalArgumentException{
        File local = null;

        return local;
    }

    public File find(URL url) throws IllegalArgumentException{
        File local = null;

        return local;
    }

    public boolean delete(URL url, Properties properties) throws IllegalArgumentException {
        Boolean found = false;

        return found;
    }
}
