package com.example.campusevent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "campus_event.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_REGISTRATIONS = "registrations";

    public RegistrationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_REGISTRATIONS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, registration_number TEXT NOT NULL, "
                + "email TEXT NOT NULL, phone TEXT NOT NULL, programme TEXT NOT NULL, "
                + "event_name TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTRATIONS);
        onCreate(db);
    }

    public long insert(StudentRegistration registration) {
        return getWritableDatabase().insert(TABLE_REGISTRATIONS, null, valuesFor(registration));
    }

    public int update(StudentRegistration registration) {
        return getWritableDatabase().update(TABLE_REGISTRATIONS, valuesFor(registration),
                "id = ?", new String[]{String.valueOf(registration.id)});
    }

    public int delete(long id) {
        return getWritableDatabase().delete(TABLE_REGISTRATIONS, "id = ?",
                new String[]{String.valueOf(id)});
    }

    public List<StudentRegistration> getAll() {
        List<StudentRegistration> registrations = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(TABLE_REGISTRATIONS, null,
                null, null, null, null, "id DESC")) {
            while (cursor.moveToNext()) {
                registrations.add(new StudentRegistration(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("registration_number")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("programme")),
                        cursor.getString(cursor.getColumnIndexOrThrow("event_name"))));
            }
        }
        return registrations;
    }

    private ContentValues valuesFor(StudentRegistration registration) {
        ContentValues values = new ContentValues();
        values.put("name", registration.name);
        values.put("registration_number", registration.registrationNumber);
        values.put("email", registration.email);
        values.put("phone", registration.phone);
        values.put("programme", registration.programme);
        values.put("event_name", registration.eventName);
        return values;
    }
}
