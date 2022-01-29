
package com.example.entraideettoi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class EditPasswordActivity extends AppCompatActivity {

    // Temporaly user test
    User generalUser = new User();
    private FirebaseFirestore fStore;
    Button backToSetting, registerData;
    EditText password, verifPassword, oldPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_edit_password);
        if(FirebaseAuth.getInstance().getCurrentUser() == null || UserAccess.getId() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }


        fStore = FirebaseFirestore.getInstance();
        backToSetting = findViewById(R.id.BtnCancel);
        registerData = findViewById(R.id.BtnSave);
        password = findViewById(R.id.newPassword);
        oldPassword = findViewById(R.id.oldPassword);
        verifPassword = findViewById(R.id.newPasswordConfirm);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }




        backToSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                onBackPressed();
            }
        });

        registerData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().length() == 0){
                    password.setError("Mot de passe nécessaire");
                    return;
                }
                if (password.getText().toString().length() <= 5){
                    password.setError("Le mot de passe doit contenir plus de 6 caractères");
                    return;
                }


                if (!password.getText().toString().equals(verifPassword.getText().toString())){
                    Log.d("TAG", "Password data: " + password.getText().toString());
                    Log.d("TAG", "Password confirm data: " + verifPassword.getText().toString());

                    password.setError("Les 2 mots de passes doivent être identiques");
                    return;
                }

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

            }


        });



    }
}