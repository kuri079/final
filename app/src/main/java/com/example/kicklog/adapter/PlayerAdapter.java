import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kicklog.R;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {
    private List<PlayerResponse.PlayerData> playerList;

    public PlayerAdapter(List<PlayerResponse.PlayerData> players) {
        this.playerList = players;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlayerResponse.PlayerData data = playerList.get(position);
        holder.name.setText(data.player.name);
        holder.goals.setText("Goals: " + data.statistics.get(0).goals.total);
        Glide.with(holder.itemView.getContext()).load(data.player.photo).into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, goals;
        ImageView photo;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.playerName);
            goals = itemView.findViewById(R.id.playerGoals);
            photo = itemView.findViewById(R.id.playerPhoto);
        }
    }
}
