package com.tanganmu.iptv;

import android.os.Handler;
import android.widget.Toast;

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {
    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {

        DetailsShareData myobj = new DetailsShareData();
        viewHolder.getTitle().setText(myobj.getName());

    }
}