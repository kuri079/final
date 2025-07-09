package com.example.kicklog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kicklog.model.Scorer;
import com.example.kicklog.R;

import java.util.List;

public class ScorerAdapter extends RecyclerView.Adapter<ScorerAdapter.ScorerViewHolder> {

    private List<Scorer> scorerList;

    public ScorerAdapter(List<Scorer> scorerList) {
        this.scorerList = scorerList;
    }

    @NonNull
    @Override
    public ScorerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scorer, parent, false);
        return new ScorerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScorerViewHolder holder, int position) {
        Scorer scorer = scorerList.get(position);
        holder.playerName.setText(scorer.getPlayer().getName());
        holder.teamName.setText(scorer.getTeam().getName());
        holder.goals.setText("Goals: " + scorer.getGoals());
    }

    @Override
    public int getItemCount() {
        return scorerList.size();
    }

    static class ScorerViewHolder extends RecyclerView.ViewHolder {
        TextView playerName;
        TextView teamName;
        TextView goals;

        ScorerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.textViewPlayerName);
            teamName = itemView.findViewById(R.id.textViewTeamName);
            goals = itemView.findViewById(R.id.textViewGoals);
        }
    }
}