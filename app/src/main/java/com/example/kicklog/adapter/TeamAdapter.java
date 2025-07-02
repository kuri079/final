package com.example.kicklog.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.kicklog.ClubMenuActivity;
import com.example.kicklog.R;
import com.example.kicklog.model.Team;
import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.VH> {

    private final List<Team> list;
    public TeamAdapter(List<Team> list){ this.list = list; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v){
        return new VH(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_team, p, false));
    }
    @Override public void onBindViewHolder(@NonNull VH h,int pos){
        Team t = list.get(pos);
        h.name.setText(t.name);
        h.tla.setText(t.tla);
        Glide.with(h.itemView).load(t.crest).into(h.logo);

        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), ClubMenuActivity.class);
            i.putExtra("teamId", t.id);
            i.putExtra("teamName", t.name);
            v.getContext().startActivity(i);
        });
    }
    @Override public int getItemCount(){ return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView name,tla; ImageView logo;
        VH(View v){ super(v);
            name=v.findViewById(R.id.teamName);
            tla =v.findViewById(R.id.teamTla);
            logo=v.findViewById(R.id.teamLogo);}
    }
}
