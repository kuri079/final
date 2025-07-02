package com.example.kicklog.api;

import com.example.kicklog.model.MatchResponse;
import com.example.kicklog.model.PersonResponse;
import com.example.kicklog.model.TeamResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ApiService {

    @GET("v4/competitions/{code}/teams")
    Call<TeamResponse> getTeams(@Header("X-Auth-Token") String key,
                                @Path("code") String leagueCode);

    @GET("v4/teams/{id}")
    Call<TeamResponse> getTeamDetail(@Header("X-Auth-Token") String key,
                                     @Path("id") int teamId);

    @GET("v4/teams/{id}/matches?season=2024")
    Call<MatchResponse> getTeamMatches(@Header("X-Auth-Token") String key,
                                       @Path("id") int teamId);
    @GET("v4/persons/{id}")
    Call<PersonResponse> getPerson(
            @Header("X-Auth-Token") String key,
            @Path("id") int personId);

}
