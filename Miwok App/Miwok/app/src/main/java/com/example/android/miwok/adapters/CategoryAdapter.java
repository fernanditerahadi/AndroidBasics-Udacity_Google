package com.example.android.miwok.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.miwok.fragments.ColorsFragment;
import com.example.android.miwok.fragments.FamilyFragment;
import com.example.android.miwok.fragments.NumbersFragment;
import com.example.android.miwok.fragments.PhrasesFragment;
import com.example.android.miwok.R;

public class CategoryAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private final static int PAGE_COUNT = 4;

    public CategoryAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new NumbersFragment();
        }
        else if (position == 1) {
            return new FamilyFragment();
        }
        else if (position == 2) {
            return new ColorsFragment();
        }
        else if (position == 3) {
            return new PhrasesFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.title_numbers_activity);
        }
        else if (position == 1) {
            return mContext.getString(R.string.title_family_activity);
        }
        else if (position == 2) {
            return mContext.getString(R.string.title_colors_activity);
        }
        else if (position == 3) {
            return mContext.getString(R.string.title_phrases_activity);
        }
        return null;
    }
}
