package de.fh_aachen.mis.mis_project;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.fh_aachen.mis.mis_project.database.NoteDataSource;
import de.fh_aachen.mis.mis_project.model.Note;
import de.fh_aachen.mis.mis_project.receiver.AlarmReceiver;

public class NewNoteActivity extends Activity implements GoogleMap.OnMapClickListener {

    private NoteDataSource datasource;

    String email;
    String remind_me_datetime;
    private DatePickerDialog remind_me_date_picker;
    private TimePickerDialog remind_me_time_picker;

    Button save_btn;
    Switch remind_me_switch;
    EditText textarea;
    EditText reminder_email;
    Button btnPlacePicker;

    private LatLng location;
    private TextView txtLocation;
    private Spinner prioSpinner;

    Context context;

    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        remind_me_datetime ="";
        setTitle("New Note");
        datasource = new NoteDataSource(this);
        datasource.open();

        context = this;
        location = new LatLng(50.7586453, 6.0851664); // FH-Aachen

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        timeFormatter = new SimpleDateFormat("H:m:s", Locale.US);

        save_btn = (Button) findViewById(R.id.note_save_button);
        remind_me_switch = (Switch) findViewById(R.id.note_remind_me_switch);
        textarea = (EditText) findViewById(R.id.note_data);
        reminder_email = (EditText) findViewById(R.id.note_email);
        prioSpinner = (Spinner) findViewById(R.id.spinnerPrio);
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

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("SaveBtn", "Clicked");
                String text = textarea.getText().toString();
                boolean has_reminder = remind_me_switch.isChecked();
                email = reminder_email.getText().toString();
                Note note = datasource.createNote(text,has_reminder, email, location.latitude,location.longitude, prioSpinner.getSelectedItemPosition(), remind_me_datetime );

                // set up an alert if the note has a reminder
                if (has_reminder) {
                    Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                    alarmIntent.putExtra("note_id", note.getId());
                    alarmIntent.putExtra("reminderMail", note.getReminder_email());
                    PendingIntent sender = PendingIntent.getBroadcast(context, EditNoteActivity.REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // Build a calendar instance to get the milliseconds of note.getDate
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy H:mm:ss",Locale.GERMAN);
                    try {
                        Date parsedDate = formatter.parse(note.getDatetimeStr());
                        cal.setTime(parsedDate);
                    }
                    catch (ParseException e) {
                        Log.e(EditNoteActivity.class.getName(), "failed to match " + note.getDatetimeStr() + " against " + formatter.toPattern());
                    }

                    // Get the AlarmManager service
                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                    am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
                }

                setResult(RESULT_OK, null);
                finish();
            }
        });

        remind_me_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Log.v("RemindMeS", "Checked");
                    Calendar newCalendar = Calendar.getInstance();

                    remind_me_time_picker =  new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(1,1,1,hourOfDay,minute);
                            remind_me_datetime += " " + timeFormatter.format(newDate.getTime());
                            Log.v("time is: ", remind_me_datetime);
                        }
                    },newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE), true);

                    remind_me_date_picker =  new DatePickerDialog(context, new OnDateSetListener() {

                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            remind_me_datetime = dateFormatter.format(newDate.getTime());
                            Log.v("date is: ", remind_me_datetime);

                            remind_me_time_picker.show();
                        }

                    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                    remind_me_date_picker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            remind_me_switch.setChecked(false);
                        }
                    });
                    remind_me_date_picker.show();
                }
                else{
                    Log.v("RemindMeS", "Unchecked");
                }
            }
        });


    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(context, "Location chosen", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_note, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        location = (LatLng) extras.get("location");
        txtLocation.setText(location.toString());
    }
}
