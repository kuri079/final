<<<<<<< HEAD
import android.net.DnsResolver;
import android.os.Bundle;
import android.telecom.Call;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kicklog.adapter.PlayerAdapter;
import com.example.kicklog.R;
import com.example.kicklog.api.ApiService;
import com.example.kicklog.model.PlayerResponse;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PlayerAdapter adapter;
=======
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
>>>>>>> 92d54545549ea62d0cdbd422d0205151cbe3d352

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
<<<<<<< HEAD

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<PlayerResponse> call = apiService.getPlayers(33, 2023); // チームIDとシーズン

        call.enqueue(new DnsResolver.Callback<PlayerResponse>() {
            @Override
            public void onResponse(Call<PlayerResponse> call, Response<PlayerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new PlayerAdapter(response.body().getResponse());
                    recyclerView.setAdapter(adapter);
=======
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
>>>>>>> 92d54545549ea62d0cdbd422d0205151cbe3d352
                }
            }

            @Override
<<<<<<< HEAD
            public void onFailure(Call<PlayerResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
=======
            public void onFailure(Call<TeamResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "通信エラー", Toast.LENGTH_SHORT).show();
>>>>>>> 92d54545549ea62d0cdbd422d0205151cbe3d352
            }
        });
    }
}

<<<<<<< HEAD

=======
>>>>>>> 92d54545549ea62d0cdbd422d0205151cbe3d352
