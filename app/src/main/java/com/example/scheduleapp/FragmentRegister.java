package com.example.scheduleapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class FragmentRegister extends Fragment {
    public static final String TAG = "TAG";
    Button btLogin, btRegister;
    EditText etUserName, etPassword, etEmail, etPhone;
    String userName, email, pass, phone;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseFirestore fStore;
    String userId;

    FirebaseAuth fAuth;

    @Override
    public void onAttach(Context context) {
        sharedPreferences = context.getSharedPreferences("usersFile", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);

        etUserName = view.findViewById(R.id.etUserName);
        etPassword = view.findViewById(R.id.etPassword);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        btRegister = view.findViewById(R.id.btRegister);

        if (fAuth.getCurrentUser() != null) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), UserActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        }

        btRegister.setOnClickListener(v -> {
            userName = etUserName.getText().toString();
            email = etEmail.getText().toString().trim();
            pass = etPassword.getText().toString().trim();
            phone = etPhone.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is Required");
                return;
            }

            if (TextUtils.isEmpty(pass)) {
                etPassword.setError("Password is Required");
                return;
            }

            if (pass.length() < 6) {
                etPassword.setError("Password must be >= 6");
                return;
            }

            //Register user in firebase
            fAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Registered", Toast.LENGTH_SHORT).show();
                        //userId = fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("users").document(email);
                        Map<String, Object> user = new HashMap<>();
                        user.put("email", email);
                        user.put("username", userName);
                        user.put("phone", phone);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "User Saved", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onSuccess: user profile is created for " + userId);
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), RegisterVehicleActivity.class);
                        getActivity().startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });


        return view;
    }
}
