package com.example.kicklog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kicklog.model.MatchDetailResponse;
import com.google.gson.Gson;

public class StatsFragment extends Fragment {
    private static final String ARG_DETAILS_JSON = "details_json";

    public static StatsFragment newInstance(MatchDetailResponse details) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DETAILS_JSON, new Gson().toJson(details));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView cardViewGoals = view.findViewById(R.id.cardViewGoals);
        RecyclerView recyclerViewGoals = view.findViewById(R.id.recyclerViewGoals);
        CardView cardViewBookings = view.findViewById(R.id.cardViewBookings);
        RecyclerView recyclerViewBookings = view.findViewById(R.id.recyclerViewBookings);

        recyclerViewGoals.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            String detailsJson = getArguments().getString(ARG_DETAILS_JSON);
            MatchDetailResponse details = new Gson().fromJson(detailsJson, MatchDetailResponse.class);

            if (details != null) {
                if (details.getGoals() != null && !details.getGoals().isEmpty()) {
                    cardViewGoals.setVisibility(View.VISIBLE);
                    recyclerViewGoals.setAdapter(new GoalAdapter(details.getGoals()));
                } else {
                    cardViewGoals.setVisibility(View.GONE);
                }

                if (details.getBookings() != null && !details.getBookings().isEmpty()) {
                    cardViewBookings.setVisibility(View.VISIBLE);
                    recyclerViewBookings.setAdapter(new BookingAdapter(details.getBookings()));
                } else {
                    cardViewBookings.setVisibility(View.GONE);
                }
            }
        }
    }
}