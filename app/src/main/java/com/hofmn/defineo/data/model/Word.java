package com.hofmn.defineo.data.model;

import java.util.Date;

public class Word {
    private int id;
    private String word;
    private Date addedDate;
    private LearningPhase learningPhase;

    public Word() {

    }

    public Word(String word) {
        this.word = word;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public LearningPhase getLearningPhase() {
        return learningPhase;
    }

    public void setLearningPhase(LearningPhase learningPhase) {
        this.learningPhase = learningPhase;
    }

    public static enum LearningPhase {
        Learn, Repeat, None
    }
}
