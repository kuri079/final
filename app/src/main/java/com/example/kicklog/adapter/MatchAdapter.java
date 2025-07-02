// app/src/main/java/com/example/kicklog/adapter/MatchAdapter.java
package com.example.kicklog.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kicklog.R;
import com.example.kicklog.model.MatchResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * チームの試合一覧を 1 行ずつ表示するアダプター
 */
public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    private final List<MatchResponse.Match> matchList;
    private final SimpleDateFormat inFmt  =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);      // API の日付
    private final SimpleDateFormat outFmt =
            new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.JAPAN);           // 表示用

    public MatchAdapter(List<MatchResponse.Match> matchList) {
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        MatchResponse.Match m = matchList.get(pos);

        // スコアが未確定（null）の場合は “vs”
        String score = (m.score.fullTime.home == null)
                ? " vs "
                : m.score.fullTime.home + " - " + m.score.fullTime.away;

        h.txtTeams.setText(
                m.homeTeam.shortName + "  " + score + "  " + m.awayTeam.shortName);

        // 日付フォーマット
        try {
            h.txtDate.setText(outFmt.format(inFmt.parse(m.utcDate)));
        } catch (ParseException e) {
            h.txtDate.setText(m.utcDate);
        }
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    /* ─────────────  ViewHolder  ───────────── */
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView txtTeams;
        final TextView txtDate;

        ViewHolder(View v) {
            super(v);
            txtTeams = v.findViewById(R.id.txtTeams);
            txtDate  = v.findViewById(R.id.txtDate);
        }
    }
}
