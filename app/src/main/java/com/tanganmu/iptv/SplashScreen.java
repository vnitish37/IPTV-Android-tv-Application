package com.tanganmu.iptv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends FragmentActivity {

    private static int SPLASH_SCREEN_OUT = 2000000 ;
    private VideoView VideoAd ;
    private Button Skipad ;
    private TextView sec ;
    private int count  = 30 ;
    public boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setup();
        videoad();
        Skipad();
        secAd();
    }

    private void splashScreen(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent Login = new Intent(getApplication() , Login_user.class);
                startActivity(Login);
                finish();
            }
        },SPLASH_SCREEN_OUT);
    }



    public void setup(){
       VideoAd = (VideoView)findViewById(R.id.VideoAD);
       Skipad = (Button)findViewById(R.id.Skipad);
       sec = (TextView)findViewById(R.id.sectxt);
    }

    public void secAd(){
        Timer T=new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        sec.setText("Ad:" +" "+ count);
                        if(count == 28){
                            Intent Login = new Intent(getApplication() , Login_user.class);
                            startActivity(Login);
                            finish();
                        }
                        count--;
                    }
                });
            }
        }, 1000, 1000);

    }

    public void Skipad(){
        Skipad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 1 ;
            }
        });
    }

    public  void videoad(){
        Uri uri = Uri.parse("http://136.185.4.32/2.mp4");
        VideoAd.setVideoURI(uri);
        VideoAd.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                //int duration = mp.getDuration() / 1000;
                //count = duration ;
                mp.setVolume(50 , 50);
            }
        });
        VideoAd.start();

    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Toast.makeText(this, "\uD83D\uDE00", Toast.LENGTH_SHORT).show();
            System.exit(0);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


}