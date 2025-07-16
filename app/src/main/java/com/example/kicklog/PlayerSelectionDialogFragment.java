// 新規作成: com/example/kicklog/PlayerSelectionDialogFragment.java
package com.example.kicklog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kicklog.model.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class PlayerSelectionDialogFragment extends DialogFragment {

    private static final String ARG_PLAYERS = "players_json";
    private PlayerAdapter.OnPlayerClickListener playerClickListener;

    // ダイアログを作成するための新しい方法
    public static PlayerSelectionDialogFragment newInstance(List<Player> players) {
        PlayerSelectionDialogFragment fragment = new PlayerSelectionDialogFragment();
        Bundle args = new Bundle();
        // PlayerのリストをJSON文字列に変換して渡す
        args.putString(ARG_PLAYERS, new Gson().toJson(players));
        fragment.setArguments(args);
        return fragment;
    }

    // 選手が選択されたことを通知するリスナーを設定
    public void setOnPlayerClickListener(PlayerAdapter.OnPlayerClickListener listener) {
        this.playerClickListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_player_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewDialogPlayers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            String playersJson = getArguments().getString(ARG_PLAYERS);
            Type type = new TypeToken<List<Player>>() {}.getType();
            List<Player> players = new Gson().fromJson(playersJson, type);

            // PlayerAdapterを使い、クリックされたら自身を閉じるように設定
            PlayerAdapter adapter = new PlayerAdapter(players, player -> {
                if (playerClickListener != null) {
                    playerClickListener.onPlayerClick(player);
                }
                dismiss(); // 選手を選択したらダイアログを閉じる
            });
            recyclerView.setAdapter(adapter);
        }
    }
}