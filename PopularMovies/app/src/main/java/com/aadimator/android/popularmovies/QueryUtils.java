package com.aadimator.android.popularmovies;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aadam on 10/9/2016.
 */

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // So no one could instantiate it
    private QueryUtils() {
    }

    public static List<Movie> fetchMoviesData(String requestUrl) {

        // Create a URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list
        List<Movie> movies = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Movie}
        return movies;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // if the url is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200)
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code : " + urlConnection.getResponseCode());

            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "URL: " + url.toString());
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies that an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Movie> extractFeatureFromJson(String jsonString) {
        // If the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding movies to
        List<Movie> movieList = new ArrayList<>();

        // Try to parse the jsonString
        try {

            JSONObject baseJsonResponse = new JSONObject(jsonString);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of movies.
            JSONArray resultsArray = baseJsonResponse.getJSONArray("results");

            // For each movie in the moviesArray, create a {@link Movie} object
            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject currentMovie = resultsArray.getJSONObject(i);

                String title = currentMovie.getString("original_title");
                String imageUrl = currentMovie.getString("poster_path");
                String plot = currentMovie.getString("overview");
                String ratings = currentMovie.getString("vote_average");
                String releaseDate = currentMovie.getString("release_date");

                movieList.add(new Movie(title, imageUrl, plot, ratings, releaseDate));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results.", e);
        }

        return movieList;
    }
}
