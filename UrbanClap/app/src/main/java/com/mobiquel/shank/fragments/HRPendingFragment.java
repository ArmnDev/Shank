package com.mobiquel.shank.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.mobiquel.shank.adapter.HRPendingAdapter;
import com.mobiquel.shank.utils.AppConstants;
import com.mobiquel.shank.utils.Preferences;
import com.mobiquel.shank.utils.Utils;
import com.mobiquel.shank.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.mobiquel.shank.R.*;

/**
 * Created by landshark on 4/1/18.
 */


public class HRPendingFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private RecyclerView technicianList;
    private TextView noRecordsFound;
    private ProgressBarCircularIndeterminate progressBar;
    private String source;

    private JSONArray techArray;
    private HRPendingAdapter mAdapter;
    public static HRPendingFragment newInstance(String source) {
        Bundle args = new Bundle();
        args.putString(ARG_PAGE, source);
        HRPendingFragment fragment = new HRPendingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null)
        {
            source = getArguments().getString(ARG_PAGE);
        }
    }

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_layout, container, false);
        progressBar = (ProgressBarCircularIndeterminate)view.findViewById(id.progressBar);
        technicianList= (RecyclerView)view.findViewById(id.recycler_view);
        noRecordsFound= (TextView) view.findViewById(id.noRecordsToShow);
        /*mAdapter = new LeadsAdapter(leads,getActivity());

        prepareServicesData();*/

        getHRReferedTechnicianList();
        return view;
    }

    private void getHRReferedTechnicianList() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        String url="";
        Log.e("SOURCE","==> "+source);
        if(source.equalsIgnoreCase("Employee"))
        {
            url= AppConstants.SERVER_URL+"getAddedUserReferredList";
        }
        else
        {
            url= AppConstants.SERVER_URL+"getUserReferredList";

        }

        Log.e("URL_PASSED","==> "+url);
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject responseObject;
                try {
                    responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_TECHNICIAN", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0"))
                    {

                        // {"responseObject":[{"id":"1","companyId":"1","name":"test","mobile":"29292929","role":"Tets","status":"PENDING","createdOn":"05 Jan 2018, 10:14 AM","updatedOn":"01 Jan 2018, 12:00 AM"},{"id":"2","companyId":"1","name":"Arman","mobile":"9069876402","role":"Option 1","status":"PENDING","createdOn":"05 Jan 2018, 11:05 AM","updatedOn":"01 Jan 2018, 12:00 AM"}],"errorCode":0,"errorMessage":"Success"}
                        techArray=new JSONArray();
                        techArray=responseObject.getJSONArray("responseObject");

                        if(techArray.length()==0)
                        {
                            noRecordsFound.setVisibility(View.VISIBLE);
                            technicianList.setVisibility(View.GONE);
                        }
                        else
                        {
                            noRecordsFound.setVisibility(View.GONE);
                            technicianList.setVisibility(View.VISIBLE);

                            mAdapter=new HRPendingAdapter(getActivity(),techArray,"PENDING",progressBar,technicianList,noRecordsFound,source);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            technicianList.setLayoutManager(mLayoutManager);
                            technicianList.setItemAnimator(new DefaultItemAnimator());
                            technicianList.setAdapter(mAdapter);
                        }


                    }
                    else
                    {
                        // Utils.showToast(CompanyHome.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
                Utils.showToast(getActivity(), AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(getActivity());
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId",Preferences.getInstance().companyId);
                params.put("userType","COMPANY");
                params.put("type","PENDING");
                Log.e("PARAMS_TECH",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(getActivity())) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(getActivity(), AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

}
