package de.fh_aachen.mis.mis_project;

import android.os.Bundle;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.widget.EditText;
import android.widget.TimePicker;
import java.util.Calendar;
import android.text.format.DateFormat;


public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        final EditText timeField =  (EditText) getActivity().findViewById(R.id.input_time);
        timeField.setText(String.format("%02d:%02d", hourOfDay, minute));
    }
}
