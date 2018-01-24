package com.mobiquel.shank;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobiquel.shank.utils.AppConstants;
import com.mobiquel.shank.utils.Preferences;
import com.mobiquel.shank.utils.Utility;
import com.mobiquel.shank.utils.Utils;
import com.mobiquel.shank.utils.VerhoeffAlgoAdharCard;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class UpdateProfile extends AppCompatActivity implements View.OnClickListener {

    private Button updateProfile;
    private ProgressBarCircularIndeterminate progressBar;

    private EditText name,email,joinAs,companyCode,inviteCode,qualification,permanentAddress,presentAddress,adharCard;
    private static EditText dob;
    private Spinner city,serviceLocation;
    private JSONArray cities,serviceLocations;
    private String cityId,locationId,getCityId,getLocationId;
    private final int PERMISSION_REQUEST = 0;

    private ImageView profilePic, fullImage, closePopup;
    private int IMAGE_PICK_REQUEST_CODE = 2;
    private FloatingActionButton updateImage;
    private Uri imageUri;
    private String uploadedFilePath;

    private int REQUEST_CAMERA = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().setTitle("Update Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c0392b")));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        name= (EditText) findViewById(R.id.name);
        email= (EditText) findViewById(R.id.email);
        city= (Spinner) findViewById(R.id.city);
        joinAs= (EditText) findViewById(R.id.joinAs);
        companyCode= (EditText) findViewById(R.id.companyCode);
        inviteCode= (EditText) findViewById(R.id.inviteCode);
        serviceLocation= (Spinner) findViewById(R.id.serviceLocation);
        qualification= (EditText) findViewById(R.id.qualification);
        permanentAddress= (EditText) findViewById(R.id.permanentAddress);
        presentAddress= (EditText) findViewById(R.id.presentAddress);
        adharCard= (EditText) findViewById(R.id.adharCard);


        dob= (EditText) findViewById(R.id.dob);
        updateImage = (FloatingActionButton) findViewById(R.id.updateImage);
        profilePic = (ImageView) findViewById(R.id.profilePic);

        Preferences.getInstance().loadPreferences(UpdateProfile.this);
        joinAs.setText(Preferences.getInstance().userType);
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
        Preferences.getInstance().loadPreferences(this);
        if (!Preferences.getInstance().userProfilePicture.equals("")) {
            Glide.with(UpdateProfile.this).load(Preferences.getInstance().userProfilePicture).asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(profilePic) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory
                                    .create(UpdateProfile.this.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            profilePic.setImageDrawable(circularBitmapDrawable);
                        }
                    });
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

        serviceLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {

                try {
                    locationId = serviceLocations.getJSONObject(pos).getString("locationId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

        updateProfile= (Button) findViewById(R.id.updateProfile);

        progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);

        updateProfile.setOnClickListener(this);

        dob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                showTruitonDatePickerDialog(v);
            }
        });
        updateImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                selectImage();
            }
        });
        getCities();
        Preferences.getInstance().loadPreferences(UpdateProfile.this);
        if(Preferences.getInstance().isProfileStatusApproved)
        {
            companyCode.setEnabled(false);
            adharCard.setEnabled(false);
            name.setEnabled(false);
            dob.setEnabled(false);

            updateProfile.setVisibility(View.GONE);
        }
        else
        {
            companyCode.setEnabled(true);
            adharCard.setEnabled(true);
            updateProfile.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "View Image", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(UpdateProfile.this);

                if (items[item].equals("Take Photo")) {
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("View Image")) {
                    dialog.dismiss();

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UpdateProfile.this);
                    LayoutInflater inflater = (LayoutInflater) UpdateProfile.this
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View dialogView = inflater.inflate(R.layout.view_image, null);
                    fullImage = (ImageView) dialogView.findViewById(R.id.imageFull);
                    closePopup = (ImageView) dialogView.findViewById(R.id.closePop);
                    Preferences.getInstance().loadPreferences(UpdateProfile.this);
                    if (!Preferences.getInstance().userProfilePicture.equals("")) {
                        Glide.with(UpdateProfile.this).load(Preferences.getInstance().userProfilePicture)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).into(fullImage);
                    } else {

                    }
                    dialogBuilder.setView(dialogView);

                    final AlertDialog alertDialog = dialogBuilder.create();
                    closePopup.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
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
            uploadImage(storeFilename);
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
            uploadImage(storeFilename);
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

    private void uploadImage(String path) {

        RequestParams params = new RequestParams();
        try {
            params.put("file", new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(AppConstants.SERVER_URL+"uploadTechnicianProfileImageURL", params,
                new AsyncHttpResponseHandler(Looper.getMainLooper()) {
                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                        System.out.println("abc");
                        if (progressBar != null && progressBar.isShown())
                            progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        System.out.println("abc");
                        if (progressBar != null && progressBar.isShown())
                            progressBar.setVisibility(View.INVISIBLE);

                        Utils.showToast(UpdateProfile.this, "Image uploaded successfully!");
                        uploadedFilePath = new String(arg2);
                        Preferences.getInstance().loadPreferences(UpdateProfile.this);
                        Preferences.getInstance().userProfilePicture = uploadedFilePath;
                        Preferences.getInstance().savePreferences(UpdateProfile.this);

                        updateUserProfilePic(uploadedFilePath);
                        Glide.with(UpdateProfile.this).load(uploadedFilePath).asBitmap().centerCrop()
                                .into(new BitmapImageViewTarget(profilePic) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory
                                                .create(UpdateProfile.this.getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        profilePic.setImageDrawable(circularBitmapDrawable);
                                    }
                                });
                    }
                });
    }

    private void updateUserProfilePic(final String picUrl) {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "updateTechnicianProfileImageURL";
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // toa();
                Utils.showToast(UpdateProfile.this, "Image updated successfully!");

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
                Preferences.getInstance().loadPreferences(UpdateProfile.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("technicianId", Preferences.getInstance().userId);
                params.put("profileImageURL", picUrl);
                Log.e("PARAMS:", "___ " + params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(UpdateProfile.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(UpdateProfile.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }
    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        android.app.FragmentManager fm = UpdateProfile.this.getFragmentManager();
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
            dob.setText(day + "/" + (month + 1) + "/" + year);
        }
    }

    private void getProfile(){
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(UpdateProfile.this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "getTechnicianProfileInfo";
        Log.e("FIRST", "CHILD");
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_PROFILE",responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {

                       name.setText(responseObject.getJSONObject("responseObject").getString("name"));
                        email.setText(responseObject.getJSONObject("responseObject").getString("email"));
                        qualification.setText(responseObject.getJSONObject("responseObject").getString("qualification"));
                        //joinAs.setText(responseObject.getJSONObject("responseObject").getString("accountType"));
                        dob.setText(responseObject.getJSONObject("responseObject").getString("dob"));
                        companyCode.setText(responseObject.getJSONObject("responseObject").getString("referralCompanyCode"));
                        permanentAddress.setText(responseObject.getJSONObject("responseObject").getString("addressHome"));
                        presentAddress.setText(responseObject.getJSONObject("responseObject").getString("addressPresent"));
                        adharCard.setText(responseObject.getJSONObject("responseObject").getString("aadharCard"));
                        inviteCode.setText(responseObject.getJSONObject("responseObject").getString("inviteCode"));

                        city.setSelection(Integer.parseInt(responseObject.getJSONObject("responseObject").getString("cityId")));
                        getCityId=responseObject.getJSONObject("responseObject").getString("cityId");
                        //getLocationId=responseObject.getJSONObject("responseObject").getString("locationId");


                        city.setSelection(getCityIdArrayPosition(getCityId));
                        //getServiceLocations(getCityId,getLocationId);

                        String url=responseObject.getJSONObject("responseObject").getString("profileImageURL");
                        Preferences.getInstance().loadPreferences(UpdateProfile.this);
                        Preferences.getInstance().userProfilePicture=url;
                        Preferences.getInstance().savePreferences(UpdateProfile.this);
                        if (!Preferences.getInstance().userProfilePicture.equals("")) {
                            Glide.with(UpdateProfile.this).load(Preferences.getInstance().userProfilePicture).asBitmap()
                                    .centerCrop()
                                    .into(new BitmapImageViewTarget(profilePic) {
                                        @Override
                                        protected void setResource(Bitmap resource) {
                                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory
                                                    .create(UpdateProfile.this.getResources(), resource);
                                            circularBitmapDrawable.setCircular(true);
                                            profilePic.setImageDrawable(circularBitmapDrawable);
                                        }
                                    });
                        }





                    } else {
                        Utils.showToast(UpdateProfile.this, responseObject.getString("errorMessage"));
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
                Preferences.getInstance().loadPreferences(UpdateProfile.this);
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(UpdateProfile.this);
                params.put("technicianId",Preferences.getInstance().userId);
                Log.e("PARAMS",params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(UpdateProfile.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(UpdateProfile.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private int getCityIdArrayPosition(String cityId)
    {
        int pos = 0;
        for(;pos<cities.length();pos++)
        {
            try {
                if(cities.getJSONObject(pos).getString("cityId").equals(cityId))
                {
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return pos;
    }

    private int getLocationIdArrayPosition(JSONArray location,String locationId)
    {
        int pos = 0;
        for(;pos<location.length();pos++)
        {
            try {
                if(location.getJSONObject(pos).getString("locationId").equals(locationId))
                {
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return pos;
    }

    private void updateProfile(){
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(UpdateProfile.this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "updateTechnicianProfile";
        Log.e("FIRST", "CHILD");
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {

                        Preferences.getInstance().loadPreferences(UpdateProfile.this);
                        Preferences.getInstance().userName=name.getText().toString();
                        Preferences.getInstance().email=email.getText().toString();
                        Preferences.getInstance().savePreferences(UpdateProfile.this);
                        Utils.showToast(UpdateProfile.this, responseObject.getString("errorMessage"));
                        finish();

                    } else {
                        Utils.showToast(UpdateProfile.this, responseObject.getString("errorMessage"));
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
                Preferences.getInstance().loadPreferences(UpdateProfile.this);
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(UpdateProfile.this);
                params.put("name",name.getText().toString());
                params.put("mobile",Preferences.getInstance().phoneNumber);
                try {
                    params.put("cityId",cities.getJSONObject(city.getSelectedItemPosition()).getString("cityId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                params.put("referralCompanyCode",companyCode.getText().toString());
                params.put("email",email.getText().toString());
                params.put("qualification",qualification.getText().toString());
                params.put("addressHome",permanentAddress.getText().toString());
                params.put("aadharCard",adharCard.getText().toString());
                params.put("inviteCode",inviteCode.getText().toString());
                params.put("addressPresent",presentAddress.getText().toString());
                params.put("dob",dob.getText().toString());
                params.put("deviceOS","Android");
                params.put("technicianId",Preferences.getInstance().userId);

                Log.e("PARAMS",params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(UpdateProfile.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(UpdateProfile.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
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
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.updateProfile:
                if(name.getText().toString().equals(""))
                {
                    Utils.showToast(UpdateProfile.this,"Please enter name!");
                }
/*
                else if(!email.getText().toString().equals("")&&!Utils.isValidEmail(email.getText().toString()))
                {
                    Utils.showToast(UpdateProfile.this,"Please enter valid Email Address!");
                }*/
                else if(permanentAddress.getText().toString().equals(""))
                {
                    Utils.showToast(UpdateProfile.this,"Please enter Permanent Address!");
                }
                else if(presentAddress.getText().toString().equals(""))
                {
                    Utils.showToast(UpdateProfile.this,"Please enter Present Address!");
                }
                else
                {
                    updateProfile();

                }

                break;


        }
    }


    public static boolean validateAadharNumber(String aadharNumber){
        Pattern aadharPattern = Pattern.compile("\\d{12}");
        boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
        if(isValidAadhar){
            isValidAadhar = VerhoeffAlgoAdharCard.validateVerhoeff(aadharNumber);
        }
        return isValidAadhar;
    }

    private void getCities(){
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(UpdateProfile.this).getRequestQueue();
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
                        String cityArray[] = new String[cities.length()];
                        for (int i = 0; i < cities.length(); i++) {
                            cityArray[i] = cities.getJSONObject(i).getString("name");
                        }
                        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(UpdateProfile.this,
                                android.R.layout.simple_list_item_1, cityArray);
                        cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        city.setAdapter(cityAdapter);
                        getProfile();

                    } else {
                       // Utils.showToast(UpdateProfile.this, responseObject.getString("errorMessage"));
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
                Preferences.getInstance().loadPreferences(UpdateProfile.this);
                Map<String, String> params = new HashMap<String, String>();
                    params.put("startIndex","-1");
                params.put("length","10");
                params.put("searchString","");
                params.put("sortBy","CITY_NAME");
                params.put("order","A");

                Log.e("PARAMS",params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(UpdateProfile.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(UpdateProfile.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private void getServiceLocations(final String getCityId){
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(UpdateProfile.this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "getCityLocationList";
        Log.e("FIRST", "CHILD");
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getInt("errorCode") == 0) {
                        serviceLocations = responseObject.getJSONObject("responseObject").getJSONArray("locationList");
                        String cityArray[] = new String[serviceLocations.length()];
                        for (int i = 0; i < serviceLocations.length(); i++) {
                            cityArray[i] = serviceLocations.getJSONObject(i).getString("name");
                        }
                        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(UpdateProfile.this,
                                android.R.layout.simple_list_item_1, cityArray);
                        cityAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                        serviceLocation.setAdapter(cityAdapter);




                    } else {
                        //Utils.showToast(UpdateProfile.this, responseObject.getString("errorMessage"));
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
                Preferences.getInstance().loadPreferences(UpdateProfile.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("cityId",cityId);
                params.put("startIndex","-1");
                params.put("length","10");
                params.put("searchString","");
                params.put("sortBy","LOCATION_NAME");
                params.put("order","A");
                Log.e("PARAMS",params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(UpdateProfile.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(UpdateProfile.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }
}
