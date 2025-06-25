package com.example.kicklog;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kicklog.adapter.PlayerAdapter;
import com.example.kicklog.api.ApiService;
import com.example.kicklog.api.RetrofitClient;
import com.example.kicklog.model.PlayerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PlayerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<PlayerResponse> call = apiService.getPlayers(33, 2023);

        call.enqueue(new Callback<PlayerResponse>() {
            @Override
            public void onResponse(Call<PlayerResponse> call, Response<PlayerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new PlayerAdapter(response.body().getResponse());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "データ取得失敗", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlayerResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "通信エラー: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
