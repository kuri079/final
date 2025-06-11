package com.example.kicklog;

import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    PlayerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.player_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.football-data.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FootballApiService api = retrofit.create(FootballApiService.class);

        // 例：Liverpool FC の teamId = 64
        Call<TeamResponse> call = api.getTeam(64);
        call.enqueue(new Callback<TeamResponse>() {
            @Override
            public void onResponse(Call<TeamResponse> call, Response<TeamResponse> res) {
                if (res.isSuccessful()) {
                    List<SquadMember> players = res.body().getSquad();
                    adapter = new PlayerAdapter(players);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "データ取得失敗", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeamResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "通信エラー", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

