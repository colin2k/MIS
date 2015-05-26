package de.fh_aachen.mis.mis_project;

import android.os.AsyncTask;
import android.util.Log;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {

    GMailSender sender = new GMailSender("mis@xenzilla.de", "eJne488c");
    String noteId;

    public SendEmailAsyncTask(String noteId) {
        this.noteId = noteId;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (BuildConfig.DEBUG) Log.v(SendEmailAsyncTask.class.getName(), "doInBackground()");
        try {
            sender.sendMail("Notification for Note #" + noteId, "Note Text", "mis@xenzilla.de", "to@gmail.com");
            return true;
        } catch (AuthenticationFailedException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "failed");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
