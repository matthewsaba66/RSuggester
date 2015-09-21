package com.example.matteo.rsuggester;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    LatLng myPosition;
    private TextView output;
    private LocationManager locationManager;
    private String bestProvider;
    private List<FlickrRoute> flickrRoutes;
    //final String flickrRoutesPath = "app/src/main/res/RoutesApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            builder.setTitle("GPS not found");  // GPS not found
            builder.setMessage("Enable?"); // Want to enable?
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    finish();
                }
            });
            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                   finish();
                }
            });
            builder.create().show();
            return;
        }

        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        map = mapFragment.getMap();
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMyLocationEnabled(true);
        LocationManager mLocationManager;



            try{
                Location myLocation = getLastKnownLocation();
                myPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            }
            catch (NullPointerException e){

        }
        try {
            flickrRoutes = createFlickrRoutes();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private List<FlickrRoute> createFlickrRoutes() throws IOException {
        List<FlickrRoute> flickrRoutes = new ArrayList<FlickrRoute>();
        String[] f = getAssets().list("RoutesApp");
        for(String f1 : f){
            System.out.println(f1);
            String[] ff = getAssets().list("RoutesApp/"+f1);
            for(String f2 : ff){
                System.out.println(f2);
                FlickrRoute route = new FlickrRoute();
                //apri il file
                List<float[]> intermediates = new ArrayList<float[]>();
                //leggi il file
                InputStream is = getAssets().open("RoutesApp/"+f1+"/"+f2);
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                    //aggiungi a rlista un array di float con le coordinate lette
                    String[] tokenizer = line.split("\t");
                    float[] coordinates = {0, 0};
                    coordinates[0] = Float.parseFloat(tokenizer[0]);
                    coordinates[1] = Float.parseFloat(tokenizer[1]);
                    intermediates.add(coordinates);
                }
                route.setIntermediates(intermediates);
                br.close();
                flickrRoutes.add(route);

            }
        }


        System.out.println("size: " + flickrRoutes.size());
        for (FlickrRoute r : flickrRoutes){
            System.out.println(r.toString());
            System.out.println(r.getIntermediates().toString());
        }
        return flickrRoutes;
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) MapsActivity.this.getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        //int nearest = getNearestRoute(); //solo per un percorso singolo
        List<Integer> nearests = getNearestRoutes(3);


        mkRoute(nearests.get(0), "green");
        mkRoute(nearests.get(1), "red");
        mkRoute(nearests.get(2), "blue");


        map.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(12);
        map.animateCamera(zoom);
    }





    private class Pair {
        public Double delta;
        public Integer index;

        public Pair(Double delta, Integer index) {
            this.delta = delta;
            this.index = index;
        }
    };


    private List<Integer> getNearestRoutes(int topK) {
        Location location = getLastKnownLocation();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        List<Integer> top = new ArrayList<Integer>();
        PriorityQueue<Pair> queue = new PriorityQueue<Pair>(topK, new Comparator<Pair>() {
            public int compare(Pair p1, Pair p2) {
                return -(p1.delta.compareTo(p2.delta));
            }
        });
        for (FlickrRoute f : flickrRoutes){
            double delta = Math.abs(latitude - f.getIntermediates().get(0)[0]) + Math.abs(longitude - f.getIntermediates().get(0)[1]);
            Integer index = flickrRoutes.indexOf(f);
            Pair p = new Pair(delta, index);
            queue.add(p);
            if (queue.size() > topK) {
                queue.remove();
            }
        }
        List<Pair> topKPairs = new ArrayList<Pair>();
        while (! queue.isEmpty()) {
            topKPairs.add(queue.remove());
        }
        for (Pair p1 : topKPairs){
            top.add(p1.index);
            System.out.println("index: " + p1.index + " delta: " + p1.delta);
        }
        return top;
    }

    private int getNearestRoute() {
        //prendo la mia posizione
        Location location = getLastKnownLocation();
        int index = -1;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double deltaMin = 999999999;
        for (FlickrRoute f : flickrRoutes){
            double delta = Math.abs(latitude - f.getIntermediates().get(0)[0]) + Math.abs(longitude - f.getIntermediates().get(0)[1]);
            if(delta < deltaMin){
                deltaMin = delta;
                index = flickrRoutes.indexOf(f);
            }
        }
        System.out.println("index: "+ index + " delta: "+ deltaMin);
        return index;
    }

    private void mkRoute(int nearest, String color) {
        FlickrRoute route0 = flickrRoutes.get(nearest);
        System.out.println(flickrRoutes.get(nearest).toString());
        List<float[]> intermediates = route0.getIntermediates();
        List<LatLng> positions = new ArrayList<LatLng>();

        int routeSize = intermediates.size();
        for (int i = 0; i < routeSize; i++) {
            System.out.println((double) intermediates.get(i)[0] + "\t" + (double) intermediates.get(i)[1]);

            LatLng tappa = addYelpResult("landmarks,parks", (double) intermediates.get(i)[0], (double) intermediates.get(i)[1], color, "piedi");
            if (tappa == null) {
                tappa = addYelpResult("landmarks,parks", (double) intermediates.get(i)[0], (double) intermediates.get(i)[1], color, "bicicletta");
            }
            if (tappa == null) {
                tappa = addYelpResult("landmarks,parks", (double) intermediates.get(i)[0], (double) intermediates.get(i)[1], color, "auto");
            }
            positions.add(tappa);
        }

        percorso(myPosition, positions.get(0), color);
        for (int i = 0; i < routeSize-1; i++) {
            percorso(positions.get(i), positions.get(i+1), color);
        }
    }


    public LatLng addYelpResult(String local, Double latitude, Double longitude, String color, String mezzo) {
        // BISOGNA METTERE DOPPIA SCELTA SE CON LOCALIZZAZIONE O NO ... I METODI SONO GIA FATTI
        Float hue = null;
        String[] colors = {"red", "green", "violet", "azure", "blue", "cyan", "magenta", "orange", "rose", "yellow"};
        switch (color) {
            case "red":
                hue = BitmapDescriptorFactory.HUE_RED;
                break;
            case "green":
                hue = BitmapDescriptorFactory.HUE_GREEN;
                break;
            case "violet":
                hue = BitmapDescriptorFactory.HUE_VIOLET;
                break;
            case "blue":
                hue = BitmapDescriptorFactory.HUE_BLUE;
                break;
            case "cyan":
                hue = BitmapDescriptorFactory.HUE_CYAN;
                break;
            case "magenta":
                hue = BitmapDescriptorFactory.HUE_MAGENTA;
                break;
            case "orange":
                hue = BitmapDescriptorFactory.HUE_ORANGE;
                break;
            case "rose":
                hue = BitmapDescriptorFactory.HUE_ROSE;
                break;
            case "yellow":
                hue = BitmapDescriptorFactory.HUE_YELLOW;
                break;

            case "azure":
                hue = BitmapDescriptorFactory.HUE_AZURE;
                break;
        }
        HashMap place = Yelp.searchLL(local, latitude, longitude, mezzo);
        Iterator it2 = place.entrySet().iterator();
        Double[] c = new Double[2];

        Map.Entry pair = null;
        if (it2.hasNext()) {
            pair = (Map.Entry) it2.next();
            c = (Double[]) pair.getValue();            Double[] coo = (Double[]) pair.getValue();
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(hue);
            map.addMarker(new MarkerOptions().position(new LatLng(coo[0], coo[1])).title((String) pair.getKey()).icon(bitmapDescriptor));

        } else {
            c = null;
        }

        if (c != null) {
            LatLng test = new LatLng(c[0], c[1]);
            return test;
        } else {
            return null;
        }
    }


    public void percorso(LatLng partenza, LatLng arrivo, String color) {
        int hue = 0;
        String[] colors = {"red", "green", "violet", "azure", "blue", "cyan", "magenta", "orange", "rose", "yellow"};
        switch (color) {
            case "red":
                hue = Color.RED;
                break;
            case "green":
                hue = Color.GREEN;
                break;
            case "black":
                hue = Color.BLACK;
                break;
            case "blue":
                hue = Color.BLUE;
                break;
            case "cyan":
                hue = Color.CYAN;
                break;
            case "magenta":
                hue = Color.MAGENTA;
                break;
            case "gray":
                hue = Color.GRAY;
                break;
        }
        map = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        GMapV2Direction md = new GMapV2Direction();
        Document doc = md.getDocument(partenza, arrivo, GMapV2Direction.MODE_WALKING);
        ArrayList<LatLng> directionPoint = md.getDirection(doc);
        PolylineOptions rectLine = new PolylineOptions().width(11).color(hue);

        for (int i = 0; i < directionPoint.size(); i++) {
            rectLine.add(directionPoint.get(i));
        }

        map.addPolyline(rectLine);
    }

}