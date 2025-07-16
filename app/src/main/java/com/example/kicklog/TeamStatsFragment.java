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
import com.example.kicklog.model.Standing;
import com.example.kicklog.model.StandingsResponse;
import com.example.kicklog.model.TableEntry;
import com.google.gson.Gson;

public class TeamStatsFragment extends Fragment {

    private static final String ARG_STANDINGS_JSON = "standings_json";
    private static final String ARG_DETAILS_JSON = "details_json";

    public static TeamStatsFragment newInstance(StandingsResponse standings, MatchDetailResponse details) {
        TeamStatsFragment fragment = new TeamStatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STANDINGS_JSON, new Gson().toJson(standings));
        args.putString(ARG_DETAILS_JSON, new Gson().toJson(details));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_team_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textViewHomeStats = view.findViewById(R.id.textViewHomeStats);
        TextView textViewAwayStats = view.findViewById(R.id.textViewAwayStats);

        if (getArguments() != null) {
            String standingsJson = getArguments().getString(ARG_STANDINGS_JSON);
            String detailsJson = getArguments().getString(ARG_DETAILS_JSON);

            StandingsResponse standings = new Gson().fromJson(standingsJson, StandingsResponse.class);
            MatchDetailResponse details = new Gson().fromJson(detailsJson, MatchDetailResponse.class);

            if (standings != null && details != null) {
                TableEntry homeTeamEntry = findTeamInStandings(standings, details.getHomeTeam().getId());
                TableEntry awayTeamEntry = findTeamInStandings(standings, details.getAwayTeam().getId());

                textViewHomeStats.setText(formatTeamStats(details.getHomeTeam(), homeTeamEntry));
                textViewAwayStats.setText(formatTeamStats(details.getAwayTeam(), awayTeamEntry));
            }
        }
    }

    private TableEntry findTeamInStandings(StandingsResponse standings, int teamId) {
        if (standings == null || standings.getStandings() == null) return null;
        for (Standing standing : standings.getStandings()) {
            if ("TOTAL".equals(standing.getType())) {
                if (standing.getTable() != null) {
                    for (TableEntry entry : standing.getTable()) {
                        if (entry.getTeam() != null && entry.getTeam().getId() == teamId) {
                            return entry;
                        }
                    }
                }
            }
        }
        return null;
    }

    private String formatTeamStats(MatchDetailResponse.TeamLineup team, TableEntry entry) {
        if (team == null) return "データなし";
        if (entry == null) return team.getName() + "\n(順位情報なし)";

        String teamName = team.getName();
        String position = "順位: " + entry.getPosition() + "位";
        String record = entry.getWon() + "勝 " + entry.getDraw() + "分 " + entry.getLost() + "敗";
        String goals = "得点/失点: " + entry.getGoalsFor() + "/" + entry.getGoalsAgainst();

        return teamName + "\n" + position + "\n" + record + "\n" + goals;
    }
}