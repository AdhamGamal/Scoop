package com.amg.scoop.data;

import android.net.Uri;

public class MovieContract {
    public static final String TABLE_NAME = "movies";

    private static final String AUTHORITY = "com.amg.scoop";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_JSON = "movie_json";
}
