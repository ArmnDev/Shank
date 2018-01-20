package com.mobiquel.urbanclap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
import com.mobiquel.urbanclap.adapter.MaterialAdapter;
import com.mobiquel.urbanclap.adapter.ServiceAdapter;
import com.mobiquel.urbanclap.utils.AppConstants;
import com.mobiquel.urbanclap.utils.Preferences;
import com.mobiquel.urbanclap.utils.Utils;
import com.mobiquel.urbanclap.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Materials extends AppCompatActivity implements OnClickListener {

    private Button submitMatrial,submitServiceSkill,editServiceSkill;
    private ListView materiaList,serviceSkillList;
    private MaterialAdapter adapterMaterial;
    private ServiceAdapter adapterService;

    private String materialIds,materialUncheckedIds,serviceids,serviceUncheckedIds;

    private LinearLayout materialLayout;
    private ProgressBarCircularIndeterminate progressBar;
    private String mappedServiceTypeIds="",mappedMaterialIds="";
    private TextView actionBarTitleTextView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);
        getSupportActionBar().setTitle("Supporting Materials");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c0392b")));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        progressBar = (ProgressBarCircularIndeterminate)findViewById(R.id.progressBar);



        materiaList = (ListView) findViewById(R.id.materialList);
        serviceSkillList = (ListView) findViewById(R.id.serviceSkillList);

        submitMatrial = (Button) findViewById(R.id.submitMatrial);
        submitServiceSkill = (Button) findViewById(R.id.submitServiceSkill);
        editServiceSkill = (Button) findViewById(R.id.editSericeSkill);
        materialLayout = (LinearLayout) findViewById(R.id.materialLayout);

        submitMatrial.setOnClickListener(this);
        submitServiceSkill.setOnClickListener(this);
        editServiceSkill.setOnClickListener(this);

        materialIds="";
        materialUncheckedIds="";

        serviceids="";
        serviceUncheckedIds="";



        getServiceMaterialMappingInfo();

        Preferences.getInstance().loadPreferences(Materials.this);
        if(Preferences.getInstance().isProfileStatusApproved)
        {
            submitMatrial.setVisibility(View.INVISIBLE);
        }
        else
        {
            submitMatrial.setVisibility(View.VISIBLE);
        }



    }

    @Override
    public void onBackPressed()
    {
        finish();

        super.onBackPressed();
    }

    private void getServiceMaterialMappingInfo() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url = AppConstants.SERVER_URL +"getUserServiceMaterialMapping";


        StringRequest requestObject = new StringRequest(Method.POST, url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject responseObject;
                try {
                    toa();
                    responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_STUD", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0"))
                    {
                        mappedServiceTypeIds = responseObject.getJSONObject("responseObject").getString("serviceTypeIds");
                        mappedMaterialIds = responseObject.getJSONObject("responseObject").getString("materialIds");

                    }
                    else
                    {
                       // Utils.showToast(Materials.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
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
            public void onErrorResponse(VolleyError error)
            {
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
                Utils.showToast(Materials.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(Materials.this);
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

                Log.e("PARAMS",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(Materials.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(Materials.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }



    private void getData(final String source) {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url="";
        if(source.equals("SERVICE"))
        {
            url = AppConstants.SERVER_URL +"getServiceTypeList";
        }
        else
        {
            url = AppConstants.SERVER_URL +"getServiceTypeMaterialList";

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
                    if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0"))
                    {

                        if(source.equals("SERVICE"))
                        {
                            adapterService = new ServiceAdapter(Materials.this,responseObject.getJSONArray("responseObject"),mappedServiceTypeIds);

                            serviceSkillList.setAdapter(adapterService);

                            adapterService.notifyDataSetChanged();

                        }
                        else
                        {
                            adapterMaterial = new MaterialAdapter(Materials.this,responseObject.getJSONArray("responseObject"),mappedMaterialIds);

                            materiaList.setAdapter(adapterMaterial);
                            adapterMaterial.notifyDataSetChanged();

                            materialLayout.setVisibility(View.VISIBLE);
                            submitServiceSkill.setVisibility(View.GONE);
                            serviceSkillList.setVisibility(View.GONE);
                            materiaList.setVisibility(View.VISIBLE);
                        }

                    }
                    else
                    {
                        //Utils.showToast(Materials.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
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
            public void onErrorResponse(VolleyError error)
            {
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
                Utils.showToast(Materials.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(Materials.this);
                Map<String, String> params = new HashMap<String, String>();
                if(source.equals("SERVICE"))
                {

                }
                else
                {
                    params.put("serviceTypeIds", serviceids);
                }
                Log.e("PARAMS",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(Materials.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(Materials.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }




    private void submitServiceMatrial() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url=AppConstants.SERVER_URL +"generateUserServiceMaterialMapping";

        StringRequest requestObject = new StringRequest(Method.POST, url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject responseObject;
                try {
                    toa();
                    responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_STUD", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0"))
                    {
						/*adapter = new MaterialAdapter(Materials.this,responseObject.getJSONArray("responseObject"));
						serviceSkillList.setAdapter(adapter);
						adapter.notifyDataSetChanged();*/
                        Utils.showToast(Materials.this, responseObject.getString("errorMessage"));
                        finish();

                    }
                    else
                    {
                        //Utils.showToast(Materials.this, responseObject.getString("errorMessage"));
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
            public void onErrorResponse(VolleyError error)
            {
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
                Utils.showToast(Materials.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(Materials.this);
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(Materials.this);
                params.put("serviceTypeIds", serviceids);
                if(Preferences.getInstance().userType.equalsIgnoreCase("COMPANY"))
                {
                    params.put("userId", Preferences.getInstance().companyId);

                }
                else
                {
                    params.put("userId", Preferences.getInstance().userId);

                }
                params.put("materialIds", materialIds);
                params.put("userType", Preferences.getInstance().userType);



                Log.e("PARAMS",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(Materials.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(Materials.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }


    private void toa() {
        System.out.println("abc");
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.submitMatrial:

                if (adapterMaterial != null)
                {
                    materialIds = adapterMaterial.getSelectedMaterialIds();
                    materialUncheckedIds = adapterMaterial.getNotSelectedMaterialIds();
                    Log.e("IDS:", materialIds+"  "+materialUncheckedIds);
                    //	getData("MATERIAL");
                    if(materialIds.equals(""))
                    {
                        Utils.showToast(Materials.this,"Please select atleast one material item!");
                    }
                    else
                    {
                        submitServiceMatrial();
                    }

                }

                break;
            case R.id.submitServiceSkill:

                if (adapterService != null)
                {
                    serviceids = adapterService.getSelectedServiceIds();
                    serviceUncheckedIds = adapterService.getNotSelectedServiceIds();
                    Log.e("IDS:", serviceids+"  "+serviceUncheckedIds);
                    if(serviceids.equals(""))
                    {
                        Utils.showToast(Materials.this,"Please select anyone Service  item!");
                    }
                    else
                    {
                        getData("MATERIAL");
                    }


                }

                break;
            case R.id.editSericeSkill:
                //	getData("SERVICE");

                materialLayout.setVisibility(View.GONE);
                submitServiceSkill.setVisibility(View.VISIBLE);
                serviceSkillList.setVisibility(View.VISIBLE);
                materiaList.setVisibility(View.GONE);


                break;

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