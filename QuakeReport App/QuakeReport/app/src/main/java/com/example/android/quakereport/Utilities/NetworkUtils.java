package com.example.android.quakereport.Utilities;

import android.util.Log;

import com.example.android.quakereport.Models.EarthquakeData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;

public final class NetworkUtils {

    private NetworkUtils(){
    }

    public static ArrayList<EarthquakeData> fetchEarthquakeData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;

        try {
            jsonResponse = getJsonResponseFromUrl(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Unable to get JSON Response", e);
        }
        return JsonUtils.extractEarthquakes(jsonResponse);
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Cannot create an URL with the following string: " + stringUrl);
        }
        return url;
    }

    private static String getJsonResponseFromUrl(URL url) throws IOException {
        String jsonResponse = null;
        if (url == null) {
            return null;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readJsonFromInputStream(inputStream);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to connect to the URL: " + url, e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readJsonFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder jsonResponse = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                jsonResponse.append(line);
                line = bufferedReader.readLine();
            }
        }
        return jsonResponse.toString();
    }
}
