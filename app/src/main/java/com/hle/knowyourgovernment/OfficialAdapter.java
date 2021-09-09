package com.hle.knowyourgovernment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter <MyViewHolder> {

    private static final String TAG = "OfficialAdapter";
    private List<Official> officialList;
    private MainActivity mainAct;

    OfficialAdapter(List<Official> stockList, MainActivity ma) {
        this.officialList = stockList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_official_entry, parent, false);

        itemView.setOnClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Official official = officialList.get(position);

        holder.name.setText(official.getName());
        holder.position.setText(official.getPosition());
        holder.party.setText(official.getParty());
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
