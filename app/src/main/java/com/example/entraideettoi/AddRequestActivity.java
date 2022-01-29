package com.example.entraideettoi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AddRequestActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private FirebaseFirestore db;
    private Spinner spinner;
    private String typeService;
    private static final String[] paths = {"Courses", "Démarche administrative", "Soutiens scolaire","Accompagnement en voiture", "Déménagement","Autre"};
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_add_request);
        if(FirebaseAuth.getInstance().getCurrentUser() == null || UserAccess.getId() == null || UserAccess.getFirstName() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        spinner = (Spinner)findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddRequestActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        this. db = FirebaseFirestore.getInstance();

        // Initialize And Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.nav_add_request);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_add_request:
                        return true;
                    case R.id.nav_message:
                        startActivity(new Intent(getApplicationContext(), MessageHubActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_my_request:
                        startActivity(new Intent(getApplicationContext(), MyRequestActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_setting:
                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.typeService = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void envoyer(View view) {

        EditText edtTitre = (EditText) findViewById(R.id.editTextTextPersonName);
        EditText edtDescription = (EditText) findViewById(R.id.editTextTextMultiLine);

        String titre = edtTitre.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        boolean acces = true;
        if(TextUtils.isEmpty(titre)){
            edtTitre.setError("titre est requis.");
            acces = false;
        }

        if(TextUtils.isEmpty(description)){
            edtDescription.setError("titre est requis.");
            acces = false;
        }

        if(acces){

            Calendar current = Calendar.getInstance();
            Date dateCreation = current.getTime();
            Map<String, Object> users = new HashMap<>();
            users.put("Title", titre);
            users.put("Description", description);
            users.put("TypeProblem", typeService);
            users.put("CreationDate", dateCreation);
            users.put("UserID",UserAccess.getId());
            users.put("IsReport", false);


            AddRequestActivity.this.db.collection("Requests").document().set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Log.d(TAG, "DocumentSnapshot successfully written!");
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Log.w(TAG, "Error writing document", e);
                }
            });
        }

    }
}
