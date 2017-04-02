package com.amg.scoop.mainactivity;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.amg.scoop.R;
import com.amg.scoop.adapters.MoviesAdapter;
import com.amg.scoop.models.Movie;
import com.amg.scoop.synctasks.FetchMovieTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.amg.scoop.data.MovieContract.BASE_URI;
import static com.amg.scoop.data.MovieContract.MOVIE_ID;
import static com.amg.scoop.data.MovieContract.MOVIE_JSON;


public class MainActivityFragment extends Fragment implements OnMovieTaskExecute {

    public static int selecteditem = R.id.popular;
    public static int position = 0;
    private static SelectItemListener selectedMovie;
    private GridView gridView;
    private MoviesAdapter moviesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMovie.onItemSelected(moviesAdapter.getItem(position));
            }
        });

        gridView.setSelection(position);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                position = gridView.getFirstVisiblePosition();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        Configuration newConfig = getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridView.setNumColumns(3);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridView.setNumColumns(2);
        }

        if (hasInternetAccess()) {
            switchItems(selecteditem);
        } else {
            Toast.makeText(getContext(), getString(R.string.noConnection), Toast.LENGTH_SHORT).show();
            selecteditem = R.id.favorite;
            FetchMovieTask.movies = getFavoriteMovies();
            setMoviesAdapter();
        }

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (R.id.refresh == item.getItemId())
            switchItems(selecteditem);
        else {
            switchItems(item.getItemId());
            item.setChecked(true);
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchItems(int id) {
        switch (id) {
            case R.id.popular:
                selecteditem = R.id.popular;
                executeSync(getString(R.string.popular));
                break;

            case R.id.top_rate:
                selecteditem = R.id.top_rate;
                executeSync(getString(R.string.top_rated));
                break;

            case R.id.favorite:
                selecteditem = R.id.favorite;
                FetchMovieTask.movies = getFavoriteMovies();
                setMoviesAdapter();
                break;
        }
    }

    private void executeSync(String arg) {
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity(), this);
        fetchMovieTask.execute(arg);
    }

    @Override
    public void findFavorite(List<Movie> movies) {
        List<Movie> favoriteMovies = getFavoriteMovies();
        if (FetchMovieTask.movies != null) {
            for (Movie favoritemovie : favoriteMovies) {
                for (Movie movie : movies) {
                    if (favoritemovie.getId() == movie.getId()) {
                        movie.setFavorite(true);
                    }
                }
            }
            setMoviesAdapter();
        }
    }

    private void setMoviesAdapter() {
        moviesAdapter = new MoviesAdapter(getActivity(), FetchMovieTask.movies);
        gridView.setAdapter(moviesAdapter);
    }

    public boolean hasInternetAccess() {
        NetworkInfo activeNetwork = ((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        boolean isWiFi = (isConnected) && (isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);
        return isConnected || isWiFi;
    }

    public void setInterfac(SelectItemListener selectedMovie) {
        MainActivityFragment.selectedMovie = selectedMovie;
    }

    public ArrayList<Movie> getFavoriteMovies() {
        String[] MOVIE_COLUMNS = {
                MOVIE_ID,
                MOVIE_JSON,
        };
        Cursor cursor = getActivity().getContentResolver().query(
                BASE_URI,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
        ArrayList<Movie> favorites = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            try {
                Movie movie = null;
                do {
                    movie = new Movie(new JSONObject(cursor.getString(1)));
                    movie.setFavorite(true);
                    favorites.add(movie);
                } while (cursor.moveToNext());
            } catch (JSONException e) {
            }
            cursor.close();
        }
        return favorites;
    }
}
