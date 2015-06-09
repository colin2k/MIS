package de.fh_aachen.mis.mis_project;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.fh_aachen.mis.mis_project.database.NoteDataSource;
import de.fh_aachen.mis.mis_project.model.Note;


public class ExportActivity extends Activity {

    private ListView note_list;
    private Button export_button;
    private ArrayAdapter<Note> note_list_adapter;
    private NoteDataSource datasource;

    final Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        setTitle("Export");

        note_list = (ListView) findViewById(R.id.note_list);
        export_button = (Button) findViewById(R.id.export_button);
        registerForContextMenu(note_list);

        datasource = new NoteDataSource(this);
        datasource.open();
        List<Note> values = datasource.getAllNotes();

        note_list_adapter = new ArrayAdapter<Note>(this,
                android.R.layout.simple_list_item_multiple_choice, values);
        Log.v("notes:", "" + note_list_adapter.getCount() + " datasets loaded.");
        note_list.setAdapter(note_list_adapter);
        note_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        export_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    export();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(context, "Notes Exported.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, null);
                finish();
            }
        });

    }

    protected void export() throws IOException {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/MIS-Export");
        dir.mkdirs();
        File file = new File(dir, "note_export.xml");

        FileOutputStream f = new FileOutputStream(file);
        f.write("<MISNoteExport>\n".getBytes());

        SparseBooleanArray checked = note_list.getCheckedItemPositions();
        Note n = null;
        for (int i = 0; i < checked.size(); i++) {

            int position = checked.keyAt(i);
            if (checked.valueAt(i)) {
                n = note_list_adapter.getItem(position);
                f.write("  <Note>\n".getBytes());

                String export_string =
                        "    <text>" + n.getNoteText() + "</text>\n"
                        + "    <priority>" + n.getPriority() + "</priority>\n"
                        + "    <lat>" + n.getLocationLat() + "</lat>\n"
                        + "    <lng>" + n.getLocationLng() + "</lng>\n";

                if(n.getHasReminder()) {
                    export_string += "    <remind_date>" + n.getDatetimeStr() + "</remind_date>\n";

                    if(!n.getReminder_email().isEmpty()){
                        export_string += "    <remind_email>" + n.getReminder_email() + "</remind_email>\n";
                    }
                }

                f.write(export_string.getBytes());
                f.write("  </Note>\n".getBytes());
            }
        }

        f.write("</MISNoteExport>\n".getBytes());
        f.flush();
        f.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_export, menu);
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
