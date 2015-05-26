package de.fh_aachen.mis.mis_project;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PlaceNoteActivity extends Activity implements GoogleMap.OnMapClickListener{

    private Button btnPlacePicker;
    private Context context;
    private LatLng location;
    private TextView txtLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_note);


        context = this;
        Date date = new Date();
        location = new LatLng(50.7586453, 6.0851664); // FH-Aachen
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        final EditText timeField = (EditText) this.findViewById(R.id.input_time);
        timeField.setText(String.format("%02d:%02d", hours, minutes));

        final EditText dateField =  (EditText) this.findViewById(R.id.input_date);
        dateField.setText(String.format("%02d.%02d.%04d", day, month+1, year));

        txtLocation = (TextView) this.findViewById(R.id.txtPickedPlace);
        txtLocation.setText(location.toString());


        btnPlacePicker = (Button) findViewById(R.id.btnPlace);
        btnPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("location",location);
                startActivityForResult(intent, 1);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView txtLocation = (TextView) this.findViewById(R.id.txtPickedPlace);
        Bundle extras = data.getExtras();
        location = (LatLng) extras.get("location");
        txtLocation.setText(location.toString());


    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_place_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(context,"Location chosen",Toast.LENGTH_SHORT).show();

    }
}
