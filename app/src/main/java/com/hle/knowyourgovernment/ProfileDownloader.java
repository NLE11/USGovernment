package com.hle.knowyourgovernment;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class ProfileDownloader implements Runnable {

    private static final String TAG = "ProfileDownloader";
    private static final String REGION_URL = "https://www.googleapis.com/civicinfo/v2/representatives";
    private static final String API = "AIzaSyD9GR0qBE-T08g2A4xJsDFGnKLuzhQjco8";
    public static HashMap<Integer, String> Index_Position = new HashMap<>();
    private MainActivity mainActivity;
    private String searchedlocation;

    public ProfileDownloader(MainActivity mainActivity, String location) {
        this.mainActivity = mainActivity;
        this.searchedlocation = location;
    }

    @Override
    public void run() {
        Uri.Builder uriBuilder = Uri.parse(REGION_URL).buildUpon(); // + "?key=" + API + "&" + "address=60656"
        // https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyD9GR0qBE-T08g2A4xJsDFGnKLuzhQjco8&address=60656
        uriBuilder.appendQueryParameter("key", API);
        uriBuilder.appendQueryParameter("address", searchedlocation);

        String urlToUse = uriBuilder.toString();

        Log.d(TAG, "run: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "run: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            return;
        }
        process(sb.toString());
        Log.d(TAG, "run: ");
    }

    private void process(String sb) {
        try {
            //JSONArray jArray = new JSONArray();
            //jArray.put(sb);
            //JSONObject jStock = (JSONObject) jArray.get(0); //take first object, in fact there is only one object

            JSONObject jProfile = new JSONObject(sb);

            JSONObject location = jProfile.getJSONObject ("normalizedInput");
            JSONArray offices = jProfile.getJSONArray("offices");
            JSONArray officials = jProfile.getJSONArray("officials");

            String locationString = "";
            if (location.getString("city").equals("")) {
                locationString = location.getString("state") + " "
                        + location.getString("zip");
            } else {
                locationString = location.getString("city") + ", "
                        + location.getString("state") + " "
                        + location.getString("zip");
            }

            final String finalLocationString = locationString;

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.updateLocation(finalLocationString);
                }
            });

            for (int i = 0; i < offices.length(); i++) {
                JSONObject jObject1 = (JSONObject) offices.get(i);

                String position = jObject1.getString("name");
                JSONArray index = jObject1.getJSONArray("officialIndices");
                for (int j = 0; j < index.length(); j++) {
                    int indice = (Integer) index.get(j);
                    Index_Position.put(indice, position); //At object[i] in offices, there are index.length() people with same position
                }
            }

            for (int indice = 0; indice < officials.length(); indice++) {
                JSONObject jObject2 = (JSONObject) officials.get(indice);

                String name = jObject2.getString("name");

                String position = Index_Position.get(indice);

                String addressString = "";
                if (jObject2.has("address")) {
                    JSONArray address = jObject2.getJSONArray("address");
                    for (int y = 0; y < address.length(); y++) { //for each address
                        JSONObject addressObject = (JSONObject) address.get(y); // get address number y
                        String line1 = addressObject.getString("line1");
                        String line2 = "";
                        if (addressObject.has("line2")) {
                            line2 = addressObject.getString("line2");
                        }
                        String city = addressObject.getString("city");
                        String state = addressObject.getString("state");
                        String zip = addressObject.getString("zip");
                        addressString = line1 + line2 + ", " + city + ", " + state + " " + zip + "\n"; //go to another line after each address
                    }
                }

                String party = "(" + jObject2.getString("party") + ")"; //if Non: Nonpartisan

                String phonesString = "";
                if (jObject2.has("phones")) {
                    JSONArray phones = jObject2.getJSONArray("phones");
                    for (int y = 0; y < phones.length(); y++) { // for each phone number
                        String phonesObject = (String) phones.get(y); // get phone number at y
                        phonesString = phonesObject + "\n"; //go to another line after each phone number
                    }
                }

                String urlsString = "";
                if (jObject2.has("urls")) {
                    JSONArray urls = jObject2.getJSONArray("urls");
                    for (int y = 0; y < urls.length(); y++) { //for each url
                        String urlsObject = (String) urls.get(y); //get url at y
                        urlsString = urlsObject + "\n"; //go to another line after each url
                    }
                }

                String emailsString = "";
                if (jObject2.has("emails")) {
                    JSONArray emails = jObject2.getJSONArray("emails");
                    for (int y = 0; y < emails.length(); y++) { //for each email
                        String emailsObject = (String) emails.get(y);
                        emailsString = emailsObject + "\n"; //go to another line after each email
                    }
                }

                String photoUrl = "";
                if (jObject2.has("photoUrl")) {
                    photoUrl = jObject2.getString("photoUrl");
                }

                String FacebookID = "";
                String YouTubeID = "";
                String TwitterID = "";
                if (jObject2.has("channels")) {
                    JSONArray channels = jObject2.getJSONArray("channels");
                    for (int y = 0; y < channels.length(); y++) { // check each object to see if they are FB, TT or YT
                        JSONObject channelsObject = (JSONObject) channels.get(y);
                        if (channelsObject.getString("type").equals("Facebook")) {
                            FacebookID = channelsObject.getString("id");
                        }
                        if (channelsObject.getString("type").equals("YouTube")) {
                            YouTubeID = channelsObject.getString("id");
                        }
                        if (channelsObject.getString("type").equals("Twitter")) {
                            TwitterID = channelsObject.getString("id");
                        }
                    }
                }

                final Official official = new Official( name,
                                                        position,
                                                        party,
                                                        addressString,
                                                        phonesString,
                                                        urlsString,
                                                        emailsString,
                                                        photoUrl,
                                                        FacebookID,
                                                        TwitterID,
                                                        YouTubeID,
                                                        indice);

                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.addOfficial(official);
                    }
                });
            }

        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
