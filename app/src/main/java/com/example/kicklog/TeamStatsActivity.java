// 場所: com/example/kicklog/TeamStatsActivity.java
package com.example.kicklog;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kicklog.model.Scorer;
import com.example.kicklog.model.ScorersResponse;
import com.example.kicklog.model.Standing;
import com.example.kicklog.model.StandingsResponse;
import com.example.kicklog.model.TableEntry;
import com.example.kicklog.network.ApiClient;
import com.example.kicklog.network.FootballDataApiService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamStatsActivity extends AppCompatActivity {
    public static final String EXTRA_TEAM_ID = "extra_team_id";
    public static final String EXTRA_LEAGUE_ID = "extra_league_id";

    private Spinner spinnerSeasons;
    private ProgressBar progressBar;
    private LinearLayout statsContainer;
    private TextView textViewPosition, textViewPoints, textViewPlayedGames, textViewWon,
            textViewDraw, textViewLost, textViewGoalsFor, textViewGoalsAgainst,
            textViewGoalDifference, textViewHomeStats, textViewAwayStats, textViewTopScorers;

    private int teamId;
    private String leagueId;
    private final List<String> seasonList = new ArrayList<>();
    private final List<Integer> seasonYearList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_stats);

        teamId = getIntent().getIntExtra(EXTRA_TEAM_ID, -1);
        leagueId = getIntent().getStringExtra(EXTRA_LEAGUE_ID);

        initializeViews();
        setupSpinner();

        if (teamId == -1 || leagueId == null) {
            Toast.makeText(this, "情報が不足しています", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("チーム成績");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        spinnerSeasons = findViewById(R.id.spinnerSeasons);
        progressBar = findViewById(R.id.progressBarStats);
        statsContainer = findViewById(R.id.statsContainer);

        textViewPosition = findViewById(R.id.textViewPosition);
        textViewPoints = findViewById(R.id.textViewPoints);
        textViewPlayedGames = findViewById(R.id.textViewPlayedGames);
        textViewWon = findViewById(R.id.textViewWon);
        textViewDraw = findViewById(R.id.textViewDraw);
        textViewLost = findViewById(R.id.textViewLost);
        textViewGoalsFor = findViewById(R.id.textViewGoalsFor);
        textViewGoalsAgainst = findViewById(R.id.textViewGoalsAgainst);
        textViewGoalDifference = findViewById(R.id.textViewGoalDifference);
        textViewHomeStats = findViewById(R.id.textViewHomeStats);
        textViewAwayStats = findViewById(R.id.textViewAwayStats);
        textViewTopScorers = findViewById(R.id.textViewTopScorers);
    }

    private void setupSpinner() {
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
                statsContainer.setVisibility(View.GONE);
                fetchStandingsForSeason(selectedSeason);
                fetchScorersForSeason(selectedSeason);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchStandingsForSeason(int seasonYear) {
        progressBar.setVisibility(View.VISIBLE);
        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);
        Call<StandingsResponse> call = service.getStandings(leagueId, BuildConfig.API_KEY, seasonYear);

        call.enqueue(new Callback<StandingsResponse>() {
            @Override
            public void onResponse(@NonNull Call<StandingsResponse> call, @NonNull Response<StandingsResponse> response) {
                if (isFinishing()) return; // 画面が終了していたら何もしない
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    updateStandingsUI(response.body());
                } else {
                    Toast.makeText(TeamStatsActivity.this, "順位表データの取得に失敗", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StandingsResponse> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TeamStatsActivity.this, "通信エラー", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchScorersForSeason(int seasonYear) {
        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);
        // 🔽 第4引数に取得したい人数（例: 20）を追加 🔽
        Call<ScorersResponse> call = service.getScorers(leagueId, BuildConfig.API_KEY, seasonYear, 500);

        call.enqueue(new Callback<ScorersResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScorersResponse> call, @NonNull Response<ScorersResponse> response) {
                if (isFinishing()) return;
                if (response.isSuccessful() && response.body() != null) {
                    updateScorersUI(response.body().getScorers());
                } else {
                    textViewTopScorers.setText("得点データがありません");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ScorersResponse> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                textViewTopScorers.setText("得点データの取得に失敗");
            }
        });
    }

    private TableEntry findTeamStats(StandingsResponse standingsResponse, int teamId, String type) {
        if (standingsResponse == null || standingsResponse.getStandings() == null) return null;
        for (Standing standing : standingsResponse.getStandings()) {
            if (type.equals(standing.getType())) {
                for (TableEntry entry : standing.getTable()) {
                    if (entry.getTeam().getId() == teamId) {
                        return entry;
                    }
                }
            }
        }
        return null;
    }

    private void updateStandingsUI(StandingsResponse standings) {
        TableEntry total = findTeamStats(standings, teamId, "TOTAL");
        TableEntry home = findTeamStats(standings, teamId, "HOME");
        TableEntry away = findTeamStats(standings, teamId, "AWAY");

        if (total != null) {
            statsContainer.setVisibility(View.VISIBLE);
            textViewPosition.setText("順位: " + total.getPosition() + "位");
            textViewPoints.setText("勝ち点: " + total.getPoints());
            textViewPlayedGames.setText("試合数: " + total.getPlayedGames());
            textViewWon.setText("勝利: " + total.getWon());
            textViewDraw.setText("引分: " + total.getDraw());
            textViewLost.setText("敗戦: " + total.getLost());
            textViewGoalsFor.setText("総得点: " + total.getGoalsFor());
            textViewGoalsAgainst.setText("総失点: " + total.getGoalsAgainst());
            int diff = total.getGoalsFor() - total.getGoalsAgainst();
            textViewGoalDifference.setText("得失点差: " + (diff >= 0 ? "+" : "") + diff);
        } else {
            statsContainer.setVisibility(View.GONE);
            Toast.makeText(this, "このシーズンの順位表データがありません", Toast.LENGTH_SHORT).show();
        }

        if (home != null) {
            textViewHomeStats.setText("ホーム: " + home.getWon() + "勝 " + home.getDraw() + "分 " + home.getLost() + "敗");
        } else {
            textViewHomeStats.setText("ホーム: データなし");
        }
        if (away != null) {
            textViewAwayStats.setText("アウェイ: " + away.getWon() + "勝 " + away.getDraw() + "分 " + away.getLost() + "敗");
        } else {
            textViewAwayStats.setText("アウェイ: データなし");
        }
    }

    private void updateScorersUI(List<Scorer> scorers) {
        List<Scorer> teamScorers = scorers.stream()
                .filter(scorer -> scorer.getTeam().getId() == teamId)
                .collect(Collectors.toList());

        if (teamScorers.isEmpty()) {
            textViewTopScorers.setText("データがありません");
            return;
        }

        StringBuilder sb = new StringBuilder();
        // 上位5人まで表示
        for (int i = 0; i < teamScorers.size() && i < 20; i++) {
            Scorer scorer = teamScorers.get(i);
            sb.append(i + 1).append(". ")
                    .append(scorer.getPlayer().getName())
                    .append(" (").append(scorer.getGoals()).append("点)\n");
        }
        // 末尾の改行を削除
        if(sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        textViewTopScorers.setText(sb.toString());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}