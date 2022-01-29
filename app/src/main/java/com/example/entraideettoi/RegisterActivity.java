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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mBanner,mRegisterBtn;
    private EditText mFirstName,mLastName, mEmail, mPassword,mAddress,mCity, mPostalCode, mPseudo, mTel;
    private DatePicker mBirth;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar;
    private DatePicker birth;
    private FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_register);
        fStore = FirebaseFirestore.getInstance();

        fAuth           = FirebaseAuth.getInstance();
        mBanner         = (TextView) findViewById(R.id.appNameB);
        mFirstName       = (EditText) findViewById(R.id.rFirstName);
        mLastName      = (EditText) findViewById(R.id.rLastName);
        mEmail          = (EditText) findViewById(R.id.rEmail);
        mPassword       = (EditText) findViewById(R.id.rPassword);
        mAddress       = (EditText) findViewById(R.id.rAddress);
        mCity       = (EditText) findViewById(R.id.rCity);
        mPostalCode       = (EditText) findViewById(R.id.rPostalCode);
        mPseudo       = (EditText) findViewById(R.id.rPseudo);
        mTel          = (EditText) findViewById(R.id.rTel);
        birth = (DatePicker) findViewById(R.id.rBirth);

        mRegisterBtn    = (Button) findViewById(R.id.rRegisterBtn);
        mRegisterBtn.setOnClickListener(this);

        progressBar     = findViewById(R.id.rProgressBar);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.appNameB:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.rRegisterBtn:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String firstName = mFirstName.getText().toString().trim();
        String lastName = mFirstName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String address = mAddress.getText().toString().trim();
        String city = mCity.getText().toString().trim();
        String postalCode = mPostalCode.getText().toString().trim();
        String pseudo = mPseudo.getText().toString().trim();
        String tel= mTel.getText().toString().trim();

        Calendar current = Calendar.getInstance();
        current.set(birth.getYear(), birth.getMonth(), birth.getDayOfMonth(), 0, 0, 0);
        Date dateCreation = current.getTime();
        Timestamp uneTime = new Timestamp(dateCreation);

        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || city.isEmpty() || postalCode.isEmpty() || pseudo.isEmpty() || tel.isEmpty()){
            mFirstName.setError("All fields are Required");
            return;
        }
        if (email.isEmpty()){
            mEmail.setError("Email is Required");
            return;
        }
        if (TextUtils.isEmpty(password)){
            mPassword.setError("Password is required");
            return;
        }
        if (password.length() <6){
            mPassword.setError("Password must be greater than 6 characters");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmail.setError("Please provoide a valide Email");
            mEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(RegisterActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.VISIBLE);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Create a new user with a props
                Map<String, Object> user = new HashMap<>();
                user.put("Address", address);
                user.put("Admin", false);
                user.put("Birth", uneTime.getSeconds());
                user.put("City", city);
                user.put("Email", email);
                user.put("FirstName", firstName);
                user.put("LastName", lastName);
                user.put("PostalCode", postalCode);
                user.put("Pseudo", pseudo);
                user.put("Tel", tel);

                UserAccess.setAddress(address);
                UserAccess.setAdmin(false);

                Calendar unCalendar = Calendar.getInstance();
                unCalendar.setTimeInMillis(uneTime.getSeconds() * 1000);
                UserAccess.setBirth(unCalendar.getTime());

                UserAccess.setCity(city);
                UserAccess.setEmail(email);
                UserAccess.setFirstName(firstName);
                UserAccess.setLastName(lastName);
                UserAccess.setPostalCode(postalCode);
                UserAccess.setPseudo(pseudo);
                UserAccess.setTel(tel);
                UserAccess.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());

                Map<String, Object> userNotification = new HashMap<>();
                userNotification.put("Message", true);
                userNotification.put("Autre", true);
                userNotification.put("Courses", true);
                userNotification.put("Administratif", true);
                userNotification.put("Scolaire", true);
                userNotification.put("Demenagement", true);
                userNotification.put("Voiture", true);


                // Add a new user with same ID as the user created in Auth
                db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(user);
                db.collection("UsersNotifications").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(userNotification);
                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            }
        });
    }

    public void toLogin(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
}