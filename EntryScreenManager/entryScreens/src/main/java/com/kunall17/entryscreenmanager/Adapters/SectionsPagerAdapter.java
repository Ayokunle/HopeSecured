package com.kunall17.entryscreenmanager.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.kunall17.entryscreenmanager.Fragments.Enroll;
import com.kunall17.entryscreenmanager.Fragments.Scan;
import com.kunall17.entryscreenmanager.Fragments.Verify;
import com.kunall17.entryscreenmanager.Java.ContactInterface;

/**
 * Created by kunall17 on 1/5/16.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final int pages;
    private ContactInterface talkToActivity;

    public SectionsPagerAdapter(FragmentManager fm, int pages, Context context) {
        super(fm);
        this.pages = pages;
        try {
            talkToActivity = (ContactInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement interface.");
        }
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                talkToActivity.setContainerBackground(Color.parseColor("#F44336"));
                talkToActivity.setControlsBackground(Color.parseColor("#F44336"));
                return new Enroll();
            case 1:
                return new Scan();
            case 2:
                return new Verify();
        }
        return null;
    }

    @Override
    public int getCount() {
        return pages;
    }
}