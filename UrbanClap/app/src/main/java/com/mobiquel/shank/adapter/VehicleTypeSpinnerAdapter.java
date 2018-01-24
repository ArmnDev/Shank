package com.mobiquel.shank.adapter;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobiquel.shank.R;
import com.mobiquel.shank.model.VehicleTypeModel;

/***** Adapter class extends with ArrayAdapter ******/
public class VehicleTypeSpinnerAdapter extends ArrayAdapter<VehicleTypeModel> {
    int groupid;
    Activity context;
    ArrayList<VehicleTypeModel> list;
    LayoutInflater inflater;
    public VehicleTypeSpinnerAdapter(Activity context, int groupid, int id, ArrayList<VehicleTypeModel>
            list){
        super(context,id,list);
        this.list=list;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid=groupid;
    }

    public View getView(int position, View convertView, ViewGroup parent ){
        View itemView=inflater.inflate(groupid,parent,false);
        ImageView imageView=(ImageView)itemView.findViewById(R.id.image);
        imageView.setImageResource(list.get(position).getImageId());
        TextView textView=(TextView)itemView.findViewById(R.id.title);
        textView.setText(list.get(position).getText());
        return itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup
            parent){
        return getView(position,convertView,parent);

    }
}
