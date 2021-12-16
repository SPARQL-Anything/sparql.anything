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

package com.github.sparqlanything.model;

import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * HTTP Helper
 *
 *
 */
public class HTTPHelper {

    private static final Logger log = LoggerFactory.getLogger(HTTPHelper.class);

    public static final String HTTPCLIENT_PREFIX = "http.client.";
    public static final String HTTPHEADER_PREFIX = "http.header.";
    public static final String HTTPQUERY_PREFIX = "http.query.";
    public static final String HTTPFORM_PREFIX = "http.form.";
//    public static final String HTTPDATA_PREFIX = "http.data.";
    public static final String HTTPMETHOD = "http.method";
    public static final String HTTPPAYLOAD = "http.payload";
    public static final String HTTPPROTOCOL = "http.protocol";
    public static final String HTTPPAUTHUSER = "http.auth.user";
    public static final String HTTPPAUTHPASSWORD = "http.auth.password";
    public static final String HTTPFOLLOWREDIRECT = "http.redirect";

    public static final String[] RELEVANT_PROPERTIES = new String[]{
            HTTPCLIENT_PREFIX,   HTTPHEADER_PREFIX, HTTPQUERY_PREFIX, HTTPFORM_PREFIX, HTTPMETHOD, HTTPPAYLOAD, HTTPPROTOCOL, HTTPFOLLOWREDIRECT
    };

    public static boolean isProperty(String prefix, String key) {
        if (key.startsWith(prefix)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getProperty(String prefix, Object property) {
        return ((String) property).substring(prefix.length());
    }

    public static Properties relevantProperties(Properties properties) {
        Properties relevant = new Properties();
        for(Map.Entry<Object,Object> entry : properties.entrySet()){
            for(String property: RELEVANT_PROPERTIES){
                if( (((String)entry.getKey()).equals(property))
                    || (property.endsWith(".") && ((String)entry.getKey()).startsWith(property))){
                    relevant.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return relevant;
    }


    public static HttpClientBuilder setupClientBuilder(URL url, Properties properties) {
        // System properties are considered by default, by default, unless the SPARQL Anything property 'useSystemSettings' is set to 'false'.
        // The following System properties are considered
        //        ssl.TrustManagerFactory.algorithm
        //        javax.net.ssl.trustStoreType
        //        javax.net.ssl.trustStore
        //        javax.net.ssl.trustStoreProvider
        //        javax.net.ssl.trustStorePassword
        //        java.home
        //        ssl.KeyManagerFactory.algorithm
        //        javax.net.ssl.keyStoreType
        //        javax.net.ssl.keyStore
        //        javax.net.ssl.keyStoreProvider
        //        javax.net.ssl.keyStorePassword
        //        http.proxyHost
        //        http.proxyPort
        //        http.nonProxyHosts
        //        http.keepAlive
        //        http.maxConnections
        //        http.agent
        HttpClientBuilder builder = HttpClients.custom();
        if(!properties.containsKey(HTTPCLIENT_PREFIX + "useSystemProperties") || Boolean.parseBoolean((String)properties.get(HTTPCLIENT_PREFIX + "useSystemProperties"))){
            builder.useSystemProperties();
        }

        Method[] methods = builder.getClass().getMethods();
        for (Object key : properties.keySet()) {
            if (isProperty(HTTPCLIENT_PREFIX, (String) key)) {
                String methodName = getProperty(HTTPCLIENT_PREFIX, key);

                String[] values = ((String) properties.get(key)).split(",");
                // Test until the number of parameters is found
                for (Method method : methods) {
                    try {
                        if(method.getName().equals(methodName)) {
                            // treat some methods specifically
                            if(methodName.equals("setProxy")){
                                log.info("Setting proxy: {}:{}", values[0],values[1]);
                                builder.setProxy(new HttpHost(values[0], Integer.parseInt(values[1])));
                                continue;
                            }
                            // General method for most boolean properties
                            if (method.getParameterCount() == 0) {
                                // Check value is boolean (a false value makes only sense for system properties
                                boolean v = Boolean.parseBoolean((String) properties.get(key));
                                if(v) {
                                    method.invoke(builder);
                                }
                            } else if (method.getParameterCount() == 1 && values.length == 1) {
                                Class<?> parType = method.getParameterTypes()[0];
                                method.invoke(builder, parType.cast(properties.get(key)));
                            } else if (method.getParameterCount() == 2 && values.length == 2) {
                                // XXX Not sure this is useful in practice
                                Class<?> parType1 = method.getParameterTypes()[0];
                                Class<?> parType2 = method.getParameterTypes()[1];
                                method.invoke(builder, parType1.cast(values[0]), parType1.cast(values[1]));
                            }
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error("Cannot run method for property configuration: {} -> {}", key, properties.get(key));
                        log.error("Exception details:", e);
                    }
                }
            }
        }
        // Redirect strategy
        Boolean followRedirect = true;
        if (properties.containsKey(HTTPFOLLOWREDIRECT)) {
            followRedirect = Boolean.parseBoolean((String) properties.get(HTTPFOLLOWREDIRECT));
        }
        log.debug("Following redirect responses: {}", followRedirect);
        if(followRedirect){
            builder.setRedirectStrategy(new LaxRedirectStrategy());
        }

        // Authentication
        if(properties.containsKey(HTTPPAUTHUSER) && properties.containsKey(HTTPPAUTHPASSWORD)) {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                    new AuthScope(url.getHost(), url.getPort()),
                    new UsernamePasswordCredentials((String) properties.get(HTTPPAUTHUSER), (String) properties.get(HTTPPAUTHPASSWORD)));
            builder.setDefaultCredentialsProvider(credsProvider);
        }
        return builder;
    }

    public static HttpUriRequest buildRequest(URL url, Properties properties) {

        String method = "GET";
        if (properties.containsKey(HTTPMETHOD)) {
            method = ((String) properties.get(HTTPMETHOD)).toUpperCase();
        }
        String version = "HTTP/1.1";
        ProtocolVersion protocol;
        if (properties.containsKey(HTTPPROTOCOL)) {
            version = ((String) properties.get(HTTPPROTOCOL)).toUpperCase();
        }
        if(version.equals("HTTP/1.1")){
            protocol = HttpVersion.HTTP_1_1;
        }else if(version.equals("HTTP/1.0")){
            protocol = HttpVersion.HTTP_1_0;
        }else if(version.equals("HTTP/0.9")){
            protocol = HttpVersion.HTTP_0_9;
        }else{
            log.error("Invalid protocol: {}, using 1.1", version);
            protocol = HttpVersion.HTTP_1_1;
        }

        // Prepare headers, data, and querystring
        List<NameValuePair> query = new ArrayList<NameValuePair>();
        List<NameValuePair> form = new ArrayList<NameValuePair>();
        List<Header> headers = new ArrayList<Header>();
        for (Object key : properties.keySet()) {
            // Headers
            if (isProperty(HTTPHEADER_PREFIX, (String) key)) {
                headers.add(new BasicHeader(getProperty(HTTPHEADER_PREFIX, (String) key), (String) properties.get(key)));
            } else if (isProperty(HTTPQUERY_PREFIX, (String) key)){
                // Querystring
                String p = getProperty(HTTPQUERY_PREFIX, (String) key);
                if(p.matches("\\.[0-9]+$")){
                    // There are many of these, remove number
                    p = p.substring(0, p.indexOf('.') + 1);
                }
                query.add(new BasicNameValuePair(p, (String) properties.get(key)));
            }else if (isProperty(HTTPFORM_PREFIX, (String) key)){
                // Querystring
                String p = getProperty(HTTPFORM_PREFIX, (String) key);
                if(p.matches("\\.[0-9]+$")){
                    // There are many of these, remove number
                    p = p.substring(0, p.indexOf('.') + 1);
                }
                form.add(new BasicNameValuePair(p, (String) properties.get(key)));
            }
        }
        // Attach query string to URL
        String uri;
        try {
            uri = new URIBuilder(url.toString()).addParameters(query).build().toString();
        } catch (URISyntaxException e) {
            log.error("Exception when building URI with parameters");
            log.error("Details", e);
            log.warn("Skipping query string parameters");
            uri = url.toString();
        }

//        RequestLine requestLine = new BasicRequestLine(method, uri, protocol);
        HttpUriRequest request;

        switch (method){
            case "GET":
                request = new HttpGet(uri);
                break;
            case "POST":
                request = new HttpPost(uri);
                break;
            case "PUT":
                request = new HttpPut(uri);
                break;
            case "DELETE":
                request = new HttpDelete(uri);
                break;
            case "OPTIONS":
                request = new HttpOptions(uri);
                break;
            case "HEAD":
                request = new HttpHead(uri);
                break;
            case "TRACE":
                request = new HttpTrace(uri);
                break;
            case "PATCH":
                request = new HttpPatch(uri);
                break;
            default:
                log.error("Method not supported: {}", method);
                throw new IllegalArgumentException("Unsupported method: " + method);
        }


        // Add Headers
        for(Header h: headers){
            request.addHeader(h);
        }

        // Check if we need to attach a payload to the request
        if (properties.containsKey(HTTPPAYLOAD)) {
            // If form data exists, raise warning
            if(!form.isEmpty()){
                log.warn("Cannot send 'form' and 'payload' at the same time! Content from 'payload' will be used and content from 'form' will be ignored");
            }
            String payload = ((String) properties.get(HTTPPAYLOAD));
            BasicHttpEntity entity = new BasicHttpEntity();
            entity.setContent(IOUtils.toInputStream(payload, StandardCharsets.UTF_8)); // TODO get charset from config?
            if(request instanceof HttpPost){
                HttpPost post = (HttpPost) request;
                post.setEntity(entity);
            }else if(request instanceof HttpPut){
                HttpPut put = (HttpPut) request;
                put.setEntity(entity);
            }
        } else {
            // Only if Method is POST, forces content-type to be form urlencoded
            // Populate form data
            if((request instanceof HttpPost)){
                if(!form.isEmpty()){
                    // Only form
                    try {
                        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form);
                        ((HttpPost) request).setEntity(entity);
                        // force content-type
                        request.setHeader("Content-type", "application/x-www-form-urlencoded");
                    } catch (UnsupportedEncodingException e) {
                        log.error("Problem when encoding form data", e);
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return request;
    }

    public static final boolean isSuccessful(CloseableHttpResponse response){
        return response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 299;
    }

    public static final boolean isNotFound(CloseableHttpResponse response){
        return response.getStatusLine().getStatusCode() == 404;
    }

    public static final boolean isRedirect(CloseableHttpResponse response){
        return response.getStatusLine().getStatusCode() >= 300 && response.getStatusLine().getStatusCode() <= 399;
    }
    public static final boolean isClientError(CloseableHttpResponse response){
        return response.getStatusLine().getStatusCode() >= 400 && response.getStatusLine().getStatusCode() <= 499;
    }
    public static final boolean isServerError(CloseableHttpResponse response){
        return response.getStatusLine().getStatusCode() >= 500 && response.getStatusLine().getStatusCode() <= 599;
    }

    public static final CloseableHttpResponse getInputStream(URL url, Properties properties) throws IOException {
        log.debug("Downloading via HTTP Client");
        HttpClientBuilder builder = HTTPHelper.setupClientBuilder(url, properties);
        CloseableHttpClient client = builder.build();
        HttpUriRequest request = HTTPHelper.buildRequest(url, properties);
        if(log.isDebugEnabled()){
            log.debug("* Request line: {}", request.getRequestLine());
            for(Header h: request.getAllHeaders()) {
                log.debug("> {}: {}", h.getName(), h.getValue());
            }
        }
        CloseableHttpResponse response = client.execute(request);
        log.debug("* Status line: {}", response.getStatusLine());
        for(Header h: response.getAllHeaders()) {
            log.debug("< {}: {}", h.getName(), h.getValue());
        }
        log.debug("Downloaded {} :: {}", url, response.getStatusLine().getStatusCode());
        return response;
    }
}
