package com.mobiquel.urbanclap;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.mobiquel.urbanclap.utils.Preferences;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlResImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

public class ContactUs extends AppCompatActivity {

    private HtmlTextView howItWorks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);

        getSupportActionBar().setTitle("Contact Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c0392b")));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        howItWorks= (HtmlTextView) findViewById(R.id.termsOfUse);

        Preferences.getInstance().loadPreferences(ContactUs.this);
        howItWorks.setHtml(Preferences.getInstance().contactUs, new HtmlResImageGetter(howItWorks));


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
