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
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.pets.adapter.PetsCursorAdapter;
import com.example.android.pets.data.PetsContract;
import com.example.android.pets.data.PetsDbHelper;

import java.util.ArrayList;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    private static final String PETS_TABLE = PetsContract.PetsTable.TABLE_NAME;
    private static final String BREED_TABLE = PetsContract.BreedTable.TABLE_NAME;
    private static final String GENDER_TABLE = PetsContract.GenderTable.TABLE_NAME;

    private static final int PETS_LOADER = 1001;
    private static final String LOG_TAG = CatalogActivity.class.getName();

    private PetsCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        new PetsDbHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mCursorAdapter = new PetsCursorAdapter(this, null);

        View emptyView = findViewById(R.id.empty_view);

        ListView petsListView = (ListView) findViewById(R.id.list_view_pet);
        petsListView.setEmptyView(emptyView);
        petsListView.setAdapter(mCursorAdapter);
        petsListView.setOnItemClickListener(this);

        if (mCursorAdapter.getCount() == 0) {
            invalidateOptionsMenu();
        }

        getLoaderManager().initLoader(PETS_LOADER, null, this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem deleteAllEntries = menu.findItem(R.id.action_delete_all_entries);
        if (mCursorAdapter.getCount() == 0) {
            deleteAllEntries.setVisible(false);
        } else {
            deleteAllEntries.setVisible(true);
        }


        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDummyPetData();
                return true;
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
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
                        GENDER_TABLE + "." + PetsContract.GenderTable.COLUMN_GENDER_NAME,
                        PETS_TABLE + "." + PetsContract.PetsTable.COLUMN_PET_WEIGHT};
                return new CursorLoader(this, PetsContract.PetsTable.PETS_CONTENT_URI, projection,
                        null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
        invalidateOptionsMenu();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Uri petUri = ContentUris.withAppendedId(PetsContract.PetsTable.PETS_CONTENT_URI, id);
        Intent editIntent = new Intent(this, EditorActivity.class);
        editIntent.setData(petUri);
        startActivity(editIntent);
    }

    public void insertDummyPetData() {
        ContentValues cv = new ContentValues();
        cv.put(PetsContract.PetsTable.COLUMN_PET_NAME, "Toto");
        cv.put(PetsContract.PetsTable.COLUMN_PET_BREED, "Labrador Retriever");
        cv.put(PetsContract.PetsTable.COLUMN_PET_GENDER, PetsContract.GenderTable.GENDER_MALE);
        cv.put(PetsContract.PetsTable.COLUMN_PET_WEIGHT, 7);
        getContentResolver().insert(PetsContract.PetsTable.PETS_CONTENT_URI, cv);
    }

    public void deleteAllPets() {
        getContentResolver().delete(PetsContract.PetsTable.PETS_CONTENT_URI, "1", null);
    }

    private void showDeleteConfirmationDialog() {
        final CharSequence[] items = {"Yes, delete all pets."};
        final ArrayList<Integer> selectedItems = new ArrayList<>();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(R.string.delete_dialog_all_pets_msg);
        alertBuilder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int indexSelected, boolean isChecked) {
                if (isChecked) {
                    selectedItems.add(indexSelected);
                } else if (selectedItems.contains(indexSelected)) {
                    selectedItems.remove(Integer.valueOf(indexSelected));
                }
            }
        });
        alertBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (selectedItems.size() > 0) {
                    deleteAllPets();
                } else {
                    dialog.dismiss();
                }
            }
        });
        alertBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        alertBuilder.create().show();
    }
}
