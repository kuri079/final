package com.example.kicklog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.kicklog.model.MatchDetailResponse;
import com.example.kicklog.model.StandingsResponse;

public class MatchDetailPagerAdapter extends FragmentStateAdapter {

    private final MatchDetailResponse matchDetails;
    private final StandingsResponse standings;

    public MatchDetailPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                                   MatchDetailResponse matchDetails,
                                   StandingsResponse standings) {
        super(fragmentActivity);
        this.matchDetails = matchDetails;
        this.standings = standings;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return LineupFragment.newInstance(matchDetails);
            case 1:
                return TeamStatsFragment.newInstance(standings, matchDetails);
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}