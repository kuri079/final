package com.example.kicklog;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kicklog.adapter.TeamAdapter;
import com.example.kicklog.api.ApiService;
import com.example.kicklog.api.RetrofitClient;
import com.example.kicklog.model.TeamResponse;
import com.example.kicklog.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Spinner leagueSpinner; private RecyclerView rv; private ApiService api;

    protected void onCreate(Bundle s){
        super.onCreate(s); setContentView(R.layout.activity_main);
        leagueSpinner = findViewById(R.id.leagueSpinner);
        rv = findViewById(R.id.recyclerView); rv.setLayoutManager(new LinearLayoutManager(this));

        ArrayAdapter<CharSequence> ad = ArrayAdapter.createFromResource(
                this,R.array.league_names,android.R.layout.simple_spinner_item);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leagueSpinner.setAdapter(ad);

        api = RetrofitClient.getClient().create(ApiService.class);

        leagueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> p, View v,int pos,long id){
                String[] codes = getResources().getStringArray(R.array.league_codes);
                fetchTeams(codes[pos]);
            }
            public void onNothingSelected(AdapterView<?> p){}
        });
    }

    private void fetchTeams(String code){
        api.getTeams(Constants.API_KEY,code).enqueue(new Callback<TeamResponse>() {
            public void onResponse(Call<TeamResponse> c, Response<TeamResponse> r){
                Log.d("API_DEBUG","code="+r.code());
                if(r.isSuccessful() && r.body()!=null && r.body().teams!=null)
                    rv.setAdapter(new TeamAdapter(r.body().teams));
                else Toast.makeText(MainActivity.this,"取得失敗:"+r.code(),Toast.LENGTH_SHORT).show();
            }
            public void onFailure(Call<TeamResponse> c, Throwable t){
                Toast.makeText(MainActivity.this,"通信エラー:"+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
