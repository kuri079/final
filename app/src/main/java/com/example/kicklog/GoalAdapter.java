package com.example.kicklog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kicklog.model.Goal;
import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {
    private final List<Goal> goalList;
    public GoalAdapter(List<Goal> goalList) { this.goalList = goalList; }

    @NonNull @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goalList.get(position);
        String goalText = goal.getMinute() + "' " + goal.getScorer().getName();
        holder.goalInfo.setText(goalText);
    }

    @Override
    public int getItemCount() { return goalList.size(); }

    static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView goalInfo;
        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            goalInfo = itemView.findViewById(R.id.textViewGoalInfo);
        }
    }
}