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

    private ArrayList<AlarmMethod> alarmMethods;
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


        holder.txtId.setText(String.valueOf(alarmMethods.get(position).getId()));
        holder.txtMethod.setText(alarmMethods.get(position).getType() + toMethod(alarmMethods.get(position).get));
        holder.txtDifficulty.setText(alarmMethods.get(position).getDifficulty());

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

                        DBHelper db = new DBHelper(context, "Database.db");

                        int id = alarmParameter.get(position).getID();
                        db.deleteRow("Methoddatabase", alarmParameter.get(position).getID());

                        String table = typeToTable(alarmParameter.get(position).getType());

                        if(!table.equals("QRBarcodedatabase") && !table.equals("") && id != -1) db.deleteRow(table, id);

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

                String type = alarmParameter.get(position).getType();
                Class classType = EditAlarmActivity.class;

                switch (type){
                    case "Tap Off":
                        Toast.makeText(context, "What exactly do you want to edit here? :D", Toast.LENGTH_SHORT).show();
                        break;
                    case "Math: ":
                        classType = MathMethodSetActivity.class;
                        break;
                    case "QR/Barcode":
                        classType = QRMethodSetActivity.class;
                        break;
                    case "Sudoku":
                        classType = SudokuMethodSetActivity.class;
                        break;
                    case "Memory":
                        classType = MemoryMethodSetActivity.class;
                        break;
                    default:
                        classType = EditAlarmActivity.class; //TODO: make sure, the names of the string that is switch cased is actually correctly muxed
                        break;
                }

                if(type.contains("Location: ")){
                    classType = LocationMethodSetActivity.class;
                }

                Intent iUpdate = new Intent(context, classType);
                iUpdate.putExtra("edit_add","edit");

                switch (type) {
                    case "Math: ":
                        iUpdate.putExtra("method", alarmParameter.get(position).getTurnOffMethod());
                        iUpdate.putExtra("difficulty", alarmParameter.get(position).getDifficulty());
                        break;
                    case "QR/Barcode":
                        iUpdate.putExtra("label", alarmParameter.get(position).getDifficulty());
                    case "Sudoku":
                        iUpdate.putExtra("difficulty", alarmParameter.get(position).getDifficulty());
                }
                if(type.contains("Location: ")) {
                    double radius = Double.parseDouble(alarmParameter.get(position).getDifficulty().substring(0, alarmParameter.get(position).getDifficulty().indexOf(" "))); //last arg in .substring is exclusive, so no -1 needed
                    String enter_leave = alarmParameter.get(position).getDifficulty().substring(alarmParameter.get(position).getDifficulty().indexOf("To ")+3);
                    String ent_lea = enter_leave.substring(0,enter_leave.indexOf(" "));

                    iUpdate.putExtra("street", type.substring(type.indexOf(" ")+1));
                    iUpdate.putExtra("radius",radius);
                    iUpdate.putExtra("enter_leave", ent_lea);


                    DBHelper db = new DBHelper(context,"Database.db");
                    int pos = -1;

                    Cursor c1 = db.getData("Locationdatabase");

                    if(c1.getCount()>0){
                        while (c1.moveToNext()){
                            if(c1.getString(7).equals(type.substring(type.indexOf(" ")+1)) && c1.getInt(5) == (int)radius && c1.getString(8).equals(ent_lea)) pos = c1.getInt(0);
                        }
                    }

                    iUpdate.putExtra("pos",pos);
                     //todo meh, there is a better way, but this requires putting an id into locationdatabase

                }
                iUpdate.putExtra("id",alarmParameter.get(position).getID());


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

    private String typeToTable(String type) {

        if(type.contains("Math")){
            return "Mathdatabase";
        } else if (type.contains("QR")) {
            return "QRBarcodedatabase";
        } else if (type.contains("Location")) {
            return "Locationdatabase";
        }else {
            return ""; //todo
        }

    }


    @Override
    public int getItemCount() {
        if(alarmParameter == null){
            return 0;
        }
        return alarmParameter.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAlarmParameter(ArrayList<Alarm> alarmParameter) {

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
