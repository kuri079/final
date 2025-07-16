package com.example.kicklog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kicklog.model.Player;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private final List<Player> playerList;
    private final OnPlayerClickListener listener;

    public interface OnPlayerClickListener {
        void onPlayerClick(Player player);
    }

    public PlayerAdapter(List<Player> playerList, OnPlayerClickListener listener) {
        this.playerList = playerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.bind(player, listener);
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNumber, textViewName, textViewPosition;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewPlayerNumber);
            textViewName = itemView.findViewById(R.id.textViewPlayerName);
            textViewPosition = itemView.findViewById(R.id.textViewPlayerPosition);
        }

        public void bind(final Player player, final OnPlayerClickListener listener) {
            textViewName.setText(player.getName());

            // ポジション情報があれば表示、なければ非表示
            if (player.getPosition() != null && !player.getPosition().isEmpty()) {
                textViewPosition.setVisibility(View.VISIBLE);
                textViewPosition.setText(player.getPosition());
                textViewPosition.setText(PositionUtils.abbreviate(player.getPosition()));
            } else {
                textViewPosition.setVisibility(View.GONE);
            }

            // 背番号情報があれば表示
            if (player.getShirtNumber() != null) {
                textViewNumber.setText(String.valueOf(player.getShirtNumber()));
            } else {
                textViewNumber.setText(""); // なければ空文字
            }

            // リスナーが設定されていればクリック処理を有効化
            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onPlayerClick(player));
            }
        }
    }
}