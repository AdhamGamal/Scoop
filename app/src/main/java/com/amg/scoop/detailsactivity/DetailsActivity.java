package com.amg.scoop.detailsactivity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.amg.scoop.R;


public class DetailsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        DetailsActivityFragment detailsActivityFragment = new DetailsActivityFragment();
        detailsActivityFragment.setArguments(getIntent().getExtras());
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, detailsActivityFragment)
                    .commit();
    }
}
