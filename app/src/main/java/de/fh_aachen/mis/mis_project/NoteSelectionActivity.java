package de.fh_aachen.mis.mis_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.fh_aachen.mis.mis_project.database.NoteDataSource;
import de.fh_aachen.mis.mis_project.model.Note;


public class NoteSelectionActivity extends Activity {

    private Button edit_btn;
    private ListView note_list;
    private ArrayAdapter<Note> note_list_adapter;
    private NoteDataSource datasource;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_selection);

        context = this;

        note_list = (ListView) findViewById(R.id.note_list);
        registerForContextMenu(note_list);

        edit_btn = (Button) findViewById(R.id.btn_edit_multiple);

        final String reminder_date = getIntent().getStringExtra("reminder_date");

        datasource = new NoteDataSource(this);
        datasource.open();
        List<Note> values = datasource.getAllNotes();
        setTitle("Multi Reminder");
        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        note_list_adapter = new ArrayAdapter<Note>(this,
                android.R.layout.simple_list_item_multiple_choice, values);
        Log.v("notes:", ""+note_list_adapter.getCount() +" datasets loaded.");
        note_list.setAdapter(note_list_adapter);
        note_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = note_list.getCheckedItemPositions();
                Note n = null;
                for (int i = 0; i < checked.size(); i++) {
                    int position = checked.keyAt(i);
                    if (checked.valueAt(i)) {
                        n = note_list_adapter.getItem(position);
                        n.setDatetimeStr(reminder_date);
                        n.setHasReminder(true);
                        datasource.updateNote(n);
                     //   selectedItems.add(note_list_adapter.getItem(position));
                    }
                }
                Toast.makeText(context, "Note(s) edited", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, null);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_selection, menu);
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

}
