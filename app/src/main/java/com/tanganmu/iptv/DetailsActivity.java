package com.tanganmu.iptv;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

public class DetailsActivity extends FragmentActivity {
    public static final String SHARED_ELEMENT_NAME = "hero";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity); //Create XML View
    }

}
