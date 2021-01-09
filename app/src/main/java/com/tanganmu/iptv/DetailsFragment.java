package com.tanganmu.iptv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.leanback.app.DetailsFragmentBackgroundController;
import androidx.leanback.app.DetailsSupportFragment;
import androidx.leanback.app.DetailsSupportFragmentBackgroundController;
import androidx.leanback.media.MediaPlayerGlue;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.BaseOnItemViewClickedListener;
import androidx.leanback.widget.BaseOnItemViewSelectedListener;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.OnActionClickedListener;
import androidx.leanback.widget.SparseArrayObjectAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;

import static android.media.session.PlaybackState.ACTION_PLAY;
import static android.media.session.PlaybackState.ACTION_REWIND;

public class DetailsFragment extends DetailsSupportFragment  {
    Drawable poster = null;
    private static final int DETAIL_THUMB_WIDTH = 300;
    private static final int DETAIL_THUMB_HEIGHT = 290;
    private SingleRowView mSelectedMovie;
    DetailsSupportFragmentBackgroundController mController = new DetailsSupportFragmentBackgroundController(this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildUI();
        mController.enableParallax();
    }

    private void buildUI()  {
        ClassPresenterSelector selector = new ClassPresenterSelector();
        selector.addClassPresenter(DetailsOverviewRow.class,
                new FullWidthDetailsOverviewRowPresenter(
                        new DetailsDescriptionPresenter()
                )
        );
        ArrayObjectAdapter madapter =new ArrayObjectAdapter(selector);
        DetailsOverviewRow detailsOverview = new DetailsOverviewRow("Media Item Details");
        int width = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT);
        try{
            String name = getActivity().getIntent().getStringExtra("Movie");
            JSONObject obj = new JSONObject(name);

            // Add images and action buttons to the details view
            Glide.with(getActivity())
                    .load(obj.getString("image"))
                    .centerCrop()
                    .error(R.drawable.logo)
                    .into(new SimpleTarget<GlideDrawable>(width, height) {
                        @Override
                        public void onResourceReady(GlideDrawable resource,
                                                    GlideAnimation<? super GlideDrawable>
                                                            glideAnimation) {
                            detailsOverview.setImageDrawable(resource);

                        }
                    });
            Glide.with(getActivity())
                    .load(obj.getString("image"))
                    .asBitmap()
                    .centerCrop()
                    .error(R.drawable.logo)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap,
                                                    GlideAnimation<? super Bitmap> glideAnimation) {
                            mController.setCoverBitmap(bitmap);

                        }
                    });
        }catch (Exception e){
               e.printStackTrace();
        }
        //Add some Actions
        SparseArrayObjectAdapter actionAdap = new SparseArrayObjectAdapter();
        actionAdap.set((int)ACTION_PLAY,new Action(1, "Play"));
       // actionAdap.set((int)ACTION_REWIND,new Action(2, ""));
        detailsOverview.setActionsAdapter(actionAdap);
        madapter.add(detailsOverview);
        setAdapter(madapter);
    }

    private int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public void setOnItemViewSelectedListener(BaseOnItemViewSelectedListener listener) {
        Toast.makeText(getActivity() , "working 1" , Toast.LENGTH_LONG).show();
        super.setOnItemViewSelectedListener(listener);
    }


    @Override
    public void setOnItemViewClickedListener(BaseOnItemViewClickedListener listener) {
        Toast.makeText(getActivity() , "working 1" , Toast.LENGTH_LONG).show();
        super.setOnItemViewClickedListener(listener);
    }


}
