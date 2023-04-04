package com.example.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SoundRecViewAdapter extends RecyclerView.Adapter<SoundRecViewAdapter.ViewHolder>{


    ArrayList<String> soundNames;
    Context context;
    MediaPlayer player;

    public SoundRecViewAdapter(Context context){
        this.context = context;
    }


    public void setSounds(ArrayList<String> sound){
        this.soundNames = sound;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sound_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.txtSoundName.setText(soundNames.get(position));

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player == null){
                    player = MediaPlayer.create(context,R.raw.strong_5);
                    player.setLooping(true);
                    player.start();
                    //player.pause();
                    //player.release(); // --> functionality of stop button
                    //player = null;
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return soundNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtSoundName;
        CardView parent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSoundName = itemView.findViewById(R.id.txtSoundName);
            parent = itemView.findViewById(R.id.cvSounds);


        }
    }


}
