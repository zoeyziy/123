package com.example.scheduleapp;


import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public abstract class SpoofData {

    public static HashMap<String, LatLng> hashMap = buildData();

    private static HashMap<String, LatLng> buildData() {
        HashMap<String, LatLng> hash = new HashMap<>();

        hash.put("NTB-National Tire & Battery", new LatLng(40.012740, -82.994070));
        hash.put("Campus Auto Service", new LatLng(40.007460, -83.032900));
        hash.put("Tom and Jerry's Auto Service", new LatLng(39.993960, -83.035350));
        hash.put("Tire Choice Auto Service Centers", new LatLng(40.023890, -83.026530));
        hash.put("Hound Dog's Towing & Auto Repair", new LatLng(	40.00221, 	-82.99642));

        return hash;
    }
}
