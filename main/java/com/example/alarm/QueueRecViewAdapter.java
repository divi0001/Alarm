package com.example.alarm;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QueueRecViewAdapter extends RecyclerView.Adapter<QueueRecViewAdapter.ViewHolder>{

    private ArrayList<Alarm> alarmParameter;
    private Context context;
    private ViewHolder holder;

    public QueueRecViewAdapter(Context context) {
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.method_list_item_math, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        this.holder = holder;

        holder.txtMethod.setText(alarmParameter.get(position).getType() + alarmParameter.get(position).getTurnOffMethod());
        holder.txtDifficulty.setText(alarmParameter.get(position).getDifficulty());

        holder.imgMinusDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmParameter.remove(position);
                setAlarmParameter(alarmParameter);
                //SharedPreferences s = context.getSharedPreferences(holder.itemView.getContext().getString(R.string.queue_shared_pref_key_adapter),Context.MODE_PRIVATE);
                notifyDataSetChanged();//TODO: Look here, this might be causing trouble, maybe the sharedPreferences are called again, when leaving this activity, so deleting effectively does not work?

            }
        });

        holder.imgThreeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });

        //holder.parent.setOnClickListener(new View.OnClickListener() {
         //   @Override
          //  public void onClick(View v) {
           //     Toast.makeText(context, "It fucking worked", Toast.LENGTH_SHORT).show();
            //}
        //});

    }



    @Override
    public int getItemCount() {
        return alarmParameter.size();
    }

    public void setAlarmParameter(ArrayList<Alarm> alarmParameter) {

        this.alarmParameter = alarmParameter;
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{


        private TextView txtMethod,txtDifficulty;
        private ImageView imgThreeDots,imgMinusDelete;
        public CardView parent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMethod = itemView.findViewById(R.id.txtMethodType);
            txtDifficulty = itemView.findViewById(R.id.txtDifficultyType);
            imgThreeDots = itemView.findViewById(R.id.threeDotsEdit);
            imgMinusDelete = itemView.findViewById(R.id.imgMinusDelete);
            parent = itemView.findViewById(R.id.cvQueue);

        }
    }

}
