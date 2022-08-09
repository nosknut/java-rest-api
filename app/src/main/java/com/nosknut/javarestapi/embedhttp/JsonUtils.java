package com.nosknut.javarestapi.embedhttp;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonUtils {
    public static final ObjectMapper objectMapper = new ObjectMapper();

    // Store this in a variable and pass it to setBody once the object has been
    // populated
    public static ObjectNode createJsonObject() {
        return objectMapper.createObjectNode();
    }

    public static String serializeJson(JsonNode jsonObject) {
        try {
            String jsonString = objectMapper.writeValueAsString(jsonObject);
            return jsonString;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObjectNode deserializeJson(String jsonString) {
        try {
            JsonNode jsonObject = objectMapper.readTree(jsonString);
            if (jsonObject.isObject()) {
                return (ObjectNode) jsonObject;
            } else {
                throw new IOException("Body is not a JSON object");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T jsonStringToClass(String jsonString, Class<T> valueType) {
        try {
            return objectMapper.readValue(jsonString, valueType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T jsonObjectToClass(JsonNode jsonObject, Class<T> valueType) {
        try {
            return objectMapper.treeToValue(jsonObject, valueType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String classToJsonString(Object classObject) {
        try {
            return objectMapper.writeValueAsString(classObject);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObjectNode classToJsonObject(Object classObject) {
        return deserializeJson(classToJsonString(classObject));
    }
}
