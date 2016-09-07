package com.example.onyx_enroll_wizard_sample_app.onyx;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import com.dft.onyx.guide.OnyxGuideFragment;
import com.dft.onyx.guide.OnyxGuideFragment.OnyxGuideFragmentType;

class FragmentAdapter extends FragmentPagerAdapter {
    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public Fragment getItem(int position) {
        OnyxGuideFragment fragment = new OnyxGuideFragment();
        Bundle bundle = new Bundle();
        switch(position) {
            case 0:
                bundle.putSerializable("OnyxGuideFragmentType", OnyxGuideFragmentType.WELCOME);
                break;
            case 1:
                bundle.putSerializable("OnyxGuideFragmentType", OnyxGuideFragmentType.FINGER_SELECTION);
                break;
            case 2:
                bundle.putSerializable("OnyxGuideFragmentType", OnyxGuideFragmentType.POSITIONING);
                break;
            case 3:
                bundle.putSerializable("OnyxGuideFragmentType", OnyxGuideFragmentType.DEPTH);
                break;
            case 4:
                bundle.putSerializable("OnyxGuideFragmentType", OnyxGuideFragmentType.ENROLL);
        }

        fragment.setArguments(bundle);
        return fragment;
    }

    public int getCount() {
        return 5;
    }
}
