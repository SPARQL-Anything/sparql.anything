/*
 * Copyright (c) 2021 Enrico Daga @ http://www.enridaga.net
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.sparqlanything.engine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sparqlanything.model.HTTPHelper;

public class Location {
    private static final Logger log = LoggerFactory.getLogger(Location.class);
    public static boolean isHTTP(URL url) {
        return (url.getProtocol().toLowerCase().equals("http") || url.getProtocol().toLowerCase().equals("https"));
    }

    public static boolean isFile(URL url){
        return url.getProtocol().toLowerCase().equals("file");
    }

    public static InputStream getInputStream(URL url, Properties properties) throws IllegalArgumentException, IOException {
        File local = null;
        // If local throw exception
        if(url.getProtocol().equals("file")){
            log.debug("Getting input stream from file");
            return url.openStream();
        }

        // If HTTP
        if(url.getProtocol().equals("http")||url.getProtocol().equals("https")){
            CloseableHttpResponse response = HTTPHelper.getInputStream(url, properties);
            if(!HTTPHelper.isSuccessful(response) ){
                throw new IOException(response.getStatusLine().toString());
            }
            return response.getEntity().getContent();
        }

        // If other protocol, try URL and Connection
        log.debug("Other protocol: {}", url.getProtocol());
        return url.openStream();
    }
}
