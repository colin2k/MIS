package de.fh_aachen.mis.mis_project.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by batman on 5/25/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_HAS_REMINDER = "has_reminder";
    public static final String COLUMN_DATETIME = "remind_date";
    public static final String COLUMN_MAIL = "remind_mail";
    public static final String COLUMN_LOCATION_LAT = "remind_location_lat";
    public static final String COLUMN_LOCATION_LNG = "remind_location_lng";
    public static final String COLUMN_PRIORITY = "remind_priority";

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 6;
    private static SQLiteDatabase mDb;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NOTES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NOTE
            + " text not null,"
            + COLUMN_HAS_REMINDER + " integer NOT NULL,"
            + COLUMN_MAIL + " text NOT NULL,"
            + COLUMN_LOCATION_LAT + " double NOT NULL,"
            + COLUMN_LOCATION_LNG + " double NOT NULL,"
            + COLUMN_PRIORITY + " integer NOT NULL,"
            + COLUMN_DATETIME + " text NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        mDb = database;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    public Cursor searchNote(String input) throws SQLException {
        String query = "SELECT docid as _id," + COLUMN_NOTE + " from " + TABLE_NOTES + " where " + COLUMN_NOTE + " MATCH '" + input + "';";

        Cursor mCursor = mDb.rawQuery(query, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

}