package com.mobiquel.shank;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.mobiquel.shank.utils.AppConstants;
import com.mobiquel.shank.utils.Preferences;
import com.mobiquel.shank.utils.Utils;
import com.mobiquel.shank.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsTechnician extends AppCompatActivity
{
    private Button logout,leave,joinCompany;
    private TextView inviteCode, companyCode, mobileNumber;
    private LinearLayout leaveLayout;
    private ProgressBarCircularIndeterminate progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        logout = (Button) findViewById(R.id.logOut);
        joinCompany = (Button) findViewById(R.id.joinCompany);

        leave = (Button) findViewById(R.id.leave);
        progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);

        inviteCode = (TextView) findViewById(R.id.inviteCode);
        companyCode = (TextView) findViewById(R.id.companyCode);
        mobileNumber = (TextView) findViewById(R.id.phoneNumber);
        leaveLayout = (LinearLayout) findViewById(R.id.leaveLayout);

        Preferences.getInstance().loadPreferences(this);
        if (Preferences.getInstance().companyCode.equals(""))
        {
            companyCode.setText(companyCode.getText().toString() + "-");
            leaveLayout.setVisibility(View.GONE);
            joinCompany.setVisibility(View.VISIBLE);
        }
        else
        {
            companyCode.setText(companyCode.getText().toString() + Preferences.getInstance().companyCode);
            leaveLayout.setVisibility(View.VISIBLE);
            joinCompany.setVisibility(View.GONE);
        }


        if (Preferences.getInstance().hrReferralCode.equals(""))
        {
            inviteCode.setText(inviteCode.getText().toString() + "-");
        }
        else
        {
            inviteCode.setText(inviteCode.getText().toString() + Preferences.getInstance().hrReferralCode);
        }

        if (Preferences.getInstance().phoneNumber.equals(""))
        {
            mobileNumber.setText(mobileNumber.getText().toString() + "-");
        }
        else
        {
            mobileNumber.setText(mobileNumber.getText().toString() + Preferences.getInstance().phoneNumber);
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsTechnician.this);
                builder.setMessage("Are you sure you want to logout?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Preferences.getInstance().loadPreferences(SettingsTechnician.this);
                                Preferences.getInstance().isLoggedIn = false;
                                Preferences.getInstance().companyType = "";
                                Preferences.getInstance().savePreferences(SettingsTechnician.this);
                                Intent i = new Intent(SettingsTechnician.this, MobileOTPActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsTechnician.this);
                builder.setMessage("Are you sure you want to leave the Company?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                initiateCompanyLeaveRequest();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        joinCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopUp();
            }
        });
    }

    private void openPopUp() {
        final Dialog dialog = new Dialog(SettingsTechnician.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.comp_code_popup);
        dialog.setCancelable(true);
        final EditText compCode = (EditText) dialog.findViewById(R.id.enterCode);

        Button okButton = (Button) dialog.findViewById(R.id.okbutton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(compCode.getText().toString().equals(""))
                {
                    Utils.showToast(SettingsTechnician.this,"Please enter Company Code!");
                }
                else
                {
                    initiateCompanyJoinRequest(compCode.getText().toString());
                    dialog.cancel();
                }


            }
        });

        dialog.show();

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

    private void initiateCompanyLeaveRequest(){
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(SettingsTechnician.this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "initiateCompanyLeaveRequest";
        Log.e("FIRST", "CHILD");
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_PROFILE",responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        Utils.showToast(SettingsTechnician.this, responseObject.getString("errorMessage"));
                        SettingsTechnician.this.finish();

                    } else {
                         Utils.showToast(SettingsTechnician.this, responseObject.getString("errorMessage"));
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
                Preferences.getInstance().loadPreferences(SettingsTechnician.this);
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(SettingsTechnician.this);
                params.put("technicianId",Preferences.getInstance().userId);
                params.put("companyCode",Preferences.getInstance().companyCode);

                Log.e("PARAMS",params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(SettingsTechnician.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(SettingsTechnician.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private void initiateCompanyJoinRequest(final String compCode){
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(SettingsTechnician.this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "initiateCompanyJoinRequest";
        Log.e("FIRST", "CHILD");
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_PROFILE",responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        Utils.showToast(SettingsTechnician.this, responseObject.getString("errorMessage"));
                        SettingsTechnician.this.finish();

                    } else {
                         Utils.showToast(SettingsTechnician.this, responseObject.getString("errorMessage"));
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
                Preferences.getInstance().loadPreferences(SettingsTechnician.this);
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(SettingsTechnician.this);
                params.put("technicianId",Preferences.getInstance().userId);
                params.put("companyCode",compCode);

                Log.e("PARAMS",params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(SettingsTechnician.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(SettingsTechnician.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }
}
