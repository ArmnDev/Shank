package com.mobiquel.urbanclap.mapclasses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mobiquel.urbanclap.DAO.DAO;
import com.mobiquel.urbanclap.model.LocationVO;
import com.mobiquel.urbanclap.utils.AppConstants;
import com.mobiquel.urbanclap.utils.Preferences;
import com.mobiquel.urbanclap.utils.Utils;
import com.mobiquel.urbanclap.utils.VolleySingleton;
//import com.mobiquel.urbanclap.vo.LocationVO;

public class SendGPSLocation extends BroadcastReceiver
{
	private Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		System.out.println("In SendGPSLocation onReceive");
		mContext=context;
		DAO dao = new DAO(mContext);
		boolean isLocationSentToServer = false;
		Preferences.getInstance().loadPreferences(mContext);
		List<LocationVO> list = dao.getLocations(Preferences.getInstance().userId);
		for(LocationVO v : list)
		{
			System.out.println("In SendGPSLocation onReceive");
			isLocationSentToServer = true;
			System.out.println("START --- " + System.currentTimeMillis());
			sendGPSLocation(v.getLat(),v.getLon(),v.getId());
			System.out.println("END --- " + System.currentTimeMillis());		
		}
		if(isLocationSentToServer && Utils.isNetworkAvailable(mContext))
		{
			//Utils.showToast(mContext, "Location sent to server successfully!!");
		}
	}

	private void sendGPSLocation(final String lat,final String lon,final String id)
	{
		RequestQueue queue = VolleySingleton.getInstance(mContext).getRequestQueue();
		String url = AppConstants.SERVER_URL+"recordTechnicianLocation";
		StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
			@Override
			public void onResponse(String response) 
			{
				final JSONObject responseObject;
				try {
					responseObject = new JSONObject(response);

					DAO dao = new DAO(mContext);
					dao.removeLocation(id);
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}}, new Response.ErrorListener() 
			{
				@Override
				public void onErrorResponse(VolleyError error) 
				{

				}
			}) 
		{
			@Override
			protected Map<String, String> getParams()  
			{
				Preferences.getInstance().loadPreferences(mContext);
				Map<String, String> params = new HashMap<String, String>();
				params.put("technicianId",Preferences.getInstance().userId);
				params.put("longitude",lon);
				params.put("latitude",lat);

				return params;
			}};
			requestObject.setRetryPolicy(new DefaultRetryPolicy(25000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			if(Utils.isNetworkAvailable(mContext))
			{
				requestObject.setShouldCache(false);
				queue.add(requestObject);
			}
			else
			{	

			}
	}

	public void onReceive(Context context) 
	{
		
	}
}