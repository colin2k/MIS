package de.fh_aachen.mis.mis_project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import de.fh_aachen.mis.mis_project.model.Note;

/**
 * Created by batman on 5/25/15.
 */
public class NoteDataSource {
    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = { DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_NOTE };

    public NoteDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Note createNote(String note) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NOTE, note);
        long insertId = database.insert(DatabaseHelper.TABLE_NOTES, null,
                values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_NOTES,
                allColumns, DatabaseHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Note newNote = cursorToNote(cursor);
        cursor.close();
        return newNote;
    }

    public void deleteNote(Note note) {
        long id = note.getId();
        System.out.println("Note deleted with id: " + id);
        database.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<Note>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NOTES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = cursorToNote(cursor);
            notes.add(note);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return notes;
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getLong(0));
        note.setNoteText(cursor.getString(1));
        return note;
    }
}
