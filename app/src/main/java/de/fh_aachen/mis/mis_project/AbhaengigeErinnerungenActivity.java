package de.fh_aachen.mis.mis_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import de.fh_aachen.mis.mis_project.database.NoteDataSource;
import de.fh_aachen.mis.mis_project.model.Note;


public class AbhaengigeErinnerungenActivity extends Activity {

    private ListView note_list;
    private ArrayAdapter<Note> note_list_adapter;
    private NoteDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abhaengige_erinnerungen);

        note_list = (ListView) findViewById(R.id.note_list);

        datasource = new NoteDataSource(this);
        datasource.open();
        List<Note> values = datasource.getAllNotes();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        note_list_adapter = new ArrayAdapter<Note>(this,
                android.R.layout.simple_list_item_1, values);
        Log.v("notes:", ""+note_list_adapter.getCount() +" datasets loaded.");
        note_list.setAdapter(note_list_adapter);
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
        getMenuInflater().inflate(R.menu.menu_abhaengige_erinnerungen, menu);
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
            this.finish();
        }
    }
}
