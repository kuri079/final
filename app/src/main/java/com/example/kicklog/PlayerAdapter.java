// 新規作成: com/example/kicklog/PlayerAdapter.java
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

    @NonNull @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.bind(player, listener);
    }

    @Override
    public int getItemCount() { return playerList.size(); }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
        public void bind(final Player player, final OnPlayerClickListener listener) {
            textView.setText(player.getName());
            itemView.setOnClickListener(v -> listener.onPlayerClick(player));
        }
    }
}