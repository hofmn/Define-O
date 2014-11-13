package com.hofmn.defineo.data.model;

public class Definition {

    private int id;
    private int wordId;
    private String definition;
    private String context;

    public Definition(String definition, String context) {
        this.definition = definition;
        this.context = context;
    }

    public Definition() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
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
}
