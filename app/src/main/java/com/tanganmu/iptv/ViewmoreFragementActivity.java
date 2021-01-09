package com.tanganmu.iptv;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.BaseCardView;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.VerticalGridPresenter;

import com.bumptech.glide.Glide;

public class ViewmoreFragementActivity extends BrowseSupportFragment {

    private static final int NUM_COLUMNS = 3;
    private static final int NUM_ITEMS = 50;
    private static final int HEIGHT = 200;
    private static final boolean TEST_ENTRANCE_TRANSITION = true;





    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setui();
        loadrow();

    }

    private void setui(){
        Drawable logo = ContextCompat.getDrawable(getActivity() , R.drawable.logo );
        setBadgeDrawable(logo);
        setHeadersState(HEADERS_DISABLED);

    }

    private void loadrow(){



    }



}
