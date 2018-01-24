package com.mobiquel.shank.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobiquel.shank.LeaveRequests;
import com.mobiquel.shank.MobileOTPActivity;
import com.mobiquel.shank.R;
import com.mobiquel.shank.SettingsTechnician;
import com.mobiquel.shank.utils.AppConstants;
import com.mobiquel.shank.utils.GPSTracker;
import com.mobiquel.shank.utils.Preferences;
import com.mobiquel.shank.utils.Utils;
import com.mobiquel.shank.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LeaveRequestsAdapter extends RecyclerView.Adapter<LeaveRequestsAdapter.MyViewHolder> {

    private Context context;
    private JSONArray techArray;
    private String type;
    private ProgressBarCircularIndeterminate progressBar;
    private RecyclerView techList;
    private String source;
    private TextView noRecordsFound;

    public LeaveRequestsAdapter(Context context, JSONArray techArray, String type, ProgressBarCircularIndeterminate progressBar, RecyclerView techList, TextView noRecordsFound,String source) {
        this.techArray = techArray;
        this.type=type;
        this.context=context;
        this.progressBar=progressBar;
        this.techList=techList;
        this.noRecordsFound=noRecordsFound;
        this.source=source;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leave_request_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        try {
            holder.technicianName.setText(techArray.getJSONObject(position).getString("name"));
            holder.technicianMobile.setText(techArray.getJSONObject(position).getString("mobile"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return techArray.length();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView technicianName,technicianMobile;
        private Button approveRequest;

        public MyViewHolder(View view) {
            super(view);
            technicianName = (TextView) view.findViewById(R.id.technicianName);
            technicianMobile = (TextView) view.findViewById(R.id.technicianMobile);
            approveRequest = (Button) view.findViewById(R.id.approveRequest);

            approveRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(source.equalsIgnoreCase("LEAVE"))
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Are you sure you want to remove this employ?").setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        try {
                                            approveCompanyLeaveRequest(techArray.getJSONObject(getLayoutPosition()).getString("technicianId"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Are you sure you want to Add this employee?").setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        try {
                                            approveCompanyLeaveRequest(techArray.getJSONObject(getLayoutPosition()).getString("technicianId"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                }
            });

        }
    }

    private void approveCompanyLeaveRequest(final String technicianId) {
        RequestQueue queue = VolleySingleton.getInstance(LeaveRequestsAdapter.this.context).getRequestQueue();
        String url="";
        if(source.equalsIgnoreCase("LEAVE"))
        {
            url= AppConstants.SERVER_URL+"approveCompanyLeaveRequest";
        }
        else
        {
            url= AppConstants.SERVER_URL+"approveCompanyJoinRequest";
        }
        Log.e("URL"," "+url);
        progressBar.setVisibility(View.VISIBLE);

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
                        Utils.showToast(LeaveRequestsAdapter.this.context,responseObject.getString("errorMessage"));
                        getLeaveRequests();
                       // getHRReferedTechnicianList("PENDING");
                    }
                    else
                    {
                        Utils.showToast(LeaveRequestsAdapter.this.context,responseObject.getString("errorMessage"));
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
            { if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
                Utils.showToast(LeaveRequestsAdapter.this.context, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(LeaveRequestsAdapter.this.context);
                Map<String, String> params = new HashMap<String, String>();
                params.put("technicianId",technicianId);
                params.put("companyCode",Preferences.getInstance().companyCode);

                Log.e("PARAMS"," "+params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(LeaveRequestsAdapter.this.context)) {
            queue.add(requestObject);
        }
        else
        { if (progressBar != null && progressBar.isShown()) {
            progressBar.setVisibility(View.GONE);
        }
            Utils.showToast(LeaveRequestsAdapter.this.context, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private void getLeaveRequests() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();
        String url="";
        if(source.equalsIgnoreCase("LEAVE"))
        {
            url= AppConstants.SERVER_URL+"getCompanyLeaveRequestList";
        }
        else
        {
            url= AppConstants.SERVER_URL+"getCompanyJoinRequestList";
        }

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
                            techList.setVisibility(View.GONE);
                        }
                        else
                        {
                            noRecordsFound.setVisibility(View.GONE);
                            techList.setVisibility(View.VISIBLE);

                            LeaveRequestsAdapter mAdapter=new LeaveRequestsAdapter(context,techArray,"PENDING",progressBar,techList,noRecordsFound,source);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                            techList.setLayoutManager(mLayoutManager);
                            techList.setItemAnimator(new DefaultItemAnimator());
                            techList.setAdapter(mAdapter);
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
                Utils.showToast(context, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(context);
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyCode",Preferences.getInstance().companyCode);
                Log.e("PARAMS_LEAVE",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(context)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(context, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }


}
