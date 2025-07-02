package com.example.kicklog;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kicklog.adapter.MatchAdapter;
import com.example.kicklog.api.ApiService;
import com.example.kicklog.api.RetrofitClient;
import com.example.kicklog.model.MatchResponse;
import com.example.kicklog.util.Constants;
import com.example.kicklog.util.SimpleCallback;

public class MatchesActivity extends AppCompatActivity {
    protected void onCreate(Bundle b){
        super.onCreate(b); setContentView(R.layout.activity_recycler);
        int id = getIntent().getIntExtra("teamId",-1);
        RecyclerView rv = findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getTeamMatches(Constants.API_KEY,id)
                .enqueue(new SimpleCallback<MatchResponse>(this){
                    protected void success(MatchResponse body){
                        rv.setAdapter(new MatchAdapter(body.matches));
                    }
                });
    }
}
