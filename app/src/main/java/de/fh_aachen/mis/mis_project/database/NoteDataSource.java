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
            DatabaseHelper.COLUMN_NOTE, DatabaseHelper.COLUMN_HAS_REMINDER, DatabaseHelper.COLUMN_MAIL, DatabaseHelper.COLUMN_LOCATION_LAT, DatabaseHelper.COLUMN_LOCATION_LNG,DatabaseHelper.COLUMN_PRIORITY, DatabaseHelper.COLUMN_DATETIME};

    public NoteDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Note createNote(String note, boolean has_reminder, String mail, Double location_lat,Double location_lng,  int priority, String remind_me_datetime) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NOTE, note);
        values.put(DatabaseHelper.COLUMN_HAS_REMINDER, has_reminder);
        values.put(DatabaseHelper.COLUMN_MAIL, mail);
        values.put(DatabaseHelper.COLUMN_LOCATION_LAT, location_lat);
        values.put(DatabaseHelper.COLUMN_LOCATION_LNG, location_lng);
        values.put(DatabaseHelper.COLUMN_PRIORITY, priority);
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

   /* public Note createNote(String note, boolean has_reminder, String reminder_email, String location, int priority, String remind_me_datetime) {
        Note newNote = this.createNote(note, has_reminder, reminder_email, location, priority,remind_me_datetime);
        newNote.setReminder_email(reminder_email);

        return newNote;
    }*/

    public void deleteNote(Note note) {
        long id = note.getId();
        System.out.println("Note deleted with id: " + id);
        database.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.COLUMN_ID
                + " = " + id, null);
    }

    public void updateNote(Note note) {
        ContentValues args = new ContentValues();
        args.put("note", note.getNoteText());
        args.put("has_reminder", note.getHasReminder());

        args.put(DatabaseHelper.COLUMN_MAIL, note.getReminder_email());
        args.put(DatabaseHelper.COLUMN_LOCATION_LAT, note.getLocationLat());
        args.put(DatabaseHelper.COLUMN_LOCATION_LNG, note.getLocationLng());
        args.put(DatabaseHelper.COLUMN_PRIORITY, note.getPriority());

        args.put(DatabaseHelper.COLUMN_DATETIME, note.getDatetimeStr());
        database.update(DatabaseHelper.TABLE_NOTES, args, "_id=" + note.getId(), null);
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<Note>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NOTES,
                allColumns, null, null, null, null, null,null);

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
                allColumns, DatabaseHelper.COLUMN_ID + " = " + id, null, null, null, null,null);

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
        note.setReminder_email(cursor.getString(3));
        note.setLocationLat(cursor.getDouble(4));
        note.setLocationLng(cursor.getDouble(5));
        note.setPriority(cursor.getInt(6));
        note.setDatetimeStr(cursor.getString(7));
        return note;
    }
}
