package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.pets.R;

public class PetsProvider extends ContentProvider {

    private PetsDbHelper mDbHelper;

    private static final int PETS = 101;
    private static final int PET_ID = 102;

    public static final String LOG_TAG = PetsProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY, PetsContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY, PetsContract.PATH_PETS + "/#", PET_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PetsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case PETS:
                cursor = database.query(PetsContract.ALL_TABLES_JOIN, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case PET_ID:
                selection = PetsContract.PetsTable.TABLE_NAME + "." + PetsContract.PetsTable.PET_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetsContract.ALL_TABLES_JOIN, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown Uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case PETS:
                return PetsContract.PetsTable.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetsContract.PetsTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown Uri: " + uri + " with " + sUriMatcher.match(uri));
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        contentValues = validateData(contentValues);
        if (contentValues == null) {
            return null;
        }

        // The breedName that will be inserted into the Pets table will be replaced by its breedId
        long breedId = insertAndGetBreedId(contentValues);
        contentValues.put(PetsContract.PetsTable.COLUMN_PET_BREED, breedId);

        switch (sUriMatcher.match(uri)) {
            case PETS:
                long petId = database.insert(PetsContract.PetsTable.TABLE_NAME, null, contentValues);
                if (petId < 0) {
                    Log.e(LOG_TAG, "Failed to insert row for" + uri);
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, petId);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        switch (sUriMatcher.match(uri)) {
            case PETS:

                // Convert the breedName inserted by the user into a breedId
                // Because what are contained in the Pet Table are the pets' breedId, not their breedName
                if (selection.contains(PetsContract.PetsTable.COLUMN_PET_BREED)) {
                    selectionArgs = getBreedId(selectionArgs);
                }
                rowsDeleted = database.delete(PetsContract.PetsTable.TABLE_NAME, selection, selectionArgs);
                break;
            case PET_ID:
                selection = PetsContract.PetsTable.TABLE_NAME + "." + PetsContract.PetsTable.PET_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(PetsContract.PetsTable.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues contentValues,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        if (contentValues.size() < 1) {
            return 0;
        }

        contentValues = validateData(contentValues);
        if (contentValues == null) {
            return 0;
        }

        if (contentValues.containsKey(PetsContract.PetsTable.COLUMN_PET_GENDER)) {
            long breedId = insertAndGetBreedId(contentValues);
            contentValues.put(PetsContract.PetsTable.COLUMN_PET_BREED, breedId);
        }

        int rowsUpdated;
        switch (sUriMatcher.match(uri)) {
            case PETS:
                getContext().getContentResolver().notifyChange(uri, null);
                rowsUpdated = database.update(PetsContract.PetsTable.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case PET_ID:
                selection = PetsContract.PetsTable.TABLE_NAME + "." + PetsContract.PetsTable.PET_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.update(PetsContract.PetsTable.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    // Insert breedName into the Breed Table and get its breedId
    private long insertAndGetBreedId(ContentValues contentValues) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        String breed = contentValues.getAsString(PetsContract.PetsTable.COLUMN_PET_BREED);

        ContentValues cv = new ContentValues();
        cv.put(PetsContract.BreedTable.COLUMN_BREED_NAME, breed);

        long breedId = database.insert(PetsContract.BreedTable.TABLE_NAME, null, cv);

        if (breedId < 0) {
            database = mDbHelper.getReadableDatabase();
            Cursor cursor = database.query(PetsContract.BreedTable.TABLE_NAME,
                    new String[]{PetsContract.BreedTable.BREED_ID},
                    PetsContract.BreedTable.COLUMN_BREED_NAME + "=?",
                    new String[]{String.valueOf(breed)},
                    null,
                    null,
                    null);

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        breedId = cursor.getInt(0);
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        return breedId;
    }

    // get the breedId from the Breed Table
    private String[] getBreedId(String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        String[] projection = {PetsContract.BreedTable._ID};
        String selection = PetsContract.BreedTable.COLUMN_BREED_NAME;
        String[] breedId = new String[selectionArgs.length];
        for (int i = 0; i < selectionArgs.length; i++) {
            Cursor cursor = database.query(PetsContract.BreedTable.TABLE_NAME, projection, selection,
                    new String[]{selectionArgs[i]}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        breedId[i] = cursor.getString(0);
                    }
                } finally {
                    cursor.close();
                }
            } else {
                //0 is the "Unknown breed" breedId
                breedId[i] = String.valueOf(0);
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return breedId;
    }

    //Validate the data typed by the user
    private ContentValues validateData(ContentValues contentValues) {

        if (contentValues.containsKey(PetsContract.PetsTable.COLUMN_PET_NAME)) {
            String petName = contentValues.getAsString(PetsContract.PetsTable.COLUMN_PET_NAME);
            if (petName == null || petName.length() < 1) {
                Log.e(LOG_TAG, "Pet requires a valid name");
                return null;
            }
        }

        if (contentValues.containsKey(PetsContract.PetsTable.COLUMN_PET_GENDER)) {
            Integer gender = contentValues.getAsInteger(PetsContract.PetsTable.COLUMN_PET_GENDER);
            if (gender == null || !PetsContract.GenderTable.isValidGender(gender)) {
                Log.e(LOG_TAG, "Pet requires a valid gender");
                return null;
            }
        }


        if (contentValues.containsKey(PetsContract.PetsTable.COLUMN_PET_WEIGHT)) {
            Integer weight = contentValues.getAsInteger(PetsContract.PetsTable.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                Log.e(LOG_TAG, "Pet requires a valid weight");
                return null;
            }
        }

        // Null breed will be replace by the word "Unknown breed"
        if (contentValues.containsKey(PetsContract.PetsTable.COLUMN_PET_BREED)) {
            String breed = contentValues.getAsString(PetsContract.PetsTable.COLUMN_PET_BREED);
            if (breed == null || breed.length() < 1) {
                breed = getContext().getString(R.string.breed_unknown);
                contentValues.put(PetsContract.PetsTable.COLUMN_PET_BREED, breed);
            }
        }

        return contentValues;
    }
}
