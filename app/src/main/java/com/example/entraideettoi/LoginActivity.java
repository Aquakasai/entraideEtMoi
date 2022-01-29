package com.example.entraideettoi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText loginEmail, loginPassword;
    private Button singIn;
    private FirebaseFirestore fStore;

    private FirebaseAuth mAuth;
    private ProgressBar loginProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fStore = FirebaseFirestore.getInstance();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            System.out.println("Condition valide : " + FirebaseAuth.getInstance().getCurrentUser().getUid());
            DocumentReference noteRef = fStore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

            noteRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                            String uneVar;
                            Long unLong;
                            Calendar unCalendar = Calendar.getInstance();
                            Date uneDate;

                            /// Element to take in database in Timestamp
                            uneVar = document.getData().get("Birth").toString();

                            /// Remove all useless information for convert to "Long"
                            uneVar = uneVar.replace("Timestamp(seconds=","");
                            uneVar = uneVar.replace(", nanoseconds=0)","");
                            unLong = Long.parseLong(uneVar);
                            unLong = unLong * 1000;
                            unCalendar.setTimeInMillis(unLong);
                            uneDate = unCalendar.getTime();


                            UserAccess.setFirstName(document.getData().get("FirstName").toString());
                            UserAccess.setLastName(document.getData().get("LastName").toString());
                            UserAccess.setCity(document.getData().get("City").toString());
                            UserAccess.setEmail(document.getData().get("Email").toString());
                            UserAccess.setPostalCode(document.getData().get("PostalCode").toString());
                            UserAccess.setPseudo(document.getData().get("Pseudo").toString());
                            UserAccess.setAddress(document.getData().get("Address").toString());
                            UserAccess.setTel(document.getData().get("Tel").toString());
                            UserAccess.setBirth(uneDate);
                            if(document.getData().get("Admin").toString() == "true"){
                                UserAccess.setAdmin(true);
                            }
                            else{
                                UserAccess.setAdmin(false);
                            }


                            UserAccess.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });

        }
        setContentView(R.layout.page_login);




        singIn = (Button) findViewById(R.id.loginBtn);
        singIn.setOnClickListener(this);

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);

        mAuth  = FirebaseAuth.getInstance();


        loginProgressBar =(ProgressBar) findViewById(R.id.loginProgressBar);
    }




    private void userLogin() {
        String logEmail = loginEmail.getText().toString().trim();
        String logpassword = loginPassword.getText().toString().trim();


        if (logEmail.isEmpty()){
            loginEmail.setError("Email is Required");
            return;
        }

        if (TextUtils.isEmpty(logpassword)){
            loginPassword.setError("Password is required");
            return;
        }
        if (logpassword.length() <6){
            loginPassword.setError("Password must be greater than 6 characters");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(logEmail).matches()){
            loginPassword.setError("Please provoide a valide Email");
            loginPassword.requestFocus();
            return;
        }
        loginProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(logEmail,logpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    System.out.println("ITS MY IDDDDDDDDDDDDDDDDD : " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                    FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DocumentReference noteRef = fStore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    noteRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                                    String uneVar;
                                    Long unLong;
                                    Calendar unCalendar = Calendar.getInstance();
                                    Date uneDate;

                                    /// Element to take in database in Timestamp
                                    uneVar = document.getData().get("Birth").toString();

                                    /// Remove all useless information for convert to "Long"
                                    uneVar = uneVar.replace("Timestamp(seconds=","");
                                    uneVar = uneVar.replace(", nanoseconds=0)","");
                                    unLong = Long.parseLong(uneVar);
                                    unLong = unLong * 1000;
                                    unCalendar.setTimeInMillis(unLong);
                                    uneDate = unCalendar.getTime();


                                    UserAccess.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    UserAccess.setFirstName(document.getData().get("FirstName").toString());
                                    UserAccess.setLastName(document.getData().get("LastName").toString());
                                    UserAccess.setCity(document.getData().get("City").toString());
                                    UserAccess.setEmail(document.getData().get("Email").toString());
                                    UserAccess.setPostalCode(document.getData().get("PostalCode").toString());
                                    UserAccess.setPseudo(document.getData().get("Pseudo").toString());
                                    UserAccess.setAddress(document.getData().get("Address").toString());
                                    UserAccess.setTel(document.getData().get("Tel").toString());
                                    UserAccess.setBirth(uneDate);
                                    if(document.getData().get("Admin").toString() == "true"){
                                        UserAccess.setAdmin(true);
                                    }
                                    else{
                                        UserAccess.setAdmin(false);
                                    }

                                    // open gallery
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));


                                } else {
                                    Log.d("TAG", "No such document");
                                }
                            } else {
                                Log.d("TAG", "get failed with ", task.getException());
                            }
                        }
                    });





                }else {
                    Toast.makeText(LoginActivity.this,"Failed to Login pleas check your Email or Password", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toRegister:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.loginBtn:
                userLogin();
                break;

        }
    }

    public void toRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }
}