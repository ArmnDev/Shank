package com.mobiquel.shank;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiquel.shank.utils.Preferences;

public class SettingsCompany extends AppCompatActivity
{
    private Button logout;
    private TextView inviteCode, companyCode, mobileNumber;
    private LinearLayout leaveLayout;

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
        inviteCode = (TextView) findViewById(R.id.inviteCode);
        companyCode = (TextView) findViewById(R.id.companyCode);
        mobileNumber = (TextView) findViewById(R.id.phoneNumber);
        leaveLayout = (LinearLayout) findViewById(R.id.leaveLayout);
        leaveLayout.setVisibility(View.GONE);

        Preferences.getInstance().loadPreferences(this);
        if (Preferences.getInstance().companyCode.equals(""))
        {
            companyCode.setText(companyCode.getText().toString() + "-");
        }
        else
        {
            companyCode.setText(companyCode.getText().toString() + Preferences.getInstance().companyCode);
        }

        if (Preferences.getInstance().userType.equalsIgnoreCase("company"))
        {
            companyCode.setVisibility(View.GONE);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsCompany.this);
                builder.setMessage("Are you sure you want to logout?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Preferences.getInstance().loadPreferences(SettingsCompany.this);
                                Preferences.getInstance().isLoggedIn = false;
                                Preferences.getInstance().companyType = "";
                                Preferences.getInstance().savePreferences(SettingsCompany.this);
                                Intent i = new Intent(SettingsCompany.this, MobileOTPActivity.class);
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
