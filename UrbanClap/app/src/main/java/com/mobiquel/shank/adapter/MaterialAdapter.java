package com.mobiquel.shank.adapter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobiquel.shank.R;

public class MaterialAdapter extends BaseAdapter{

	private Context instance;
	private JSONArray studentsList;
	private LayoutInflater mInflater;
	private boolean[] checkBoxStates;
	private String mappedMaterialIds;

	public MaterialAdapter(Context instance,JSONArray studentsList,String mappedMaterialIds)
	{
		this.instance = instance;
		this.studentsList = studentsList;
		this.mappedMaterialIds=mappedMaterialIds;
		checkBoxStates = new boolean[this.studentsList.length()];
		mInflater = LayoutInflater.from(this.instance);
		try
		{

			for (int i = 0; i < studentsList.length(); i++)
			{
				if(ifMaterialIdMapped(mappedMaterialIds, studentsList.getJSONObject(i).getString("materialId")))
				{
					checkBoxStates[i] = true;
				}
				else
				{
					checkBoxStates[i] = false;
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

	public String getSelectedMaterialIds()
	{
		String childIds = "";
		boolean noSelect = false;
		ArrayList<String> selections = new ArrayList<String>();

		for (int i = 0; i < checkBoxStates.length; i++) {  
			if (checkBoxStates[i] == true) {  
				noSelect = true;  
				try 
				{
					selections.add(studentsList.getJSONObject(i).getString("materialId"));
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
	public String getNotSelectedMaterialIds()
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
					selections.add(studentsList.getJSONObject(i).getString("materialId"));
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
		
		final Holder holder;
		if (convertView == null) 
		{
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.material_list_item_new, null);
			holder.nameV = (TextView) convertView.findViewById(R.id.materialName);
			holder.image = (ImageView) convertView.findViewById(R.id.image);

			holder.checkBoxV = (CheckBox) convertView.findViewById(R.id.checkBox);
			convertView.setTag(holder);

			}
		else
		{
			holder = (Holder) convertView.getTag();
		}



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

		try 
		{
			holder.nameV.setText(studentsList.getJSONObject(position).getString("name"));
			Glide.with(instance)
					.load(studentsList.getJSONObject(position).getString("image"))
					.override(100, 100)
					.into(holder.image);
		} 
		catch (JSONException e) 
		{
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

	static class Holder
	{
		TextView nameV;
		ImageView image;
		CheckBox checkBoxV;

		int id;
	}
	private boolean ifMaterialIdMapped(String materialIds,String itemId)
	{
		String [] mappedIdArray = materialIds.split(",");
		for(String id : mappedIdArray)
		{
			if(itemId.equals(id))
			{
				return true;
			}
		}
		return false;

	}


}