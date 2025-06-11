package com.example.kicklog;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface FootballApiService {
    @Headers("X-Auth-Token: c0e6fd2b26a5420890e93b4ed51ecb71")
    @GET("v2/teams/{id}")
    Call<TeamResponse> getTeam(@Path("id") int teamId);
}
