package com.hofmn.defineo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.hofmn.defineo.fragments.DefinitionFragment;
import com.hofmn.defineo.fragments.TrainingFragment;
import com.hofmn.defineo.fragments.TranslateFragment;
import com.hofmn.defineo.fragments.WordCardFragment;

public class TrainingActivity extends FragmentActivity
        implements WordCardFragment.OnWordClick, DefinitionFragment.OnShowTranslationClicked {

    public static final String MY_PREFS = "prefs";
    public static final String IS_DEFINITION_SHOWN = "definition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new TrainingFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onWordClick(String word) {
        showFragment(DefinitionFragment.newInstance(word));
    }

    @Override
    public void onShowTranslation(String word) {
        showFragment(TranslateFragment.newInstance(word));
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
