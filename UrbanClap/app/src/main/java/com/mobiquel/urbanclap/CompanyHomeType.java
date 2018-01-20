package com.mobiquel.urbanclap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.mobiquel.urbanclap.adapter.CompanyTypeAdapter;
import com.mobiquel.urbanclap.model.CompanyTypeModel;
import com.mobiquel.urbanclap.utils.AppConstants;
import com.mobiquel.urbanclap.utils.Preferences;
import com.mobiquel.urbanclap.utils.Utils;
import com.mobiquel.urbanclap.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyHomeType extends AppCompatActivity implements View.OnClickListener {

    private Button nextButton;
    private ProgressBarCircularIndeterminate progressBar;
    private ListView companyList;
    private CompanyTypeAdapter mAdapter;

    private List<CompanyTypeModel> leads = new ArrayList<>();
    private final int PERMISSION_REQUEST = 0;

    private String checkedCompany,uncheckedCOmpany;
    private TextView actionBarTitleTextView;
    private Toolbar toolbar;
    private CheckBox iAcceptTerms;
    private TextView termsAndConditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_select_companytype);

        getSupportActionBar().setTitle("Select Company type");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c0392b")));
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        nextButton= (Button) findViewById(R.id.nextButton);

        termsAndConditions=(TextView)findViewById(R.id.termsAndConditions);
        iAcceptTerms=(CheckBox)findViewById(R.id.iAcceptTerms);

        customTextView(termsAndConditions);


        progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);
        companyList= (ListView) findViewById(R.id.companyList);
        nextButton.setOnClickListener(this);


        mAdapter = new CompanyTypeAdapter(leads, CompanyHomeType.this);
        prepareServicesData();
        companyList.setAdapter(mAdapter);

        iAcceptTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    nextButton.setEnabled(true);
                    nextButton.setBackgroundResource(R.drawable.rectangle_background_blue);

                }
                else
                {
                    nextButton.setEnabled(false);
                    nextButton.setBackgroundResource(R.drawable.rectangle_background_disabled);

                }
            }
        });

    }


    private void prepareServicesData() {

        CompanyTypeModel service = new CompanyTypeModel("1. SERVICE CENTER/ FRANCHISE/ DEALER","");
        leads.add(service);

        service = new CompanyTypeModel("2. LABOR CONTRACTOR/ SERVICE DEALER","");
        leads.add(service);

        service = new CompanyTypeModel("3. MATERIAL/ SPARES SUPPLIERS","");
        leads.add(service);

        service = new CompanyTypeModel("4. HR COMPANIES","");
        leads.add(service);

        /*service = new CompanyTypeModel("5. CORPORATE SELLER","");
        leads.add(service);
*/

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.nextButton:
                if (mAdapter != null)
                {
                    checkedCompany = mAdapter.getSelectedCompanyIds();
                    uncheckedCOmpany = mAdapter.getNotSelectedCompanyIds();

                    Log.e("IDS:", checkedCompany+"  "+uncheckedCOmpany);
                   if(checkedCompany.equals(""))
                   {
                       Utils.showToast(CompanyHomeType.this, "Please select any one from above!");

                   }
                   else  if(checkedCompany.contains("0"))
                   {
                       if(checkedCompany.contains("3"))
                       {
                           Utils.showToast(CompanyHomeType.this, "Please select 1 AND  2 AND 3 option or 4th option or 5th Option!");

                       }
                       else  if(checkedCompany.contains("4"))
                       {
                           Utils.showToast(CompanyHomeType.this, "Please select 1 AND  2 AND 3 option or 4th option or 5th Option!");

                       }
                       else
                       {
                           Preferences.getInstance().loadPreferences(CompanyHomeType.this);
                           Preferences.getInstance().companyTypeValue="SERVICE CENTER/ FRANCHISE/ DEALER";
                           Preferences.getInstance().savePreferences(CompanyHomeType.this);
                           registerForm();
                       }



                   }
                   else  if(checkedCompany.contains("1"))
                   {
                       if(checkedCompany.contains("3"))
                       {
                           Utils.showToast(CompanyHomeType.this, "Please select 1 AND  2 AND 3 option or 4th option or 5th Option!");

                       }
                       else  if(checkedCompany.contains("4"))
                       {
                           Utils.showToast(CompanyHomeType.this, "Please select 1 AND  2 AND 3 option or 4th option or 5th Option!");

                       }
                       else {
                           Preferences.getInstance().loadPreferences(CompanyHomeType.this);
                           Preferences.getInstance().companyTypeValue = "LABOR CONTRACTOR/ SERVICE DEALER";
                           Preferences.getInstance().savePreferences(CompanyHomeType.this);
                           registerForm();
                       }
                   }
                   else  if(checkedCompany.contains("2"))
                   {
                       if(checkedCompany.contains("3"))
                       {
                           Utils.showToast(CompanyHomeType.this, "Please select 1 AND  2 AND 3 option or 4th option or 5th Option!");

                       }
                       else  if(checkedCompany.contains("4"))
                       {
                           Utils.showToast(CompanyHomeType.this, "Please select 1 AND  2 AND 3 option or 4th option or 5th Option!");

                       }
                       else {
                           Preferences.getInstance().loadPreferences(CompanyHomeType.this);
                           Preferences.getInstance().companyTypeValue = "MATERIAL/ SPARES SUPPLIERS";
                           Preferences.getInstance().savePreferences(CompanyHomeType.this);
                           registerForm();
                       }
                   }
                   else  if(checkedCompany.equals("3"))
                   {
                       Preferences.getInstance().loadPreferences(CompanyHomeType.this);
                       Preferences.getInstance().companyTypeValue="HR COMPANIES";
                       Preferences.getInstance().savePreferences(CompanyHomeType.this);
                       registerForm();

                   }
                   else  if(checkedCompany.equals("4"))
                   {
                       Preferences.getInstance().loadPreferences(CompanyHomeType.this);
                       Preferences.getInstance().companyTypeValue="CORPORATE SELLER";
                       Preferences.getInstance().savePreferences(CompanyHomeType.this);
                       registerForm();

                   }
                   else
                   {
                       Utils.showToast(CompanyHomeType.this, "Please select 1 AND  2 AND 3 option or 4th option!");

                   }

                }
                else {
                }
                break;


        }
    }
    private void registerForm(){
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(CompanyHomeType.this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "registerCompany";
        Log.e("FIRST", "CHILD");
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {

                        Preferences.getInstance().loadPreferences(CompanyHomeType.this);
                        Preferences.getInstance().companyName=getIntent().getExtras().getString("NAME");
                        Preferences.getInstance().companyCode=getIntent().getExtras().getString("COMPANY_CODE");
                        Preferences.getInstance().companyCity=getIntent().getExtras().getString("CITY_NAME");
                        Preferences.getInstance().companyId=responseObject.getString("responseObject");
                        Preferences.getInstance().isLoggedIn=true;
                        Preferences.getInstance().companyType=checkedCompany.replaceAll(",","").toString();
                        Preferences.getInstance().savePreferences(CompanyHomeType.this);
                        Utils.showToast(CompanyHomeType.this,responseObject.getString("errorMessage"));
                        if(Preferences.getInstance().companyType.equals("3"))
                        {
                            Intent i=new Intent(CompanyHomeType.this,CompanyHomeHR.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();

                        }
                        else if(Preferences.getInstance().companyType.equals("4"))
                        {
                            Intent i=new Intent(CompanyHomeType.this,CompanyHomeCorporate.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();

                        }
                        else
                        {
                            Intent i=new Intent(CompanyHomeType.this,CompanyHome.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        }

                    } else {
                        //Utils.showToast(CompanyHomeType.this, responseObject.getString("errorMessage"));
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
                Preferences.getInstance().loadPreferences(CompanyHomeType.this);
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(CompanyHomeType.this);

                params.put("name",getIntent().getExtras().getString("NAME"));
                params.put("cityId",getIntent().getExtras().getString("CITY_ID"));
                params.put("locationId",getIntent().getExtras().getString("LOCATION_ID"));
                params.put("referralCompanyCode","");
                params.put("inviteCode",getIntent().getExtras().getString("INVITE_CODE"));
                params.put("mobile",Preferences.getInstance().phoneNumber);
                params.put("panNumber","");
                params.put("gstNumber","");
                params.put("email","");
                params.put("address",getIntent().getExtras().getString("CITY_NAME"));
                params.put("companyCategory",checkedCompany.replaceAll(",$", ""));

                Log.e("PARAMS",params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(CompanyHomeType.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(CompanyHomeType.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
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
