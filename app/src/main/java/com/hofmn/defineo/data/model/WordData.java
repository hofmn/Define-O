package com.hofmn.defineo.data.model;

import java.util.ArrayList;

public class WordData {
    private Word word;
    private ArrayList<Definition> definitions;
    private ArrayList<Translation> translations;

    public WordData(Word word, ArrayList<Definition> definitions, ArrayList<Translation> translations) {
        this.word = word;
        this.definitions = definitions;
        this.translations = translations;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public ArrayList<Definition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(ArrayList<Definition> definitions) {
        this.definitions = definitions;
    }

    public ArrayList<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(ArrayList<Translation> translations) {
        this.translations = translations;
    }
}
