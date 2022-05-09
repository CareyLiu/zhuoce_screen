package com.sample;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.zhuoce.screen.R;


public class SingleVideoActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_single_video);
        if (state == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.containerView, VideoFragment.newInstance(Video.ORANGE_1.url))
                    .commit();
        }
    }
}
