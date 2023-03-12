package com.example.alarm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlarmRecViewAdapter extends RecyclerView.Adapter<AlarmRecViewAdapter.ViewHolder> {

    private ArrayList<Alarm> alarms = new ArrayList<>();
    private Context context;

    public AlarmRecViewAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {


        holder.clock.setFormat24Hour(alarms.get(position).getT());
        holder.method.setText(alarms.get(position).getTurnOffMethod());
        holder.sound.setText(alarms.get(position).getSoundPath());
        if(alarms.get(position).isExtendedPrivileges()){
            holder.privileg.setText("True");
        }else{
            holder.privileg.setText("False");
        }

        holder.imgBtnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.relExpand.setVisibility(View.VISIBLE);
            }
        });
        holder.imgBtnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.relExpand.setVisibility(View.INVISIBLE);
            }
        });


        holder.imgThreeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alarm curr = alarms.get(position);
                Intent intent = new Intent(context, EditAlarmActivity.class);
                intent.putExtra("position", position);
                context.startActivity(intent); //TODO: understand context
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarms.remove(position);
                notifyDataSetChanged(); //TODO: test if this actually deletes + updates the view
            }
        });


        holder.turnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarms.get(position).isActive()){
                    alarms.get(position).setActive(false);
                    //TODO: Schedule actual alarm with all the set options
                }else{
                    alarms.get(position).setActive(true);
                    //TODO: Switch off the scheduled alarm
                }
            }
        });

    }
    
    public void setAlarms(ArrayList<Alarm> alarms){
        this.alarms = alarms;
        notifyDataSetChanged(); //TODO: same as the one in delete
    }


    @Override
    public int getItemCount() {
        return alarms.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextClock clock;
        private TextView method;
        private TextView sound;
        private TextView privileg;
        private TextView delete;
        private SwitchCompat turnOnOff;
        private ImageView imgBtnDown;
        private ImageView imgBtnUp;
        private ImageView imgThreeDots;
        private RelativeLayout relExpand;
        private CardView parent;
        private TextView dates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clock = itemView.findViewById(R.id.clock);
            method = itemView.findViewById(R.id.txtMethod);
            sound = itemView.findViewById(R.id.txtSound);
            privileg = itemView.findViewById(R.id.txtTrueFalse);
            turnOnOff = itemView.findViewById(R.id.switchAlarmOff);
            imgBtnDown = itemView.findViewById(R.id.btnDown);
            imgBtnUp = itemView.findViewById(R.id.btnUp);
            imgThreeDots = itemView.findViewById(R.id.btnThreeDots);
            delete = itemView.findViewById(R.id.txtDelete);
            relExpand = itemView.findViewById(R.id.relLayoutExpand);
            parent = itemView.findViewById(R.id.parent);
            dates = itemView.findViewById(R.id.dates);

        }
    }
}
