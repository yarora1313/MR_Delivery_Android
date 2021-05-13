package com.example.mrdelivery;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class MapRequest extends AsyncTask<LatLng, Void, ArrayList> {
    private static String MAPS_KEY = "P8hjZ8AyHvMaUPnDHHQCSPxqThDAi9eO";
    public AsyncResponse resp = null;

    @Override
    protected ArrayList doInBackground(LatLng... latLngs) {
        ArrayList wayPoint = null;
        try {
            wayPoint = getWaypoints(latLngs[0], latLngs[1]);
        } catch (MalformedURLException | JSONException e) {
            e.printStackTrace();
        }

        return wayPoint;
    }

    private static ArrayList getWaypoints(LatLng start, LatLng stop) throws MalformedURLException, JSONException {
        String[] tag = {"lat", "lng"};
        ArrayList wayPoints = new ArrayList<>();
        StringBuilder inline = new StringBuilder();

        HttpURLConnection mapConnection = null;
        try {
            URL mapsURL = new URL(getURL(start, stop));
            mapConnection = (HttpURLConnection) mapsURL.openConnection();
            mapConnection.setRequestMethod("GET");
            mapConnection.connect();

            int status = mapConnection.getResponseCode();

            if (status == 200 || status == 201) {
                Scanner sc = new Scanner(mapsURL.openStream());

                while(sc.hasNext()){
                    inline.append(sc.nextLine());
                }
            } else {
                System.out.println("STATUS ERR");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        wayPoints = parseJSON(inline);

        return wayPoints;
    }

    private static String getURL(LatLng start, LatLng stop) {
        String origin = start.latitude + "," + start.longitude;
        String destination = stop.latitude + "," + stop.longitude;
        String unit = "k";
        String revGeo = "false";

        String mapsURL = "https://open.mapquestapi.com/directions/v2/route?";
        mapsURL += "key=" + MAPS_KEY;
        mapsURL += "&from=" + origin;
        mapsURL += "&to=" + destination;
        mapsURL += "&unit=" + unit;
        mapsURL += "&doReverseGeocode=" + revGeo;

        return mapsURL;


    }

    private static ArrayList parseJSON(StringBuilder inline) throws JSONException {
        ArrayList wayPoints = new ArrayList<LatLng>();

        JSONObject reader = new JSONObject(inline.toString());
        JSONObject route = (JSONObject) reader.get("route");
        JSONObject legs = (JSONObject) ((JSONArray) route.get("legs")).get(0);
        JSONArray maneuvers = (JSONArray) legs.get("maneuvers");

        for(int i=0;i<maneuvers.length();i++){
            JSONObject stPoint = (JSONObject) ((JSONObject)maneuvers.get(i)).get("startPoint");
            wayPoints.add(new LatLng(stPoint.getDouble("lat"), stPoint.getDouble("lng")));
        }

        return wayPoints;
    }

    @Override
    protected void onPostExecute(ArrayList wayPt){
        resp.processFinish(wayPt);
    }
}
