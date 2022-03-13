package com.ucc.csbsafety.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ucc.csbsafety.fragment1;
import com.ucc.csbsafety.fragment2;
import com.ucc.csbsafety.fragment3;

public class fragPagerAdapter extends FragmentStateAdapter {
    int pos;
    public fragPagerAdapter(@NonNull FragmentActivity fragmentActivity,int pos) {
        super(fragmentActivity);
        this.pos = pos;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new fragment1();
            case 1:
                return new fragment2();
            default:
                return new fragment3();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void updatePos(int pos){
        switch (pos){
            case 0:
                new fragment1();
                return;
            case 1:
                new fragment2();
                return;
            default:
                new fragment3();
        }
    }
}
