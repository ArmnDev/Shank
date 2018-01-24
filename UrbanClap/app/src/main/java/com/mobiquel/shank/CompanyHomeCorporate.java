package com.mobiquel.shank;

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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.mobiquel.shank.adapter.CorporateFragmentAdapter;
import com.mobiquel.shank.adapter.LeadsAdapter;
import com.mobiquel.shank.mapclasses.GPSReceiver;
import com.mobiquel.shank.mapclasses.SendGPSActivity;
import com.mobiquel.shank.model.LeadsModel;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyHomeCorporate extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback, ConnectivityReceiver.ConnectivityReceiverListener  {

    private Button homeFooter,setting,status,more,addUser,logOut;
    private View viewHomeFooter,viewSetting,viewStatus,viewMore;

    private ProgressBarCircularIndeterminate progressBar;
   // private RecyclerView leadList;
    private LeadsAdapter mAdapter;
    private LinearLayout settingLyout,companyInformationLayout,moreLayout,statusLayout,mapLayout;

    private List<LeadsModel> leads = new ArrayList<>();
    private final int PERMISSION_REQUEST = 0;
    private TextView companyName,companyCity,companyCode,accountSettingLabel,submitApprovalLabel;
    private TextView giveLeadAndEarn,shareReferral,howItWorks,termOfUse,contactUs,rateApp,downloadUserApp,referTechnician;
    //private String materialIds="",serviceMaterialIds="";

    private ImageView companyPic, fullImage, closePopup;
    private int IMAGE_PICK_REQUEST_CODE = 2;
    private FloatingActionButton updateImage;
    private Uri imageUri;
    private String uploadedFilePath;
    private int REQUEST_CAMERA = 0;

    private TextView actionBarTitleTextView;
    private Toolbar toolbar;
    private EditText name,mobile,remarks;

    private GoogleMap map;
    private Marker latestMarker;
    private LocationManager locationManager;
    private GPSTracker gpsTracker;
    private Context context;
    private double lat, lan;
    private LatLng myPosition;
    private Handler sHandler = new Handler();
    private Handler tHandler = new Handler();
    private AlertDialog alert;
    private AlertDialog.Builder builder;
    private SendGPSActivity sendGPSTrigger;
    private PendingIntent gpsPendingIntent,gpsSyncPendingIntent;
    private Intent gpsSendIntent,gpsSyncIntent;
    private AlarmManager gpsAlarmManager,gpsSyncAlarmManager;
    private GPSReceiver gpsTrigger;
    private JSONArray locationArray;
    private Runnable sendGPSRunnable = new Runnable() {
        public void run() {
            sendGPSTrigger.onReceive(ShankApplication.getInstance().getContext());

            sHandler.postDelayed(sendGPSRunnable, 7000);
        }
    };

    private Runnable gpsCapture = new Runnable() {
        public void run() {
            gpsTrigger.trigger(CompanyHomeCorporate.this);
            tHandler.postDelayed(gpsCapture, 5000);
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_home_corporate);

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


        Preferences.getInstance().loadPreferences(CompanyHomeCorporate.this);
        Preferences.getInstance().userId=Preferences.getInstance().companyId;
        Preferences.getInstance().savePreferences(CompanyHomeCorporate.this);
        homeFooter= (Button) findViewById(R.id.homeFooter);
        setting= (Button) findViewById(R.id.settings);
        status= (Button) findViewById(R.id.status);
        more= (Button) findViewById(R.id.more);

        name= (EditText) findViewById(R.id.name);
        mobile= (EditText) findViewById(R.id.mobile);
        remarks= (EditText) findViewById(R.id.remarks);

        viewHomeFooter= (View) findViewById(R.id.homeFooterView);
        viewMore= (View) findViewById(R.id.moreView);
        viewSetting= (View) findViewById(R.id.settingsView);
        viewStatus= (View) findViewById(R.id.statusView);


        addUser= (Button) findViewById(R.id.addUser);
        logOut= (Button) findViewById(R.id.logOut);

        updateImage = (FloatingActionButton) findViewById(R.id.updateImage);
        companyPic = (ImageView) findViewById(R.id.companyPic);


        companyName= (TextView) findViewById(R.id.companyName);
        companyCity= (TextView) findViewById(R.id.companyCity);
        companyCode= (TextView) findViewById(R.id.companyCode);

        giveLeadAndEarn= (TextView) findViewById(R.id.giveLeadEarn);
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
        statusLayout= (LinearLayout) findViewById(R.id.statusLayout);

        companyInformationLayout= (LinearLayout) findViewById(R.id.profileLayout);
        moreLayout= (LinearLayout) findViewById(R.id.moreLayout);
        mapLayout= (LinearLayout) findViewById(R.id.mapLayout);
        statusLayout= (LinearLayout) findViewById(R.id.statusLayout);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new CorporateFragmentAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
        progressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBar);


     /*   if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ||(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ||(ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED)
                ||(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.GET_ACCOUNTS,android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
        }
        else
        {





        }*/

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CompanyHomeCorporate.this);
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


        Preferences.getInstance().loadPreferences(this);
        if (!Preferences.getInstance().companyProfilePicture.equals("")) {
            Glide.with(CompanyHomeCorporate.this).load(Preferences.getInstance().companyProfilePicture).asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(companyPic) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory
                                    .create(CompanyHomeCorporate.this.getResources(), resource);
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
        setting.setOnClickListener(this);
        status.setOnClickListener(this);
        more.setOnClickListener(this);
        addUser.setOnClickListener(this);
        logOut.setOnClickListener(this);

        giveLeadAndEarn.setOnClickListener(this);
        shareReferral.setOnClickListener(this);
        howItWorks.setOnClickListener(this);
        termOfUse.setOnClickListener(this);
        contactUs.setOnClickListener(this);
        rateApp.setOnClickListener(this);
        downloadUserApp.setOnClickListener(this);
        referTechnician.setOnClickListener(this);

        mAdapter = new LeadsAdapter(leads,CompanyHomeCorporate.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        //prepareServicesData();


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
        getCompanyTechniciansLocation();
        ShankApplication.getInstance().setConnectivityListener(this);
        Preferences.getInstance().loadPreferences(CompanyHomeCorporate.this);
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
                Utils.showToast(CompanyHomeCorporate.this, "Sorry! unable to create maps");
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


    }

    private void prepareServicesData() {

        LeadsModel service = new LeadsModel("1. LEAD A","12/07/2017 11:00 PM");
        leads.add(service);

        service = new LeadsModel("2. LEAD B","12/08/2017 11:00 PM");
        leads.add(service);

        service = new LeadsModel("3. LEAD C","12/09/2017 11:00 PM");
        leads.add(service);

        service = new LeadsModel("4. LEAD D","12/10/2017 11:00 PM");
        leads.add(service);

        service = new LeadsModel("5. LEAD E","12/11/2017 11:00 PM");
        leads.add(service);

        service = new LeadsModel("6. LEAD F","12/12/2017 11:00 PM");
        leads.add(service);


        mAdapter.notifyDataSetChanged();
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
                companyInformationLayout.setVisibility(View.VISIBLE);
                statusLayout.setVisibility(View.GONE);
                moreLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.GONE);

                viewHomeFooter.setVisibility(View.VISIBLE);
                viewMore.setVisibility(View.GONE);
                viewSetting.setVisibility(View.GONE);
                viewStatus.setVisibility(View.GONE);


                break;

            case R.id.addUser:
                Utils.showToast(CompanyHomeCorporate.this,"Coming soon!");
                break;

            case R.id.settings:

                actionBarTitleTextView.setText("SETTINGS");
                settingLyout.setVisibility(View.VISIBLE);
                companyInformationLayout.setVisibility(View.GONE);
                statusLayout.setVisibility(View.GONE);
                moreLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.GONE);

                changeBackGround();

                viewHomeFooter.setVisibility(View.GONE);
                viewMore.setVisibility(View.GONE);
                viewSetting.setVisibility(View.VISIBLE);
                viewStatus.setVisibility(View.GONE);




                break;
            case R.id.more:

                settingLyout.setVisibility(View.GONE);
                companyInformationLayout.setVisibility(View.GONE);
                //leadList.setVisibility(View.GONE);
                moreLayout.setVisibility(View.GONE);
                mapLayout.setVisibility(View.VISIBLE);
                statusLayout.setVisibility(View.GONE);

                viewHomeFooter.setVisibility(View.GONE);
                viewMore.setVisibility(View.VISIBLE);
                viewSetting.setVisibility(View.GONE);
                viewStatus.setVisibility(View.GONE);
               // Utils.showToast(CompanyHomeCorporate.this,"Coming soon!");

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
                viewMore.setVisibility(View.GONE);
                viewSetting.setVisibility(View.GONE);
                viewStatus.setVisibility(View.VISIBLE);

               // Utils.showToast(CompanyHomeCorporate.this,"Coming soon!");

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
                AlertDialog.Builder builder = new AlertDialog.Builder(CompanyHomeCorporate.this);
                builder.setMessage("Are you sure you want to logout?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Preferences.getInstance().loadPreferences(CompanyHomeCorporate.this);
                                Preferences.getInstance().isLoggedIn=false;
                                Preferences.getInstance().savePreferences(CompanyHomeCorporate.this);
                                Intent i=new Intent(CompanyHomeCorporate.this,MobileOTPActivity.class);
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

                Utils.showToast(CompanyHomeCorporate.this,"Coming soon!");

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
                i=new Intent(CompanyHomeCorporate.this,HowItWorks.class);
                startActivity(i);
                break;
            case R.id.termOfUse:
                changeBackGround();
                termOfUse.setBackgroundResource(R.drawable.rectangle_background_clicked);
                termOfUse.setTextColor(Color.parseColor("#FFFFFF"));
                i=new Intent(CompanyHomeCorporate.this,TermsOfUse.class);
                startActivity(i);
                break;
            case R.id.contactUs:
                changeBackGround();
                contactUs.setBackgroundResource(R.drawable.rectangle_background_clicked);
                contactUs.setTextColor(Color.parseColor("#FFFFFF"));
                i=new Intent(CompanyHomeCorporate.this,ContactUs.class);
                startActivity(i);
                break;
            case R.id.rateApp:
                changeBackGround();
                rateApp.setBackgroundResource(R.drawable.rectangle_background_clicked);
                rateApp.setTextColor(Color.parseColor("#FFFFFF"));
                Utils.showToast(CompanyHomeCorporate.this,"Coming soon!");

                break;
            case R.id.downloadUserApp:
                changeBackGround();
                downloadUserApp.setBackgroundResource(R.drawable.rectangle_background_clicked);
                downloadUserApp.setTextColor(Color.parseColor("#FFFFFF"));

                downloadUserApp.setTextColor(Color.parseColor("#FFFFFF"));
                Utils.showToast(CompanyHomeCorporate.this,"Coming soon!");

                break;
            case R.id.referTechnician:
                changeBackGround();
                referTechnician.setBackgroundResource(R.drawable.rectangle_background_clicked);
                referTechnician.setTextColor(Color.parseColor("#FFFFFF"));

                Log.e("HIT","HIT");
                i = new Intent();
                i.setAction(Intent.ACTION_SEND);

                String s1="Hi! Download Appliance Care.";
                i.putExtra(Intent.EXTRA_SUBJECT, "Download Appliance Care");
                i.putExtra(Intent.EXTRA_TEXT, s1);
                i.setType("text/plain");
                startActivity(i);
                break;

        }
    }
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "View Image", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(CompanyHomeCorporate.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(CompanyHomeCorporate.this);

                if (items[item].equals("Take Photo")) {
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("View Image")) {
                    dialog.dismiss();

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CompanyHomeCorporate.this);
                    LayoutInflater inflater = (LayoutInflater) CompanyHomeCorporate.this
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View dialogView = inflater.inflate(R.layout.view_image, null);
                    fullImage = (ImageView) dialogView.findViewById(R.id.imageFull);
                    closePopup = (ImageView) dialogView.findViewById(R.id.closePop);
                    Preferences.getInstance().loadPreferences(CompanyHomeCorporate.this);
                    if (!Preferences.getInstance().companyProfilePicture.equals("")) {
                        Glide.with(CompanyHomeCorporate.this).load(Preferences.getInstance().companyProfilePicture)
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

                        Utils.showToast(CompanyHomeCorporate.this, "Image uploaded successfully!");
                        uploadedFilePath = new String(arg2);
                        Preferences.getInstance().loadPreferences(CompanyHomeCorporate.this);
                        Preferences.getInstance().companyProfilePicture = uploadedFilePath;
                        Preferences.getInstance().savePreferences(CompanyHomeCorporate.this);

                        updateUsercompanyInformationPic(uploadedFilePath);
                        Glide.with(CompanyHomeCorporate.this).load(uploadedFilePath).asBitmap().centerCrop()
                                .into(new BitmapImageViewTarget(companyPic) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory
                                                .create(CompanyHomeCorporate.this.getResources(), resource);
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
                Utils.showToast(CompanyHomeCorporate.this, "Image updated successfully!");

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
                Preferences.getInstance().loadPreferences(CompanyHomeCorporate.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyId", Preferences.getInstance().userId);
                params.put("logoImageURL", picUrl);
                Log.e("PARAMS:", "___ " + params.toString());
                return params;
            }

        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(CompanyHomeCorporate.this)) {
            requestObject.setShouldCache(false);
            queue.add(requestObject);
        } else {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(CompanyHomeCorporate.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
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
                        Preferences.getInstance().loadPreferences(CompanyHomeCorporate.this);
                        Preferences.getInstance().companyProfilePicture=responseObject.getJSONObject("responseObject").getString("logoURL");
                        Preferences.getInstance().savePreferences(CompanyHomeCorporate.this);
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
                Utils.showToast(CompanyHomeCorporate.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(CompanyHomeCorporate.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyId",Preferences.getInstance().companyId);
                Log.e("PARAMS",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(CompanyHomeCorporate.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(CompanyHomeCorporate.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }

    private void getCompanyTechniciansLocation() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url=AppConstants.SERVER_URL+"getCompanyTechniciansLocation";

        StringRequest requestObject = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    System.out.println(responseObject.toString());
                    Log.e("RESPO_TECH_ARRAY", responseObject.toString());
                    if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0")) {
                        locationArray = new JSONArray();
                        locationArray = responseObject.getJSONArray("responseObject");

                        if(locationArray.length()>0)
                        {
                            Log.e("TECH_LOCATION_ARRAY", "ARRAY" + responseObject.toString());
                            int l = locationArray.length();
                            PolylineOptions options = new PolylineOptions().width(12).color(Color.TRANSPARENT)
                                    .geodesic(true);
                            for (int z = 0; z < l; z++) {
                                if(locationArray.getJSONObject(z).getString("latitude").equals("null")||locationArray.getJSONObject(z).getString("longitude").equals("null"))
                                {

                                }
                                else
                                {
                                    MarkerOptions mo = new MarkerOptions();
                                    mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_technician_icon));

                                    LatLng point = new LatLng(
                                            Double.parseDouble(locationArray.getJSONObject(z).getString("latitude")),
                                            Double.parseDouble(locationArray.getJSONObject(z).getString("longitude")));
                                    options.add(point);

                                    String title = locationArray.getJSONObject(z).getString("name")+"\n"+"ID: "+locationArray.getJSONObject(z).getString("technicianId");
                                    mo.position(new LatLng(Double.parseDouble(locationArray.getJSONObject(z).getString("latitude")), Double.parseDouble(locationArray.getJSONObject(z).getString("longitude"))));
                                    Marker marker = map.addMarker(mo);
                                    marker.setTitle(title);
                                    marker.showInfoWindow();
                                    map.addMarker(mo);
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
                                }


                            }
                        }
                        else
                        {
                           // Utils.showToast(CompanyHomeCorporate.this,"No Data for technicians found!");
                        }

                    }
                    else
                    {
                        //Utils.showToast(CompanyHomeCorporate.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
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
                Utils.showToast(CompanyHomeCorporate.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(CompanyHomeCorporate.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyId",Preferences.getInstance().companyId);
                Log.e("PARAMS",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(CompanyHomeCorporate.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(CompanyHomeCorporate.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        gpsTracker = new GPSTracker(CompanyHomeCorporate.this);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMyLocationEnabled(true);
        Location loc = gpsTracker.getLocation();
        lat=loc.getLatitude();
        lan=loc.getLongitude();
        myPosition = new LatLng(lat, lan);

        if (loc == null) {
            Utils.showToast(CompanyHomeCorporate.this, "Wait till GPS is set!");
        } else
        {
            lat = loc.getLatitude();
            lan = loc.getLongitude();
        }

        myPosition = new LatLng(lat, lan);
        if (latestMarker != null) {
            latestMarker.remove();
        }
       /* latestMarker = map.addMarker(new MarkerOptions().position(myPosition)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_add)));
        latestMarker.showInfoWindow();*/
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lan), 15));

    }

    private void toa() {
        System.out.println("abc");
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected)
    {
        if (isConnected)
        {
            Preferences.getInstance().loadPreferences(this);
            Utils.showToast(CompanyHomeCorporate.this, "Your internet is back!");
            Utils.showToast(CompanyHomeCorporate.this, "Your internet is back!");
            sendGPSRunnable.run();
        }
        else
        {
            builder = new AlertDialog.Builder(CompanyHomeCorporate.this, R.style.AppCompatAlertDialogStyle);
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
                        Preferences.getInstance().loadPreferences(CompanyHomeCorporate.this);
                        Preferences.getInstance().howItWorks=responseObject.getJSONObject("responseObject").getString("howItWorks");
                        Preferences.getInstance().termsOfUse=responseObject.getJSONObject("responseObject").getString("termsOfUse");
                        Preferences.getInstance().contactUs=responseObject.getJSONObject("responseObject").getString("contactUs");
                        Preferences.getInstance().savePreferences(CompanyHomeCorporate.this);


                    }
                    else
                    {
                        //Utils.showToast(CompanyHomeCorporate.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
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
                Utils.showToast(CompanyHomeCorporate.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Preferences.getInstance().loadPreferences(CompanyHomeCorporate.this);
                Map<String, String> params = new HashMap<String, String>();
                params.put("companyId",Preferences.getInstance().companyId);
                Log.e("PARAMS",params.toString());
                return params;
            }
        };
        requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(CompanyHomeCorporate.this)) {
            queue.add(requestObject);
        } else
        {
            if (progressBar != null && progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            Utils.showToast(CompanyHomeCorporate.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
        }
    }
}
