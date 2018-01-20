package com.mobiquel.urbanclap.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mobiquel.urbanclap.fragments.HRApprovedFragment;
import com.mobiquel.urbanclap.fragments.HRMemberFragment;
import com.mobiquel.urbanclap.fragments.HRPendingFragment;
import com.mobiquel.urbanclap.fragments.HRVerifiedFragment;

/**
 * Created by landshark on 4/1/18.
 */



public class HRFragmentAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] { "Pending", "Verified", "Member","Approved" };
    private String source;

    public HRFragmentAdapter(FragmentManager fm,String source) {
        super(fm);
        this.source=source;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int index)
    {
        switch (index)
        {
            case 0:
                HRPendingFragment c = new HRPendingFragment().newInstance(source);
                return c;
            case 1:
                HRVerifiedFragment c1 = new HRVerifiedFragment().newInstance(source);
                return c1;
            case 2:
                HRMemberFragment c2 = new HRMemberFragment().newInstance(source);
                return c2;
            case 3:
                HRApprovedFragment c3 = new HRApprovedFragment().newInstance(source);
                return c3;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}