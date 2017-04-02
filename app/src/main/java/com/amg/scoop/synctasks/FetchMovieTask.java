package com.amg.scoop.synctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;

import com.amg.scoop.BuildConfig;
import com.amg.scoop.R;
import com.amg.scoop.mainactivity.OnMovieTaskExecute;
import com.amg.scoop.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    public static ArrayList<Movie> movies;
    Activity activity;
    private ProgressDialog dialog;
    private OnMovieTaskExecute onMovieTaskExecute;

    public FetchMovieTask(Activity activity, OnMovieTaskExecute onMovieTaskExecute) {
        this.onMovieTaskExecute = onMovieTaskExecute;
        dialog = new ProgressDialog(activity);
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage(activity.getString(R.string.movies));
        dialog.show();
    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            Uri builtUri = Uri.parse(activity.getString(R.string.movie_url))
                    .buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter(activity.getString(R.string.api_key), BuildConfig.MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }

            return getMoviesDataFromJson(buffer.toString());
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private ArrayList<Movie> getMoviesDataFromJson(String jsonStr) throws JSONException {
        JSONArray movieArray = new JSONObject(jsonStr).getJSONArray("results");
        movies = new ArrayList<>();
        for (int i = 0; i < movieArray.length(); i++) {
            movies.add(new Movie(movieArray.getJSONObject(i)));
        }
        return movies;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        onMovieTaskExecute.findFavorite(movies);
    }
}