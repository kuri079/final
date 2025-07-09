// com/example/kicklog/network/FootballDataApiService.java を修正

package com.example.kicklog.network;

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

// クラス名が'FootballDataApiService'であることを確認してください
public interface FootballDataApiService {

    // 得点ランキング取得
    // 🔽 getScorersメソッドにシーズン指定を追加 🔽
    @GET("v4/competitions/{competitionId}/scorers")
    Call<ScorersResponse> getScorers(
            @Path("competitionId") String competitionId,
            @Header("X-Auth-Token") String apiKey,
            @Query("season") int season,
            @Query("limit") int limit
    );
    // 🔽 チーム詳細（選手リスト含む）取得を新規追加 🔽
    @GET("v4/teams/{teamId}")
    Call<TeamDetailResponse> getTeamDetails(
            @Path("teamId") int teamId,
            @Header("X-Auth-Token") String apiKey
    );
    // チーム一覧取得
    @GET("v4/competitions/{competitionId}/teams")
    Call<TeamsResponse> getTeamsInCompetition(
            @Path("competitionId") String competitionId,
            @Header("X-Auth-Token") String apiKey
    );

    // 選手詳細取得
    @GET("v4/persons/{personId}")
    Call<PlayerDetailResponse> getPlayerDetails(
            @Path("personId") int personId,
            @Header("X-Auth-Token") String apiKey
    );
    // 🔽 チームの試合一覧取得を新規追加 🔽
    @GET("v4/teams/{teamId}/matches")
    Call<MatchListResponse> getMatchesForTeam(
            @Path("teamId") int teamId,
            @Header("X-Auth-Token") String apiKey
    );
    // 🔽 順位表取得を新規追加 (Queryアノテーションも使用) 🔽
    @GET("v4/competitions/{competitionId}/standings")
    Call<StandingsResponse> getStandings(
            @Path("competitionId") String competitionId,
            @Header("X-Auth-Token") String apiKey,
            @Query("season") int season // シーズンを指定するためのクエリパラメータ
    );
}