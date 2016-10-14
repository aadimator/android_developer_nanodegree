package com.aadimator.android.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Aadam on 10/12/2016.
 * <p>
 * This will store favorite movies
 */

public class MovieContract {

    // To prevent someone from accidentally instantiating the class
    private MovieContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.aadimator.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "movies";

    public static final class MovieEntry implements BaseColumns {

        /**
         * The content URI to access the movie data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of inventory.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single inventory.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * Name of database table for movies
         */
        public final static String TABLE_NAME = "movies";

        /**
         * Unique ID number for the inventory (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_MOVIE_ID = "movie_id";
        public final static String COLUMN_MOVIE_TITLE = "title";
        public final static String COLUMN_MOVIE_IMAGE_PATH = "image_path";
        public final static String COLUMN_MOVIE_PLOT = "plot";
        public final static String COLUMN_MOVIE_RATING = "rating";
        public final static String COLUMN_MOVIE_RELEASE_DATE = "release_date";


    }
}
