package com.tanganmu.iptv;

import android.content.pm.PackageManager;
import android.net.VpnService;
import android.net.VpnService.Builder;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


public class MyVpnService extends VpnService {
    VpnService.Builder builder= new VpnService.Builder();

    // Complete the VPN interface config.
    ParcelFileDescriptor localTunnel = builder
            .addAddress("136.185.4.32", 64)
            .addRoute("0.0.0.0", 0).addDnsServer("8.8.8.8")
            .establish();



}
