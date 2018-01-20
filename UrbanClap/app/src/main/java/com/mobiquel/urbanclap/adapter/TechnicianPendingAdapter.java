package com.mobiquel.urbanclap.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.mobiquel.urbanclap.R;
import com.mobiquel.urbanclap.utils.AppConstants;
import com.mobiquel.urbanclap.utils.Preferences;
import com.mobiquel.urbanclap.utils.Utils;
import com.mobiquel.urbanclap.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class TechnicianPendingAdapter extends RecyclerView.Adapter<TechnicianPendingAdapter.MyViewHolder> {

    private Context context;
    private JSONArray techArray;
    private String type;
    private ProgressBarCircularIndeterminate progressBar;
    private RecyclerView techList;
    private TextView noRecordsFound;



    public TechnicianPendingAdapter(Context context, JSONArray techArray, String type, ProgressBarCircularIndeterminate progressBar, RecyclerView techList, TextView noRecordsFound) {
        this.techArray = techArray;
        this.type=type;
        this.context=context;
        this.progressBar=progressBar;
        this.techList=techList;
        this.noRecordsFound=noRecordsFound;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hr_status_pending_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        try {
            holder.technicianName.setText(techArray.getJSONObject(position).getString("name"));
            holder.technicianMobile.setText(techArray.getJSONObject(position).getString("mobile"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return techArray.length();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView technicianName,technicianMobile,statusText;
        private EditText code;
        private Button sendCode,pendingForApproval,verifyCode;

        public MyViewHolder(View view) {
            super(view);
            technicianName = (TextView) view.findViewById(R.id.technicianName);
            technicianMobile = (TextView) view.findViewById(R.id.technicianMobile);
            statusText = (TextView) view.findViewById(R.id.statusText);

            code = (EditText) view.findViewById(R.id.code);
            sendCode = (Button) view.findViewById(R.id.sendCode);
            verifyCode = (Button) view.findViewById(R.id.verifyCode);

            pendingForApproval = (Button) view.findViewById(R.id.pendingForApproval);

            code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager inputMethodManager =
                            (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInputFromWindow(
                            code.getApplicationWindowToken(),
                            InputMethodManager.SHOW_FORCED, 0);
                    openPopUp(code.getText().toString(),code);

                }
            });
            if(type.equalsIgnoreCase("PENDING"))
            {
                code.setVisibility(View.VISIBLE);

                pendingForApproval.setVisibility(View.GONE);
                statusText.setVisibility(View.GONE);
                sendCode.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        resendCode(technicianMobile.getText().toString());
                    }
                });
                verifyCode.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if(code.getText().toString().equals(""))
                        {
                            Utils.showToast(TechnicianPendingAdapter.this.context,"Please provide code first!");
                        }
                        else
                        {
                            verifyCode(technicianMobile.getText().toString(),code.getText().toString());
                        }
                    }
                });

            }
            else if(type.equalsIgnoreCase("VERIFIED"))
            {
                code.setVisibility(View.GONE);
                verifyCode.setVisibility(View.GONE);
                sendCode.setText("Send App");
                pendingForApproval.setVisibility(View.GONE);
                statusText.setVisibility(View.GONE);
                sendCode.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        shareAppLink(technicianMobile.getText().toString());

                    }
                });
            }
            else if(type.equalsIgnoreCase("MEMBER"))
            {
                code.setVisibility(View.GONE);
                sendCode.setVisibility(View.GONE);
                pendingForApproval.setVisibility(View.GONE);
                statusText.setVisibility(View.GONE);
                verifyCode.setVisibility(View.GONE);

            }
            else if(type.equalsIgnoreCase("APPROVED"))
            {
                code.setVisibility(View.GONE);
                sendCode.setVisibility(View.GONE);
                pendingForApproval.setVisibility(View.GONE);
                statusText.setVisibility(View.GONE);
                verifyCode.setVisibility(View.GONE);

            }


        }
    }

    private void verifyCode(final String technicianMobile,final String code) {
        RequestQueue queue = VolleySingleton.getInstance(TechnicianPendingAdapter.this.context).getRequestQueue();
        String url= AppConstants.SERVER_URL+"verifyUserReferCode";
        progressBar.setVisibility(View.VISIBLE);

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
                        Utils.showToast(TechnicianPendingAdapter.this.context,responseObject.getString("errorMessage"));
                        getHRReferedTechnicianList("PENDING");
                    }
                    else
                    {
                        Utils.showToast(TechnicianPendingAdapter.this.context,responseObject.getString("errorMessage"));
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
            { if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
                Utils.showToast(TechnicianPendingAdapter.this.context, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(TechnicianPendingAdapter.this.context);
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile",technicianMobile);
                params.put("code",code);
                params.put("userId",Preferences.getInstance().userId);
                params.put("userType","TECHNICIAN");
                params.put("verificationType","referred");
                Log.e("VERIFY_PARAM",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(TechnicianPendingAdapter.this.context)) {
            queue.add(requestObject);
        }
        else
        { if (progressBar != null && progressBar.isShown()) {
            progressBar.setVisibility(View.GONE);
        }
            Utils.showToast(TechnicianPendingAdapter.this.context, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private void resendCode(final String technicianMobile) {
        RequestQueue queue = VolleySingleton.getInstance(TechnicianPendingAdapter.this.context).getRequestQueue();
        String url= AppConstants.SERVER_URL+"resendHRReferCode";
        progressBar.setVisibility(View.VISIBLE);

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
                        Utils.showToast(TechnicianPendingAdapter.this.context,responseObject.getString("errorMessage"));
                        getHRReferedTechnicianList("PENDING");
                    }
                    else
                    {
                        Utils.showToast(TechnicianPendingAdapter.this.context,responseObject.getString("errorMessage"));
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
            { if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
                Utils.showToast(TechnicianPendingAdapter.this.context, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(TechnicianPendingAdapter.this.context);
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile",technicianMobile);
                params.put("userId",Preferences.getInstance().userId);
                params.put("userType","TECHNICIAN");

                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(TechnicianPendingAdapter.this.context)) {
            queue.add(requestObject);
        }
        else
        { if (progressBar != null && progressBar.isShown()) {
            progressBar.setVisibility(View.GONE);
        }
            Utils.showToast(TechnicianPendingAdapter.this.context, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private void shareAppLink(final String technicianMobile) {
        RequestQueue queue = VolleySingleton.getInstance(TechnicianPendingAdapter.this.context).getRequestQueue();
        String url= AppConstants.SERVER_URL+"shareAppLink";
        progressBar.setVisibility(View.VISIBLE);

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
                        Utils.showToast(TechnicianPendingAdapter.this.context,responseObject.getString("errorMessage"));
                    }
                    else
                    {
                        Utils.showToast(TechnicianPendingAdapter.this.context,responseObject.getString("errorMessage"));
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
            { if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
                Utils.showToast(TechnicianPendingAdapter.this.context, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(TechnicianPendingAdapter.this.context);
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile",technicianMobile);
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(TechnicianPendingAdapter.this.context)) {
            queue.add(requestObject);
        }
        else
        { if (progressBar != null && progressBar.isShown()) {
            progressBar.setVisibility(View.GONE);
        }
            Utils.showToast(TechnicianPendingAdapter.this.context, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }
    private void openPopUp(String code, final EditText codeField) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.code_popup);
        dialog.setCancelable(true);
        final EditText enterCode = (EditText) dialog.findViewById(R.id.enterCode);
        Button okButton = (Button) dialog.findViewById(R.id.okbutton);
        enterCode.setText(code);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeField.setText(enterCode.getText().toString());
                dialog.cancel();

            }
        });

        dialog.show();

    }

    private void getHRReferedTechnicianList(String pend) {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();
        String url= AppConstants.SERVER_URL+"getUserReferredList";

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
                        techArray=new JSONArray();
                        techArray=responseObject.getJSONArray("responseObject");

                        if(techArray.length()==0)
                        {
                            noRecordsFound.setVisibility(View.VISIBLE);
                            techList.setVisibility(View.GONE);
                        }
                        else
                        {
                            noRecordsFound.setVisibility(View.GONE);
                            techList.setVisibility(View.VISIBLE);

                            TechnicianPendingAdapter mAdapter=new TechnicianPendingAdapter(context,techArray,"PENDING",progressBar,techList,noRecordsFound);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                            techList.setLayoutManager(mLayoutManager);
                            techList.setItemAnimator(new DefaultItemAnimator());
                            techList.setAdapter(mAdapter);
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
                Utils.showToast(context, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(context);
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId",Preferences.getInstance().userId);
                params.put("userType","TECHNICIAN");
                params.put("type","PENDING");
                Log.e("PARAMS_TECH",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(context)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(context, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }
}
