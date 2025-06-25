package com.example.kicklog.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.kicklog.R;
import com.example.kicklog.model.PlayerResponse;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {
    private List<PlayerResponse.PlayerData> playerList;

    public PlayerAdapter(List<PlayerResponse.PlayerData> playerList) {
        this.playerList = playerList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlayerResponse.PlayerData playerData = playerList.get(position);
        holder.name.setText(playerData.player.name);
        holder.goals.setText("得点: " + playerData.statistics.get(0).goals.total);
        Glide.with(holder.itemView.getContext())
                .load(playerData.player.photo)
                .into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, goals;
        ImageView photo;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.playerName);
            goals = itemView.findViewById(R.id.playerGoals);
            photo = itemView.findViewById(R.id.playerPhoto);
        }
    }
}
