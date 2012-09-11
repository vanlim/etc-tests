package com.cyscorpions.beatmaster;

import interpolationtests.InterpolationTest;
import tests.Test;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class BeatMasterAndroid extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
//        cfg.useGL20 = false;
        
        initialize(new InterpolationTest(), cfg);
    }
}