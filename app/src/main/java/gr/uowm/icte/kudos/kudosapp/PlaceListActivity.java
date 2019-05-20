package gr.uowm.icte.kudos.kudosapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import gr.uowm.icte.kudos.kudosapp.api.HttpHandler;
import gr.uowm.icte.kudos.kudosapp.instance.Place;

import static android.content.ContentValues.TAG;

public class PlaceListActivity extends AppCompatActivity implements LocationListener {

    /**
     * KUDOS DATABASE API URL
     */
    private static final String KUDOS_API_PLACES = "https://kudos.xyzt.online/api.php/records/places";

    public static ArrayList<Place> _MYPLACES;

    private ArrayAdapter<Place> listAdapter = null;
    private String placeType = null;

    // static values for my location - changes onLocationChanged()
    public static double myLatitude = 0;
    public static double myLongitude = 0;

    TextView tvPlaceType, tvPlaceDistance;
    ImageView ivPlaceIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        _MYPLACES = new ArrayList<>();

        new GetPlacesAPI().execute();

        tvPlaceType = (TextView) findViewById(R.id.tvPlaceType);
        ivPlaceIcon = (ImageView) findViewById(R.id.place_icon);

        /**
         * Set the place type to send later in PlaceViewActivity
         */

        setPlaceType(getIntent().getStringExtra("type"));
        Log.d(TAG, "place type passed: " + getPlaceType());
        switch(getPlaceType()){
            case "place":
                tvPlaceType.setText("Kudos Place");
                break;
            case "route":
                tvPlaceType.setText("Kudos Route");
                break;
            case "statue":
                tvPlaceType.setText("Kudos Statue");
                break;
            default:
                break;
        }

        if (getPlaceType().equals("route")) {
            registerLocationListener();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.insertPlace:
                Intent i = new Intent(PlaceListActivity.this, EditPlaceActivity.class);
                i.putExtra("type", getPlaceType());
                startActivity(i);
                Toast.makeText(getApplicationContext(),"Εισάγετε νέα τοποθεσία",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // register adapter for the listview
    private void populateListView() {
        listAdapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.listPlaces);
        list.setAdapter(listAdapter);

        //sort by comparing the distances
        Collections.sort( _MYPLACES, counterComparator);
        // notify out list that data have changed
        listAdapter.notifyDataSetChanged();

    }

    private void registerOnClickCallback()
    {
        ListView list = (ListView) findViewById(R.id.listPlaces);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {
                switch (getPlaceType()){
                    case "place":
                        Intent details = new Intent(PlaceListActivity.this, PlaceViewActivity.class);
                        details.putExtra("type","place");
                        details.putExtra("place", position);
                        startActivity(details);
                        break;
                    case "route":
                        Intent route = new Intent(PlaceListActivity.this, PlaceViewActivity.class);
                        route.putExtra("type","route");
                        route.putExtra("place", position);
                        Toast.makeText(getBaseContext(), _MYPLACES.get(position).getDistance() + "m", Toast.LENGTH_LONG);
                        startActivity(route);
                        break;
                    case "statue":
                        Intent statue = new Intent(PlaceListActivity.this, PlaceViewActivity.class);
                        statue.putExtra("type","statue");
                        statue.putExtra("place", position);
                        startActivity(statue);
                        break;
                    default:
                        Toast.makeText(getBaseContext(), "Not correct extra value.", Toast.LENGTH_LONG)
                                .show();
                }
                _MYPLACES.get(position).incrementCounter(PlaceListActivity.this);
                Toast.makeText(getBaseContext(), _MYPLACES.get(position).getName() + ". Το έχεις επισκεφτεί " + _MYPLACES.get(position).getCounter(PlaceListActivity.this) + " φορές.", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void registerLocationListener() {

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // Register the listener with the Location Manager to receive location
        // updates via GPS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);
    }

    private class MyListAdapter extends ArrayAdapter<Place> {
        public MyListAdapter() {
            super(PlaceListActivity.this, R.layout.item_view, _MYPLACES);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_view,
                        parent, false);
            }

            // Find the place to work
            Place currentPlace =  _MYPLACES.get(position);

            // Fill the view
            ImageView imageView = (ImageView) itemView
                    .findViewById(R.id.place_icon);

            switch (getPlaceType()){
                case "place":
                    imageView.setImageResource(R.mipmap.ic_place_round);
                    break;
                case "route":
                    imageView.setImageResource(R.mipmap.ic_route_round);
                    break;
                case "statue":
                    imageView.setImageResource(R.mipmap.ic_statue_round);
                    break;
                default:
                    imageView.setImageResource(R.drawable.icon);
                    break;
            }

            // Name:
            TextView tvName = (TextView) itemView.findViewById(R.id.place_name);
            tvName.setText(currentPlace.getName());

            // Area:
            TextView tvArea = (TextView) itemView.findViewById(R.id.place_area);
            tvArea.setText(currentPlace.getArea());

            TextView tvDistance = (TextView) itemView.findViewById(R.id.place_distance);
            // Distance
            if (getPlaceType().equals("route")){
                if (myLatitude == 0 || myLongitude == 0) {
                    tvDistance.setText("Υπολογισμός απόστασης...");
                } else {
                    currentPlace.setDistance(calculateDistance(currentPlace.getLatitude(),currentPlace.getLongitude(), myLatitude, myLongitude));
                    tvDistance.setText("" + currentPlace.getDistance() + "m");
                }
            } else {
                tvDistance.setText(currentPlace.getCounter(PlaceListActivity.this) + " προβολές ");
            }


            return itemView;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();

        // update distance between me and all locations
        for (Place p : _MYPLACES) {
            p.setDistance(calculateDistance(p.getLatitude(),p.getLongitude(), location.getLatitude(), location.getLongitude()));

            //notify user that walks by the place
            if (p.getDistance() < 10){
                notifyUserWhenNear(p);
            }
        }
        //sort by comparing the distances
        Collections.sort( _MYPLACES, distanceComparator);
        // notify out list that data have changed
        listAdapter.notifyDataSetChanged();
    }

    /**
     * Distance comperator for list adapter sort function
     */
    public Comparator<Place> distanceComparator = new Comparator<Place>() {
        @Override
        public int compare(Place p1, Place p2) {
            return (int) (p1.getDistance() - p2.getDistance());
        }
    };

    /**
     * Counter comparator to sort list adapter by counter (most visited)
     */
    public Comparator<Place> counterComparator = new Comparator<Place>() {
        @Override
        public int compare(Place p1, Place p2) {
            return (int) (p2.getCounter(PlaceListActivity.this) - p1.getCounter(PlaceListActivity.this));
        }
    };

    public void notifyUserWhenNear(Place p){
        NotificationManager notification = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify=new Notification.Builder
                (getApplicationContext()).setContentTitle("..:: Kudos Place Notification ::..").setContentText("You are passing by " + p.getName()).
                setContentTitle("Kudos Notification").setSmallIcon(R.drawable.logo).build();

        notify.flags |= Notification.FLAG_AUTO_CANCEL;

        notification.notify(0, notify);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }


    // calculate distance between 2 points
    private int calculateDistance(double lat1, double long1, double lat2,
                                  double long2) {

        Location loc1 = new Location("some place");
        loc1.setLatitude(lat1);
        loc1.setLongitude(long1);

        Location loc2 = new Location("my current location");
        loc2.setLatitude(lat2);
        loc2.setLongitude(long2);

        int distance = (int) (loc1.distanceTo(loc2));
        return distance;

    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private class GetPlacesAPI extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(getmContext(),"Places are being synced from API...",Toast.LENGTH_SHORT).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(KUDOS_API_PLACES);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray places = jsonObj.getJSONArray("records");

                    // looping through All Contacts
                    for (int i = 0; i < places.length(); i++) {
                        JSONObject place = places.getJSONObject(i);

                        String id = place.get("id").toString();
                        String name = place.get("name").toString();
                        String area = place.get("area").toString();
                        String iconUrl = place.get("icon").toString();
                        String latitude = place.get("latitude").toString();
                        String longitude = place.get("longitude").toString();
                        String description = place.get("description").toString();

                        Place p = new Place(Integer.valueOf(id), name, area, iconUrl, Double.valueOf(latitude), Double.valueOf(longitude), description);

                        // adding contact to contact list
                        _MYPLACES.add(p);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Toast.makeText( getApplicationContext(),_MYPLACES.size() + " places synced from API.",Toast.LENGTH_SHORT).show();

            // apply the adapter on our listview
            populateListView();
            // set some callbacks of the list items
            registerOnClickCallback();
        }
    }
}
