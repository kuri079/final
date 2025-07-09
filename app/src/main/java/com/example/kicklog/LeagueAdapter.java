// 新規作成: com/example/kicklog/LeagueAdapter.java
package com.example.kicklog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kicklog.model.League;
import java.util.List;

public class LeagueAdapter extends RecyclerView.Adapter<LeagueAdapter.LeagueViewHolder> {

    private final List<League> leagues;
    private final OnLeagueClickListener listener;

    public interface OnLeagueClickListener {
        void onLeagueClick(League league);
    }

    public LeagueAdapter(List<League> leagues, OnLeagueClickListener listener) {
        this.leagues = leagues;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LeagueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new LeagueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeagueViewHolder holder, int position) {
        League league = leagues.get(position);
        holder.bind(league, listener);
    }

    @Override
    public int getItemCount() {
        return leagues.size();
    }

    static class LeagueViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public LeagueViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }

        public void bind(final League league, final OnLeagueClickListener listener) {
            textView.setText(league.getName());
            itemView.setOnClickListener(v -> listener.onLeagueClick(league));
        }
    }
}