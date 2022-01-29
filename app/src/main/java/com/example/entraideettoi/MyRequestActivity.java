package com.example.entraideettoi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.RED;

public class MyRequestActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirebaseFirestore fStore;
    private EditText schearBar;
    private static final String[] services = {"Tous","Courses", "Démarche administrative", "Soutiens scolaire","Accompagnement en voiture", "Déménagement","Autre"};
    private String searchTxt;
    private String service;
    private Spinner spinner;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(FirebaseAuth.getInstance().getCurrentUser() == null || UserAccess.getId() == null || UserAccess.getFirstName() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        setContentView(R.layout.page_my_request);
        this.service = "Tous";
        spinner = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyRequestActivity.this,
                android.R.layout.simple_spinner_item,services);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        this.fStore = FirebaseFirestore.getInstance();
        this.schearBar = findViewById(R.id.SearchTitle);
        this.searchTxt = "";
        loadDonnee();
        // Initialize And Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.nav_my_request);

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
                        startActivity(new Intent(getApplicationContext(), AddRequestActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_message:
                        startActivity(new Intent(getApplicationContext(), MessageHubActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_my_request:
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

    public void loadDonnee(){
        LinearLayout layout = findViewById(R.id.addCart);
        layout.removeAllViews();
        CollectionReference request = fStore.collection("Requests");
        System.out.println( UserAccess.getId()+" Testestestestestestestestestestestestestestestest hahahahahaha");
        //orderBy("CreationDate")
        Query result;

        if(!this.service.equals("Tous")){
            if(this.searchTxt.length() == 0){
                result = request.whereEqualTo("TypeProblem",this.service).orderBy("CreationDate");
            } else {
                result = request.whereEqualTo("TypeProblem",this.service).whereGreaterThanOrEqualTo("Title",this.searchTxt);
            }
        } else {
            if(this.searchTxt.length() == 0){
                result = request.orderBy("CreationDate");
            } else {
                result = request.whereGreaterThanOrEqualTo("Title",this.searchTxt);
            }
        }
        result.whereEqualTo("UserID", UserAccess.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public  synchronized void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot snapshot = task.getResult();
                    for(DocumentSnapshot d : snapshot.getDocuments()){
                        System.out.println("Testestestestestestestestestestestestestestestest");
                        //Layout for all (in this page addButton);
                        LinearLayout linearAll = new LinearLayout(MyRequestActivity.this);
                        linearAll.setOrientation(LinearLayout.VERTICAL);
                        //Creation de la carte avec Image
                        CardView cardview = new CardView(MyRequestActivity.this);
                        cardview.setUseCompatPadding(true);

                        LinearLayout lt = new LinearLayout(MyRequestActivity.this);
                        lt.setOrientation(LinearLayout.HORIZONTAL);
                        //lt.setGravity(Gravity.CENTER_HORIZONTAL);


                        ImageView img = new ImageView(MyRequestActivity.this);
                        //img.setForegroundGravity(Gravity.LEFT);
                        if(d.get("TypeProblem").toString().equals("Courses")){
                            img.setImageResource(R.drawable.shopping);
                        }
                        if(d.get("TypeProblem").toString().equals("Administratif")){
                            img.setImageResource(R.drawable.administrative);
                        }
                        if(d.get("TypeProblem").toString().equals("Soutiens scolaire")){
                            img.setImageResource(R.drawable.school);
                        }
                        if(d.get("TypeProblem").toString().equals("Accompagnement en voiture")){
                            img.setImageResource(R.drawable.car);
                        }
                        if(d.get("TypeProblem").toString().equals("Déménagement")){
                            img.setImageResource(R.drawable.moving_house);
                        }
                        if(d.get("TypeProblem").toString().equals("Autre")){
                            img.setImageResource(R.drawable.other);
                        }

                        img.setAdjustViewBounds(true);
                        img.setMaxWidth(300);
                        img.setMaxHeight(300);
                        LinearLayout test = new LinearLayout(MyRequestActivity.this);
                        test.setOrientation(LinearLayout.HORIZONTAL);
                        test.setGravity(Gravity.CENTER_VERTICAL);
                        test.addView(img);

                        //linear Text Pour la description etc
                        LinearLayout ltxt = new LinearLayout(getApplicationContext());
                        ltxt.setMinimumWidth(0);
                        ltxt.setWeightSum(2);
                        ltxt.setMinimumHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                        ltxt.setOrientation(LinearLayout.VERTICAL);
                        TextView txtadd = new TextView(MyRequestActivity.this);

                        txtadd.setText(d.get("Title").toString());
                        txtadd.setTextAppearance(MyRequestActivity.this, R.style.TextAppearance_AppCompat_Large);
                        txtadd.setTextColor(BLACK);
                        txtadd.setGravity(Gravity.LEFT);
                        ltxt.addView(txtadd);

                        txtadd = new TextView(MyRequestActivity.this);
                        txtadd.setText(d.get("TypeProblem").toString());
                        txtadd.setTextAppearance(MyRequestActivity.this, R.style.TextAppearance_AppCompat_Medium);
                        txtadd.setTextColor(BLACK);
                        txtadd.setGravity(Gravity.LEFT);
                        ltxt.addView(txtadd);

                        txtadd = new TextView(MyRequestActivity.this);
                        txtadd.setText(d.get("Description").toString());
                        txtadd.setTextAppearance(MyRequestActivity.this, R.style.TextAppearance_AppCompat_Medium);
                        txtadd.setTextColor(BLACK);
                        txtadd.setGravity(Gravity.LEFT);
                        ltxt.addView(txtadd);

                        test.addView(ltxt);

                        //Gestion reporte
                        lt.addView(test);

                        cardview.addView(lt);
                        cardview.setCardBackgroundColor(Color.WHITE);
                        cardview.setTag(d.getId());
                        cardview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.out.println(v.getTag().toString());
                                Intent compteIntent = new Intent(MyRequestActivity.this, SeeRequestActivity.class);
                                compteIntent.putExtra("IDREQUEST",cardview.getTag().toString());
                                startActivity(compteIntent);
                                // do whatever you want to do on click (to launch any fragment or activity you need to put intent here.)
                            }
                        });

                        linearAll.addView(cardview);

                        LinearLayout layoutBtn = new LinearLayout(MyRequestActivity.this);
                        layoutBtn.setOrientation(LinearLayout.HORIZONTAL);

                        Button modif = new Button(MyRequestActivity.this);
                        modif.setText("Modification");
                        modif.setBackgroundResource(R.color.orange);
                        modif.setBackgroundColor(0xA9A9A9);
                        modif.getBackground().setColorFilter(Color.parseColor("#A9A9A9"), PorterDuff.Mode.DARKEN);
                        modif.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent compteIntent = new Intent(MyRequestActivity.this, ModifRequestActivity.class);
                                compteIntent.putExtra("IDREQUEST",cardview.getTag().toString());
                                compteIntent.putExtra("DESCRIPTION",d.get("Description").toString());
                                compteIntent.putExtra("TITLE",d.get("Title").toString());
                                compteIntent.putExtra("TypeProblem",d.get("TypeProblem").toString());

                                startActivity(compteIntent);
                            }
                        });


                        Button suppr = new Button(MyRequestActivity.this);
                        suppr.setText("Supprimer");
                        suppr.setBackground(getResources().getDrawable(R.drawable.radusbtn));
                        suppr.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder popup = new AlertDialog.Builder(MyRequestActivity.this);
                                popup.setTitle("Suppresion");
                                popup.setMessage("Voulez vous supprimer ?");
                                popup.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LinearLayout layout = findViewById(R.id.addCart);
                                        layout.removeView(cardview);


                                        fStore.collection("HubsRequests").whereEqualTo("RequestID",cardview.getTag().toString()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                                                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                                    Log.d("TAG", "DocumentSnapshot data: " + dc.getDocument().getId());

                                                    fStore.collection("RequestsMessages").whereEqualTo("HubRequestID", dc.getDocument().getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                                                            for (DocumentChange dcc : snapshots.getDocumentChanges()) {
                                                                Log.d("TAG", "DocumentSnapshot data tout les messages : " + dcc.getDocument().getId());



                                                                fStore.collection("RequestsMessages").document(dcc.getDocument().getId())
                                                                        .delete()
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                //Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                //Log.w(TAG, "Error deleting document", e);
                                                                            }
                                                                        });
                                                            }
                                                        }

                                                    });

                                                    fStore.collection("HubsRequests").document(dc.getDocument().getId())
                                                            .delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    //Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    //Log.w(TAG, "Error deleting document", e);
                                                                }
                                                            });


                                                }
                                            }
                                        });
                                        fStore.collection("Requests").document(cardview.getTag().toString())
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        //Log.w(TAG, "Error deleting document", e);
                                                    }
                                                });
                                        startActivity(new Intent(MyRequestActivity.this, MyRequestActivity.class));

                                    }

                                });
                                popup.setNegativeButton("Ne pas supprimer",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("IsReport",false);

                                        fStore.collection("Requests").document(cardview.getTag().toString())
                                                .set(data, SetOptions.merge());
                                    }
                                });
                                popup.show();
                            }
                        });

                        layoutBtn.addView(modif);
                        layoutBtn.addView(suppr);
                        linearAll.addView(layoutBtn);

                        layout.addView(linearAll);
                    }
                    if(snapshot.getDocuments().size() == 0){
                        Log.d("TAG", "Mot de passe ou login incorrect", task.getException());
                    }
                } else {
                    Log.d("TAG", "Mot de passe ou login incorrect", task.getException());
                }
            }
        });
    }

    public void search(View v){
        this.searchTxt = this.schearBar.getText().toString().trim();
        this.loadDonnee();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.service = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
