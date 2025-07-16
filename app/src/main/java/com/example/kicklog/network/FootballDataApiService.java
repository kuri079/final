package com.example.kicklog.network;

import com.example.kicklog.model.MatchDetailResponse;
import com.example.kicklog.model.MatchListResponse;
import com.example.kicklog.model.PlayerDetailResponse;
import com.example.kicklog.model.ScorersResponse;
import com.example.kicklog.model.StandingsResponse;
import com.example.kicklog.model.TeamDetailResponse;
import com.example.kicklog.model.TeamsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FootballDataApiService {

    // リーグのチーム一覧取得
    @GET("v4/competitions/{competitionId}/teams")
    Call<TeamsResponse> getTeamsInCompetition(
            @Path("competitionId") String competitionId,
            @Header("X-Auth-Token") String apiKey
    );

    // リーグの得点ランキング取得
    @GET("v4/competitions/{competitionId}/scorers")
    Call<ScorersResponse> getScorers(
            @Path("competitionId") String competitionId,
            @Header("X-Auth-Token") String apiKey,
            @Query("season") int season,
            @Query("limit") int limit
    );

    // チーム詳細（選手リスト含む）取得
    @GET("v4/teams/{teamId}")
    Call<TeamDetailResponse> getTeamDetails(
            @Path("teamId") int teamId,
            @Header("X-Auth-Token") String apiKey
    );

    // 選手詳細取得
    @GET("v4/persons/{personId}")
    Call<PlayerDetailResponse> getPlayerDetails(
            @Path("personId") int personId,
            @Header("X-Auth-Token") String apiKey
    );

    // チームの試合一覧取得
    @GET("v4/teams/{teamId}/matches")
    Call<MatchListResponse> getMatchesForTeam(
            @Path("teamId") int teamId,
            @Header("X-Auth-Token") String apiKey,
            @Query("season") int season
    );

    // 試合詳細取得
    @GET("v4/matches/{matchId}")
    Call<MatchDetailResponse> getMatchDetails(
            @Path("matchId") int matchId,
            @Header("X-Auth-Token") String apiKey
    );

    // リーグの順位表取得
    @GET("v4/competitions/{competitionId}/standings")
    Call<StandingsResponse> getStandings(
            @Path("competitionId") String competitionId,
            @Header("X-Auth-Token") String apiKey,
            @Query("season") int season
    );
}