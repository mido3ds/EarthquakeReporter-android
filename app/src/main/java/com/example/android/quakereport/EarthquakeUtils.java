package com.example.android.quakereport;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeUtils {
    public static String getJSON(final String url, Context context) {
        // TODO
//        RequestQueue queue = Volley.newRequestQueue(context);
//        String result;
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        result = response;
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(EarthquakeUtils.class.getName(), "couldn't get response from url"+url);
//                    }
//                }
//        );
//
//        queue.add(stringRequest);
//
//        return result;
        return "";
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
}
