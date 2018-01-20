package com.mobiquel.urbanclap.adapter;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.mobiquel.urbanclap.model.LeadsModel;
import com.mobiquel.urbanclap.R;
import com.mobiquel.urbanclap.utils.Utils;

import java.util.List;

public class LeadsAdapter extends RecyclerView.Adapter<LeadsAdapter.MyViewHolder> {

    private List<LeadsModel> ServiceModelsList;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView leadName,dateOfLead;
        public Button viewLead;


        public MyViewHolder(View view) {
            super(view);
            leadName = (TextView) view.findViewById(R.id.leadName);
            dateOfLead = (TextView) view.findViewById(R.id.dateOfLead);
            viewLead = (Button) view.findViewById(R.id.viewLead);



        }
    }


    public LeadsAdapter(List<LeadsModel> ServiceModelsList, Context context) {
        this.ServiceModelsList = ServiceModelsList;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.status_list_item, parent, false);



        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final LeadsModel ServiceModel = ServiceModelsList.get(position);
        holder.leadName.setText(ServiceModel.getTitle());
        holder.dateOfLead.setText(ServiceModel.getDate());

        holder.viewLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToast(context,ServiceModel.getTitle());
            }
        });

       // Glide.with(context).load(ServiceModel.getImageURL()).override(200,200).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.serviceImage);

    }

    @Override
    public int getItemCount() {
        return ServiceModelsList.size();
    }
}
