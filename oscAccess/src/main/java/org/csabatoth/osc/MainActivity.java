/*
 * Copyright 2016 LG Electronics Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.csabatoth.osc;

import com.lge.octopus.ConnectionManager;
import com.lge.octopus.OctopusManager;
import com.lge.octopus.tentacles.wifi.client.WifiClient;
import com.lge.osclibrary.HTTP_SERVER_INFO;
import com.lge.osclibrary.HttpAsyncTask;
import com.lge.osclibrary.OSCCommandsExecute;
import com.lge.osclibrary.OSCCommandsStatus;
import com.lge.osclibrary.OSCParameterNameMapper;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Take Picture App
 * Flow:
 * Start Session
 * Take Picture
 * Check status for take picture API
 * If take picture is done,
 * Close Session
 * else
 * keep check status for take picture API
 */
public class MainActivity extends AppCompatActivity {

    private EditText editTextIPAddress;
    private Button buttonConnect;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            int result = intent.getIntExtra(WifiClient.EXTRA_RESULT, WifiClient.RESULT.DISCONNECTED);
            if (WifiClient.ACTION_WIFI_STATE.equals(action)) {
                if (result == WifiClient.RESULT.CONNECTED) {
                    buttonConnect.setText(R.string.disconnect);
                } else {
                    buttonConnect.setText(R.string.connect);
                }
            }
        }
    };
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextIPAddress = (EditText) findViewById(R.id.editTextIPAddr);
        buttonConnect = (Button) findViewById(R.id.button_connect);

        //Set IP for http request
        String IP = editTextIPAddress.getText().toString();
        setIPPort(IP);
        mContext = this;

        // register local broadcast receiver
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mReceiver, getFilter());

        buttonConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String currentConnection = buttonConnect.getText().toString();
                if (currentConnection.equals(getString(R.string.connect))) {
                    Intent i = new Intent(mContext, ConnectionActivity.class);
                    startActivity(i);
                } else {
                    disconnectWifi();
                }
            }
        });
    }

    public boolean checkIsConnectedToDevice() {
        WifiManager wifimanager;
        wifimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        WifiInfo info = wifimanager.getConnectionInfo();

        String ssid = info.getSSID();
        Log.d("HERE", " ssid = " + ssid);

        if (ssid.contains(".OSC")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //ConnectionManager mConnectionManager = OctopusManager.getInstance(mContext).getConnectionManager();
        if (checkIsConnectedToDevice()) {
            buttonConnect.setText(R.string.disconnect);
        } else {
            buttonConnect.setText(R.string.connect);
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    // disconnect wifi
    private void disconnectWifi() {
        ConnectionManager mConnectionManager = OctopusManager.getInstance(mContext).getConnectionManager();
        mConnectionManager.disconnect();
    }

    // make intent filter
    private IntentFilter getFilter() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(WifiClient.ACTION_WIFI_STATE);
        return mFilter;
    }

    private void setIPPort(String ip) {
        String[] temp = ip.split(":");
        HTTP_SERVER_INFO.IP = temp[0];
        if (temp.length == 2) {
            HTTP_SERVER_INFO.PORT = temp[1];
        } else {
            HTTP_SERVER_INFO.PORT = "6624";
        }
    }
}