package com.example.alarm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Visibility;

import java.util.ArrayList;

public class AlarmLevelAdapter extends RecyclerView.Adapter<AlarmLevelAdapter.ViewHolder>{

    Context context;
    ArrayList<String> alarmLevel = new ArrayList<>();

    public AlarmLevelAdapter(Context context) {
        this.context = context;
        }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_level_list_item, parent, false);
        return new AlarmLevelAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.txtAlarmLevel.setText(alarmLevel.get(position));
    }

    @Override
    public int getItemCount() {
        return alarmLevel.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setAlarmLevel(ArrayList<String> a){
        this.alarmLevel = a;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtAlarmLevel;
        CardView cvParentLevel;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            itemView.findViewById(R.id.txtAlarmLevel);
            itemView.findViewById(R.id.cvParentLevel);
        }

    }


}
