package de.fh_aachen.mis.mis_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        int PLACE_PICKER_REQUEST = 1;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.story_1){
            Intent intent = new Intent(this, PlaceNoteActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.story_6){
            Intent intent = new Intent(this, PrioActivity.class);
            startActivity(intent);
            return true;
        }

        // personen standorte
        if (id == R.id.story_10) {
            Intent intent = new Intent(this, PersonenstandorteActivity.class);
            startActivity(intent);
            return true;
        }

        // abhaengige erinnerungen
        if (id == R.id.story_12) {
            Intent intent = new Intent(this, AbhaengigeErinnerungenActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
