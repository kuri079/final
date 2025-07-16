package com.example.kicklog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.kicklog.model.MatchDetailResponse;
import com.example.kicklog.model.Player;
import com.google.gson.Gson;
import java.util.List;

public class LineupFragment extends Fragment {

    private static final String ARG_DETAILS_JSON = "details_json";

    public static LineupFragment newInstance(MatchDetailResponse details) {
        LineupFragment fragment = new LineupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DETAILS_JSON, new Gson().toJson(details));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lineup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvHomeLineup = view.findViewById(R.id.textViewHomeLineup);
        TextView tvHomeBench = view.findViewById(R.id.textViewHomeBench);
        TextView tvHomeCoach = view.findViewById(R.id.textViewHomeCoach);
        TextView tvAwayLineup = view.findViewById(R.id.textViewAwayLineup);
        TextView tvAwayBench = view.findViewById(R.id.textViewAwayBench);
        TextView tvAwayCoach = view.findViewById(R.id.textViewAwayCoach);

        if (getArguments() != null) {
            String detailsJson = getArguments().getString(ARG_DETAILS_JSON);
            MatchDetailResponse details = new Gson().fromJson(detailsJson, MatchDetailResponse.class);

            if (details != null) {
                // Home Team
                MatchDetailResponse.TeamLineup homeTeam = details.getHomeTeam();
                if (homeTeam != null) {
                    tvHomeLineup.setText(formatPlayerList(homeTeam.getLineup()));
                    tvHomeBench.setText(formatPlayerList(homeTeam.getBench()));
                    if (homeTeam.getCoach() != null) {
                        tvHomeCoach.setText(homeTeam.getCoach().getName());
                    } else {
                        tvHomeCoach.setText("データなし");
                    }
                }
                // Away Team
                MatchDetailResponse.TeamLineup awayTeam = details.getAwayTeam();
                if (awayTeam != null) {
                    tvAwayLineup.setText(formatPlayerList(awayTeam.getLineup()));
                    tvAwayBench.setText(formatPlayerList(awayTeam.getBench()));
                    if(awayTeam.getCoach() != null) {
                        tvAwayCoach.setText(awayTeam.getCoach().getName());
                    } else {
                        tvAwayCoach.setText("データなし");
                    }
                }
            }
        }
    }

    private String formatPlayerList(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return "データなし";
        }
        StringBuilder sb = new StringBuilder();
        for (Player player : players) {
            String number = (player.getShirtNumber() != null) ? player.getShirtNumber() + ". " : "";
            sb.append(number).append(player.getName()).append("\n");
        }
        return sb.toString().trim();
    }
}