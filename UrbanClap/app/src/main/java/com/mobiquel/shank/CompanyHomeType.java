package com.mobiquel.shank;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.mobiquel.shank.adapter.CompanyTypeAdapter;
import com.mobiquel.shank.model.CompanyTypeModel;
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
    private CheckBox chkBox1,chkBox2,chkBox3,chkBox4;
    private String checkedValues="";

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
        chkBox1= (CheckBox) findViewById(R.id.chkBox1);
        chkBox2= (CheckBox) findViewById(R.id.chkBox2);
        chkBox3= (CheckBox) findViewById(R.id.chkBox3);
        chkBox4= (CheckBox) findViewById(R.id.chkBox4);

        mAdapter = new CompanyTypeAdapter(leads, CompanyHomeType.this);
        prepareServicesData();
        companyList.setAdapter(mAdapter);

        iAcceptTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    if(chkBox1.isChecked()||chkBox2.isChecked()||chkBox3.isChecked()||chkBox4.isChecked())
                    {
                        nextButton.setEnabled(true);
                        nextButton.setBackgroundResource(R.drawable.rectangle_background_blue);
                    }


                }
                else
                {
                    nextButton.setEnabled(false);
                    nextButton.setBackgroundResource(R.drawable.rectangle_background_disabled);

                }
            }
        });

        chkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if(iAcceptTerms.isChecked())
                    {
                        nextButton.setEnabled(true);
                        nextButton.setBackgroundResource(R.drawable.rectangle_background_blue);
                    }

                    chkBox4.setEnabled(false);
                    chkBox4.setChecked(false);

                }
                else {
                    if(!chkBox2.isChecked()&&!chkBox3.isChecked())
                    {
                        chkBox4.setEnabled(true);

                    }
                    checkedValues=checkedValues.replaceAll("0","");
                }
            }
        });

        chkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if(iAcceptTerms.isChecked())
                    {
                        nextButton.setEnabled(true);
                        nextButton.setBackgroundResource(R.drawable.rectangle_background_blue);
                    }
                    chkBox4.setEnabled(false);
                    chkBox4.setChecked(false);
                }
                else {
                    if(!chkBox1.isChecked()&&!chkBox3.isChecked())
                    {
                        chkBox4.setEnabled(true);

                    }

                }
            }
        });
        chkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if(iAcceptTerms.isChecked())
                    {
                        nextButton.setEnabled(true);
                        nextButton.setBackgroundResource(R.drawable.rectangle_background_blue);
                    }
                    chkBox4.setEnabled(false);
                    chkBox4.setChecked(false);

                }
                else {
                    if(!chkBox1.isChecked()&&!chkBox2.isChecked())
                    {
                        chkBox4.setEnabled(true);

                    }

                }
            }
        });

        chkBox4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if(iAcceptTerms.isChecked())
                    {
                        nextButton.setEnabled(true);
                        nextButton.setBackgroundResource(R.drawable.rectangle_background_blue);
                    }
                    chkBox1.setEnabled(false);
                    chkBox2.setEnabled(false);
                    chkBox3.setEnabled(false);
                    chkBox1.setChecked(false);
                    chkBox2.setChecked(false);
                    chkBox3.setChecked(false);

                }
                else {
                    chkBox1.setEnabled(true);
                    chkBox2.setEnabled(true);
                    chkBox3.setEnabled(true);
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

                       registerForm();


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
                        if(chkBox4.isChecked())
                        {
                            Preferences.getInstance().companyType="3";
                            Preferences.getInstance().savePreferences(CompanyHomeType.this);

                        }
                        else
                        {
                            if(chkBox1.isChecked())
                            {
                                checkedValues=checkedValues+"1,";
                            }
                            if(chkBox2.isChecked())
                            {
                                checkedValues=checkedValues+"2,";
                            }
                            if(chkBox3.isChecked())
                            {
                                checkedValues=checkedValues+"3,";
                            }
                            Preferences.getInstance().companyType=checkedValues.replaceAll(",$", "");
                            Preferences.getInstance().savePreferences(CompanyHomeType.this);

                        }
                        Preferences.getInstance().savePreferences(CompanyHomeType.this);
                        Utils.showToast(CompanyHomeType.this,responseObject.getString("errorMessage"));
                        if(Preferences.getInstance().companyType.equals("3"))
                        {
                            Intent i=new Intent(CompanyHomeType.this,CompanyHomeHR.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();

                        }

                        else
                        {
                            Intent i=new Intent(CompanyHomeType.this,CompanyHome.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("SOURCE","MAP");
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
                if(chkBox4.isChecked())
                {
                    params.put("companyCategory","3");
                }
                else
                {
                    if(chkBox1.isChecked())
                    {
                        checkedValues=checkedValues+"0,";
                    }
                    if(chkBox2.isChecked())
                    {
                        checkedValues=checkedValues+"1,";
                    }
                    if(chkBox3.isChecked())
                    {
                        checkedValues=checkedValues+"2,";
                    }
                    params.put("companyCategory",checkedValues.replaceAll(",$", ""));

                }

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
