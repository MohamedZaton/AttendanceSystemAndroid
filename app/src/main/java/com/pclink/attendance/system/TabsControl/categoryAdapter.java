package com.pclink.attendance.system.TabsControl;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pclink.attendance.system.TabAttendanceLog.AttendanceFragment;
import com.pclink.attendance.system.TabBreak.BreakFragment;
import com.pclink.attendance.system.TabCheck.CheckInFragment;
import com.pclink.attendance.system.TabRequest.RequestFragment;

/**
 * Created by mohamed.zaytoun on 9/16/2018.
 */

public class categoryAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] { "CheckIn", "Attendance", "Break","Request"};

    public categoryAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        if (position==0)
        {
            return new CheckInFragment();
        }
        else if (position==1)
        {
            return new BreakFragment();

        }
        else if (position==2)
        {
            return new AttendanceFragment();

        }
        else
        {
            return new RequestFragment();
        }

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount(){
        return 4;
    }
}

