package com.mobiquel.shank;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.mobiquel.shank.adapter.ServiceAdapter;
import com.mobiquel.shank.utils.AppConstants;
import com.mobiquel.shank.utils.Preferences;
import com.mobiquel.shank.utils.Utils;
import com.mobiquel.shank.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ServicesAndSkillsFirst extends AppCompatActivity implements OnClickListener {

    private Button submitService;
    private ListView serviceList;
    private ServiceAdapter adapterService;

    private String serviceids, serviceUncheckedIds;

    private ProgressBarCircularIndeterminate progressBar;
    private String mappedServiceTypeIds = "";
    private TextView actionBarTitleTextView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_skill_first);
        getSupportActionBar().setTitle("Select Services");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c0392b")));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);


        serviceList = (ListView) findViewById(R.id.serviceList);


        submitService = (Button) findViewById(R.id.submitService);
        submitService.setOnClickListener(this);

        serviceids = "";
        serviceUncheckedIds = "";


        getServiceMaterialMappingInfo();


    }

    @Override
    public void onBackPressed() {
        finish();

        super.onBackPressed();
    }

    private void getServiceMaterialMappingInfo() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "getUserServiceSkillProductFeatureMapping";


        StringRequest requestObject = new StringRequest(Method.POST, url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject responseObject;
                try {
                    toa();
                    responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_STUD", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0")) {
                        mappedServiceTypeIds = responseObject.getJSONObject("responseObject").getString("serviceTypeId");
                        //mappedMaterialIds = responseObject.getJSONObject("responseObject").getString("materialIds");

                    } else {
                        // Utils.showToast(ServicesAndSkillsFirst.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
                    }

                    getData("SERVICE");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
                Utils.showToast(ServicesAndSkillsFirst.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(ServicesAndSkillsFirst.this);
                Map<String, String> params = new HashMap<String, String>();
                if(Preferences.getInstance().userType.equalsIgnoreCase("COMPANY"))
                {
                    params.put("userId", Preferences.getInstance().companyId);

                }
                else
                {
                    params.put("userId", Preferences.getInstance().userId);

                }
                params.put("userType", Preferences.getInstance().userType);
                Log.e("PARAMS", params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(ServicesAndSkillsFirst.this)) {
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(ServicesAndSkillsFirst.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }


    private void getData(final String source) {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url = "";
        if (source.equals("SERVICE")) {
            url = AppConstants.SERVER_URL + "getServiceTypeList";
        } else {
            url = AppConstants.SERVER_URL + "getServiceTypeMaterialList";

        }

        StringRequest requestObject = new StringRequest(Method.POST, url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject responseObject;
                try {
                    toa();
                    responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_STUD", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0")) {

                        if (source.equals("SERVICE")) {
                            adapterService = new ServiceAdapter(ServicesAndSkillsFirst.this, responseObject.getJSONArray("responseObject"), mappedServiceTypeIds);

                            serviceList.setAdapter(adapterService);

                            adapterService.notifyDataSetChanged();

                        }
                       /* else
                        {
                            adapterMaterial = new MaterialAdapter(ServicesAndSkillsFirst.this,responseObject.getJSONArray("responseObject"),mappedMaterialIds);

                            materiaList.setAdapter(adapterMaterial);
                            adapterMaterial.notifyDataSetChanged();

                            materialLayout.setVisibility(View.VISIBLE);
                            submitService.setVisibility(View.GONE);
                            serviceList.setVisibility(View.GONE);
                            materiaList.setVisibility(View.VISIBLE);
                        }*/

                    } else {
                        //Utils.showToast(ServicesAndSkillsFirst.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
                Utils.showToast(ServicesAndSkillsFirst.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(ServicesAndSkillsFirst.this);
                Map<String, String> params = new HashMap<String, String>();
                if (source.equals("SERVICE")) {

                } else {
                    params.put("serviceTypeIds", serviceids);
                }
                Log.e("PARAMS", params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(ServicesAndSkillsFirst.this)) {
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(ServicesAndSkillsFirst.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }


    private void toa() {
        System.out.println("abc");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.submitService:
                if (adapterService != null) {
                    serviceids = adapterService.getSelectedServiceIds();
                    serviceUncheckedIds = adapterService.getNotSelectedServiceIds();
                    Log.e("IDS:", serviceids + "  " + serviceUncheckedIds);
                    // getData("MATERIAL");
                    if (serviceids.equals("")) {
                        Utils.showToast(ServicesAndSkillsFirst.this, "Please select atleast one service item!");
                    } else {
                        Intent i = new Intent(ServicesAndSkillsFirst.this, ServicesAndSkillsSecond.class);
                        i.putExtra("SERVICE_IDS", serviceids);
                        startActivity(i);
                    }

                }
                /*else {
                    Utils.showToast(ServicesAndSkillsFirst.this, AppConstants.MESSAGES.NO_FIELD_BLANK_MESSAGE);
                }*/

                break;


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}