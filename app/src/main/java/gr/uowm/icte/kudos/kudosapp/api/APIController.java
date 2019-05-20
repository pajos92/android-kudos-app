package gr.uowm.icte.kudos.kudosapp.api;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import gr.uowm.icte.kudos.kudosapp.MainActivity;
import gr.uowm.icte.kudos.kudosapp.instance.Place;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import static android.content.ContentValues.TAG;

public class APIController{


    /**
     * KUDOS DATABASE API URL
     */
    private static final String KUDOS_API_PLACES = "https://kudos.xyzt.online/api.php/records/places";

    ArrayList<Place> myPlaces;
    RequestQueue requestQ;

    private Context mContext;

    /**
     * Constructor
     */
    public APIController(Context mContext) {
        this.myPlaces = new ArrayList<>();
        this.mContext = mContext;
        this.requestQ = Volley.newRequestQueue(mContext);
    }


    public void createPlace(Map place) {

        JSONObject placeJSON = new JSONObject(place);
        Log.d("[JSON POST REQUEST]", placeJSON.toString());


        JsonObjectRequest createPlaceRequest = new JsonObjectRequest(Request.Method.POST, KUDOS_API_PLACES, placeJSON,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.d("[JSON POST REQUEST]", response.toString());
                        Toast.makeText(getmContext(), response.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("[JSON PREQ ERROR]", error.toString());
                        Toast.makeText(getmContext(), error.toString(),
                                Toast.LENGTH_LONG).show();

                    }

                }
        );

        requestQ.add(createPlaceRequest);
    }

    public void updatePlace(Map place) {
        JSONObject placeJSON = new JSONObject(place);
        Log.d("[JSON POST REQUEST]", placeJSON.toString());


        JsonObjectRequest updatePlaceRequest = new JsonObjectRequest(Request.Method.PUT, KUDOS_API_PLACES + "/" + place.get("id"), placeJSON,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.d("[JSON POST REQUEST]", response.toString());
                        //Toast.makeText(getmContext(), response.toString(), Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("[JSON PREQ ERROR]", error.toString());
                    }
                });
        requestQ.add(updatePlaceRequest);
    }

    public void deletePlace(int id) {
        JsonObjectRequest deletePlaceRequest = new JsonObjectRequest(Request.Method.DELETE, KUDOS_API_PLACES + "/" + id, null,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.d("[JSON POST REQUEST]", response.toString());
                        //Toast.makeText(getmContext(), response.toString(), Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("[JSON PREQ ERROR]", error.toString());
                        //Toast.makeText(getmContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQ.add(deletePlaceRequest);
    }


    public Context getmContext() {
        return mContext;
    }


}