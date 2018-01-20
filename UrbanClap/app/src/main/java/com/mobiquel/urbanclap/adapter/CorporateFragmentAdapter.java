package com.mobiquel.urbanclap.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mobiquel.urbanclap.fragments.CorporateCompleteFragment;
import com.mobiquel.urbanclap.fragments.CorporatePendingFragment;
import com.mobiquel.urbanclap.fragments.CorporateProcessFragment;
import com.mobiquel.urbanclap.fragments.CorporateRejectFragment;
import com.mobiquel.urbanclap.fragments.HRApprovedFragment;
import com.mobiquel.urbanclap.fragments.HRMemberFragment;
import com.mobiquel.urbanclap.fragments.HRPendingFragment;

/**
 * Created by landshark on 4/1/18.
 */



public class CorporateFragmentAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] { "Pending", "Process", "Reject","Complete" };

    public CorporateFragmentAdapter(FragmentManager fm) {
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
                CorporatePendingFragment c = new CorporatePendingFragment();
                return c;
            case 1:
                CorporateProcessFragment c1 = new CorporateProcessFragment();
                return c1;
            case 2:
                CorporateRejectFragment c2 = new CorporateRejectFragment();
                return c2;
            case 3:
                CorporateCompleteFragment c3 = new CorporateCompleteFragment();
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