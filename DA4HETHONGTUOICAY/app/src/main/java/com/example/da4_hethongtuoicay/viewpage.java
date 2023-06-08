package com.example.da4_hethongtuoicay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class viewpage extends FragmentStatePagerAdapter {
    public viewpage(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0 :
                return new Main();
            case 1 :
                return new Profile();
//            case 2 :
//                return new Setting();

            default:
                return new Main();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title ="";
        switch (position){
            case 0 :
                title = "Main";
                break;
            case 1 :
                title = "Profile";
                break;
            case 2 :
                title = "Setting";
                break;
        }
       return title;
    }
}
