package com.example.onyx_enroll_wizard_sample_app.onyx;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import com.dft.onyx.guide.CirclePageIndicator;
import com.dft.onyx.guide.PageIndicator;
import com.example.onyx_enroll_wizard_sample_app.R;

public class OnyxGuideActivity_ extends FragmentActivity {
    public static final int REQUEST_CODE = 1337;
    FragmentAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;

    public OnyxGuideActivity_() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_guide_main);
//        this.getActionBar().hide();
        this.mAdapter = new FragmentAdapter(this.getFragmentManager());
        this.mPager = (ViewPager)this.findViewById(R.id.pager);
        this.mPager.setAdapter(this.mAdapter);
        this.mIndicator = (CirclePageIndicator)this.findViewById(R.id.indicator);
        this.mIndicator.setViewPager(this.mPager);
        boolean ignorePrefs = false;
        boolean prefsDontShowGuide = false;
        boolean showGuide = false;
        Intent onyxGuideIntent = this.getIntent();
        if(onyxGuideIntent.hasExtra("ignore_guide_prefs") && onyxGuideIntent.getBooleanExtra("ignore_guide_prefs", false)) {
            ignorePrefs = true;
        }

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("ShowOnyxGuidePref", false)) {
            prefsDontShowGuide = true;
        }

        if(ignorePrefs || !prefsDontShowGuide) {
            showGuide = true;
        }

        if(!showGuide) {
            this.setResultOK();
        }

    }

    protected void setResultOK() {
        if(null != this.getCallingActivity()) {
            if(null == this.getParent()) {
                this.setResult(-1);
            } else {
                this.getParent().setResult(-1);
            }
        }

        this.finish();
    }
}
