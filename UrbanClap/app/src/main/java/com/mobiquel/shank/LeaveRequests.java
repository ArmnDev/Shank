package com.mobiquel.shank;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.mobiquel.shank.adapter.HRPendingAdapter;
import com.mobiquel.shank.adapter.LeaveRequestsAdapter;
import com.mobiquel.shank.utils.AppConstants;
import com.mobiquel.shank.utils.Preferences;
import com.mobiquel.shank.utils.Utils;
import com.mobiquel.shank.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LeaveRequests extends AppCompatActivity {

    private ProgressBarCircularIndeterminate progressBar;
    private RecyclerView leaveRequests;
    private TextView noRecordsFound;
    private JSONArray leaveArray;
    private LeaveRequestsAdapter mAdapter;
    private String source="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_requests);
        getSupportActionBar().setTitle("Leave Requests");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c0392b")));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        source=getIntent().getExtras().getString("SOURCE");
        if(source.equalsIgnoreCase("join"))
        {
            getSupportActionBar().setTitle("Join Requests");

        }
        progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);
        leaveRequests= (RecyclerView)findViewById(R.id.recycler_view);
        noRecordsFound= (TextView)findViewById(R.id.noRecordsToShow);

        getLeaveRequests();



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void getLeaveRequests() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(LeaveRequests.this).getRequestQueue();
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
                        leaveArray=new JSONArray();
                        leaveArray=responseObject.getJSONArray("responseObject");

                        if(leaveArray.length()==0)
                        {
                            noRecordsFound.setVisibility(View.VISIBLE);
                            leaveRequests.setVisibility(View.GONE);
                        }
                        else
                        {
                            noRecordsFound.setVisibility(View.GONE);
                            leaveRequests.setVisibility(View.VISIBLE);

                            mAdapter=new LeaveRequestsAdapter(LeaveRequests.this,leaveArray,"PENDING",progressBar,leaveRequests,noRecordsFound,source);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(LeaveRequests.this);
                            leaveRequests.setLayoutManager(mLayoutManager);
                            leaveRequests.setItemAnimator(new DefaultItemAnimator());
                            leaveRequests.setAdapter(mAdapter);
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
                Utils.showToast(LeaveRequests.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(LeaveRequests.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyCode",Preferences.getInstance().companyCode);
                Log.e("PARAMS_LEAVE",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(LeaveRequests.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(LeaveRequests.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }
}
