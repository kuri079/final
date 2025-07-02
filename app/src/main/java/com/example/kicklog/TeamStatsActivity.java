package com.example.kicklog;

import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kicklog.api.ApiService;
import com.example.kicklog.api.RetrofitClient;
import com.example.kicklog.model.MatchResponse;
import com.example.kicklog.util.Constants;
import com.example.kicklog.util.SimpleCallback;

public class TeamStatsActivity extends AppCompatActivity {

    private int teamId;
    private GridLayout grid;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_stats);

        teamId = getIntent().getIntExtra("teamId",-1);
        grid   = findViewById(R.id.gridStats);

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getTeamMatches(Constants.API_KEY, teamId)
                .enqueue(new SimpleCallback<MatchResponse>(this){
                    @Override protected void success(MatchResponse body){
                        bindStats(body);
                    }
                });
    }

    /** 集計して画面へ流し込む */
    private void bindStats(MatchResponse r){
        int pld=0, w=0, d=0, l=0, gf=0, ga=0;

        for (MatchResponse.Match m : r.matches){
            if(m.score.fullTime.home==null) continue;   // 未開催
            pld++;
            boolean home = (m.homeTeam.id == teamId);
            int forTeam  = home ? m.score.fullTime.home : m.score.fullTime.away;
            int against  = home ? m.score.fullTime.away : m.score.fullTime.home;
            gf += forTeam; ga += against;

            if(forTeam > against) w++;
            else if(forTeam == against) d++;
            else l++;
        }
        int gd   = gf - ga;
        int pts  = w*3 + d;
        float wr = (pld==0) ? 0 : (w*100f/pld);

        addRow("試合数",  pld);
        addRow("勝利",      w);
        addRow("引き分け",     d);
        addRow("負け",    l);
        addRow("ゴール",gf);
        addRow("失点", ga);
        addRow("得失点差",gd);
        addRow("勝ち点",        pts);
        addRow("勝率",    String.format("%.1f %%", wr));
    }

    /** GridLayout に [項目] [値] を 1 行追加 */
    private void addRow(String label, Object value){
        TextView tvKey = new TextView(this);
        tvKey.setText(label); tvKey.setPadding(0,8,16,8);

        TextView tvVal = new TextView(this);
        tvVal.setText(String.valueOf(value)); tvVal.setPadding(0,8,0,8);

        grid.addView(tvKey);
        grid.addView(tvVal);
    }
}
