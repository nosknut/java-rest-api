package com.nosknut.javarestapi;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nosknut.javarestapi.embedhttp.EmbeddedHttpServer;
import com.nosknut.javarestapi.pojos.ParentObject;
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

        server.addHandler("/post/parse-json", (req, res) -> {
            ObjectNode body = req.getBodyAsJson();
            String bodyString = req.getBody();
            
            ParentObject deserialized = req.objectMapper.treeToValue(body, ParentObject.class);
            ParentObject deserializedFromString = req.objectMapper.readValue(bodyString, ParentObject.class);

            System.out.println("From parsed ObjectNode");
            System.out.println(body);
            System.out.println(deserialized.childObject.value);
            System.out.println(deserialized.childObject.value2);
            System.out.println(deserialized.nullableChildObject);

            System.out.println("From raw json string");
            System.out.println(bodyString);
            System.out.println(deserializedFromString.childObject.value);
            System.out.println(deserializedFromString.childObject.value2);
            System.out.println(deserializedFromString.nullableChildObject);

            String serialized = req.objectMapper.writeValueAsString(deserialized);
            res.setBody(serialized, "application/json");

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

        server.addHandler("/files", (req, res) -> {
            String path = req.getQueryParameter("filePath");

            // https://stackoverflow.com/questions/423376/how-to-get-the-file-name-from-a-full-path-using-javascript
            try {
                switch (req.getMethod()) {
                    case "GET": {
                        res.setBody(FileUtils.readFileToByteString(path), "application/octet-stream");
                        // Formality that allows browsers to download the file using the correct name
                        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Disposition
                        String fileName = Paths.get(path).getFileName().toString();
                        res.addHeader("Content-Disposition", "attachment; filename=" + fileName);
                        res.setStatusCode(200);
                        break;
                    }
                    case "POST": {
                        FileUtils.writeByteStringToFile(path, req.getBody());
                        res.setStatusCode(200);
                        break;
                    }
                    default:
                        res.failRequest(405, "Method Not Allowed");
                        break;
                }
            } catch (NoSuchFileException e) {
                res.failRequest(404, "File not found");
                throw e;
            } catch (IOException e) {
                res.failRequest(500, "Internal Server Error");
                throw e;
            }
        }, authenticator);

        int port = 7070;
        server.start(port);
        System.out.println("Server started on port " + port);
    }
}
