package com.nosknut.javarestapi.embedhttp;

import java.io.IOException;

// https://github.com/arteam/embedded-http-server/tree/master/src/main/java/com/github/arteam/embedhttp
/**
 * A functional interface which represents a handler for an HTTP request.
 * It provides ability to get access to the parameters of the request and
 * set-up an HTTP response.
 */
@FunctionalInterface
public interface HttpHandler {

    /**
     * Handles an HTTP request and builds an HTTP response.
     */
    void handle(HttpRequest request, HttpResponse response) throws IOException;

}