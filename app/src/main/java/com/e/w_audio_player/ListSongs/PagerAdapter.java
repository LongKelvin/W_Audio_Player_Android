package com.e.w_audio_player.ListSongs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.fragment.app.FragmentPagerAdapter;

import com.e.w_audio_player.MusicPlayer.MusicPlayerFragment;


public class PagerAdapter extends FragmentPagerAdapter{

    private int numOfTab;
    public PagerAdapter(@NonNull FragmentManager fm, int numOfTab) {
        super(fm);
        this.numOfTab=numOfTab;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new SongsFragment();
            case 1:
                return new MusicPlayerFragment();
            case 2:
                return new BlankFragment();
                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTab;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
