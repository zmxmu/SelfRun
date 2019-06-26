package com.example.zhengmin.maidian;

import com.example.zhengmin.maidian.model.Movies;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by zhengmin on 2018/3/13.
 */

public interface ApiService {
    @GET("v2/movie/coming_soon")
    Call<Movies> requestComingSoonMovies();
}
