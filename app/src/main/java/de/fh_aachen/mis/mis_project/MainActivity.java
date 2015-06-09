package de.fh_aachen.mis.mis_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.app.SearchManager;
import android.widget.SearchView.OnQueryTextListener;

import java.util.List;

import de.fh_aachen.mis.mis_project.database.NoteDataSource;
import de.fh_aachen.mis.mis_project.model.Note;


public class MainActivity extends Activity implements OnQueryTextListener{

    private ListView note_list;
    private ArrayAdapter<Note> note_list_adapter;
    private NoteDataSource datasource;

    final Context context=this;
    Menu m;
    android.widget.Filter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Overview");
        note_list = (ListView) findViewById(R.id.note_list);
        registerForContextMenu(note_list);

        datasource = new NoteDataSource(this);
        datasource.open();
        List<Note> values = datasource.getAllNotes();

        note_list_adapter = new ArrayAdapter<Note>(this,
                android.R.layout.simple_list_item_1, values);
        Log.v("notes:", "" + note_list_adapter.getCount() + " datasets loaded.");
        note_list.setAdapter(note_list_adapter);
        note_list.setTextFilterEnabled(false);
        filter = note_list_adapter.getFilter();
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

        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);


        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        // this is your adapter that will be filtered
      /*  if (TextUtils.isEmpty(newText))
        {
            note_list.clearTextFilter();
        }
        else
        {
            note_list.setFilterText(newText.toString());
        }*/

        filter.filter(newText);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // TODO Auto-generated method stub
        return false;
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
        }else if (id == R.id.action_new_note) {
            Intent intent = new Intent(this, NewNoteActivity.class);
            startActivityForResult(intent, 1);
            //reloadAllData();

            return true;
        }else if (id == R.id.action_export) {
            Intent intent = new Intent(this, ExportActivity.class);
            startActivityForResult(intent, 1);
        return true;
    }
        /*
        else if (id == R.id.menu_search) {
            onSearchRequested();
            return true;
        }*/

        return false;
        //return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            //Intent refresh = new Intent(this, MainActivity.class);
            //startActivity(refresh);
            //Log.v("refreshing note list view","");
            this.finish();
            startActivity(getIntent());
        }
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        int PLACE_PICKER_REQUEST = 1;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.story_1){
            Intent intent = new Intent(this, PlaceNoteActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.story_6){
            Intent intent = new Intent(this, PrioActivity.class);
            startActivity(intent);
            return true;
        }

        // personen standorte
        if (id == R.id.story_10) {
            Intent intent = new Intent(this, PersonenstandorteActivity.class);
            startActivity(intent);
            return true;
        }

        // abhaengige erinnerungen
        if (id == R.id.story_12) {
            Intent intent = new Intent(this, AbhaengigeErinnerungenActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
