package com.example.lab5_starter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CityDialogFragment.CityDialogListener {

    private Button addCityButton;
    private ListView cityListView;

    private ArrayList<City> cityArrayList;
    private CityArrayAdapter cityArrayAdapter;

    private FirebaseFirestore db;
    private CollectionReference citiesRef;
    private Button editListButton;
    private Button deleteButton;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set views
        addCityButton = findViewById(R.id.buttonAddCity);
        cityListView = findViewById(R.id.listviewCities);
        editListButton = findViewById(R.id.buttonEditList); // added in for my implementation of deleting with checkboxes
        deleteButton = findViewById(R.id.buttonDelete);

        // create city array
        cityArrayList = new ArrayList<>();
        cityArrayAdapter = new CityArrayAdapter(this, cityArrayList, isEditMode);
        cityListView.setAdapter(cityArrayAdapter);

        deleteButton.setVisibility(View.GONE);

//        addDummyData();

        // set listeners
        addCityButton.setOnClickListener(view -> {
            CityDialogFragment cityDialogFragment = new CityDialogFragment();
            cityDialogFragment.show(getSupportFragmentManager(), "Add City");
        });

        cityListView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (!isEditMode){
                City city = cityArrayAdapter.getItem(i);
                CityDialogFragment cityDialogFragment = CityDialogFragment.newInstance(city);
                cityDialogFragment.show(getSupportFragmentManager(), "City Details");
            }
        });

        editListButton.setOnClickListener(view -> toggleEditMode());
        deleteButton.setOnClickListener(view -> deleteCheckedItems());

        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");

        citiesRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
            }
            if (value != null && !value.isEmpty()) {
                cityArrayList.clear();
                for (QueryDocumentSnapshot snapshot : value) {
                    String name = snapshot.getString("name");
                    String province = snapshot.getString("province");

                    cityArrayList.add(new City(name, province));
                }
                cityArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        editListButton.setText(isEditMode ? "Done" : "Edit List");
        deleteButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        addCityButton.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
        
        cityArrayAdapter.setEditMode(isEditMode);
        cityArrayAdapter.notifyDataSetChanged();
    }
    
    private void deleteCheckedItems() {
        ArrayList<City> citiesToDelete = new ArrayList<>();
        for (City city : cityArrayList) {
            if (city.isChecked()) {
                citiesToDelete.add(city);
            }
        }
        
        if (citiesToDelete.isEmpty()) {
            return;
        }
        
        for (City city : citiesToDelete) {
            cityArrayList.remove(city);
            citiesRef.document(city.getName()).delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "successful delete of document with ID: " + city.getName()))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting document with ID: " + city.getName() + ", error: " + e));
        }
        toggleEditMode();
    }

    @Override
    public void updateCity(City city, String title, String year) {
        citiesRef.document(city.getName()).delete();
        
        city.setName(title);
        city.setProvince(year);
        cityArrayAdapter.notifyDataSetChanged();
        
        DocumentReference docRef = citiesRef.document(city.getName());
        docRef.set(city);

        // Updating the database using delete + addition
    }

    @Override
    public void addCity(City city){
        cityArrayList.add(city);
        cityArrayAdapter.notifyDataSetChanged();

        DocumentReference docRef = citiesRef.document(city.getName());
        docRef.set(city);
    }

//    public void addDummyData(){
//        City m1 = new City("Edmonton", "AB");
//        City m2 = new City("Vancouver", "BC");
//        cityArrayList.add(m1);
//        cityArrayList.add(m2);
//        cityArrayAdapter.notifyDataSetChanged();
//    }
}