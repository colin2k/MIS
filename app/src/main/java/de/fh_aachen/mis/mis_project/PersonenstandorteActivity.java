package de.fh_aachen.mis.mis_project;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//import de.fh_aachen.mis.mis_project.R;

import java.util.List;

import de.fh_aachen.mis.mis_project.database.NoteDataSource;
import de.fh_aachen.mis.mis_project.model.Note;


public class PersonenstandorteActivity extends Activity {

    private ListView note_list;
    private ArrayAdapter<Note> note_list_adapter;
    private NoteDataSource datasource;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personenstandorte);
        setTitle("Overview");

        note_list = (ListView) findViewById(R.id.note_list);
        registerForContextMenu(note_list);

        datasource = new NoteDataSource(this);
        datasource.open();
        List<Note> values = datasource.getAllNotes();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        note_list_adapter = new ArrayAdapter<Note>(this,
                android.R.layout.simple_list_item_1, values){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                if(position == 1) {

                    text.setTextColor(Color.RED);
                }else{
                    text.setTextColor(Color.WHITE);
                }
                return view;
            }
        };
        Log.v("notes:", "" + note_list_adapter.getCount() + " datasets loaded.");
        note_list.setAdapter(note_list_adapter);

        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.alert_dark_frame)
                .setContentTitle("Person of interest nearby.")
                .setContentText("U have a note relating a person near you!");

        Intent resultIntent = new Intent(this, PersonenstandorteActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.note_list) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.notes_modification_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Note note = note_list_adapter.getItem(info.position);
        switch(item.getItemId()) {
            case R.id.remove_note:
                // remove stuff here
                datasource.deleteNote(note);
                reloadAllData();
                Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.edit_note:
                // edit stuff here
                Intent intent = new Intent(this, EditNoteActivity.class);
                intent.putExtra("note_id", ""+note.getId());
                Log.v("the note_id should be:", ""+note.getId());
                startActivityForResult(intent, 1);
                reloadAllData();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void reloadAllData(){
        List<Note> values = datasource.getAllNotes();
        note_list_adapter.clear();
        note_list_adapter.addAll(values);
        note_list.invalidateViews();
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
        getMenuInflater().inflate(R.menu.menu_personenstandorte, menu);
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

        if (id == R.id.action_new_note) {
            Intent intent = new Intent(this, NewNoteActivity.class);
            startActivityForResult(intent, 1);
            reloadAllData();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Intent refresh = new Intent(this, AbhaengigeErinnerungenActivity.class);
            startActivity(refresh);
            //Log.v("refreshing note list view","");
            this.finish();
        }
    }
}
