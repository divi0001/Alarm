package com.example.alarm;

import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdressAutoCompleteAdapter extends RecyclerView.Adapter<AdressAutoCompleteAdapter.ViewHolder>{

    List<Address> lAddr;
    ValContainer<Address> vC;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adress_autocomplete_row_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        String localAddr;

        Address address = lAddr.get(position);

        localAddr = address.getThoroughfare() + " " + address.getSubThoroughfare() + "\n" + address.getPostalCode() + " " + address.getLocality() + "\n" + address.getCountryName();

        holder.txtAdress.setText(localAddr);
        holder.imgAutoComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vC = new ValContainer<>();
                vC.setVal(lAddr.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        if(lAddr == null) return 0;
        return lAddr.size();
    }

    public void setlAddr(List<Address> lA){
        this.lAddr = lA;
        notifyDataSetChanged();
    }

    public ValContainer<Address> getvC() {
        return vC;
    }

    public void setvC(ValContainer<Address> vC) {
        this.vC = vC;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CardView parent;
        private TextView txtAdress;
        private ImageView imgAutoComplete;

        public ViewHolder(View itemView){
            super(itemView);
            txtAdress = (TextView) itemView.findViewById(R.id.txtCompleteLocation);
            imgAutoComplete = (ImageView) itemView.findViewById(R.id.imgAutoComplete);
        }


    }

}
