package com.aadimator.android.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.aadimator.android.popularmovies.helpers.QueryUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aadam on 10/9/2016.
 */

public class Movie implements Parcelable {

    private int mId;
    private String mTitle;
    private String mImageUrl;
    private String mPlot;
    private double mRating;
    private String mReleaseDate;
    private List<String> mTrailers; // only store the YouTube video key, we'll build the url ourselves
    private List<Review> mReviews;

    private boolean mFavorite = false; //
    private boolean mLoadFromDb = false;

    /**
     * Constructor for the @Movie
     *
     * @param id
     * @param title       original title (original_title)
     * @param imageUrl    relative path to the image (poster_path)
     * @param plot        plot of the movie (overview)
     * @param rating      user ratings (vote_average)
     * @param releaseDate (release_date) "yyyy-MM-dd"
     */
    public Movie(int id, String title, String imageUrl, String plot, double rating, String releaseDate) {
        mId = id;
        mTitle = title;
        mImageUrl = imageUrl;
        mPlot = plot;
        mRating = rating;
        mReleaseDate = releaseDate;
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImageUrl() {
        if (mLoadFromDb == true) {
            return mImageUrl; // already contains the content:URI
        }
        return QueryUtils.IMG_BASE_URL + QueryUtils.DEFAULT_IMG_SIZE + mImageUrl;
    }

    public String getPlot() {
        return mPlot;
    }

    public double getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getReleaseYear() {
        return mReleaseDate.split("-")[0];
    }

    public List<String> getTrailers() {
        return mTrailers;
    }

    public List<String> getYoutubeLinks() {
        List<String> links = new ArrayList<>();
        if (mTrailers != null) {
            for (String s : mTrailers) {
                links.add("https://www.youtube.com/watch?v=" + s);
            }
        }
        return links;
    }

    public void setTrailers(List<String> trailers) {
        mTrailers = trailers;
    }

    public List<Review> getReviews() {
        return mReviews != null ? mReviews : new ArrayList<Review>();
    }

    public void setReviews(List<Review> reviews) {
        mReviews = reviews;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }

    public void setLoadFromDb(boolean loadFromDb) {
        mLoadFromDb = loadFromDb;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    // Parcelling part


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mImageUrl);
        dest.writeString(this.mPlot);
        dest.writeDouble(this.mRating);
        dest.writeString(this.mReleaseDate);
        dest.writeStringList(this.mTrailers);
        dest.writeTypedList(this.mReviews);
        dest.writeByte(this.mFavorite ? (byte) 1 : (byte) 0);
    }

    protected Movie(Parcel in) {
        this.mId = in.readInt();
        this.mTitle = in.readString();
        this.mImageUrl = in.readString();
        this.mPlot = in.readString();
        this.mRating = in.readDouble();
        this.mReleaseDate = in.readString();
        this.mTrailers = in.createStringArrayList();
        this.mReviews = in.createTypedArrayList(Review.CREATOR);
        this.mFavorite = in.readByte() != 0;
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