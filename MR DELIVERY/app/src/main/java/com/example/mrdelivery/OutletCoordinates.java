package com.example.mrdelivery;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class OutletCoordinates {
    private static LatLng yumpies = new LatLng(17.543568, 78.575904);
    private static LatLng c3 = new LatLng(17.543808, 78.572222);
    private static LatLng fruitful = new LatLng(17.541162, 78.574988);

    public static LatLng getOutletCoords(String outlet) throws Exception {
        switch (outlet){
            case "YUMMPYS":
                return yumpies;
            case "C3":
                return c3;
            case "FRUITFUL":
                return fruitful;
            default:
                throw new Exception("Invalid outlet name was passed.");
        }
    }
}
