package com.hofmn.defineo.data.model;

public class Translation {

    private int id;
    private int wordId;
    private String translation;

    public Translation(String translation) {
        this.translation = translation;
    }

    public Translation() {

    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
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
}
