package com.example.entraideettoi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class EditPageActivity extends AppCompatActivity {

    // Temporaly user test
    User generalUser = new User();
    private FirebaseFirestore fStore;
    Button backToSetting, registerData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_edit_profile);


        fStore = FirebaseFirestore.getInstance();
        backToSetting = findViewById(R.id.BtnCancel);
        registerData = findViewById(R.id.BtnSave);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        //  Label
        final EditText firstName = (EditText) findViewById(R.id.editFirstName);
        final EditText lastName = (EditText) findViewById(R.id.editLastName);
        final EditText pseudo = (EditText) findViewById(R.id.editPseudo);
        final EditText tel = (EditText) findViewById(R.id.editTel);
        final EditText city = (EditText) findViewById(R.id.editCity);
        final EditText postalCode = (EditText) findViewById(R.id.editPostalCode);
        final EditText address = (EditText) findViewById(R.id.editAddress);
        final DatePicker birth = (DatePicker) findViewById(R.id.editBirth);

        //  Fill label
        firstName.setText(UserAccess.getFirstName());
        lastName.setText(UserAccess.getLastName());
        pseudo.setText(UserAccess.getPseudo());
        tel.setText(UserAccess.getTel());
        city.setText(UserAccess.getCity());
        postalCode.setText(UserAccess.getPostalCode());
        address.setText(UserAccess.getAddress());
        birth.init(UserAccess.getBirthYearInt(), UserAccess.getBirth().getMonth(), UserAccess.getBirth().getDate(), null);



        backToSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                startActivity(new Intent(EditPageActivity.this, SettingActivity.class));
            }
        });

        registerData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    Calendar current = Calendar.getInstance();
                    current.set(birth.getYear(), birth.getMonth(), birth.getDayOfMonth(), 0, 0, 0);
                    Date dateCreation = current.getTime();
                    Timestamp uneTime = new Timestamp(dateCreation);


                    DocumentReference noteRef = fStore.collection("Users").document(UserAccess.getId());
                    noteRef.update(
                            "FirstName", firstName.getText().toString(),
                            "LastName", lastName.getText().toString(),
                            "Address", address.getText().toString(),
                            "City", city.getText().toString(),
                            "PostalCode", postalCode.getText().toString(),
                            "Pseudo", pseudo.getText().toString(),
                            "Tel", tel.getText().toString(),
                            "Birth", uneTime.getSeconds()
                    ).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });




                    noteRef = fStore.collection("Users").document(UserAccess.getId());

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
                                    UserAccess.setPostalCode(document.getData().get("PostalCode").toString());
                                    UserAccess.setPseudo(document.getData().get("Pseudo").toString());
                                    UserAccess.setAddress(document.getData().get("Address").toString());
                                    UserAccess.setTel(document.getData().get("Tel").toString());
                                    UserAccess.setBirth(uneDate);

                                    startActivity(new Intent(EditPageActivity.this, SettingActivity.class));



                                } else {
                                    Log.d("TAG", "No such document");
                                }
                            } else {
                                Log.d("TAG", "Get failed with ", task.getException());
                            }
                        }


                    });





                }
                catch (Exception e) {
                    // This will catch any exception, because they are all descended from Exception
                    System.out.println("Error " + e.getMessage());
                }


            }
        });



    }
}