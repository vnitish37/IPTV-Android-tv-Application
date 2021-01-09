package com.tanganmu.iptv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.ProgressBarManager;
import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.media.MediaPlayerAdapter;
import androidx.leanback.media.PlaybackTransportControlGlue;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.PlaybackControlsRow;
import androidx.leanback.widget.PlaybackRowPresenter;
import androidx.leanback.widget.PlaybackTransportRowPresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.POWER_SERVICE;
import static androidx.core.content.ContextCompat.getDrawable;
import static androidx.core.content.ContextCompat.getSystemService;


public class PlaybackVideoFragment extends VideoSupportFragment {

    private PlaybackTransportControlGlue<MediaPlayerAdapter> mTransportControlGlue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
               VideoPlayer();
        }catch (Exception y) {
            try{
                VideoPlayer();
            }catch (Exception e){
                Toast.makeText(getActivity() , "live tv not played" , Toast.LENGTH_LONG).show();
                Log.e("IPTV", "live tv not played");
                Intent intent = new Intent(getActivity() , MainActivity.class);
                startActivity(intent);
                return;
            }
        }finally {
            try {
                VideoPlayer();
            } catch (JSONException e) {
                Toast.makeText(getActivity() , "live tv not played" , Toast.LENGTH_LONG).show();
                Log.e("IPTV", "live tv not played");
                Intent intent = new Intent(getActivity() , MainActivity.class);
                startActivity(intent);
                return;
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundResource(android.R.color.black);
    }



    private void VideoPlayer() throws JSONException {
        String name = getActivity().getIntent().getStringExtra("Movie");
        JSONObject obj = new JSONObject(name);
        //video player control
        VideoSupportFragmentGlueHost glueHost =
                new VideoSupportFragmentGlueHost(PlaybackVideoFragment.this);
        MediaPlayerAdapter playerAdapter = new MediaPlayerAdapter(getActivity());
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE);
        mTransportControlGlue = new PlaybackTransportControlGlue<>(getActivity(), playerAdapter);
        mTransportControlGlue.setHost(glueHost);
        mTransportControlGlue.setTitle(obj.getString("name"));
        mTransportControlGlue.setSubtitle(obj.getString("Signal"));
        playerAdapter.setDataSource(Uri.parse(obj.getString("Video")));
        Glide.with(getActivity())
                .load(obj.getString("image"))
                .centerCrop()
                .error(R.drawable.logo)
                .into(new SimpleTarget<GlideDrawable>(200, 200) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        // Log.d(TAG, "details overview card image url ready: " + resource);
                        mTransportControlGlue.setArt(resource);

                    }
                });
        mTransportControlGlue.play();
        mTransportControlGlue.playWhenPrepared();
        mTransportControlGlue.setSeekEnabled(true);
        mTransportControlGlue.isControlsOverlayAutoHideEnabled();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mTransportControlGlue != null) {
            mTransportControlGlue.pause();
        }
    }

    @Override
    protected void onVideoSizeChanged(int width, int height) {
        width = 1920;
        height = 1080;
        super.onVideoSizeChanged(width, height);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public ProgressBarManager getProgressBarManager() {
        return super.getProgressBarManager();
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if(isInPictureInPictureMode != true){
            mTransportControlGlue.next();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    

}
