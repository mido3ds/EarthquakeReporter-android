package com.example.android.quakereport;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeUtils {
    public static String getJSON(final String urlString) {
        // TODO
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        String result = "";

        try {
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            readStream(inputStream);
            result = streamToString(inputStream);
        } catch (Exception e) {
            Log.e(EarthquakeUtils.class.getName(), "couldn't get response");
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return result;
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

    private static String streamToString(final InputStream inputStream) {
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");

        return writer.toString();
    }
}
