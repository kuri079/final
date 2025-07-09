package com.example.kicklog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kicklog.model.Team;
import com.example.kicklog.model.TeamsResponse;
import com.example.kicklog.network.ApiClient;
import com.example.kicklog.network.FootballDataApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 🔽 OnTeamClickListenerを実装(implements)する
public class MainActivity extends AppCompatActivity implements ClubAdapter.OnTeamClickListener {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ClubAdapter adapter;
    private List<Team> teamList = new ArrayList<>();
    private String leagueId; // リーグIDを保持する変数を追加


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("クラブ一覧");
        }

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        String leagueId = getIntent().getStringExtra(LeagueSelectionActivity.EXTRA_LEAGUE_ID);

        setupRecyclerView();
        this.leagueId = getIntent().getStringExtra(LeagueSelectionActivity.EXTRA_LEAGUE_ID);

        if (leagueId != null && !leagueId.isEmpty()) {
            fetchTeamData(leagueId);
        } else {
            Toast.makeText(this, "リーグIDがありません", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        // MainActivityがリスナーになったので、thisを渡せる
        adapter = new ClubAdapter(teamList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    // 🔽 @Overrideが正しく機能するようになる 🔽
    @Override
    public void onTeamClick(Team team) {
        Intent intent = new Intent(this, TeamMenuActivity.class);

        // 🔽 タイプミスを修正 🔽
        intent.putExtra(TeamMenuActivity.EXTRA_TEAM_NAME, team.getName());
        startActivity(intent);

        intent.putExtra(TeamMenuActivity.EXTRA_TEAM_ID, team.getId());
        intent.putExtra(TeamMenuActivity.EXTRA_TEAM_NAME, team.getName());
        intent.putExtra(TeamMenuActivity.EXTRA_LEAGUE_ID, this.leagueId); // ◀️ リーグIDを追加
        startActivity(intent);
    }

    private void fetchTeamData(String leagueId) {
        progressBar.setVisibility(View.VISIBLE);

        FootballDataApiService apiService = ApiClient.getClient().create(FootballDataApiService.class);
        Call<TeamsResponse> call = apiService.getTeamsInCompetition(leagueId, BuildConfig.API_KEY);

        call.enqueue(new Callback<TeamsResponse>() {
            @Override
            public void onResponse(Call<TeamsResponse> call, Response<TeamsResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    teamList.clear();
                    teamList.addAll(response.body().getTeams());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Response not successful. Code: " + response.code());
                    Toast.makeText(MainActivity.this, "Failed to fetch data: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeamsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "API call failed.", t);
                Toast.makeText(MainActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}