package com.example.entraideettoi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MessageActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private EditText message;
    private FirebaseFirestore fStore;
    private LinearLayout lLayout;
    private String ownerId, ownerPseudo, helperId, helperPseudo, otherId, otherPseudo;

    ImageView starImg;

    String hubRequestID, otherID;
    Button backPage, userPseudoButton, star1, star2, star3, star4, star5;
    TextView userPseudo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        setContentView(R.layout.page_message);
        Intent intent = getIntent();
        hubRequestID = intent.getStringExtra("IDHUB");
        otherID = intent.getStringExtra("IDOTHER");
        fStore = FirebaseFirestore.getInstance();
        System.out.println("testestestestestestestestestest quad test "+ hubRequestID);
        DocumentReference userNoteRef = fStore.collection("Users").document(otherID);
        userNoteRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        otherPseudo = document.getData().get("Pseudo").toString();
                    }
                }
            }
        });

        fStore.collection("NotationsUsers")
                .whereEqualTo("UserNoteID", UserAccess.getId()).whereEqualTo("UserNotedID", otherID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            String i = "0";
                            for (DocumentChange dcc : document.getDocumentChanges()) {
                                i = dcc.getDocument().get("Note").toString();
                                if(i.equals("1")){
                                    starImg.setImageResource(R.drawable.star1);
                                }
                                if(i.equals("2")){
                                    starImg.setImageResource(R.drawable.star2);
                                }
                                if(i.equals("3")){
                                    starImg.setImageResource(R.drawable.star3);
                                }
                                if(i.equals("4")){
                                    starImg.setImageResource(R.drawable.star4);
                                }
                                if(i.equals("5")){
                                    starImg.setImageResource(R.drawable.star5);
                                }


                            }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        chargingMessage();
        message = findViewById(R.id.chat);
        lLayout = findViewById(R.id.testlayout);
        backPage = findViewById(R.id.backButton);
        userPseudo = findViewById(R.id.userPseudo);
        userPseudoButton = findViewById(R.id.userPseudoButton);
        starImg = findViewById(R.id.starImg);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);
        searchNewMessages();


        Log.d("TAG", "MON OTHER ID C'EST : " + otherID);

        DocumentReference hubRef = fStore.collection("Users").document(otherID);

        hubRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public synchronized void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot dcc = task.getResult();
                    if (dcc.exists()) {
                        userPseudo.setText(dcc.get("Pseudo").toString());
                    }

                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        userPseudoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                Intent compteIntent = new Intent(MessageActivity.this, UserActivity.class);
                compteIntent.putExtra("IDUSER",otherID);
                startActivity(compteIntent);
            }
        });


        //  Button for back page
        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                onBackPressed();
            }
        });

        //  Buttons for set note
        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Set image
                starImg.setImageResource(R.drawable.star1);
                createNotation(1);

            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starImg.setImageResource(R.drawable.star2);
                createNotation(2);
            }
        });
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starImg.setImageResource(R.drawable.star3);
                createNotation(3);
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starImg.setImageResource(R.drawable.star4);
                createNotation(4);
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starImg.setImageResource(R.drawable.star5);
                createNotation(5);
            }
        });
    }

    public void createNotation(int note){

        CollectionReference requestHub = fStore.collection("NotationsUsers");
        requestHub.whereEqualTo("UserNoteID", UserAccess.getId()).whereEqualTo("UserNotedID", otherID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()) {
                    Log.d("TAG", "No Notation exist for this ask, creating ...");


                    //  Set Mapping of notation
                    Map<String, Object> notation = new HashMap<>();
                    notation.put("UserNoteID", UserAccess.getId());
                    notation.put("UserNotedID", otherID);
                    notation.put("Note", note);

                    fStore.collection("NotationsUsers").document().set(notation).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });

                }
                else{
                    Log.d("TAG", "The HubRequest with this idUser and idOwner already exist");

                    fStore.collection("NotationsUsers")
                            .whereEqualTo("UserNoteID", UserAccess.getId()).whereEqualTo("UserNotedID", otherID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot document = task.getResult();
                                        for (DocumentChange dcc : document.getDocumentChanges()) {
                                            Log.d("TAG", "DocumentSnapshot data tout les messages : " + dcc.getDocument().getId());


                                            DocumentReference oneRef = fStore.collection("NotationsUsers").document(dcc.getDocument().getId());

                                            oneRef
                                                    .update("Note", note)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error updating document", e);
                                                        }
                                                    });

                                        }


                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
            }
        });
    }

    /**
     * this method was called when your click in the buttom send is check of your message is not empty and after that send your message in the firecloud
     * @param v
     */
    public void sendMessage(View v) {

        if (this.message != null &&!this.message.getText().toString().isEmpty()) {

            String test = this.message.getText().toString();
            message.setText("");
            Calendar current = Calendar.getInstance();
            Date dateCreation = current.getTime();
            Map<String, Object> users = new HashMap<>();
            users.put("DateMessage", dateCreation);
            users.put("HubRequestID", hubRequestID);
            users.put("Message",test);
            users.put("SenderID", UserAccess.getId());

            MessageActivity.this.fStore.collection("RequestsMessages").document().set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);
                }
            });
        }
    }

    public void chargingMessage() {
        //  Take ID of helper and owner
        fStore.collection("RequestsMessages").orderBy("DateMessage", Query.Direction.ASCENDING).whereEqualTo("HubRequestID", hubRequestID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public synchronized void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    for (DocumentSnapshot d : snapshot.getDocuments()) {
                        LinearLayout lmessage = new LinearLayout(MessageActivity.this);
                        lmessage.setPadding(10,10,10,10);
                        CardView card = new CardView(MessageActivity.this);
                        card.setUseCompatPadding(true);
                        LinearLayout msgOPti = new LinearLayout(MessageActivity.this);
                        msgOPti.setWeightSum(3);
                        msgOPti.setOrientation(LinearLayout.VERTICAL);
                        String pseudo;
                        if(otherID.equals(d.get("SenderID").toString())){
                            pseudo = otherPseudo;
                            lmessage.setGravity(Gravity.LEFT);
                            card.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                            msgOPti.setGravity(Gravity.LEFT);
                        }
                        else{
                            card.setBackgroundResource(R.drawable.radusbtn);
                            pseudo = UserAccess.getPseudo();
                            lmessage.setGravity(Gravity.RIGHT);
                            card.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                            msgOPti.setGravity(Gravity.RIGHT);
                        }

                        String mess = d.get("Message").toString();

                        TextView t = new TextView((MessageActivity.this));
                        TextView tmsg = new TextView(MessageActivity.this);
                        tmsg.setTextAppearance(MessageActivity.this, R.style.TextAppearance_AppCompat_Large);
                        tmsg.setText(pseudo + " : ");
                        t.setText(mess);
                        t.setTextAppearance(MessageActivity.this, R.style.TextAppearance_AppCompat_Medium);
                        msgOPti.addView(tmsg);
                        msgOPti.addView(t);
                        System.out.println(ownerPseudo + " JE SUIS DANS SENDER EGAL OWNERID");
                        card.addView(msgOPti);
                        lmessage.addView(card);
                        MessageActivity.this.lLayout.addView(lmessage);

                    }
                }
            }
        });
    }

    public void searchNewMessages(){
        fStore.collection("RequestsMessages").whereGreaterThanOrEqualTo("DateMessage", Calendar.getInstance().getTime()).whereEqualTo("HubRequestID", hubRequestID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        LinearLayout lmessage = new LinearLayout(MessageActivity.this);
                        lmessage.setPadding(10,10,10,10);
                        CardView card = new CardView(MessageActivity.this);
                        card.setUseCompatPadding(true);
                        LinearLayout msgOPti = new LinearLayout(MessageActivity.this);
                        msgOPti.setWeightSum(2);
                        msgOPti.setOrientation(LinearLayout.VERTICAL);
                        String pseudo;
                        if(otherID.equals(dc.getDocument().get("SenderID").toString())){

                            pseudo = otherPseudo;
                            lmessage.setGravity(Gravity.LEFT);
                            card.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                            msgOPti.setGravity(Gravity.LEFT);
                        }
                        else{
                            card.setBackgroundResource(R.drawable.radusbtn);
                            pseudo = UserAccess.getPseudo();
                            lmessage.setGravity(Gravity.RIGHT);
                            card.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                            msgOPti.setGravity(Gravity.RIGHT);
                        }

                        String mess = dc.getDocument().get("Message").toString();

                        TextView t = new TextView((MessageActivity.this));
                        TextView tmsg = new TextView(MessageActivity.this);
                        tmsg.setTextAppearance(MessageActivity.this, R.style.TextAppearance_AppCompat_Large);
                        tmsg.setText(pseudo + " : ");
                        t.setText(mess);
                        t.setTextAppearance(MessageActivity.this, R.style.TextAppearance_AppCompat_Medium);
                        msgOPti.addView(tmsg);
                        msgOPti.addView(t);
                        System.out.println(ownerPseudo + " JE SUIS DANS SENDER EGAL OWNERID");
                        card.addView(msgOPti);
                        lmessage.addView(card);
                        MessageActivity.this.lLayout.addView(lmessage);

                    }
                }

            }
        });
    }
}

/*
DocumentReference noteRef = fStore.collection("HubsRequests").document(hubRequestID);
        noteRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ownerId = document.getData().get("OwnerID").toString();
                        helperId = document.getData().get("HelperID").toString();
                        System.out.println(ownerId+ "De mon Maxime");
                        System.out.println(helperId+ "De mon Maxime");


                        //  Take pseudo of ownerId
                        DocumentReference userNoteRef = fStore.collection("Users").document(ownerId);

                        userNoteRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d("TAG", "DocumentSnapshot owner data: " + document.getData());
                                        ownerPseudo = document.getData().get("Pseudo").toString();


                                        //  Take pseudo of helperId
                                        DocumentReference userNoteRef = fStore.collection("Users").document(helperId);

                                        userNoteRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        Log.d("TAG", "DocumentSnapshot owner data: " + document.getData());
                                                        helperPseudo = document.getData().get("Pseudo").toString();


                                                        Log.d("TAG", "Mon helperID: " + helperId + "/ Mon ID :"+ UserAccess.getId());
                                                        Log.d("TAG", "Mon ownerID: " + ownerId + "/ Mon ID :"+ UserAccess.getId());


                                                        //  Found pseudo
                                                        if(helperId.equals(UserAccess.getId())){
                                                            userPseudo.setText(ownerPseudo);
                                                            otherId = ownerId;
                                                            otherPseudo = ownerPseudo;
                                                        }
                                                        if(ownerId.equals(UserAccess.getId())){
                                                            userPseudo.setText(helperPseudo);
                                                            otherId = helperId;
                                                            otherPseudo = helperPseudo;
                                                        }




                                                        // Charging messages
                                                        CollectionReference requestHub = fStore.collection("RequestsMessages");
                                                        requestHub.whereEqualTo("HubRequestID", hubRequestID).orderBy("DateMessage").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                                                // MESSAGES TO MESSAGE
                                                                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                                                    System.out.println(helperId + " ce ceci est mon HHHHHHHHHHHHHHHHHHHEEEEEELLLLLPPPPP");
                                                                    System.out.println(ownerId + " ce ceci est mon Owwwwwwwwwwwwwwwwwner");
                                                                    if (dc.getType() == DocumentChange.Type.ADDED) {
                                                                        String senderID = dc.getDocument().get("SenderID").toString();

                                                                        if(senderID.equals(ownerPseudo)){

                                                                            if(ownerPseudo.equals(otherID)){
                                                                                LinearLayout lmessage = new LinearLayout(MessageActivity.this);
                                                                                lmessage.setGravity(Gravity.LEFT);
                                                                                LinearLayout msgOPti = new LinearLayout(MessageActivity.this);
                                                                                msgOPti.setOrientation(LinearLayout.VERTICAL);
                                                                                msgOPti.setGravity(Gravity.LEFT);
                                                                                CardView card = new CardView(MessageActivity.this);
                                                                                card.setMinimumWidth(500);
                                                                                String pseudo = ownerPseudo;
                                                                                String mess = dc.getDocument().get("Message").toString();
                                                                                TextView t = new TextView((MessageActivity.this));
                                                                                TextView tmsg = new TextView(MessageActivity.this);
                                                                                tmsg.setTextAppearance(MessageActivity.this, R.style.TextAppearance_AppCompat_Large);
                                                                                tmsg.setText(pseudo + " : ");
                                                                                t.setText(mess);
                                                                                t.setTextAppearance(MessageActivity.this, R.style.TextAppearance_AppCompat_Medium);
                                                                                msgOPti.addView(tmsg);
                                                                                msgOPti.addView(t);
                                                                                System.out.println(ownerPseudo + " JE SUIS DANS SENDER EGAL OWNERID");
                                                                                card.addView(msgOPti);
                                                                                lmessage.addView(card);
                                                                                MessageActivity.this.lLayout.addView(lmessage);
                                                                            } else {
                                                                                LinearLayout lmessage = new LinearLayout(MessageActivity.this);
                                                                                lmessage.setGravity(Gravity.RIGHT);
                                                                                LinearLayout msgOPti = new LinearLayout(MessageActivity.this);
                                                                                msgOPti.setOrientation(LinearLayout.VERTICAL);
                                                                                msgOPti.setGravity(Gravity.RIGHT);
                                                                                CardView card = new CardView(MessageActivity.this);
                                                                                card.setMinimumWidth(500);
                                                                                String pseudo = ownerPseudo;
                                                                                String mess = dc.getDocument().get("Message").toString();
                                                                                TextView t = new TextView((MessageActivity.this));
                                                                                TextView tmsg = new TextView(MessageActivity.this);
                                                                                tmsg.setTextAppearance(MessageActivity.this, R.style.TextAppearance_AppCompat_Large);
                                                                                tmsg.setText(pseudo + " : ");
                                                                                t.setText(mess);
                                                                                t.setTextAppearance(MessageActivity.this, R.style.TextAppearance_AppCompat_Medium);
                                                                                msgOPti.addView(tmsg);
                                                                                msgOPti.addView(t);
                                                                                System.out.println(ownerPseudo + " JE SUIS DANS SENDER EGAL OWNERID");
                                                                                card.addView(msgOPti);
                                                                                lmessage.addView(card);
                                                                                MessageActivity.this.lLayout.addView(lmessage);
                                                                            }


                                                                        }
                                                                        else {
                                                                                if(helperId.equals(otherID)){
                                                                                    LinearLayout lmessage = new LinearLayout(MessageActivity.this);
                                                                                    lmessage.setGravity(Gravity.LEFT);
                                                                                    LinearLayout msgOPti = new LinearLayout(MessageActivity.this);
                                                                                    msgOPti.setOrientation(LinearLayout.VERTICAL);
                                                                                    msgOPti.setGravity(Gravity.LEFT);
                                                                                    CardView card = new CardView(MessageActivity.this);
                                                                                    card.setMinimumWidth(500);
                                                                                    String pseudo = helperPseudo.toString();
                                                                                    String mess = dc.getDocument().get("Message").toString();
                                                                                    TextView t = new TextView((MessageActivity.this));
                                                                                    TextView tmsg = new TextView(MessageActivity.this);
                                                                                    tmsg.setTextAppearance(MessageActivity.this, R.style.TextAppearance_AppCompat_Large);
                                                                                    tmsg.setText(pseudo + " : ");
                                                                                    t.setText(mess);
                                                                                    t.setTextAppearance(MessageActivity.this, R.style.TextAppearance_AppCompat_Medium);
                                                                                    msgOPti.addView(tmsg);
                                                                                    msgOPti.addView(t);
                                                                                    System.out.println(ownerPseudo + " JE SUIS DANS SENDER EGAL OWNERID");
                                                                                    card.addView(msgOPti);
                                                                                    lmessage.addView(card);
                                                                                    MessageActivity.this.lLayout.addView(lmessage);
                                                                                } else {
                                                                                    LinearLayout lmessage = new LinearLayout(MessageActivity.this);
                                                                                    lmessage.setGravity(Gravity.RIGHT);
                                                                                    LinearLayout msgOPti = new LinearLayout(MessageActivity.this);
                                                                                    msgOPti.setOrientation(LinearLayout.VERTICAL);
                                                                                    msgOPti.setGravity(Gravity.RIGHT);
                                                                                    CardView card = new CardView(MessageActivity.this);
                                                                                    card.setMinimumWidth(500);
                                                                                    String pseudo = helperPseudo.toString();
                                                                                    String mess = dc.getDocument().get("Message").toString();
                                                                                    TextView t = new TextView((MessageActivity.this));
                                                                                    TextView tmsg = new TextView(MessageActivity.this);
                                                                                    tmsg.setTextAppearance(MessageActivity.this, R.style.TextAppearance_AppCompat_Large);
                                                                                    tmsg.setText(pseudo + " : ");
                                                                                    t.setText(mess);
                                                                                    t.setTextAppearance(MessageActivity.this, R.style.TextAppearance_AppCompat_Medium);
                                                                                    msgOPti.addView(tmsg);
                                                                                    msgOPti.addView(t);
                                                                                    System.out.println(ownerPseudo + " JE SUIS DANS SENDER EGAL OWNERID");
                                                                                    card.addView(msgOPti);
                                                                                    lmessage.addView(card);
                                                                                    MessageActivity.this.lLayout.addView(lmessage);
                                                                                }

                                                                        }


                                                                    }
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        Log.d("TAG", "No such document");
                                                    }
                                                } else {
                                                    Log.d("TAG", "get failed with ", task.getException());
                                                }
                                            }
                                        });
                                    } else {
                                        Log.d("TAG", "No such document");
                                    }
                                } else {
                                    Log.d("TAG", "get failed with ", task.getException());
                                }
                            }
                        });
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else{
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

 */