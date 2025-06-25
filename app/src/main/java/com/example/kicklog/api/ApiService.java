package com.example.kicklog.api;

import com.example.kicklog.model.PlayerResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiService {
    @Headers({
            "x-apisports-key: c0e6fd2b26a5420890e93b4ed51ecb71"
    })
    @GET("players")
    Call<PlayerResponse> getPlayers(@Query("team") int teamId, @Query("season") int season);
}


