package com.example.alarm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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

    private ArrayList<AlarmMethod> alarmParameter;
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        this.holder = holder;

        if(alarmParameter.get(position).getType() == Enums.Method.QRBar){
            holder.txtMethod.setText(alarmParameter.get(position).getType().toString() + ": " + alarmParameter.get(position).getQrLabel());
        }else{
            holder.txtMethod.setText(alarmParameter.get(position).getType().toString() + ": " + alarmParameter.get(position).getSubType().toString());
        }

        holder.txtId.setText(String.valueOf(alarmParameter.get(position).getId()));

        if(alarmParameter.get(position).getType() == Enums.Method.Location){
            holder.txtDifficulty.setText("Radius: " + alarmParameter.get(position).getLocationRadius() + "m \n" + alarmParameter.get(position).getAddr());
        }else {
            holder.txtDifficulty.setText(alarmParameter.get(position).getDifficulty().toString());
        }

        holder.imgMinusDelete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure you want to delete " + alarmParameter.get(position).getType() + " ?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {dialog.cancel();}});
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        alarmParameter.remove(position);
                        notifyDataSetChanged();
                    }
                });
                builder.create().show();



            }
        });

        holder.imgThreeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Enums.Method type = alarmParameter.get(position).getType();
                Class classType = EditAlarmActivity.class;

                SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.math_to_edit_alarm_pref_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor se = sp.edit();

                switch (type){
                    case TapOff:
                        Toast.makeText(context, "What exactly do you want to edit here? :D", Toast.LENGTH_SHORT).show();
                        break;
                    case Math:
                        classType = MathMethodSetActivity.class;
                        break;
                    case QRBar:
                        classType = QRMethodSetActivity.class;
                        break;
                    case Sudoku:
                        classType = SudokuMethodSetActivity.class;
                        break;
                    case Memory:
                        classType = MemoryMethodSetActivity.class;
                        break;
                    default:
                        classType = EditAlarmActivity.class;
                        break;
                }

                if(type == Enums.Method.Location){
                    classType = LocationMethodSetActivity.class;
                }

                Intent iUpdate = new Intent(context, classType);
                se.putString("edit_add","edit");

                switch (type) {
                    case Math:
                        se.putString("SubMethod", alarmParameter.get(position).getSubMethod().name());
                        se.putString("difficulty", alarmParameter.get(position).getDifficulty().name());
                        break;
                    case QRBar:
                        se.putString("label", alarmParameter.get(position).getQrLabel());
                    case Sudoku:
                        se.putString("difficulty", alarmParameter.get(position).getDifficulty().name());
                }
                if(type == Enums.Method.Location) {
                    double radius = alarmParameter.get(position).getLocationRadius(); //last arg in .substring is exclusive, so no -1 needed
                    String enter_leave = alarmParameter.get(position).getSubType().toString();

                    se.putString("street", alarmParameter.get(position).getAdress().getSubThoroughfare());
                    se.putInt("radius",(int)radius);
                    se.putString("enter_leave", enter_leave);


                }
                se.putInt("id",alarmParameter.get(position).getId());
                se.putInt("queue_id",alarmParameter.get(position).getId());//just to be sure to get the id vars correctly hopefully
                se.apply();
                context.startActivity(iUpdate);
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
        if(alarmParameter == null){
            return 0;
        }
        return alarmParameter.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAlarmParameter(ArrayList<AlarmMethod> alarmParameter) {

        this.alarmParameter = alarmParameter;
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{


        private TextView txtMethod,txtDifficulty, txtId;
        private ImageView imgThreeDots,imgMinusDelete;
        public CardView parent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtMethodId);
            txtMethod = itemView.findViewById(R.id.txtMethodType);
            txtDifficulty = itemView.findViewById(R.id.txtDifficultyType);
            imgThreeDots = itemView.findViewById(R.id.threeDotsEdit);
            imgMinusDelete = itemView.findViewById(R.id.imgMinusDelete);
            parent = itemView.findViewById(R.id.cvQueue);

        }
    }

}
