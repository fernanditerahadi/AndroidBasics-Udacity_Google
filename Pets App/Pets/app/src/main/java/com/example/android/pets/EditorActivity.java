/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetsContract;
import com.example.android.pets.data.PetsDbHelper;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String PETS_TABLE = PetsContract.PetsTable.TABLE_NAME;
    private static final String BREED_TABLE = PetsContract.BreedTable.TABLE_NAME;
    private static final String GENDER_TABLE = PetsContract.GenderTable.TABLE_NAME;

    private static final int PETS_LOADER = 1001;
    private static final String LOG_TAG = EditorActivity.class.getName();

    private EditText mNameEditText;
    private EditText mBreedEditText;
    private EditText mWeightEditText;
    private Spinner mGenderSpinner;
    private Uri mCurrentPetUri;

    private int mGender = 0;

    private boolean mPetHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        new PetsDbHelper(this);

        Intent receivedIntent = getIntent();
        mCurrentPetUri = receivedIntent.getData();
        if (mCurrentPetUri == null) {
            setTitle(R.string.editor_activity_title_new_pet);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.editor_activity_title_edit_pet);
            getLoaderManager().initLoader(PETS_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mPetHasChanged) {
            super.onBackPressed();
        } else {
            showUnsavedChangesDialog();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save:
                savePetData();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    showUnsavedChangesDialog();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case PETS_LOADER:
                String[] projection = {PETS_TABLE + "." + PetsContract.PetsTable.PET_ID,
                        PETS_TABLE + "." + PetsContract.PetsTable.COLUMN_PET_NAME,
                        BREED_TABLE + "." + PetsContract.BreedTable.COLUMN_BREED_NAME,
                        PETS_TABLE + "." + PetsContract.PetsTable.COLUMN_PET_GENDER,
                        PETS_TABLE + "." + PetsContract.PetsTable.COLUMN_PET_WEIGHT};
                return new CursorLoader(this, mCurrentPetUri, projection,
                        null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            String petName = cursor.getString(cursor.getColumnIndex(PetsContract.PetsTable.COLUMN_PET_NAME));
            String breedName = cursor.getString(cursor.getColumnIndex(PetsContract.BreedTable.COLUMN_BREED_NAME));
            int gender = cursor.getInt(cursor.getColumnIndex(PetsContract.PetsTable.COLUMN_PET_GENDER));
            int weight = cursor.getInt(cursor.getColumnIndex(PetsContract.PetsTable.COLUMN_PET_WEIGHT));

            mNameEditText.setText(petName);
            if (breedName.equals(getString(R.string.breed_unknown))) {
                mBreedEditText.setText("");
                mBreedEditText.setHint(getString(R.string.hint_pet_breed));
            } else {
                mBreedEditText.setText(breedName);
            }
            if (weight == 0) {
                mWeightEditText.setText("");
                mWeightEditText.setHint(getString(R.string.hint_pet_weight));
            } else {
                mWeightEditText.setText(String.valueOf(weight));
            }
            mGenderSpinner.setSelection(gender);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(PetsContract.GenderTable.GENDER_UNKNOWN);
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mGenderSpinner.setAdapter(genderSpinnerAdapter);
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetsContract.GenderTable.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetsContract.GenderTable.GENDER_FEMALE;
                    } else {
                        mGender = PetsContract.GenderTable.GENDER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0;
            }
        });
    }

    private void savePetData() {
        String petName = mNameEditText.getText().toString().trim();
        String breedName = mBreedEditText.getText().toString().trim();
        int petGender = mGender;
        int petWeight = 0;
        if (!TextUtils.isEmpty(mWeightEditText.getText().toString().trim())) {
            petWeight = Integer.parseInt(mWeightEditText.getText().toString().trim());
        }

        ContentValues cv = new ContentValues();
        cv.put(PetsContract.PetsTable.COLUMN_PET_NAME, petName);
        cv.put(PetsContract.PetsTable.COLUMN_PET_BREED, breedName);
        cv.put(PetsContract.PetsTable.COLUMN_PET_GENDER, petGender);
        cv.put(PetsContract.PetsTable.COLUMN_PET_WEIGHT, petWeight);


        if (mCurrentPetUri == null) {
            Uri newPetUri = getContentResolver().insert(PetsContract.PetsTable.PETS_CONTENT_URI, cv);
            if (newPetUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                        Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentPetUri, cv, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void deletePet() {
        int rowsDeleted = 0;
        if (mCurrentPetUri != null) {
            rowsDeleted = getContentResolver().delete(mCurrentPetUri, null, null);
        }
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.editor_delete_pet_failed), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_delete_pet_successful), Toast.LENGTH_LONG).show();
            finish();
        }

    }

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.unsaved_changes_dialog_msg);
        alertBuilder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alertBuilder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        alertBuilder.create().show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.delete_dialog_msg);
        alertBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
            }
        });
        alertBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        alertBuilder.create().show();
    }
}