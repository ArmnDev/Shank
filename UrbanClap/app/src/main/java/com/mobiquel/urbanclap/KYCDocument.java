package com.mobiquel.urbanclap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobiquel.urbanclap.adapter.MaterialAdapter;
import com.mobiquel.urbanclap.adapter.ServiceAdapter;
import com.mobiquel.urbanclap.utils.AppConstants;
import com.mobiquel.urbanclap.utils.Preferences;
import com.mobiquel.urbanclap.utils.Utils;
import com.mobiquel.urbanclap.utils.VolleySingleton;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.ipaulpro.afilechooser.utils.FileUtils;


import static com.mobiquel.urbanclap.utils.Utils.calculateInSampleSize;

public class KYCDocument extends AppCompatActivity implements OnClickListener {

    private Button uploadAdharCard,uploadPanCard,uploadVoterId,submitKYCFiles,uploadDoc1,uploadDoc2,addDoc,removeDoc;
    private Button viewAdharCardNo,viewPanCardNo,viewVoterId,viewDoc1,viewDoc2;
    private ProgressBarCircularIndeterminate progressBar;
    private String file1="",file2="",file3="",file4="",file5="";
    private int i=0;
    private final int PERMISSION_REQUEST = 0;
    private int IMAGE_PICK_REQUEST_CODE = 2;
    private Uri imageUri;
    private LinearLayout doc1Layout,doc2Layout;
    private String id="";
    private static final int REQUEST_PICK_FILE = 1;
    private String uploadedFilePath;
    private static final int REQUEST_CHOOSER = 1234;
    private TextView actionBarTitleTextView;
    private EditText adharNo,panNo,voterId;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_doc);
        getSupportActionBar().setTitle("KYC Documents");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c0392b")));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        progressBar = (ProgressBarCircularIndeterminate)findViewById(R.id.progressBar);

        uploadAdharCard = (Button) findViewById(R.id.uploadAdhaarCard);
        uploadPanCard = (Button) findViewById(R.id.uploadPanCard);
        uploadVoterId = (Button) findViewById(R.id.uploadVoterId);
        submitKYCFiles = (Button) findViewById(R.id.submitKYCFiles);

        uploadDoc1 = (Button) findViewById(R.id.uploadDoc1);
        uploadDoc2 = (Button) findViewById(R.id.uploadDoc2);
        addDoc = (Button) findViewById(R.id.addDoc);
        removeDoc = (Button) findViewById(R.id.removeDoc);

        viewAdharCardNo = (Button) findViewById(R.id.viewAdhaarCard);
        viewPanCardNo = (Button) findViewById(R.id.viewPanCard);
        viewVoterId = (Button) findViewById(R.id.viewVoterId);
        viewDoc1 = (Button) findViewById(R.id.viewDoc1);
        viewDoc2 = (Button) findViewById(R.id.viewDoc2);

        doc1Layout= (LinearLayout) findViewById(R.id.doc1Layout);
        doc2Layout= (LinearLayout) findViewById(R.id.doc2layout);

        adharNo = (EditText) findViewById(R.id.adharCard);
       panNo = (EditText) findViewById(R.id.panCard);
        voterId = (EditText) findViewById(R.id.voterId);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        uploadAdharCard.setOnClickListener(this);
        uploadPanCard.setOnClickListener(this);
        uploadVoterId.setOnClickListener(this);
        uploadDoc1.setOnClickListener(this);
        uploadDoc2.setOnClickListener(this);

        viewAdharCardNo.setOnClickListener(this);
        viewPanCardNo.setOnClickListener(this);
        viewVoterId.setOnClickListener(this);
        viewDoc1.setOnClickListener(this);
        viewDoc2.setOnClickListener(this);

        submitKYCFiles.setOnClickListener(this);

        addDoc.setOnClickListener(this);
        removeDoc.setOnClickListener(this);

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
        }

        getKYCDetails();
        Preferences.getInstance().loadPreferences(KYCDocument.this);
        if(Preferences.getInstance().isProfileStatusApproved)
        {
            adharNo.setEnabled(false);
            panNo.setEnabled(false);
            voterId.setEnabled(false);
        }

        Preferences.getInstance().loadPreferences(KYCDocument.this);
        if(Preferences.getInstance().isProfileStatusApproved)
        {
            submitKYCFiles.setVisibility(View.INVISIBLE);
            uploadAdharCard.setVisibility(View.INVISIBLE);
            uploadPanCard.setVisibility(View.INVISIBLE);
            uploadVoterId.setVisibility(View.INVISIBLE);
            uploadDoc1.setVisibility(View.INVISIBLE);
            uploadDoc2.setVisibility(View.INVISIBLE);

        }
        else
        {
            submitKYCFiles.setVisibility(View.VISIBLE);
            uploadAdharCard.setVisibility(View.VISIBLE);
            uploadPanCard.setVisibility(View.VISIBLE);
            uploadVoterId.setVisibility(View.VISIBLE);
            uploadDoc1.setVisibility(View.VISIBLE);
            uploadDoc2.setVisibility(View.VISIBLE);

        }


    }

    private void getKYCDetails() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url=AppConstants.SERVER_URL+"getTechnicianKYCInfo";

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


                        file1=responseObject.getJSONObject("responseObject").getString("aadharUploadURL");
                        file2=responseObject.getJSONObject("responseObject").getString("panUploadURL");
                        file3=responseObject.getJSONObject("responseObject").getString("voterUploadURL");
                        file4=responseObject.getJSONObject("responseObject").getString("docUpload1");
                        file5=responseObject.getJSONObject("responseObject").getString("docUpload2");
                        id=responseObject.getJSONObject("responseObject").getString("id");

                        adharNo.setText(responseObject.getJSONObject("responseObject").getString("aadharCardNo"));
                        voterId.setText(responseObject.getJSONObject("responseObject").getString("voterId"));
                        panNo.setText(responseObject.getJSONObject("responseObject").getString("panCardNo"));

                        if(!file1.equals(""))
                        {
                            viewAdharCardNo.setVisibility(View.VISIBLE);
                        }
                        if(!file2.equals(""))
                        {
                            viewPanCardNo.setVisibility(View.VISIBLE);
                        }
                        if(!file3.equals(""))
                        {
                            viewVoterId.setVisibility(View.VISIBLE);
                        }
                        if(!file4.equals(""))
                        {
                            viewDoc1.setVisibility(View.VISIBLE);
                            doc1Layout.setVisibility(View.VISIBLE);
                        }
                        if(!file5.equals(""))
                        {
                            viewDoc2.setVisibility(View.VISIBLE);
                            doc2Layout.setVisibility(View.VISIBLE);

                        }
                        if(doc2Layout.getVisibility()==View.VISIBLE||doc1Layout.getVisibility()==View.VISIBLE)
                        {
                            removeDoc.setVisibility(View.VISIBLE);
                        }


                    }
                    else
                    {
                        //Utils.showToast(KYCDocument.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
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
                Utils.showToast(KYCDocument.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(KYCDocument.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("technicianId",Preferences.getInstance().userId);
                Log.e("PARAMS",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(KYCDocument.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(KYCDocument.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied. show an explanation stating the importance of this permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
                }
                break;
        }
    }
    @Override
    public void onBackPressed()
    {
        finish();

        super.onBackPressed();
    }



    private void submitFiles() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url=AppConstants.SERVER_URL+"updateTechnicianKYC";

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
                        Utils.showToast(KYCDocument.this, responseObject.getString("errorMessage"));
                        finish();

                    }
                    else
                    {
                      //  Utils.showToast(KYCDocument.this, responseObject.getString("errorMessage"));
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
                Utils.showToast(KYCDocument.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(KYCDocument.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("panUploadURL",file2);
                params.put("aadharUploadURL",file1);
                params.put("voterUploadURL",file3);
                params.put("docUpload1",file4);
                params.put("docUpload2",file5);
                params.put("id",id);
                params.put("technicianId",Preferences.getInstance().userId);
                params.put("aadharNo",adharNo.getText().toString());
                params.put("voterId",voterId.getText().toString());
                params.put("panNo",panNo.getText().toString());


                Log.e("PARAMS",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(KYCDocument.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(KYCDocument.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }



    private void toa() {
        System.out.println("abc");
    }

    @Override
    public void onClick(View v)
    {
        Intent photoPickerIntent,getContentIntent,intent;
        switch (v.getId()) {
            case R.id.uploadAdhaarCard:
                i=1;
                /*photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image*//*");
                startActivityForResult(photoPickerIntent, IMAGE_PICK_REQUEST_CODE);*/

                getContentIntent = FileUtils.createGetContentIntent();
                intent = Intent.createChooser(getContentIntent, "Select a file");
                startActivityForResult(intent, REQUEST_CHOOSER);

                break;
            case R.id.uploadPanCard:
                i=2;
                getContentIntent = FileUtils.createGetContentIntent();
                intent = Intent.createChooser(getContentIntent, "Select a file");
                startActivityForResult(intent, REQUEST_CHOOSER);
                break;
            case R.id.uploadVoterId:
                i=3;
                getContentIntent = FileUtils.createGetContentIntent();
                intent = Intent.createChooser(getContentIntent, "Select a file");
                startActivityForResult(intent, REQUEST_CHOOSER);
                break;
            case R.id.uploadDoc1:
                i=4;
                getContentIntent = FileUtils.createGetContentIntent();
                intent = Intent.createChooser(getContentIntent, "Select a file");
                startActivityForResult(intent, REQUEST_CHOOSER);
                break;
            case R.id.uploadDoc2:
                i=5;
                getContentIntent = FileUtils.createGetContentIntent();
                intent = Intent.createChooser(getContentIntent, "Select a file");
                startActivityForResult(intent, REQUEST_CHOOSER);
                break;

            case R.id.viewVoterId:

                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(file3));
                startActivity(intent);
                break;
            case R.id.viewPanCard:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(file2));
                startActivity(intent);
                break;
            case R.id.viewAdhaarCard:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(file1));
                startActivity(intent);
                break;
            case R.id.viewDoc1:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(file4));
                startActivity(intent);
                break;
            case R.id.viewDoc2:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(file5));
                startActivity(intent);
                break;

            case R.id.addDoc:
                removeDoc.setVisibility(View.VISIBLE);
                     if(doc2Layout.getVisibility()==View.VISIBLE)
                     {
                        Utils.showToast(KYCDocument.this,"Maximum 5 files can be uploaded!");
                     }
                else if(doc1Layout.getVisibility()==View.VISIBLE)
                    {
                        doc2Layout.setVisibility(View.VISIBLE);

                    }
                    else
                    {
                        doc1Layout.setVisibility(View.VISIBLE);
                    }
                break;
            case R.id.removeDoc:
                if(doc2Layout.getVisibility()==View.VISIBLE)
                {
                    doc2Layout.setVisibility(View.GONE);
                    file5="";
                }
                else
                {
                    doc1Layout.setVisibility(View.GONE);
                    file4="";
                    removeDoc.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.submitKYCFiles:
             /*  if(file1.equals("")&&file2.equals("")&&file3.equals(""))
               {
                   Utils.showToast(KYCDocument.this,"Please upload any one file!");
               }
               else
               {*/
               //}
                if(adharNo.getText().toString().equals(""))
                {
                    Utils.showToast(KYCDocument.this,"Please enter Aadhar number!");
                }
                else if(adharNo.getText().toString().length()!=12)
                {
                    Utils.showToast(KYCDocument.this,"Aadhar Number has to be exactly 12 digits long!");
                }
                else if(panNo.getText().toString().equals(""))
                {
                    Utils.showToast(KYCDocument.this,"Please enter Pan Card number!");
                }
                else if(panNo.getText().toString().length()!=10)
                {
                    Utils.showToast(KYCDocument.this,"Pan Card number should be 10 letters long!");
                }
                else if(voterId.getText().toString().equals(""))
                {
                    Utils.showToast(KYCDocument.this,"Please enter Voter ID!");
                }
                else
                {
                    submitFiles();

                }
                break;

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode)
        {
            case REQUEST_CHOOSER:
                if (resultCode == RESULT_OK) {

                    final Uri uri = intent.getData();
                    String path = FileUtils.getPath(this, uri);
                    Log.e("FILE_PATH","VALUE: "+path.toString());
                    uploadFile(path);
                }
                break;
        }
    }

   /* @SuppressLint("SimpleDateFormat") private String currentDateFormat()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String  currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }*/

   /* private void uploadFile(String path)
    {

       // uploadFile.setEnabled(false);
        RequestParams params = new RequestParams();
        try
        {
            params.put("file", new File(path));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(AppConstants.SERVER_URL + "uploadTechnicianKYCFile" , params, new AsyncHttpResponseHandler(Looper.getMainLooper())
        {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,Throwable arg3)
            {
                if(progressBar!=null&&progressBar.isShown())
                    progressBar.setVisibility(View.INVISIBLE);
                Utils.showToast(KYCDocument.this,"Error uploading file!");

               // uploadFile.setEnabled(true);
               // ((ImageView)findViewById(R.id.uploadStatus)).setImageResource(R.drawable.cross_red);
               // ((ImageView)findViewById(R.id.uploadStatus)).setVisibility(View.VISIBLE);
            }
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2)
            {
                if(progressBar!=null&&progressBar.isShown())
                    progressBar.setVisibility(View.INVISIBLE);

                Utils.showToast(KYCDocument.this,"File uploaded successfully!");
                uploadedFilePath = new String(arg2);
             //   uploadFile.setEnabled(true);
              //  ((ImageView)findViewById(R.id.uploadStatus)).setImageResource(R.drawable.green_tic);
               // ((ImageView)findViewById(R.id.uploadStatus)).setVisibility(View.VISIBLE);
            }
        });
    }*/
    private void uploadFile(String path)
    {

        RequestParams params = new RequestParams();
        try
        {
            params.put("file", new File(path));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(AppConstants.SERVER_URL + "uploadTechnicianKYCDocs" , params, new AsyncHttpResponseHandler(Looper.getMainLooper())
        {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,Throwable arg3)
            {
                System.out.println("abcdef");
                if(progressBar!=null&&progressBar.isShown())
                    progressBar.setVisibility(View.INVISIBLE);
                Utils.showToast(KYCDocument.this,"Error uploading files! Please try again.");
            }
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2)
            {
                System.out.println("abc");
                if(progressBar!=null&&progressBar.isShown())
                    progressBar.setVisibility(View.INVISIBLE);

                Utils.showToast(KYCDocument.this,"File uploaded successfully!");
                if(i==1)
                {
                    file1 = new String(arg2);
                    viewAdharCardNo.setVisibility(View.VISIBLE);


                }
                else  if(i==2)
                {
                    file2 = new String(arg2);
                    viewPanCardNo.setVisibility(View.VISIBLE);

                }
                else  if(i==3)
                {
                    file3 = new String(arg2);
                    viewVoterId.setVisibility(View.VISIBLE);

                }
                else  if(i==4)
                {
                    file4 = new String(arg2);
                    viewDoc1.setVisibility(View.VISIBLE);

                }
                else  if(i==5)
                {
                    file5 = new String(arg2);
                    viewDoc2.setVisibility(View.VISIBLE);

                }
                i=0;

            }
        });
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