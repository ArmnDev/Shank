package com.mobiquel.shank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mobiquel.shank.R;
import com.mobiquel.shank.model.ProductCheckboxState;
import com.mobiquel.shank.model.UserProductMappingVO;

import org.json.JSONArray;

import java.util.LinkedList;
import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.MyViewHolder> {

    public List<UserProductMappingVO> ServiceModelsList;
    private Context context;
    private List<ProductCheckboxState> checkBoxStateList;
    private JSONArray productSelectedJSONArray;
    private boolean[] serviceStates;
    private boolean[] complaintStates;
    private boolean[] installStates;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView leadName;
        private CheckBox checkBoxInstall, checkBoxService, checkBoxComplaint;


        public MyViewHolder(View view) {
            super(view);
            leadName = (TextView) view.findViewById(R.id.title);
            checkBoxInstall = (CheckBox) view.findViewById(R.id.checkBoxInstall);
            checkBoxService = (CheckBox) view.findViewById(R.id.checkBoxService);
            checkBoxComplaint = (CheckBox) view.findViewById(R.id.checkBoxComplaint);
        }
    }


    public FeatureAdapter(List<UserProductMappingVO> ServiceModelsList, Context context, JSONArray productJSONArray) {
        this.ServiceModelsList = ServiceModelsList;
        this.context = context;
        this.productSelectedJSONArray = productJSONArray;
        serviceStates=new boolean[this.productSelectedJSONArray.length()];
        complaintStates=new boolean[this.productSelectedJSONArray.length()];
        installStates=new boolean[this.productSelectedJSONArray.length()];

        initCheckboxStateList();
    }

    private void initCheckboxStateList()
    {
        checkBoxStateList = new LinkedList<ProductCheckboxState>();
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


        if (ServiceModel.getIsInstall().equals("T")) {
            holder.checkBoxInstall.setEnabled(true);
            holder.checkBoxInstall.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.checkBoxInstall.setEnabled(false);
            holder.checkBoxInstall.setVisibility(View.INVISIBLE);
        }
        if (ServiceModel.getIsService().equals("T"))
        {
            holder.checkBoxService.setEnabled(true);
            holder.checkBoxService.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.checkBoxService.setEnabled(false);
            holder.checkBoxService.setVisibility(View.INVISIBLE);
        }
        if (ServiceModel.getIsComplaint().equals("T"))
        {
            holder.checkBoxComplaint.setEnabled(true);
            holder.checkBoxComplaint.setVisibility(View.VISIBLE);

        }
        else
        {
            holder.checkBoxComplaint.setEnabled(false);
            holder.checkBoxComplaint.setVisibility(View.INVISIBLE);
        }

        ProductCheckboxState vo = new ProductCheckboxState();
        vo.setIsComplaintChecked(Boolean.toString(holder.checkBoxComplaint.isChecked()));
        vo.setIsInstallChecked(Boolean.toString(holder.checkBoxComplaint.isChecked()));
        vo.setIsServiceChecked(Boolean.toString(holder.checkBoxComplaint.isChecked()));

        if(checkBoxStateList.size()>position && checkBoxStateList.get(position)!=null)
        {
            checkBoxStateList.remove(position);
            checkBoxStateList.add(position,vo);
        }
        else
        {
            checkBoxStateList.add(position,vo);
        }

        holder.checkBoxComplaint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if(b)
                {
                    checkBoxStateList.get(position).setIsComplaintChecked(Boolean.toString(b));
                }
            }
        });

        holder.checkBoxInstall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if(b)
                {
                    checkBoxStateList.get(position).setIsInstallChecked(Boolean.toString(b));
                }
            }
        });

        holder.checkBoxService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if(b)
                {
                    checkBoxStateList.get(position).setIsServiceChecked(Boolean.toString(b));
                }
            }
        });
   }

    @Override
    public int getItemCount()
    {
        return ServiceModelsList.size();
    }

    public List<ProductCheckboxState> returnProductCheckBoxStateList()
    {
        return checkBoxStateList;
    }
}
