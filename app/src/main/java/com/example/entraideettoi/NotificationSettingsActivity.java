package com.example.entraideettoi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class NotificationSettingsActivity extends AppCompatActivity {
    private FirebaseFirestore fStore;
    Switch administratif, course, scolaire, voiture, demenagement, autre, messagerie;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(FirebaseAuth.getInstance().getCurrentUser() == null || UserAccess.getId() == null || UserAccess.getFirstName() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        setContentView(R.layout.page_notification_setting);
        fStore = FirebaseFirestore.getInstance();

        course = findViewById(R.id.courses);
        administratif = findViewById(R.id.administratif);
        scolaire = findViewById(R.id.scolaire);
        voiture = findViewById(R.id.voiture);
        demenagement = findViewById(R.id.demenagement);
        autre = findViewById(R.id.autre);
        messagerie = findViewById(R.id.message);

        foundNotification();

    }

    public void backPage(View view) {
        // open gallery
        onBackPressed();
    }

    public void save(View view) {
        Map<String, Object> userNotification = new HashMap<>();
        if(administratif.isChecked()){
            userNotification.put("Administratif", true);
        }
        else{
            userNotification.put("Administratif", false);
        }

        if(messagerie.isChecked()){
            userNotification.put("Message", true);
        }
        else {
            userNotification.put("Message", false);
        }

        if(autre.isChecked()){
            userNotification.put("Autre", true);
        }
        else{
            userNotification.put("Autre", false);
        }

        if(course.isChecked()){
            userNotification.put("Courses", true);
        }
        else{
            userNotification.put("Courses", false);
        }

        if(scolaire.isChecked()){
            userNotification.put("Scolaire", true);
        }
        else{
            userNotification.put("Scolaire", false);
        }

        if(demenagement.isChecked()){
            userNotification.put("Demenagement", true);
        }
        else{
            userNotification.put("Demenagement", false);
        }

        if(voiture.isChecked()){
            userNotification.put("Voiture", true);
        }
        else{
            userNotification.put("Voiture", false);
        }
        DocumentReference oneRef = fStore.collection("UsersNotifications").document(UserAccess.getId());

        oneRef
                .update(userNotification)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated! ");
                        // open gallery
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Error updating document ");
                    }
                });







    }

    public void foundNotification(){
        DocumentReference hubRef = fStore.collection("UsersNotifications").document(UserAccess.getId());

        hubRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public synchronized void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot dc = task.getResult();
                    if (dc.exists()) {
                        if(dc.get("Administratif").toString().equals("true")){
                            administratif.setChecked(true);
                        }
                        if(dc.get("Autre").toString().equals("true")){
                            autre.setChecked(true);
                        }
                        if(dc.get("Courses").toString().equals("true")){
                            course.setChecked(true);
                        }
                        if(dc.get("Demenagement").toString().equals("true")){
                            demenagement.setChecked(true);
                        }
                        if(dc.get("Message").toString().equals("true")){
                            messagerie.setChecked(true);
                        }
                        if(dc.get("Scolaire").toString().equals("true")){
                            scolaire.setChecked(true);
                        }
                        if(dc.get("Voiture").toString().equals("true")){
                            voiture.setChecked(true);
                        }






                    }

                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

    }
}
