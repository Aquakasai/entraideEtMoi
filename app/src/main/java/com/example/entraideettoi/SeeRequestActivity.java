package com.example.entraideettoi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SeeRequestActivity extends AppCompatActivity {

    private FirebaseFirestore fStore;


    Button backPage, addConversation;
    LinearLayout description;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_see_request);
        Intent intent = getIntent();
        String requestIDTemp = intent.getStringExtra("IDREQUEST");
        if(FirebaseAuth.getInstance().getCurrentUser() == null || UserAccess.getId() == null || UserAccess.getFirstName() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }



        //  Database variable
        final String[] ownerId = new String[1];

        //  Temp variable for ID REQUEST, MAXIME have to change this for ID become from HomePage
        //String requestIDTemp = "VF29dtoXEnJiy1xambb3";
        Log.d("TAG", "La request envoyé est : "+requestIDTemp);


        //  Label
        final TextView title = (TextView) findViewById(R.id.title);
        final TextView category = (TextView) findViewById(R.id.category);
        final ImageView imageCategory = (ImageView) findViewById(R.id.imageCategorie);


        fStore = FirebaseFirestore.getInstance();
        backPage = findViewById(R.id.backPage);
        addConversation = findViewById(R.id.addConversation);
        description = findViewById(R.id.description);

        DocumentReference noteRef = fStore.collection("Requests").document(requestIDTemp);

        try {
            noteRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //img.setForegroundGravity(Gravity.LEFT);
                            if(document.getData().get("TypeProblem").toString().equals("Courses")){
                                imageCategory.setImageResource(R.drawable.shopping);
                            }
                            if(document.getData().get("TypeProblem").toString().equals("Démarche administrative")){
                                imageCategory.setImageResource(R.drawable.administrative);
                            }
                            if(document.getData().get("TypeProblem").toString().equals("Soutiens scolaire")){
                                imageCategory.setImageResource(R.drawable.school);
                            }
                            if(document.getData().get("TypeProblem").toString().equals("Accompagnement en voiture")){
                                imageCategory.setImageResource(R.drawable.car);
                            }
                            if(document.getData().get("TypeProblem").toString().equals("Déménagement")){
                                imageCategory.setImageResource(R.drawable.moving_house);
                            }
                            if(document.getData().get("TypeProblem").toString().equals("Autre")){
                                imageCategory.setImageResource(R.drawable.other);
                            }


                            title.setText(document.getData().get("Title").toString());
                            category.setText(document.getData().get("TypeProblem").toString());
                            TextView descri = findViewById(R.id.descriText);
                            System.out.println("Rend mon quad fdp " + document.getData().get("Description").toString());
                            descri.setText(document.getData().get("Description").toString());

                            ownerId[0] = document.getData().get("UserID").toString();


                            //  Remove button for add conversation if ID of the request and current ID is same / using char comparison because String comparison doesn't work
                            if(UserAccess.getId().charAt(0) == document.getData().get("UserID").toString().charAt(0) &&
                                    UserAccess.getId().charAt(1) == document.getData().get("UserID").toString().charAt(1) &&
                                    UserAccess.getId().charAt(2) == document.getData().get("UserID").toString().charAt(2) &&
                                    UserAccess.getId().charAt(3) == document.getData().get("UserID").toString().charAt(3) &&
                                    UserAccess.getId().charAt(4) == document.getData().get("UserID").toString().charAt(4) &&
                                    UserAccess.getId().charAt(5) == document.getData().get("UserID").toString().charAt(5) &&
                                    UserAccess.getId().charAt(6) == document.getData().get("UserID").toString().charAt(6) &&
                                    UserAccess.getId().length() == document.getData().get("UserID").toString().length()){

                                addConversation.setVisibility(View.INVISIBLE);
                            }

                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });

        } catch (Exception e) {
            // This will catch any exception, because they are all descended from Exception
            System.out.println("Error " + e.getMessage());
        }








        //  Button for back page
        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                onBackPressed();
            }
        });

        addConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addConversation.setVisibility(View.INVISIBLE);


                CollectionReference requestHub = fStore.collection("HubsRequests");
                requestHub.whereEqualTo("HelperID", UserAccess.getId()).whereEqualTo("OwnerID", ownerId[0]).whereEqualTo("RequestID", requestIDTemp).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()) {
                            Log.d("TAG", "No HubRequest exist for this ask, creating ...");
                            Calendar current = Calendar.getInstance();
                            Date dateCreation = current.getTime();


                            Map<String, Object> hubsRequests = new HashMap<>();
                            hubsRequests.put("CreationDate", dateCreation);
                            hubsRequests.put("HelperID", UserAccess.getId());
                            hubsRequests.put("OwnerID", ownerId[0]);
                            hubsRequests.put("RequestID", requestIDTemp);



                            // Add a new document with a generated ID
                            fStore.collection("HubsRequests")
                                    .add(hubsRequests)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                            System.out.println("Send to database WORK");
                                            Intent compteIntent = new Intent(SeeRequestActivity.this, MessageActivity.class);
                                            compteIntent.putExtra("IDHUB",documentReference.getId());
                                            compteIntent.putExtra("IDOTHER",ownerId[0]);
                                            startActivity(compteIntent);


                                            //////// NOT FORGET TO REDIRECT TO THE NEW CONVERSATION HUB


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Log.w(TAG, "Error adding document", e);
                                            System.out.println("Send to database DOESN'T WORK");
                                        }
                                    });

                        }
                        else{
                            for(DocumentSnapshot d :queryDocumentSnapshots.getDocuments()){
                                Log.d("TAG", "The HubRequest with this idUser and idOwner already exist");
                                Intent compteIntent = new Intent(SeeRequestActivity.this, MessageActivity.class);
                                compteIntent.putExtra("IDHUB",d.getId());
                                System.out.println("IDHUB");
                                compteIntent.putExtra("IDOTHER",ownerId[0]);
                                System.out.println("IDOTHER");
                                startActivity(compteIntent);
                            }

                        }

                    }
                });









            }
        });



    }



}