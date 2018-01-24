package com.mobiquel.shank;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobiquel.shank.utils.AppConstants;
import com.mobiquel.shank.utils.Preferences;
import com.mobiquel.shank.utils.Utility;
import com.mobiquel.shank.utils.Utils;
import com.mobiquel.shank.utils.VolleySingleton;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BankDetails extends AppCompatActivity implements OnClickListener {

    private Button uploadCheque,uploadStatement,uploadMore,submitBankDetails;
    private Button viewStatement,viewCheque,viewMore;
    private ProgressBarCircularIndeterminate progressBar;
    private String file1="",file2="",file3="",file4="",file5="";
    private int i=0;
    private final int PERMISSION_REQUEST = 0;
    private int IMAGE_PICK_REQUEST_CODE = 2;
    private Uri imageUri;
    private String id="";
    private static final int REQUEST_PICK_FILE = 1;
    private String uploadedFilePath;
    private static final int REQUEST_CHOOSER = 1234;
    private Spinner bankSpinner;
    private EditText accountNumber,ifscCode,accountHolderName;
    private ArrayAdapter<String> banksAdapter;
    private TextView actionBarTitleTextView;
    private Toolbar toolbar;
    private JSONArray banks;
    private int REQUEST_CAMERA = 0;
    private  Intent  getContentIntent,intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);
        getSupportActionBar().setTitle("Bank Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3560A4")));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        progressBar = (ProgressBarCircularIndeterminate)findViewById(R.id.progressBar);

        uploadCheque = (Button) findViewById(R.id.uploadCheque);
        uploadStatement = (Button) findViewById(R.id.uploadStatement);
        uploadMore = (Button) findViewById(R.id.uploadMore);
        submitBankDetails = (Button) findViewById(R.id.submitBankDetails);
        bankSpinner = (Spinner) findViewById(R.id.bankName);

        viewStatement = (Button) findViewById(R.id.viewStatement);
        viewCheque = (Button) findViewById(R.id.viewCheque);
        viewMore = (Button) findViewById(R.id.viewMore);

        //adapter = ArrayAdapter.createFromResource(this, R.array.bank_name_array, android.R.layout.simple_spinner_item);
        // Drop down layout style - list view with radio button
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
       // bankSpinner.setAdapter(adapter);


        accountNumber = (EditText) findViewById(R.id.accountNumber);
        ifscCode = (EditText) findViewById(R.id.ifscCode);
        accountHolderName = (EditText) findViewById(R.id.accountHolderName);




        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        uploadCheque.setOnClickListener(this);
        uploadStatement.setOnClickListener(this);
        uploadMore.setOnClickListener(this);


        viewStatement.setOnClickListener(this);
        viewCheque.setOnClickListener(this);
        viewMore.setOnClickListener(this);

        submitBankDetails.setOnClickListener(this);

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
        }

        getBankList();

        Preferences.getInstance().loadPreferences(BankDetails.this);
        if(Preferences.getInstance().isProfileStatusApproved)
        {
            submitBankDetails.setVisibility(View.INVISIBLE);
        }
        else
        {
            submitBankDetails.setVisibility(View.VISIBLE);
        }


    }

    private void getBankDetails() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url=AppConstants.SERVER_URL+"getUserBankInfo";

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
                        file1=responseObject.getJSONObject("responseObject").getString("chequeUploadURL");
                        file2=responseObject.getJSONObject("responseObject").getString("statementUploadURL");
                        file3=responseObject.getJSONObject("responseObject").getString("docUpload1");
                        ifscCode.setText(responseObject.getJSONObject("responseObject").getString("ifscCode"));
                        accountNumber.setText(responseObject.getJSONObject("responseObject").getString("accountNumber"));
                        accountHolderName.setText(responseObject.getJSONObject("responseObject").getString("accountHolderName"));

                        String bankName= responseObject.getJSONObject("responseObject").getString("bankName");

                        for(int i=0;i<banksAdapter.getCount();i++)
                        {
                            if(banksAdapter.getItem(i).equals(bankName))
                            {
                                bankSpinner.setSelection(i);
                            }
                        }
                        //file5=responseObject.getJSONObject("responseObject").getString("docUpload2");
                        id=responseObject.getJSONObject("responseObject").getString("id");
                        if(!file1.equals(""))
                        {
                            viewCheque.setVisibility(View.VISIBLE);
                        }
                        if(!file2.equals(""))
                        {
                            viewStatement.setVisibility(View.VISIBLE);
                        }
                        if(!file3.equals(""))
                        {
                            viewMore.setVisibility(View.VISIBLE);

                        }
                        if(!file4.equals(""))
                        {

                        }
                        if(!file5.equals(""))
                        {


                        }

                    }
                    else
                    {
                      //  Utils.showToast(BankDetails.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
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
                Utils.showToast(BankDetails.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(BankDetails.this);
                Map<String, String> params = new HashMap<String, String>();
                if(Preferences.getInstance().userType.equalsIgnoreCase("COMPANY"))
                {
                    params.put("userId", Preferences.getInstance().companyId);

                }
                else
                {
                    params.put("userId", Preferences.getInstance().userId);

                }
                params.put("userType",Preferences.getInstance().userType);

                Log.e("PARAMS",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(BankDetails.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(BankDetails.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
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



    private void getBankList() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url=AppConstants.SERVER_URL+"getBankListApp";

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
                        banks = responseObject.getJSONArray("responseObject");
                        String cityArray[] = new String[banks.length()];
                        for (int i = 0; i < banks.length(); i++) {
                            cityArray[i] = banks.getString(i);
                        }
                         banksAdapter = new ArrayAdapter<String>(BankDetails.this,
                                android.R.layout.simple_list_item_1, cityArray);
                        banksAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        bankSpinner.setAdapter(banksAdapter);
                        getBankDetails();


                    }
                    else
                    {
                       // Utils.showToast(BankDetails.this, responseObject.getString("errorMessage"));
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
                Utils.showToast(BankDetails.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(BankDetails.this);
                Map<String, String> params = new HashMap<String, String>();
                //bankName accountNumber  ifscCode accountHolderName chequeUploadURL statementUploadURL docUpload1 docUpload2  technicianId id

                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(BankDetails.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(BankDetails.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private void submitBankDetails() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url=AppConstants.SERVER_URL+"updateUserBankDetails";

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
                        Utils.showToast(BankDetails.this, responseObject.getString("errorMessage"));
                        finish();

                    }
                    else
                    {
                        //Utils.showToast(BankDetails.this, responseObject.getString("errorMessage"));
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
                Utils.showToast(BankDetails.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(BankDetails.this);
                Map<String, String> params = new HashMap<String, String>();
                //bankName accountNumber  ifscCode accountHolderName chequeUploadURL statementUploadURL docUpload1 docUpload2  technicianId id
                params.put("bankName",bankSpinner.getSelectedItem().toString());
                params.put("accountNumber",accountNumber.getText().toString());
                params.put("ifscCode",ifscCode.getText().toString());
                params.put("accountHolderName",accountHolderName.getText().toString());
                params.put("userType",Preferences.getInstance().userType);

                params.put("chequeUploadURL",file1);
                params.put("statementUploadURL",file2);
                params.put("docUpload1",file3);

                params.put("id",id);
                if(Preferences.getInstance().userType.equalsIgnoreCase("COMPANY"))
                {
                    params.put("userId", Preferences.getInstance().companyId);

                }
                else
                {
                    params.put("userId", Preferences.getInstance().userId);

                }

                Log.e("PARAMS",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(BankDetails.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(BankDetails.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
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
            case R.id.uploadCheque:
                i=1;
                /*photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image*//*");
                startActivityForResult(photoPickerIntent, IMAGE_PICK_REQUEST_CODE);*/

               /* getContentIntent = FileUtils.createGetContentIntent();
                intent = Intent.createChooser(getContentIntent, "Select a file");
                startActivityForResult(intent, REQUEST_CHOOSER);*/

                selectImage();

                break;
            case R.id.uploadStatement:
                i=2;
                /*getContentIntent = FileUtils.createGetContentIntent();
                intent = Intent.createChooser(getContentIntent, "Select a file");
                startActivityForResult(intent, REQUEST_CHOOSER);*/
                selectImage();

                break;
            case R.id.uploadMore:
                i=3;
            /*    getContentIntent = FileUtils.createGetContentIntent();
                intent = Intent.createChooser(getContentIntent, "Select a file");
                startActivityForResult(intent, REQUEST_CHOOSER);*/
                selectImage();

                break;


            case R.id.viewStatement:

                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(file2));
                startActivity(intent);
                break;
            case R.id.viewCheque:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(file1));
                startActivity(intent);
                break;
            case R.id.viewMore:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(file3));
                startActivity(intent);
                break;

            case R.id.submitBankDetails:
               if(accountNumber.getText().toString().equals(""))
               {
                   Utils.showToast(BankDetails.this,"Please enter Account Number!");
               }
               else  if(accountNumber.getText().toString().length()<9 || accountNumber.getText().toString().length()>18)
               {
                   Utils.showToast(BankDetails.this,"Account Number can be between 9 to 18 characters long!");
               }
               else if(ifscCode.getText().toString().equals(""))
               {
                   Utils.showToast(BankDetails.this,"Please enter IFSC code!");
               }
               else if(ifscCode.getText().toString().length()!=11)
               {
                   Utils.showToast(BankDetails.this,"IFSC code has to be 11 characters long!");
               }
               else if(accountHolderName.getText().toString().equals(""))
               {
                   Utils.showToast(BankDetails.this,"Please enter Account Holder name!");
               }
               else
               {
                   submitBankDetails();
               }
                break;

        }
    }

    private void selectImage() {
        final CharSequence[] items = { "From Camera", "From Library", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(BankDetails.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(BankDetails.this);

                if (items[item].equals("From Camera")) {
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("From Library")) {
                    if (result)
                        //galleryIntent();
                         getContentIntent = FileUtils.createGetContentIntent();
                     intent = Intent.createChooser(getContentIntent, "Select a file");
                    startActivityForResult(intent, REQUEST_CHOOSER);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*|application/pdf|audio/*");
        startActivityForResult(photoPickerIntent, IMAGE_PICK_REQUEST_CODE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_REQUEST_CODE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == REQUEST_CHOOSER)
            {
                if (resultCode == RESULT_OK) {

                    final Uri uri = data.getData();
                    String path = FileUtils.getPath(this, uri);
                    Log.e("FILE_PATH","VALUE: "+path.toString());
                    uploadFile(path);
                }
            }

        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = null;
        Bitmap scaled = null;
        String storeFilename = null;
        try {
            thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            int nh = (int) (thumbnail.getHeight() * (512.0 / thumbnail.getWidth()));
            scaled = Bitmap.createScaledBitmap(thumbnail, 512, nh, true);
            String partFilename = currentDateFormat();
            storeCameraPhotoInSDCard(scaled, partFilename);
            storeFilename = Environment.getExternalStorageDirectory() + "/photo_" + partFilename + ".jpg";
            uploadFile(storeFilename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @SuppressLint("SimpleDateFormat")
    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    private void storeCameraPhotoInSDCard(Bitmap bitmap, String currentDate) {
        File outputFile = new File(Environment.getExternalStorageDirectory(), "photo_" + currentDate + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private Bitmap getImageFileFromSDCard(String filename) {
        Bitmap bitmap = null;
        File imageFile = new File(Environment.getExternalStorageDirectory() + filename);
        try {
            FileInputStream fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void onSelectFromGalleryResult(Intent data) {

        Bitmap thumbnail = null;
        Bitmap scaled = null;
        try {
            imageUri = data.getData();
            thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            int nh = (int) (thumbnail.getHeight() * (512.0 / thumbnail.getWidth()));
            scaled = decodeSampledBitmapFromUri(this, imageUri, 512, nh);
            String partFilename = currentDateFormat();
            storeCameraPhotoInSDCard(scaled, partFilename);
            String storeFilename = Environment.getExternalStorageDirectory() + "/photo_" + partFilename + ".jpg";
            uploadFile(storeFilename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromUri(Context context, Uri imageUri, int reqWidth, int reqHeight)
            throws FileNotFoundException {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        InputStream iStream = context.getContentResolver().openInputStream(imageUri);
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(iStream, null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        try {
            iStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        iStream = context.getContentResolver().openInputStream(imageUri);
        return BitmapFactory.decodeStream(iStream, null, options);
    }

 /*   @Override
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
        String url="";
        if(i==1)
        {
            url="uploadUserBankChequeURL";


        }
        else  if(i==2)
        {
            url="uploadUserBankStatementURL";

        }
        else  if(i==3)
        {
            url="uploadUserBankMoreURL";

        }
        progressBar.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(AppConstants.SERVER_URL + url , params, new AsyncHttpResponseHandler(Looper.getMainLooper())
        {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,Throwable arg3)
            {
                System.out.println("abcdef");
                if(progressBar!=null&&progressBar.isShown())
                    progressBar.setVisibility(View.INVISIBLE);
                Utils.showToast(BankDetails.this,"Error uploading files! Please try again.");
            }
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2)
            {
                System.out.println("abc");
                if(progressBar!=null&&progressBar.isShown())
                    progressBar.setVisibility(View.INVISIBLE);

                Utils.showToast(BankDetails.this,"File uploaded successfully!");
                if(i==1)
                {
                    file1 = new String(arg2);
                    viewCheque.setVisibility(View.VISIBLE);


                }
                else  if(i==2)
                {
                    file2 = new String(arg2);
                    viewStatement.setVisibility(View.VISIBLE);

                }
                else  if(i==3)
                {
                    file3 = new String(arg2);
                    viewMore.setVisibility(View.VISIBLE);

                }
                else  if(i==4)
                {
                    file4 = new String(arg2);

                }
                else  if(i==5)
                {
                    file5 = new String(arg2);

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