package com.example.scheduleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.scheduleapp.models.Appointment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class FirestoreCrud extends AppCompatActivity {

    private static final String TAG = FirestoreCrud.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firestore_crud);
    }

    public void firestoreCreate() {
        GeoPoint loc = new GeoPoint(39.9887537, -83.0362866);
        Timestamp time = Timestamp.now();
        Appointment appt = new Appointment("AutoZone",
                "Tire rotation.",
                loc,
                "Always come here for tires.",
                time,
                "davis.5778@osu.edu",
                "VIN123");


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("appointments")
                .add(appt)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Successfully added document to Firestore.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to add document to Firestore.");
                    }
                });
    }
}