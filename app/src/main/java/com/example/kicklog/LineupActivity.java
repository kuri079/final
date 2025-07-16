package com.example.kicklog;

import android.content.Intent; // 必要であれば追加
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// AI関連のモデルとサービス
import com.example.kicklog.model.GeminiRequest;
import com.example.kicklog.model.GeminiResponse;
import com.example.kicklog.network.GeminiApiClient;
import com.example.kicklog.network.GeminiApiService;

// プレイヤーデータモデル (TeamMenuActivityから渡される情報や、スタメン選手用)
import com.example.kicklog.model.Player; // FootballData APIのPlayerモデル
import com.example.kicklog.model.LineupPlayer; // カスタムスタメン選手モデル (以前提供済み)

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LineupActivity extends AppCompatActivity {
    // TeamMenuActivityから渡される情報のための定数
    public static final String EXTRA_TEAM_ID = "extra_team_id";
    // もしTeamMenuActivityからleagueIdも渡すならこれも追加
    // public static final String EXTRA_LEAGUE_ID = "extra_league_id";

    private static final String TAG = "LineupActivity";

    // UIコンポーネント
    private Spinner spinnerFormation;
    private Button buttonDiagnose;
    private TextView textViewAIDiagnosis; // AI診断結果表示用
    private ProgressBar progressBarAIDiagnose; // AI診断中プログレスバー

    // ポジションボックスのView参照
    // activity_lineup.xmlのincludeタグのIDに合わせて宣言
    private View pos_gk, pos_df1, pos_df2, pos_df3, pos_df4,
            pos_mf1, pos_mf2, pos_mf3, pos_mf4,
            pos_fw1, pos_fw2;

    // フォーメーション定義: フォーメーション名 -> ポジションタイプ（例: "GK", "DF"など）のリスト
    private final Map<String, List<String>> formationPositions = new HashMap<>();

    // 現在のフォーメーションに割り当てられた選手データ
    // キーはactivity_lineup.xmlのincludeタグのID (例: "pos_gk", "pos_df1")
    private final Map<String, LineupPlayer> currentFormationPlayers = new HashMap<>();

    // TeamMenuActivityから受け取ったチームID (現在はダミープレイヤー用。将来的には選手リスト取得に利用)
    private int teamId;
    // 必要であればleagueIdも受け取る
    // private String leagueId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lineup);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("スタメン構成");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // IntentからteamIdを受け取る
        teamId = getIntent().getIntExtra(EXTRA_TEAM_ID, -1);
        // 必要であればleagueIdも受け取る
        // leagueId = getIntent().getStringExtra(EXTRA_LEAGUE_ID);

        initializeViews();
        setupFormations(); // フォーメーションの定義
        setupSpinner();    // スピナーの設定とリスナー

        // AI診断のために仮の選手データを設定
        // 実際には、APIから取得したチームの選手リストから選択する機能が必要になります
        setupDummyPlayers();

        // 診断ボタンのクリックリスナー設定
        buttonDiagnose.setOnClickListener(v -> fetchAIDiagnosis());
    }

    private void initializeViews() {
        spinnerFormation = findViewById(R.id.spinnerFormation);
        buttonDiagnose = findViewById(R.id.buttonDiagnose);
        textViewAIDiagnosis = findViewById(R.id.textViewAIDiagnosis);
        progressBarAIDiagnose = findViewById(R.id.progressBarAIDiagnose);

        // 各ポジションボックスのViewを取得 (activity_lineup.xmlのincludeタグのIDを使用)
        pos_gk = findViewById(R.id.pos_gk);
        pos_df1 = findViewById(R.id.pos_df1);
        pos_df2 = findViewById(R.id.pos_df2);
        pos_df3 = findViewById(R.id.pos_df3);
        pos_df4 = findViewById(R.id.pos_df4);
        pos_mf1 = findViewById(R.id.pos_mf1);
        pos_mf2 = findViewById(R.id.pos_mf2);
        pos_mf3 = findViewById(R.id.pos_mf3);
        pos_mf4 = findViewById(R.id.pos_mf4);
        pos_fw1 = findViewById(R.id.pos_fw1);
        pos_fw2 = findViewById(R.id.pos_fw2);
    }

    // フォーメーションとそのポジションタイプのマッピングを定義
    private void setupFormations() {
        // 例: 4-4-2 フォーメーション (GK 1, DF 4, MF 4, FW 2)
        formationPositions.put("4-4-2", Arrays.asList(
                "GK",
                "DF", "DF", "DF", "DF", // DFは位置によってLWB/LCB/RCB/RWBなど細分化可能だが、ここではタイプのみ
                "MF", "MF", "MF", "MF", // MFもCDM/CMF/CAM/LMF/RMFなど細分化可能
                "FW", "FW"
        ));
        // 他のフォーメーションもここに追加できます
        // formationPositions.put("4-3-3", Arrays.asList("GK", "DF", "DF", "DF", "DF", "MF", "MF", "MF", "FW", "FW", "FW"));
        // formationPositions.put("3-5-2", Arrays.asList("GK", "DF", "DF", "DF", "MF", "MF", "MF", "MF", "MF", "FW", "FW"));
    }

    private void setupSpinner() {
        List<String> formations = new ArrayList<>(formationPositions.keySet()); // 定義したフォーメーション名をリスト化
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, formations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFormation.setAdapter(adapter);

        spinnerFormation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFormation = parent.getItemAtPosition(position).toString();
                updateFormationDisplay(selectedFormation); // 選択されたフォーメーションで表示を更新
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 何もしない
            }
        });

        // アプリ起動時にデフォルトで最初のフォーメーションを表示
        if (!formations.isEmpty()) {
            spinnerFormation.setSelection(0); // 最初のアイテムを選択
        }
    }

    // AI診断のために仮の選手データを設定するメソッド
    // 実際には、ユーザーが選手を選択する機能や、APIから取得したチームの選手リストに基づいて、
    // ここに本物のPlayerオブジェクトが設定されるように実装する必要があります。
    private void setupDummyPlayers() {
        currentFormationPlayers.put("pos_gk", new LineupPlayer("GK", "アリソン", 1));
        currentFormationPlayers.put("pos_df1", new LineupPlayer("LB", "ロバートソン", 26));
        currentFormationPlayers.put("pos_df2", new LineupPlayer("CB", "ファン・ダイク", 4));
        currentFormationPlayers.put("pos_df3", new LineupPlayer("CB", "コナテ", 5));
        currentFormationPlayers.put("pos_df4", new LineupPlayer("RB", "アーノルド", 66));
        currentFormationPlayers.put("pos_mf1", new LineupPlayer("CMF", "遠藤", 3));
        currentFormationPlayers.put("pos_mf2", new LineupPlayer("CMF", "マクアリスター", 10));
        currentFormationPlayers.put("pos_mf3", new LineupPlayer("LMF", "ソボスライ", 8));
        currentFormationPlayers.put("pos_mf4", new LineupPlayer("RMF", "サラー", 11));
        currentFormationPlayers.put("pos_fw1", new LineupPlayer("CF", "ヌニェス", 9));
        currentFormationPlayers.put("pos_fw2", new LineupPlayer("ST", "ガクポ", 18));

        // 仮データ設定後、UIに反映
        updatePlayerPositionsUI();
    }


    // 選択されたフォーメーションに基づいてフィールド上の選手ボックスの表示を更新
    private void updateFormationDisplay(String formation) {
        hideAllPositionBoxes(); // まず全てのボックスを非表示にする

        List<String> positions = formationPositions.get(formation);
        if (positions != null) {
            // 各ポジションタイプのインデックスを追跡するためのカウンター
            Map<String, Integer> positionTypeCounters = new HashMap<>();
            positionTypeCounters.put("GK", 0);
            positionTypeCounters.put("DF", 0);
            positionTypeCounters.put("MF", 0);
            positionTypeCounters.put("FW", 0);

            for (String posType : positions) {
                int currentCount = positionTypeCounters.get(posType);
                View positionBox = getPositionBoxByDynamicIndex(posType, currentCount); // 動的なインデックスでViewを取得
                if (positionBox != null) {
                    positionBox.setVisibility(View.VISIBLE);
                    String positionBoxId = getResources().getResourceEntryName(positionBox.getId());
                    LineupPlayer player = currentFormationPlayers.get(positionBoxId);
                    updatePlayerBoxUI(positionBox, player, posType); // プレイヤーデータまたはポジションタイプを表示
                }
                positionTypeCounters.put(posType, currentCount + 1); // カウンターをインクリメント
            }
        }
    }

    // 現在のcurrentFormationPlayersマップに基づいて全ての選手ボックスのUIを更新
    private void updatePlayerPositionsUI() {
        // 全てのポジションボックスに対して、対応する選手データを設定
        updatePlayerBoxUI(pos_gk, currentFormationPlayers.get("pos_gk"), "GK");
        updatePlayerBoxUI(pos_df1, currentFormationPlayers.get("pos_df1"), "DF");
        updatePlayerBoxUI(pos_df2, currentFormationPlayers.get("pos_df2"), "DF");
        updatePlayerBoxUI(pos_df3, currentFormationPlayers.get("pos_df3"), "DF");
        updatePlayerBoxUI(pos_df4, currentFormationPlayers.get("pos_df4"), "DF");
        updatePlayerBoxUI(pos_mf1, currentFormationPlayers.get("pos_mf1"), "MF");
        updatePlayerBoxUI(pos_mf2, currentFormationPlayers.get("pos_mf2"), "MF");
        updatePlayerBoxUI(pos_mf3, currentFormationPlayers.get("pos_mf3"), "MF");
        updatePlayerBoxUI(pos_mf4, currentFormationPlayers.get("pos_mf4"), "MF");
        updatePlayerBoxUI(pos_fw1, currentFormationPlayers.get("pos_fw1"), "FW");
        updatePlayerBoxUI(pos_fw2, currentFormationPlayers.get("pos_fw2"), "FW");

        // スピナーの現在の選択に基づいて、表示・非表示を再度適用
        String currentSelectedFormation = (String) spinnerFormation.getSelectedItem();
        if(currentSelectedFormation != null) {
            updateFormationDisplay(currentSelectedFormation);
        }
    }


    // 個々の選手ボックスのUI (背番号と名前) を更新
    private void updatePlayerBoxUI(View positionBox, LineupPlayer player, String defaultPosText) {
        TextView textViewNumber = positionBox.findViewById(R.id.textViewPlayerNumber);
        TextView textViewName = positionBox.findViewById(R.id.textViewPlayerName);

        if (player != null) {
            textViewNumber.setText(String.valueOf(player.getShirtNumber()));
            textViewName.setText(player.getName());
        } else {
            // 選手が割り当てられていない場合、ポジションタイプ（GK, DFなど）を表示
            textViewNumber.setText(""); // 番号は空
            textViewName.setText(defaultPosText);
        }
    }

    // フォーメーションのポジションタイプとインデックスに基づいて対応するViewを取得するヘルパーメソッド
    // 例: DFの最初の要素なら pos_df1, DFの2番目の要素なら pos_df2 を返す
    private View getPositionBoxByDynamicIndex(String posType, int indexInType) {
        // このロジックは、activity_lineup.xmlで定義されたincludeタグのIDと、
        // setupFormations()で定義したポジションの並び順に厳密に依存します。
        // フォーメーションの複雑さが増す場合は、より汎用的なマッピングが必要です。
        switch (posType) {
            case "GK": return (indexInType == 0) ? pos_gk : null;
            case "DF":
                switch (indexInType) {
                    case 0: return pos_df1;
                    case 1: return pos_df2;
                    case 2: return pos_df3;
                    case 3: return pos_df4;
                    default: return null;
                }
            case "MF":
                switch (indexInType) {
                    case 0: return pos_mf1;
                    case 1: return pos_mf2;
                    case 2: return pos_mf3;
                    case 3: return pos_mf4;
                    default: return null;
                }
            case "FW":
                switch (indexInType) {
                    case 0: return pos_fw1;
                    case 1: return pos_fw2;
                    default: return null;
                }
            default: return null;
        }
    }

    // 全てのポジションボックスを非表示にする
    private void hideAllPositionBoxes() {
        pos_gk.setVisibility(View.GONE);
        pos_df1.setVisibility(View.GONE);
        pos_df2.setVisibility(View.GONE);
        pos_df3.setVisibility(View.GONE);
        pos_df4.setVisibility(View.GONE);
        pos_mf1.setVisibility(View.GONE);
        pos_mf2.setVisibility(View.GONE);
        pos_mf3.setVisibility(View.GONE);
        pos_mf4.setVisibility(View.GONE);
        pos_fw1.setVisibility(View.GONE);
        pos_fw2.setVisibility(View.GONE);
    }

    // AI診断ロジック
    private void fetchAIDiagnosis() {
        textViewAIDiagnosis.setText("AIが診断中です...");
        progressBarAIDiagnose.setVisibility(View.VISIBLE);

        String currentFormation = (String) spinnerFormation.getSelectedItem();
        if (currentFormation == null) {
            textViewAIDiagnosis.setText("フォーメーションが選択されていません。");
            progressBarAIDiagnose.setVisibility(View.GONE);
            return;
        }

        StringBuilder lineupDescription = new StringBuilder();
        lineupDescription.append("フォーメーション: ").append(currentFormation).append("\n");
        lineupDescription.append("選手リスト:\n");

        // 各ポジションに割り当てられた選手情報を収集
        // currentFormationPlayersマップは、全てのポジションボックスIDと対応する選手を保持している想定
        // 実際には、フォーメーション表示中の選手のみを収集するように調整するべきかもしれません
        List<String> visiblePositions = formationPositions.get(currentFormation);
        if (visiblePositions != null) {
            Map<String, Integer> positionTypeCounters = new HashMap<>();
            positionTypeCounters.put("GK", 0);
            positionTypeCounters.put("DF", 0);
            positionTypeCounters.put("MF", 0);
            positionTypeCounters.put("FW", 0);

            for (String posType : visiblePositions) {
                int currentCount = positionTypeCounters.get(posType);
                View positionBox = getPositionBoxByDynamicIndex(posType, currentCount);
                if (positionBox != null && positionBox.getVisibility() == View.VISIBLE) {
                    String positionBoxId = getResources().getResourceEntryName(positionBox.getId());
                    LineupPlayer player = currentFormationPlayers.get(positionBoxId);
                    if (player != null) {
                        lineupDescription.append("- ").append(player.getPositionType())
                                .append(" (背番号: ").append(player.getShirtNumber()).append("): ")
                                .append(player.getName()).append("\n");
                    } else {
                        lineupDescription.append("- ").append(posType).append(" (選手未指定)\n");
                    }
                }
                positionTypeCounters.put(posType, currentCount + 1);
            }
        }


        String prompt = "あなたはサッカーの戦術アナリストです。以下のスタメン構成について、戦術的な強み、弱み、試合展開における予想される動きを500文字程度で簡潔に診断・分析してください。\n\n" +
                lineupDescription.toString() + "\n" +
                "重要な注意: このAIはリアルタイムの選手能力やコンディションにはアクセスできません。一般的なサッカーの知識と提供されたスタメン構成のみに基づいて診断を生成してください。";


        GeminiApiService geminiService = GeminiApiClient.getClient().create(GeminiApiService.class);
        GeminiRequest request = new GeminiRequest(
                Collections.singletonList(
                        new GeminiRequest.Content(
                                Collections.singletonList(
                                        new GeminiRequest.Part(prompt)
                                )
                        )
                )
        );

        Call<GeminiResponse> call = geminiService.generateContent(BuildConfig.GEMINI_API_KEY, request);

        call.enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeminiResponse> call, @NonNull Response<GeminiResponse> response) {
                progressBarAIDiagnose.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().getCandidates().isEmpty()) {
                    String aiText = response.body().getCandidates().get(0).getContent().getParts().get(0).getText();
                    textViewAIDiagnosis.setText(aiText);
                    Log.d(TAG, "AI Diagnosis Response: " + aiText);
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing AI error body", e);
                    }
                    Log.e(TAG, "Failed to get AI diagnosis: " + response.code() + " - " + errorBody);
                    textViewAIDiagnosis.setText("AI診断の取得に失敗しました (APIエラー: " + response.code() + ")。");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeminiResponse> call, @NonNull Throwable t) {
                progressBarAIDiagnose.setVisibility(View.GONE);
                Log.e(TAG, "AI diagnosis network error", t);
                textViewAIDiagnosis.setText("AI診断の取得に失敗しました (通信エラー)。");
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