package com.mobiquel.urbanclap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.mobiquel.urbanclap.utils.AppConstants;
import com.mobiquel.urbanclap.utils.Preferences;
import com.mobiquel.urbanclap.utils.Utils;
import com.mobiquel.urbanclap.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReferTechnicianCompany extends AppCompatActivity {

    private Button addTechnician;
    private ProgressBarCircularIndeterminate progressBar;

    private EditText mobileNo,technicianName;
    private Spinner roleSpinner;
    private String id="";
    private TextView actionBarTitleTextView;
    private Toolbar toolbar;
    private JSONArray roleArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_technician);

        getSupportActionBar().setTitle("Refer Technician/Company");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c0392b")));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        technicianName= (EditText) findViewById(R.id.name);
        mobileNo= (EditText) findViewById(R.id.mobile);
        roleSpinner= (Spinner) findViewById(R.id.role);

        addTechnician= (Button) findViewById(R.id.addTechnician);

        progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);


        getRolesTechnicianRefer();

        addTechnician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(technicianName.getText().toString().equals(""))
                    {
                        Utils.showToast(ReferTechnicianCompany.this,"Please enter Technician name!");
                    }
                    else  if(mobileNo.getText().toString().equals(""))
                    {
                        Utils.showToast(ReferTechnicianCompany.this,"Please enter Mobile Number!");

                    }
                    else  if(!Utils.validatePhoneNumber(mobileNo.getText().toString()))
                    {
                        Utils.showToast(ReferTechnicianCompany.this,"Please enter valid Mobile Number!");

                    }
                    else
                    {
                        addHRTechnicianRefer();
                    }
            }
        });


    }


    private void getRolesTechnicianRefer() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "getRolesTechnicianRefer";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject responseObject;
                try {
                    responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_COMP_INFO", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0")) {
                        roleArray = responseObject.getJSONArray("responseObject");
                        String role[] = new String[roleArray.length()];
                        for (int i = 0; i < roleArray.length(); i++) {
                            role[i] = roleArray.getString(i);
                        }
                        ArrayAdapter<String> roleAdapter = new ArrayAdapter<String>(ReferTechnicianCompany.this,
                                android.R.layout.simple_list_item_1, role);
                        roleAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        roleSpinner.setAdapter(roleAdapter);
                    } else {
                        // Utils.showToast(AddTechnician.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
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
            public void onErrorResponse(VolleyError error) {
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
                Utils.showToast(ReferTechnicianCompany.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(ReferTechnicianCompany.this);
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(ReferTechnicianCompany.this)) {
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(ReferTechnicianCompany.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private void addHRTechnicianRefer() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "addUserReferCompany";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject responseObject;
                try {
                    responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_COMP_INFO", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0"))
                    {

                        Utils.showToast(ReferTechnicianCompany.this, responseObject.getString("errorMessage"));
                       finish();
                    }
                    else
                    {
                        Utils.showToast(ReferTechnicianCompany.this, responseObject.getString("errorMessage"));
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
            public void onErrorResponse(VolleyError error) {
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
                //Utils.showToast(AddTechnician.this, error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(ReferTechnicianCompany.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", Preferences.getInstance().companyId);
                params.put("userType", "COMPANY");
                params.put("name", technicianName.getText().toString());
                params.put("mobile", mobileNo.getText().toString());
                params.put("role", roleSpinner.getSelectedItem().toString());

                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(ReferTechnicianCompany.this)) {
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(ReferTechnicianCompany.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
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
}
