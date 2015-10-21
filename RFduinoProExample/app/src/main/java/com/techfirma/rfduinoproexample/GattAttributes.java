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

import java.util.HashMap;

public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap();

    public static String RFDUINO_SERVICE = "00002220-0000-1000-8000-00805f9b34fb";
    public static String RFDUINO_ACTIVITY = "00002221-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String RFDUINO_SETTINGS = "00002222-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put(RFDUINO_SERVICE, "RFduino Service");

        // Activity Characteristic
        attributes.put(RFDUINO_ACTIVITY, "RFduino Activity");

        // Settings Characteristic
        attributes.put(RFDUINO_SETTINGS, "RFduino Settings");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
