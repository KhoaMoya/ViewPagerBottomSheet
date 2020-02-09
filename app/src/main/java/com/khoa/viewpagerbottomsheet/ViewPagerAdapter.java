package com.khoa.viewpagerbottomsheet;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private View container;

    public ViewPagerAdapter(@NonNull FragmentManager fm, View container) {
        super(fm);
        this.container = container;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new ExampleFragment(container);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0) return "Một";
        if(position==1) return "Hai";
        if(position==2) return "Ba";
        return "";
    }
}
