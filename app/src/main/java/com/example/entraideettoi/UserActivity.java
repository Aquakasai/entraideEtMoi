package com.example.entraideettoi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class UserActivity extends AppCompatActivity {

    private FirebaseFirestore fStore;


    Button backPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_user);

        fStore = FirebaseFirestore.getInstance();
        backPage = findViewById(R.id.backPage);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        Intent intent = getIntent();
        String userID = intent.getStringExtra("IDUSER");


        //  Take pseudo of helperId
        DocumentReference userNoteRef = fStore.collection("Users").document(userID);

        userNoteRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        //  LABEL
                        final TextView firstName = (TextView) findViewById(R.id.firstName);
                        final TextView lastName = (TextView) findViewById(R.id.lastName);
                        final TextView pseudo = (TextView) findViewById(R.id.pseudo);

                        //  Label fill
                        firstName.setText(document.get("FirstName").toString());
                        lastName.setText(document.get("LastName").toString());
                        pseudo.setText("Pseudo : " + document.get("Pseudo").toString());


                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        fStore.collection("NotationsUsers")
                .whereEqualTo("UserNotedID", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            // Number of elements
                            int i = 0;
                            // Additional all the note
                            int addAll = 0;
                            float result;
                            for (DocumentChange dcc : document.getDocumentChanges()) {
                                Log.d("TAG", "DocumentSnapshot data tout les messages : " + dcc.getDocument().getId());
                                i = i + 1;
                                if(dcc.getDocument().get("Note").toString().equals("1")){
                                    addAll = addAll + 1;
                                }
                                if(dcc.getDocument().get("Note").toString().equals("2")){
                                    addAll = addAll + 2;
                                }
                                if(dcc.getDocument().get("Note").toString().equals("3")){
                                    addAll = addAll + 3;
                                }
                                if(dcc.getDocument().get("Note").toString().equals("4")){
                                    addAll = addAll + 4;
                                }
                                if(dcc.getDocument().get("Note").toString().equals("5")){
                                    addAll = addAll + 5;
                                }


                            }
                            if(i==0){
                                result = 0;
                            }
                            else{
                                result = (float) addAll / i;
                            }



                            Log.d("TAG", "Le nombre d'élément est : " + i);
                            Log.d("TAG", "L'addition non divisé des notes est : " + addAll);
                            Log.d("TAG", "Le résultat est: " + result);
                            final TextView note = (TextView) findViewById(R.id.note);
                            final TextView notice = (TextView) findViewById(R.id.notice);
                            if(result == 0){
                                note.setText("Non évalué");
                            }
                            else{
                                note.setText(result + " / 5");
                            }
                            notice.setText(i + " avis");


                        } else {
                            Log.d("TAG", "Error : ");
                        }
                    }
                });








        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                onBackPressed();
            }
        });






    }



}