package com.example.kicklog;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
    private static final String TAG = "TeamStatsActivity";

    private Spinner spinnerSeasons;
    private ProgressBar progressBar;
    private ScrollView statsContainer;
    private TextView textViewPosition, textViewPoints, textViewPlayedGames, textViewWon,
            textViewDraw, textViewLost, textViewGoalDifference, textViewTopScorers;

    private int teamId;
    private String leagueId;
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
        textViewGoalDifference = findViewById(R.id.textViewGoalDifference);
        textViewTopScorers = findViewById(R.id.textViewTopScorers);
    }

    private void setupSpinner() {
        List<String> seasonDisplayList = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 10; i++) {
            int seasonStartYear = currentYear - i ;
            seasonYearList.add(seasonStartYear);
            seasonDisplayList.add(seasonStartYear + "-" + (seasonStartYear + 1));
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, seasonDisplayList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeasons.setAdapter(spinnerAdapter);

        spinnerSeasons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedSeason = seasonYearList.get(position);
                fetchSeasonData(selectedSeason);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchSeasonData(int seasonYear) {
        progressBar.setVisibility(View.VISIBLE);
        statsContainer.setVisibility(View.INVISIBLE);

        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);
        AtomicInteger successfulCalls = new AtomicInteger(0);

        // 順位表を取得
        service.getStandings(leagueId, BuildConfig.API_KEY, seasonYear).enqueue(new Callback<StandingsResponse>() {
            @Override
            public void onResponse(@NonNull Call<StandingsResponse> call, @NonNull Response<StandingsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TableEntry teamEntry = findTeamInStandings(response.body(), teamId);
                    updateStandingsUI(teamEntry);
                }
                if (successfulCalls.incrementAndGet() == 2) hideLoading();
            }
            @Override
            public void onFailure(@NonNull Call<StandingsResponse> call, @NonNull Throwable t) {
                if (successfulCalls.incrementAndGet() == 2) hideLoading();
            }
        });

        // 得点ランキングを取得
        service.getScorers(leagueId, BuildConfig.API_KEY, seasonYear, 100).enqueue(new Callback<ScorersResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScorersResponse> call, @NonNull Response<ScorersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateScorersUI(response.body().getScorers());
                }
                if (successfulCalls.incrementAndGet() == 2) hideLoading();
            }
            @Override
            public void onFailure(@NonNull Call<ScorersResponse> call, @NonNull Throwable t) {
                if (successfulCalls.incrementAndGet() == 2) hideLoading();
            }
        });
    }

    private void hideLoading() {
        if(isFinishing()) return;
        progressBar.setVisibility(View.GONE);
        statsContainer.setVisibility(View.VISIBLE);
    }

    private TableEntry findTeamInStandings(StandingsResponse standings, int teamId) {
        if (standings == null || standings.getStandings() == null) return null;
        for (Standing standing : standings.getStandings()) {
            if ("TOTAL".equals(standing.getType())) {
                for (TableEntry entry : standing.getTable()) {
                    if (entry.getTeam() != null && entry.getTeam().getId() == teamId) {
                        return entry;
                    }
                }
            }
        }
        return null;
    }

    private void updateStandingsUI(TableEntry entry) {
        if (entry == null) {
            Toast.makeText(this, "このシーズンの順位表データがありません", Toast.LENGTH_SHORT).show();
            return;
        }
        textViewPosition.setText("順位: " + entry.getPosition() + "位");
        textViewPoints.setText("勝ち点: " + entry.getPoints());
        textViewPlayedGames.setText("試合数: " + entry.getPlayedGames());
        textViewWon.setText("勝利: " + entry.getWon());
        textViewDraw.setText("引分: " + entry.getDraw());
        textViewLost.setText("敗戦: " + entry.getLost());
        int diff = entry.getGoalsFor() - entry.getGoalsAgainst();
        textViewGoalDifference.setText("得失点差: " + (diff >= 0 ? "+" : "") + diff + " (" + entry.getGoalsFor() + "-" + entry.getGoalsAgainst() + ")");
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
        for (int i = 0; i < teamScorers.size() && i < 5; i++) {
            Scorer scorer = teamScorers.get(i);
            sb.append(i + 1).append(". ")
                    .append(scorer.getPlayer().getName())
                    .append(" (").append(scorer.getGoals()).append("点)\n");
        }
        textViewTopScorers.setText(sb.toString().trim());
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