package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeLoader extends AsyncTaskLoader<ArrayList<Earthquake>> {
    private static final String USGS_WEBSITE = "earthquake.usgs.gov";
    private static final String START_QUERY = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    private static final String REASON_INTERNAL = "Internal Error Happened";
    private static final String REASON_NO_INT = "No Internet Connection";
    private static final String REASON_NO_DATA = "No Earthquake Data";

    private String failureReason;
    private URL url = null;

    public EarthquakeLoader(Context context, int limit, String minMagnitude, String orderBy) {
        super(context);

        try {
            url = buildQuery(Math.abs(limit), minMagnitude, orderBy);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(EarthquakeLoader.class.getName(), "error in query constructing ");
            failureReason = REASON_INTERNAL;
        }
    }

    private static String streamToString(final BufferedInputStream inputStream) throws IOException {
        return IOUtils.toString(inputStream, "UTF-8");
    }

    private static String formatDate(Long epochSeconds) {
        Date date = new Date(epochSeconds);
        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        return dateFormat.format(date);
    }

    @NonNull
    private static URL buildQuery(int limit, String minMagnitude, String orderBy) throws MalformedURLException {
        Uri baseUri = Uri.parse(START_QUERY);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", Integer.toString(limit));
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new URL(uriBuilder.toString());
    }

    public String getFailureReason() {
        return failureReason;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    private ArrayList<Earthquake> parseJSON(final String json) {
        ArrayList<Earthquake> earthquakes = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray features = jsonObject.getJSONArray("features");

            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");

                Double mag = properties.getDouble("mag");
                String place = properties.getString("place");
                Long epochTime = properties.getLong("time");
                String url = properties.getString("url");

                earthquakes.add(new Earthquake(mag, place, formatDate(epochTime), url));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(EarthquakeLoader.class.getName(), "couldn't parse");
            failureReason = REASON_INTERNAL;
        }
        if (earthquakes.size() == 0) {
            failureReason = REASON_NO_DATA;
        }

        return earthquakes;
    }

    @Override
    public ArrayList<Earthquake> loadInBackground() {
        String result = "";
        HttpURLConnection connection = null;

        if (!isConnectedToInternet()) {
            failureReason = REASON_NO_INT;
            return new ArrayList<>();
        }

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000 /*ms*/);
            connection.setReadTimeout(1500 /*ms*/);

            result = streamToString(new BufferedInputStream(connection.getInputStream()));

            if (!connection.getResponseMessage().equals("OK")) {
                throw new Exception("response not ok");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(EarthquakeLoader.class.getName(), "couldn't establish a connection");
            failureReason = REASON_INTERNAL;
        } finally {
            assert connection != null;
            connection.disconnect();
        }

        return parseJSON(result);
    }

    private boolean isConnectedToInternet() {
        try {
            InetAddress ipAddr = InetAddress.getByName(USGS_WEBSITE);
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }
}
