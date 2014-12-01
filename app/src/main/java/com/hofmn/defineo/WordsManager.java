package com.hofmn.defineo;

import android.speech.tts.TextToSpeech;

import com.hofmn.defineo.data.model.Word;
import com.hofmn.defineo.data.model.WordData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WordsManager {
    private static final Map<String, Word.LearningPhase> learningPhaseMap
            = new HashMap<String, Word.LearningPhase>();
    private static WordsManager mInstance = null;
    private ArrayList<WordData> data;
    private TextToSpeech textToSpeech;

    private int statsWordViews;
    private int statsDefinitionViews;
    private int statsTranslationViews;

    private float repeatFrequency;

    private WordsManager() {

    }

    public static WordsManager getInstance() {
        if (mInstance == null) {
            mInstance = new WordsManager();
        }
        return mInstance;
    }

    public ArrayList<WordData> getData() {
        return data;
    }

    public void setData(ArrayList<WordData> data) {
        this.data = data;
    }

    public Map<String, Word.LearningPhase> getLearningPhaseMap() {
        return learningPhaseMap;
    }

    public void addToLearningPhaseMap(String word, Word.LearningPhase phase) {
        if (!learningPhaseMap.containsKey(word) || phase == Word.LearningPhase.Learn) {
            learningPhaseMap.put(word, phase);
        }
    }

    public void clearLearningPhaseMap() {
        learningPhaseMap.clear();
    }

    public void setTTS(TextToSpeech tts) {
        textToSpeech = tts;
    }

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }

    public int getStatsWordViews() {
        return statsWordViews;
    }

    public void updateStatsWordViews(boolean increase) {
        this.statsWordViews = this.statsWordViews + (increase ? 1 : -1);
    }

    public int getStatsDefinitionViews() {
        return statsDefinitionViews;
    }

    public void updateStatsDefinitionViews(boolean increase) {
        this.statsDefinitionViews = this.statsDefinitionViews + (increase ? 1 : -1);
    }

    public int getStatsTranslationViews() {
        return statsTranslationViews;
    }

    public void updateStatsTranslationViews(boolean increase) {
        this.statsTranslationViews = this.statsTranslationViews + (increase ? 1 : -1);
    }

    public void clearStatsViews() {
        statsWordViews = 0;
        statsDefinitionViews = 0;
        statsTranslationViews = 0;
    }

    public float getRepeatFrequency() {
        return repeatFrequency;
    }

    public void setRepeatFrequency(float repeatFrequency) {
        this.repeatFrequency = repeatFrequency;
    }
}