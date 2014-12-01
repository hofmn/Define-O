package com.hofmn.defineo.data.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hofmn.defineo.WordsManager;
import com.hofmn.defineo.data.model.Definition;
import com.hofmn.defineo.data.model.Translation;
import com.hofmn.defineo.data.model.Word;
import com.hofmn.defineo.data.model.WordData;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String STATS_WORD_VIEWS = "word views";
    public static final String STATS_DEFINITION_VIEWS = "definition views";
    public static final String STATS_TRANSLATION_VIEWS = "translation views";
    private static final String LOG = DatabaseHandler.class.getCanonicalName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "words_db";
    private static final String TABLE_WORDS = "words";
    private static final String TABLE_DEFINITIONS = "definitions";
    private static final String TABLE_TRANSLATIONS = "translations";
    private static final String TABLE_STATS = "stats";
    private static final String KEY_WORD_ID = "word_id";
    private static final String KEY_WORD = "word";
    private static final String KEY_LEARNING_PHASE = "phase";
    private static final String KEY_WORD_DATE = "addedDate";
    private static final String CREATE_TABLE_WORDS = "CREATE TABLE " + TABLE_WORDS
            + "(" + KEY_WORD_ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  DEFAULT 1,"
            + KEY_WORD + " VARCHAR(30) UNIQUE," + KEY_LEARNING_PHASE + " VARCHAR(20),"
            + KEY_WORD_DATE + " DATETIME" + ")";
    private static final String KEY_DEFINITION_ID = "definition_id";
    private static final String KEY_DEFINITION = "definition";
    private static final String KEY_CONTEXT = "context";
    private static final String CREATE_TABLE_DEFINITIONS = "CREATE TABLE " + TABLE_DEFINITIONS
            + "(" + KEY_DEFINITION_ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  DEFAULT 1,"
            + KEY_WORD_ID + " INTEGER," + KEY_DEFINITION + " TEXT," + KEY_CONTEXT + " TEXT,"
            + "FOREIGN KEY(" + KEY_WORD_ID + ") REFERENCES " + TABLE_WORDS + "("
            + KEY_WORD_ID + "))";
    private static final String KEY_TRANSLATION_ID = "translation_id";
    private static final String KEY_TRANSLATION = "translation";
    private static final String CREATE_TABLE_TRANSLATIONS = "CREATE TABLE " + TABLE_TRANSLATIONS
            + "(" + KEY_TRANSLATION_ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  DEFAULT 1,"
            + KEY_WORD_ID + " INTEGER," + KEY_TRANSLATION + " TEXT,"
            + "FOREIGN KEY(" + KEY_WORD_ID + ") REFERENCES " + TABLE_WORDS + "("
            + KEY_WORD_ID + "))";
    private static final String KEY_STATS_ID = "stats_id";
    private static final String KEY_STATS_KEY = "key";
    private static final String KEY_STATS_VALUE = "value";
    private static final String CREATE_TABLE_STATS = "CREATE TABLE " + TABLE_STATS
            + "(" + KEY_STATS_ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  DEFAULT 1,"
            + KEY_STATS_KEY + " TEXT," + KEY_STATS_VALUE + " INTEGER)";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    public static String getDbName() {
        return DATABASE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WORDS);
        db.execSQL(CREATE_TABLE_DEFINITIONS);
        db.execSQL(CREATE_TABLE_TRANSLATIONS);
        db.execSQL(CREATE_TABLE_STATS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEFINITIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSLATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS);

        onCreate(db);
    }

    public void insertWords(ArrayList<WordData> wordsList) {
        SQLiteDatabase db = getReadableDatabase();

        long wordId;

        for (WordData wordData : wordsList) {
            String word = wordData.getWord().getWord();
            ArrayList<Definition> definitions = wordData.getDefinitions();
            ArrayList<Translation> translations = wordData.getTranslations();

            if (!wordExist(word)) {
                ContentValues wordValues = new ContentValues();
                wordValues.put(KEY_WORD, word);
                wordValues.put(KEY_LEARNING_PHASE, Word.LearningPhase.None.toString());
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                wordValues.put(KEY_WORD_DATE, dateFormat.format(date));

                wordId = db.insert(TABLE_WORDS, null, wordValues);
                Log.d(LOG, "Word ID = " + wordId);

                for (Definition definitionObject : definitions) {
                    String definition = definitionObject.getDefinition();
                    Log.d(LOG, "INSERT DEFINITION: " + definition);
                    String context = definitionObject.getContext();

                    ContentValues definitionValues = new ContentValues();
                    definitionValues.put(KEY_DEFINITION, definition);
                    definitionValues.put(KEY_CONTEXT, context);
                    definitionValues.put(KEY_WORD_ID, wordId);
                    db.insert(TABLE_DEFINITIONS, null, definitionValues);
                }

                for (Translation translationObject : translations) {
                    String translation = translationObject.getTranslation();

                    ContentValues translationValues = new ContentValues();
                    translationValues.put(KEY_TRANSLATION, translation);
                    translationValues.put(KEY_WORD_ID, wordId);
                    db.insert(TABLE_TRANSLATIONS, null, translationValues);
                }
            }
        }
        db.close();
    }

    public ArrayList<WordData> getAllWords() {
        ArrayList<WordData> wordList = new ArrayList<WordData>();

        String selectWordsQuery = "SELECT * FROM " + TABLE_WORDS;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursorWords = db.rawQuery(selectWordsQuery, null);

        if (cursorWords.moveToFirst()) {
            do {
                WordData wordData = new WordData();
                ArrayList<Definition> definitions = new ArrayList<Definition>();
                ArrayList<Translation> translations = new ArrayList<Translation>();

                Word word = new Word();
                word.setId(cursorWords.getInt(cursorWords.getColumnIndex(KEY_WORD_ID)));
                word.setWord(cursorWords.getString(cursorWords.getColumnIndex(KEY_WORD)));
                word.setLearningPhase(Word.LearningPhase.valueOf(cursorWords
                        .getString(cursorWords.getColumnIndex(KEY_LEARNING_PHASE))));
                try {
                    word.setAddedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                            cursorWords.getString(cursorWords.getColumnIndex(KEY_WORD_DATE))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Log.d(LOG, word.getWord() + ": " + word.getLearningPhase().toString());

                if (!shouldAddToLearningList(word)) {
                    continue;
                }

                String selectDefinitionQuery = "SELECT * FROM " + TABLE_DEFINITIONS + " WHERE "
                        + KEY_WORD_ID + " = " + word.getId();
                Cursor cursorDefinitions = db.rawQuery(selectDefinitionQuery, null);

                if (cursorDefinitions.moveToFirst()) {
                    do {
                        Definition definition = new Definition();
                        definition.setId(cursorDefinitions.getInt(cursorDefinitions
                                .getColumnIndex(KEY_DEFINITION_ID)));
                        definition.setWordId(cursorDefinitions.getInt(cursorDefinitions
                                .getColumnIndex(KEY_WORD_ID)));
                        definition.setDefinition(cursorDefinitions.getString(cursorDefinitions
                                .getColumnIndex(KEY_DEFINITION)));
                        definition.setContext(cursorDefinitions.getString(cursorDefinitions
                                .getColumnIndex(KEY_CONTEXT)));
                        definitions.add(definition);
                    } while (cursorDefinitions.moveToNext());
                }

                String selectTranslationQuery = "SELECT * FROM " + TABLE_TRANSLATIONS + " WHERE "
                        + KEY_WORD_ID + " = " + word.getId();
                Cursor cursorTranslations = db.rawQuery(selectTranslationQuery, null);

                if (cursorTranslations.moveToFirst()) {
                    do {
                        Translation translation = new Translation();
                        translation.setId(cursorTranslations.getInt(cursorTranslations
                                .getColumnIndex(KEY_TRANSLATION_ID)));
                        translation.setWordId(cursorTranslations.getInt(cursorTranslations
                                .getColumnIndex(KEY_WORD_ID)));
                        translation.setTranslation(cursorTranslations.getString(cursorTranslations
                                .getColumnIndex(KEY_TRANSLATION)));
                        translations.add(translation);
                    } while (cursorTranslations.moveToNext());
                }

                wordData.setWord(word);
                wordData.setDefinitions(definitions);
                wordData.setTranslations(translations);

                wordList.add(wordData);

            } while (cursorWords.moveToNext());
        } else {
            Log.d(LOG, "NO WORDS :(  :(  :(/");
        }

        db.close();
        return wordList;
    }

    private boolean wordExist(String word) {
        String selectWordQuery = "SELECT word FROM " + TABLE_WORDS + " WHERE "
                + KEY_WORD + " like '" + word + "'";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectWordQuery, null);
        return cursor.moveToFirst();
    }

    private boolean shouldAddToLearningList(Word word) {
        if (word.getLearningPhase() == Word.LearningPhase.Repeat) {
            float repeatFrequency = WordsManager.getInstance().getRepeatFrequency();
            if (hoursBetween(new Date(), word.getAddedDate()) > repeatFrequency) {
                updateLearningPhase(word.getWord(), Word.LearningPhase.None);
                updateDate(word);
                return true;
            } else {
                return false;
            }
        } else {
            updateLearningPhase(word.getWord(), Word.LearningPhase.None);
            return true;
        }
    }

    private float hoursBetween(Date dateOne, Date dateTwo) {
        float secs = (dateOne.getTime() - dateTwo.getTime()) / 1000;
        return secs / 3600;
    }

    public Word getWord(String word) {
        Word wordObject = new Word();
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_WORDS + " WHERE "
                + KEY_WORD + " like '" + word + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            wordObject.setId(cursor.getInt(cursor.getColumnIndex(KEY_WORD_ID)));
            wordObject.setWord(cursor.getString(cursor.getColumnIndex(KEY_WORD)));
            wordObject.setLearningPhase(Word.LearningPhase.valueOf(cursor
                    .getString(cursor.getColumnIndex(KEY_LEARNING_PHASE))));
            try {
                wordObject.setAddedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                        cursor.getString(cursor.getColumnIndex(KEY_WORD_DATE))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Log.d(LOG, wordObject.getWord());

        return wordObject;
    }

    public void updateLearningPhase(String word, Word.LearningPhase phase) {

        Word wordObject = getWord(word);

        if (!(wordObject.getLearningPhase() == Word.LearningPhase.Learn)
                || phase == Word.LearningPhase.None) {
            SQLiteDatabase db = getReadableDatabase();
            String condition = KEY_WORD_ID + "=" + wordObject.getId();
            ContentValues values = new ContentValues();
            values.put(KEY_LEARNING_PHASE, String.valueOf(phase));

            Log.d(LOG, "old phase: " + wordObject.getLearningPhase());
            Log.d(LOG, " new phase: " + phase.toString());

            db.update(TABLE_WORDS, values, condition, null);
            Word updatedWord = this.getWord(wordObject.getWord());
            Log.d(LOG, updatedWord.getWord() + " UPDATED PHASE: " + updatedWord.getLearningPhase());
        }
    }

    public void updateAllLearningPhases(HashMap<String, Word.LearningPhase> phasesMap) {

        SQLiteDatabase db = getReadableDatabase();

        for (Map.Entry<String, Word.LearningPhase> entry : phasesMap.entrySet()) {
            Log.d(LOG, "Updating " + entry.getKey() + " " + entry.getValue());
            Word wordObject = getWord(entry.getKey());
            String condition = KEY_WORD_ID + "=" + wordObject.getId();
            ContentValues values = new ContentValues();
            values.put(KEY_LEARNING_PHASE, String.valueOf(entry.getValue()));
            int i = db.update(TABLE_WORDS, values, condition, null);
            Log.d(LOG, "num of rows updated  " + i);
        }
    }

    public void updateDate(Word word) {
        SQLiteDatabase db = getReadableDatabase();

        String condition = KEY_WORD_ID + "=" + word.getId();
        ContentValues values = new ContentValues();
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        values.put(KEY_WORD_DATE, dateFormat.format(date));

        db.update(TABLE_WORDS, values, condition, null);
    }

    public void updateViewStats(String key, int value) {
        if (value > 0) {
            SQLiteDatabase db = getReadableDatabase();
            String condition = KEY_STATS_KEY + " like '" + key + "'";

            ContentValues newValues = new ContentValues();
            newValues.put(KEY_STATS_VALUE, String.valueOf(value));
            db.update(TABLE_STATS, newValues, condition, null);
        }
    }

    public int getViewStatsValue(String key) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT " + KEY_STATS_VALUE + " FROM " + TABLE_STATS + " WHERE "
                + KEY_STATS_KEY + " like '" + key + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndex(KEY_STATS_VALUE));
        }

        return -1;
    }

    public void insertViewStatsValues() {
        SQLiteDatabase db = getReadableDatabase();

        String[] statsKeys = {STATS_WORD_VIEWS, STATS_DEFINITION_VIEWS, STATS_TRANSLATION_VIEWS};

        for (String key : statsKeys) {
            ContentValues statsValues = new ContentValues();
            statsValues.put(KEY_STATS_VALUE, 0);
            statsValues.put(KEY_STATS_KEY, key);
            db.insert(TABLE_STATS, null, statsValues);
        }
    }
}
