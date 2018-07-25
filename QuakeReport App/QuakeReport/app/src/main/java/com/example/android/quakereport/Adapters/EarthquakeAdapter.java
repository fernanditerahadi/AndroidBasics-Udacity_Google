package com.example.android.quakereport.Adapters;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.quakereport.Models.EarthquakeData;
import com.example.android.quakereport.R;

import java.util.ArrayList;

public class EarthquakeAdapter extends ArrayAdapter<EarthquakeData> {

    public EarthquakeAdapter(Activity context, ArrayList<EarthquakeData> earthquakeData) {
        super(context, 0, earthquakeData);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.list_item, parent, false);
        }

        EarthquakeData currEarthQuakeData = getItem(position);


        TextView magTextView = (TextView) listItemView.findViewById(R.id.tv_mag);
        magTextView.setText(currEarthQuakeData.getMag());

        GradientDrawable magCircle = (GradientDrawable) magTextView.getBackground();
        int magCircleColor = getMagColor(currEarthQuakeData.getMag());
        magCircle.setColor(magCircleColor);

        TextView distanceTextView = (TextView) listItemView.findViewById(R.id.tv_offset);
        distanceTextView.setText(currEarthQuakeData.getOffsetFromCity());

        TextView cityTextView = (TextView) listItemView.findViewById(R.id.tv_location);
        cityTextView.setText(currEarthQuakeData.getCity());

        TextView dateTextView = (TextView) listItemView.findViewById(R.id.tv_date);
        dateTextView.setText(currEarthQuakeData.getDate());

        TextView timeTextView = (TextView) listItemView.findViewById(R.id.tv_time);
        timeTextView.setText(currEarthQuakeData.getTime());

        return listItemView;
    }

    private int getMagColor(String magnitude) {
        int mag = Integer.parseInt(magnitude.replace(".",""))/100;
        int magColorResourceID = -1;
        if (mag >=0 && mag < 2) {
            magColorResourceID = R.color.magnitude1;
        }
        else if (mag >=2 && mag < 3) {
            magColorResourceID = R.color.magnitude2;
        }
        else if (mag >=3 && mag < 4) {
            magColorResourceID = R.color.magnitude3;
        }
        else if (mag >=4 && mag < 5) {
            magColorResourceID = R.color.magnitude4;
        }
        else if (mag >=5 && mag < 6) {
            magColorResourceID = R.color.magnitude5;
        }
        else if (mag >=6 && mag < 7) {
            magColorResourceID = R.color.magnitude6;
        }
        else if (mag >=7 && mag < 8) {
            magColorResourceID = R.color.magnitude7;
        }
        else if (mag >=8 && mag < 9) {
            magColorResourceID = R.color.magnitude8;
        }
        else if (mag >=9 && mag < 10) {
            magColorResourceID = R.color.magnitude9;
        }
        else if (mag >=10 ) {
            magColorResourceID = R.color.magnitude10plus;
        }
        return ContextCompat.getColor(getContext(), magColorResourceID);
    }
}
