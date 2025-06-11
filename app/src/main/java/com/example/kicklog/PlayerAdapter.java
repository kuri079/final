package com.example.kicklog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {
    private List<SquadMember> playerList;

    public PlayerAdapter(List<SquadMember> list) {
        this.playerList = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameView, posView, numberView;

        public ViewHolder(View v) {
            super(v);
            nameView = v.findViewById(R.id.player_name);
            posView = v.findViewById(R.id.player_position);
            numberView = v.findViewById(R.id.player_number);
        }
    }

    @Override
    public PlayerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {
        SquadMember p = playerList.get(pos);
        h.nameView.setText(p.getName());
        h.posView.setText(p.getPosition());
        h.numberView.setText("背番号：" + p.getShirtNumber());
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }
}

