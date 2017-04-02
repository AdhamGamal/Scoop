package com.amg.scoop.detailsactivity;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amg.scoop.R;
import com.amg.scoop.models.Movie;
import com.amg.scoop.models.Review;
import com.amg.scoop.models.Trailer;
import com.amg.scoop.synctasks.FetchReviewsTask;
import com.amg.scoop.synctasks.FetchTrailersTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.amg.scoop.adapters.MoviesAdapter.getColor;
import static com.amg.scoop.data.MovieContract.BASE_URI;
import static com.amg.scoop.data.MovieContract.MOVIE_ID;
import static com.amg.scoop.data.MovieContract.MOVIE_JSON;

public class DetailsActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        final Movie movie = getArguments().getParcelable("movie");
        final ArrayList<Review> reviews = FetchReviewsTask.reviews;
        final ArrayList<Trailer> trailers = FetchTrailersTask.trailers;

        Picasso.with(getContext()).load(getString(R.string.poster_url) + movie.getPosterPath()).
                error(R.drawable.default_poster).into((ImageView) rootView.findViewById(R.id.image));

        Picasso.with(getContext()).load(getString(R.string.poster_url) + movie.getBackdropPath()).
                error(R.drawable.default_poster).into((ImageView) rootView.findViewById(R.id.image2));

        ((TextView) rootView.findViewById(R.id.title)).setText(movie.getTitle());

        ((TextView) rootView.findViewById(R.id.date)).setText(getString(R.string.date) + " " + movie.getDate());

        ((TextView) rootView.findViewById(R.id.rating)).setText(getString(R.string.rate) + " " + movie.getRating() +
                getString(R.string.over10));

        ((TextView) rootView.findViewById(R.id.overview)).setText(movie.getOverview());


        int trailers_size = trailers.size();
        if (trailers_size > 0) {
            LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.trailersLayout);

            for (int i = 0; i < trailers_size; i++) {
                RelativeLayout thumb = (RelativeLayout) getLayoutInflater(savedInstanceState).inflate(R.layout.trailers, null);

                ImageView trailerImageView = (ImageView) thumb.findViewById(R.id.trailer_image);
                Picasso.with(getContext()).load(getString(R.string.trailers_poster_url) + trailers.get(i).getKey() + getString(R.string.default_size))
                        .error(R.drawable.default_poster).into(trailerImageView);

                final int finalI = i;
                trailerImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(getString(R.string.trailers_url) + trailers.get(finalI).getKey()));
                        startActivity(intent);
                    }
                });

                TextView name = (TextView) thumb.findViewById(R.id.trailer_name);
                name.setText(trailers.get(i).getName());
                name.setBackgroundColor(getColor() * 100);
                linearLayout.addView(thumb);
            }
        }

        int reviews_size = reviews.size();
        if (reviews_size > 0) {
            LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.root);

            for (int i = 0; i < reviews_size; i++) {
                LinearLayout review = (LinearLayout) getLayoutInflater(savedInstanceState).inflate(R.layout.reviews, null);

                ((TextView) review.findViewById(R.id.reviewer)).setText(reviews.get(i).getAuthor());

                ((TextView) review.findViewById(R.id.content)).setText(reviews.get(i).getContent());

                linearLayout.addView(review);
            }
        } else {
            rootView.findViewById(R.id.reviewHeader).setVisibility(View.GONE);
        }

        CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.favorite);

        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    ContentValues values = new ContentValues();
                    values.put(MOVIE_ID, movie.getId());
                    values.put(MOVIE_JSON, movie.getMovie_jason().toString());
                    getActivity().getContentResolver().insert(BASE_URI, values);
                } else {
                    getActivity().getContentResolver().delete(
                            BASE_URI,
                            MOVIE_ID + " = ?",
                            new String[]{Integer.toString(movie.getId())}
                    );
                }
                movie.setFavorite(((CheckBox) v).isChecked());
            }
        });

        checkBox.setChecked(movie.isFavorite());

        return rootView;
    }
}