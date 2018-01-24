package com.mobiquel.shank;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.mobiquel.shank.utils.Preferences;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

public class TermsOfUse extends AppCompatActivity {

    private ProgressBarCircularIndeterminate progressBar;

    private TextView actionBarTitleTextView;
    private Toolbar toolbar;
    private HtmlTextView termsOfUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);

        getSupportActionBar().setTitle("Terms Of Use");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c0392b")));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        termsOfUse = (HtmlTextView) findViewById(R.id.termsOfUse);
        Preferences.getInstance().loadPreferences(TermsOfUse.this);
        termsOfUse.setHtml(Preferences.getInstance().termsOfUse, new HtmlHttpImageGetter(termsOfUse));
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
