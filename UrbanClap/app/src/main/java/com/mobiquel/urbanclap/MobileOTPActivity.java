package com.mobiquel.urbanclap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MobileOTPActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mobile,otp;
    private TextView resendOtp;
    private Button nextButton,verifyOtp;
    private ProgressBarCircularIndeterminate progressBar;
    private String source="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mobile);

        mobile= (EditText) findViewById(R.id.mobileField);
        otp= (EditText) findViewById(R.id.otpField);
        progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);

        resendOtp= (TextView) findViewById(R.id.resendOtp);
        resendOtp.setPaintFlags(resendOtp.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        nextButton= (Button) findViewById(R.id.next);
        verifyOtp= (Button) findViewById(R.id.verifyOtp);

        nextButton.setOnClickListener(MobileOTPActivity.this);
        verifyOtp.setOnClickListener(MobileOTPActivity.this);
        resendOtp.setOnClickListener(MobileOTPActivity.this);

    }


    @Override
    public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.accountLogin:
                    break;
                case R.id.next:
                    if(mobile.getText().toString().equals(""))
                    {
                        Utils.showToast(MobileOTPActivity.this,"Please enter Mobile Number!");
                    }
                    else if(!Utils.validatePhoneNumber(mobile.getText().toString()))
                    {
                        Utils.showToast(MobileOTPActivity.this,"Please enter valid Mobile Number!");
                    }
                    else
                    {
                            getOtp();
                    }
                    break;
                case R.id.verifyOtp:
                    if(otp.getText().toString().equals(""))
                    {
                        Utils.showToast(MobileOTPActivity.this,"Please enter OTP");
                    }
                    else

                    {
                        verifyOtp();
                    }
                    break;
                case R.id.resendOtp:
                    getOtp();
                    break;
            }

        registerReceiver(broadcastReceiver, new IntentFilter("OTP"));

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String otp1 = intent.getExtras().getString("OTP");
            otp.setText(otp1.substring(otp1.indexOf(".")-4,otp1.indexOf(".")));
            verifyOtp();

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
    protected void getOtp() {
        // TODO Auto-generated method stub
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(MobileOTPActivity.this).getRequestQueue();
        String url = AppConstants.SERVER_URL + AppConstants.GET_OTP;
        Log.e("FIRST", "CHILD");
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {

                        otp.setVisibility(View.VISIBLE);
                        resendOtp.setVisibility(View.VISIBLE);
                        verifyOtp.setVisibility(View.VISIBLE);
                        mobile.setVisibility(View.GONE);
                        nextButton.setVisibility(View.GONE);
                        otp.setEnabled(true);
                        resendOtp.setEnabled(true);
                        verifyOtp.setEnabled(true);
                        verifyOtp.setBackgroundResource(R.drawable.rectangle_background_blue);

                     } else {
                       // Utils.showToast(MobileOTPActivity.this, responseObject.getString("errorMessage"));
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
                Preferences.getInstance().loadPreferences(MobileOTPActivity.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobileNumber",mobile.getText().toString());

                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(MobileOTPActivity.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(MobileOTPActivity.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }


    protected void verifyOtp() {
        // TODO Auto-generated method stub
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(MobileOTPActivity.this).getRequestQueue();
        String url = AppConstants.SERVER_URL + AppConstants.VERIFY_OTP;
        Log.e("FIRST", "CHILD");
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_VERIFY_OTP",responseObject.toString());

                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {

                        //"responseObject":{"userId":null,"isRegistered":null,"referralCode":null,"userType":null},
                        Preferences.getInstance().loadPreferences(MobileOTPActivity.this);
                        Preferences.getInstance().phoneNumber=mobile.getText().toString();
                        Preferences.getInstance().savePreferences(MobileOTPActivity.this);

                        if(responseObject.getJSONObject("responseObject").getString("isRegistered").equals("T"))
                        {
                            if(responseObject.getJSONObject("responseObject").getString("userType").equals("C"))
                            {
                                Preferences.getInstance().loadPreferences(MobileOTPActivity.this);
                                Preferences.getInstance().companyId=responseObject.getJSONObject("responseObject").getString("userId");
                                //Preferences.getInstance().referralCode=responseObject.getJSONObject("responseObject").getString("referralCode");
                                Preferences.getInstance().userType="COMPANY";
                                Preferences.getInstance().companyType=responseObject.getJSONObject("responseObject").getString("companyType");
                                Preferences.getInstance().isLoggedIn=true;
                                Preferences.getInstance().savePreferences(MobileOTPActivity.this);
                                if(responseObject.getJSONObject("responseObject").getString("companyType").equals("4"))
                                {
                                    Intent i=new Intent(MobileOTPActivity.this,CompanyHomeCorporate.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                }
                                else if(responseObject.getJSONObject("responseObject").getString("companyType").equals("3"))
                                {
                                    Intent i=new Intent(MobileOTPActivity.this,CompanyHomeHR.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                }
                                else
                                {
                                    Intent i=new Intent(MobileOTPActivity.this,CompanyHome.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                }

                            }
                            else
                            {
                                Preferences.getInstance().loadPreferences(MobileOTPActivity.this);
                                Preferences.getInstance().userId=responseObject.getJSONObject("responseObject").getString("userId");
                                //Preferences.getInstance().referralCode=responseObject.getJSONObject("responseObject").getString("referralCode");
                                Preferences.getInstance().userType="INDIVIDUAL";
                                Preferences.getInstance().isLoggedIn=true;
                                Preferences.getInstance().savePreferences(MobileOTPActivity.this);
                                Intent i=new Intent(MobileOTPActivity.this,Home.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }

                        }
                        else
                        {
                            String hrRefCode="";
                            String compCode="";
                            if(responseObject.getJSONObject("responseObject").isNull("hrReferralCode"))
                            {
                                 hrRefCode="";
                                Preferences.getInstance().loadPreferences(MobileOTPActivity.this);
                                Preferences.getInstance().hrReferralCode=hrRefCode;
                                Preferences.getInstance().savePreferences(MobileOTPActivity.this);
                            }
                            else
                            {
                                 hrRefCode=responseObject.getJSONObject("responseObject").getString("hrReferralCode");
                                Preferences.getInstance().loadPreferences(MobileOTPActivity.this);
                                Preferences.getInstance().hrReferralCode=hrRefCode;
                                Preferences.getInstance().savePreferences(MobileOTPActivity.this);
                            }
                            if(responseObject.getJSONObject("responseObject").isNull("companyCode"))
                            {
                                 compCode="";
                            }
                            else
                            {
                                 compCode=responseObject.getJSONObject("responseObject").getString("companyCode");

                            }
                            Intent i=new Intent(MobileOTPActivity.this,RegisterFormActivity.class);
                            i.putExtra("HR_REF_CODE",hrRefCode);
                            i.putExtra("COMP_CODE",compCode);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        }

                    } else {
                        //Utils.showToast(MobileOTPActivity.this, "Error fetching data!Please try again.");
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
                Preferences.getInstance().loadPreferences(MobileOTPActivity.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobileNumber",mobile.getText().toString());
                params.put("otp",otp.getText().toString());
                Log.e("PARAMS","OTP "+params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(MobileOTPActivity.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(MobileOTPActivity.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
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
