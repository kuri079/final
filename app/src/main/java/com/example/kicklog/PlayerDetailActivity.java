package com.example.kicklog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kicklog.model.Player;
import com.example.kicklog.model.Scorer;
import com.example.kicklog.model.ScorersResponse;
import com.example.kicklog.model.Standing;
import com.example.kicklog.model.StandingsResponse;
import com.example.kicklog.model.TableEntry;
import com.example.kicklog.model.TeamDetailResponse;
import com.example.kicklog.network.ApiClient;
import com.example.kicklog.network.FootballDataApiService;

import com.example.kicklog.model.GeminiRequest;
import com.example.kicklog.model.GeminiResponse;
import com.example.kicklog.network.GeminiApiClient;
import com.example.kicklog.network.GeminiApiService;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// ★★★ ここからインターフェースの実装を削除 ★★★
public class PlayerDetailActivity extends AppCompatActivity { // 'implements PlayerAdapter.OnPlayerClickListener' を削除

    public static final String EXTRA_PLAYER_ID = "extra_player_id";
    public static final String EXTRA_TEAM_ID = "extra_team_id";
    public static final String EXTRA_LEAGUE_ID = "extra_league_id";
    private static final String TAG = "PlayerDetailActivity";

    private int playerId;
    private int teamId;
    private String leagueId;

    private TextView textViewPlayerNameHeader;
    private TextView textViewTeamName;
    private TextView textViewNationality;
    private TextView textViewDateOfBirth;
    private TextView textViewPlayerPosition;
    private TextView textViewPlayerShirtNumber;
    private TextView textViewSeasonGoals;
    private TextView textViewPlayerAIAnalysis;
    private ProgressBar progressBarPlayerAI;
    private Button buttonWikipedia;

    private TableEntry teamSeasonStats = null;
    private Player currentPlayerDetails = null;
    private String currentTeamName = null;

    private AtomicInteger pendingApiCalls = new AtomicInteger(0); // API呼び出しの完了を追跡

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("選手情報");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        playerId = getIntent().getIntExtra(EXTRA_PLAYER_ID, -1);
        teamId = getIntent().getIntExtra(EXTRA_TEAM_ID, -1);
        leagueId = getIntent().getStringExtra(EXTRA_LEAGUE_ID);

        initializeViews();

        if (playerId != -1 && teamId != -1 && leagueId != null && !leagueId.isEmpty()) {
            pendingApiCalls.set(3); // 3つのAPI呼び出しを期待
            fetchPlayerDetails(playerId);
            fetchSeasonGoals(leagueId, teamId);
            fetchTeamSeasonStats(leagueId, teamId);
        } else {
            Toast.makeText(this, "選手情報の取得に必要なデータが不足しています", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Missing player, team or league ID. PlayerId: " + playerId + ", TeamId: " + teamId + ", LeagueId: " + leagueId);
            textViewPlayerAIAnalysis.setText("AI分析に失敗 (情報不足)。");
            progressBarPlayerAI.setVisibility(View.GONE);
        }
    }

    private void initializeViews() {
        textViewPlayerNameHeader = findViewById(R.id.textViewPlayerNameHeader);
        textViewTeamName = findViewById(R.id.textViewTeamName);
        textViewNationality = findViewById(R.id.textViewNationality);
        textViewDateOfBirth = findViewById(R.id.textViewDateOfBirth);
        textViewPlayerPosition = findViewById(R.id.textViewPlayerPosition);
        textViewPlayerShirtNumber = findViewById(R.id.textViewPlayerShirtNumber);
        textViewSeasonGoals = findViewById(R.id.textViewSeasonGoals);
        textViewPlayerAIAnalysis = findViewById(R.id.textViewPlayerAIAnalysis);
        progressBarPlayerAI = findViewById(R.id.progressBarPlayerAI);
        buttonWikipedia = findViewById(R.id.buttonWikipedia);

        textViewPlayerAIAnalysis.setText("AIが分析中です...");
        progressBarPlayerAI.setVisibility(View.VISIBLE);
    }

    private void fetchPlayerDetails(int playerId) {
        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);
        // TeamDetailResponseから選手リストを取得する
        Call<TeamDetailResponse> call = service.getTeamDetails(teamId, BuildConfig.API_KEY);

        call.enqueue(new Callback<TeamDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<TeamDetailResponse> call, @NonNull Response<TeamDetailResponse> response) {
                if (isFinishing()) return;
                if (response.isSuccessful() && response.body() != null) {
                    Player player = findPlayerInSquad(response.body().getSquad(), playerId);
                    if (player != null) {
                        currentPlayerDetails = player;
                        currentTeamName = response.body().getName();
                        updatePlayerUI(player, response.body().getName());
                    } else {
                        Toast.makeText(PlayerDetailActivity.this, "選手が見つかりません", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Player with ID " + playerId + " not found in team squad.");
                    }
                } else {
                    Toast.makeText(PlayerDetailActivity.this, "選手データの取得に失敗", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to fetch team details for player. Code: " + response.code());
                }
                onApiCallComplete(); // API呼び出し完了
            }

            @Override
            public void onFailure(@NonNull Call<TeamDetailResponse> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                Toast.makeText(PlayerDetailActivity.this, "通信エラー", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error fetching player details.", t);
                onApiCallComplete(); // API呼び出し完了
            }
        });
    }

    private void updatePlayerUI(Player player, String teamName) {
        textViewPlayerNameHeader.setText(player.getName());
        textViewTeamName.setText(teamName);
        // nullチェックを追加し、値がない場合は「不明」と表示
        textViewNationality.setText("国籍: " + (player.getNationality() != null ? player.getNationality() : "不明"));
        textViewDateOfBirth.setText("誕生日: " + (player.getDateOfBirth() != null ? formatDate(player.getDateOfBirth()) : "不明"));
        textViewPlayerPosition.setText("ポジション: " + (player.getPosition() != null ? player.getPosition() : "不明"));
        textViewPlayerShirtNumber.setText("背番号: " + (player.getShirtNumber() != null ? String.valueOf(player.getShirtNumber()) : "不明"));

        buttonWikipedia.setOnClickListener(v -> {
            String url = "https://ja.wikipedia.org/wiki/" + player.getName().replace(" ", "_");
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
    }

    private Player findPlayerInSquad(List<Player> squad, int playerId) {
        if (squad == null) return null;
        for (Player player : squad) {
            if (player.getId() == playerId) {
                return player;
            }
        }
        return null;
    }

    private void fetchSeasonGoals(String leagueId, int teamId) {
        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);
        final int season = Calendar.getInstance().get(Calendar.YEAR);
        Log.d(TAG, "Fetching scorers for League ID: " + leagueId + ", Team ID: " + teamId + ", Season: " + season);

        service.getScorers(leagueId, BuildConfig.API_KEY, season, 100)
                .enqueue(new Callback<ScorersResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ScorersResponse> call, @NonNull Response<ScorersResponse> response) {
                        if (isFinishing()) return;
                        if (response.isSuccessful() && response.body() != null && response.body().getScorers() != null) {
                            int playerGoals = 0;
                            for (Scorer scorer : response.body().getScorers()) {
                                // Scorer.getGoals() が Integer なので null チェックが必要
                                if (scorer.getPlayer() != null && scorer.getPlayer().getId() == playerId && scorer.getGoals() != null) {
                                    playerGoals = scorer.getGoals();
                                    break;
                                }
                            }
                            textViewSeasonGoals.setText("リーグ戦ゴール数: " + playerGoals + "点");
                        } else {
                            Log.e(TAG, "Failed to fetch scorers. Code: " + response.code() + ", Message: " + response.message());
                            textViewSeasonGoals.setText("リーグ戦ゴール数: データなし");
                        }
                        onApiCallComplete();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ScorersResponse> call, @NonNull Throwable t) {
                        if (isFinishing()) return;
                        Log.e(TAG, "Network error fetching scorers.", t);
                        textViewSeasonGoals.setText("リーグ戦ゴール数: 通信エラー");
                        onApiCallComplete();
                    }
                });
    }

    private void fetchTeamSeasonStats(String leagueId, int teamId) {
        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);
        final int season = Calendar.getInstance().get(Calendar.YEAR);

        // ここで getStandings を呼び出す。StandingsResponseにはSquadは含まれない。
        Call<StandingsResponse> call = service.getStandings(leagueId, BuildConfig.API_KEY, season);

        call.enqueue(new Callback<StandingsResponse>() {
            @Override
            public void onResponse(@NonNull Call<StandingsResponse> call, @NonNull Response<StandingsResponse> response) {
                if (isFinishing()) return;
                if (response.isSuccessful() && response.body() != null) {
                    teamSeasonStats = findTeamInStandings(response.body(), teamId); // 成績情報を保存
                } else {
                    Log.e(TAG, "Failed to fetch team standings. Code: " + response.code() + ", Message: " + response.message());
                    teamSeasonStats = null;
                }
                onApiCallComplete();
            }

            @Override
            public void onFailure(@NonNull Call<StandingsResponse> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                Log.e(TAG, "Network error fetching team standings.", t);
                teamSeasonStats = null;
                onApiCallComplete();
            }
        });
    }

    private TableEntry findTeamInStandings(StandingsResponse standings, int teamId) {
        if (standings == null || standings.getStandings() == null) return null;
        for (Standing standing : standings.getStandings()) {
            if ("TOTAL".equals(standing.getType())) {
                if (standing.getTable() != null) {
                    for (TableEntry entry : standing.getTable()) {
                        if (entry.getTeam() != null && entry.getTeam().getId() == teamId) {
                            return entry;
                        }
                    }
                }
            }
        }
        return null;
    }

    // 全てのAPI呼び出しが完了したらAI分析をトリガーする
    private void onApiCallComplete() {
        if (pendingApiCalls.decrementAndGet() == 0) { // 全てのAPI呼び出しが完了したら
            if (currentPlayerDetails != null && currentTeamName != null) {
                // シーズンゴール数も既にUIにセットされているはず
                fetchPlayerAIAnalysis(currentPlayerDetails, currentTeamName, teamSeasonStats);
            } else {
                textViewPlayerAIAnalysis.setText("AI分析に必要な選手情報が揃いませんでした。");
                progressBarPlayerAI.setVisibility(View.GONE);
            }
        }
    }

    private void fetchPlayerAIAnalysis(Player player, String teamName, TableEntry teamStats) {
        progressBarPlayerAI.setVisibility(View.VISIBLE);
        textViewPlayerAIAnalysis.setText("AIが分析中です...");

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("あなたはサッカー選手分析の専門家です。以下の選手とチームのデータに基づいて、");
        promptBuilder.append("この選手のプレーススタイル、強み、弱み、チームへの貢献度について、データを用いて500文字程度で分析してください。\n\n");
        promptBuilder.append("選手名: ").append(player.getName()).append("\n");
        promptBuilder.append("所属チーム: ").append(teamName).append("\n");
        promptBuilder.append("ポジション: ").append(player.getPosition() != null ? player.getPosition() : "不明").append("\n");
        promptBuilder.append("背番号: ").append(player.getShirtNumber() != null ? player.getShirtNumber() : "不明").append("\n");
        promptBuilder.append("国籍: ").append(player.getNationality() != null ? player.getNationality() : "不明").append("\n");

        String playerGoalsText = textViewSeasonGoals.getText().toString(); // UIから直接取得
        if (playerGoalsText.startsWith("リーグ戦ゴール数: ")) {
            playerGoalsText = playerGoalsText.replace("リーグ戦ゴール数: ", "").replace("点", "");
            if (!playerGoalsText.equals("データなし") && !playerGoalsText.equals("通信エラー") && !playerGoalsText.isEmpty()) {
                promptBuilder.append("リーグ戦ゴール数: ").append(playerGoalsText).append("点\n");
            }
        }


        // チーム成績情報をプロンプトに追加
        if (teamStats != null) {
            promptBuilder.append("チーム成績:\n");
            promptBuilder.append("- 順位: ").append(teamStats.getPosition()).append("位\n");
            promptBuilder.append("- 勝ち点: ").append(teamStats.getPoints()).append("\n");
            promptBuilder.append("- 試合数: ").append(teamStats.getPlayedGames()).append("\n");
            promptBuilder.append("- 勝利: ").append(teamStats.getWon()).append(", 引分: ").append(teamStats.getDraw()).append(", 敗戦: ").append(teamStats.getLost()).append("\n");
            promptBuilder.append("- 得失点差: ").append(teamStats.getGoalDifference()).append(" (").append(teamStats.getGoalsFor()).append("-").append(teamStats.getGoalsAgainst()).append(")\n");
        } else {
            promptBuilder.append("チーム成績: データなし\n");
        }
        promptBuilder.append("重要な注意: このAIはリアルタイムのデータにはアクセスできません。一般的なサッカー知識と提供された情報のみに基づいて分析を生成してください。");

        String prompt = promptBuilder.toString();

        GeminiApiService geminiService = GeminiApiClient.getClient().create(GeminiApiService.class);
        GeminiRequest request = new GeminiRequest(
                Collections.singletonList(
                        new GeminiRequest.Content(
                                Collections.singletonList(
                                        new GeminiRequest.Part(prompt)
                                )
                        )
                )
        );

        Call<GeminiResponse> call = geminiService.generateContent(BuildConfig.GEMINI_API_KEY, request);

        call.enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeminiResponse> call, @NonNull Response<GeminiResponse> response) {
                progressBarPlayerAI.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().getCandidates().isEmpty()) {
                    String aiText = response.body().getCandidates().get(0).getContent().getParts().get(0).getText();
                    textViewPlayerAIAnalysis.setText(aiText);
                    Log.d(TAG, "Player AI Analysis Response: " + aiText);
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing AI error body for player analysis", e);
                    Log.e(TAG, "Failed to fetch team standings. Code: " + response.code() + ", Message: " + response.message());
                    teamSeasonStats = null;
                }
                onApiCallComplete();
            }

            @Override
            public void onFailure(@NonNull Call<StandingsResponse> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                Log.e(TAG, "Network error fetching team standings.", t);
                teamSeasonStats = null;
                onApiCallComplete();
            }
        });
    }

    private TableEntry findTeamInStandings(StandingsResponse standings, int teamId) {
        if (standings == null || standings.getStandings() == null) return null;
        for (Standing standing : standings.getStandings()) {
            if ("TOTAL".equals(standing.getType())) {
                if (standing.getTable() != null) {
                    for (TableEntry entry : standing.getTable()) {
                        if (entry.getTeam() != null && entry.getTeam().getId() == teamId) {
                            return entry;
                        }
                    }
                }
            }
        }
        return null;
    }

    // 全てのAPI呼び出しが完了したらAI分析をトリガーする
    private void onApiCallComplete() {
        if (pendingApiCalls.decrementAndGet() == 0) { // 全てのAPI呼び出しが完了したら
            if (currentPlayerDetails != null && currentTeamName != null) {
                // シーズンゴール数も既にUIにセットされているはず
                fetchPlayerAIAnalysis(currentPlayerDetails, currentTeamName, teamSeasonStats);
            } else {
                textViewPlayerAIAnalysis.setText("AI分析に必要な選手情報が揃いませんでした。");
                progressBarPlayerAI.setVisibility(View.GONE);
            }
        }
    }

    private void fetchPlayerAIAnalysis(Player player, String teamName, TableEntry teamStats) {
        progressBarPlayerAI.setVisibility(View.VISIBLE);
        textViewPlayerAIAnalysis.setText("AIが分析中です...");

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("あなたはサッカー選手分析の専門家です。以下の選手とチームのデータに基づいて、");
        promptBuilder.append("この選手のプレーススタイル、強み、弱み、チームへの貢献度について、150文字程度で簡潔に分析してください。\n\n");
        promptBuilder.append("選手名: ").append(player.getName()).append("\n");
        promptBuilder.append("所属チーム: ").append(teamName).append("\n");
        promptBuilder.append("ポジション: ").append(player.getPosition() != null ? player.getPosition() : "不明").append("\n");
        promptBuilder.append("背番号: ").append(player.getShirtNumber() != null ? player.getShirtNumber() : "不明").append("\n");
        promptBuilder.append("国籍: ").append(player.getNationality() != null ? player.getNationality() : "不明").append("\n");

        String playerGoalsText = textViewSeasonGoals.getText().toString(); // UIから直接取得
        if (playerGoalsText.startsWith("リーグ戦ゴール数: ")) {
            playerGoalsText = playerGoalsText.replace("リーグ戦ゴール数: ", "").replace("点", "");
            if (!playerGoalsText.equals("データなし") && !playerGoalsText.equals("通信エラー") && !playerGoalsText.isEmpty()) {
                promptBuilder.append("リーグ戦ゴール数: ").append(playerGoalsText).append("点\n");
            }
        }


        // チーム成績情報をプロンプトに追加
        if (teamStats != null) {
            promptBuilder.append("チーム成績:\n");
            promptBuilder.append("- 順位: ").append(teamStats.getPosition()).append("位\n");
            promptBuilder.append("- 勝ち点: ").append(teamStats.getPoints()).append("\n");
            promptBuilder.append("- 試合数: ").append(teamStats.getPlayedGames()).append("\n");
            promptBuilder.append("- 勝利: ").append(teamStats.getWon()).append(", 引分: ").append(teamStats.getDraw()).append(", 敗戦: ").append(teamStats.getLost()).append("\n");
            promptBuilder.append("- 得失点差: ").append(teamStats.getGoalDifference()).append(" (").append(teamStats.getGoalsFor()).append("-").append(teamStats.getGoalsAgainst()).append(")\n");
        } else {
            promptBuilder.append("チーム成績: データなし\n");
        }
        promptBuilder.append("重要な注意: このAIはリアルタイムのデータにはアクセスできません。一般的なサッカー知識と提供された情報のみに基づいて分析を生成してください。");

        String prompt = promptBuilder.toString();

        GeminiApiService geminiService = GeminiApiClient.getClient().create(GeminiApiService.class);
        GeminiRequest request = new GeminiRequest(
                Collections.singletonList(
                        new GeminiRequest.Content(
                                Collections.singletonList(
                                        new GeminiRequest.Part(prompt)
                                )
                        )
                )
        );

        Call<GeminiResponse> call = geminiService.generateContent(BuildConfig.GEMINI_API_KEY, request);

        call.enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeminiResponse> call, @NonNull Response<GeminiResponse> response) {
                progressBarPlayerAI.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().getCandidates().isEmpty()) {
                    String aiText = response.body().getCandidates().get(0).getContent().getParts().get(0).getText();
                    textViewPlayerAIAnalysis.setText(aiText);
                    Log.d(TAG, "Player AI Analysis Response: " + aiText);
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing AI error body for player analysis", e);
                    }
                    Log.e(TAG, "Failed to get player AI analysis: " + response.code() + " - " + errorBody);
                    textViewPlayerAIAnalysis.setText("AI分析の取得に失敗しました (APIエラー: " + response.code() + ")。");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeminiResponse> call, @NonNull Throwable t) {
                progressBarPlayerAI.setVisibility(View.GONE);
                Log.e(TAG, "Player AI analysis network error", t);
                textViewPlayerAIAnalysis.setText("AI分析の取得に失敗しました (通信エラー)。");
            }
        });
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return "不明";
        try {
            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = apiFormat.parse(dateString);
            SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.JAPAN);
            return displayFormat.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "Date parsing error: " + dateString, e);
            return dateString;
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}