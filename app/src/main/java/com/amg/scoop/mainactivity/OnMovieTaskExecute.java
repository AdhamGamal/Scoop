package com.amg.scoop.mainactivity;


import com.amg.scoop.models.Movie;

import java.util.List;

public interface OnMovieTaskExecute {
    void findFavorite(List<Movie> movies);
}
