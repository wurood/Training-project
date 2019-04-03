package com.example.wurood.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Aroliant on 1/3/2018.
 */

public class DrillDown extends RecyclerView.Adapter<DrillDown.ViewHolder> {
    public List<Contact> nameList;

    public DrillDown(List<Contact> nameList) {

        this.nameList = nameList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView email;
        TextView number;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemName);
            email = itemView.findViewById(R.id.itemEmail);
            number = itemView.findViewById(R.id.itemNumber);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_expand_item, parent, false);


        DrillDown.ViewHolder vh = new DrillDown.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.name.setText(nameList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }


}
