package com.example.alarm;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
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
import java.util.Arrays;

public class QueueRecViewAdapter extends RecyclerView.Adapter<QueueRecViewAdapter.ViewHolder>{

    private ArrayList<Alarm> alarmParameter;
    Context context;

    public QueueRecViewAdapter(Context context) {
        this.context = context;
    }



    @NonNull
    @Override
    public QueueRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.method_list_item_math, parent, false); //pass null instead of parent,false , if you are not sure to which parent to pass the view/layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueueRecViewAdapter.ViewHolder holder, final int position) {
        holder.txtMethod.setText(alarmParameter.get(position).getTurnOffMethod());
        holder.txtDifficulty.setText(alarmParameter.get(position).getDifficulty());

        holder.imgMinusDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmParameter.remove(position); //TODO: make this not only locally effect alarmParameter
                notifyDataSetChanged();
            }
        });

        holder.imgThreeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iSave = new Intent();
                iSave.putExtra("btnType","save");
                Bundle b = new Bundle();
                b.putString("btnType","save");
                startActivity(context, iSave, b);
            }
        });

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, (alarmParameter.get(position).toString()) + " Selected", Toast.LENGTH_SHORT).show();
            }
        });
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
        private CardView parent;

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
