package com.example.entraideettoi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import static android.graphics.Color.BLACK;

public class MessageHubActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private FirebaseFirestore fStore;
    private Spinner spinner;
    private String otherId, title, typeService, requestID;
    private static final String[] paths = {"courses", "démarche administrative", "soutiens scolaire", "accompagnement en voiture", "demenagement", "autre"};
    private List list = Collections.synchronizedList(new ArrayList());

    String userID = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null || UserAccess.getId() == null || UserAccess.getFirstName() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        setContentView(R.layout.page_message_hub);
        fStore = FirebaseFirestore.getInstance();
        displayMessage();

        // Initialize And Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.nav_message);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_add_request:
                        startActivity(new Intent(getApplicationContext(), AddRequestActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_message:
                        return true;
                    case R.id.nav_my_request:
                        startActivity(new Intent(getApplicationContext(), MyRequestActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_setting:
                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    public synchronized void displayMessage() {
        userID = UserAccess.getId();
        System.out.println(userID);
        LinearLayout layoutAll = findViewById(R.id.addCart);
        layoutAll.removeAllViews();

        fStore.collection("HubsRequests").whereEqualTo("HelperID", UserAccess.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public  synchronized void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    for (DocumentSnapshot dc : snapshot.getDocuments()) {

                        otherId = dc.get("OwnerID").toString();
                        requestID = dc.get("RequestID").toString();

                        methodeOsef(new String(requestID),new String(otherId),new String(dc.getId()));


                    }
                }
            }
        });
        fStore.collection("HubsRequests").whereEqualTo("OwnerID", UserAccess.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public  synchronized void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    for (DocumentSnapshot dc : snapshot.getDocuments()) {

                        otherId = dc.get("HelperID").toString();
                        requestID = dc.get("RequestID").toString();

                        methodeOsef(new String(requestID),new String(otherId),new String(dc.getId()));


                    }
                }
            }
        });

    }

    public synchronized void methodeOsef(String reqID, String other, String idHub){

        DocumentReference hubRef = fStore.collection("Requests").document(reqID);

        hubRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public synchronized void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot dcc = task.getResult();
                    if (dcc.exists()) {
                        //System.out.println(other+ " hahahahahahahahaahahahahaahahahaahahaha");
                        title = dcc.get("Title").toString();
                        typeService = dcc.get("TypeProblem").toString();
                                        /*System.out.println("La personne que j'aide est : " + otherId);
                                        System.out.println("Le titre est : " + title);
                                        System.out.println("Le probleme type est : " + typeService);*/

                        CardView card = new CardView(MessageHubActivity.this);
                        card.setCardBackgroundColor(Color.WHITE);
                        card.setUseCompatPadding(true);
                        card.setRadius(50);
                        LinearLayout ltText = new LinearLayout(MessageHubActivity.this);
                        ltText.setOrientation(LinearLayout.VERTICAL);
                        ltText.setWeightSum(2);


                        //Title
                        TextView titleV = new TextView(MessageHubActivity.this);
                        titleV.setText(title);
                        titleV.setTextAppearance(MessageHubActivity.this, R.style.TextAppearance_AppCompat_Large);
                        titleV.setTextColor(BLACK);
                        titleV.setGravity(Gravity.CENTER_HORIZONTAL);
                        ltText.addView(titleV);
                        //Categorie
                        TextView typeProblem = new TextView(MessageHubActivity.this);
                        typeProblem.setText(typeService);
                        typeProblem.setTextAppearance(MessageHubActivity.this, R.style.TextAppearance_AppCompat_Medium);
                        typeProblem.setTextColor(BLACK);
                        typeProblem.setGravity(Gravity.CENTER_HORIZONTAL);
                        ltText.addView(typeProblem);

                        card.addView(ltText);
                        System.out.println(idHub + "hahahahahahahahahahahahahahahahahahahaha hhihihihihihihihi");
                        card.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //System.out.println(v.getTag().toString());
                                Intent compteIntent = new Intent(MessageHubActivity.this, MessageActivity.class);
                                compteIntent.putExtra("IDHUB",idHub);
                                compteIntent.putExtra("IDOTHER",other);
                                startActivity(compteIntent);
                                // do whatever you want to do on click (to launch any fragment or activity you need to put intent here.)
                            }
                        });
                        //Can add OnClick for the card and add the id of the hub in the TAG


                        //  Take pseudo of the other
                        DocumentReference userRef = fStore.collection("Users").document(other);

                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public synchronized void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot dccc = task.getResult();
                                    if (dccc.exists()) {

                                        TextView typeProblem = new TextView(MessageHubActivity.this);
                                        typeProblem.setText(dccc.get("Pseudo").toString());
                                        typeProblem.setTextAppearance(MessageHubActivity.this, R.style.TextAppearance_AppCompat_Medium);
                                        typeProblem.setGravity(Gravity.CENTER_HORIZONTAL);
                                        ltText.addView(typeProblem);
                                        typeProblem.setTextColor(BLACK);
                                        LinearLayout layoutAll = findViewById(R.id.addCart);


                                        layoutAll.addView(card);



                                    } else {
                                        Log.d("TAG", "No such document");
                                    }
                                } else {
                                    Log.d("TAG", "get failed with ", task.getException());
                                }
                            }
                        });

                    } else {
                        Log.d("TAG", "No such request found : " + requestID);
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

    }
}
