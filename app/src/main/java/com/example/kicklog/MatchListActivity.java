// 場所: com/example/kicklog/MatchListActivity.java

package com.example.kicklog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Logのために追加
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kicklog.model.Match;
import com.example.kicklog.model.MatchListResponse;
import com.example.kicklog.network.ApiClient;
import com.example.kicklog.network.FootballDataApiService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// MatchAdapter.OnMatchClickListener インターフェースはもう実装しないので削除
public class MatchListActivity extends AppCompatActivity { // 'implements MatchAdapter.OnMatchClickListener' を削除

    public static final String EXTRA_TEAM_ID = "extra_team_id";
    private MatchAdapter adapter;
    private final List<Match> matchList = new ArrayList<>();
    private int teamId;
    private Spinner spinnerSeasons;
    private final List<Integer> seasonYearList = new ArrayList<>();
    private static final String TAG = "MatchListActivity"; // Logタグを追加

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("試合一覧");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        teamId = getIntent().getIntExtra(EXTRA_TEAM_ID, -1);
        Log.d(TAG, "Received Team ID: " + teamId); // Team IDをログ出力

        RecyclerView recyclerView = findViewById(R.id.recyclerViewMatches);
        spinnerSeasons = findViewById(R.id.spinnerMatchSeasons);

        // ★★★ MatchAdapter のコンストラクタ呼び出しを修正 ★★★
        // Context (this) と matchList を渡す
        adapter = new MatchAdapter(this, matchList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setupSeasonSpinner();
    }

    private void setupSeasonSpinner() {
        List<String> seasonDisplayList = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        // 現在のシーズンはまだ終わっていない可能性があるので、新しいシーズン（currentYear + 1）から表示を開始し、過去10年分程度遡る
        // 例: 現在が2025年なら、2025-26シーズン (APIでは2025) から表示
        // Spinnerの初期選択を最新のシーズンにするために、リストの先頭に最新シーズンを入れる
        // 開始年を現在の年 (2025) から10年遡るように変更
        for (int i = 0; i < 11; i++) { // 0から10まで11回ループ
            int seasonStartYear = currentYear - i;
            seasonYearList.add(seasonStartYear);
            seasonDisplayList.add(seasonStartYear + "-" + (seasonStartYear + 1));
        }
        // seasonYearListとseasonDisplayListが逆順になっているので、Collections.reverse()で反転させると、
        // スピナーの見た目が「最新シーズン」から「古いシーズン」の順になる。
        // Collections.reverse(seasonYearList);
        // Collections.reverse(seasonDisplayList);

        // 初期選択は常にリストの最初の要素 (最新シーズン) にする
        int initialSelection = 0; // seasonYearList の最初の要素が最新シーズン


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, seasonDisplayList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeasons.setAdapter(spinnerAdapter);

        // スピナーの初期選択を設定し、データをフェッチ
        spinnerSeasons.setSelection(initialSelection); // 最新シーズンをデフォルト選択
        // 初期データフェッチはOnItemSelectedListenerが自動的にトリガーするので不要だが、
        // もし自動トリガーされない環境ならここで明示的に呼び出す
        // if (teamId != -1) {
        //     fetchMatches(teamId, seasonYearList.get(initialSelection));
        // }


        spinnerSeasons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Spinnerでシーズンが選択されたら、そのシーズンの試合を取得
                int selectedSeason = seasonYearList.get(position);
                Log.d(TAG, "Selected Season: " + selectedSeason); // 選択されたシーズンをログ出力
                if (teamId != -1) {
                    fetchMatches(teamId, selectedSeason);
                } else {
                    Toast.makeText(MatchListActivity.this, "チーム情報が不足しています。", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchMatches(int teamId, int season) {
        FootballDataApiService service = ApiClient.getClient().create(FootballDataApiService.class);
        Log.d(TAG, "Fetching matches for team " + teamId + ", season " + season); // API呼び出し情報をログ出力
        Call<MatchListResponse> call = service.getMatchesForTeam(teamId, BuildConfig.API_KEY, season);

        call.enqueue(new Callback<MatchListResponse>() {
            @Override
            public void onResponse(@NonNull Call<MatchListResponse> call, @NonNull Response<MatchListResponse> response) {
                if (isFinishing()) return; // Activityが終了状態なら何もしない

                if (response.isSuccessful() && response.body() != null) {
                    matchList.clear();
                    matchList.addAll(response.body().getMatches());
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Fetched " + response.body().getMatches().size() + " matches."); // 取得した試合数をログ出力
                    if (response.body().getMatches().isEmpty()) {
                        Toast.makeText(MatchListActivity.this, season + "シーズンの試合データはありません。", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body for matches", e);
                    }
                    Log.e(TAG, "Failed to fetch match data. Code: " + response.code() + ", Error: " + errorBody);
                    Toast.makeText(MatchListActivity.this, "試合データの取得に失敗: " + response.code(), Toast.LENGTH_SHORT).show();
                    matchList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MatchListResponse> call, @NonNull Throwable t) {
                if (isFinishing()) return; // Activityが終了状態なら何もしない
                Log.e(TAG, "Network error fetching matches.", t); // ネットワークエラーをログ出力
                Toast.makeText(MatchListActivity.this, "通信エラー", Toast.LENGTH_SHORT).show();
                matchList.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    // onMatchClick メソッドはもう不要なので削除
    // @Override
    // public void onMatchClick(Match match) {
    //     Intent intent = new Intent(this, MatchDetailActivity.class);
    //     intent.putExtra(MatchDetailActivity.EXTRA_MATCH_ID, match.getId());
    //     // League ID の受け渡しは MatchAdapter 内で行われる
    //     startActivity(intent);
    // }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}