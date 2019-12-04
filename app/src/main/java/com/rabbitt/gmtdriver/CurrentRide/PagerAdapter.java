package com.rabbitt.gmtdriver.CurrentRide;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> stFragment = new ArrayList<>();
    private final List<String> stTitles = new ArrayList<>();

    PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {
        return stFragment.get(i);
    }

    @Override
    public int getCount() {
        return stTitles.size();
    }

    void AddFragment(Fragment fragment, Fragment rmFragment) {
        stFragment.remove(rmFragment);
        stFragment.add(fragment);
        stTitles.add("");
    }
}
