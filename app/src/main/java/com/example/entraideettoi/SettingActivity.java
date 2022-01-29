package com.example.entraideettoi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.entraideettoi.UserAccess.getBirth;

public class SettingActivity extends AppCompatActivity {

    private FirebaseFirestore fStore;

    Button editProfile, disconnect, userPseudoButton, notification, changePassword;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(FirebaseAuth.getInstance().getCurrentUser() == null || UserAccess.getId() == null || UserAccess.getFirstName() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        setContentView(R.layout.page_setting);

        fStore = FirebaseFirestore.getInstance();
        editProfile = findViewById(R.id.changeProfile);
        disconnect = findViewById(R.id.disconnect);
        userPseudoButton = findViewById(R.id.userPseudoButton);
        notification = findViewById(R.id.notification);
        changePassword = findViewById(R.id.changePassword);
        changePassword.setVisibility(View.INVISIBLE);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        //  Label
        final TextView firstName = (TextView) findViewById(R.id.firstName);
        final TextView lastName = (TextView) findViewById(R.id.lastName);
        final TextView email = (TextView) findViewById(R.id.email);
        final TextView address = (TextView) findViewById(R.id.address);
        final TextView city = (TextView) findViewById(R.id.city);
        final TextView pseudo = (TextView) findViewById(R.id.pseudo);
        final TextView tel = (TextView) findViewById(R.id.tel);
        final TextView birth = (TextView) findViewById(R.id.birth);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = getBirth();
        String time = sdf.format(date);        //  Label fill

        firstName.setText(UserAccess.getFirstName());
        lastName.setText(UserAccess.getLastName());
        email.setText("Email : " + UserAccess.getEmail());
        address.setText("Adresse : " + UserAccess.getAddress());
        city.setText("Ville : " + UserAccess.getPostalCode() + " " + UserAccess.getCity());
        pseudo.setText("Pseudo : " + UserAccess.getPseudo());
        tel.setText("Téléphone : " + UserAccess.getTel());
        birth.setText("Naissance : " + time);
        System.out.println("////////////////////////////////////////"+getBirth());



        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                startActivity(new Intent(SettingActivity.this, EditPageActivity.class));
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
            }
        });


        userPseudoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                Intent compteIntent = new Intent(SettingActivity.this, UserActivity.class);
                compteIntent.putExtra("IDUSER", UserAccess.getId());
                startActivity(compteIntent);
            }
        });


        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                startActivity(new Intent(SettingActivity.this, NotificationSettingsActivity.class));
            }
        });
        /*
        changePassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // open gallery
                startActivity(new Intent(SettingActivity.this, EditPasswordActivity.class));
            }
        });

        */
        // Initialize And Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.nav_setting);

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
                        startActivity(new Intent(getApplicationContext(), MyRequestActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_setting:
                        return true;
                }
                return false;
            }
        });


    }



}