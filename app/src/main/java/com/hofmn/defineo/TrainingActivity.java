package com.hofmn.defineo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.hofmn.defineo.data.model.Word;
import com.hofmn.defineo.data.model.db.DatabaseHandler;
import com.hofmn.defineo.fragments.DefinitionFragment;
import com.hofmn.defineo.fragments.TrainingFragment;
import com.hofmn.defineo.fragments.TranslationFragment;
import com.hofmn.defineo.fragments.WordCardFragment;

import java.util.HashMap;

public class TrainingActivity extends ActionBarActivity
        implements WordCardFragment.OnWordClick, DefinitionFragment.OnShowTranslationClicked {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (fragment == null) {
            fragment = new TrainingFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    finish();
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onWordClick(String word) {
        showFragment(DefinitionFragment.newInstance(word));
        WordsManager.getInstance().updateStatsDefinitionViews(true);
        WordsManager.getInstance().updateStatsWordViews(false);
        WordsManager.getInstance().updateStatsWordViews(false);
        WordsManager.getInstance().addToLearningPhaseMap(word, Word.LearningPhase.Learn);
    }

    @Override
    public void onShowTranslation(String word) {
        showFragment(TranslationFragment.newInstance(word));
        WordsManager.getInstance().updateStatsTranslationViews(true);
        WordsManager.getInstance().updateStatsDefinitionViews(false);
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        HashMap<String, Word.LearningPhase> map
                = (HashMap<String, Word.LearningPhase>) WordsManager
                .getInstance().getLearningPhaseMap();
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        databaseHandler.updateAllLearningPhases(map);
        updateViewStats(databaseHandler);
    }

    private void updateViewStats(DatabaseHandler databaseHandler) {
        int views = WordsManager.getInstance().getStatsWordViews()
                + databaseHandler.getViewStatsValue(DatabaseHandler.STATS_WORD_VIEWS);
        databaseHandler.updateViewStats(DatabaseHandler.STATS_WORD_VIEWS, views);
        views = WordsManager.getInstance().getStatsDefinitionViews()
                + databaseHandler.getViewStatsValue(DatabaseHandler.STATS_DEFINITION_VIEWS);
        databaseHandler.updateViewStats(DatabaseHandler.STATS_DEFINITION_VIEWS, views);
        views = WordsManager.getInstance().getStatsTranslationViews() +
                databaseHandler.getViewStatsValue(DatabaseHandler.STATS_TRANSLATION_VIEWS);
        databaseHandler.updateViewStats(DatabaseHandler.STATS_TRANSLATION_VIEWS, views);
    }
}
