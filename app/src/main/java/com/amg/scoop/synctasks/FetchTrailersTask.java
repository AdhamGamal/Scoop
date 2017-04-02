package com.amg.scoop.synctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;

import com.amg.scoop.BuildConfig;
import com.amg.scoop.R;
import com.amg.scoop.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FetchTrailersTask extends AsyncTask<String, Void, ArrayList<Trailer>> {

    public static ArrayList<Trailer> trailers;
    Activity activity;
    private ProgressDialog dialog;

    public FetchTrailersTask(Activity activity) {
        dialog = new ProgressDialog(activity);
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage(activity.getString(R.string.trailers));
        dialog.show();
    }

    @Override
    protected ArrayList<Trailer> doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            Uri builtUri = Uri.parse(activity.getString(R.string.movie_url))
                    .buildUpon().appendPath(params[0]).appendPath(activity.getString(R.string.videos))
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
            return getTrailersDataFromJson(buffer.toString());
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
            } catch (IOException e) {
            }
        }
    }

    private ArrayList<Trailer> getTrailersDataFromJson(String jsonStr) throws JSONException {
        JSONArray trailerArray = new JSONObject(jsonStr).getJSONArray("results");
        trailers = new ArrayList<>();

        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject trailer = trailerArray.getJSONObject(i);
            if (trailer.getString("site").contentEquals("YouTube")) {
                trailers.add(new Trailer(trailer));
            }
        }
        return trailers;
    }

    @Override
    protected void onPostExecute(ArrayList<Trailer> trailers) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}