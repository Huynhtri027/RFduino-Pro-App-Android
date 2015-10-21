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

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private boolean mConnected;
    private String mDeviceText;
    private TextView mDeviceName;
    private TextView mDeviceSleepState;
    private Button mButton;
    private Button mPushButton;
    private ProgressBar mProgressBar;
    private TextView mTextView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mDeviceName = (TextView) rootView.findViewById(R.id.deviceName);
        mDeviceSleepState = (TextView) rootView.findViewById(R.id.textView5);
        mButton = (Button) rootView.findViewById(R.id.button);
        mPushButton = (Button) rootView.findViewById(R.id.pushButton);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mTextView = (TextView) rootView.findViewById(R.id.textView);

        return rootView;
    }

    public void setDeviceName(String name)
    {
        mDeviceText = name;

        setDeviceStatus(mConnected);
    }

    public void setDeviceStatus(boolean connected) {
        mConnected = connected;

        if(connected) {
            mDeviceName.setText(mDeviceText);
            mDeviceName.setTextColor(Color.BLACK);
        } else {
            Resources res = getResources();
            String text = res.getString(R.string.disconnected).toUpperCase();

            mDeviceName.setText(text);
            mDeviceName.setTextColor(Color.RED);
            setDeviceSleepState(false);
        }

        mDeviceName.invalidate();
    }

    public void setDeviceSleepState(boolean sleepState) {
        if (mConnected) {
            if (sleepState) {
                mDeviceSleepState.setText(getString(R.string.asleep));
                mDeviceSleepState.setTextColor(Color.RED);
            } else {
                mDeviceSleepState.setText(getString(R.string.awake));
                mDeviceSleepState.setTextColor(Color.BLACK);
            }
        } else {
            mDeviceSleepState.setText(R.string.waiting);
            mDeviceSleepState.setTextColor(Color.BLACK);
        }

        mDeviceSleepState.invalidate();
    }

    public void setLDR(int value) {
        mProgressBar.setProgress(value);
        mTextView.setText(String.valueOf(value));
        mProgressBar.invalidate();
        mTextView.invalidate();
    }

    public void setPushButtonPressed(boolean pressed) {
        if (pressed) {
            mPushButton.setText(R.string.on);
        } else {
            mPushButton.setText(R.string.off);
        }

        mPushButton.setPressed(pressed);
        mPushButton.invalidate();
    }

    public void setButtonTouchListener( View.OnTouchListener listener) {
        mButton.setOnTouchListener( listener );
    }

    public void setButtonEnabled(boolean enabled) {
        mButton.setEnabled(enabled);
        mButton.invalidate();
    }
}
