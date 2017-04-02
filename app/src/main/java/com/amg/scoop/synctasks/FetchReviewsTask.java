package com.amg.scoop.synctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;

import com.amg.scoop.BuildConfig;
import com.amg.scoop.R;
import com.amg.scoop.mainactivity.SelectItemListener;
import com.amg.scoop.models.Review;

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

public class FetchReviewsTask extends AsyncTask<String, Void, ArrayList<Review>> {

    public static ArrayList<Review> reviews;
    Activity activity;
    private ProgressDialog dialog;
    private SelectItemListener selectItemListener;

    public FetchReviewsTask(Activity activity, SelectItemListener selectItemListener) {
        this.selectItemListener = selectItemListener;
        dialog = new ProgressDialog(activity);
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage(activity.getString(R.string.reviews));
        dialog.show();
    }

    @Override
    protected ArrayList<Review> doInBackground(String... params) {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            Uri builtUri = Uri.parse(activity.getString(R.string.movie_url))
                    .buildUpon().appendPath(params[0]).appendPath(activity.getString(R.string.asreviews))
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
            return getReviewsDataFromJson(buffer.toString());
        } catch (Exception e) {
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private ArrayList<Review> getReviewsDataFromJson(String jsonStr) throws JSONException {
        JSONArray reviewArray = new JSONObject(jsonStr).getJSONArray("results");
        reviews = new ArrayList<>();

        for (int i = 0; i < reviewArray.length(); i++) {
            reviews.add(new Review(reviewArray.getJSONObject(i)));
        }
        return reviews;
    }

    @Override
    protected void onPostExecute(ArrayList<Review> reviews) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        selectItemListener.onAsyncTask();
    }
}