package gr.uowm.icte.kudos.kudosapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.util.Locale;

import gr.uowm.icte.kudos.kudosapp.api.APIController;
import gr.uowm.icte.kudos.kudosapp.instance.Place;

public class PlaceViewActivity extends AppCompatActivity {

    private Place place;
    private String placeGiven = null;

    private APIController api;

    TextView tvPlaceName, tvPlaceArea, tvDescription;
    ImageView ivPlaceImage;
    Button bFunction;
    TextToSpeech tts;

    Locale el_gr = new Locale("el_GR");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_view);

        setPlaceGiven(getIntent().getStringExtra("type"));

        setPlace(PlaceListActivity._MYPLACES.get(getIntent().getIntExtra("place", 0)));

        initializeViews();
    }

    private void initializeViews(){

        tvPlaceName = findViewById(R.id.tvPlaceName);
        tvPlaceArea = findViewById(R.id.tvPlaceArea);
        tvDescription = findViewById(R.id.tvDescription);
        ivPlaceImage = findViewById(R.id.ivPlaceImage);
        bFunction = findViewById(R.id.bFunction);

        tvPlaceName.setText(place.getName());
        tvPlaceArea.setText(place.getArea());
        tvDescription.setText(place.getDescription());

        // download image from the web - async
        DownloadImageTask dt = new DownloadImageTask(ivPlaceImage);
        dt.execute(place.getIconUrl());


        if(getPlaceGiven().equals("place")){
            bFunction.setVisibility(View.INVISIBLE);
        }
        else if(getPlaceGiven().equals("route"))
        {
            bFunction.setText("Χάρτης");
            /**
             * Redirect to a map Uri
             * this will usually prompt to open Google Maps(?) on the phone
             */
            bFunction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri mapUri = Uri.parse("geo:"+ getPlace().getLatitude() +","+getPlace().getLongitude()+"?q=" + Uri.encode(getPlace().getName()));
                    Intent geoIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                    startActivity(geoIntent);
                }
            });
        }else if(getPlaceGiven().equals("statue"))
        {
            bFunction.setText("Αφήγηση");
            tts = new TextToSpeech(PlaceViewActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    Toast.makeText(PlaceViewActivity.this, "[KUDOS STATUE]: Ενεργοποιημένη δυνατότητα αφήγησης.",
                            Toast.LENGTH_SHORT).show();
                }
            });

            /**
             * Start a Text-To-Speech narration of the place description |
             */
            bFunction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    convertTextToSpeech();
                }
            });
        }

    }

    private void convertTextToSpeech() {
        if(place.getDescription() == null || "".equals(place.getDescription()))
        {
            place.setDescription("Δεν υπάρχει διαθέσιμη περιγραφή για αυτήν την τοποθεσία.");
            tts.setLanguage(el_gr);
            tts.speak(place.getDescription(), TextToSpeech.QUEUE_FLUSH, null);
        }else
            tts.setLanguage(el_gr);
            tts.speak(place.getDescription(), TextToSpeech.QUEUE_FLUSH, null);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String pathToFile = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(pathToFile).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(tts != null){

            tts.stop();
            tts.shutdown();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tts != null){

            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.editPlace:
                Intent editIntent = new Intent(PlaceViewActivity.this, EditPlaceActivity.class);
                editIntent.putExtra("mode", "edit");
                editIntent.putExtra("id", place.getId());
                editIntent.putExtra("name", place.getName());
                editIntent.putExtra("area", place.getArea());
                editIntent.putExtra("iconUrl", place.getIconUrl());
                editIntent.putExtra("lat", place.getLatitude());
                editIntent.putExtra("lon", place.getLongitude());
                editIntent.putExtra("description", place.getDescription());
                editIntent.putExtra("type", getPlaceGiven());
                startActivity(editIntent);
                return true;
            case R.id.deletePlace:
                APIController api = new APIController(this.getApplicationContext());
                api.deletePlace(place.getId());
                Intent i = new Intent(PlaceViewActivity.this, PlaceListActivity.class);
                i.putExtra("type", getPlaceGiven());
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getPlaceGiven() {
        return placeGiven;
    }

    public void setPlaceGiven(String placeGiven) {
        this.placeGiven = placeGiven;
    }

    public APIController getApi() {
        return api;
    }

    public void setApi(APIController api) {
        this.api = api;
    }


}
