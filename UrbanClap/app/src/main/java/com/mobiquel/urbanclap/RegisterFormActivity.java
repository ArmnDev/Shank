package com.mobiquel.urbanclap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class RegisterFormActivity extends AppCompatActivity {

    private EditText name, companyCode, inviteCode, email;
    private RadioGroup joinGroup;
    private Button join;
    private Spinner city, serviceLocation;
    private ProgressBarCircularIndeterminate progressBar;
    private JSONArray cities, locationJSONArray;
    private String cityId, getCityId;
    private LinearLayout locationLayout;
    private TextView actionBarTitleTextView,joinAsLabel;
    private Toolbar toolbar;
    private TextInputLayout companyCodeLayout;
    private RadioButton companyJoin,individualJoin;
    private CheckBox iAcceptTerms;
    private TextView termsAndConditions;
    private LinearLayout termsAndConditionsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);

        getSupportActionBar().setTitle("Registration Form");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c0392b")));
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        name = (EditText) findViewById(R.id.name);
        companyCode = (EditText) findViewById(R.id.compCode);
        inviteCode = (EditText) findViewById(R.id.inviteCode);
        email = (EditText) findViewById(R.id.email);
        joinAsLabel=(TextView)findViewById(R.id.joinAsLabel);
        termsAndConditions=(TextView)findViewById(R.id.termsAndConditions);
        iAcceptTerms=(CheckBox)findViewById(R.id.iAcceptTerms);
        termsAndConditionsLayout= (LinearLayout) findViewById(R.id.termsAndConditionsLayout);
        companyCodeLayout = (TextInputLayout) findViewById(R.id.companyCodeLayout);

        progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);
        city = (Spinner) findViewById(R.id.city);
        locationLayout = (LinearLayout) findViewById(R.id.locationLayout);
        serviceLocation = (Spinner) findViewById(R.id.serviceLocation);

        joinGroup = (RadioGroup) findViewById(R.id.joinGroup);
        companyJoin = (RadioButton) findViewById(R.id.companyJoin);
        individualJoin = (RadioButton) findViewById(R.id.individualJoin);
        join = (Button) findViewById(R.id.joinForm);

       customTextView(termsAndConditions);

        if(!getIntent().getExtras().getString("HR_REF_CODE").equals("")||!getIntent().getExtras().getString("COMP_CODE").equals(""))
        {
            joinGroup.setEnabled(false);
            individualJoin.setEnabled(false);
            companyJoin.setEnabled(false);

            individualJoin.setChecked(true);
            companyCodeLayout.setVisibility(View.VISIBLE);

           joinAsLabel.setVisibility(View.GONE);
           joinGroup.setVisibility(View.GONE);
        }
        if(getIntent().getExtras().getString("HR_REF_CODE").equals(""))
        {

        }
        else
        {
            inviteCode.setText(getIntent().getExtras().getString("HR_REF_CODE"));
            inviteCode.setEnabled(false);
        }
        if(getIntent().getExtras().getString("COMP_CODE").equals(""))
        {

        }
        else
        {
            companyCode.setText(getIntent().getExtras().getString("COMP_CODE"));
            companyCode.setEnabled(false);
        }
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {


                try {
                    cityId = cities.getJSONObject(pos).getString("cityId");
                    getServiceLocations(cityId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

        getCities();

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")) {
                    Utils.showToast(RegisterFormActivity.this, "Please enter name!");
                } else if (joinGroup.getCheckedRadioButtonId() == -1) {
                    Utils.showToast(RegisterFormActivity.this, "Please select any one join type!");
                } else {
                    //  RadioButton r=findViewById(joinGroup.getCheckedRadioButtonId());
                    int id = joinGroup.getCheckedRadioButtonId();
                    RadioButton r1 = (RadioButton) findViewById(id);
                    String joinAs = r1.getText().toString();
                    Preferences.getInstance().loadPreferences(RegisterFormActivity.this);
                    Preferences.getInstance().userType = joinAs;
                    Preferences.getInstance().savePreferences(RegisterFormActivity.this);

                    if (joinAs.equalsIgnoreCase("INDIVIDUAL")) {
                        registerForm();
                    } else {
                        Intent i = new Intent(RegisterFormActivity.this, CompanyHomeType.class);
                        i.putExtra("NAME", name.getText().toString());
                        try {
                            i.putExtra("CITY_ID", cities.getJSONObject(city.getSelectedItemPosition()).getString("cityId"));
                            i.putExtra("CITY_NAME", cities.getJSONObject(city.getSelectedItemPosition()).getString("name"));
                            i.putExtra("LOCATION_ID", locationJSONArray.getJSONObject(serviceLocation.getSelectedItemPosition()).getString("locationId"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        i.putExtra("INVITE_CODE", inviteCode.getText().toString());

                        startActivity(i);
                        finish();
                    }

                }

            }
        });

        joinGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                int id = joinGroup.getCheckedRadioButtonId();
                RadioButton r1 = (RadioButton) findViewById(id);
                String carExchange = r1.getText().toString();
                if (carExchange.equalsIgnoreCase("COMPANY")) {
                    locationLayout.setVisibility(View.VISIBLE);
                    companyCodeLayout.setVisibility(View.GONE);
                    termsAndConditionsLayout.setVisibility(View.GONE);
                    join.setEnabled(true);
                    join.setBackgroundResource(R.drawable.rectangle_background_blue);

                } else {
                    locationLayout.setVisibility(View.GONE);
                    companyCodeLayout.setVisibility(View.VISIBLE);
                    termsAndConditionsLayout.setVisibility(View.VISIBLE);
                    join.setEnabled(false);
                    join.setBackgroundResource(R.drawable.rectangle_background_disabled);
                }

            }
        });

        iAcceptTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    join.setEnabled(true);
                    join.setBackgroundResource(R.drawable.rectangle_background_blue);

                }
                else
                {
                    join.setEnabled(false);
                    join.setBackgroundResource(R.drawable.rectangle_background_disabled);

                }
            }
        });
    }

    private void registerForm() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(RegisterFormActivity.this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "registerTechnician";
        Log.e("FIRST", "CHILD");
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {

                        Preferences.getInstance().loadPreferences(RegisterFormActivity.this);
                        Preferences.getInstance().userId = responseObject.getString("responseObject");
                        Preferences.getInstance().userName = name.getText().toString();
                        Preferences.getInstance().email = email.getText().toString();
                        Preferences.getInstance().companyCode = companyCode.getText().toString();

                        Preferences.getInstance().isLoggedIn = true;

                        // Preferences.getInstance().email=email.getText().toString();
                        Preferences.getInstance().savePreferences(RegisterFormActivity.this);
                        Intent i = new Intent(RegisterFormActivity.this, Home.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();

                    } else {
                        // Utils.showToast(RegisterFormActivity.this, responseObject.getString("errorMessage"));
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
        }) {


            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(RegisterFormActivity.this);
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(RegisterFormActivity.this);
                params.put("name", name.getText().toString());
                params.put("mobile", Preferences.getInstance().phoneNumber);
                try {
                    params.put("cityId", cities.getJSONObject(city.getSelectedItemPosition()).getString("cityId"));
                    params.put("locationId", locationJSONArray.getJSONObject(serviceLocation.getSelectedItemPosition()).getString("locationId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("referralCompanyCode", companyCode.getText().toString());
                params.put("email", email.getText().toString());
                params.put("qualification", "");
                params.put("addressHome", "");
                params.put("inviteCode", inviteCode.getText().toString());

                params.put("addressPresent", "");
                params.put("aadharCard", "");
                params.put("dob", "");
                params.put("deviceOS", "Android");
                Log.e("PARAMS", params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(RegisterFormActivity.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(RegisterFormActivity.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private void getCities() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(RegisterFormActivity.this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "getCityList";
        Log.e("FIRST", "CHILD");
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        cities = responseObject.getJSONObject("responseObject").getJSONArray("cityList");
                        Log.e("CITY_LIST", " " + cities.toString());
                        String cityArray[] = new String[cities.length()];
                        for (int i = 0; i < cities.length(); i++) {
                            cityArray[i] = cities.getJSONObject(i).getString("name");
                        }
                        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(RegisterFormActivity.this,
                                android.R.layout.simple_list_item_1, cityArray);
                        cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        city.setAdapter(cityAdapter);
                        getServiceLocations("1");


                    } else {
                        // Utils.showToast(RegisterFormActivity.this, responseObject.getString("errorMessage"));
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
        }) {


            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(RegisterFormActivity.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("startIndex", "-1");
                params.put("length", "10");
                params.put("searchString", "");
                params.put("sortBy", "CITY_NAME");
                params.put("order", "A");

                Log.e("PARAMS", params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(RegisterFormActivity.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(RegisterFormActivity.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private void getServiceLocations(final String getCityId) {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(RegisterFormActivity.this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "getCityLocationList";
        Log.e("FIRST", "CHILD");
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        locationJSONArray = responseObject.getJSONObject("responseObject").getJSONArray("locationList");
                        String locationArray[] = new String[locationJSONArray.length()];
                        for (int i = 0; i < locationJSONArray.length(); i++) {
                            locationArray[i] = locationJSONArray.getJSONObject(i).getString("name");
                        }
                        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(RegisterFormActivity.this,
                                android.R.layout.simple_list_item_1, locationArray);
                        cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        serviceLocation.setAdapter(cityAdapter);


                    } else {
                        //  Utils.showToast(RegisterFormActivity.this, responseObject.getString("errorMessage"));
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
        }) {


            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(RegisterFormActivity.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("cityId", getCityId);
                params.put("startIndex", "-1");
                params.put("length", "10");
                params.put("searchString", "");
                params.put("sortBy", "LOCATION_NAME");
                params.put("order", "A");
                Log.e("PARAMS", params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(RegisterFormActivity.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(RegisterFormActivity.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }
    private void customTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "By Registering, you are accepting our ");
        spanTxt.append("Terms");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.in/"));
                startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);

            }
        }, spanTxt.length() - "Terms".length(), spanTxt.length(), 0);
        spanTxt.append(" and ");
        spanTxt.append("Privacy Policy");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.in/"));
                startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);

            }
        }, spanTxt.length() - "Privacy Policy".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }
}
