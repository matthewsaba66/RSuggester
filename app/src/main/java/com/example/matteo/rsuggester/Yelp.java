package com.example.matteo.rsuggester;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.HashMap;

/**
 * Example for accessing the Yelp API.
 */
public class Yelp extends AsyncTask<String,Void,Response> {

    OAuthService service;
    Token accessToken;

    /**
     * Setup the Yelp API OAuth credentials.
     *
     * OAuth credentials are available from the developer site, under Manage API access (version 2 API).
     *
     * @param consumerKey Consumer key
     * @param consumerSecret Consumer secret
     * @param token Token
     * @param tokenSecret Token secret
     */
    public Yelp(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        System.out.println("INIZIA");
        this.service = new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);

    }

    /**
     * Search with term and location.
     *
     * @param term Search term
     * @param latitude Latitude
     * @param longitude Longitude
     * @return JSON string response
     */
    public String search(String term, double latitude, double longitude) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("ll", latitude + "," + longitude);
        request.addQuerystringParameter("lang", "IT");

        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    public String search2(String category, String location, String distance) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("category_filter", category);
        request.addQuerystringParameter("location", location);
        request.addQuerystringParameter("cc", "IT");
        request.addQuerystringParameter("radius_filter", distance);
        request.addQuerystringParameter("lang", "IT");

        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    public String search3(String category, double latitude, double longitude, String distance) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("category_filter", category);
        request.addQuerystringParameter("ll", latitude + "," + longitude);
        request.addQuerystringParameter("cc", "IT");
        request.addQuerystringParameter("radius_filter", distance);
        request.addQuerystringParameter("lang", "IT");

        //request.addQuerystringParameter("sort", "2");

        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }


    protected Response doInBackground(String... params) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        //request.addQuerystringParameter("term", _term);
        //request.addQuerystringParameter("ll", _lat + "," + _long);
        this.service.signRequest(this.accessToken, request);
        Response r = request.send();
        return r;
    }

    public static HashMap search(String type, String place, String mezzo) {
        // Update tokens here from Yelp developers site, Manage API access.
        String consumerKey = "3jipH-2ZXj5hwXcED6RbbA";
        String consumerSecret = "Os7OVeg26ktHZH9oVi-h4LYo6i0";
        String token = "PII5-eH5LvqdY155lh20tdIcDM3wW04p";
        String tokenSecret = "9JgST9AN_uLdg5fd22t-UtlEOu4";
        String quartiere = "";
        String[] nomeRistorante = null;
        Double[] latitudine = null;
        Double[] longitudine = null;
        Double[][] doubles = null;
        HashMap<String, Double[]> ristorante = new HashMap<String, Double[]>();
        try {
            Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
            //String response = yelp.search("burritos", 30.361471, -87.164326);
            String[] mezzi = {"auto","piedi","bicicletta"};
            String distance = "";
            switch (mezzo) {
                case "auto":
                    distance = "10000";
                    break;
                case "piedi":
                    distance = "500";
                    break;
                case "bicicletta":
                    distance = "6000";
                    break;
            }
            String response = yelp.search2(type, place, distance);
            JSONObject json = new JSONObject(response);
            //Add this line
            final JSONArray geodata = json.getJSONArray("businesses");
            JSONArray[] quartieri  = new JSONArray[geodata.length()];
            JSONObject[] coordinate  = new JSONObject[geodata.length()];
            final int n = geodata.length();
            nomeRistorante = new String[n];
            latitudine = new Double[n];
            longitudine = new Double[n];
            doubles = new Double[n][n];
            for (int i = 0; i < n; i++) {
                final JSONObject person = geodata.getJSONObject(i);
                coordinate[i] = person.getJSONObject("location").getJSONObject("coordinate");
                nomeRistorante[i] = person.getString("name");
                doubles[0][i] = coordinate[i].getDouble("latitude");
                doubles[1][i] = coordinate[i].getDouble("longitude");
            }
            for (int i = 0; i < n; i++) {
                Double[] coo = new Double[2];
                coo[0] = doubles[0][i];
                coo[1] = doubles[1][i];
                ristorante.put(nomeRistorante[i],coo);
            }
        } catch (JSONException e) {
            Log.e("MYAPP", "I shouldn't be here");
            // Do something to recover ... or kill the app.
        }
        return ristorante;
    }

    public static HashMap searchLL (String type, double latitude, double longitude, String mezzo) {
        // Update tokens here from Yelp developers site, Manage API access.
        String consumerKey = "3jipH-2ZXj5hwXcED6RbbA";
        String consumerSecret = "Os7OVeg26ktHZH9oVi-h4LYo6i0";
        String token = "PII5-eH5LvqdY155lh20tdIcDM3wW04p";
        String tokenSecret = "9JgST9AN_uLdg5fd22t-UtlEOu4";
        String quartiere = "";
        String[] nomeRistorante = null;
        Double[] latitudine = null;
        Double[] longitudine = null;
        Double[][] doubles = null;
        HashMap<String, Double[]> ristorante = new HashMap<String, Double[]>();
        try {
            Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
            //String response = yelp.search("burritos", 30.361471, -87.164326);
            String[] mezzi = {"auto","piedi","bicicletta"};
            String distance = "";
            switch (mezzo) {
                case "auto":
                    distance = "600";
                    break;
                case "piedi":
                    distance = "100";
                    break;
                case "bicicletta":
                    distance = "300";
                    break;
            }
            String response = yelp.search3(type, latitude, longitude, distance);
            JSONObject json = new JSONObject(response);
            //Add this line
            final JSONArray geodata = json.getJSONArray("businesses");
            JSONArray[] quartieri  = new JSONArray[geodata.length()];
            JSONObject[] coordinate  = new JSONObject[geodata.length()];
            final int n = geodata.length();
            nomeRistorante = new String[n];
            latitudine = new Double[n];
            longitudine = new Double[n];
            doubles = new Double[2][n];
            for (int i = 0; i < n; i++) {
                final JSONObject person = geodata.getJSONObject(i);
                coordinate[i] = person.getJSONObject("location").getJSONObject("coordinate");
                nomeRistorante[i] = person.getString("name");
                doubles[0][i] = coordinate[i].getDouble("latitude");
                doubles[1][i] = coordinate[i].getDouble("longitude");
            }
            for (int i = 0; i < n; i++) {
                Double[] coo = new Double[2];
                coo[0] = doubles[0][i];
                coo[1] = doubles[1][i];
                ristorante.put(nomeRistorante[i],coo);
            }
        } catch (JSONException e) {
            Log.e("MYAPP", "I shouldn't be here");
            // Do something to recover ... or kill the app.
        }
        return ristorante;
    }

}
