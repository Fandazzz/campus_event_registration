package com.example.campusevent;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class RegistrationContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.campusevent.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/registrations");
    private static final int REGISTRATIONS = 100;
    private static final int REGISTRATION_ID = 101;
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, "registrations", REGISTRATIONS);
        URI_MATCHER.addURI(AUTHORITY, "registrations/#", REGISTRATION_ID);
    }

    private RegistrationDatabaseHelper databaseHelper;

    @Override
    public boolean onCreate() {
        databaseHelper = new RegistrationDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        if (URI_MATCHER.match(uri) == REGISTRATION_ID) {
            selection = "id = ?";
            selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
        } else if (URI_MATCHER.match(uri) != REGISTRATIONS) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        Cursor cursor = database.query(RegistrationDatabaseHelper.TABLE_REGISTRATIONS, projection,
                selection, selectionArgs, null, null,
                sortOrder == null ? "id DESC" : sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != REGISTRATIONS) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        long id = databaseHelper.getWritableDatabase().insertOrThrow(
                RegistrationDatabaseHelper.TABLE_REGISTRATIONS, null, values);
        Uri insertedUri = ContentUris.withAppendedId(CONTENT_URI, id);
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return insertedUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (URI_MATCHER.match(uri) == REGISTRATION_ID) {
            selection = "id = ?";
            selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
        } else if (URI_MATCHER.match(uri) != REGISTRATIONS) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int count = databaseHelper.getWritableDatabase().update(
                RegistrationDatabaseHelper.TABLE_REGISTRATIONS, values, selection, selectionArgs);
        if (count > 0) getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (URI_MATCHER.match(uri) == REGISTRATION_ID) {
            selection = "id = ?";
            selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
        } else if (URI_MATCHER.match(uri) != REGISTRATIONS) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int count = databaseHelper.getWritableDatabase().delete(
                RegistrationDatabaseHelper.TABLE_REGISTRATIONS, selection, selectionArgs);
        if (count > 0) getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        return URI_MATCHER.match(uri) == REGISTRATION_ID
                ? "vnd.android.cursor.item/vnd." + AUTHORITY + ".registration"
                : "vnd.android.cursor.dir/vnd." + AUTHORITY + ".registration";
    }
}
