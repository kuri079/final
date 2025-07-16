package com.example.kicklog;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kicklog.model.Player;
import com.example.kicklog.model.TeamDetailResponse;
import com.example.kicklog.network.ApiClient;
import com.example.kicklog.network.FootballDataApiService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerListActivity extends AppCompatActivity implements PlayerAdapter.OnPlayerClickListener {

    public static final String EXTRA_TEAM_ID = "extra_team_id";
    public static final String EXTRA_LEAGUE_ID = "extra_league_id";
    private PlayerAdapter adapter;
    private final List<Player> playerList = new ArrayList<>();
    private int teamId;
    private String leagueId;

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

        teamId = getIntent().getIntExtra(EXTRA_TEAM_ID, -1);
        leagueId = getIntent().getStringExtra(EXTRA_LEAGUE_ID);
        if (teamId != -1) {
            fetchPlayers(teamId);
        }
    }

    private void fetchPlayers(int teamId) {
        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);
        Call<TeamDetailResponse> call = service.getTeamDetails(teamId, BuildConfig.API_KEY);
        call.enqueue(new Callback<TeamDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<TeamDetailResponse> call, @NonNull Response<TeamDetailResponse> response) {
                if (isFinishing()) return;
                if (response.isSuccessful() && response.body() != null && response.body().getSquad() != null) {
                    List<Player> squad = response.body().getSquad();

                    // ▼▼▼ 並び替えロジックを修正 ▼▼▼
                    // 1. ポジションのカテゴリで並び替え
                    // 2. 同じカテゴリ内では名前で並び替え
                    squad.sort(
                            Comparator.comparingInt((Player p) -> getPositionRank(p.getPosition()))
                                    .thenComparing(Player::getName)
                    );
                    // ▲▲▲ ここまで ▲▲▲

                    playerList.clear();
                    playerList.addAll(squad);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(PlayerListActivity.this, "選手データの取得に失敗", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<TeamDetailResponse> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                Toast.makeText(PlayerListActivity.this, "通信エラー", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * ポジションを大きなカテゴリに分類して優先順位を返す
     */
    private int getPositionRank(String position) {
        if (position == null) return 4; // 不明な場合は最後

        switch (position) {
            case "Goalkeeper":
                return 0;
            case "Left-Back":
            case "Right-Back":
            case "Centre-Back":
            case "Defence":
            case "Defender":
                return 1; // DFグループ
            case "Defensive Midfield":
            case "Central Midfield":
            case "Attacking Midfield":
            case "Left Midfield":
            case "Right Midfield":
            case "Midfielder":
                return 2; // MFグループ
            case "Left Winger":
            case "Right Winger":
            case "Centre-Forward":
            case "Second Striker":
            case "Attacker":
                return 3; // FWグループ
            default:
                return 4; // その他
        }
    }

    @Override
    public void onPlayerClick(Player player) {
        Intent intent = new Intent(this, PlayerDetailActivity.class);
        intent.putExtra(PlayerDetailActivity.EXTRA_PLAYER_ID, player.getId());
        intent.putExtra(PlayerDetailActivity.EXTRA_TEAM_ID, this.teamId);
        intent.putExtra(PlayerDetailActivity.EXTRA_LEAGUE_ID, this.leagueId);
        startActivity(intent);
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