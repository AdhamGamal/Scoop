package com.amg.scoop.mainactivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;

import com.amg.scoop.R;
import com.amg.scoop.detailsactivity.DetailsActivity;
import com.amg.scoop.detailsactivity.DetailsActivityFragment;
import com.amg.scoop.models.Movie;
import com.amg.scoop.synctasks.FetchReviewsTask;
import com.amg.scoop.synctasks.FetchTrailersTask;


public class MainActivity extends AppCompatActivity implements SelectItemListener {

    private static boolean udacity;
    private boolean isTablet;
    private Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MainActivityFragment mainActivityFragment = new MainActivityFragment();
        mainActivityFragment.setInterfac(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, mainActivityFragment)
                    .commit();
        }

        if (udacity) {
            ImageView imageView = (ImageView) findViewById(R.id.intro);
            imageView.setImageResource(0);
        }

        if (findViewById(R.id.container) != null) {
            isTablet = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (!hasInternetAccess()) {
            menu.findItem(R.id.favorite).setChecked(true);
        } else {
            menu.findItem(MainActivityFragment.selecteditem).setChecked(true);
        }
        return true;
    }

    @Override
    public void onItemSelected(Movie movie) {

        bundle.putParcelable("movie", movie);

        FetchTrailersTask fetchTrailersTask = new FetchTrailersTask(this);
        fetchTrailersTask.execute(movie.getId() + "");

        FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(this, this);
        fetchReviewsTask.execute(movie.getId() + "");
    }

    @Override
    public void onAsyncTask() {
        if (FetchReviewsTask.reviews != null && FetchTrailersTask.trailers != null) {
            if (!isTablet) {
                Intent intent = new Intent(this, DetailsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                if (!udacity) {
                    ImageView imageView = (ImageView) findViewById(R.id.intro);
                    imageView.setImageResource(0);

                    udacity = true;
                }

                DetailsActivityFragment detailsActivityFragment = new DetailsActivityFragment();
                detailsActivityFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, detailsActivityFragment)
                        .commit();
            }
        } else {
            Toast.makeText(this, R.string.noConnection, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean hasInternetAccess() {
        NetworkInfo activeNetwork = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        boolean isWiFi = (isConnected) && (isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);
        return isConnected || isWiFi;
    }
}
