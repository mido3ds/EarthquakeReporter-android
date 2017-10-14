package com.example.android.quakereport;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeBuilder {
    public static ArrayList<Earthquake> parseEarthQuakes(final String json) {
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

                earthquakes.add(new Earthquake(mag, place, formatDate(epochTime)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(EarthquakeBuilder.class.getName(), "couldn't parse");
        }
        return earthquakes;
    }

    private static String formatDate(Long epochSeconds) {
        Date date = new Date(epochSeconds);
        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        return dateFormat.format(date);
    }
}
