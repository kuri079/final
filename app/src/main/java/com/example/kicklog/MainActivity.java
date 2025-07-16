package com.example.kicklog;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kicklog.model.Team;
import com.example.kicklog.model.TeamsResponse;
import com.example.kicklog.network.ApiClient;
import com.example.kicklog.network.FootballDataApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ClubAdapter.OnTeamClickListener {

    private static final String TAG = "MainActivity";
    // Intentでデータを受け取るためのキー
    public static final String EXTRA_LEAGUE_ID = "extra_league_id";
    public static final String EXTRA_LEAGUE_NAME = "extra_league_name";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ClubAdapter adapter;
    private final List<Team> teamList = new ArrayList<>();
    private String leagueId;

    // リーグコードとテーマカラーを紐付けるマップ
    private final Map<String, String> leagueColors = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeColors(); // 色のマップを初期化

        // 前の画面からリーグIDとリーグ名を受け取る
        leagueId = getIntent().getStringExtra(EXTRA_LEAGUE_ID);
        String leagueName = getIntent().getStringExtra(EXTRA_LEAGUE_NAME);

        // アクションバーのタイトルを設定
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(leagueName != null ? leagueName : "クラブ一覧");
            // 戻るボタンを有効化
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // リーグのテーマカラーを適用
        setThemeColor(leagueColors.get(leagueId));

        // UI部品の初期化
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        setupRecyclerView();

        // リーグIDがあればクラブ一覧を取得
        if (leagueId != null && !leagueId.isEmpty()) {
            fetchTeamData(leagueId);
        } else {
            Toast.makeText(this, "リーグ情報がありません", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * リーグのテーマカラーを定義する
     */
    private void initializeColors() {
        leagueColors.put("PL", "#37003c");   // プレミアリーグ (紫)
        leagueColors.put("BL1", "#d20515"); // ブンデスリーガ (赤)
        leagueColors.put("SA", "#0053a3");    // セリエA (青)
        leagueColors.put("PD", "#ee8707");    // ラ・リーガ (オレンジ)
        leagueColors.put("FL1", "#DAA520"); // リーグ・アン (黄)
    }

    /**
     * アクションバーとステータスバーの色を変更する
     * @param colorString カラーコードの文字列 (例: "#37003c")
     */
    private void setThemeColor(String colorString) {
        if (colorString == null) return;

        try {
            int color = Color.parseColor(colorString);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
            }

            Window window = getWindow();
            // 明るい色か暗い色か判断して、ステータスバーの文字色を調整
            if (ColorUtils.calculateLuminance(color) > 0.5) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(0);
            }
            window.setStatusBarColor(color);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "無効なカラーコードです: " + colorString, e);
        }
    }

    private void setupRecyclerView() {
        adapter = new ClubAdapter(teamList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onTeamClick(Team team) {
        Intent intent = new Intent(this, TeamMenuActivity.class);
        intent.putExtra(TeamMenuActivity.EXTRA_TEAM_ID, team.getId());
        intent.putExtra(TeamMenuActivity.EXTRA_TEAM_NAME, team.getName());
        intent.putExtra(TeamMenuActivity.EXTRA_LEAGUE_ID, this.leagueId);
        startActivity(intent);
    }

    private void fetchTeamData(String leagueId) {
        progressBar.setVisibility(View.VISIBLE);
        FootballDataApiService apiService = ApiClient.getClient().create(FootballDataApiService.class);
        Call<TeamsResponse> call = apiService.getTeamsInCompetition(leagueId, BuildConfig.API_KEY);

        call.enqueue(new Callback<TeamsResponse>() {
            @Override
            public void onResponse(@NonNull Call<TeamsResponse> call, @NonNull Response<TeamsResponse> response) {
                if (isFinishing()) return;
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    teamList.clear();
                    teamList.addAll(response.body().getTeams());
                    adapter.notifyDataSetChanged();
                } else {
                    String errorMsg = "データ取得に失敗。エラーコード: " + response.code();
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "API Error: " + errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TeamsResponse> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                progressBar.setVisibility(View.GONE);
                String failureMsg = "通信エラー: " + t.getMessage();
                Toast.makeText(MainActivity.this, failureMsg, Toast.LENGTH_LONG).show();
                Log.e(TAG, "API Failure", t);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // アクションバーの戻るボタンが押された時の処理
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}