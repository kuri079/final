package com.example.kicklog;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.kicklog.model.Booking;
import com.example.kicklog.model.FullTimeScore;
import com.example.kicklog.model.Goal;
import com.example.kicklog.model.MatchDetailResponse;
import com.example.kicklog.model.Player;
import com.example.kicklog.model.Score;
import com.example.kicklog.model.Scorer; // 追加
import com.example.kicklog.model.ScorersResponse; // 追加
import com.example.kicklog.model.Standing;
import com.example.kicklog.model.StandingsResponse;
import com.example.kicklog.model.TableEntry;
import com.example.kicklog.network.ApiClient;
import com.example.kicklog.network.FootballDataApiService;

import com.example.kicklog.model.GeminiRequest;
import com.example.kicklog.model.GeminiResponse;
import com.example.kicklog.network.GeminiApiClient;
import com.example.kicklog.network.GeminiApiService;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors; // 追加

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchDetailActivity extends AppCompatActivity {
    public static final String EXTRA_MATCH_ID = "extra_match_id";
    public static final String EXTRA_LEAGUE_ID = "extra_league_id";
    private static final String TAG = "MatchDetailActivity";

    private ProgressBar progressBar;
    private ScrollView matchResultContainer;
    private ScrollView matchPreviewScrollView;
    private LinearLayout matchPreviewInnerLayout;

    // 結果表示用のUI部品
    private ImageView imageViewHomeCrest, imageViewAwayCrest;
    private TextView textViewHomeTeamName, textViewAwayTeamName, textViewFinalScore,
            textViewMatchDateDetail, textViewHomeLineup, textViewAwayLineup, textViewGoals, textViewBookings;
    private CardView cardViewGoals, cardViewBookings;

    // プレビュー表示用のUI部品
    private ImageView imageViewHomeCrest_preview, imageViewAwayCrest_preview;
    private TextView textViewHomeTeamName_preview, textViewAwayTeamName_preview,
            textViewMatchDatePreview;
    private TextView textViewHomeBasicStats, textViewHomeMatchStats;
    private TextView textViewAwayBasicStats, textViewAwayMatchStats;

    // AI予想関連のUI部品
    private TextView textViewAIAnalysis;
    private ProgressBar progressBarAI;

    // 得点ランキング関連のUI部品
    private TextView textViewHomeTopScorers; // 追加
    private TextView textViewAwayTopScorers; // 追加

    private String leagueId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);
        initializeViews();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("試合情報");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int matchId = getIntent().getIntExtra(EXTRA_MATCH_ID, -1);
        leagueId = getIntent().getStringExtra(EXTRA_LEAGUE_ID);
        Log.d(TAG, "Received Match ID: " + matchId + ", League ID: " + leagueId);

        if (matchId != -1) {
            fetchMatchDetails(matchId);
        } else {
            Toast.makeText(this, "試合IDがありません", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // WindowLeakedエラー対策
        View decorView = getWindow().getDecorView();
        if (decorView != null) {
            decorView.removeCallbacks(null);
            decorView.clearFocus();
        }
    }


    private void initializeViews() {
        progressBar = findViewById(R.id.progressBarDetail);

        matchResultContainer = findViewById(R.id.matchResultContainer);

        matchPreviewScrollView = findViewById(R.id.matchPreviewScrollView);
        if (matchPreviewScrollView != null) {
            matchPreviewInnerLayout = matchPreviewScrollView.findViewById(R.id.matchPreviewContainer);
        } else {
            Log.e(TAG, "matchPreviewScrollView is null. Check XML layout.");
            matchPreviewInnerLayout = findViewById(R.id.matchPreviewContainer); // フォールバック
        }


        // 結果用UI部品の初期化
        imageViewHomeCrest = findViewById(R.id.imageViewHomeCrest);
        imageViewAwayCrest = findViewById(R.id.imageViewAwayCrest);
        textViewHomeTeamName = findViewById(R.id.textViewHomeTeamName);
        textViewAwayTeamName = findViewById(R.id.textViewAwayTeamName);
        textViewFinalScore = findViewById(R.id.textViewFinalScore);
        textViewMatchDateDetail = findViewById(R.id.textViewMatchDateDetail);
        textViewHomeLineup = findViewById(R.id.textViewHomeLineup);
        textViewAwayLineup = findViewById(R.id.textViewAwayLineup);
        cardViewGoals = findViewById(R.id.cardViewGoals);
        textViewGoals = findViewById(R.id.textViewGoals);
        cardViewBookings = findViewById(R.id.cardViewBookings);
        textViewBookings = findViewById(R.id.textViewBookings);

        // プレビュー用UI部品の初期化
        imageViewHomeCrest_preview = findViewById(R.id.imageViewHomeCrest_preview);
        imageViewAwayCrest_preview = findViewById(R.id.imageViewAwayCrest_preview);
        textViewHomeTeamName_preview = findViewById(R.id.textViewHomeTeamName_preview);
        textViewAwayTeamName_preview = findViewById(R.id.textViewAwayTeamName_preview);
        textViewMatchDatePreview = findViewById(R.id.textViewMatchDatePreview);
        textViewHomeBasicStats = findViewById(R.id.textViewHomeBasicStats);
        textViewHomeMatchStats = findViewById(R.id.textViewHomeMatchStats);
        textViewAwayBasicStats = findViewById(R.id.textViewAwayBasicStats);
        textViewAwayMatchStats = findViewById(R.id.textViewAwayMatchStats);

        // AI予想関連のUI部品を初期化
        textViewAIAnalysis = findViewById(R.id.textViewAIAnalysis);
        progressBarAI = findViewById(R.id.progressBarAI);

        // 得点ランキング関連のUI部品を初期化
        textViewHomeTopScorers = findViewById(R.id.textViewHomeTopScorers); // 追加
        textViewAwayTopScorers = findViewById(R.id.textViewAwayTopScorers); // 追加
    }

    private void fetchMatchDetails(int matchId) {
        progressBar.setVisibility(View.VISIBLE);
        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);
        Call<MatchDetailResponse> call = service.getMatchDetails(matchId, BuildConfig.API_KEY);

        call.enqueue(new Callback<MatchDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<MatchDetailResponse> call, @NonNull Response<MatchDetailResponse> response) {
                if (isFinishing()) return;

                if (response.isSuccessful() && response.body() != null) {
                    MatchDetailResponse details = response.body();
                    if (details.getStatus() != null && (details.getStatus().equals("SCHEDULED") || details.getStatus().equals("TIMED"))) {
                        progressBar.setVisibility(View.GONE);
                        matchPreviewScrollView.setVisibility(View.VISIBLE);
                        updatePreviewUI(details);
                        fetchAIAnalysis(details); // AI予想を呼び出す
                    } else {
                        progressBar.setVisibility(View.GONE);
                        matchResultContainer.setVisibility(View.VISIBLE);
                        matchPreviewScrollView.setVisibility(View.GONE);
                        updateFinishedMatchUI(details);
                        textViewAIAnalysis.setText("この試合は終了しました。AI予想は行われません。");
                        progressBarAI.setVisibility(View.GONE);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                    Log.e(TAG, "Failed to fetch match details: " + response.code() + " - " + errorBody);
                    Toast.makeText(MatchDetailActivity.this, "詳細データの取得に失敗: " + response.code(), Toast.LENGTH_SHORT).show();
                    matchPreviewScrollView.setVisibility(View.GONE);
                    matchResultContainer.setVisibility(View.GONE);
                    textViewAIAnalysis.setText("AI予想の取得に失敗しました (試合情報取得エラー)。");
                    progressBarAI.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MatchDetailResponse> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MatchDetailActivity.this, "通信エラー", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to fetch match details due to network error", t);
                matchPreviewScrollView.setVisibility(View.GONE);
                matchResultContainer.setVisibility(View.GONE);
                textViewAIAnalysis.setText("AI予想の取得に失敗しました (試合情報通信エラー)。");
                progressBarAI.setVisibility(View.GONE);
            }
        });
    }

    private void updateFinishedMatchUI(MatchDetailResponse details) {
        matchResultContainer.setVisibility(View.VISIBLE);
        matchPreviewScrollView.setVisibility(View.GONE);

        if(details.getHomeTeam() != null) {
            textViewHomeTeamName.setText(details.getHomeTeam().getName());
            Glide.with(this).load(details.getHomeTeam().getCrest()).into(imageViewHomeCrest);
        } else {
            textViewHomeTeamName.setText("不明なチーム");
        }
        if(details.getAwayTeam() != null) {
            textViewAwayTeamName.setText(details.getAwayTeam().getName());
            Glide.with(this).load(details.getAwayTeam().getCrest()).into(imageViewAwayCrest);
        } else {
            textViewAwayTeamName.setText("不明なチーム");
        }

        textViewMatchDateDetail.setText(formatDate(details.getUtcDate()));

        Score score = details.getScore();
        if (score != null && score.getFullTime() != null) {
            FullTimeScore fullTime = score.getFullTime();
            if(fullTime.getHome() != null && fullTime.getAway() != null) {
                textViewFinalScore.setText(fullTime.getHome() + " - " + fullTime.getAway());
            } else {
                textViewFinalScore.setText(" - ");
            }
        } else {
            textViewFinalScore.setText(" - ");
        }

        if (details.getHomeTeam() != null && details.getHomeTeam().getLineup() != null) {
            textViewHomeLineup.setText(formatPlayerList(details.getHomeTeam().getLineup()));
        } else {
            textViewHomeLineup.setText("ラインナップ情報なし");
        }
        if (details.getAwayTeam() != null && details.getAwayTeam().getLineup() != null) {
            textViewAwayLineup.setText(formatPlayerList(details.getAwayTeam().getLineup()));
        } else {
            textViewAwayLineup.setText("ラインナップ情報なし");
        }

        if (details.getGoals() != null && !details.getGoals().isEmpty()) {
            cardViewGoals.setVisibility(View.VISIBLE);
            StringBuilder goalsStr = new StringBuilder();
            for (Goal goal : details.getGoals()) {
                if(goal.getScorer() != null) {
                    goalsStr.append(goal.getMinute()).append("' ")
                            .append(goal.getScorer().getName());
                    if (goal.getTeam() != null && goal.getTeam().getName() != null) {
                        goalsStr.append(" (").append(goal.getTeam().getName()).append(")");
                    }
                    goalsStr.append("\n");
                }
            }
            textViewGoals.setText(goalsStr.toString().trim());
        } else {
            cardViewGoals.setVisibility(View.GONE);
        }

        if (details.getBookings() != null && !details.getBookings().isEmpty()) {
            cardViewBookings.setVisibility(View.VISIBLE);
            StringBuilder bookingsStr = new StringBuilder();
            for (Booking booking : details.getBookings()) {
                if(booking.getPlayer() != null) {
                    String cardEmoji = "";
                    if ("YELLOW_CARD".equals(booking.getCard())) {
                        cardEmoji = "🟨";
                    } else if ("RED_CARD".equals(booking.getCard())) {
                        cardEmoji = "🟥";
                    } else if ("YELLOW_RED_CARD".equals(booking.getCard())) {
                        cardEmoji = "🟨🟥";
                    }
                    bookingsStr.append(cardEmoji).append(" ")
                            .append(booking.getMinute()).append("' ")
                            .append(booking.getPlayer().getName());
                    if (booking.getTeam() != null && booking.getTeam().getName() != null) {
                        bookingsStr.append(" (").append(booking.getTeam().getName()).append(")");
                    }
                    bookingsStr.append("\n");
                }
            }
            textViewBookings.setText(bookingsStr.toString().trim());
        } else {
            cardViewBookings.setVisibility(View.GONE);
        }
    }


    private void updatePreviewUI(MatchDetailResponse details) {
        matchPreviewScrollView.setVisibility(View.VISIBLE);
        matchResultContainer.setVisibility(View.GONE);

        if(details.getHomeTeam() != null) {
            textViewHomeTeamName_preview.setText(details.getHomeTeam().getName());
            Glide.with(this).load(details.getHomeTeam().getCrest()).into(imageViewHomeCrest_preview);
        } else {
            textViewHomeTeamName_preview.setText("不明なチーム");
        }
        if(details.getAwayTeam() != null) {
            textViewAwayTeamName_preview.setText(details.getAwayTeam().getName());
            Glide.with(this).load(details.getAwayTeam().getCrest()).into(imageViewAwayCrest_preview);
        } else {
            textViewAwayTeamName_preview.setText("不明なチーム");
        }
        textViewMatchDatePreview.setText(formatDate(details.getUtcDate()));

        if (leagueId == null || leagueId.isEmpty()) {
            Log.e(TAG, "League ID is null or empty. Cannot fetch standings.");
            progressBar.setVisibility(View.GONE); // メインのプログレスバーを隠す
            textViewHomeBasicStats.setText("今季の成績データなし ");
            textViewHomeMatchStats.setText("");
            textViewAwayBasicStats.setText("今季の成績データなし ");
            textViewAwayMatchStats.setText("");
            textViewHomeTopScorers.setText("得点ランキングデータなし "); // 得点ランキングもエラー表示
            textViewAwayTopScorers.setText("得点ランキングデータなし"); // 得点ランキングもエラー表示
            textViewAIAnalysis.setText("AI予想の生成に失敗しました ");
            progressBarAI.setVisibility(View.GONE);
            return;
        }

        // チーム成績と得点ランキングは2025-2026シーズン（APIでは2025）で固定
        final int season = 2025;
        Log.d(TAG, "Fetching standings and scorers for League ID: " + leagueId + ", Season: " + season + " (Fixed to 2025 for 2025-2026 season)");

        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);

        // 順位表の取得
        service.getStandings(leagueId, BuildConfig.API_KEY, season).enqueue(new Callback<StandingsResponse>() {
            @Override
            public void onResponse(@NonNull Call<StandingsResponse> call, @NonNull Response<StandingsResponse> response) {
                if(isFinishing()) return;
                // progressBar.setVisibility(View.GONE); // ここではまだ隠さない

                if (response.isSuccessful() && response.body() != null) {
                    TableEntry homeTeamEntry = findTeamInStandings(response.body(), details.getHomeTeam().getId());
                    TableEntry awayTeamEntry = findTeamInStandings(response.body(), details.getAwayTeam().getId());

                    if (homeTeamEntry != null) {
                        textViewHomeBasicStats.setText(
                                "順位: " + homeTeamEntry.getPosition() + "位\n" +
                                        "勝ち点: " + homeTeamEntry.getPoints()
                        );
                        textViewHomeMatchStats.setText(
                                "試合数: " + homeTeamEntry.getPlayedGames() + "\n" +
                                        "勝利: " + homeTeamEntry.getWon() + "\n" +
                                        "引分: " + homeTeamEntry.getDraw() + "\n" +
                                        "敗戦: " + homeTeamEntry.getLost() + "\n" +
                                        "得失点差: " + (homeTeamEntry.getGoalDifference() != null ? (homeTeamEntry.getGoalDifference() >= 0 ? "+" : "") + homeTeamEntry.getGoalDifference() : "N/A") +
                                        " (" + homeTeamEntry.getGoalsFor() + "-" + homeTeamEntry.getGoalsAgainst() + ")"
                        );
                    } else {
                        textViewHomeBasicStats.setText("今季の成績データなし ");
                        textViewHomeMatchStats.setText("");
                    }

                    if (awayTeamEntry != null) {
                        textViewAwayBasicStats.setText(
                                "順位: " + awayTeamEntry.getPosition() + "位\n" +
                                        "勝ち点: " + awayTeamEntry.getPoints()
                        );
                        textViewAwayMatchStats.setText(
                                "試合数: " + awayTeamEntry.getPlayedGames() + "\n" +
                                        "勝利: " + awayTeamEntry.getWon() + "\n" +
                                        "引分: " + awayTeamEntry.getDraw() + "\n" +
                                        "敗戦: " + awayTeamEntry.getLost() + "\n" +
                                        "得失点差: " + (awayTeamEntry.getGoalDifference() != null ? (awayTeamEntry.getGoalDifference() >= 0 ? "+" : "") + awayTeamEntry.getGoalDifference() : "N/A") +
                                        " (" + awayTeamEntry.getGoalsFor() + "-" + awayTeamEntry.getGoalsAgainst() + ")"
                        );
                    } else {
                        textViewAwayBasicStats.setText("今季の成績データなし (順位データなし)");
                        textViewAwayMatchStats.setText("");
                    }

                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing standings error body", e);
                    }
                    Log.e(TAG, "Failed to fetch standings: " + response.code() + " - " + errorBody);
                    textViewHomeBasicStats.setText("成績データ取得失敗 ");
                    textViewHomeMatchStats.setText("");
                    textViewAwayBasicStats.setText("成績データ取得失敗 ");
                    textViewAwayMatchStats.setText("");
                }
            }
            @Override
            public void onFailure(@NonNull Call<StandingsResponse> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                Log.e(TAG, "Error fetching standings for preview due to network error", t);
                textViewHomeBasicStats.setText("通信エラー");
                textViewHomeMatchStats.setText("");
                textViewAwayBasicStats.setText("通信エラー");
                textViewAwayMatchStats.setText("");
            }
        });

        // 得点ランキングの取得
        service.getScorers(leagueId, BuildConfig.API_KEY, season, 100).enqueue(new Callback<ScorersResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScorersResponse> call, @NonNull Response<ScorersResponse> response) {
                if (isFinishing()) return;
                // progressBar.setVisibility(View.GONE); // ここではまだ隠さない

                if (response.isSuccessful() && response.body() != null) {
                    List<Scorer> scorers = response.body().getScorers();
                    updateTopScorersUI(scorers, details.getHomeTeam().getId(), details.getAwayTeam().getId());
                } else {
                    Log.e(TAG, "Failed to get scorers: " + response.code() + " - " + response.message());
                    textViewHomeTopScorers.setText("得点ランキングデータ取得失敗 (APIエラー)");
                    textViewAwayTopScorers.setText("得点ランキングデータ取得失敗 (APIエラー)");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ScorersResponse> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                Log.e(TAG, "Scorers network error", t);
                textViewHomeTopScorers.setText("得点ランキングデータ取得失敗 (通信エラー)");
                textViewAwayTopScorers.setText("得点ランキングデータ取得失敗 (通信エラー)");
            }
        });
    }

    // 得点ランキングのUI更新ヘルパーメソッド
    private void updateTopScorersUI(List<Scorer> scorers, int homeTeamId, int awayTeamId) {
        if (scorers == null || scorers.isEmpty()) {
            textViewHomeTopScorers.setText("データがありません");
            textViewAwayTopScorers.setText("データがありません");
            return;
        }

        // ホームチームの得点者
        List<Scorer> homeTeamScorers = scorers.stream()
                .filter(scorer -> scorer.getTeam() != null && scorer.getTeam().getId() == homeTeamId)
                .sorted((s1, s2) -> s2.getGoals().compareTo(s1.getGoals())) // ゴール数で降順ソート
                .collect(Collectors.toList());
        setScorersText(textViewHomeTopScorers, homeTeamScorers);

        // アウェイチームの得点者
        List<Scorer> awayTeamScorers = scorers.stream()
                .filter(scorer -> scorer.getTeam() != null && scorer.getTeam().getId() == awayTeamId)
                .sorted((s1, s2) -> s2.getGoals().compareTo(s1.getGoals())) // ゴール数で降順ソート
                .collect(Collectors.toList());
        setScorersText(textViewAwayTopScorers, awayTeamScorers);
    }

    private void setScorersText(TextView textView, List<Scorer> scorers) {
        if (scorers.isEmpty()) {
            textView.setText("データがありません");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < scorers.size() && i < 5; i++) { // 上位5名まで
            Scorer scorer = scorers.get(i);
            if (scorer.getPlayer() != null && scorer.getPlayer().getName() != null && scorer.getGoals() != null) {
                sb.append(i + 1).append(". ")
                        .append(scorer.getPlayer().getName())
                        .append(" (").append(scorer.getGoals()).append("点)\n");
            }
        }
        textView.setText(sb.toString().trim().isEmpty() ? "データがありません" : sb.toString().trim());
    }


    // AI予想を取得するメソッド
    private void fetchAIAnalysis(MatchDetailResponse matchDetails) {
        if (matchDetails == null || matchDetails.getHomeTeam() == null || matchDetails.getAwayTeam() == null) {
            textViewAIAnalysis.setText("試合情報が不足しているため、AI予想を生成できません。");
            progressBarAI.setVisibility(View.GONE);
            return;
        }

        progressBarAI.setVisibility(View.VISIBLE);
        textViewAIAnalysis.setText("AIが分析中です..."); // 読み込み中のメッセージ

        String homeTeamName = matchDetails.getHomeTeam().getName();
        String awayTeamName = matchDetails.getAwayTeam().getName();
        String matchDate = formatDate(matchDetails.getUtcDate()); // フォーマットされた日付

        // Geminiに送るプロンプトを作成
        String prompt = "あなたはサッカーの試合アナリストです。以下の試合について、簡易的な予想と見どころを50文字から100文字程度で簡潔に説明してください。\n\n" +
                "試合: " + homeTeamName + " vs " + awayTeamName + "\n" +
                "日時: " + matchDate + "\n" +
                "重要な注意: このAIはリアルタイムの試合データや現在のチーム状況にはアクセスできません。一般的なサッカーの知識と提供された情報のみに基づいて予想を生成してください。";


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
                progressBarAI.setVisibility(View.GONE); // 処理完了、プログレスバーを非表示
                if (response.isSuccessful() && response.body() != null && !response.body().getCandidates().isEmpty()) {
                    String aiText = response.body().getCandidates().get(0).getContent().getParts().get(0).getText();
                    textViewAIAnalysis.setText(aiText);
                    Log.d(TAG, "AI Response: " + aiText);
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing AI error body", e);
                    }
                    Log.e(TAG, "Failed to get AI analysis: " + response.code() + " - " + errorBody);
                    textViewAIAnalysis.setText("AI予想の取得に失敗しました (APIエラー: " + response.code() + ")。"); // エラーコードを表示
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeminiResponse> call, @NonNull Throwable t) {
                progressBarAI.setVisibility(View.GONE); // 通信エラー、プログレスバーを非表示
                Log.e(TAG, "AI analysis network error", t);
                textViewAIAnalysis.setText("AI予想の取得に失敗しました (通信エラー)。");
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

    private String formatPlayerList(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return "データなし";
        }
        StringBuilder sb = new StringBuilder();
        for (Player player : players) {
            String number = (player.getShirtNumber() != null) ? player.getShirtNumber() + ". " : "";
            sb.append(number).append(player.getName()).append("\n");
        }
        return sb.toString().trim();
    }

    private String formatDate(String utcDate) {
        if(utcDate == null || utcDate.isEmpty()) return "日時未定";
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = utcFormat.parse(utcDate);
            if(date == null) return utcDate;

            SimpleDateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPAN);
            localFormat.setTimeZone(TimeZone.getDefault());
            return localFormat.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "Date format parsing error for: " + utcDate, e);
            return utcDate;
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