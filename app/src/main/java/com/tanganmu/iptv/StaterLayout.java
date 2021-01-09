package com.tanganmu.iptv;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class StaterLayout extends FragmentActivity {

    public static int SPLASH_SCREEN_OUT = 2000;
    public boolean connected = false;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private RequestQueue queue ;
    public  String versionName1 ;
    public String notes ;
    public boolean doubleBackToExitPressedOnce = false;
    public boolean vpnStart = false ;

    private OpenVPNThread vpnThread = new OpenVPNThread();
    private OpenVPNService vpnService = new OpenVPNService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stater_layout);
        final String versionName = BuildConfig.VERSION_NAME;
        final int versionCode = BuildConfig.VERSION_CODE;
        versionName1 = versionName;
        connected = isNetworkAvailable();
        checkVersion(versionName);
    }

    private void prepareVpn() {
        if (!vpnStart) {
            if (true) {
                Intent intent = vpnService.prepare(StaterLayout.this);
                if (intent != null) {
                    startActivityForResult(intent, 1);
                } else startVpn();
            } else {
            }

        }
    }



    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
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

    private void checkVersion(final String versionName1){
       if(connected == true){
           queue = Volley.newRequestQueue(this);
           String url = "http://136.185.4.32:8081/check/checkupdate";
           JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url , null,
                   new Response.Listener<JSONObject>() {
                       @Override
                       public void onResponse(JSONObject response) {
                           try {
                               JSONObject update =  response.getJSONObject("response");
                               JSONObject obj = new JSONObject(update.toString());
                               List<String> list = new ArrayList<String>();
                               JSONArray array = obj.getJSONArray("message");
                               System.out.print(array.toString());
                               JSONObject UpdateData = array.getJSONObject(0);
                               String version = UpdateData.getString("version");
                               final String last = version ;
                               String note = UpdateData.getString("note");
                               notes = note;
                               if (versionName1.equals(last)) {
                                   stater();
                               } else {
                                   CheckUpdate(note);
                                   Toast.makeText(getApplication() , "new update available" + " "+ last , Toast.LENGTH_LONG).show();
                               }

                           } catch (JSONException e) {
                               Toast.makeText(getApplication() , "EC702" , Toast.LENGTH_LONG).show();
                               e.printStackTrace();
                           }
                       }
                   }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                   Toast.makeText(getApplication() , "EC702" , Toast.LENGTH_LONG).show();
                   checkVersion(versionName1);
                   error.printStackTrace();
               }
           });

           queue.add(request1);
       }else{
           Intent error = new Intent(getApplication(), ErrorLayout.class);
           startActivity(error);
           finish();
       }
    }

    private void stater() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (connected) {
                    Intent Login = new Intent(getApplication(), SplashScreen.class);
                    startActivity(Login);
                    finish();
                } else {
                    Intent error = new Intent(getApplication(), ErrorLayout.class);
                    startActivity(error);
                    finish();
                }

            }
        }, SPLASH_SCREEN_OUT);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void CheckUpdate(String note){
        new AlertDialog.Builder(this , R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("New Update Available!!!").setMessage("WHAT'S NEW").setMessage(note)
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkPermission()) {
                            UpdateApp atualizaApp = new UpdateApp();
                            atualizaApp.setContext( StaterLayout.this );
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
                        System.exit(0);
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
                    mPDialog.setMessage("Please wait....");
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            //Permission granted, start the VPN
            startVpn();
        } else {
           Toast.makeText(getApplication() , "not start" , Toast.LENGTH_LONG).show();
        }
    }



    private  void startVpn(){
        try {
            // .ovpn file
            InputStream conf = getApplication().getAssets().open("india.ovpn");
            InputStreamReader isr = new InputStreamReader(conf);
            BufferedReader br = new BufferedReader(isr);
            String config = "";
            String line;

            while (true) {
                line = br.readLine();
                if (line == null) break;
                config += line + "\n";
            }

            br.readLine();
            OpenVpnApi.startVpn(StaterLayout.this, config , "india", "nitish", "nitish123");

            // Update log
            vpnStart = true;

        } catch (IOException | RemoteException e) {

            e.printStackTrace();
        }
    }


}