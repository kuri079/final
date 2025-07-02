package com.example.kicklog;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kicklog.adapter.PlayerAdapter;
import com.example.kicklog.api.ApiService;
import com.example.kicklog.api.RetrofitClient;
import com.example.kicklog.model.TeamResponse;
import com.example.kicklog.util.Constants;
import com.example.kicklog.util.SimpleCallback;

public class PlayersActivity extends AppCompatActivity {
    protected void onCreate(Bundle b){
        super.onCreate(b); setContentView(R.layout.activity_recycler);
        int id = getIntent().getIntExtra("teamId",-1);
        RecyclerView rv = findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getTeamDetail(Constants.API_KEY,id)
                .enqueue(new SimpleCallback<TeamResponse>(this){
                    protected void success(TeamResponse body){
                        rv.setAdapter(new PlayerAdapter(body.squad));
                    }
                });
    }
}
