/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport.Activites;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.android.quakereport.Earthquake;
import com.example.android.quakereport.EarthquakeListAdapter;
import com.example.android.quakereport.EarthquakeLoader;
import com.example.android.quakereport.R;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Earthquake>> {
    private final int ITEMS_LIMIT = 30;
    private final int LOADER_ID = 1;

    private ListView earthquakeListView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_earthquake);

        getLoaderManager().initLoader(LOADER_ID, null, this);
        earthquakeListView = (ListView) findViewById(R.id.list);
        progressBar = (ProgressBar) findViewById(R.id.progress);
    }

    private void setListEmptyView(String text) {
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        emptyView.setText(text);
        earthquakeListView.setEmptyView(emptyView);
    }

    @Override
    public Loader<ArrayList<Earthquake>> onCreateLoader(int id, Bundle args) {
        return new EarthquakeLoader(this, ITEMS_LIMIT);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Earthquake>> loader, ArrayList<Earthquake> earthquakes) {
        EarthquakeListAdapter adapter = new EarthquakeListAdapter(this, earthquakes);
        earthquakeListView.setAdapter(adapter);
        earthquakeListView.setOnItemClickListener(adapter.getOnItemClickListener());
        progressBar.setVisibility(View.GONE);

        if (earthquakes.size() == 0) {
            setListEmptyView(((EarthquakeLoader) loader).getFailureReason());
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Earthquake>> loader) {}

}
