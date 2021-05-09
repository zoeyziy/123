package com.example.scheduleapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentLogin extends Fragment {
    Button  btLogin, btRegister;
    EditText etPassword, etEmail;
    CallbackFragment callbackFragment;
    String pass, email;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    FirebaseAuth fAuth;

    @Override
    public void onAttach(Context context){
        sharedPreferences = context.getSharedPreferences("usersFile", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //fAuth = FirebaseAuth.getInstance();
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment,container, false);

        fAuth = FirebaseAuth.getInstance();
        etPassword = view.findViewById(R.id.etPassword);
        btLogin = view.findViewById(R.id.btLogin);//5:40
        btRegister = view.findViewById(R.id.btRegister);//5:40
        etEmail = view.findViewById(R.id.etEmail);

        if (sharedPreferences.contains("username")) {
            etEmail.setText(sharedPreferences.getString("username", ""));
        }

        btLogin.setOnClickListener(v -> {
            pass = etPassword.getText().toString();
            email = etEmail.getText().toString().trim();
            if(TextUtils.isEmpty(email)){
                etEmail.setError("Email is Required");
                return;
            }

            if(TextUtils.isEmpty(pass)){
                etPassword.setError("Password is Required");
                return;
            }

            if(pass.length() < 6){
                etPassword.setError("Password must be >= 6");
                return;
            }
            // authenticate user
            fAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(getContext(), "Logged in", Toast.LENGTH_SHORT).show();
                        editor.putString("username", email);
                        editor.commit();
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), UserActivity.class);
                        getActivity().startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });



        });

        btRegister.setOnClickListener(v -> {
            if(callbackFragment != null){
                callbackFragment.changeFragment();
            }
        });

        return view;
    }
    public void setCallbackFragment(CallbackFragment callbackFragment){
        this.callbackFragment = callbackFragment;
    }
}
