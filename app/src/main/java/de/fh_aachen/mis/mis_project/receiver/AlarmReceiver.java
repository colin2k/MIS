package de.fh_aachen.mis.mis_project.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import de.fh_aachen.mis.mis_project.SendEmailAsyncTask;


public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            String noteId = bundle.getString("note_id");
            String reminder_email = bundle.getString("reminder_email");
            new SendEmailAsyncTask(noteId, reminder_email).execute();

            CharSequence message = "Note with ID " + String.valueOf(noteId);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Log.e(AlarmReceiver.class.getName(), "Alarm Receiver failed");
        }
    }
}
