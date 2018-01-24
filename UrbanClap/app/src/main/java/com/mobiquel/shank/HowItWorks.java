package com.mobiquel.shank;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.mobiquel.shank.utils.Preferences;

import org.sufficientlysecure.htmltextview.HtmlResImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

public class HowItWorks extends AppCompatActivity {

    private TextView actionBarTitleTextView;
    private Toolbar toolbar;
    private HtmlTextView howItWorks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);

        getSupportActionBar().setTitle("How it works");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c0392b")));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        howItWorks= (HtmlTextView) findViewById(R.id.termsOfUse);

        Preferences.getInstance().loadPreferences(HowItWorks.this);
        howItWorks.setHtml(Preferences.getInstance().howItWorks, new HtmlResImageGetter(howItWorks));





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
