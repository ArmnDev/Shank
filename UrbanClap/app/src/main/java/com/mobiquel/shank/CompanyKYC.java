package com.mobiquel.shank;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.mobiquel.shank.model.PartnerKYCVO;
import com.mobiquel.shank.utils.AppConstants;
import com.mobiquel.shank.utils.Preferences;
import com.mobiquel.shank.utils.Utils;
import com.mobiquel.shank.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyKYC extends AppCompatActivity implements OnClickListener {


    private ProgressBarCircularIndeterminate progressBar;
    private Spinner companyType;
    private EditText adharNo, voterId;
    private Spinner noOfPartner, noOfDirector;
    private LinearLayout noOfPartnerLayout, addPartnerLayout;
    private LinearLayout noOfDirectorLayout, addDirectorLayout;
    private LinearLayout proprietaryLayout;
    private Button submitCompanyKYC;
    private static EditText dateOfBirth;
    private List<PartnerKYCVO> partnerViewInstance = new ArrayList<PartnerKYCVO>();
    //private List<CompanyKYCPartnerModel> partnerViewInstance = new ArrayList<CompanyKYCPartnerModel>();
    //private PartnerKYCVO o1,o2,o3,o4,o5;
    private EditText name, mobile, adhar;
    private PartnerKYCVO[] partnerVO;
    private TextView actionBarTitleTextView;
    private Toolbar toolbar;
    private JSONArray jArray;

    private int labelId = 0;
    private int nameId = 1;
    private int mobileId = 2;
    private int adharId = 3;
    private int directView = 0;
    private List<EditText> nameEds;
    private List<EditText> mobileEds;
    private List<EditText> adharEds;
    private JSONArray partnerKYC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_kyc);
        getSupportActionBar().setTitle("KYC");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c0392b")));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

       /* o1=new PartnerKYCVO();
        o2=new PartnerKYCVO();
        o3=new PartnerKYCVO();
        o4=new PartnerKYCVO();
        o5=new PartnerKYCVO();*/
        progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);

        companyType = (Spinner) findViewById(R.id.companyType);
        noOfPartner = (Spinner) findViewById(R.id.noOfPartner);
        noOfDirector = (Spinner) findViewById(R.id.noOfDirector);
        submitCompanyKYC = (Button) findViewById(R.id.submitCompanyKYC);

        adharNo = (EditText) findViewById(R.id.adharNo);
        dateOfBirth = (EditText) findViewById(R.id.dateOfBirth);
        voterId = (EditText) findViewById(R.id.voterId);

        noOfPartnerLayout = (LinearLayout) findViewById(R.id.noOfPartnerlayout);
        addPartnerLayout = (LinearLayout) findViewById(R.id.addPartnerLayout);

        noOfDirectorLayout = (LinearLayout) findViewById(R.id.noOfDirectorLayout);
        addDirectorLayout = (LinearLayout) findViewById(R.id.addDirectorLayout);

        proprietaryLayout = (LinearLayout) findViewById(R.id.proprietaryLayout);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.company_type_array, android.R.layout.simple_spinner_item);
        // Drop down layout style - list view with radio button
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //companyType.setPrompt("[Nothing Selected]");
        // attaching data adapter to spinner
        companyType.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this, R.array.count_array, android.R.layout.simple_spinner_item);
        // Drop down layout style - list view with radio button
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        noOfDirector.setPrompt("[Nothing Selected]");
        // attaching data adapter to spinner
        noOfDirector.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.count_array, android.R.layout.simple_spinner_item);
        // Drop down layout style - list view with radio button
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        noOfPartner.setPrompt("[Nothing Selected]");
        // attaching data adapter to spinner
        noOfPartner.setAdapter(adapter2);


        dateOfBirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                showTruitonDatePickerDialog(v);
            }
        });
        companyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                if (position == 0) {
                    proprietaryLayout.setVisibility(View.VISIBLE);
                    noOfPartnerLayout.setVisibility(View.GONE);
                    noOfDirectorLayout.setVisibility(View.GONE);

                } else if (position == 1) {
                    proprietaryLayout.setVisibility(View.GONE);
                    noOfPartnerLayout.setVisibility(View.VISIBLE);
                    noOfDirectorLayout.setVisibility(View.GONE);
                    //addPartnerView(1);

                } else {
                    proprietaryLayout.setVisibility(View.GONE);
                    noOfPartnerLayout.setVisibility(View.GONE);
                    noOfDirectorLayout.setVisibility(View.VISIBLE);
                    //addDirectorView(1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

        noOfPartner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                int i = position + 1;
                Log.e("COUNT", "VAL" + i);

                addPartnerLayout.removeAllViews();
                partnerVO = new PartnerKYCVO[i];
                addPartnerView(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

        noOfDirector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                int i = position + 1;
                Log.e("COUNT", "VAL" + i);
                addDirectorLayout.removeAllViews();
                partnerVO = new PartnerKYCVO[i];
                addDirectorView(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

        jArray = new JSONArray();// /ItemDetail jsonArray


        submitCompanyKYC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (companyType.getSelectedItemPosition() == 0) {
                    if (adharNo.getText().toString().equals("")) {
                        Utils.showToast(CompanyKYC.this, "Please enter Aadhar Number");
                    } else if (adharNo.getText().toString().length() != 12) {
                        Utils.showToast(CompanyKYC.this, "Aadhar Number has to be exactly 12 digits long!");
                    } else {
                        submitKYCDetails();
                    }

                } else {
                    for (int i = 0; i < nameEds.size(); i++) {
                        JSONObject jResult = new JSONObject();// main object

                        JSONObject jGroup = new JSONObject();// /sub Object

                        try {
                            jGroup.put("name", nameEds.get(i).getText().toString());
                            jGroup.put("mobile", mobileEds.get(i).getText().toString());
                            jGroup.put("aadhar", adharEds.get(i).getText().toString());
                            jArray.put(jGroup);

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                    submitKYCDetails();
                }
            }
        });

        Preferences.getInstance().loadPreferences(CompanyKYC.this);
        if (Preferences.getInstance().isProfileStatusApproved) {
            adharNo.setEnabled(false);
            voterId.setEnabled(false);
        } else {
            adharNo.setEnabled(true);
            voterId.setEnabled(true);
        }
        Preferences.getInstance().loadPreferences(CompanyKYC.this);
        if (Preferences.getInstance().isProfileStatusApproved) {
            submitCompanyKYC.setVisibility(View.INVISIBLE);
        } else {
            submitCompanyKYC.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        getCompanyKYCInfo();
    }


    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new CompanyKYC.DatePickerFragment();
        android.app.FragmentManager fm = CompanyKYC.this.getFragmentManager();
        newFragment.show(fm, "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            dateOfBirth.setText(day + "/" + (month + 1) + "/" + year);
        }
    }

    private void addDirectorView(int count) {

        nameEds = new ArrayList<EditText>();
        mobileEds = new ArrayList<EditText>();
        adharEds = new ArrayList<EditText>();

        for (int i = 0; i < count; i++) {
            View view = getLayoutInflater().inflate(R.layout.singledirectorlayout, addDirectorLayout, false);
            TextView label = (TextView) view.findViewById(R.id.label);
            name = (EditText) view.findViewById(R.id.name);
            mobile = (EditText) view.findViewById(R.id.mobile);
            adhar = (EditText) view.findViewById(R.id.adhar);
            nameEds.add(i, name);
            mobileEds.add(i, mobile);
            adharEds.add(i, adhar);
            name.setId(i);
            mobile.setId(i);
            adhar.setId(i);
            int a = i + 1;
            label.setText("Director " + a);
            addDirectorLayout.addView(view);
            Log.e("EDITEXT_SIZE", " " + nameEds.size());


        }
    }


    private void addPartnerView(int count) {
        nameEds = new ArrayList<EditText>();
        mobileEds = new ArrayList<EditText>();
        adharEds = new ArrayList<EditText>();

        for (int i = 0; i < count; i++) {
            View view = getLayoutInflater().inflate(R.layout.singledirectorlayout, addDirectorLayout, false);
            TextView label = (TextView) view.findViewById(R.id.label);
            name = (EditText) view.findViewById(R.id.name);
            mobile = (EditText) view.findViewById(R.id.mobile);
            adhar = (EditText) view.findViewById(R.id.adhar);
            nameEds.add(i, name);
            mobileEds.add(i, mobile);
            adharEds.add(i, adhar);
            name.setId(i);
            mobile.setId(i);
            adhar.setId(i);
            int a = i + 1;
            label.setText("Partner " + a);
            addPartnerLayout.addView(view);
            Log.e("EDITEXT_SIZE", " " + nameEds.size());


        }
    }

    private void addResponseView(int count, JSONArray respo, String type) {

        addDirectorLayout.removeAllViews();
        addPartnerLayout.removeAllViews();
        nameEds = new ArrayList<EditText>();
        mobileEds = new ArrayList<EditText>();
        adharEds = new ArrayList<EditText>();
        for (int i = 0; i < count; i++) {
            View view = getLayoutInflater().inflate(R.layout.singledirectorlayout, addPartnerLayout, false);
            TextView label = (TextView) view.findViewById(R.id.label);
            name = (EditText) view.findViewById(R.id.name);
            mobile = (EditText) view.findViewById(R.id.mobile);
            adhar = (EditText) view.findViewById(R.id.adhar);

            //{"name":"arman reyaz","mobile":"9632581470","aadhar":"5778"}

            try {
                name.setText(respo.getJSONObject(i).getString("name"));
                mobile.setText(respo.getJSONObject(i).getString("mobile"));
                adhar.setText(respo.getJSONObject(i).getString("aadhar"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            nameEds.add(name);
            mobileEds.add(mobile);
            adharEds.add(adhar);
            name.setId(i);
            mobile.setId(i);
            adhar.setId(i);
            int a = i + 1;
            label.setText(type + " " + a);
            if (type.equals("Partner")) {
                addPartnerLayout.addView(view);

            } else {
                addDirectorLayout.addView(view);

            }
            //Log.e("EDITEXT_SIZE"," "+nameEds.size());

        }
    }

    private void submitKYCDetails() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "updateCompanyKYC";

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

                        nameEds.clear();
                        mobileEds.clear();
                        adharEds.clear();
                        CompanyKYC.this.finish();


                    } else {
                        //Utils.showToast(CompanyKYC.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
                    }

                    partnerViewInstance.clear();
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
                //Utils.showToast(CompanyKYC.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(CompanyKYC.this);
                Map<String, String> params = new HashMap<String, String>();
//                companyId type aadharNumber dateOfBirth voterId companyPartnerKYCList

                params.put("companyId", Preferences.getInstance().companyId);
                params.put("type", companyType.getSelectedItem().toString().toUpperCase());
                params.put("aadharNumber", adharNo.getText().toString());
                // params.put("dateOfBirth",dateOfBirth.getText().toString());
                params.put("voterId", voterId.getText().toString());
                params.put("companyPartnerKYCList", jArray.toString());
                Log.e("PARAMS", params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(CompanyKYC.this)) {
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(CompanyKYC.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private void getCompanyKYCInfo() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "getCompanyKYCInfo";

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


                        adharNo.setText(responseObject.getJSONObject("responseObject").getString("aadharNumber"));
                        voterId.setText(responseObject.getJSONObject("responseObject").getString("voterId"));
                       /* "partnerKYCVO":[{"name":"arman reyaz","mobile":"9632581470","aadhar":"5778"},{"name":"67888","mobile":"632566","aadhar":"fgjj"}]*/

                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                CompanyKYC.this, R.array.company_type_array, android.R.layout.simple_spinner_item);
                        // Drop down layout style - list view with radio button
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //companyType.setPrompt("[Nothing Selected]");
                        // attaching data adapter to spinner
                        companyType.setAdapter(adapter);
                        for (int i = 0; i < adapter.getCount(); i++) {
                            if (companyType.getItemAtPosition(i).toString().equalsIgnoreCase(responseObject.getJSONObject("responseObject").getString("type"))) {
                                companyType.setSelection(i);
                            }
                        }

                        partnerKYC = new JSONArray();
                        partnerKYC = responseObject.getJSONObject("responseObject").getJSONArray("partnerKYCVO");
                        if (partnerKYC.length() > 0) {

                            if (responseObject.getJSONObject("responseObject").getString("type").toString().equalsIgnoreCase("PROPRIETOR/INDIVIDUAL")) {
                                proprietaryLayout.setVisibility(View.VISIBLE);
                                noOfPartnerLayout.setVisibility(View.GONE);
                                noOfDirectorLayout.setVisibility(View.GONE);
                            } else if (responseObject.getJSONObject("responseObject").getString("type").toString().equalsIgnoreCase("PARTNERSHIP")) {
                                proprietaryLayout.setVisibility(View.GONE);
                                noOfPartnerLayout.setVisibility(View.VISIBLE);
                                noOfDirectorLayout.setVisibility(View.GONE);
                                addResponseView(partnerKYC.length(), partnerKYC, "Partner");
                            } else {
                                proprietaryLayout.setVisibility(View.GONE);
                                noOfPartnerLayout.setVisibility(View.GONE);
                                noOfDirectorLayout.setVisibility(View.VISIBLE);
                                addResponseView(partnerKYC.length(), partnerKYC, "Director");
                            }

                        }

                    } else {
                        //Utils.showToast(CompanyKYC.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
                    }

                    partnerViewInstance.clear();
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
                //Utils.showToast(CompanyKYC.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(CompanyKYC.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyId", Preferences.getInstance().companyId);

                Log.e("PARAMS", params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(CompanyKYC.this)) {
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(CompanyKYC.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();

        super.onBackPressed();
    }


    private void toa() {
        System.out.println("abc");
    }

    @Override
    public void onClick(View v) {
        Intent photoPickerIntent, getContentIntent, intent;
        switch (v.getId()) {


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