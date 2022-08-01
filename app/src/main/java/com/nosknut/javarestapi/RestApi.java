package com.nosknut.javarestapi;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nosknut.javarestapi.embedhttp.EmbeddedHttpServer;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;

public class RestApi {
    
    public static ArrayList<String> whitelistedIps() {
        ArrayList<String> ips = new ArrayList<>();
        ips.add("127.0.0.1");
        return ips;
    }

    public static class ApiIpWhitelistAuthentication extends Authenticator {

        @Override
        public Result authenticate (HttpExchange exch) {
            // TODO: Make sure this is the correct value
            String hostAddress = exch.getRemoteAddress().getAddress().getHostAddress();
            if (!whitelistedIps().contains(hostAddress)) {
                    return new Authenticator.Failure(401);
            }
            return new Authenticator.Success(exch.getPrincipal());
        }
    }

    public static void init() throws IOException {
        ApiIpWhitelistAuthentication authenticator = new ApiIpWhitelistAuthentication();
        EmbeddedHttpServer server = new EmbeddedHttpServer();

        server.addHandler("/post/anything", (req, res) -> {
            String body = req.getBody();
            res.setBody(body, req.getContentType());
            res.setStatusCode(200);
        }, authenticator);

        server.addHandler("/post/json", (req, res) -> {
            ObjectNode body = req.getBodyAsJson();
            ObjectNode response = res.createJsonBody();

            String value = body.get("value").asText();
            response.put("receivedValue", value);

            res.setBody(response);

            res.setStatusCode(200);
        }, authenticator);

        server.addHandler("/get", (req, res) -> {
            ObjectNode json = res.createJsonBody();

            req.getQueryParameters().forEach((key, value) -> {
                json.put(key, value);
            });
            
            res.setBody(json);
            res.setStatusCode(200);
        }, authenticator);

        server.addHandler("/error", (req, res) -> {
            res.failRequest(500, "Internal Server Error");
        }, authenticator);

        server.addHandler("/ping", (req, res) -> {
            res.setBody("Pong!", "text/plain");
            res.setStatusCode(200);
        }, authenticator);

        int port = 7070;
        server.start(port);
        System.out.println("Server started on port " + port);
    }
}
