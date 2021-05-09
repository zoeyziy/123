package com.example.scheduleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class RegisterVehicleActivity extends AppCompatActivity {
    private static final String TAG = "RegisterVehicleActivity";

    private static final String KEY_MAKE = "make";
    private static final String KEY_Mileage = "mileage";
    private static final String KEY_MODEL = "model";
    private static final String KEY_YEAR = "year";
    private static final String KEY_VIN = "vin";
    private EditText editTextMake;
    private EditText editTextMileage;
    private EditText editTextModel;
    private EditText editTextYear;
    private EditText editTextVin;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userId, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_vehicle);

        editTextMake = findViewById(R.id.edMake);
        editTextMileage= findViewById(R.id.edMileage);
        editTextModel = findViewById(R.id.edModel);
        editTextYear = findViewById(R.id.edYear);
        editTextVin = findViewById(R.id.edVin);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

    }
    public void saveNote(View v){
        String make = editTextMake.getText().toString();
        String mileage = editTextMileage.getText().toString();
        String model = editTextModel.getText().toString();
        String year = editTextYear.getText().toString();
        String vin = editTextVin.getText().toString();

        Map<String, Object> note = new HashMap<>();
        note.put(KEY_MAKE, make);
        note.put(KEY_Mileage, mileage);
        note.put(KEY_MODEL, model);
        note.put(KEY_YEAR, year);
        note.put(KEY_VIN, vin);

        // update VIN with userEmail
        //userId = fAuth.getCurrentUser().getUid();
        userEmail = fAuth.getCurrentUser().getEmail();
        Map<String, Object> user_vin = new HashMap<>();
        user_vin.put(KEY_VIN, vin);
        DocumentReference documentReference = fStore.collection("users").document(userEmail);
        documentReference.set(user_vin, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: user profile is created for " + userEmail);
            }
            })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "OnFailure: " + e.toString());
                    }
                });

        // add vehicle to db
        db.collection("vehicles").document(vin).set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegisterVehicleActivity.this, "Vehicle Saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), UserActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterVehicleActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });


    }
}