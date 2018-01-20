package com.mobiquel.urbanclap.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mobiquel.urbanclap.R;
import com.mobiquel.urbanclap.model.CompanyTypeModel;
import com.mobiquel.urbanclap.model.LeadsModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CompanyTypeAdapter extends BaseAdapter{

    private Context instance;
    private LayoutInflater mInflater;
    private boolean[] checkBoxStates;
    private List<CompanyTypeModel> companyList;

    public CompanyTypeAdapter(List<CompanyTypeModel> companyList, Context context)
    {
        this.instance = context;
        this.companyList = companyList;
        checkBoxStates = new boolean[4];
        mInflater = LayoutInflater.from(this.instance);
        deSelectAll();
    }

    @Override
    public int getCount() {

        return companyList.size();
    }

    @Override
    public Object getItem(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    public void selectAll()
    {
        for(int i=0;i<checkBoxStates.length;i++)
        {
            checkBoxStates[i] = true;

        }
    }
    public void deSelectAll()
    {
        for(int i=0;i<checkBoxStates.length;i++)
        {
            checkBoxStates[i] = false;
            Log.e("BOX_STAE","==="+i);
        }
    }

    public String getSelectedCompanyIds()
    {
        String childIds = "";
        boolean noSelect = false;
        ArrayList<String> selections = new ArrayList<String>();

        for (int i = 0; i < checkBoxStates.length; i++) {
            if (checkBoxStates[i] == true) {
                noSelect = true;
                selections.add(String.valueOf(i));

                Log.e("sel pos thu-->", "" + i);
            }
        }
        if (!noSelect)
        {
            childIds = "";
        }
        else
        {
            childIds = TextUtils.join(",",selections.toArray());
            System.out.println("child ids: " + childIds);
        }
        return childIds;
    }
    public String getNotSelectedCompanyIds()
    {
        String childIds = "";
        boolean noSelect = false;
        ArrayList<String> selections = new ArrayList<String>();
        for (int i = 0; i < checkBoxStates.length; i++)
        {
            if (checkBoxStates[i] == true)
            {

            }
            else
            {
                noSelect = true;
                selections.add(String.valueOf(i));
            }
        }
        if (!noSelect)
        {
            childIds = "";
        }
        else
        {
            childIds = TextUtils.join(",",selections.toArray());
            System.out.println("child ids: " + childIds);
        }
        return childIds;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder;
        if (convertView == null)
        {
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.material_list_item, null);
            holder.nameV = (TextView) convertView.findViewById(R.id.materialName);

            holder.checkBoxV = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }

        final CompanyTypeModel companyModel = companyList.get(position);
        holder.nameV.setId(position);

        holder.checkBoxV.setId(position);

        holder.checkBoxV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view)
            {
                CheckBox checkBox = (CheckBox) view;
                if (checkBoxStates[position]) {
                    checkBox.setChecked(false);
                    checkBoxStates[position] = false;
                } else {
                    checkBox.setChecked(true);
                    checkBoxStates[position] = true;
                }
            }
        });

            holder.nameV.setText(companyModel.getTitle());

       holder.checkBoxV.setChecked(checkBoxStates[position]);
       // holder.id = position;

        return convertView;
    }

    static class Holder
    {
        TextView nameV;
        CheckBox checkBoxV;

        int id;
    }


}