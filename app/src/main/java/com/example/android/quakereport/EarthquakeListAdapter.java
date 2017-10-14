package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
        GradientDrawable magnitudeDrawable = (GradientDrawable) magnitudeText.getBackground();
        TextView placeText = convertView.findViewById(R.id.place_name_text);
        TextView dateText = convertView.findViewById(R.id.date_text);

        magnitudeText.setText(String.format("%s", earthquake.getMagnitude()));
        magnitudeDrawable.setColor(earthquake.getMagnitudeColor(getContext()));
        placeText.setText(String.format("%s", earthquake.getPlace()));
        dateText.setText(String.format("%s", earthquake.getDate()));

        return convertView;
    }

    private final ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Earthquake earthquake = getItem(position);
            String url = earthquake.getUrl();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(url));
            getContext().startActivity(browserIntent);
        }
    };

    public ListView.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }
}
