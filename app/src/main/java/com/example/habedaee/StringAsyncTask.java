package com.example.habedaee;

import android.os.AsyncTask;

public class StringAsyncTask extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] objects) {
        Model.get().createStrings();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {

    }
}
