package com.sarahmizzi.demo_connectsmartphonetoandroidtv;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import com.sarahmizzi.demo_connectsmartphonetoandroidtv.fragments.ConnectFragment;
import com.sarahmizzi.demo_connectsmartphonetoandroidtv.fragments.RemoteFragment;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends Activity implements ConnectFragment.OnConnectListener, RemoteFragment.OnButtonPressedListener{
    private boolean connected = false;
    InetAddress serverIpAddress;
    int port;
    String command = "";
    Socket mSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, new ConnectFragment(), "connectFragment")
                .commit();
    }

    public class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                //InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                mSocket = new Socket(serverIpAddress, port);
                connected = true;
                if (connected) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new RemoteFragment(), "remoteFragment")
                            .commit();

                }
            } catch (Exception e) {
                ConnectFragment connectFragment = (ConnectFragment) getFragmentManager().findFragmentByTag("connectFragment");

                if(connectFragment == null){
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new ConnectFragment(), "connectFragment")
                            .commit();

                    connectFragment = (ConnectFragment) getFragmentManager().findFragmentByTag("connectFragment");
                }

                if(connectFragment != null){
                    Log.e("ClientActivity", "Error: Something went very very wrong." + e.getMessage());
                }
                connected = false;
            }
        }
    }

    @Override
    public void onConnectTo(InetAddress host, int port) {
        serverIpAddress = host;
        this.port = port;
        if (!connected) {
            if (host != null) {
                Thread cThread = new Thread(new ClientThread());
                cThread.start();
            }
        }
    }

    @Override
    public void buttonPressed(String s) {
        command = s;
        if(s.equals("EXIT")){
            if(mSocket != null) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    Log.e("ClientActivity", e.getMessage());
                }
                connected = false;
                Log.d("ClientActivity", "C: Closed.");

                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new ConnectFragment(), "connectFragment")
                        .commit();
            }
        }
        else {
            if (connected) {
                try {
                    Log.d("ClientActivity", "C: Sending command.");
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())), true);
                    // Issue Commands
                    if (!command.equals("")) {
                        out.println(command);
                        Log.d("ClientActivity", "C: Sent.");
                    }
                } catch (Exception e) {
                    Log.e("ClientActivity", "S: Error" + e.getMessage(), e);
                    RemoteFragment remoteFragment = (RemoteFragment) getFragmentManager().findFragmentByTag("remoteFragment");

                    if (remoteFragment == null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, new RemoteFragment(), "remoteFragment")
                                .commit();

                        remoteFragment = (RemoteFragment) getFragmentManager().findFragmentByTag("remoteFragment");
                    }

                    if (remoteFragment != null) {
                        Log.e("ClientActivity", "S: Error" + e.getMessage());
                    }
                }
            }
        }
    }
}
