package com.nosknut.javarestapi.embedhttp;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.Headers;

// https://github.com/arteam/embedded-http-server/tree/master/src/main/java/com/github/arteam/embedhttp
/**
 * Represents an HTTP responses from an HTTP server
 */
public class HttpResponse {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int STATUS_CODE_OK = 200;

    private int statusCode;
    private Headers headers;
    private String body;

    public HttpResponse() {
        this(STATUS_CODE_OK, new Headers(), "");
    }

    public HttpResponse(int statusCode, Headers headers, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public HttpResponse setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponse failRequest(int statusCode, String errorMessage) throws IOException {
        setStatusCode(statusCode);
        
        ObjectNode json = objectMapper.createObjectNode();
        json.put("message", errorMessage);
        
        setBody(json);
        return this;
    }

    Headers getHeaders() {
        return headers;
    }

    public HttpResponse addHeader(String name, String value) {
        if(headers.containsKey(name)) {
            throw new IllegalArgumentException("Header already exists: " + name);
        }
        headers.add(name, value);
        return this;
    }

    public HttpResponse setHeaders(Headers headers) {
        this.headers = headers;
        return this;
    }

    public String getBody() {
        return body;
    }

    public HttpResponse setBody(String body, String contentType) {
        setContentType(contentType);
        this.body = body;
        return this;
    }

    public HttpResponse setContentType(String contentType) {
        if(contentType == null) {
            throw new IllegalArgumentException("Content type cannot be null");
        }
        addHeader("Content-Type", contentType);
        return this;
    }

    // Store this in a variable and pass it to setBody once the object has been populated
    public ObjectNode createJsonBody() {
        return objectMapper.createObjectNode();
    }

    public HttpResponse setBody(JsonNode body) throws IOException {
        setContentType("application/json");
        this.body = objectMapper.writeValueAsString(body);
        return this;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusCode=" + statusCode +
                ", headers=" + headers.entrySet() +
                ", body='" + body + '\'' +
                '}';
    }
}