/*
  RFduinoProExample
  GUI Module - Android Cell Phone Application

  Copyright (C) 2015 - Tech Firma, LLC

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.

  Tech Firma, LLC
  Cincinnati, OH  45242

  info@techfirma.com

  ****************************

  Created October 1, 2015
  Revision 1.0
  Michael Bac

  Update History:

*/

package com.techfirma.rfduinoproexample;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.UUID;

public class MainActivity extends Activity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private String mDeviceAddress;

    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private boolean mConnected = false;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTING.equals(action)) {
                //mConnected = false;
                //updateConnectionState(R.string.connecting);
                //invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG, "ACTION:  Gatt Connected");

                mConnected = true;
                updateConnectionState(true);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG, "ACTION:  Gatt Disconnected");

                mConnected = false;
                updateConnectionState(false);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.d(TAG, "ACTION:  Gatt Services Discovered");

                // Setup notification on our characteristic of interest
                setupCharacteristicNotification();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d(TAG, "ACTION:  Data Available");

                onReceiveData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void Disconnect( ) {
        mConnected = false;
        mBluetoothLeService.disconnect();
        mBluetoothLeService.close();
        updateConnectionState(false);
        invalidateOptionsMenu();
    }

    private void updateConnectionState(final boolean connected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivityFragment frag = (MainActivityFragment) getFragmentManager().findFragmentById(R.id.fragment);
                frag.setDeviceStatus(connected);
            }
        });
    }

    private void setupCharacteristicNotification() {

        BluetoothGattService service = mBluetoothLeService.getGattService(UUID.fromString(GattAttributes.RFDUINO_SERVICE));

        if (service != null) {

            BluetoothGattCharacteristic activity = service.getCharacteristic(UUID.fromString(GattAttributes.RFDUINO_ACTIVITY));
            mBluetoothLeService.setCharacteristicNotification(activity, true);
        }
    }

    private void writeCharacteristic(byte[] data) {

        BluetoothGattService service = mBluetoothLeService.getGattService(UUID.fromString(GattAttributes.RFDUINO_SERVICE));

        if (service != null) {

            BluetoothGattCharacteristic activity = service.getCharacteristic(UUID.fromString(GattAttributes.RFDUINO_SETTINGS));
            mBluetoothLeService.writeCharacteristic(activity, data);
        }
    }

    private void onReceiveData(byte[ ] data) {
        MainActivityFragment frag = (MainActivityFragment) getFragmentManager().findFragmentById(R.id.fragment);

        switch (data[0]) {
            case 0x01:                              // Button State byte code
                if (data[1] == 0x00) {
                    frag.setPushButtonPressed(true);
                } else {
                    frag.setPushButtonPressed(false);
                }
                break;
            case 0x03:                              // LDR analog value byte code
                int ldr = (((int) data[1]) << 8 ) + (((int) data[2]) & 0xff);
                frag.setLDR(ldr);
                break;
            case 0x04:                              // Sleep Mode byte code
                if (data[1] == 0x00) {                  // 0 = 'awake', 1 = 'asleep'
                    frag.setDeviceSleepState(false);
                } else {
                    frag.setDeviceSleepState(true);
                }
                break;
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTING);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        MainActivityFragment frag = (MainActivityFragment) getFragmentManager().findFragmentById(R.id.fragment);
        frag.setButtonTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if((event.getAction() != MotionEvent.ACTION_DOWN) && (event.getAction() != MotionEvent.ACTION_UP))
                    return true;

                byte[ ] data = new byte[ 3 ];
                data[0] = 0x02;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    data[1] = 0x01;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    data[1] = 0x00;
                }

                writeCharacteristic(data);

                return false;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String deviceAddress = settings.getString("deviceAddress", "");
        String name = settings.getString(deviceAddress, settings.getString("deviceName", ""));

        MainActivityFragment frag = (MainActivityFragment) getFragmentManager().findFragmentById(R.id.fragment);
        frag.setDeviceName(name);

        updateConnectionState(mConnected);
        invalidateOptionsMenu();

        if(!mConnected || (mDeviceAddress != deviceAddress)) {
            mDeviceAddress = deviceAddress;

            if (mBluetoothLeService != null) {
                if(mConnected) {
                    Disconnect();
                }

                final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                Log.d(TAG, "Connect request result=" + result);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MainActivityFragment frag = (MainActivityFragment) getFragmentManager().findFragmentById(R.id.fragment);

        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
            frag.setButtonEnabled(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        frag.setButtonEnabled(false);
    }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_connect) {

            if (mBluetoothLeService != null) {
                final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                Log.d(TAG, "Connect request result=" + result);
            }

        } else if (id == R.id.menu_disconnect) {

            if (mBluetoothLeService != null) {
                Disconnect();
                Log.d(TAG, "Disconnect request");
            }

        } else if (id == R.id.action_devices) {

            if (mBluetoothLeService != null) {
                Disconnect();
                Log.d(TAG, "Disconnect request");
            }

            Intent intent = new Intent(getApplicationContext(), DevicesActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
