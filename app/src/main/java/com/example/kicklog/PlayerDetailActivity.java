package com.example.kicklog;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.kicklog.R;
import com.example.kicklog.databinding.ActivityPlayerDetailBinding;
import com.example.kicklog.model.PlayerDetailResponse;
import com.example.kicklog.network.ApiClient;
import com.example.kicklog.network.FootballDataService;

import retrofit2.Call;
import com.example.kicklog.model.Player;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerDetailActivity extends AppCompatActivity {

    private ActivityPlayerDetailBinding binding;
    private int playerId;
    public static final String EXTRA_PLAYER_ID = "extra_player_id"; // ◀️ 定数を定義


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // アクションバーに戻るボタンを表示
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("選手詳細");
        }

        playerId = getIntent().getIntExtra(Constants.PLAYER_ID_EXTRA, -1);

        if (playerId != -1) {
            fetchPlayerDetails();
        } else {
            Toast.makeText(this, "選手情報の取得に失敗しました。", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchPlayerDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);
        FootballDataService service = ApiClient.getClient().create(FootballDataService.class);
        Call<PlayerDetailResponse> call = service.getPlayerDetails(playerId, BuildConfig.API_KEY);

        call.enqueue(new Callback<PlayerDetailResponse>() {
            @Override
            public void onResponse(Call<PlayerDetailResponse> call, Response<PlayerDetailResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    String errorMsg = "詳細情報の取得に失敗。";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += " エラー: " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e("API_ERROR_DETAIL", "Error parsing error body", e);
                        }
                    } else {
                        errorMsg += " エラーコード: " + response.code();
                    }
                    Log.e("API_ERROR_DETAIL", errorMsg);
                    Toast.makeText(PlayerDetailActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PlayerDetailResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Log.e("NETWORK_ERROR_DETAIL", "通信エラー", t);
                Toast.makeText(PlayerDetailActivity.this, "通信エラーが発生しました。", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(PlayerDetailResponse player) {
        binding.textViewPlayerName.setText(player.getName());
        if (player.getCurrentTeam() != null) {
            binding.textViewTeamName.setText(player.getCurrentTeam().getName());
            Glide.with(this)
                    .load(player.getCurrentTeam().getCrest())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(binding.imageViewTeamCrest);
        }
        binding.textViewNationality.setText("国籍: " + (player.getNationality() != null ? player.getNationality() : "N/A"));
        binding.textViewPosition.setText("ポジション: " + (player.getPosition() != null ? player.getPosition() : "N/A"));
        binding.textViewDateOfBirth.setText("誕生日: " + (player.getDateOfBirth() != null ? player.getDateOfBirth() : "N/A"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 戻るボタンが押された時の処理
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

