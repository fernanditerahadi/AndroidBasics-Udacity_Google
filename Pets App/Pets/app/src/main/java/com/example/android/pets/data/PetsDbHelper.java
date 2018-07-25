package com.example.android.pets.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class PetsDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Pets.db";
    public static final int DATABASE_VERSION = 1;

    public static final String SQL_CREATE_PETS_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS " + PetsContract.PetsTable.TABLE_NAME + " " +
            "(" + PetsContract.PetsTable.PET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            " " + PetsContract.PetsTable.COLUMN_PET_NAME + " TEXT NOT NULL," +
            " " + PetsContract.PetsTable.COLUMN_PET_BREED + " INTEGER DEFAULT 0," +
            " " + PetsContract.PetsTable.COLUMN_PET_GENDER + " INTEGER NOT NULL," +
            " " + PetsContract.PetsTable.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

    public static final String SQL_CREATE_BREED_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS " + PetsContract.BreedTable.TABLE_NAME + " " +
            "(" + PetsContract.BreedTable.BREED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " " + PetsContract.BreedTable.COLUMN_BREED_NAME + " TEXT UNIQUE NOT NULL);";

    public static final String SQL_CREATE_GENDER_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS " + PetsContract.GenderTable.TABLE_NAME + " " +
            "(" + PetsContract.GenderTable.GENDER_ID + " INTEGER PRIMARY KEY, " +
            " " + PetsContract.GenderTable.COLUMN_GENDER_NAME + " TEXT UNIQUE NOT NULL);";

    public static final String SQL_DELETE_PETS_TABLE = "" +
            "DROP TABLE IF EXISTS " + PetsContract.PetsTable.TABLE_NAME + ";";

    public static final String SQL_DELETE_BREED_TABLE = "" +
            "DROP TABLE IF EXISTS " + PetsContract.BreedTable.TABLE_NAME + ";";

    public static final String SQL_DELETE_GENDER_TABLE = "" +
            "DROP TABLE IF EXISTS " + PetsContract.GenderTable.TABLE_NAME + ";";

    public PetsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_PETS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BREED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GENDER_TABLE);
        initGenderTable(sqLiteDatabase);
        initBreedTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_PETS_TABLE);
        sqLiteDatabase.execSQL(SQL_DELETE_BREED_TABLE);
        sqLiteDatabase.execSQL(SQL_DELETE_GENDER_TABLE);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    private void initBreedTable(SQLiteDatabase sqLiteDatabase) {
        ContentValues cv = new ContentValues();
        cv.put(PetsContract.BreedTable.BREED_ID, 0);
        cv.put(PetsContract.BreedTable.COLUMN_BREED_NAME, "Unknown breed");
        sqLiteDatabase.insert(PetsContract.BreedTable.TABLE_NAME, null, cv);
    }

    private void initGenderTable(SQLiteDatabase sqLiteDatabase) {
        ContentValues cv = new ContentValues();
        cv.put(PetsContract.GenderTable.GENDER_ID, PetsContract.GenderTable.GENDER_UNKNOWN);
        cv.put(PetsContract.GenderTable.COLUMN_GENDER_NAME, "Unknown");
        sqLiteDatabase.insert(PetsContract.GenderTable.TABLE_NAME, null, cv);

        cv = new ContentValues();
        cv.put(PetsContract.GenderTable.GENDER_ID, PetsContract.GenderTable.GENDER_MALE);
        cv.put(PetsContract.GenderTable.COLUMN_GENDER_NAME, "Male");
        sqLiteDatabase.insert(PetsContract.GenderTable.TABLE_NAME, null, cv);

        cv = new ContentValues();
        cv.put(PetsContract.GenderTable.GENDER_ID, PetsContract.GenderTable.GENDER_FEMALE);
        cv.put(PetsContract.GenderTable.COLUMN_GENDER_NAME, "Female");
        sqLiteDatabase.insert(PetsContract.GenderTable.TABLE_NAME, null, cv);
    }
}
