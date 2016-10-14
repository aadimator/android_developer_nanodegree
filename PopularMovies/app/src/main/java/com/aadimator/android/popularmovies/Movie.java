package com.aadimator.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aadam on 10/9/2016.
 */

public class Movie implements Parcelable {

    private String mTitle;

    // Relative path to the Image (poster)
    private String mImageUrl;

    private String mPlot;
    private String mRating;
    private String mReleaseDate;

    /**
     * Constructor for the @Movie
     *
     * @param title       original title (original_title)
     * @param imageUrl    relative path to the image (poster_path)
     * @param plot        plot of the movie (overview)
     * @param rating      user ratings (vote_average)
     * @param releaseDate (release_date) "yyyy-MM-dd"
     */
    public Movie(String title, String imageUrl, String plot, String rating, String releaseDate) {
        mTitle = title;
        mImageUrl = imageUrl;
        mPlot = plot;
        mRating = rating;
        mReleaseDate = releaseDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImageUrl() {
        return MainActivity.IMG_BASE_URL + MainActivity.DEFAULT_IMG_SIZE + mImageUrl;
    }

    public String getPlot() {
        return mPlot;
    }

    public String getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    // Parcelling part


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTitle);
        dest.writeString(this.mImageUrl);
        dest.writeString(this.mPlot);
        dest.writeString(this.mRating);
        dest.writeString(this.mReleaseDate);
    }

    protected Movie(Parcel in) {
        this.mTitle = in.readString();
        this.mImageUrl = in.readString();
        this.mPlot = in.readString();
        this.mRating = in.readString();
        this.mReleaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
