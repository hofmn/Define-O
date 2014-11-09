package com.hofmn.defineo;

import com.hofmn.defineo.data.model.WordData;

import java.util.ArrayList;

public class DefineoApp {
    private static DefineoApp mInstance = null;

    private ArrayList<WordData> data;

    private DefineoApp() {

    }

    public static DefineoApp getInstance() {
        if (mInstance == null) {
            mInstance = new DefineoApp();
        }
        return mInstance;
    }

    public ArrayList<WordData> getData() {
        return data;
    }

    public void setData(ArrayList<WordData> data) {
        this.data = data;
    }
}