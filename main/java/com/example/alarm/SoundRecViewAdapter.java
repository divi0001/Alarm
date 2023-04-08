package com.example.alarm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SoundRecViewAdapter extends RecyclerView.Adapter<SoundRecViewAdapter.ViewHolder>{


    ArrayList<Song> soundNames;
    Context context;
    MediaPlayer player;
    Uri curr_uri = null;
    String curr_name = null;

    public SoundRecViewAdapter(Context context){
        this.context = context;
    }


    public void setSounds(ArrayList<Song> sound){
        this.soundNames = sound;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sound_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        Song song = soundNames.get(position);


        holder.txtSoundTitle.setText(song.getName());
        curr_name = song.getName();
        curr_uri = song.getUri();

        String size;

        if(song.getSize() >= 1024){
            size = (double) Math.round((song.getSize()/1024.0)*100)/100 + " kiB";
            if(song.getSize() >= 1048576){
                size = (double) Math.round((song.getSize()/1048576.0)*100)/100 + " miB";
                if(song.getSize() >= 1073741824){
                    size = (double) Math.round((song.getSize()/1073741824.0)*100)/100 + " giB";
                }else if(song.getSize() >= 2147483000){
                     size = "too big wtf";}
            }

        }else{
            size = (double) Math.round((song.getSize())*100)/100 + " B";
        }


        holder.txtSoundSize.setText(String.valueOf(size));

        String duration;



        if(song.getDuration() >= 60000){
            duration = song.getDuration()/60000 + " m " + (song.getDuration()/1000)%60+ " s";
            if (song.getDuration() >= 3600000) {
                duration = song.getDuration()/3600000 + " h " + (song.getDuration()/60000)%60 + " m";
            }
        }else{
            duration = song.getDuration()+" s";
        }



        holder.txtSoundDuration.setText(duration); //String.valueOf(song.getDuration())
        holder.txtSoundId.setText(String.valueOf(song.getId()));


        Uri albumArtUri = song.getAlbumArtUri();

        if(albumArtUri != null){
            holder.imgAlbumArt.setImageURI(albumArtUri);

            if (holder.imgAlbumArt.getDrawable() == null){
                holder.imgAlbumArt.setImageResource(R.drawable.ic_map_overlay);
            }
        }else{
            holder.imgAlbumArt.setImageResource(R.drawable.ic_map_overlay);
        }


        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player == null){


                    player = MediaPlayer.create(context, song.getUri());
                    player.setLooping(true);
                    player.start();
                    holder.cvSoundControls.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.imgBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(player == null){
                    player = MediaPlayer.create(context, song.getUri());
                    player.setLooping(true);
                    player.start();
                }

            }
        });

        holder.imgBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player != null){
                    player.pause();
                }
            }
        });

        holder.imgBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player != null){
                    player.release();
                    player = null;
                    holder.cvSoundControls.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return soundNames.size();
    }


    public Uri getChosen(){
        return curr_uri;
    }

    public String getName(){
        return curr_name;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtSoundTitle, txtSoundId, txtSoundDuration, txtSoundSize;
        ImageView imgAlbumArt, imgBtnPlay, imgBtnPause, imgBtnStop;
        CardView parent, cvSoundControls;
        RelativeLayout relLaySound;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relLaySound = itemView.findViewById(R.id.relLaySound);
            txtSoundTitle = itemView.findViewById(R.id.txtSoundTitle);
            txtSoundId = itemView.findViewById(R.id.txtSoundId);
            txtSoundDuration = itemView.findViewById(R.id.txtSoundDuration);
            txtSoundSize = itemView.findViewById(R.id.txtSoundSize);
            imgAlbumArt = itemView.findViewById(R.id.imgAlbumArt);
            parent = itemView.findViewById(R.id.cvSounds);
            imgBtnPause = itemView.findViewById(R.id.imgSoundPause);
            imgBtnPlay = itemView.findViewById(R.id.imgSoundPlay);
            imgBtnStop = itemView.findViewById(R.id.imgSoundStop);
            cvSoundControls = itemView.findViewById(R.id.cvSoundControls);


        }
    }




}
