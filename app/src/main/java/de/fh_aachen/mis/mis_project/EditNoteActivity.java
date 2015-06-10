package de.fh_aachen.mis.mis_project;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.fh_aachen.mis.mis_project.database.NoteDataSource;
import de.fh_aachen.mis.mis_project.model.Note;
import de.fh_aachen.mis.mis_project.receiver.AlarmReceiver;


public class EditNoteActivity extends Activity {

    String email;
    private NoteDataSource datasource;

    Note note;
    Button save_btn;
    Switch remind_me_switch;
    Switch multi_remind_me_switch;
    EditText textarea;
    Context context;
    String remind_me_datetime;
    TextView txtLocation;
    Button btnPlacePicker;
    Button btnPhoto;

    private LatLng location;
    private Spinner prioSpinner;
    private EditText reminder_email;

    private DatePickerDialog remind_me_date_picker;
    private TimePickerDialog remind_me_time_picker;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;

    private ImageView mImageView;

    public static int REQUEST_CODE = 811512;

    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = new File (
                    Environment.getExternalStorageDirectory()
                            + "dcim"
                            + getAlbumName()
            );

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        setTitle("Edit Note");

        datasource = new NoteDataSource(this);
        datasource.open();


        Intent intent = getIntent();
        final String note_id = intent.getStringExtra("note_id");
        note = datasource.getNoteById(Long.parseLong(note_id, 10));
        location = new LatLng(note.getLocationLat(), note.getLocationLng()); // FH-Aachen

        context = this;


        Log.v("note edit loaded: ", note.toString());
        remind_me_datetime = note.getDatetimeStr();

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        timeFormatter = new SimpleDateFormat("H:m:s", Locale.US);

        save_btn = (Button) findViewById(R.id.note_save_button);
        remind_me_switch = (Switch) findViewById(R.id.note_remind_me_switch);
        multi_remind_me_switch = (Switch) findViewById(R.id.edit_multiple_reminders);
        textarea = (EditText) findViewById(R.id.note_data);

        reminder_email = (EditText) findViewById(R.id.note_email);
        prioSpinner = (Spinner) findViewById(R.id.spinnerPrio);
        txtLocation = (TextView) this.findViewById(R.id.txtPickedPlace);

        txtLocation.setText(location.toString());

        multi_remind_me_switch.setVisibility(View.INVISIBLE);
        textarea.setText(note.getNoteText());
        remind_me_switch.setChecked(note.getHasReminder());
        if(note.getHasReminder()){
            multi_remind_me_switch.setVisibility(View.VISIBLE);
        }
        btnPlacePicker = (Button) findViewById(R.id.btnPlace);
        btnPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("location", location);
                startActivityForResult(intent, 2);
            }
        });

        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("PhotoBtn", "Clicked");
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create an image file name
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
                    File albumF = getAlbumDir();
                    try {
                        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageF));
                    } catch (IOException e) {
                        Log.e("PhotoBtn", e.getMessage());
                    }
                    startActivityForResult(takePictureIntent, 666);
                }
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("SaveBtn", "Clicked");
                String text = textarea.getText().toString();
                boolean has_reminder = remind_me_switch.isChecked();
                boolean has_multi_reminder = multi_remind_me_switch.isChecked();

                email = reminder_email.getText().toString();
                note.setDatetimeStr(remind_me_datetime);
                note.setHasReminder(has_reminder);
                note.setNoteText(text);
                note.setReminder_email(email);
                note.setLocationLat(location.latitude);
                note.setLocationLng(location.longitude);
                note.setPriority(prioSpinner.getSelectedItemPosition());

                datasource.updateNote(note);

                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                alarmIntent.putExtra("note_id", note_id);
                PendingIntent sender = PendingIntent.getBroadcast(context, EditNoteActivity.REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Build a calendar instance to get the milliseconds of note.getDate
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy H:mm:ss");
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

                if(multi_remind_me_switch.isChecked()) {
                    Intent intent = new Intent(context, NoteSelectionActivity.class);
                    intent.putExtra("reminder_date", remind_me_datetime);
                    intent.putExtra("reminder_email", note.getReminder_email());
                    startActivityForResult(intent, 1);
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
                            multi_remind_me_switch.setVisibility(View.VISIBLE);
                        }
                    },newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE), true);

                    remind_me_date_picker =  new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

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
                    multi_remind_me_switch.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
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
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        mImageView.setImageBitmap(bitmap);
        mImageView.setVisibility(View.VISIBLE);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1: {
                location = (LatLng) data.getExtras().get("location");
                txtLocation.setText(location.toString());
                if(resultCode==RESULT_OK){
                    /*Intent refresh = new Intent(this, AbhaengigeErinnerungenActivity.class);
                    startActivity(refresh);
                    this.finish();*/
                }
            }
            case 666: {
                if (resultCode == RESULT_OK) {
                    if (mCurrentPhotoPath != null) {
                        setPic();
                        galleryAddPic();
                        mCurrentPhotoPath = null;
                    }
                }
            }
        }
    }
}
