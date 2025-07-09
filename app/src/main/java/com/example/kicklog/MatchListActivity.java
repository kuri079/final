// 新規作成: com/example/kicklog/MatchListActivity.java
package com.example.kicklog;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kicklog.model.Match;
import com.example.kicklog.model.MatchListResponse;
import com.example.kicklog.network.ApiClient;
import com.example.kicklog.network.FootballDataApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchListActivity extends AppCompatActivity {

    public static final String EXTRA_TEAM_ID = "extra_team_id";
    private MatchAdapter adapter;
    private List<Match> matchList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("試合一覧");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewMatches);
        adapter = new MatchAdapter(matchList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        int teamId = getIntent().getIntExtra(EXTRA_TEAM_ID, -1);
        if (teamId != -1) {
            fetchMatches(teamId);
        }
    }

    private void fetchMatches(int teamId) {
        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);
        Call<MatchListResponse> call = service.getMatchesForTeam(teamId, BuildConfig.API_KEY);

        call.enqueue(new Callback<MatchListResponse>() {
            @Override
            public void onResponse(Call<MatchListResponse> call, Response<MatchListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    matchList.clear();
                    matchList.addAll(response.body().getMatches());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MatchListActivity.this, "試合データの取得に失敗", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MatchListResponse> call, Throwable t) {
                Toast.makeText(MatchListActivity.this, "通信エラー", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}