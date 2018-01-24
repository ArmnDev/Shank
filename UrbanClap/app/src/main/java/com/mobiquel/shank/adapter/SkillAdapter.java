package com.mobiquel.shank.adapter;

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

import com.mobiquel.shank.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class SkillAdapter extends BaseAdapter{

	private Context instance;
	private JSONArray studentsList;
	private LayoutInflater mInflater;
	private boolean[] checkBoxStates;
	private String getSkillIds;

	public SkillAdapter(Context instance, JSONArray studentsList,String getSkillIds)
	{
		this.instance = instance;
		this.studentsList = studentsList;
		checkBoxStates = new boolean[this.studentsList.length()];
		mInflater = LayoutInflater.from(this.instance);
		this.getSkillIds=getSkillIds;
		try
		{

			for (int i = 0; i < studentsList.length(); i++)
			{
				if(getSkillIds!=null)
				{
					if(ifSkillIdMapped(getSkillIds, studentsList.getJSONObject(i).getString("skillId")))
					{
						checkBoxStates[i] = true;
					}
					else
					{
						checkBoxStates[i] = false;
					}
				}

			}
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public int getCount() {
		
		return studentsList.length();
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
		}
	}

	public String getSelectedSkillIds()
	{
		String childIds = "";
		boolean noSelect = false;
		ArrayList<String> selections = new ArrayList<String>();

		for (int i = 0; i < checkBoxStates.length; i++) {  
			if (checkBoxStates[i] == true) {  
				noSelect = true;  
				try 
				{
					selections.add(studentsList.getJSONObject(i).getString("skillId"));
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
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
	public String getNotSelectedSKillIds()
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
				try 
				{
					selections.add(studentsList.getJSONObject(i).getString("skillId"));
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
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

		holder.checkBoxV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
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


		try {
			holder.nameV.setText(studentsList.getJSONObject(position).getString("name"));

		} catch (JSONException e) {
			e.printStackTrace();
		}

		if(checkBoxStates[position])
		{
			holder.checkBoxV.setChecked(true);
		}
		else
		{
			holder.checkBoxV.setChecked(false);
		}
		return convertView;
	}

	static class Holder {
		TextView nameV;
		CheckBox checkBoxV;

		int id;
	}

	private boolean ifSkillIdMapped(String getSkillIds, String itemId) {


		String[] mappedIdArray = getSkillIds.split(",");
		for (String id : mappedIdArray) {
			if (itemId.equals(id)) {
				return true;
			}
		}
		return false;

	}

}