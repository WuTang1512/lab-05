package com.example.lab5_starter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CityArrayAdapter extends ArrayAdapter<City> {
    private ArrayList<City> cities;
    private Context context;
    private boolean isEditMode; // for tracking if we are in edit mode or not


    public CityArrayAdapter(Context context, ArrayList<City> cities, boolean isEditMode){
        super(context, 0, cities);
        this.cities = cities;
        this.context = context;
        this.isEditMode = isEditMode;
    }

    public void setEditMode(boolean isEditMode){
        this.isEditMode = isEditMode;

        if (!isEditMode) {
            for (City city : cities) {
                city.setChecked(false);
            }
        }
    }


    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.layout_city, parent, false);
        }

        City city = cities.get(position);
        TextView movieName = view.findViewById(R.id.textCityName);
        TextView movieYear = view.findViewById(R.id.textCityProvince);
        movieName.setText(city.getName());
        movieYear.setText(city.getProvince());

        CheckBox deleteCheckbox = view.findViewById(R.id.checkboxDelete);

        if (isEditMode){
            deleteCheckbox.setVisibility(View.VISIBLE);
            deleteCheckbox.setChecked(city.isChecked());

            deleteCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                city.setChecked(isChecked);
            });
        } else {
            deleteCheckbox.setVisibility(View.GONE);
            deleteCheckbox.setOnCheckedChangeListener(null);
            deleteCheckbox.setChecked(false);
        }

        return view;
    }
}
