package com.example.android.quakereport.Models;

import android.net.Uri;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EarthquakeData {

    private final static int OFFSET_INDEX = 0;
    private final static int LOCATION_INDEX = 1;

    private final static String WORD_TO_SPLIT = "of";
    private final static int WORD_TO_SPLIT_LENGTH = WORD_TO_SPLIT.length();

    private String[] mPlace;
    private Date mDate;
    private String mMag;
    private Uri mUri;


    public EarthquakeData(String place, String date, double mag, String url) {
        mPlace = createPlace(place);
        mDate = createDate(date);
        mMag = createMag(mag);
        mUri = createUri(url);
    }


    public String getOffsetFromCity() {
        return mPlace[OFFSET_INDEX];
    }

    public String getCity() {
        return mPlace[LOCATION_INDEX];
    }

    public String getDate() {
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        return df.format(mDate);
    }

    public String getTime() {
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a", Locale.US);
        return df.format(mDate);
    }

    public String getMag() {
        return mMag;
    }

    public Uri getUri() {
        return mUri;
    }

    private String[] createPlace(String place) {
        String[] mPlaceObject = new String[2];
        int position = place.indexOf(WORD_TO_SPLIT);
        if (position != -1) {
            mPlaceObject[OFFSET_INDEX] = place.substring(0, position + WORD_TO_SPLIT_LENGTH);
            mPlaceObject[LOCATION_INDEX] = place.substring(position + WORD_TO_SPLIT_LENGTH);
        } else {
            mPlaceObject[OFFSET_INDEX] = "";
            mPlaceObject[LOCATION_INDEX] = place;
        }
        return mPlaceObject;
    }


    private Date createDate(String date) {
        long epoch = Long.parseLong(date);
        return new Date(epoch);
    }

    private String createMag(double mag) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(mag);
    }

    private Uri createUri(String url) {
        return Uri.parse(url);
    }
}
