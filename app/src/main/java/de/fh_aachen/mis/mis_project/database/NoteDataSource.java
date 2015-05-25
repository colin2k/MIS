package de.fh_aachen.mis.mis_project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

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
            DatabaseHelper.COLUMN_NOTE, DatabaseHelper.COLUMN_HAS_REMINDER, DatabaseHelper.COLUMN_DATETIME};

    public NoteDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Note createNote(String note, boolean has_reminder, String remind_me_datetime) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NOTE, note);
        Log.v("create note:", "has_reminder:" + has_reminder);
        values.put(DatabaseHelper.COLUMN_HAS_REMINDER, has_reminder);
        values.put(DatabaseHelper.COLUMN_DATETIME, remind_me_datetime);

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

    public Note getNoteById(long id) {
        Note note = null;

        Cursor cursor = database.query(DatabaseHelper.TABLE_NOTES,
                allColumns, DatabaseHelper.COLUMN_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            note = cursorToNote(cursor);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return note;
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getLong(0));
        note.setNoteText(cursor.getString(1));
        note.setHasReminder(cursor.getInt(2) == 1);
        note.setDatetimeStr(cursor.getString(3));
        return note;
    }
}
