package com.hofmn.defineo.data.model;

public class Definition {
    private String definition;
    private String context;
    private String type;

    public Definition(String definition, String context, String type) {
        this.definition = definition;
        this.context = context;
        this.type = type;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
