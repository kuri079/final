package com.example.kicklog;

import android.content.Context; // Contextを使うために追加
import android.content.Intent; // Intentを使うために追加
import android.graphics.Color; // 色指定のために追加
import android.util.Log; // Logのために追加
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // ImageViewを使うために追加
import android.widget.LinearLayout; // LinearLayoutを使うために追加 (背景変更用)
import android.widget.TextView;
import android.widget.Toast; // Toastを使うために追加

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Glideを使うために追加
import com.example.kicklog.model.FullTimeScore;
import com.example.kicklog.model.Match;
// MatchDetailActivityでBuildConfigを使うため、ここはコメントアウトしてあります。
// import com.example.kicklog.network.FootballDataApiService; // BuildConfigのため

import java.text.ParseException; // 日付フォーマットのため
import java.text.SimpleDateFormat; // 日付フォーマットのため
import java.util.Date; // 日付フォーマットのため
import java.util.List;
import java.util.Locale; // 日付フォーマットのため
import java.util.TimeZone; // 日付フォーマットのため

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private static final String TAG = "MatchAdapter"; // Logタグを追加
    private final List<Match> matchList;
    // OnMatchClickListener は直接使わず、ViewHolder内でIntentを起動するように変更します
    // private final OnMatchClickListener listener;
    private final Context context; // Contextを保持するように変更

    // OnMatchClickListener インターフェースはAdapter内で直接使わないので、削除または変更します。
    // public interface OnMatchClickListener {
    //     void onMatchClick(Match match);
    // }

    // コンストラクタを変更: Contextを受け取るようにする
    public MatchAdapter(Context context, List<Match> matchList) {
        this.context = context;
        this.matchList = matchList;
        // this.listener = listener; // listenerは不要になる
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

        // 試合日時
        holder.textViewMatchDate.setText(formatDate(match.getUtcDate()));

        // チーム名とエンブレム
        if (match.getHomeTeam() != null) {
            holder.textViewHomeTeamName.setText(match.getHomeTeam().getName());
            Glide.with(context) // contextを使用
                    .load(match.getHomeTeam().getCrest())
                    .placeholder(R.drawable.ic_team_placeholder) // 仮のプレースホルダー画像があれば設定
                    .error(R.drawable.ic_team_placeholder) // エラー時の画像
                    .into(holder.imageViewHomeCrest);
        } else {
            holder.textViewHomeTeamName.setText("不明");
            holder.imageViewHomeCrest.setImageResource(R.drawable.ic_team_placeholder);
        }

        if (match.getAwayTeam() != null) {
            holder.textViewAwayTeamName.setText(match.getAwayTeam().getName());
            Glide.with(context) // contextを使用
                    .load(match.getAwayTeam().getCrest())
                    .placeholder(R.drawable.ic_team_placeholder)
                    .error(R.drawable.ic_team_placeholder)
                    .into(holder.imageViewAwayCrest);
        } else {
            holder.textViewAwayTeamName.setText("不明");
            holder.imageViewAwayCrest.setImageResource(R.drawable.ic_team_placeholder);
        }

        // スコア表示
        String status = match.getStatus();
        if (status != null && status.equals("FINISHED")) {
            FullTimeScore fullTimeScore = match.getScore().getFullTime();
            if (fullTimeScore != null && fullTimeScore.getHome() != null && fullTimeScore.getAway() != null) {
                String scoreText = fullTimeScore.getHome() + " - " + fullTimeScore.getAway();
                holder.textViewScore.setText(scoreText);
            } else {
                holder.textViewScore.setText(" - ");
            }
        } else {
            holder.textViewScore.setText("VS"); // 試合前は "VS"
        }

        // ★★★ 背景色の変更ロジック ★★★
        if (status != null) {
            switch (status) {
                case "FINISHED":
                    holder.matchItemContainer.setBackgroundColor(Color.parseColor("#E0E0E0")); // 終了した試合は灰色
                    break;
                case "SCHEDULED":
                case "TIMED":
                    holder.matchItemContainer.setBackgroundColor(Color.parseColor("#E8F5E9")); // 予定されている試合は薄い緑
                    break;
                case "LIVE": // 試合中のステータスもあれば
                    holder.matchItemContainer.setBackgroundColor(Color.parseColor("#FFFDE7")); // ライブ中の試合は薄い黄色
                    break;
                default:
                    holder.matchItemContainer.setBackgroundColor(Color.WHITE); // その他のステータスは白
                    break;
            }
        } else {
            holder.matchItemContainer.setBackgroundColor(Color.WHITE); // ステータス不明は白
        }

        // アイテムクリックリスナー
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MatchDetailActivity.class);
            intent.putExtra(MatchDetailActivity.EXTRA_MATCH_ID, match.getId());

            // ★★★ ここでリーグIDを正しく渡す必要があります ★★★
            // matchオブジェクトからリーグID（competition.code）を取得
            if (match.getCompetition() != null && match.getCompetition().getCode() != null) {
                intent.putExtra(MatchDetailActivity.EXTRA_LEAGUE_ID, match.getCompetition().getCode());
                Log.d(TAG, "Passing League ID: " + match.getCompetition().getCode() + " for match " + match.getId());
            } else {
                Log.e(TAG, "League ID is null or competition object is null for match " + match.getId() + ". Cannot pass to MatchDetailActivity.");
                Toast.makeText(context, "リーグ情報が取得できません。", Toast.LENGTH_SHORT).show();
                return; // リーグIDがない場合は詳細画面を開かない
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMatchDate;
        ImageView imageViewHomeCrest; // 追加
        TextView textViewHomeTeamName; // IDをitem_match.xmlに合わせる
        TextView textViewScore;
        ImageView imageViewAwayCrest; // 追加
        TextView textViewAwayTeamName; // IDをitem_match.xmlに合わせる
        LinearLayout matchItemContainer; // 追加

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMatchDate = itemView.findViewById(R.id.textViewMatchDate);
            imageViewHomeCrest = itemView.findViewById(R.id.imageViewHomeCrest); // 追加
            textViewHomeTeamName = itemView.findViewById(R.id.textViewHomeTeamName); // IDを修正
            textViewScore = itemView.findViewById(R.id.textViewScore);
            imageViewAwayCrest = itemView.findViewById(R.id.imageViewAwayCrest); // 追加
            textViewAwayTeamName = itemView.findViewById(R.id.textViewAwayTeamName); // IDを修正
            matchItemContainer = itemView.findViewById(R.id.match_item_container); // 追加
        }

        // bindメソッドは削除し、onBindViewHolder内で直接ロジックを記述します。
        // これまでのbindメソッドのロジックはonBindViewHolderに移されます。
    }

    // 日付フォーマットのヘルパーメソッド (MatchDetailActivityと同じロジック)
    private String formatDate(String utcDate) {
        if(utcDate == null || utcDate.isEmpty()) return "日時未定";
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = utcFormat.parse(utcDate);
            if(date == null) return utcDate;

            SimpleDateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPAN);
            localFormat.setTimeZone(TimeZone.getDefault());
            return localFormat.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "Date format parsing error for: " + utcDate, e);
            return utcDate;
        }
    }
}