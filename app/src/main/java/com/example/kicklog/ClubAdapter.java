// com/example/kicklog/ClubAdapter.java を修正
package com.example.kicklog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kicklog.model.Team;

import java.util.List;

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ClubViewHolder> {

    private final List<Team> teamList;
    private OnTeamClickListener listener; // ◀️ リスナーを保持する変数を追加

    // 🔽 クリックリスナー用のインターフェースを定義 🔽
    public interface OnTeamClickListener {
        void onTeamClick(Team team);
    }

    // 🔽 コンストラクタでリスナーを受け取るように変更 🔽
    public ClubAdapter(List<Team> teamList, OnTeamClickListener listener) {
        this.teamList = teamList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_club, parent, false);
        return new ClubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubViewHolder holder, int position) {
        Team team = teamList.get(position);
        // 🔽 ViewHolderにリスナーを渡す 🔽
        holder.bind(team, listener);
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    static class ClubViewHolder extends RecyclerView.ViewHolder {
        ImageView clubCrest;
        TextView clubName;

        public ClubViewHolder(@NonNull View itemView) {
            super(itemView);
            clubCrest = itemView.findViewById(R.id.imageViewClubCrest);
            clubName = itemView.findViewById(R.id.textViewClubName);
        }

        // 🔽 bindメソッドを追加して、データとクリック処理を設定 🔽
        public void bind(final Team team, final OnTeamClickListener listener) {
            clubName.setText(team.getName());
            Glide.with(itemView.getContext())
                    .load(team.getCrest())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(clubCrest);

            itemView.setOnClickListener(v -> listener.onTeamClick(team));
        }
    }
}