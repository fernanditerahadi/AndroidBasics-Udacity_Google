package com.example.android.pets.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.R;
import com.example.android.pets.data.PetsContract;

public class PetsCursorAdapter extends CursorAdapter {

    public PetsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_items, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvPetName = (TextView) view.findViewById(R.id.tv_pet_name);
        TextView tvBreedName = (TextView) view.findViewById(R.id.tv_breed_name);

        String petName = cursor.getString(cursor.getColumnIndex(PetsContract.PetsTable.COLUMN_PET_NAME));
        String breedName = cursor.getString(cursor.getColumnIndex(PetsContract.BreedTable.COLUMN_BREED_NAME));

        tvPetName.setText(petName);
        tvBreedName.setText(breedName);
    }
}
