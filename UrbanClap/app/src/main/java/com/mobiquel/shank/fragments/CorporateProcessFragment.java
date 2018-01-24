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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.mobiquel.shank.R;
import com.mobiquel.shank.adapter.LeadsAdapter;
import com.mobiquel.shank.model.LeadsModel;
import com.mobiquel.shank.utils.AppConstants;
import com.mobiquel.shank.utils.Preferences;
import com.mobiquel.shank.utils.Utils;
import com.mobiquel.shank.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by landshark on 4/1/18.
 */


public class CorporateProcessFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private RecyclerView leadList;
    private ProgressBarCircularIndeterminate progressBar;
    private LeadsAdapter mAdapter;
    private List<LeadsModel> leads = new ArrayList<>();
    private int mPage;

    public static CorporateProcessFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        CorporateProcessFragment fragment = new CorporateProcessFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mPage = getArguments().getInt(ARG_PAGE);
    }

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        progressBar = (ProgressBarCircularIndeterminate)view.findViewById(R.id.progressBar);
        leadList= (RecyclerView)view.findViewById(R.id.recycler_view);

        mAdapter = new LeadsAdapter(leads,getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        leadList.setLayoutManager(mLayoutManager);
        leadList.setItemAnimator(new DefaultItemAnimator());
        leadList.setAdapter(mAdapter);
        prepareServicesData();

        getHRReferedTechnicianList();
        return view;
    }

    private void prepareServicesData() {

        LeadsModel service = new LeadsModel("1. LEAD A","12/07/2017 11:00 PM");
        leads.add(service);

        service = new LeadsModel("2. LEAD B","12/08/2017 11:00 PM");
        leads.add(service);

        service = new LeadsModel("3. LEAD C","12/09/2017 11:00 PM");
        leads.add(service);

        service = new LeadsModel("4. LEAD D","12/10/2017 11:00 PM");
        leads.add(service);

        service = new LeadsModel("5. LEAD E","12/11/2017 11:00 PM");
        leads.add(service);

        service = new LeadsModel("6. LEAD F","12/12/2017 11:00 PM");
        leads.add(service);


        mAdapter.notifyDataSetChanged();
    }
    private void getHRReferedTechnicianList() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        String url= AppConstants.SERVER_URL+"getHRReferedTechnicianList";

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
                params.put("companyId",Preferences.getInstance().companyId);
                params.put("type","PROCESS");
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
