// 新規作成: com/example/kicklog/PlayerListActivity.java
package com.example.kicklog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kicklog.model.Player;
import com.example.kicklog.model.TeamDetailResponse;
import com.example.kicklog.network.ApiClient;
import com.example.kicklog.network.FootballDataApiService;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerListActivity extends AppCompatActivity implements PlayerAdapter.OnPlayerClickListener {

    public static final String EXTRA_TEAM_ID = "extra_team_id";
    private PlayerAdapter adapter;
    private final List<Player> playerList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("選手一覧");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPlayers);
        adapter = new PlayerAdapter(playerList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        int teamId = getIntent().getIntExtra(EXTRA_TEAM_ID, -1);
        if (teamId != -1) {
            fetchPlayers(teamId);
        }
    }

    private void fetchPlayers(int teamId) {
        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);
        Call<TeamDetailResponse> call = service.getTeamDetails(teamId, BuildConfig.API_KEY);
        call.enqueue(new Callback<TeamDetailResponse>() {
            @Override
            public void onResponse(Call<TeamDetailResponse> call, Response<TeamDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    playerList.clear();
                    playerList.addAll(response.body().getSquad());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(PlayerListActivity.this, "選手データの取得に失敗", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<TeamDetailResponse> call, Throwable t) {
                Toast.makeText(PlayerListActivity.this, "通信エラー", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPlayerClick(Player player) {
        // 次のステップで、ここからPlayerDetailActivityに遷移させる
        Intent intent = new Intent(this, PlayerDetailActivity.class);
        intent.putExtra(PlayerDetailActivity.EXTRA_PLAYER_ID, player.getId());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}