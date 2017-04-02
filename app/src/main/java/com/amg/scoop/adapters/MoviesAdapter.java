package com.amg.scoop.adapters;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amg.scoop.R;
import com.amg.scoop.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import static com.amg.scoop.data.MovieContract.BASE_URI;
import static com.amg.scoop.data.MovieContract.MOVIE_ID;
import static com.amg.scoop.data.MovieContract.MOVIE_JSON;


public class MoviesAdapter extends ArrayAdapter<Movie> {
    private final Activity context;
    private ViewHolder viewHolder;

    public MoviesAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
        this.context = context;
    }

    public static int getColor() {
        float[] hsv = new float[3];
        Random rnd = new Random();
        Color.colorToHSV((Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)) + Color.BLACK), hsv);
        hsv[2] *= 0.6f;
        return Color.HSVToColor(hsv);
    }

    @Override
    public View getView(int position, View rootView, ViewGroup parent) {
        final Movie movie = getItem(position);

        if (rootView == null) {
            rootView = LayoutInflater.from(context).inflate(R.layout.grid_view_item, parent, false);
            viewHolder = new ViewHolder(rootView);
            rootView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rootView.getTag();
        }
        viewHolder.linearLayout.setBackgroundColor(getColor() * 100);

        viewHolder.titleTextView.setText(movie.getTitle());

        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w300" + movie.getPosterPath()).error(R.drawable.default_poster).into(viewHolder.imageView);

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    ContentValues values = new ContentValues();
                    values.put(MOVIE_ID, movie.getId());
                    values.put(MOVIE_JSON, movie.getMovie_jason().toString());
                    context.getContentResolver().insert(BASE_URI, values);
                } else {
                    context.getContentResolver().delete(
                            BASE_URI,
                            MOVIE_ID + " = ?",
                            new String[]{Integer.toString(movie.getId())}
                    );
                }
                movie.setFavorite(((CheckBox) v).isChecked());
            }
        });

        viewHolder.checkBox.setChecked(movie.isFavorite());

        return rootView;
    }

    private class ViewHolder {
        LinearLayout linearLayout;
        ImageView imageView;
        TextView titleTextView;
        CheckBox checkBox;

        public ViewHolder(View rootView) {
            linearLayout = (LinearLayout) rootView.findViewById(R.id.background);
            imageView = (ImageView) rootView.findViewById(R.id.image);
            titleTextView = (TextView) rootView.findViewById(R.id.title);
            checkBox = (CheckBox) rootView.findViewById(R.id.favorite);
        }
    }
}
