package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class PetsContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "pets";

    public static final String ALL_TABLES_JOIN = PetsContract.PetsTable.TABLE_NAME + " JOIN " +
            PetsContract.BreedTable.TABLE_NAME + " ON " +
            PetsContract.PetsTable.TABLE_NAME + "." + PetsContract.PetsTable.COLUMN_PET_BREED + " = " +
            PetsContract.BreedTable.TABLE_NAME + "." + PetsContract.BreedTable.BREED_ID + " JOIN " +
            PetsContract.GenderTable.TABLE_NAME + " ON " +
            PetsContract.PetsTable.TABLE_NAME + "." + PetsContract.PetsTable.COLUMN_PET_GENDER + " = " +
            PetsContract.GenderTable.TABLE_NAME + "." + PetsContract.GenderTable.GENDER_ID + " ";

    private PetsContract() {
    }

    public static final class PetsTable implements BaseColumns {

        public static final Uri PETS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        //The MIME type of the {@link #PETS_CONTENT_URI} for a list of pets.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        //The MIME type of the {@link #PETS_CONTENT_URI} for a single pet.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        public static final String TABLE_NAME = "pets";

        public static final String PET_ID = BaseColumns._ID;
        public static final String COLUMN_PET_NAME = "pet_name";
        public static final String COLUMN_PET_BREED = "breed_id";
        public static final String COLUMN_PET_GENDER = "gender_id";
        public static final String COLUMN_PET_WEIGHT = "weight";
    }

    public static final class GenderTable implements BaseColumns {

        public static final String TABLE_NAME = "gender";

        public static final String GENDER_ID = BaseColumns._ID;
        public static final String COLUMN_GENDER_NAME = "gender_name";

        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }
    }

    public static final class BreedTable implements BaseColumns {

        public static final String TABLE_NAME = "breed";

        public static final String BREED_ID = BaseColumns._ID;
        public static final String COLUMN_BREED_NAME = "breed_name";
    }
}
