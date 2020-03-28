package com.example.moviebuff.Api;

import com.example.moviebuff.DataModel.MovieResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/3/movie/{category}")
    Call<MovieResult> getMovies(@Path("category") String category,
                                @Query("api_key") String apiKey
    );

}
