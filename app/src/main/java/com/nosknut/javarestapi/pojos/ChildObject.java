package com.nosknut.javarestapi.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

// https://www.baeldung.com/jackson-annotations
public class ChildObject {
    
    // When not using JsonProperty the field name is the same as the JSON field name
    // You should still specify JsonProperty to prevent future changes from altering
    // the field name in json payloads as this will cause trouble with other API's
    // that may not show up as exceptions
    public String value;

    // JsonProperty can have a different value than the field name
    @JsonProperty("intValue")
    public int value2;
}