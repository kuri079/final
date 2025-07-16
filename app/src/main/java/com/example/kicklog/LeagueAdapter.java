package com.example.kicklog;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kicklog.model.League;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeagueAdapter extends RecyclerView.Adapter<LeagueAdapter.LeagueViewHolder> {

    private final List<League> leagueList;
    private final OnLeagueClickListener listener;
    private final Map<String, String> leagueColors = new HashMap<>();

    public interface OnLeagueClickListener {
        void onLeagueClick(League league);
    }

    public LeagueAdapter(List<League> leagueList, OnLeagueClickListener listener) {
        this.leagueList = leagueList;
        this.listener = listener;
        initializeColors();
    }

    private void initializeColors() {
        // 各リーグのテーマカラー（16進数）
        leagueColors.put("PL", "#37003c");   // プレミアリーグ (紫)
        leagueColors.put("BL1", "#d20515"); // ブンデスリーガ (赤)
        leagueColors.put("SA", "#0053a3");    // セリエA (青)
        leagueColors.put("PD", "#ee8707");    // ラ・リーガ (オレンジ)
        leagueColors.put("FL1", "#DAA520"); // リーグ・アン (黄)
    }

    @NonNull
    @Override
    public LeagueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_league, parent, false);
        return new LeagueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeagueViewHolder holder, int position) {
        League league = leagueList.get(position);
        holder.bind(league, listener, leagueColors);
    }

    @Override
    public int getItemCount() {
        return leagueList.size();
    }

    static class LeagueViewHolder extends RecyclerView.ViewHolder {
        TextView leagueName;
        ImageView leagueEmblem;
        View leagueColorView;

        public LeagueViewHolder(@NonNull View itemView) {
            super(itemView);
            leagueName = itemView.findViewById(R.id.textViewLeagueName);
            leagueEmblem = itemView.findViewById(R.id.imageViewLeagueEmblem);
            leagueColorView = itemView.findViewById(R.id.viewLeagueColor);
        }

        public void bind(final League league, final OnLeagueClickListener listener, final Map<String, String> colors) {
            leagueName.setText(league.getName());

            // Glideを使ってエンブレムを読み込む（APIからURLが取得できる場合）
            // Glide.with(itemView.getContext()).load(league.getEmblem()).into(leagueEmblem);

            // リーグのテーマカラーを設定
            String colorString = colors.get(league.getCode());
            if (colorString != null) {
                try {
                    leagueColorView.setBackgroundColor(Color.parseColor(colorString));
                } catch (IllegalArgumentException e) {
                    leagueColorView.setBackgroundColor(Color.LTGRAY); // パース失敗時はデフォルト色
                }
            } else {
                leagueColorView.setBackgroundColor(Color.LTGRAY); // マップにない場合もデフォルト色
            }

            itemView.setOnClickListener(v -> listener.onLeagueClick(league));
        }
    }
}