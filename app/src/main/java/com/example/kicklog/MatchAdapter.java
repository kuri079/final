// 新規作成: com/example/kicklog/MatchAdapter.java
package com.example.kicklog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kicklog.model.FullTimeScore;
import com.example.kicklog.model.Match;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private final List<Match> matchList;

    public MatchAdapter(List<Match> matchList) {
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matchList.get(position);
        holder.homeTeam.setText(match.getHomeTeam().getName());
        holder.awayTeam.setText(match.getAwayTeam().getName());
        holder.matchDate.setText(match.getUtcDate()); // TODO: 日付フォーマットの変換

        FullTimeScore score = match.getScore().getFullTime();
        if ("FINISHED".equals(match.getStatus()) && score != null) {
            String scoreText = score.getHome() + " - " + score.getAway();
            holder.score.setText(scoreText);
        } else {
            holder.score.setText("vs");
        }
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView homeTeam, awayTeam, score, matchDate;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            homeTeam = itemView.findViewById(R.id.textViewHomeTeam);
            awayTeam = itemView.findViewById(R.id.textViewAwayTeam);
            score = itemView.findViewById(R.id.textViewScore);
            matchDate = itemView.findViewById(R.id.textViewMatchDate);
        }
    }
}