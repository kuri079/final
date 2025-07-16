package com.example.kicklog;

import android.content.Intent;
import android.graphics.Bitmap; // Bitmapを使うために追加
import android.graphics.drawable.BitmapDrawable; // BitmapDrawableを使うために追加
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View; // Viewを使うために追加
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette; // Paletteをインポート

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget; // GlideでBitmapを受け取るために追加
import com.bumptech.glide.request.transition.Transition; // GlideでBitmapを受け取るために追加
import com.example.kicklog.model.TeamDetailResponse;
import com.example.kicklog.network.ApiClient;
import com.example.kicklog.network.FootballDataApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamMenuActivity extends AppCompatActivity {

    public static final String EXTRA_TEAM_ID = "extra_team_id";
    public static final String EXTRA_TEAM_NAME = "extra_team_name";
    public static final String EXTRA_LEAGUE_ID = "extra_league_id";
    private static final String TAG = "TeamMenuActivity";

    private int teamId;
    private String teamName;
    private String leagueId;

    private ImageView teamCrestImageView;
    private View rootLayout; // アクティビティのルートレイアウト (背景色変更用)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_menu);

        // アクティビティのルートレイアウトを取得 (背景色変更用)
        rootLayout = findViewById(android.R.id.content); // ActivityのcontentViewを取得する一般的な方法

        // Intentから各種IDと名前を受け取る
        teamId = getIntent().getIntExtra(EXTRA_TEAM_ID, -1);
        teamName = getIntent().getStringExtra(EXTRA_TEAM_NAME);
        leagueId = getIntent().getStringExtra(EXTRA_LEAGUE_ID);

        // アクションバーの設定
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(teamName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // UI部品の初期化とデータ設定
        teamCrestImageView = findViewById(R.id.imageViewTeamCrestMenu);
        TextView teamNameHeader = findViewById(R.id.textViewTeamNameMenu);
        teamNameHeader.setText(teamName);

        // ボタンの初期化とクリックリスナー設定
        initializeButtons();

        // チームIDが有効なら、エンブレムを取得
        if (teamId != -1) {
            fetchTeamCrest(teamId);
        }
    }

    private void initializeButtons() {
        Button buttonPlayerList = findViewById(R.id.buttonPlayerList);
        Button buttonMatchList = findViewById(R.id.buttonMatchList);
        Button buttonTeamStats = findViewById(R.id.buttonTeamStats);
        Button buttonLineup = findViewById(R.id.buttonLineup);

        buttonPlayerList.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerListActivity.class);
            intent.putExtra(PlayerListActivity.EXTRA_TEAM_ID, teamId);
            intent.putExtra(PlayerListActivity.EXTRA_LEAGUE_ID, leagueId);
            startActivity(intent);
        });

        buttonMatchList.setOnClickListener(v -> {
            Intent intent = new Intent(this, MatchListActivity.class);
            intent.putExtra(MatchListActivity.EXTRA_TEAM_ID, teamId);
            // MatchListActivityにleagueIdが必要な場合も渡す
            // intent.putExtra(MatchListActivity.EXTRA_LEAGUE_ID, leagueId);
            startActivity(intent);
        });

        buttonTeamStats.setOnClickListener(v -> {
            Intent intent = new Intent(this, TeamStatsActivity.class);
            intent.putExtra(TeamStatsActivity.EXTRA_TEAM_ID, teamId);
            intent.putExtra(TeamStatsActivity.EXTRA_LEAGUE_ID, leagueId);
            startActivity(intent);
        });

        buttonLineup.setOnClickListener(v -> {
            Intent intent = new Intent(this, LineupActivity.class);
            intent.putExtra(LineupActivity.EXTRA_TEAM_ID, teamId);
            // LineupActivityにleagueIdが必要な場合も渡す
            // intent.putExtra(LineupActivity.EXTRA_LEAGUE_ID, leagueId);
            startActivity(intent);
        });
    }

    private void fetchTeamCrest(int teamId) {
        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);
        Call<TeamDetailResponse> call = service.getTeamDetails(teamId, BuildConfig.API_KEY);

        call.enqueue(new Callback<TeamDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<TeamDetailResponse> call, @NonNull Response<TeamDetailResponse> response) {
                if (isFinishing()) return;
                if (response.isSuccessful() && response.body() != null && response.body().getCrest() != null) {
                    String crestUrl = response.body().getCrest();

                    // ★★★ ここからエンブレムから色を抽出するロジック ★★★
                    Glide.with(TeamMenuActivity.this)
                            .asBitmap() // Bitmapとして読み込む
                            .load(crestUrl)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(new CustomTarget<Bitmap>() { // CustomTargetでBitmapを受け取る
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    teamCrestImageView.setImageBitmap(resource); // まずエンブレムを表示

                                    // Paletteを使って色を抽出
                                    Palette.from(resource).generate(palette -> {
                                        int defaultColor = 0xFFFFFFFF; // デフォルトの背景色（白）
                                        int dominantColor = defaultColor;

                                        if (palette != null) {
                                            // 最も支配的な色を取得
                                            dominantColor = palette.getDominantColor(defaultColor);
                                            // あるいは、より鮮やかな色、暗い色などを試すこともできます
                                            // dominantColor = palette.getVibrantColor(defaultColor);
                                            // dominantColor = palette.getDarkVibrantColor(defaultColor);
                                            // dominantColor = palette.getMutedColor(defaultColor);
                                        }

                                        // 抽出した色を背景に設定
                                        // alphaを調整して、文字が読めるようにすることも検討
                                        // 例: 半透明にする (0xAA はアルファ値)
                                        // int finalColor = (dominantColor & 0x00FFFFFF) | 0xAA000000;
                                        rootLayout.setBackgroundColor(dominantColor);
                                    });
                                }

                                @Override
                                public void onLoadCleared(@Nullable android.graphics.drawable.Drawable placeholder) {
                                    // リソースがクリアされた時の処理
                                    teamCrestImageView.setImageDrawable(placeholder);
                                }
                            });
                    // ★★★ エンブレム色抽出ロジックここまで ★★★

                } else {
                    Log.e(TAG, "Failed to fetch team crest. Code: " + response.code() + ", URL: " + response.body().getCrest());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TeamDetailResponse> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                Log.e(TAG, "Error fetching team crest.", t);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}