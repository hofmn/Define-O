package com.hofmn.defineo.data.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hofmn.defineo.data.model.Definition;
import com.hofmn.defineo.data.model.Translation;
import com.hofmn.defineo.data.model.Word;
import com.hofmn.defineo.data.model.WordData;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String LOG = DatabaseHandler.class.getCanonicalName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "words_db";

    private static final String TABLE_WORDS = "words";
    private static final String TABLE_DEFINITIONS = "definitions";
    private static final String TABLE_TRANSLATIONS = "translations";

    private static final String KEY_WORD_ID = "word_id";

    private static final String KEY_WORD = "word";
    private static final String KEY_LEARNING_PHASE = "phase";
    private static final String KEY_WORD_DATE = "addedDate";
    private static final String CREATE_TABLE_WORDS = "CREATE TABLE " + TABLE_WORDS
            + "(" + KEY_WORD_ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  DEFAULT 1,"
            + KEY_WORD + " VARCHAR(30)," + KEY_LEARNING_PHASE + " VARCHAR(20),"
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEFINITIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSLATIONS);

        onCreate(db);
    }

    public void insertWords(ArrayList<WordData> wordsList) {
        SQLiteDatabase db = getReadableDatabase();

        long wordId;

        for (WordData wordData : wordsList) {
            String word = wordData.getWord().getWord();
            ArrayList<Definition> definitions = wordData.getDefinitions();
            ArrayList<Translation> translations = wordData.getTranslations();

            ContentValues wordValues = new ContentValues();
            wordValues.put(KEY_WORD, word);
            wordValues.put(KEY_WORD_DATE, new Date().getTime());

            wordId = db.insert(TABLE_WORDS, null, wordValues);

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

                Log.d(LOG, "ADDED TO DB: " + word.getWord());
                wordList.add(wordData);

            } while (cursorWords.moveToNext());
        } else {
            Log.d(LOG, "NO WORDS :(  :(  :(");
        }

        db.close();
        return wordList;
    }

    public void dropAllTables() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + DATABASE_NAME);
        db.close();
    }
}
