package gr.uowm.icte.kudos.kudosapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import gr.uowm.icte.kudos.kudosapp.api.APIController;

public class EditPlaceActivity extends AppCompatActivity {

    EditText etName, etArea, etIconUrl, etLat, etLon, etDescription;
    TextView tvEditPlaceHeader;
    Button bSubmit;
    int placeId;

    private String placeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_place);
        initializeViews();

        final APIController api = new APIController(getApplicationContext());
        setPlaceType(getIntent().getStringExtra("type"));

        if(isEditMode()){
            tvEditPlaceHeader.setText("Επεξεργασία τοποθεσίας");
            setPlaceId(getIntent().getIntExtra("id", 0));
            etName.setText(getIntent().getStringExtra("name"));
            etArea.setText(getIntent().getStringExtra("area"));
            etIconUrl.setText(getIntent().getStringExtra("iconUrl"));
            etLat.setText(String.valueOf(getIntent().getDoubleExtra("lat", 0)) );
            etLon.setText(String.valueOf(getIntent().getDoubleExtra("lon", 0)));
            etDescription.setText(getIntent().getStringExtra("description"));
            bSubmit.setText("Ενημέρωση");
            bSubmit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Map place = new HashMap<String, String >();
                    place.put("id", String.valueOf(getPlaceId()));
                    place.put("name", etName.getText().toString());
                    place.put("area", etArea.getText().toString());
                    place.put("icon", etIconUrl.getText().toString());
                    place.put("latitude", etLat.getText().toString());
                    place.put("longitude", etLon.getText().toString());
                    place.put("description", etDescription.getText().toString());

                    api.updatePlace(place);

                    Intent i = new Intent(EditPlaceActivity.this, PlaceListActivity.class);

                    /* set place type if is not set */
                    if (getPlaceType() != null) {
                        i.putExtra("type", getPlaceType());
                    } else{
                        i.putExtra("type", "place");
                    }
                    startActivity(i);


                }
            });
        }
        //else is in create new place mode.
        else {
            //new place text initialize
            tvEditPlaceHeader.setText("Εισαγωγή νέας τοποθεσίας");
            bSubmit.setText("Καταχώρηση");

            //submit button function setup
            bSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //new place hashmap init with data from android editexts
                    Map place = new HashMap<String, String >();
                    place.put("name", etName.getText().toString());
                    place.put("area", etArea.getText().toString());
                    place.put("icon", etIconUrl.getText().toString());
                    place.put("latitude", etLat.getText().toString());
                    place.put("longitude", etLon.getText().toString());
                    place.put("description", etDescription.getText().toString());

                    //try to create the place - send the hashmap to api controller
                    api.createPlace(place);
                    //navigate to place list activity
                    Intent i = new Intent(EditPlaceActivity.this, PlaceListActivity.class);

                    /* set place type if is not set */
                    if (getPlaceType() != null) {
                        i.putExtra("type", getPlaceType());
                    } else{
                        i.putExtra("type", "place");
                    }
                    startActivity(i);
                }
            });
        }

    }

    private void initializeViews(){

        tvEditPlaceHeader = (TextView) findViewById(R.id.tvEditPlaceHeader);
        etName = (EditText) findViewById(R.id.etPlaceName);
        etArea = (EditText) findViewById(R.id.etPlaceArea);
        etIconUrl = (EditText) findViewById(R.id.etPlaceIconUrl);
        etLat = (EditText) findViewById(R.id.etPlaceLat);
        etLon = (EditText) findViewById(R.id.etPlaceLon);
        etDescription = (EditText) findViewById(R.id.etPlaceDescription);
        bSubmit = (Button) findViewById(R.id.bSubmit);

    }

    /**
     *
     * @return true if a key is passed in extras that flags edit mode
     */
    private boolean isEditMode(){
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if (extras.containsKey("mode")) {
                if (extras.getString("mode").equals("edit"))
                    return true;
            }
        }
        return false;
    }

    public void setPlaceId(int id){
        this.placeId = id;
    }

    public int getPlaceId(){
        return this.placeId;
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
}
