package com.example.entraideettoi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class ModifRequestActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private FirebaseFirestore db;
    private Spinner spinner;
    private String idActivity;
    private String description;
    private String typeProblem;
    private String title;
    private static final String[] paths = {"Courses", "Démarche administrative", "Soutiens scolaire","Accompagnement en voiture", "Demenagement","Autre"};

    Button backPage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(FirebaseAuth.getInstance().getCurrentUser() == null || UserAccess.getId() == null || UserAccess.getFirstName() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        setContentView(R.layout.page_modif_request);
        Intent intent = getIntent();
        this.idActivity = intent.getStringExtra("IDREQUEST");
        this.description = intent.getStringExtra("DESCRIPTION");
        this.typeProblem = intent.getStringExtra("TypeProblem");
        this.title = intent.getStringExtra("TITLE");
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        backPage = findViewById(R.id.backButton);
        EditText edt = findViewById(R.id.editTextTextMultiLine);
        edt.setText(this.description);

        edt = findViewById(R.id.editTextTextPersonName);
        edt.setText(this.title);

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ModifRequestActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        if(selected(this.typeProblem) > -1){
            this.spinner.setSelection(selected(this.typeProblem));
        }
        this. db = FirebaseFirestore.getInstance();

        //  Button for back page
        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                onBackPressed();
            }
        });

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.typeProblem = parent.getItemAtPosition(position).toString();
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

            Map<String, Object> users = new HashMap<>();
            users.put("Title", titre);
            users.put("Description", description);
            users.put("TypeProblem", typeProblem);


            ModifRequestActivity.this.db.collection("Requests").document(idActivity).set(users, SetOptions.merge());
            startActivity(new Intent(getApplicationContext(), MyRequestActivity.class));
        }

    }

    public int selected(String s){
        for(int cpt = 0 ; cpt < paths.length ;cpt++){
            if(s.equals(paths[cpt])){
                return cpt;
            }
        }
        return -1;
    }
}
