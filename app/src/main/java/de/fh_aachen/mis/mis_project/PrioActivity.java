package de.fh_aachen.mis.mis_project;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.*;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

public class PrioActivity extends Activity implements OnItemSelectedListener{

    private Spinner prioSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prio);


        Date date = new Date();
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

        prioSpinner = (Spinner) findViewById(R.id.spinnerPrio);
        prioSpinner.setOnItemSelectedListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prio, menu);
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

    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

        String str = parent.getItemAtPosition(pos).toString();
        Calendar calendar = Calendar.getInstance();

        switch(str){
            case "sehr wichtig":
                calendar.add(Calendar.DAY_OF_MONTH,2);
                break;
            case "wichtig":
                calendar.add(Calendar.DAY_OF_MONTH,4);
                break;
            case "normal":
                calendar.add(Calendar.DAY_OF_MONTH,10);
                break;
            case "bei Gelegenheit":
                calendar.add(Calendar.MONTH,1);
                break;
            case "unwichtig":
                calendar.add(Calendar.MONTH,3);
                break;
        }

        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        final EditText fristField = (EditText) this.findViewById(R.id.input_frist);
        fristField.setText(String.format("%02d.%02d.%04d %02d:%02d", day, month+1, year, hours, minutes));

        Toast.makeText(parent.getContext(), "Die Frist wurde neu gesetzt!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


}
