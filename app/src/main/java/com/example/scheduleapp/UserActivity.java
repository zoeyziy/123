package com.example.scheduleapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserActivity extends AppCompatActivity {
    TextView vehicleText;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId, userEmail;

    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mFirestoreList = findViewById(R.id.firestore_list);

        vehicleText = findViewById(R.id.vehicleView);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        FirebaseUser user = fAuth.getCurrentUser();
        if(user != null){
            userEmail = user.getEmail();
        }

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        editor = preferences.edit();
        if (!preferences.contains("vehicle")) {
            getVin(userEmail);
        } else {
            vehicleText.setText(preferences.getString("vehicle", ""));
        }

        //Query
        Query appointmentQuery = fStore.collection("appointments").whereEqualTo("user", userEmail);
        appointmentQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d("UserActivity", "OnSuccess: Query Fetched for User: " + userEmail);

            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UserActivity", "OnFailure: " + e.toString());
                    }
        });

        //RecyclerOptions
        FirestoreRecyclerOptions<AppointmentModel> options = new FirestoreRecyclerOptions.Builder<AppointmentModel>()
                .setQuery(appointmentQuery, AppointmentModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<AppointmentModel, AppointmentsViewHolder>(options) {
            @NonNull
            @Override
            public AppointmentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
                return new AppointmentsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AppointmentsViewHolder holder, int position, @NonNull AppointmentModel model) {
                holder.list_company.setText(model.getCompany());
                holder.list_description.setText(model.getDescription());
                holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteItem(holder.getAdapterPosition());
                    }
                });
            }

            public void deleteItem(int position) {
                getSnapshots().getSnapshot(position).getReference().delete();
                notifyDataSetChanged();
            }
        };




        //View Holder
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);
    }

    private void getVin(String userEmail) {
        DocumentReference docRef = fStore.collection("users").document(userEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snap = task.getResult();
                    getCar((String) task.getResult().get("vin"));
                }
            }
        });
    }

    private void getCar(String vin) {
        DocumentReference docRef = fStore.collection("vehicles").document(vin);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snap = task.getResult();
                    String make = (String) snap.get("make");
                    String model = (String) snap.get("model");
                    String year = (String) snap.get("year");
                    StringBuilder builder = new StringBuilder(year)
                            .append(" ")
                            .append(make)
                            .append(" ")
                            .append(model);
                    editor.putString("vehicle", builder.toString());
                    editor.commit();
                    vehicleText.setText(builder.toString());
                }
            }
        });
    }

    public void deleteVin(View v){
        Map<String, Object> note = new HashMap<>();
        note.put("vin", FieldValue.delete());

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.update("vin", FieldValue.delete());
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void scheduleAppt(View v) {
        Intent mapIntent = new Intent(this, MapsActivity.class);
        startActivityForResult(mapIntent, 0);
    }

    public void goToMaps(View view) {
        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
    }

    private class AppointmentsViewHolder extends RecyclerView.ViewHolder {
        private TextView list_company;
        private TextView list_description;
        private Button cancelBtn;

        public AppointmentsViewHolder(@NonNull View itemView) {
            super(itemView);

            list_company = itemView.findViewById(R.id.list_company);
            list_description = itemView.findViewById(R.id.list_description);
            cancelBtn = itemView.findViewById(R.id.btn_cancel);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}