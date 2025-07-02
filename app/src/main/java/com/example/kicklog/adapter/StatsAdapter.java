package com.example.kicklog.adapter;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kicklog.R;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.VH> {

    private final String[] keys;
    private final String[] vals;

    public StatsAdapter(Map<String,String> map){
        keys = map.keySet().toArray(new String[0]);
        vals = map.values().toArray(new String[0]);
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int v){
        View view = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_stat, p,false);
        return new VH(view);
    }
    @Override public void onBindViewHolder(@NonNull VH h,int i){
        h.key.setText(keys[i]);
        h.val.setText(vals[i]);
    }
    @Override public int getItemCount(){ return keys.length; }

    static class VH extends RecyclerView.ViewHolder{
        TextView key,val;
        VH(View v){ super(v);
            key = v.findViewById(R.id.statKey);
            val = v.findViewById(R.id.statVal);
        }
    }
}
