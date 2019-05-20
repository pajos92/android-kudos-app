package gr.uowm.icte.kudos.kudosapp;



import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import gr.uowm.icte.kudos.kudosapp.api.APIController;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                Intent i = new Intent(MainActivity.this, EditPlaceActivity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(),"Εισάγετε νέα τοποθεσία",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeButtons() {
        Button bKudosPlace = (Button) findViewById(R.id.bKudosPlace);
        Button bKudosRoute = (Button) findViewById(R.id.bKudosRoute);
        Button bKudosStatue = (Button) findViewById(R.id.bKudosStatue);

        bKudosPlace.setOnClickListener(this);
        bKudosRoute.setOnClickListener(this);
        bKudosStatue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bKudosPlace:
                Intent listviewplace = new Intent(MainActivity.this,
                        PlaceListActivity.class);
                listviewplace.putExtra("type", "place");
                startActivity(listviewplace);
                break;

            case R.id.bKudosRoute:
                Intent listviewroute = new Intent(MainActivity.this,
                        PlaceListActivity.class);
                listviewroute.putExtra("type", "route");
                startActivity(listviewroute);
                break;
            case R.id.bKudosStatue:
                Intent listviewstatue = new Intent (MainActivity.this,
                        PlaceListActivity.class);
                listviewstatue.putExtra("type", "statue");
                startActivity(listviewstatue);
            default:
                break;

        }

    }

}
