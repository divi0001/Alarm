package com.example.alarm;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridViewImageAdapter extends BaseAdapter {

    private Context context;
    int size;

    public GridViewImageAdapter(Context context, int size){
        this.context = context;
        this.size = size;
    }


    @Override
    public int getCount() {
        return 32;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imgMemory;
        if(convertView == null){
            imgMemory = new ImageView(this.context);
            imgMemory.setLayoutParams(new GridView.LayoutParams(150,150));
            imgMemory.setScaleType(ImageView.ScaleType.FIT_XY);
        }else{
            imgMemory = (ImageView) convertView;
        }

        imgMemory.setImageResource(R.drawable.hidden);
        return imgMemory;
    }
}
