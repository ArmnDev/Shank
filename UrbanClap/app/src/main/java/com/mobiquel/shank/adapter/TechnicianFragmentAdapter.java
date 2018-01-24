package com.mobiquel.shank.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mobiquel.shank.fragments.TechApprovedFragment;
import com.mobiquel.shank.fragments.TechMemberFragment;
import com.mobiquel.shank.fragments.TechPendingFragment;
import com.mobiquel.shank.fragments.TechVerifiedFragment;

/**
 * Created by landshark on 4/1/18.
 */



public class TechnicianFragmentAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] { "Pending", "Verified", "Member","Approved" };

    public TechnicianFragmentAdapter(FragmentManager fm) {
        super(fm);
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
                TechPendingFragment c = new TechPendingFragment();
                return c;
            case 1:
                TechVerifiedFragment c1 = new TechVerifiedFragment();
                return c1;
            case 2:
                TechMemberFragment c2 = new TechMemberFragment();
                return c2;
            case 3:
                TechApprovedFragment c3 = new TechApprovedFragment();
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