package com.example.android.quakereport;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EarthquakeListAdapter extends ArrayAdapter<Earthquake> {
    public EarthquakeListAdapter(Context context, List<Earthquake> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_earthquake, null);
        }

        Earthquake earthquake = getItem(position);
        TextView magnitudeText = convertView.findViewById(R.id.magnitude_text);
        TextView placeText = convertView.findViewById(R.id.place_name_text);
        TextView dateText = convertView.findViewById(R.id.date_text);

        magnitudeText.setText(String.format("%s", earthquake.getMagnitude()));
        placeText.setText(String.format("%s", earthquake.getPlace()));
        dateText.setText(String.format("%s", earthquake.getDate()));

        return convertView;
    }
}
