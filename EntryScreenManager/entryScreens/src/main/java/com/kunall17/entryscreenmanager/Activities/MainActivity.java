package com.kunall17.entryscreenmanager.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kunall17.entryscreenmanager.Adapters.SectionsPagerAdapter;
import com.kunall17.entryscreenmanager.Java.ContactInterface;
import com.kunall17.entryscreenmanager.R;


public class MainActivity extends AppCompatActivity implements ContactInterface {

    private ViewPager mViewPager;
    private final int NUMBER_OF_PAGES = 3;
    private Button skip;
    private Button next;
    private LinearLayout main_content;
    private LinearLayout controls_layout;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getApplicationContext();
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        main_content = (LinearLayout) findViewById(R.id.main_content);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SectionsPagerAdapter mSectionsPagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager(), NUMBER_OF_PAGES, MainActivity.this);
        mViewPager = (ViewPager) findViewById(R.id.container);
        skip = (Button) findViewById(R.id.SKIP_BUTTON);
        next = (Button) findViewById(R.id.NEXT_BUTTON);
        controls_layout = (LinearLayout) findViewById(R.id.controls_layout);


        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(R.drawable.circle_selector);
            (((LinearLayout) tabLayout.getChildAt(0)).getChildAt(i)).setPadding(0, 0, 0, 0);
        }
        tabLayout.getTabAt(0).setIcon(R.drawable.circle_selector_white);
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {

                        int position = tab.getPosition();
                        super.onTabSelected(tab);
                        if ((position) == NUMBER_OF_PAGES - 1) {
                            skip.setVisibility(View.INVISIBLE);
                            next.setText("FINISH");
                        } else {
                            skip.setVisibility(View.VISIBLE);
                            next.setText("Next");
                        }

                        switch (position) {
                            case 0:
                                tabLayout.getTabAt(0).setIcon(R.drawable.circle_selector_white);
                                tabLayout.getTabAt(1).setIcon(R.drawable.circle_selector);
                                tabLayout.getTabAt(2).setIcon(R.drawable.circle_selector);
                                setControlShown(true);
                                setContainerBackground(Color.parseColor("#F44336"));
                                setControlsBackground(Color.parseColor("#F44336"));
                                break;
                            case 1:
                                tabLayout.getTabAt(0).setIcon(R.drawable.circle_selector_white);
                                tabLayout.getTabAt(1).setIcon(R.drawable.circle_selector_white);
                                tabLayout.getTabAt(2).setIcon(R.drawable.circle_selector);
                                setControlShown(true);
                                setContainerBackground(Color.parseColor("#2196F3"));
                                setControlsBackground(Color.parseColor("#2196F3"));
                                break;
                            case 2:
                                tabLayout.getTabAt(0).setIcon(R.drawable.circle_selector_white);
                                tabLayout.getTabAt(1).setIcon(R.drawable.circle_selector_white);
                                tabLayout.getTabAt(2).setIcon(R.drawable.circle_selector_white);
                                setControlShown(true);
                                setContainerBackground(Color.parseColor("#FF5722"));
                                setControlsBackground(Color.parseColor("#FF5722"));
                                break;
                            case 3:
                                setControlShown(false);
                        }
                    }
                });
    }

    private void completed() {
        finish();
    }

    public void skip_button(View v) {
        completed();
    }

    public void next_button(View v) {
        if (mViewPager.getCurrentItem() != NUMBER_OF_PAGES - 1) {
            int page = mViewPager.getCurrentItem() + 1;
            mViewPager.setCurrentItem(page);
            tabLayout.setScrollPosition(page, 0f, true);
        } else {
            completed();
        }
    }

    @Override
    public void setContainerBackground(int COLOR) {
        main_content.setBackgroundColor(COLOR);
    }

    @Override
    public void setControlsBackground(int Color) {
        controls_layout.setBackgroundColor(Color);
        skip.setBackgroundColor(Color);
        next.setBackgroundColor(Color);

    }

    @Override
    public void setControlsTextColor(int color) {
        skip.setTextColor(color);
        next.setTextColor(color);
    }

    @Override
    public void setControlShown(Boolean state) {
        if (state) {
            controls_layout.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            skip.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);

        } else {
            controls_layout.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
            skip.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);

        }
    }
}
