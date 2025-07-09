package com.example.kicklog.network;

import com.example.kicklog.model.PlayerDetailResponse;
import com.example.kicklog.model.ScorersResponse;
import com.example.kicklog.model.StandingsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FootballDataService {
    @GET("v4/competitions/{competitionId}/scorers")
    Call<ScorersResponse> getTopScorers(
            @Path("competitionId") String competitionId,
            @Header("X-Auth-Token") String apiKey,
            @Query("limit") int limit
    );

    @GET("v4/persons/{id}")
    Call<PlayerDetailResponse> getPlayerDetails(
            @Path("id") int id,
            @Header("X-Auth-Token") String apiKey
    );

}