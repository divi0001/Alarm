package com.example.alarm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
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

    EditAlarms mEdit;

    ArrayList<AlarmLevel> alarmLevel = new ArrayList<>();

    public AlarmLevelAdapter(Context context, EditAlarms mEdit) {
        this.context = context;
        this.mEdit = mEdit;
        }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_level_list_item, parent, false);
        return new ViewHolder(view, mEdit);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.txtAlarmLevel.setText(alarmLevel.get(position).getLabel());
        holder.txtAlarmLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEdit.setIfClicked(alarmLevel.get(position).getLabel());
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmLevel.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setAlarmLevel(ArrayList<AlarmLevel> a){
        this.alarmLevel = a;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtAlarmLevel;
        private View parent;
        private EditAlarms mEdit;
        public ViewHolder(@NonNull View itemView, EditAlarms mEdit){
            super(itemView);
            txtAlarmLevel = itemView.findViewById(R.id.txtAlarmLvl);
            parent = itemView;
            this.mEdit = mEdit;
        }

    }


    interface EditAlarms{
        void setIfClicked(String label);
    }


}
