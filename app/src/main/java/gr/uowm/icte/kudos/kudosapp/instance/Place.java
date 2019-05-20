package gr.uowm.icte.kudos.kudosapp.instance;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import gr.uowm.icte.kudos.kudosapp.R;

public class Place {

    private int id;
    private String name;
    private String area;
    private String iconUrl;
    private double latitude;
    private double longitude;
    private String description;
    private int distance;
    private int counter;

    public Place(int id, String name, String area, String iconUrl, double latitude,
                 double longitude, String description) {
        super();
        this.id = id;
        this.name = name;
        this.area = area;
        this.iconUrl = iconUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    /**
     * Get the counter saved from shared preferences for this place.
     * @param activity
     * @return counter for this place
     */
    public int getCounter(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        int counter = sharedPref.getInt(String.valueOf(getId()), 0);
        return counter;
    }

    /**
     * Save the counter to shared preferences for this place.
     * @param activity
     * @param counter
     */
    public void setCounter(Activity activity, int counter) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(String.valueOf(getId()), counter);
        editor.commit();
    }

    /**
     * Increments the counter saved in shared prefs for this place.
     * @param activity
     */
    public void incrementCounter(Activity activity){
        setCounter(activity, getCounter(activity) + 1);
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", area='" + area + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", description='" + description + '\'' +
                '}';
    }
}

