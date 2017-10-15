package com.example.android.quakereport;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeUtils {
    public static ArrayList<Earthquake> getLatestEarthQuakes(int limit) {
        URL query = null;
        try {
            query = buildQuery(limit);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(EarthquakeUtils.class.getName(), "error in query constructing ");
        }

        return parseJSON(fetchJSON(query));
    }

    public static String fetchJSON(final URL url) {
        FetchJsonTask task = new FetchJsonTask();
        try {
            return task.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Earthquake.class.getName(), "error in executing async task FetchJsonTask");

            return "";
        }
    }

    public static ArrayList<Earthquake> parseJSON(final String json) {
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
            Log.e(EarthquakeUtils.class.getName(), "couldn't parse");
        }
        return earthquakes;
    }

    private static String formatDate(Long epochSeconds) {
        Date date = new Date(epochSeconds);
        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        return dateFormat.format(date);
    }

    private static final String START_QUERY = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&";
    private static URL buildQuery(int limit) throws MalformedURLException {
        return new URL(START_QUERY + "limit" + "=" + Integer.toString(limit));
    }

    private static String streamToString(final BufferedInputStream inputStream) throws IOException {
        return IOUtils.toString(inputStream, "UTF-8");
    }

    private static class FetchJsonTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            String result = "";
            URL url = urls[0];
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000 /*ms*/);
                connection.setReadTimeout(1500 /*ms*/);

                result = streamToString(new BufferedInputStream(connection.getInputStream()));

                if (connection.getResponseMessage() != "OK") {
                    throw new Exception("response not ok");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(EarthquakeUtils.class.getName(), "couldn't establish a connection");
            } finally {
                assert connection != null;
                connection.disconnect();
            }

            return result;
        }
    }
}
