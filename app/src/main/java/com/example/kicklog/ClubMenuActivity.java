package com.example.kicklog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ClubMenuActivity extends AppCompatActivity {
    private int teamId;
    protected void onCreate(Bundle b){
        super.onCreate(b); setContentView(R.layout.activity_club_menu);

        teamId = getIntent().getIntExtra("teamId",-1);
        ((TextView)findViewById(R.id.clubTitle))
                .setText(getIntent().getStringExtra("teamName"));

        findViewById(R.id.btnPlayers).setOnClickListener(v->open(PlayersActivity.class));
        findViewById(R.id.btnMatches).setOnClickListener(v->open(MatchesActivity.class));
        findViewById(R.id.btnStats).setOnClickListener(v->open(TeamStatsActivity.class));
    }
    private void open(Class<?> cls){
        Intent i = new Intent(this, cls);
        i.putExtra("teamId", teamId);
        startActivity(i);
    }
}
