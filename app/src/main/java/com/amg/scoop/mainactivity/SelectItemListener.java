package com.amg.scoop.mainactivity;


import com.amg.scoop.models.Movie;

public interface SelectItemListener {
    void onItemSelected(Movie movie);
    void onAsyncTask();
}
