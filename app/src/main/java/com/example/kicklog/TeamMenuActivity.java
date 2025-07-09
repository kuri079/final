// 新規作成: com/example/kicklog/TeamMenuActivity.java
package com.example.kicklog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TeamMenuActivity extends AppCompatActivity {

    public static final String EXTRA_TEAM_ID = "extra_team_id";
    public static final String EXTRA_TEAM_NAME = "extra_team_name";
    public static final String EXTRA_LEAGUE_ID = "extra_league_id"; // ◀️ 追加
    private int teamId; // ◀️ クラス変数に変更
    private String leagueId; // ◀️ 追加

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_menu);

        int teamId = getIntent().getIntExtra(EXTRA_TEAM_ID, -1);
        String teamName = getIntent().getStringExtra(EXTRA_TEAM_NAME);

        TextView textViewTeamName = findViewById(R.id.textViewSelectedTeamName);
        Button buttonPlayerList = findViewById(R.id.buttonPlayerList);
        Button buttonTeamStats = findViewById(R.id.buttonTeamStats);
        Button buttonMatchList = findViewById(R.id.buttonMatchList);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(teamName);
        }

        textViewTeamName.setText(teamName);
        this.teamId = getIntent().getIntExtra(EXTRA_TEAM_ID, -1); // ◀️ クラス変数に保持
        this.leagueId = getIntent().getStringExtra(EXTRA_LEAGUE_ID); // ◀️ 追加

        // 各ボタンのクリック処理 (今はToastを表示するだけ)
// com/example/kicklog/TeamMenuActivity.java を修正
        buttonPlayerList.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerListActivity.class);
            intent.putExtra(PlayerListActivity.EXTRA_TEAM_ID, this.teamId);
            startActivity(intent);
        });

        buttonTeamStats.setOnClickListener(v -> {
            Intent intent = new Intent(this, TeamStatsActivity.class);
            intent.putExtra(TeamStatsActivity.EXTRA_TEAM_ID, this.teamId);
            intent.putExtra(TeamStatsActivity.EXTRA_LEAGUE_ID, this.leagueId);
            startActivity(intent);
        });

        buttonMatchList.setOnClickListener(v -> {
            // Toast.makeText(this, "試合一覧（未実装）", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MatchListActivity.class);
            intent.putExtra(MatchListActivity.EXTRA_TEAM_ID, teamId);
            startActivity(intent);
        });
    }

    // アクションバーの戻るボタンの処理
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
