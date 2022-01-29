package com.example.entraideettoi;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.graphics.Color.BLACK;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private FirebaseFirestore fStore;
    private EditText searchBar;
    private static final String[] services = {"Tous","Courses", "Démarche administrative", "Soutiens scolaire","Accompagnement en voiture", "Déménagement","Autre"};
    private String searchTxt;
    private String service;
    private Spinner spinner;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(FirebaseAuth.getInstance().getCurrentUser() == null || UserAccess.getId() == null || UserAccess.getFirstName() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        setContentView(R.layout.page_home);
        fStore = FirebaseFirestore.getInstance();
        this.service = "Tous";
        spinner = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomeActivity.this,
                android.R.layout.simple_spinner_item,services);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        this.fStore = FirebaseFirestore.getInstance();
        this.searchBar = findViewById(R.id.SearchTitle);
        this.searchTxt = "";
        loadDonnee();
        notificationMessage();
        System.out.println(searchTxt.contains("le test"));
        // Initialize And Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
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

    public void loadDonnee(){
        LinearLayout layout = findViewById(R.id.addCart);
        layout.removeAllViews();
        CollectionReference request = fStore.collection("Requests");
        Query result;

        if(!this.service.equals("Tous")){
            if(this.searchTxt.length() == 0){
                result = request.whereEqualTo("TypeProblem",this.service).orderBy("CreationDate", Query.Direction.DESCENDING);
            } else {
                result = request.whereEqualTo("TypeProblem",this.service).whereGreaterThanOrEqualTo("Title",this.searchTxt);
            }
        } else {
            if(this.searchTxt.length() == 0){
                result = request.orderBy("CreationDate", Query.Direction.DESCENDING);
            } else {
                result = request.whereGreaterThanOrEqualTo("Title",this.searchTxt);
            }
        }
        result.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public  synchronized void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot snapshot = task.getResult();
                    for(DocumentSnapshot d : snapshot.getDocuments()){

                        //  User save data
                        //Creation de la carte avec Image
                        CardView cardview = new CardView(HomeActivity.this);
                        cardview.setUseCompatPadding(true);
                        cardview.setRadius(50);

                        LinearLayout lt = new LinearLayout(HomeActivity.this);
                        lt.setOrientation(LinearLayout.HORIZONTAL);
                        //lt.setGravity(Gravity.CENTER_VERTICAL);
                        if(UserAccess.isAdmin()){



                            if(d.getBoolean("IsReport")) {
                                cardview.setCardBackgroundColor(Color.RED);
                            }
                            else{
                                cardview.setCardBackgroundColor(Color.WHITE);
                            }


                            ImageView poubelle = new ImageView(HomeActivity.this);
                            poubelle.setAdjustViewBounds(true);
                            poubelle.setMaxWidth(40);
                            poubelle.setMaxHeight(40);


                            poubelle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    AlertDialog.Builder popup = new AlertDialog.Builder(HomeActivity.this);
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

                                                                    fStore.collection("RequestsMessages").document(dcc.getDocument().getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            //Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            //Log.w(TAG, "Error deleting document", e);
                                                                        }
                                                                    });
                                                                }
                                                            }

                                                        });

                                                        fStore.collection("HubsRequests").document(dc.getDocument().getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
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


                                        }

                                    });
                                    popup.show();
                                }
                            });
                            lt.addView(poubelle);
                        } else {
                            if(d.getBoolean("IsReport")) {
                                ImageView flag = new ImageView(HomeActivity.this);
                                flag.setAdjustViewBounds(true);
                                flag.setMaxWidth(40);
                                flag.setMaxHeight(40);


                                flag.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(HomeActivity.this,"Cette annonce a déja été signalé",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                lt.addView(flag);

                            } else {
                                ImageView flag = new ImageView(HomeActivity.this);
                                flag.setAdjustViewBounds(true);
                                flag.setMaxWidth(40);
                                flag.setMaxHeight(40);
                                lt.addView(flag);



                                flag.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        AlertDialog.Builder popup = new AlertDialog.Builder(HomeActivity.this);
                                        popup.setTitle("Repport");
                                        popup.setMessage("Voulez vous signaler cette annonce ?");
                                        popup.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("IsReport",true);

                                                fStore.collection("Requests").document(cardview.getTag().toString())
                                                        .set(data, SetOptions.merge());
                                                flag.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Toast.makeText(HomeActivity.this,"Cette annonce a déja été signalé",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                        });
                                        popup.show();
                                    }
                                });
                            }
                        }

                        LinearLayout test = new LinearLayout(HomeActivity.this);
                        test.setOrientation(LinearLayout.HORIZONTAL);
                        test.setGravity(Gravity.CENTER_VERTICAL);
                        ImageView img = new ImageView(HomeActivity.this);
                        //img.setForegroundGravity(Gravity.LEFT);
                        if(d.get("TypeProblem").toString().equals("Courses")){
                            img.setImageResource(R.drawable.shopping);
                        }
                        if(d.get("TypeProblem").toString().equals("Démarche administrative")){
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

                        LinearLayout llImg = new LinearLayout(HomeActivity.this);
                        llImg.setGravity(Gravity.CENTER_VERTICAL);
                        img.setAdjustViewBounds(true);
                        img.setMaxWidth(300);
                        img.setMaxHeight(300);
                        llImg.addView(img);
                        test.addView(llImg);




                        //linear Text Pour la description etc
                        LinearLayout ltxt = new LinearLayout(getApplicationContext());
                        ltxt.setMinimumWidth(0);
                        ltxt.setWeightSum(2);
                        ltxt.setMinimumHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                        ltxt.setGravity(Gravity.CENTER_VERTICAL);
                        ltxt.setOrientation(LinearLayout.VERTICAL);



                        TextView txtadd = new TextView(HomeActivity.this);
                        txtadd = new TextView(HomeActivity.this);
                        txtadd.setText("");
                        ltxt.addView(txtadd);

                        txtadd = new TextView(HomeActivity.this);
                        txtadd.setText(d.get("Title").toString());
                        txtadd.setPadding(0,10,0,0);
                        txtadd.setTextAppearance(HomeActivity.this, R.style.TextAppearance_AppCompat_Large);
                        txtadd.setTextColor(BLACK);
                        txtadd.setGravity(Gravity.CENTER_VERTICAL);
                        ltxt.addView(txtadd);

                        txtadd = new TextView(HomeActivity.this);
                        txtadd.setText(d.get("TypeProblem").toString());
                        txtadd.setTextAppearance(HomeActivity.this, R.style.TextAppearance_AppCompat_Medium);
                        txtadd.setTextColor(BLACK);
                        txtadd.setPadding(0,0,0,20);
                        txtadd.setTypeface(null, Typeface.BOLD);
                        txtadd.setGravity(Gravity.CENTER_VERTICAL);
                        ltxt.addView(txtadd);


                        txtadd = new TextView(HomeActivity.this);
                        if(d.get("Description").toString().length() > 250){
                            txtadd.setText(d.get("Description").toString().substring(0,250) + " ...");
                        }
                        else {
                            txtadd.setText(d.get("Description").toString());
                        }
                        txtadd.setPadding(0,0,0,10);
                        txtadd.setTextAppearance(HomeActivity.this, R.style.TextAppearance_AppCompat_Medium);
                        txtadd.setTextColor(BLACK);
                        txtadd.setTextSize(15);
                        txtadd.setGravity(Gravity.CENTER_VERTICAL);
                        ltxt.addView(txtadd);

                        txtadd = new TextView(HomeActivity.this);
                        txtadd.setText("");
                        ltxt.addView(txtadd);

                        test.addView(ltxt);
                        lt.addView(test);
                        //Gestion reporte


                        cardview.addView(lt);
                        if(!UserAccess.isAdmin()){
                            cardview.setCardBackgroundColor(Color.WHITE);
                        }
                        cardview.setTag(d.getId());
                        cardview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.out.println(v.getTag().toString());
                                Intent compteIntent = new Intent(HomeActivity.this, SeeRequestActivity.class);
                                compteIntent.putExtra("IDREQUEST",cardview.getTag().toString());
                                startActivity(compteIntent);
                                // do whatever you want to do on click (to launch any fragment or activity you need to put intent here.)
                            }
                        });
                        if(UserAccess.isAdmin()){
                            cardview.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v){


                                    Log.d("TAG", "ID OF REQUEST : " + cardview.getTag().toString());
                                    AlertDialog.Builder popup = new AlertDialog.Builder(HomeActivity.this);

                                    popup.setTitle("Administration");
                                    popup.setMessage("Que voulez-vous faire de cette annonce ?");
                                    popup.setNeutralButton("Enlever le signalement", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("IsReport",false);
                                            Intent compteIntent = new Intent(HomeActivity.this, HomeActivity.class);
                                            startActivity(compteIntent);

                                            fStore.collection("Requests").document(cardview.getTag().toString()).set(data, SetOptions.merge());

                                        }

                                    });


                                    popup.setPositiveButton("Supprimer la request", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("IsReport",false);

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

                                                                    fStore.collection("RequestsMessages").document(dcc.getDocument().getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            //Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            //Log.w(TAG, "Error deleting document", e);
                                                                        }
                                                                    });
                                                                }
                                                            }

                                                        });

                                                        fStore.collection("HubsRequests").document(dc.getDocument().getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
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

                                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            //Log.w(TAG, "Error deleting document", e);
                                                        }
                                                    });


                                        }

                                    });


                                    popup.show();


                                    return true;

                                }
                            });





                        }
                        else {
                            cardview.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v){


                                    Log.d("TAG", "ID OF REQUEST : " + cardview.getTag().toString());
                                    AlertDialog.Builder popup = new AlertDialog.Builder(HomeActivity.this);
                                    popup.setTitle("Signalement");
                                    popup.setMessage("Voulez vous signaler cette annonce ?");
                                    popup.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("IsReport",true);

                                            fStore.collection("Requests").document(cardview.getTag().toString()).set(data, SetOptions.merge());

                                        }

                                    });
                                    popup.show();


                                    return true;

                                }
                            });



                        }
                        layout.addView(cardview);



                       /* UserAccess.user.setEmail(d.get("email").toString());
                        UserAccess.user.setPseudo(d.get("pseudo").toString());
                        UserAccess.user.setFirstName(d.get("firstName").toString());
                        UserAccess.user.setLastName(d.get("lastName").toString());
                        UserAccess.user.setPassword(d.get("password").toString());*/

                        //  Trouver une solution pour récupérer Date UserAccess.user.setDateCreation(d.get("dateCreation"));
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

    public void messageSending(String title, String message){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(HomeActivity.this, "My Notification");
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.logo);
        builder.setAutoCancel(true);


        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(HomeActivity.this);
        managerCompat.notify(1,builder.build());
    }


    public void notificationMessage(){
        DocumentReference hubRef = fStore.collection("UsersNotifications").document(UserAccess.getId());

        hubRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public synchronized void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot dc = task.getResult();
                    if (dc.exists()) {
                        if(dc.get("Administratif").toString().equals("true")){

                            fStore.collection("Requests").whereEqualTo("TypeProblem", "Démarche administrative").whereGreaterThanOrEqualTo("CreationDate", Calendar.getInstance().getTime()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                        if (dc.getType() == DocumentChange.Type.ADDED) {
                                            if(!dc.getDocument().get("UserID").toString().equals(UserAccess.getId())) {

                                                messageSending("Demande d'aide", "Une nouvelle demande concernant 'Démarche administrative' est arrivé");
                                            }

                                        }
                                    }

                                }
                            });


                        }
                        if(dc.get("Autre").toString().equals("true")){
                            fStore.collection("Requests").whereEqualTo("TypeProblem", "Démarche administrative").whereGreaterThanOrEqualTo("CreationDate", Calendar.getInstance().getTime()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                        if (dc.getType() == DocumentChange.Type.ADDED) {
                                            if(!dc.getDocument().get("UserID").toString().equals(UserAccess.getId())) {
                                                messageSending("Demande d'aide", "Une nouvelle demande concernant un problème diverse est arrivé");
                                            }

                                        }
                                    }

                                }
                            });

                        }
                        if(dc.get("Courses").toString().equals("true")){
                            fStore.collection("Requests").whereEqualTo("TypeProblem", "Courses").whereGreaterThanOrEqualTo("CreationDate", Calendar.getInstance().getTime()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                        if (dc.getType() == DocumentChange.Type.ADDED) {
                                            if(!dc.getDocument().get("UserID").toString().equals(UserAccess.getId())) {

                                                messageSending("Demande d'aide", "Une nouvelle demande concernant une 'Course' est arrivé");
                                            }

                                        }
                                    }

                                }
                            });

                        }
                        if(dc.get("Demenagement").toString().equals("true")){
                            fStore.collection("Requests").whereEqualTo("TypeProblem", "Déménagement").whereGreaterThanOrEqualTo("CreationDate", Calendar.getInstance().getTime()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                        if (dc.getType() == DocumentChange.Type.ADDED) {
                                            if(!dc.getDocument().get("UserID").toString().equals(UserAccess.getId())) {

                                                messageSending("Demande d'aide", "Une nouvelle demande concernant un 'Déménagement' est arrivé");
                                            }

                                        }
                                    }

                                }
                            });

                        }
                        if(dc.get("Message").toString().equals("true")){
                            fStore.collection("RequestsMessages").whereGreaterThanOrEqualTo("DateMessage", Calendar.getInstance().getTime()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                        if (dc.getType() == DocumentChange.Type.ADDED) {
                                            if(!dc.getDocument().get("SenderID").toString().equals(UserAccess.getId())){

                                                //  Hub
                                                DocumentReference hubReq = fStore.collection("HubsRequests").document(dc.getDocument().get("HubRequestID").toString());

                                                hubReq.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot dcc = task.getResult();
                                                            if (dcc.exists()) {

                                                                if(dcc.get("OwnerID").toString().equals(UserAccess.getId()) || dcc.get("HelperID").toString().equals(UserAccess.getId())){
                                                                    messageSending(dc.getDocument().get("Message").toString(),"Vous avez reçu un nouveau message");
                                                                }



                                                            } else {
                                                                Log.d("TAG", "No such document");
                                                            }
                                                        } else {
                                                            Log.d("TAG", "get failed with ", task.getException());
                                                        }
                                                    }
                                                });
                                            }







                                            Log.d("TAG", "Un message a bien été ajouté, le hub de référence est : " + dc.getDocument().get("HubRequestID").toString());

                                        }
                                    }

                                }
                            });

                        }
                        if(dc.get("Scolaire").toString().equals("true")){
                            fStore.collection("Requests").whereEqualTo("TypeProblem", "Soutiens scolaire").whereGreaterThanOrEqualTo("CreationDate", Calendar.getInstance().getTime()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                        if (dc.getType() == DocumentChange.Type.ADDED) {
                                            if(!dc.getDocument().get("UserID").toString().equals(UserAccess.getId())) {

                                                messageSending("Demande d'aide", "Une nouvelle demande concernant un 'Soutiens scolaire' est arrivé");
                                            }

                                        }
                                    }

                                }
                            });


                        }
                        if(dc.get("Voiture").toString().equals("true")){
                            fStore.collection("Requests").whereEqualTo("TypeProblem", "Accompagnement en voiture").whereGreaterThanOrEqualTo("CreationDate", Calendar.getInstance().getTime()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                        if (dc.getType() == DocumentChange.Type.ADDED) {
                                            if(!dc.getDocument().get("UserID").toString().equals(UserAccess.getId())) {

                                                messageSending("Demande d'aide", "Une nouvelle demande concernant un 'Accompagnement en voiture' est arrivé");
                                            }

                                        }
                                    }

                                }
                            });

                        }


                    }

                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    public void search(View v){
        this.searchTxt = this.searchBar.getText().toString().trim();
        this.loadDonnee();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.service = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
