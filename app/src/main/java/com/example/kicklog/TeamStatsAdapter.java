// com/example/kicklog/TeamStatsAdapter.java を完成させる
package com.example.kicklog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kicklog.model.TableEntry;
import java.util.List;

public class TeamStatsAdapter extends RecyclerView.Adapter<TeamStatsAdapter.ViewHolder> {

    private final List<TableEntry> statsList;

    /**
     * コンストラクタでデータリストを受け取る
     * これで 'might not have been initialized' エラーが解決します
     */
    public TeamStatsAdapter(List<TableEntry> statsList) {
        this.statsList = statsList;
    }

    /**
     * ViewHolder を作成する
     * これで 'onCreateViewHolder' の未実装エラーが解決します
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_season_stat, parent, false);
        return new ViewHolder(view);
    }

    /**
     * ViewHolder にデータをバインド（設定）する
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TableEntry entry = statsList.get(position);

        // APIからシーズン情報を取得できないため、年などから仮で設定します
        // holder.textViewSeason.setText(...);

        String statsText = "順位: " + entry.getPosition() + "位 | " +
                entry.getPlayedGames() + "試合 " +
                entry.getWon() + "勝 " +
                entry.getDraw() + "分 " +
                entry.getLost() + "敗 | " +
                "勝ち点: " + entry.getPoints();
        holder.textViewStats.setText(statsText);

        int goalDifference = entry.getGoalsFor() - entry.getGoalsAgainst();
        String goalsText = "得点: " + entry.getGoalsFor() + " | " +
                "失点: " + entry.getGoalsAgainst() + " | " +
                "得失点差: " + (goalDifference > 0 ? "+" : "") + goalDifference;
        holder.textViewGoals.setText(goalsText);
    }

    /**
     * リストのアイテム数を返す
     */
    @Override
    public int getItemCount() {
        return statsList.size();
    }

    /**
     * ViewHolderクラス
     * これで 'Cannot resolve symbol 'ViewHolder'' エラーが解決します
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewSeason;
        final TextView textViewStats;
        final TextView textViewGoals;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSeason = itemView.findViewById(R.id.textViewSeason);
            textViewStats = itemView.findViewById(R.id.textViewStats);
            textViewGoals = itemView.findViewById(R.id.textViewGoals);
        }
    }
}