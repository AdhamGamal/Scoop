package com.amg.scoop.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie implements Parcelable {

    private int id;
    private String title;
    private String posterPath;
    private String backdropPath;
    private String overview;
    private String date;
    private int rating;
    private boolean favorite;
    private JSONObject Movie_jason;

    public Movie(JSONObject Movie_jason) {
        try {
            this.Movie_jason = Movie_jason;
            this.id = Movie_jason.getInt("id");
            this.title = Movie_jason.getString("original_title");
            this.posterPath = Movie_jason.getString("poster_path");
            this.backdropPath = Movie_jason.getString("backdrop_path");
            this.overview = Movie_jason.getString("overview");
            this.rating = Movie_jason.getInt("vote_average");
            this.date = Movie_jason.getString("release_date");
        } catch (JSONException e) {
        }
    }

    protected Movie(Parcel in) throws JSONException {
        id = in.readInt();
        title = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        overview = in.readString();
        date = in.readString();
        rating = in.readInt();
        favorite = in.readByte() != 0;
        Movie_jason = in.readByte() == 0x00 ? null : new JSONObject(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(overview);
        dest.writeString(date);
        dest.writeInt(rating);
        dest.writeByte((byte) (favorite ? 0x01 : 0x00));
        if (Movie_jason == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(Movie_jason.toString());
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            try {
                return new Movie(in);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getDate() {
        return date;
    }

    public int getRating() {
        return rating;
    }

    public JSONObject getMovie_jason() {
        return Movie_jason;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

}




