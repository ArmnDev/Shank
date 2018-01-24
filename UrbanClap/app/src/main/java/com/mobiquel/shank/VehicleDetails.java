package com.mobiquel.shank;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.mobiquel.shank.adapter.VehicleTypeSpinnerAdapter;
import com.mobiquel.shank.model.VehicleTypeModel;
import com.mobiquel.shank.utils.AppConstants;
import com.mobiquel.shank.utils.Preferences;
import com.mobiquel.shank.utils.Utils;
import com.mobiquel.shank.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VehicleDetails extends AppCompatActivity implements View.OnClickListener {

    private Button updateVehicle;
    private ProgressBarCircularIndeterminate progressBar;
    private EditText drivingLicenseNo, vehicleNumber;
    private Spinner vehicleTypeSpinner;
    private String id = "";
    private TextView actionBarTitleTextView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_vehicle_detail);

        getSupportActionBar().setTitle("Vehicle Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        vehicleNumber = (EditText) findViewById(R.id.vehicleNo);
        drivingLicenseNo = (EditText) findViewById(R.id.drivingLicenseNo);
        vehicleTypeSpinner = (Spinner) findViewById(R.id.vehicleTypeSpinner);
        updateVehicle = (Button) findViewById(R.id.updateVehicle);
        progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);
        updateVehicle.setOnClickListener(this);

        ArrayList<VehicleTypeModel> list = new ArrayList<>();
        list.add(new VehicleTypeModel("TWO-WHEELER", R.drawable.ic_two_wheeler));
        list.add(new VehicleTypeModel("THREE-WHEELER", R.drawable.ic_three_wheel_icon));
        list.add(new VehicleTypeModel("FOUR-WHEELER", R.drawable.ic_four_wheeler));
        VehicleTypeSpinnerAdapter adapter = new VehicleTypeSpinnerAdapter(this,R.layout.vehicle_type_spinner_list_item, R.id.title, list);
        vehicleTypeSpinner.setAdapter(adapter);

        getVehicleInfo();
        Preferences.getInstance().loadPreferences(VehicleDetails.this);

        if (Preferences.getInstance().isProfileStatusApproved)
        {
            updateVehicle.setVisibility(View.GONE);
        }
        else
        {
            updateVehicle.setVisibility(View.VISIBLE);
        }
    }


    private void getVehicleInfo() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(VehicleDetails.this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "getTechnicianVehicleInfo";
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        id = responseObject.getJSONObject("responseObject").getString("id");
                        if (responseObject.getJSONObject("responseObject").getString("type").contains("2")) {
                            vehicleTypeSpinner.setSelection(0);
                        }
                        if (responseObject.getJSONObject("responseObject").getString("type").contains("3")) {
                            vehicleTypeSpinner.setSelection(1);
                        }
                        if (responseObject.getJSONObject("responseObject").getString("type").contains("4")) {
                            vehicleTypeSpinner.setSelection(2);
                        }
                        vehicleNumber.setText(responseObject.getJSONObject("responseObject").getString("number"));
                        drivingLicenseNo.setText(responseObject.getJSONObject("responseObject").getString("license"));
                    } else {
                        // Utils.showToast(VehicleDetails.this, responseObject.getString("errorMessage"));
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
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(VehicleDetails.this);
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(VehicleDetails.this);
                params.put("technicianId", Preferences.getInstance().userId);
                Log.e("PARAMS", params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(VehicleDetails.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(VehicleDetails.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.updateVehicle:
                submitVehicleInfo();
                break;
        }
    }

    private void submitVehicleInfo()
    {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(VehicleDetails.this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "updateTechnicianVehicle";
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0)
                    {
                        Utils.showToast(VehicleDetails.this, responseObject.getString("errorMessage"));
                        finish();
                    }
                    else
                    {
                        //Utils.showToast(VehicleDetails.this, responseObject.getString("errorMessage"));
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                if (progressBar != null && progressBar.isShown())
                {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                if (progressBar != null && progressBar.isShown())
                {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Preferences.getInstance().loadPreferences(VehicleDetails.this);
                Map<String, String> params = new HashMap<String, String>();
                String typ = "";
                if (vehicleTypeSpinner.getSelectedItemPosition() == 0) {
                    typ = "2W";
                } else if (vehicleTypeSpinner.getSelectedItemPosition() == 1) {
                    typ = "3W";
                } else if (vehicleTypeSpinner.getSelectedItemPosition() == 2) {
                    typ = "4W";
                }
                Preferences.getInstance().loadPreferences(VehicleDetails.this);
                params.put("license", drivingLicenseNo.getText().toString());
                params.put("type", typ);
                params.put("number", vehicleNumber.getText().toString());
                params.put("isActive", "T");
                params.put("id", id);
                params.put("technicianId", Preferences.getInstance().userId);
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(VehicleDetails.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        }
        else
        {
            if (progressBar != null && progressBar.isShown())
            {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(VehicleDetails.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }
}