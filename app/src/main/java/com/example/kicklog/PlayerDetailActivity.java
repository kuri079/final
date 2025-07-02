// PlayerDetailActivity.java
package com.example.kicklog;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.kicklog.api.ApiService;
import com.example.kicklog.api.RetrofitClient;
import com.example.kicklog.model.PersonResponse;
import com.example.kicklog.model.PlayerDetailResponse;
import com.example.kicklog.util.Constants;
import com.example.kicklog.util.SimpleCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerDetailActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_player_detail);
        int pid = getIntent().getIntExtra("playerId", -1);
        Log.d("DEBUG","PlayerDetail pid="+pid);
        if (pid == -1) {
            Toast.makeText(this,"id 受信失敗", Toast.LENGTH_SHORT).show();
            finish();   // ← ここで戻ってしまう
            return;
        }


        int playerId = getIntent().getIntExtra("playerId", -1);
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getPerson(Constants.API_KEY, playerId)
                .enqueue(new SimpleCallback<PersonResponse>(this){
                    @Override protected void success(PersonResponse body){
                        // TextView に詳細を表示するなど
                    }
                });


    }

    private void bind(PlayerDetailResponse r) {

        /*―― 必ず null / 空リストをチェック ――*/
        if (r.statistics == null || r.statistics.isEmpty()) {
            Toast.makeText(this, "統計データ無し", Toast.LENGTH_SHORT).show();
            return;
        }

        // ★ List の 0 番目を取り出す
        PlayerDetailResponse.Statistics st = r.statistics.get(0);

        // --- 以下はそのまま ---
        ((TextView) findViewById(R.id.valApps))
                .setText(String.valueOf(st.games.appearances));

        ((TextView) findViewById(R.id.valGoals))
                .setText(String.valueOf(st.goals.total == null ? 0 : st.goals.total));

        ((TextView) findViewById(R.id.valAssists))
                .setText(String.valueOf(st.goals.assists == null ? 0 : st.goals.assists));

        ((TextView) findViewById(R.id.valYel))
                .setText(String.valueOf(st.cards.yellow));

        ((TextView) findViewById(R.id.valRed))
                .setText(String.valueOf(st.cards.red));
    }

}
