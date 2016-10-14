package com.aadimator.android.popularmovies.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.aadimator.android.popularmovies.data.MovieContract;
import com.aadimator.android.popularmovies.data.MovieContract.MovieEntry;
import com.aadimator.android.popularmovies.fragments.MoviesFragment;
import com.aadimator.android.popularmovies.models.Movie;
import com.aadimator.android.popularmovies.models.Review;

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

    public static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String IMG_BASE_URL = "https://image.tmdb.org/t/p/";
    public static final String DEFAULT_IMG_SIZE = "w185";
    public static final String API_KEY = "5b449f77e39ea99dc845544707c12fbb";

    // So no one could instantiate it
    private QueryUtils() {
    }

    public static List<Movie> fetchMoviesData(String requestUrl, Context context) {

        // Create a URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Movie> movieList = extractMoviesFromJson(jsonResponse);
        List<Integer> movieIdList = getIdsOfFavoriteMovies(context);

        for (Movie movie : movieList) {
            if (movieIdList.contains(movie.getId())) {
                movie.setFavorite(true);
            }
        }

        // Return the list of {@link Movie}
        return movieList;
    }

    public static List<String> fetchTrailersData(String requestUrl) {

        // Create a URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Return the list of trailers
        return extractTrailersFromJson(jsonResponse);
    }

    public static List<Review> fetchReviewsData(String requestUrl) {

        // Create a URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Return the list of trailers
        return extractReviewsFromJson(jsonResponse);
    }

    /**
     * Returns the String URL Path to the fetch the movies
     */
    public static String createMoviesUrlPath(Context context) {
        Uri baseUri = Uri.parse(QueryUtils.BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        String page = "1";
        String lang = "en-US";

        String sortBy = MoviesFragment.getSortingPreference(context);
        uriBuilder.appendPath(sortBy);

        uriBuilder.appendQueryParameter("api_key", QueryUtils.API_KEY);
        uriBuilder.appendQueryParameter("language", lang);
        uriBuilder.appendQueryParameter("page", page);

        Log.d(LOG_TAG, "URL : " + uriBuilder.toString());

        return uriBuilder.toString();
    }

    /**
     * Returns the String URL Path to the fetch the trailers
     *
     * @param id which movie to fetch
     * @return path to fetch trailers
     */
    public static String createTrailersUrlPath(int id) {
        Uri baseUri = Uri.parse(BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        String lang = "en-US";

        uriBuilder.appendPath(Integer.toString(id));
        uriBuilder.appendPath("videos");

        uriBuilder.appendQueryParameter("api_key", API_KEY);
        uriBuilder.appendQueryParameter("language", lang);

        Log.d(LOG_TAG, "URL : " + uriBuilder.toString());

        return uriBuilder.toString();
    }

    /**
     * Returns the String URL Path to the fetch the reviews
     *
     * @param id which movie to fetch
     * @return path to fetch reviews
     */
    public static String createReviewsUrlPath(int id) {
        Uri baseUri = Uri.parse(BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        String lang = "en-US";
        String page = "1";

        uriBuilder.appendPath(Integer.toString(id));
        uriBuilder.appendPath("reviews");

        uriBuilder.appendQueryParameter("api_key", API_KEY);
        uriBuilder.appendQueryParameter("language", lang);
        uriBuilder.appendQueryParameter("page", page);

        Log.d(LOG_TAG, "URL : " + uriBuilder.toString());

        return uriBuilder.toString();
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
            Log.e(LOG_TAG, "Problem retrieving the movies JSON results.", e);
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
    private static List<Movie> extractMoviesFromJson(String jsonString) {
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

                int id = currentMovie.getInt("id");
                String title = currentMovie.getString("original_title");
                String imageUrl = currentMovie.getString("poster_path");
                String plot = currentMovie.getString("overview");
                double ratings = currentMovie.getDouble("vote_average");
                String releaseDate = currentMovie.getString("release_date");

                Movie movie = new Movie(id, title, imageUrl, plot, ratings, releaseDate);

                movie.setTrailers(new ArrayList<String>());
                movie.setReviews(new ArrayList<Review>());

                movie.setFavorite(false);

                movieList.add(movie);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results.", e);
        }

        return movieList;
    }


    /**
     * Return a list of {@link String} trailers that has been built up from
     * parsing the given JSON response.
     */
    private static List<String> extractTrailersFromJson(String jsonString) {
        // If the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding trailers to
        List<String> trailersList = new ArrayList<>();

        // Try to parse the jsonString
        try {

            JSONObject baseJsonResponse = new JSONObject(jsonString);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of trailers.
            JSONArray resultsArray = baseJsonResponse.getJSONArray("results");

            // For each trailer in the resultsArray, create a {@link String} trailer
            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject currentTrailer = resultsArray.getJSONObject(i);

                String site = currentTrailer.getString("site");

                if (site.toLowerCase().equals("youtube")) {
                    String key = currentTrailer.getString("key");
                    trailersList.add(key);
                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results.", e);
        }

        return trailersList;
    }


    /**
     * Return a list of {@link Review} reviews that has been built up from
     * parsing the given JSON response.
     */
    private static List<Review> extractReviewsFromJson(String jsonString) {
        // If the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding reviews to
        List<Review> reviewList = new ArrayList<>();

        // Try to parse the jsonString
        try {

            JSONObject baseJsonResponse = new JSONObject(jsonString);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of reviews.
            JSONArray resultsArray = baseJsonResponse.getJSONArray("results");

            // For each trailer in the resultsArray, create a {@link Review} reviews
            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject currentReview = resultsArray.getJSONObject(i);

                String author = currentReview.getString("author");
                String content = currentReview.getString("content");
                reviewList.add(new Review(author, content));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results.", e);
        }

        return reviewList;
    }

    public static List<Movie> fetchMoviesDataFromDatabase(Cursor cursor) {
        List<Movie> movieList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int movieID = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID));
            String title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE));
            String imagePath = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_IMAGE_PATH));
            String plot = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_PLOT));
            Double rating = cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RATING));
            String releaseDate = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RELEASE_DATE));

            Movie movie = new Movie(movieID, title, imagePath, plot, rating, releaseDate);
            movie.setTrailers(new ArrayList<String>());
            movie.setReviews(new ArrayList<Review>());
            movie.setFavorite(true);
            movie.setLoadFromDb(true);

            movieList.add(movie);
        }

        cursor.close();

        return movieList;
    }

    public static List<Integer> getIdsOfFavoriteMovies(Context context) {
        Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null, null, null, null);
        List<Integer> idList = new ArrayList<>();
        while (cursor.moveToNext()) {
            idList.add(cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID)));
        }
        return idList;
    }
}
