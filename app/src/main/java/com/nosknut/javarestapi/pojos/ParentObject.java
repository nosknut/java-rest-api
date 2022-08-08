package com.nosknut.javarestapi.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;

// https://www.baeldung.com/jackson-annotations
public class ParentObject {
    @JsonProperty("childObject")
    public ChildObject childObject;
    
    
    @JsonProperty("nullableChildObject")
    public Optional<ChildObject> nullableChildObject;
}
