package com.mobiquel.urbanclap.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mobiquel.urbanclap.R;
import com.mobiquel.urbanclap.model.FeatureModel;
import com.mobiquel.urbanclap.model.UserProductMappingVO;
import com.mobiquel.urbanclap.utils.Utils;

import org.json.JSONArray;

import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.MyViewHolder> {

    private List<UserProductMappingVO> ServiceModelsList;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView leadName;
        private CheckBox checkBoxInstall,checkBoxService,checkBoxComplaint;




        public MyViewHolder(View view) {
            super(view);
            leadName = (TextView) view.findViewById(R.id.title);
            checkBoxInstall = (CheckBox) view.findViewById(R.id.checkBoxInstall);
            checkBoxService = (CheckBox) view.findViewById(R.id.checkBoxService);
            checkBoxComplaint = (CheckBox) view.findViewById(R.id.checkBoxComplaint);




        }
    }


    public FeatureAdapter(List<UserProductMappingVO> ServiceModelsList, Context context) {
        this.ServiceModelsList = ServiceModelsList;
        this.context=context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feature_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final UserProductMappingVO ServiceModel = ServiceModelsList.get(position);
        holder.leadName.setText(ServiceModel.getProductName());

        if(ServiceModel.getIsInstall().equals("T"))
        {
            holder.checkBoxInstall.setEnabled(true);
            holder.checkBoxInstall.setChecked(true);
            holder.checkBoxInstall.setVisibility(View.VISIBLE);

        }
        else
        {
            holder.checkBoxInstall.setChecked(false);

            holder.checkBoxInstall.setEnabled(false);
            holder.checkBoxInstall.setVisibility(View.INVISIBLE);

        }
        if(ServiceModel.getIsService().equals("T"))
        {
            holder.checkBoxService.setEnabled(true);
            holder.checkBoxService.setChecked(true);
            holder.checkBoxService.setVisibility(View.VISIBLE);

        }
        else
        {
            holder.checkBoxService.setChecked(false);
            holder.checkBoxService.setEnabled(false);
            holder.checkBoxService.setVisibility(View.INVISIBLE);

        }
        if(ServiceModel.getIsComplaint().equals("T"))
        {
            holder.checkBoxComplaint.setEnabled(true);
            holder.checkBoxComplaint.setChecked(true);
            holder.checkBoxComplaint.setVisibility(View.VISIBLE);

        }
        else
        {
            holder.checkBoxComplaint.setChecked(false);
            holder.checkBoxComplaint.setEnabled(false);
            holder.checkBoxComplaint.setVisibility(View.INVISIBLE);

        }

       // Glide.with(context).load(ServiceModel.getImageURL()).override(200,200).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.serviceImage);

    }

    @Override
    public int getItemCount() {
        return ServiceModelsList.size();
    }
}
