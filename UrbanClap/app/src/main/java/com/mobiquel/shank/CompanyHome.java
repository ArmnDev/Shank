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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobiquel.shank.utils.AppConstants;
import com.mobiquel.shank.utils.ConnectivityReceiver;
import com.mobiquel.shank.utils.GPSTracker;
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
import java.util.Timer;

public class CompanyHome extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, ConnectivityReceiver.ConnectivityReceiverListener {

    private Button homeFooter,setting,status,more,mapButton,kycDocuments,services,supportingMaterial,addTechnicians,companyInformation,logOut,bankDetails;
    private View viewHomeFooter,viewSetting,viewStatus,viewMore,mapView;

    private ProgressBarCircularIndeterminate progressBar;

    private LinearLayout settingLyout,companyInformationLayout,moreLayout,mapLayout,statusLayout;

    private final int PERMISSION_REQUEST = 0;
    private TextView companyName,companyCity,companyCode,accountSettingLabel;
    private TextView giveLeadAndEarn,shareReferral,howItWorks,termOfUse,contactUs,rateApp,downloadUserApp,referTechnician,settingsText;
    //private String materialIds="",serviceMaterialIds="";
    private ImageView companyPic, fullImage, closePopup;
    private int IMAGE_PICK_REQUEST_CODE = 2;
    private FloatingActionButton updateImage;
    private Uri imageUri;
    private String uploadedFilePath;
    private int REQUEST_CAMERA = 0;

    private TextView actionBarTitleTextView;
    private Toolbar toolbar;


    private GoogleMap map;
    private LocationManager locationManager;
    private GPSTracker gpsTracker;
    private Context context;
    private double lat, lan;
    private LatLng myPosition;

    private AlertDialog alert;
    private AlertDialog.Builder builder;

    private JSONArray locationArray;
    private Handler handler;
    private Runnable runnable;
    private Timer myTimer;
    private Button showReferredTech,showNotReferredTech,leaveRequests,joinRequests;
    private TextView submitApprovalLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_home);
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3560A4")));
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("");
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.custom_actionbar_layout, null);
        actionBarTitleTextView = ((TextView) v.findViewById(R.id.actionBarTitle));
        actionBarTitleTextView.setText("SHANK");
        getSupportActionBar().setCustomView(v);

        Preferences.getInstance().loadPreferences(CompanyHome.this);
        Preferences.getInstance().userId=Preferences.getInstance().companyId;
        Preferences.getInstance().savePreferences(CompanyHome.this);
        homeFooter= (Button) findViewById(R.id.homeFooter);
        companyInformation= (Button) findViewById(R.id.companyInformation);
        setting= (Button) findViewById(R.id.settings);
        mapButton= (Button) findViewById(R.id.mapButton);
        status= (Button) findViewById(R.id.status);
        more= (Button) findViewById(R.id.more);
        bankDetails= (Button) findViewById(R.id.bankDetails);
        submitApprovalLabel= (TextView) findViewById(R.id.submitApprovalLabel);

        viewHomeFooter= (View) findViewById(R.id.homeFooterView);
        viewMore= (View) findViewById(R.id.moreView);
        viewSetting= (View) findViewById(R.id.settingsView);
        viewStatus= (View) findViewById(R.id.statusView);
        mapView= (View) findViewById(R.id.mapView);

        kycDocuments= (Button) findViewById(R.id.kycDocuments);
        services= (Button) findViewById(R.id.services);
        addTechnicians= (Button) findViewById(R.id.addTechnicians);
        supportingMaterial= (Button) findViewById(R.id.supportingMaterial);
        logOut= (Button) findViewById(R.id.logOut);

        showNotReferredTech= (Button) findViewById(R.id.notReferredTechnician);
        showReferredTech= (Button) findViewById(R.id.referredTechnician);
        leaveRequests= (Button) findViewById(R.id.leaveRequests);
        joinRequests= (Button) findViewById(R.id.joinRequests);

        logOut= (Button) findViewById(R.id.logOut);

        updateImage = (FloatingActionButton) findViewById(R.id.updateImage);
        companyPic = (ImageView) findViewById(R.id.companyPic);


        companyName= (TextView) findViewById(R.id.companyName);
        companyCity= (TextView) findViewById(R.id.companyCity);
        companyCode= (TextView) findViewById(R.id.companyCode);

        giveLeadAndEarn= (TextView) findViewById(R.id.giveLeadEarn);
        settingsText= (TextView) findViewById(R.id.settingsTextView);

        shareReferral= (TextView) findViewById(R.id.shareReferralCode);
        howItWorks= (TextView) findViewById(R.id.howItWorks);
        termOfUse= (TextView) findViewById(R.id.termOfUse);
        contactUs= (TextView) findViewById(R.id.contactUs);
        rateApp= (TextView) findViewById(R.id.rateApp);
        downloadUserApp= (TextView) findViewById(R.id.downloadUserApp);
        referTechnician= (TextView) findViewById(R.id.referTechnician);


        accountSettingLabel= (TextView) findViewById(R.id.accountSettingLabel);
        submitApprovalLabel= (TextView) findViewById(R.id.submitApprovalLabel);
        settingLyout= (LinearLayout) findViewById(R.id.settingLayout);
        companyInformationLayout= (LinearLayout) findViewById(R.id.profileLayout);
        moreLayout= (LinearLayout) findViewById(R.id.moreLayout);
        mapLayout= (LinearLayout) findViewById(R.id.mapLayout);
        statusLayout = (LinearLayout) findViewById(R.id.statusLayout);

        progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);
       // accountSettingLabel.setPaintFlags(accountSettingLabel.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
       // submitApprovalLabel.setPaintFlags(submitApprovalLabel.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        if(getIntent().getExtras().getString("SOURCE").equals("MAP"))
        {
            mapView.setVisibility(View.VISIBLE);
            viewHomeFooter.setVisibility(View.GONE);

            mapLayout.setVisibility(View.VISIBLE);
            companyInformationLayout.setVisibility(View.GONE);
            submitApprovalLabel.setVisibility(View.GONE);



        }
        else
        {
            mapView.setVisibility(View.GONE);
            viewHomeFooter.setVisibility(View.VISIBLE);

            mapLayout.setVisibility(View.GONE);
            companyInformationLayout.setVisibility(View.VISIBLE);
            submitApprovalLabel.setVisibility(View.VISIBLE);

        }

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CompanyHome.this);
            builder.setMessage("Please enable GPS").setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            alert = builder.create();
            alert.show();
        }
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


        handler = new Handler();

        runnable = new Runnable()
        {
            public void run()
            {
               getCompanyTechniciansLocationRunnable();
                handler.postDelayed(runnable,5000);
            }
        };

        handler.postDelayed(runnable,3000);

        Preferences.getInstance().loadPreferences(this);
        if (!Preferences.getInstance().companyProfilePicture.equals("")) {
            Glide.with(CompanyHome.this).load(Preferences.getInstance().companyProfilePicture).asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(companyPic) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory
                                    .create(CompanyHome.this.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            companyPic.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }

        updateImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                selectImage();
            }
        });


        homeFooter.setOnClickListener(this);
        companyInformation.setOnClickListener(this);
        setting.setOnClickListener(this);
        mapButton.setOnClickListener(this);
        status.setOnClickListener(this);
        more.setOnClickListener(this);
        kycDocuments.setOnClickListener(this);
        services.setOnClickListener(this);
        addTechnicians.setOnClickListener(this);
        supportingMaterial.setOnClickListener(this);
        logOut.setOnClickListener(this);
        bankDetails.setOnClickListener(this);

        giveLeadAndEarn.setOnClickListener(this);
        shareReferral.setOnClickListener(this);
        howItWorks.setOnClickListener(this);
        termOfUse.setOnClickListener(this);
        contactUs.setOnClickListener(this);
        rateApp.setOnClickListener(this);
        downloadUserApp.setOnClickListener(this);
        referTechnician.setOnClickListener(this);
        settingsText.setOnClickListener(this);

        showReferredTech.setOnClickListener(this);
        showNotReferredTech.setOnClickListener(this);
        leaveRequests.setOnClickListener(this);
        joinRequests.setOnClickListener(this);

        submitApprovalLabel.setOnClickListener(this);

        giveLeadAndEarn.setVisibility(View.GONE);
        shareReferral.setVisibility(View.GONE);
        rateApp.setVisibility(View.GONE);
        downloadUserApp.setVisibility(View.GONE);
        referTechnician.setVisibility(View.VISIBLE);
        //referTechnician.setText("REFER TECHNICIAN/COMPANY");

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCompanyTechniciansLocation();
        ShankApplication.getInstance().setConnectivityListener(this);
        Preferences.getInstance().loadPreferences(CompanyHome.this);
        companyName.setText(Preferences.getInstance().companyName);
        companyCity.setText(Preferences.getInstance().companyCity);
        companyCode.setText(Preferences.getInstance().companyCode);
        getCompanyInfo();
        getCompanyProfileStatus();
    }
    private void initilizeMap() {
        if (map != null) {
            map.setMyLocationEnabled(true);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 15));
            if (map == null) {
                Utils.showToast(CompanyHome.this, "Sorry! unable to create maps");
            }
        }
    }

    private void changeBackGround() {

        giveLeadAndEarn.setBackgroundResource(R.drawable.rectangle_background_2);
        giveLeadAndEarn.setTextColor(Color.parseColor("#000000"));
        shareReferral.setBackgroundResource(R.drawable.rectangle_background_2);
        shareReferral.setTextColor(Color.parseColor("#000000"));
        howItWorks.setBackgroundResource(R.drawable.rectangle_background_2);
        howItWorks.setTextColor(Color.parseColor("#000000"));
        termOfUse.setBackgroundResource(R.drawable.rectangle_background_2);
        termOfUse.setTextColor(Color.parseColor("#000000"));
        contactUs.setBackgroundResource(R.drawable.rectangle_background_2);
        contactUs.setTextColor(Color.parseColor("#000000"));
        rateApp.setBackgroundResource(R.drawable.rectangle_background_2);
        rateApp.setTextColor(Color.parseColor("#000000"));
        downloadUserApp.setBackgroundResource(R.drawable.rectangle_background_2);
        downloadUserApp.setTextColor(Color.parseColor("#000000"));
        referTechnician.setBackgroundResource(R.drawable.rectangle_background_2);
        referTechnician.setTextColor(Color.parseColor("#000000"));
        settingsText.setBackgroundResource(R.drawable.rectangle_background_2);
        settingsText.setTextColor(Color.parseColor("#000000"));


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied. show an explanation stating the importance of this permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
                }
                break;
        }
    }
    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.homeFooter:
                actionBarTitleTextView.setText("PROFILE");

                changeBackGround();
                settingLyout.setVisibility(View.GONE);
                companyInformationLayout.setVisibility(View.VISIBLE);
                moreLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.GONE);
                statusLayout.setVisibility(View.GONE);

                viewHomeFooter.setVisibility(View.VISIBLE);
                submitApprovalLabel.setVisibility(View.VISIBLE);
                viewMore.setVisibility(View.GONE);
                viewSetting.setVisibility(View.GONE);
                viewStatus.setVisibility(View.GONE);
                mapView.setVisibility(View.GONE);


                break;
            case R.id.companyInformation:
                i=new Intent(CompanyHome.this,UpdateCompanyProfile.class);
                startActivity(i);
                break;
            case R.id.kycDocuments:
                i=new Intent(CompanyHome.this,CompanyKYC.class);
                startActivity(i);
                break;
            case R.id.bankDetails:
                i=new Intent(CompanyHome.this,BankDetails.class);
                startActivity(i);
                break;
            case R.id.supportingMaterial:
                i=new Intent(CompanyHome.this,Materials.class);
                startActivity(i);
                break;
            case R.id.addTechnicians:
                i=new Intent(CompanyHome.this,AddTechnician.class);
                startActivity(i);
                break;
            case R.id.services:
                i=new Intent(CompanyHome.this,ServicesAndSkillsFirst.class);
                startActivity(i);
                break;
            case R.id.settings:

                actionBarTitleTextView.setText("SETTINGS");
                settingLyout.setVisibility(View.VISIBLE);
                companyInformationLayout.setVisibility(View.GONE);
                moreLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.GONE);
                statusLayout.setVisibility(View.GONE);

                changeBackGround();

                viewHomeFooter.setVisibility(View.GONE);
                submitApprovalLabel.setVisibility(View.GONE);

                viewMore.setVisibility(View.GONE);
                viewSetting.setVisibility(View.VISIBLE);
                viewStatus.setVisibility(View.GONE);
                mapView.setVisibility(View.GONE);



                break;
            case R.id.more:

                moreLayout.setVisibility(View.VISIBLE);

                settingLyout.setVisibility(View.GONE);
                companyInformationLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.GONE);
                statusLayout.setVisibility(View.GONE);

                viewHomeFooter.setVisibility(View.GONE);
                submitApprovalLabel.setVisibility(View.GONE);

                viewMore.setVisibility(View.VISIBLE);
                viewSetting.setVisibility(View.GONE);
                viewStatus.setVisibility(View.GONE);
                mapView.setVisibility(View.GONE);
                //Utils.showToast(CompanyHome.this,"Coming soon!");

               /* more.setBackgroundColor(Color.parseColor("#c0392b"));
                more.setTextColor(Color.parseColor("#FFFFFF"));

                homeFooter.setBackgroundColor(Color.parseColor("#FFFFFF"));
                homeFooter.setTextColor(Color.parseColor("#000000"));
                status.setBackgroundColor(Color.parseColor("#FFFFFF"));
                status.setTextColor(Color.parseColor("#000000"));
                setting.setBackgroundColor(Color.parseColor("#FFFFFF"));
                setting.setTextColor(Color.parseColor("#000000"));*/


                break;
            case R.id.mapButton:

                mapLayout.setVisibility(View.VISIBLE);

                settingLyout.setVisibility(View.GONE);
                companyInformationLayout.setVisibility(View.GONE);
                moreLayout.setVisibility(View.GONE);
                statusLayout.setVisibility(View.GONE);

                viewHomeFooter.setVisibility(View.GONE);
                submitApprovalLabel.setVisibility(View.GONE);
                viewMore.setVisibility(View.GONE);
                viewSetting.setVisibility(View.GONE);
                viewStatus.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                //Utils.showToast(CompanyHome.this,"Coming soon!");

               /* more.setBackgroundColor(Color.parseColor("#c0392b"));
                more.setTextColor(Color.parseColor("#FFFFFF"));

                homeFooter.setBackgroundColor(Color.parseColor("#FFFFFF"));
                homeFooter.setTextColor(Color.parseColor("#000000"));
                status.setBackgroundColor(Color.parseColor("#FFFFFF"));
                status.setTextColor(Color.parseColor("#000000"));
                setting.setBackgroundColor(Color.parseColor("#FFFFFF"));
                setting.setTextColor(Color.parseColor("#000000"));*/


                break;
            case R.id.status:
                changeBackGround();
                companyInformationLayout.setVisibility(View.GONE);
                settingLyout.setVisibility(View.GONE);
                moreLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.GONE);
                statusLayout.setVisibility(View.VISIBLE);
                viewHomeFooter.setVisibility(View.GONE);
                submitApprovalLabel.setVisibility(View.GONE);
                viewMore.setVisibility(View.GONE);
                viewSetting.setVisibility(View.GONE);
                viewStatus.setVisibility(View.VISIBLE);
                mapView.setVisibility(View.GONE);
               /* status.setBackgroundColor(Color.parseColor("#c0392b"));
                status.setTextColor(Color.parseColor("#FFFFFF"));

                homeFooter.setBackgroundColor(Color.parseColor("#FFFFFF"));
                homeFooter.setTextColor(Color.parseColor("#000000"));
                more.setBackgroundColor(Color.parseColor("#FFFFFF"));
                more.setTextColor(Color.parseColor("#000000"));
                setting.setBackgroundColor(Color.parseColor("#FFFFFF"));
                setting.setTextColor(Color.parseColor("#000000"));
*/

                break;
            case R.id.logOut:
                AlertDialog.Builder builder = new AlertDialog.Builder(CompanyHome.this);
                builder.setMessage("Are you sure you want to logout?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Preferences.getInstance().loadPreferences(CompanyHome.this);
                                Preferences.getInstance().isLoggedIn=false;
                                Preferences.getInstance().savePreferences(CompanyHome.this);
                                Intent i=new Intent(CompanyHome.this,MobileOTPActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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


                break;

            case R.id.giveLeadEarn:
                changeBackGround();
                giveLeadAndEarn.setBackgroundResource(R.drawable.rectangle_background_clicked);
                giveLeadAndEarn.setTextColor(Color.parseColor("#FFFFFF"));

                Utils.showToast(CompanyHome.this,"Coming soon!");

                break;
            case R.id.shareReferralCode:
                changeBackGround();
                shareReferral.setBackgroundResource(R.drawable.rectangle_background_clicked);
                shareReferral.setTextColor(Color.parseColor("#FFFFFF"));
                final String appPackageName = getPackageName();
                Log.e("HIT","HIT");
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);

                String s="Hi! This is referral code "+Preferences.getInstance().referralCode+". Share it with your friends and avail amazing discounts.";
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Appliance Care REFERRAL");
                sendIntent.putExtra(Intent.EXTRA_TEXT, s);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.howItWorks:
                changeBackGround();
                howItWorks.setBackgroundResource(R.drawable.rectangle_background_clicked);
                howItWorks.setTextColor(Color.parseColor("#FFFFFF"));
                i=new Intent(CompanyHome.this,HowItWorks.class);
                startActivity(i);
                break;
            case R.id.termOfUse:
                changeBackGround();
                termOfUse.setBackgroundResource(R.drawable.rectangle_background_clicked);
                termOfUse.setTextColor(Color.parseColor("#FFFFFF"));
                i=new Intent(CompanyHome.this,TermsOfUse.class);
                startActivity(i);
                break;
            case R.id.contactUs:
                changeBackGround();
                contactUs.setBackgroundResource(R.drawable.rectangle_background_clicked);
                contactUs.setTextColor(Color.parseColor("#FFFFFF"));
                i=new Intent(CompanyHome.this,ContactUs.class);
                startActivity(i);
                break;
            case R.id.settingsTextView:
                changeBackGround();
                settingsText.setBackgroundResource(R.drawable.rectangle_background_clicked);
                settingsText.setTextColor(Color.parseColor("#FFFFFF"));
                i=new Intent(CompanyHome.this, SettingsCompany.class);
                startActivity(i);
                break;
            case R.id.rateApp:
                changeBackGround();
                rateApp.setBackgroundResource(R.drawable.rectangle_background_clicked);
                rateApp.setTextColor(Color.parseColor("#FFFFFF"));
                Utils.showToast(CompanyHome.this,"Coming soon!");

                break;
            case R.id.downloadUserApp:
                changeBackGround();
                downloadUserApp.setBackgroundResource(R.drawable.rectangle_background_clicked);
                downloadUserApp.setTextColor(Color.parseColor("#FFFFFF"));

                downloadUserApp.setTextColor(Color.parseColor("#FFFFFF"));
                Utils.showToast(CompanyHome.this,"Coming soon!");

                break;
            case R.id.referTechnician:
                changeBackGround();
                referTechnician.setBackgroundResource(R.drawable.rectangle_background_clicked);
                referTechnician.setTextColor(Color.parseColor("#FFFFFF"));

                Intent refer=new Intent(CompanyHome.this,ReferTechnicianCompany.class);

                startActivity(refer);
                break;
            case R.id.referredTechnician:
                Intent refered=new Intent(CompanyHome.this,ShowCompanyStatus.class);
                refered.putExtra("SOURCE","Invited");
                startActivity(refered);
                break;
            case R.id.notReferredTechnician:
                Intent noRefer=new Intent(CompanyHome.this,ShowCompanyStatus.class);
                noRefer.putExtra("SOURCE","Employee");

                startActivity(noRefer);
                break;
            case R.id.leaveRequests:
                Intent leave=new Intent(CompanyHome.this,LeaveRequests.class);
                leave.putExtra("SOURCE","LEAVE");
                startActivity(leave);
                break;
            case R.id.joinRequests:
                Intent join=new Intent(CompanyHome.this,LeaveRequests.class);
                join.putExtra("SOURCE","JOIN");
                startActivity(join);
                break;
            case R.id.submitApprovalLabel:
                submitCompanyProfileForApproval();
                break;

        }
    }
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "View Image", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(CompanyHome.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(CompanyHome.this);

                if (items[item].equals("Take Photo")) {
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("View Image")) {
                    dialog.dismiss();

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CompanyHome.this);
                    LayoutInflater inflater = (LayoutInflater) CompanyHome.this
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View dialogView = inflater.inflate(R.layout.view_image, null);
                    fullImage = (ImageView) dialogView.findViewById(R.id.imageFull);
                    closePopup = (ImageView) dialogView.findViewById(R.id.closePop);
                    Preferences.getInstance().loadPreferences(CompanyHome.this);
                    if (!Preferences.getInstance().companyProfilePicture.equals("")) {
                        Glide.with(CompanyHome.this).load(Preferences.getInstance().companyProfilePicture)
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
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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
        client.post(AppConstants.SERVER_URL+"uploadCompanyLogoImageURL", params,
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

                        Utils.showToast(CompanyHome.this, "Image uploaded successfully!");
                        uploadedFilePath = new String(arg2);
                        Preferences.getInstance().loadPreferences(CompanyHome.this);
                        Preferences.getInstance().companyProfilePicture = uploadedFilePath;
                        Preferences.getInstance().savePreferences(CompanyHome.this);

                        updateUsercompanyInformationPic(uploadedFilePath);
                        Glide.with(CompanyHome.this).load(uploadedFilePath).asBitmap().centerCrop()
                                .into(new BitmapImageViewTarget(companyPic) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory
                                                .create(CompanyHome.this.getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        companyPic.setImageDrawable(circularBitmapDrawable);
                                    }
                                });
                    }
                });
    }

    private void updateUsercompanyInformationPic(final String picUrl) {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "updateCompanyLogoImageURL";
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // toa();
                Utils.showToast(CompanyHome.this, "Image updated successfully!");

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
                Preferences.getInstance().loadPreferences(CompanyHome.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyId", Preferences.getInstance().userId);
                params.put("logoImageURL", picUrl);
                Log.e("PARAMS:", "___ " + params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(CompanyHome.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(CompanyHome.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private void submitCompanyProfileForApproval() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "submitCompanyProfileForApproval";
        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // toa();
                JSONObject responseObject;
                try {
                    responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_COMP_APPROVAL", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0"))
                    {
                        Utils.showToast(CompanyHome.this,responseObject.getString("errorMessage"));
                        getCompanyProfileStatus();
                       // submitApprovalLabel.setText("WAITING FOR APPROVAL");
                        // {"companyId":"1","name":"MOBI","mobile":"9069876402","cityId":"1","referralCompanyCode":null,"email":"checkoutarmn@gmail.com","address":"DELHI ","logoURL":"","gstNumber":"23456","panNumber":"35677"
                        //Utils.showToast(CompanyHome.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
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
            public void onErrorResponse(VolleyError error) {
                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(CompanyHome.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyId", Preferences.getInstance().companyId);
                Log.e("PARAMS:", "___ " + params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(CompanyHome.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(CompanyHome.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }
    private void getCompanyInfo() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url=AppConstants.SERVER_URL+"getCompanyInfo";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject responseObject;
                try {
                    responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_COMP_INFO", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0"))
                    {
                       // {"companyId":"1","name":"MOBI","mobile":"9069876402","cityId":"1","referralCompanyCode":null,"email":"checkoutarmn@gmail.com","address":"DELHI ","logoURL":"","gstNumber":"23456","panNumber":"35677"
                        companyName.setText(responseObject.getJSONObject("responseObject").getString("name"));
                        companyCity.setText(responseObject.getJSONObject("responseObject").getString("address"));
                        companyCode.setText(responseObject.getJSONObject("responseObject").getString("referralCompanyCode"));

                        Preferences.getInstance().loadPreferences(CompanyHome.this);
                        Preferences.getInstance().companyProfilePicture=responseObject.getJSONObject("responseObject").getString("logoURL");
                        Preferences.getInstance().companyCode=responseObject.getJSONObject("responseObject").getString("referralCompanyCode");
                        Preferences.getInstance().savePreferences(CompanyHome.this);
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
                Utils.showToast(CompanyHome.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(CompanyHome.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyId",Preferences.getInstance().companyId);
                Log.e("PARAMS",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(CompanyHome.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(CompanyHome.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }


    private void getCompanyProfileStatus() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url=AppConstants.SERVER_URL+"getCompanyProfileStatus";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject responseObject;
                try {
                    responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_COMP_INFO", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0"))
                    {
                        Preferences.getInstance().loadPreferences(CompanyHome.this);
                        Preferences.getInstance().howItWorks=responseObject.getJSONObject("responseObject").getString("howItWorks");
                        Preferences.getInstance().termsOfUse=responseObject.getJSONObject("responseObject").getString("termsOfUse");
                        Preferences.getInstance().contactUs=responseObject.getJSONObject("responseObject").getString("contactUs");
                        Preferences.getInstance().savePreferences(CompanyHome.this);

                        if(responseObject.getJSONObject("responseObject").getString("isProfileCreated").equals("T"))
                        {
                            companyInformation.setBackgroundResource(R.drawable.rectangle_background_status);
                            companyInformation.setTextColor(Color.parseColor("#FFFFFF"));

                        }
                        else
                        {
                            companyInformation.setBackgroundResource(R.drawable.box);
                            companyInformation.setTextColor(Color.parseColor("#747474"));
                        }
                        if(responseObject.getJSONObject("responseObject").getString("isBankDetailCreated").equals("T"))
                        {
                            bankDetails.setBackgroundResource(R.drawable.rectangle_background_status);
                            bankDetails.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                        else
                        {
                            bankDetails.setBackgroundResource(R.drawable.box);
                            bankDetails.setTextColor(Color.parseColor("#747474"));
                        }
                        if(responseObject.getJSONObject("responseObject").getString("isKYCCreated").equals("T"))
                        {
                            kycDocuments.setBackgroundResource(R.drawable.rectangle_background_status);
                            kycDocuments.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                        else
                        {
                            kycDocuments.setBackgroundResource(R.drawable.box);
                            kycDocuments.setTextColor(Color.parseColor("#747474"));
                        }
                        if(responseObject.getJSONObject("responseObject").getString("isMaterialCreated").equals("T"))
                        {
                            supportingMaterial.setBackgroundResource(R.drawable.rectangle_background_status);
                            supportingMaterial.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                        else
                        {
                            supportingMaterial.setBackgroundResource(R.drawable.box);
                            supportingMaterial.setTextColor(Color.parseColor("#747474"));
                        }
                        if(responseObject.getJSONObject("responseObject").getString("isServicesSkillsCreated").equals("T"))
                        {
                            services.setBackgroundResource(R.drawable.rectangle_background_status);
                            services.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                        else
                        {
                            services.setBackgroundResource(R.drawable.box);
                            services.setTextColor(Color.parseColor("#747474"));
                        }
                        if(responseObject.getJSONObject("responseObject").getString("companyProfileStatus").equals("INITIATED"))
                        {
                            submitApprovalLabel.setText("SUBMIT FOR APPROVAL");
                            Preferences.getInstance().loadPreferences(CompanyHome.this);
                            Preferences.getInstance().isProfileStatusApproved=false;
                            Preferences.getInstance().savePreferences(CompanyHome.this);
                        }
                        else if(responseObject.getJSONObject("responseObject").getString("companyProfileStatus").equals("PENDING"))
                        {
                            submitApprovalLabel.setText("WAITING FOR APPROVAL");
                            Preferences.getInstance().loadPreferences(CompanyHome.this);
                            Preferences.getInstance().isProfileStatusApproved=false;
                            Preferences.getInstance().savePreferences(CompanyHome.this);
                        }
                        else if(responseObject.getJSONObject("responseObject").getString("companyProfileStatus").equals("REJECTED"))
                        {

                            Preferences.getInstance().loadPreferences(CompanyHome.this);
                            Preferences.getInstance().isProfileStatusApproved=false;
                            Preferences.getInstance().savePreferences(CompanyHome.this);

                            Preferences.getInstance().loadPreferences(CompanyHome.this);
                            Preferences.getInstance().isLoggedIn = false;
                            Preferences.getInstance().companyType = "";
                            Preferences.getInstance().savePreferences(CompanyHome.this);
                            Intent i = new Intent(CompanyHome.this, MobileOTPActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        }

                        else
                        {
                            submitApprovalLabel.setText("PROFILE APPROVED");
                            submitApprovalLabel.setVisibility(View.GONE);

                            Preferences.getInstance().loadPreferences(CompanyHome.this);
                            Preferences.getInstance().isProfileStatusApproved=true;
                            Preferences.getInstance().savePreferences(CompanyHome.this);

                        }

                    }
                    else
                    {
                        //Utils.showToast(CompanyHome.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
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
                Utils.showToast(CompanyHome.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(CompanyHome.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyId",Preferences.getInstance().companyId);
                Log.e("PARAMS",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(CompanyHome.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(CompanyHome.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private void getCompanyTechniciansLocation() {
       // progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "getCompanyTechniciansLocation";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_TECH_ARRAY_LOCA", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0")) {
                        locationArray = new JSONArray();
                        locationArray = responseObject.getJSONArray("responseObject");

                        if (locationArray.length() > 0) {
                            Log.e("TECH_LOCATION_ARRAY", "ARRAY" + responseObject.toString());
                            int l = locationArray.length();
                            PolylineOptions options = new PolylineOptions().width(12).color(Color.TRANSPARENT)
                                    .geodesic(true);
                            for (int z = 0; z < l; z++) {
                                if (locationArray.getJSONObject(z).getString("latitude").equals("null") || locationArray.getJSONObject(z).getString("longitude").equals("null")) {

                                } else {
                                    MarkerOptions mo = new MarkerOptions();
                                    mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_technician_icon));

                                    LatLng point = new LatLng(
                                            Double.parseDouble(locationArray.getJSONObject(z).getString("latitude")),
                                            Double.parseDouble(locationArray.getJSONObject(z).getString("longitude")));
                                    options.add(point);

                                    String title = locationArray.getJSONObject(z).getString("name") + "\n" + "ID: " + locationArray.getJSONObject(z).getString("technicianId");
                                    mo.position(new LatLng(Double.parseDouble(locationArray.getJSONObject(z).getString("latitude")), Double.parseDouble(locationArray.getJSONObject(z).getString("longitude"))));
                                    Marker marker = map.addMarker(mo);
                                    marker.setTitle(title);
                                    marker.showInfoWindow();
                                    map.addMarker(mo);
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
                                }


                            }
                        } else {
                            // Utils.showToast(CompanyHome.this,"No Data for technicians found!");
                        }

                    } else {
                        //Utils.showToast(CompanyHome.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
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
                Utils.showToast(CompanyHome.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(CompanyHome.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyId", Preferences.getInstance().companyId);
                Log.e("PARAMS", params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(CompanyHome.this)) {
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            //Utils.showToast(CompanyHome.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private void getCompanyTechniciansLocationRunnable() {
       // progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url = AppConstants.SERVER_URL + "getCompanyTechniciansLocation";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_TECH_ARRAY_LOCA", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0")) {
                        locationArray = new JSONArray();
                        locationArray = responseObject.getJSONArray("responseObject");

                        if (locationArray.length() > 0) {
                            Log.e("TECH_LOCATION_ARRAY", "ARRAY" + responseObject.toString());
                            int l = locationArray.length();
                            PolylineOptions options = new PolylineOptions().width(12).color(Color.TRANSPARENT)
                                    .geodesic(true);
                            for (int z = 0; z < l; z++) {
                                if (locationArray.getJSONObject(z).getString("latitude").equals("null") || locationArray.getJSONObject(z).getString("longitude").equals("null")) {

                                } else {
                                    MarkerOptions mo = new MarkerOptions();
                                    mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_technician_icon));

                                    LatLng point = new LatLng(
                                            Double.parseDouble(locationArray.getJSONObject(z).getString("latitude")),
                                            Double.parseDouble(locationArray.getJSONObject(z).getString("longitude")));
                                    options.add(point);

                                    String title = locationArray.getJSONObject(z).getString("name") + "\n" + "ID: " + locationArray.getJSONObject(z).getString("technicianId");
                                    mo.position(new LatLng(Double.parseDouble(locationArray.getJSONObject(z).getString("latitude")), Double.parseDouble(locationArray.getJSONObject(z).getString("longitude"))));
                                    Marker marker = map.addMarker(mo);
                                    marker.setTitle(title);
                                    marker.showInfoWindow();
                                    map.addMarker(mo);
                                    //map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
                                }


                            }
                        } else {
                            // Utils.showToast(CompanyHome.this,"No Data for technicians found!");
                        }

                    } else {
                        //Utils.showToast(CompanyHome.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
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
                Utils.showToast(CompanyHome.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(CompanyHome.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyId", Preferences.getInstance().companyId);
                Log.e("PARAMS", params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(CompanyHome.this)) {
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            //Utils.showToast(CompanyHome.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        gpsTracker = new GPSTracker(CompanyHome.this);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMyLocationEnabled(true);
        Location loc = gpsTracker.getLocation();
        lat = loc.getLatitude();
        lan = loc.getLongitude();
        myPosition = new LatLng(lat, lan);

        if (loc == null) {
            Utils.showToast(CompanyHome.this, "Wait till GPS is set!");
        } else {
            lat = loc.getLatitude();
            lan = loc.getLongitude();
        }

      /*  myPosition = new LatLng(lat, lan);
        if (latestMarker != null) {
            latestMarker.remove();
        }
        latestMarker = map.addMarker(new MarkerOptions().position(myPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_add)));
        latestMarker.showInfoWindow();*/
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lan), 15));

    }

    private void toa() {
        System.out.println("abc");
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            Utils.showToast(CompanyHome.this, "Your internet is back!");
        } else {
            builder = new AlertDialog.Builder(CompanyHome.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Internet down!");
            builder.setMessage("Your internet is not working! Please try connecting.");
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }
}
