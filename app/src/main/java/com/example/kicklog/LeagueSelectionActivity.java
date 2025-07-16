// 新規作成: com/example/kicklog/LeagueSelectionActivity.java
package com.example.kicklog;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kicklog.model.League;

import java.util.ArrayList;
import java.util.List;

public class LeagueSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_LEAGUE_ID = "extra_league_id";
    public static final String EXTRA_LEAGUE_NAME = "extra_league_name";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_selection);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("リーグを選択");
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewLeagues);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<League> leagues = new ArrayList<>();
        // football-data.org のFreeプランで利用可能なリーグの例
        leagues.add(new League("Premier League (イングランド)", "PL"));
        leagues.add(new League("Bundesliga (ドイツ)", "BL1"));
        leagues.add(new League("Serie A (イタリア)", "SA"));
        leagues.add(new League("Ligue 1 (フランス)", "FL1"));
        leagues.add(new League("La Liga (スペイン)", "PD"));


        LeagueAdapter adapter = new LeagueAdapter(leagues, league -> {
            Intent intent = new Intent(LeagueSelectionActivity.this, MainActivity.class);
            intent.putExtra(EXTRA_LEAGUE_ID, league.getCode());
            intent.putExtra(EXTRA_LEAGUE_NAME, league.getName());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }
}