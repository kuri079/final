package com.example.kicklog.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kicklog.R;
import com.example.kicklog.model.TeamResponse;   // ← ここを TeamDetailResponse から変更
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.VH> {

    private final List<TeamResponse.Squad> list;  // ← 型を統一

    public PlayerAdapter(List<TeamResponse.Squad> list){
        this.list = list;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v){
        View vItem = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_player, p, false);
        return new VH(vItem);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos){
        
        TeamResponse.Squad s = list.get(pos);
        h.name.setText(s.name);
        h.pos.setText(s.position);
        h.nat.setText(s.nationality);
    }

    @Override public int getItemCount(){ return list.size(); }

    static class VH extends RecyclerView.ViewHolder{
        TextView name, pos, nat;
        VH(View v){
            super(v);
            name = v.findViewById(R.id.playerName);
            pos  = v.findViewById(R.id.playerPosition);
            nat  = v.findViewById(R.id.playerNationality);
        }
    }
}
