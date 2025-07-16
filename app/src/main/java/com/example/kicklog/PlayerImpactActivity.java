package com.example.kicklog;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.kicklog.model.Match;
import com.example.kicklog.model.MatchListResponse;
import com.example.kicklog.model.Scorer;
import com.example.kicklog.model.ScorersResponse;
import com.example.kicklog.model.Standing;
import com.example.kicklog.model.StandingsResponse;
import com.example.kicklog.model.TableEntry;
import com.example.kicklog.network.ApiClient;
import com.example.kicklog.network.FootballDataApiService;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerImpactActivity extends AppCompatActivity {
    public static final String EXTRA_PLAYER_ID = "extra_player_id";
    public static final String EXTRA_PLAYER_NAME = "extra_player_name";
    public static final String EXTRA_TEAM_ID = "extra_team_id";
    public static final String EXTRA_LEAGUE_ID = "extra_league_id";

    public static final String EXTRA_PLAYER_POSITION = "extra_player_position";

    private static final String TAG = "PlayerImpactActivity";
    private CardView cardViewDefensiveStats; // ◀️ CardViewの変数を追加
    private TextView textViewCleanSheets, textViewGoalsConceded; // ◀️ TextViewの変数を追加
    private String playerPosition;
    private PieChart pieChart;
    private Spinner spinnerSeasons;
    private ProgressBar progressBar;

    private int playerId, teamId;
    private String playerName, leagueId;
    private final List<Integer> seasonYearList = new ArrayList<>();

    private int teamTotalGoals = -1;
    private int playerGoals = -1;
    private final AtomicInteger apiCallsDone = new AtomicInteger(0);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_impact);

        // Intentからデータを取得
        playerId = getIntent().getIntExtra(EXTRA_PLAYER_ID, -1);
        playerName = getIntent().getStringExtra(EXTRA_PLAYER_NAME);
        teamId = getIntent().getIntExtra(EXTRA_TEAM_ID, -1);
        leagueId = getIntent().getStringExtra(EXTRA_LEAGUE_ID); // ここでleagueIdを受け取る
        playerPosition = getIntent().getStringExtra(EXTRA_PLAYER_POSITION);

        initializeViews();

        // ▼▼▼ leagueIdがnullでないかチェックを追加 ▼▼▼
        if (leagueId == null) {
            Toast.makeText(this, "リーグ情報がありません。グラフを表示できません。", Toast.LENGTH_LONG).show();
            Log.e(TAG, "leagueId is null. Cannot fetch data.");
            // この画面でできることがないので閉じる
            finish();
            return; // これ以上進まない
        }
        // ▲▲▲ ここまで追加 ▲▲▲

        setupPieChart();
        setupSpinner();
    }
    private void initializeViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(playerName + " の得点インパクト");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        pieChart = findViewById(R.id.pieChartPlayerImpact);
        spinnerSeasons = findViewById(R.id.spinnerImpactSeason);
        progressBar = findViewById(R.id.progressBarImpact);
        cardViewDefensiveStats = findViewById(R.id.cardViewDefensiveStats);
        textViewCleanSheets = findViewById(R.id.textViewCleanSheets);
        textViewGoalsConceded = findViewById(R.id.textViewGoalsConceded);
    }

    private void setupPieChart() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setNoDataText("No chart data available");
    }

    private void setupSpinner() {
        List<String> seasonList = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 10; i++) {
            int seasonStartYear = currentYear - i - 1;
            seasonYearList.add(seasonStartYear);
            seasonList.add(seasonStartYear + "-" + (seasonStartYear + 1));
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, seasonList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeasons.setAdapter(spinnerAdapter);

        spinnerSeasons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedSeason = seasonYearList.get(position);
                fetchImpactData(selectedSeason);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchImpactData(int seasonYear) {
        Log.d(TAG, "Fetching data for season: " + seasonYear + " for position: " + playerPosition);
        progressBar.setVisibility(View.VISIBLE);

        // まずは両方の結果表示エリアを隠す
        pieChart.setVisibility(View.GONE);
        cardViewDefensiveStats.setVisibility(View.GONE);
        pieChart.clear(); // グラフをクリア

        // ポジションによって処理を分岐させる
        if ("Attacker".equals(playerPosition) || "Midfielder".equals(playerPosition)) {
            // 攻撃的な選手の場合、得点関連のデータを取得
            Log.d(TAG, "Fetching goal impact data...");
            teamTotalGoals = -1;
            playerGoals = -1;
            apiCallsDone.set(0); // 2つのAPI呼び出しを待つカウンターをリセット
            fetchStandings(seasonYear);
            fetchScorers(seasonYear);
        } else if ("Defender".equals(playerPosition) || "Goalkeeper".equals(playerPosition)) {
            // 守備的な選手の場合、守備データを取得
            Log.d(TAG, "Fetching defensive impact data...");
            fetchDefensiveStats(seasonYear);
        } else {
            // ポジション情報がない、または不明な場合
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "ポジション情報がないため表示できません", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Unknown or null player position: " + playerPosition);
        }
    }
    private void fetchStandings(int seasonYear) {
        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);
        Call<StandingsResponse> call = service.getStandings(leagueId, BuildConfig.API_KEY, seasonYear);
        call.enqueue(new Callback<StandingsResponse>() {
            @Override
            public void onResponse(@NonNull Call<StandingsResponse> call, @NonNull Response<StandingsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Standing standing : response.body().getStandings()) {
                        if ("TOTAL".equals(standing.getType())) {
                            for (TableEntry entry : standing.getTable()) {
                                if (entry.getTeam().getId() == teamId) {
                                    teamTotalGoals = entry.getGoalsFor();
                                    Log.d(TAG, "チーム総得点: " + teamTotalGoals);
                                    break;
                                }
                            }
                        }
                    }
                    if(teamTotalGoals == -1) Log.e(TAG, "チームが順位表に見つかりませんでした");
                } else {
                    Log.e(TAG, "順位表APIエラー: " + response.code());
                }
                onDataFetched();
            }
            @Override
            public void onFailure(@NonNull Call<StandingsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "順位表API通信失敗", t);
                onDataFetched();
            }
        });
    }

    private void fetchScorers(int seasonYear) {
        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);
        Call<ScorersResponse> call = service.getScorers(leagueId, BuildConfig.API_KEY, seasonYear, 100); // 取得件数を増やす
        call.enqueue(new Callback<ScorersResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScorersResponse> call, @NonNull Response<ScorersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Scorer scorer : response.body().getScorers()) {
                        if (scorer.getPlayer().getId() == playerId) {
                            playerGoals = scorer.getGoals();
                            Log.d(TAG, "選手の得点: " + playerGoals);
                            break;
                        }
                    }
                    if(playerGoals == -1) Log.e(TAG, "選手が得点ランキングに見つかりませんでした");
                } else {
                    Log.e(TAG, "得点者APIエラー: " + response.code());
                }
                onDataFetched();
            }
            @Override
            public void onFailure(@NonNull Call<ScorersResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "得点者API通信失敗", t);
                onDataFetched();
            }
        });
    }

// 場所: com/example/kicklog/PlayerImpactActivity.java

    private void onDataFetched() {
        if (apiCallsDone.incrementAndGet() < 2) {
            return;
        }

        progressBar.setVisibility(View.GONE);

        // 🔽 メッセージを具体的にする 🔽
        if (teamTotalGoals <= 0) {
            Toast.makeText(this, "チームの総得点データがありません", Toast.LENGTH_SHORT).show();
        } else if (playerGoals <= 0) {
            // 選手名を入れて、なぜグラフが作れないかを表示する
            String message = playerName + "選手は、このシーズンに得点がありませんでした。";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } else {
            // 両方のデータがある場合のみグラフを更新
            updateChart();
            return; // Toastを表示させずに終了
        }

        // データがない場合は、グラフを非表示にし、「データなし」のテキストを表示
        pieChart.clear();
        pieChart.setVisibility(View.VISIBLE);
    }

    private void updateChart() {
        pieChart.setVisibility(View.VISIBLE);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(playerGoals, playerName));
        entries.add(new PieEntry(teamTotalGoals - playerGoals, "他の選手"));

        PieDataSet dataSet = new PieDataSet(entries, "得点割合");
        dataSet.setSliceSpace(3f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(16f);
        dataSet.setValueTextColor(Color.BLACK);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart)); // %表示にする

        pieChart.setData(pieData);
        pieChart.setCenterText("総得点\n" + teamTotalGoals + "点");
        pieChart.animateY(1000); // アニメーション
        pieChart.invalidate();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    // 🔽 --- 守備スタッツを取得するメソッドを新規作成 --- 🔽
    private void fetchDefensiveStats(int seasonYear) {
        progressBar.setVisibility(View.VISIBLE);
        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);

        // ▼▼▼ この行に引数 seasonYear を追加 ▼▼▼
        Call<MatchListResponse> call = service.getMatchesForTeam(teamId, BuildConfig.API_KEY, seasonYear);

        call.enqueue(new Callback<MatchListResponse>() {
            @Override
            public void onResponse(Call<MatchListResponse> call, Response<MatchListResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    int cleanSheets = 0;
                    int goalsConceded = 0;

                    for(Match match : response.body().getMatches()) {
                        // ホームの場合
                        if(match.getHomeTeam().getId() == teamId) {
                            if(match.getScore().getFullTime().getAway() != null) {
                                goalsConceded += match.getScore().getFullTime().getAway();
                                if(match.getScore().getFullTime().getAway() == 0) {
                                    cleanSheets++;
                                }
                            }
                        } else { // アウェイの場合
                            if(match.getScore().getFullTime().getHome() != null) {
                                goalsConceded += match.getScore().getFullTime().getHome();
                                if(match.getScore().getFullTime().getHome() == 0) {
                                    cleanSheets++;
                                }
                            }
                        }
                    }
                    updateDefensiveUI(cleanSheets, goalsConceded);
                }
            }
            @Override
            public void onFailure(Call<MatchListResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // 🔽 --- 守備スタッツをUIに反映するメソッドを新規作成 --- 🔽
    private void updateDefensiveUI(int cleanSheets, int goalsConceded) {
        cardViewDefensiveStats.setVisibility(View.VISIBLE);
        textViewCleanSheets.setText("無失点試合: " + cleanSheets);
        textViewGoalsConceded.setText("総失点: " + goalsConceded);
    }
}
