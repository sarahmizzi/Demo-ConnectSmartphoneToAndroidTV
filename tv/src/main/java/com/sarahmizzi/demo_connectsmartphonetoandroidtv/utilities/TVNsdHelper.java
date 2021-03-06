package com.sarahmizzi.demo_connectsmartphonetoandroidtv.utilities;

/*
    Tutorial: http://developer.android.com/training/connect-devices-wirelessly/nsd.html
 */

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.sarahmizzi.demo_connectsmartphonetoandroidtv.MainActivity;

/**
 * Created by Sarah on 28-Sep-15.
 */
public class TVNsdHelper {
    Context mContext;

    NsdManager mNsdManager;
    NsdManager.RegistrationListener mRegistrationListener;

    private static final String SERVICE_TYPE = "_http._tcp.";

    public static final String TAG = MainActivity.class.getSimpleName();
    public String mServiceName = "Wemote";


    public TVNsdHelper(Context context){
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void initializeRegistrationListener(){
        // Setup registration listener
        mRegistrationListener = new NsdManager.RegistrationListener(){
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                mServiceName = serviceInfo.getServiceName();
                Log.d(TAG, "Service registered: " + serviceInfo.getServiceName());
                MainActivity mainActivity = new MainActivity();
                mainActivity.startThread();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Service unregistered: " + serviceInfo.getServiceName());
            }
        };
    }

    public void registerService(int port){
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(mServiceName);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    public void unregisterService(){
        mNsdManager.unregisterService(mRegistrationListener);
    }
}
