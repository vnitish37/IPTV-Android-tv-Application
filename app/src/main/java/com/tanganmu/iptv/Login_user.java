package com.tanganmu.iptv;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Login_user extends FragmentActivity {

    public Button Login ;
    public EditText username ;
    public Button LoginOTP;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
        setup();
        Auth();
        //checkupdateviaApi();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        username.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handled = true;
                }
                return handled;
            }
        });

    }

    private void setup(){
        Login = (Button)findViewById(R.id.UserLoginBtn);
        username = (EditText)findViewById(R.id.username);
        LoginOTP = (Button)findViewById(R.id.LoginOTP);
    }

    private void checkupdateviaApi(){
        if(true){
            CheckUpdate();
        }

    }

    private void Auth(){
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication() , "Welcome user" , Toast.LENGTH_LONG).show();
                Intent Dashboard = new Intent(getApplication() , MainActivity.class);
                startActivity(Dashboard);
                finish();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void CheckUpdate(){
        new AlertDialog.Builder(this , R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("New Update Available!!!").setMessage("WHAT'S NEW").setMessage("* Added support for QR codes so you can quickly add contacts and businesses without typing in a phone number. Tap the 'QR' icon in Settings to get started.")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkPermission()) {
                            UpdateApp atualizaApp = new UpdateApp();
                            atualizaApp.setContext(Login_user.this  );
                            atualizaApp.execute("http://136.185.4.32/app.apk");
                        } else {
                            requestPermission();
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User chose NO
                    }
                }).create().show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (locationAccepted && cameraAccepted) {
                    UpdateApp updateApp = new UpdateApp();
                    updateApp.execute("http://136.185.4.32/app.apk");
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }


    public class UpdateApp extends AsyncTask<String, Integer, String> {
        private ProgressDialog mPDialog;
        private Context mContext;

     public  void setContext(Activity context) {
            mContext = context;
         context.runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 mPDialog = new ProgressDialog(mContext);
                 mPDialog.setMessage("Please wait...");
                 mPDialog.setIndeterminate(true);
                 mPDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                 mPDialog.setCancelable(false);
                 mPDialog.show();
             }
         });

        }



        @Override
        protected String doInBackground(String... arg0) {
            try {

                URL url = new URL(arg0[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.connect();
                int lenghtOfFile = c.getContentLength();
                String PATH = Objects.requireNonNull(Objects.requireNonNull(mContext.getExternalFilesDir(null)).getAbsolutePath());
                File file = new File(PATH);
                boolean isCreate = file.mkdirs();
                File outputFile = new File(file, "iptv.apk");
                if (outputFile.exists()) {
                    boolean isDelete = outputFile.delete();
                }
                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1;
                long total = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    total += len1;
                    fos.write(buffer, 0, len1);
                    publishProgress((int) ((total * 100) / lenghtOfFile));
                }
                fos.close();
                is.close();

                if (mPDialog != null)
                    mPDialog.dismiss();
                installApk();
            } catch (Exception e) {
                Log.e("UpdateAPP", "Update error! " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mPDialog != null)
                mPDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (mPDialog != null) {
                mPDialog.setIndeterminate(false);
                mPDialog.setMax(100);
                mPDialog.setProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (mPDialog != null)
                mPDialog.dismiss();
            if (result != null)
                Toast.makeText(getApplication(), "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplication(), "File Downloaded", Toast.LENGTH_SHORT).show();
        }

        private void installApk() {
            try {
                String PATH = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getAbsolutePath();
                File file = new File(PATH + "/iptv.apk");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= 24) {
                    Uri downloaded_apk = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                    intent.setDataAndType(downloaded_apk, "application/vnd.android.package-archive");
                    List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        mContext.grantUriPermission(mContext.getApplicationContext().getPackageName() + ".provider", downloaded_apk, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }







}