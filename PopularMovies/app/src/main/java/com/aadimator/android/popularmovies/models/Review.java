package com.aadimator.android.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aadam on 10/11/2016.
 */

public class Review implements Parcelable {

    private String mAuthor;
    private String mContent;

    public Review(String author, String content) {
        mAuthor = author;
        mContent = content;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    // Parcelable potion

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mAuthor);
        dest.writeString(this.mContent);
    }

    protected Review(Parcel in) {
        this.mAuthor = in.readString();
        this.mContent = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}