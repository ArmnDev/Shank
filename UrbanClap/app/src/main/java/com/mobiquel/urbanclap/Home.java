package com.mobiquel.urbanclap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gc.materialdesign.views.ScrollView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobiquel.urbanclap.adapter.HRFragmentAdapter;
import com.mobiquel.urbanclap.adapter.LeadsAdapter;
import com.mobiquel.urbanclap.adapter.MaterialAdapter;
import com.mobiquel.urbanclap.adapter.ServiceAdapter;
import com.mobiquel.urbanclap.adapter.TechnicianFragmentAdapter;
import com.mobiquel.urbanclap.mapclasses.GPSReceiver;
import com.mobiquel.urbanclap.mapclasses.SendGPSActivity;
import com.mobiquel.urbanclap.model.LeadsModel;
import com.mobiquel.urbanclap.utils.AppConstants;
import com.mobiquel.urbanclap.utils.ConnectivityReceiver;
import com.mobiquel.urbanclap.utils.GPSTracker;
import com.mobiquel.urbanclap.utils.Preferences;
import com.mobiquel.urbanclap.utils.Utility;
import com.mobiquel.urbanclap.utils.Utils;
import com.mobiquel.urbanclap.utils.VolleySingleton;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback,ConnectivityReceiver.ConnectivityReceiverListener {

    private Button homeFooter,setting,mapButton,more,kycDoc,serSkill,material,vehicleDet,profile,logOut,bankDetails,status;
    private View viewHomeFooter,viewSetting,viewMap,viewMore,statusView;
    private ProgressBarCircularIndeterminate progressBar;

    private LinearLayout settingLyout,profileLayout,moreLayout,mapLayout,statusLayout;


    private final int PERMISSION_REQUEST = 0;
    private TextView userName,emailId,accountSettingLabel,submitApprovalLabel,hrReferralCode;
    private TextView giveLeadAndEarn,shareReferral,howItWorks,termOfUse,contactUs,rateApp,downloadUserApp,referTechnician,settings;
    //private String materialIds="",serviceMaterialIds="";

    private ImageView profilePic, fullImage, closePopup;
    private int IMAGE_PICK_REQUEST_CODE = 2;
    private FloatingActionButton updateImage;
    private Uri imageUri;
    private String uploadedFilePath;

    private TextView actionBarTitleTextView;
    private Toolbar toolbar;
    private int REQUEST_CAMERA = 0;

    private Handler sHandler = new Handler();
    private Handler tHandler = new Handler();
    private SendGPSActivity sendGPSTrigger;
    private PendingIntent gpsPendingIntent,gpsSyncPendingIntent;
    private Intent gpsSendIntent,gpsSyncIntent;
    private AlarmManager gpsAlarmManager,gpsSyncAlarmManager;
    private GPSReceiver gpsTrigger;

    private GoogleMap map;
    private LocationManager locationManager;
    private GPSTracker gpsTracker;
    private Context context;
    private double lat, lan;
    private LatLng myPosition;

    private AlertDialog alert;
    private AlertDialog.Builder builder;

    private JSONArray locationArray;
    private RelativeLayout mapButtonLayout;

    private Runnable sendGPSRunnable = new Runnable() {
        public void run() {
            sendGPSTrigger.onReceive(ShankApplication.getInstance().getContext());

            sHandler.postDelayed(sendGPSRunnable, 10000);
        }
    };

    private Runnable gpsCapture = new Runnable() {
        public void run() {
            gpsTrigger.trigger(Home.this);
            tHandler.postDelayed(gpsCapture, 10000);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3560A4")));
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.custom_actionbar_layout, null);
        actionBarTitleTextView = ((TextView) v.findViewById(R.id.actionBarTitle));
        actionBarTitleTextView.setText("SHANK");
        getSupportActionBar().setCustomView(v);
        sendGPSTrigger = new SendGPSActivity();
        gpsTrigger=new GPSReceiver();
        gpsCapture.run();
        sendGPSRunnable.run();

        homeFooter= (Button) findViewById(R.id.homeFooter);
        profile= (Button) findViewById(R.id.profileButton);
        setting= (Button) findViewById(R.id.settings);
        mapButton= (Button) findViewById(R.id.map);
        more= (Button) findViewById(R.id.more);
        status= (Button) findViewById(R.id.status);

        viewHomeFooter= (View) findViewById(R.id.homeFooterView);
        viewMore= (View) findViewById(R.id.moreView);
        viewSetting= (View) findViewById(R.id.settingsView);
        viewMap= (View) findViewById(R.id.mapView);
        statusView= (View) findViewById(R.id.statusView);

        mapButtonLayout= (RelativeLayout) findViewById(R.id.mapButtonLayout);

        bankDetails= (Button) findViewById(R.id.bankDetails);
        kycDoc= (Button) findViewById(R.id.kycDoc);
        serSkill= (Button) findViewById(R.id.serviceSkill);
        vehicleDet= (Button) findViewById(R.id.vehicleDetails);
        material= (Button) findViewById(R.id.material);
        logOut= (Button) findViewById(R.id.logOut);

        updateImage = (FloatingActionButton) findViewById(R.id.updateImage);
        profilePic = (ImageView) findViewById(R.id.profilePic);

        emailId= (TextView) findViewById(R.id.emailId);
        hrReferralCode= (TextView) findViewById(R.id.hrReferralCode);

        userName= (TextView) findViewById(R.id.userName);
        giveLeadAndEarn= (TextView) findViewById(R.id.giveLeadEarn);
        settings= (TextView) findViewById(R.id.settingsTextView);

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
        profileLayout= (LinearLayout) findViewById(R.id.profileLayout);
        moreLayout= (LinearLayout) findViewById(R.id.moreLayout);
        mapLayout= (LinearLayout) findViewById(R.id.mapLayout);
        statusLayout = (LinearLayout) findViewById(R.id.statusLayout);

        progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
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

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TechnicianFragmentAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Preferences.getInstance().loadPreferences(this);
        if (!Preferences.getInstance().userProfilePicture.equals("")) {
            Glide.with(Home.this).load(Preferences.getInstance().userProfilePicture).asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(profilePic) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory
                                    .create(Home.this.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            profilePic.setImageDrawable(circularBitmapDrawable);
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
        profile.setOnClickListener(this);
        setting.setOnClickListener(this);
        mapButton.setOnClickListener(this);
        more.setOnClickListener(this);
        kycDoc.setOnClickListener(this);
        serSkill.setOnClickListener(this);
        vehicleDet.setOnClickListener(this);
        material.setOnClickListener(this);
        logOut.setOnClickListener(this);
        bankDetails.setOnClickListener(this);
        submitApprovalLabel.setOnClickListener(this);

        giveLeadAndEarn.setOnClickListener(this);
        shareReferral.setOnClickListener(this);
        howItWorks.setOnClickListener(this);
        termOfUse.setOnClickListener(this);
        contactUs.setOnClickListener(this);
        rateApp.setOnClickListener(this);
        downloadUserApp.setOnClickListener(this);
        referTechnician.setOnClickListener(this);
        status.setOnClickListener(this);
        settings.setOnClickListener(this);

        giveLeadAndEarn.setVisibility(View.GONE);
        shareReferral.setVisibility(View.GONE);
        rateApp.setVisibility(View.GONE);
        downloadUserApp.setVisibility(View.GONE);
        referTechnician.setVisibility(View.VISIBLE);
    }
    @Override
    public void onDestroy()
    {
        sHandler.removeCallbacks(sendGPSRunnable);
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Preferences.getInstance().loadPreferences(Home.this);
        userName.setText(Preferences.getInstance().userName);
        emailId.setText(Preferences.getInstance().email);
        hrReferralCode.setText(Preferences.getInstance().hrReferralCode);

        Preferences.getInstance().loadPreferences(this);
        if (!Preferences.getInstance().userProfilePicture.equals("")) {
            Glide.with(Home.this).load(Preferences.getInstance().userProfilePicture).asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(profilePic) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory
                                    .create(Home.this.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            profilePic.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
       getTechnicianProfileStatus();
        getProfile();
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
        settings.setBackgroundResource(R.drawable.rectangle_background_2);
        settings.setTextColor(Color.parseColor("#000000"));


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied. show an explanation stating the importance of this permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
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
                profileLayout.setVisibility(View.VISIBLE);
                moreLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.GONE);
                statusLayout.setVisibility(View.GONE);

                viewHomeFooter.setVisibility(View.VISIBLE);
                submitApprovalLabel.setVisibility(View.VISIBLE);
                viewMore.setVisibility(View.GONE);
                viewSetting.setVisibility(View.GONE);
                viewMap.setVisibility(View.GONE);
                statusView.setVisibility(View.GONE);

               /* homeFooter.setBackgroundColor(Color.parseColor("#c0392b"));
                homeFooter.setTextColor(Color.parseColor("#FFFFFF"));

                setting.setBackgroundColor(Color.parseColor("#FFFFFF"));
                setting.setTextColor(Color.parseColor("#000000"));
                more.setBackgroundColor(Color.parseColor("#FFFFFF"));
                more.setTextColor(Color.parseColor("#000000"));
                map.setBackgroundColor(Color.parseColor("#FFFFFF"));
                map.setTextColor(Color.parseColor("#000000"));*/


                break;
            case R.id.profileButton:
                i=new Intent(Home.this,UpdateProfile.class);
                startActivity(i);
                break;
            case R.id.kycDoc:
                i=new Intent(Home.this,KYCDocument.class);
                startActivity(i);
                break;
            case R.id.bankDetails:
                i=new Intent(Home.this,BankDetails.class);
                startActivity(i);
                break;
            case R.id.material:
                i=new Intent(Home.this,Materials.class);
               /* i.putExtra("SERVICE_IDS",serviceMaterialIds);
                i.putExtra("MATERIAL_IDS",materialIds);*/
                startActivity(i);
                break;
            case R.id.vehicleDetails:
                i=new Intent(Home.this,VehicleDetails.class);
                startActivity(i);
                break;
            case R.id.serviceSkill:
                i=new Intent(Home.this,ServicesAndSkillsFirst.class);
                startActivity(i);
                break;
            case R.id.status:
                actionBarTitleTextView.setText("SETTINGS");
                settingLyout.setVisibility(View.GONE);
                profileLayout.setVisibility(View.GONE);
                moreLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.GONE);
                statusLayout.setVisibility(View.VISIBLE);

                changeBackGround();
                viewHomeFooter.setVisibility(View.GONE);
                submitApprovalLabel.setVisibility(View.GONE);
                viewMore.setVisibility(View.GONE);
                viewSetting.setVisibility(View.GONE);
                viewMap.setVisibility(View.GONE);
                statusView.setVisibility(View.VISIBLE);


                break;
            case R.id.settings:
                actionBarTitleTextView.setText("SETTINGS");
                settingLyout.setVisibility(View.VISIBLE);
                profileLayout.setVisibility(View.GONE);
                moreLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.GONE);
                statusLayout.setVisibility(View.GONE);

                changeBackGround();
                viewHomeFooter.setVisibility(View.GONE);
                submitApprovalLabel.setVisibility(View.GONE);
                viewMore.setVisibility(View.GONE);
                viewSetting.setVisibility(View.VISIBLE);
                viewMap.setVisibility(View.GONE);
                statusView.setVisibility(View.GONE);


                break;
            case R.id.more:
                settingLyout.setVisibility(View.GONE);
                profileLayout.setVisibility(View.GONE);
                moreLayout.setVisibility(View.VISIBLE);
                mapLayout.setVisibility(View.GONE);
                statusLayout.setVisibility(View.GONE);

                viewHomeFooter.setVisibility(View.GONE);
                submitApprovalLabel.setVisibility(View.GONE);
                viewMore.setVisibility(View.VISIBLE);
                viewSetting.setVisibility(View.GONE);
                viewMap.setVisibility(View.GONE);
                statusView.setVisibility(View.GONE);



                break;
            case R.id.map:
                changeBackGround();
                mapLayout.setVisibility(View.VISIBLE);
                profileLayout.setVisibility(View.GONE);
                settingLyout.setVisibility(View.GONE);
                moreLayout.setVisibility(View.GONE);
                statusLayout.setVisibility(View.GONE);

                viewHomeFooter.setVisibility(View.GONE);
                submitApprovalLabel.setVisibility(View.GONE);
                viewMore.setVisibility(View.GONE);
                viewSetting.setVisibility(View.GONE);
                viewMap.setVisibility(View.VISIBLE);
                statusView.setVisibility(View.GONE);

                break;
            case R.id.logOut:
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setMessage("Are you sure you want to logout?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Preferences.getInstance().loadPreferences(Home.this);
                                Preferences.getInstance().isLoggedIn=false;
                                Preferences.getInstance().savePreferences(Home.this);
                                Intent i=new Intent(Home.this,MobileOTPActivity.class);
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
            case R.id.submitApprovalLabel:
                if(submitApprovalLabel.getText().toString().equalsIgnoreCase("PENDING"))
                {
                   // getTechnicianProfileStatus();
                }
                else
                {
                    submitTechnicianProfileForApproval();
                }


                break;
            case R.id.giveLeadEarn:
                changeBackGround();
                giveLeadAndEarn.setBackgroundResource(R.drawable.rectangle_background_clicked);
                giveLeadAndEarn.setTextColor(Color.parseColor("#FFFFFF"));

                Utils.showToast(Home.this,"Coming soon!");

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
                 i=new Intent(Home.this,HowItWorks.class);
                startActivity(i);
                break;
            case R.id.settingsTextView:
                changeBackGround();
                settings.setBackgroundResource(R.drawable.rectangle_background_clicked);
                settings.setTextColor(Color.parseColor("#FFFFFF"));
                i=new Intent(Home.this, com.mobiquel.urbanclap.Settings.class);
                startActivity(i);
                break;
            case R.id.termOfUse:
                changeBackGround();
                termOfUse.setBackgroundResource(R.drawable.rectangle_background_clicked);
                termOfUse.setTextColor(Color.parseColor("#FFFFFF"));
                i=new Intent(Home.this,TermsOfUse.class);
                startActivity(i);
                break;
            case R.id.contactUs:
                changeBackGround();
                contactUs.setBackgroundResource(R.drawable.rectangle_background_clicked);
                contactUs.setTextColor(Color.parseColor("#FFFFFF"));
                i=new Intent(Home.this,ContactUs.class);
                startActivity(i);
                break;
            case R.id.rateApp:
                changeBackGround();
                rateApp.setBackgroundResource(R.drawable.rectangle_background_clicked);
                rateApp.setTextColor(Color.parseColor("#FFFFFF"));
                Utils.showToast(Home.this,"Coming soon!");

                break;
            case R.id.downloadUserApp:
                changeBackGround();
                downloadUserApp.setBackgroundResource(R.drawable.rectangle_background_clicked);
                downloadUserApp.setTextColor(Color.parseColor("#FFFFFF"));

                downloadUserApp.setTextColor(Color.parseColor("#FFFFFF"));
                Utils.showToast(Home.this,"Coming soon!");

                break;
            case R.id.referTechnician:
                changeBackGround();
                referTechnician.setBackgroundResource(R.drawable.rectangle_background_clicked);
                referTechnician.setTextColor(Color.parseColor("#FFFFFF"));

                Intent i1=new Intent(Home.this,AddTechnicianForTechnician.class);

                startActivity(i1);

                break;

        }
    }
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "View Image", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(Home.this);

                if (items[item].equals("Take Photo")) {
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("View Image")) {
                    dialog.dismiss();

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Home.this);
                    LayoutInflater inflater = (LayoutInflater) Home.this
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View dialogView = inflater.inflate(R.layout.view_image, null);
                    fullImage = (ImageView) dialogView.findViewById(R.id.imageFull);
                    closePopup = (ImageView) dialogView.findViewById(R.id.closePop);
                    Preferences.getInstance().loadPreferences(Home.this);
                    if (!Preferences.getInstance().userProfilePicture.equals("")) {
                        Glide.with(Home.this).load(Preferences.getInstance().userProfilePicture)
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

                        Utils.showToast(Home.this, "Image uploaded successfully!");
                        uploadedFilePath = new String(arg2);
                        Preferences.getInstance().loadPreferences(Home.this);
                        Preferences.getInstance().userProfilePicture = uploadedFilePath;
                        Preferences.getInstance().savePreferences(Home.this);

                        updateUserProfilePic(uploadedFilePath);
                        Glide.with(Home.this).load(uploadedFilePath).asBitmap().centerCrop()
                                .into(new BitmapImageViewTarget(profilePic) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory
                                                .create(Home.this.getResources(), resource);
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
                Utils.showToast(Home.this, "Image updated successfully!");

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
                Preferences.getInstance().loadPreferences(Home.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("technicianId", Preferences.getInstance().userId);
                params.put("profileImageURL", picUrl);
                Log.e("PARAMS:", "___ " + params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(Home.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(Home.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    //getTechnicianProfileStatus
    private void getTechnicianProfileStatus() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url=AppConstants.SERVER_URL+"getTechnicianProfileStatus";

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
                       // {"responseObject":{"isProfileCreated":"T","isBankDetailCreated":"F","isKYCCreated":"T","isMaterialCreated":"F","isServicesSkillsCreated":"F","isVehicleCreated":"T","technicianProfileStatus":"INITIATED"}

                        Preferences.getInstance().loadPreferences(Home.this);
                        Preferences.getInstance().howItWorks=responseObject.getJSONObject("responseObject").getString("howItWorks");
                        Preferences.getInstance().termsOfUse=responseObject.getJSONObject("responseObject").getString("termsOfUse");
                        Preferences.getInstance().contactUs=responseObject.getJSONObject("responseObject").getString("contactUs");
                        Preferences.getInstance().savePreferences(Home.this);

                        if(responseObject.getJSONObject("responseObject").getString("isProfileCreated").equals("T"))
                        {
                            profile.setBackgroundResource(R.drawable.rectangle_background_status);
                            profile.setTextColor(Color.parseColor("#FFFFFF"));
                            profile.setText("PERSONAL INFORMATION   [ "+responseObject.getJSONObject("responseObject").getString("technicianProfileStatus")+" ]");
                        }
                        else
                        {
                            profile.setBackgroundResource(R.drawable.box);
                            profile.setTextColor(Color.parseColor("#747474"));
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
                            kycDoc.setBackgroundResource(R.drawable.rectangle_background_status);
                            kycDoc.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                        else
                        {
                            kycDoc.setBackgroundResource(R.drawable.box);
                            kycDoc.setTextColor(Color.parseColor("#747474"));
                        }
                        if(responseObject.getJSONObject("responseObject").getString("isMaterialCreated").equals("T"))
                        {
                            material.setBackgroundResource(R.drawable.rectangle_background_status);
                            material.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                        else
                        {
                            material.setBackgroundResource(R.drawable.box);
                            material.setTextColor(Color.parseColor("#747474"));
                        }
                        if(responseObject.getJSONObject("responseObject").getString("isServicesSkillsCreated").equals("T"))
                        {
                            serSkill.setBackgroundResource(R.drawable.rectangle_background_status);
                            serSkill.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                        else
                        {
                            serSkill.setBackgroundResource(R.drawable.box);
                            serSkill.setTextColor(Color.parseColor("#747474"));
                        }
                        if(responseObject.getJSONObject("responseObject").getString("isVehicleCreated").equals("T"))
                        {
                            vehicleDet.setBackgroundResource(R.drawable.rectangle_background_status);
                            vehicleDet.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                        else
                        {
                            vehicleDet.setBackgroundResource(R.drawable.box);
                            vehicleDet.setTextColor(Color.parseColor("#747474"));
                        }
                        if(responseObject.getJSONObject("responseObject").getString("technicianProfileStatus").equals("INITIATED"))
                        {
                           submitApprovalLabel.setText("SUBMIT FOR APPROVAL");
                            Preferences.getInstance().loadPreferences(Home.this);
                            Preferences.getInstance().isProfileStatusApproved=false;
                            Preferences.getInstance().savePreferences(Home.this);
                        }
                        else if(responseObject.getJSONObject("responseObject").getString("technicianProfileStatus").equals("PENDING"))
                        {
                            submitApprovalLabel.setText("WAITING FOR APPROVAL");
                            Preferences.getInstance().loadPreferences(Home.this);
                            Preferences.getInstance().isProfileStatusApproved=false;
                            Preferences.getInstance().savePreferences(Home.this);
                        }
                        else
                        {
                            submitApprovalLabel.setVisibility(View.GONE);
                            Preferences.getInstance().loadPreferences(Home.this);
                            Preferences.getInstance().isProfileStatusApproved=true;
                            Preferences.getInstance().savePreferences(Home.this);

                        }
                        //
                    }
                    else
                    {
                        //Utils.showToast(Home.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
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
                Utils.showToast(Home.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(Home.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("technicianId",Preferences.getInstance().userId);
                Log.e("PARAMS",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(Home.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(Home.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }
    private void submitTechnicianProfileForApproval() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url=AppConstants.SERVER_URL+"submitTechnicianProfileForApproval";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject responseObject;
                try {
                    responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_APPRO", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0"))
                    {
                                //submitApprovalLabel.setText("APPROVAL PENDING");
                        getTechnicianProfileStatus();
                      //  submitApprovalLabel.setTextColor(Color.parseColor("#c0392b"));

                        Preferences.getInstance().loadPreferences(Home.this);
                                Preferences.getInstance().isApprovedClicked=true;
                                Preferences.getInstance().savePreferences(Home.this);
                        getTechnicianProfileStatus();

                    }
                    else
                    {
                       // Utils.showToast(Home.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
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
                Utils.showToast(Home.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(Home.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("technicianId",Preferences.getInstance().userId);
                Log.e("PARAMS",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(Home.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(Home.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private void getProfile(){
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(Home.this).getRequestQueue();
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


                        String url=responseObject.getJSONObject("responseObject").getString("profileImageURL");
                        Preferences.getInstance().loadPreferences(Home.this);
                        Preferences.getInstance().loadPreferences(Home.this);
                        Preferences.getInstance().userName=responseObject.getJSONObject("responseObject").getString("name");
                        Preferences.getInstance().email=responseObject.getJSONObject("responseObject").getString("email");
                        Preferences.getInstance().hrReferralCode=responseObject.getJSONObject("responseObject").getString("referalCode");

                        Preferences.getInstance().userProfilePicture=url;
                        Preferences.getInstance().savePreferences(Home.this);
                        userName.setText(Preferences.getInstance().userName);
                        emailId.setText(Preferences.getInstance().email);
                        hrReferralCode.setText(Preferences.getInstance().hrReferralCode);

                    } else {
                       // Utils.showToast(Home.this, responseObject.getString("errorMessage"));
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
                Preferences.getInstance().loadPreferences(Home.this);
                Map<String, String> params = new HashMap<String, String>();
                Preferences.getInstance().loadPreferences(Home.this);
                params.put("technicianId",Preferences.getInstance().userId);
                Log.e("PARAMS",params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(Home.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(Home.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected)
        {

            Utils.showToast(Home.this, "Your internet is back!");

                sendGPSRunnable.run();

        }
        else
        {
            builder = new AlertDialog.Builder(Home.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Internet down!");
            builder.setMessage("Your internet is not working! Please try connecting.");
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        gpsTracker = new GPSTracker(Home.this);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMyLocationEnabled(true);
        Location loc = gpsTracker.getLocation();
        lat = loc.getLatitude();
        lan = loc.getLongitude();
        myPosition = new LatLng(lat, lan);

        if (loc == null) {
            Utils.showToast(Home.this, "Wait till GPS is set!");
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

}
