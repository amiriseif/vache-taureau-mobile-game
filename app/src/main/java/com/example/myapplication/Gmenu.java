package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Gmenu extends AppCompatActivity {

    Intent intent2;
    String username,email;
    Intent intent;
    TextView userlabel;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userid;
    ImageView profile;

    dbhelper db;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmenu);


        preferences = getSharedPreferences("checkbox", MODE_PRIVATE);

        db = new dbhelper(this);
        profile = findViewById(R.id.profilepic);
        userlabel = findViewById(R.id.userlabel);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userid = fauth.getCurrentUser().getUid();
        username = preferences.getString("username", "");
        email = preferences.getString("email", "");
        userlabel.setText(username);
        profile.setImageResource(db.geticonsrc(email));
    }

    public void profilpicture(View view) {

        intent=new Intent(Gmenu.this,profile.class);

        startActivity(intent);
        finish();


    }

    public void finish(View view) {
        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("remember","false");
        editor.apply();
        finish();
    }

    public void begame(View view) {
        intent2 = new Intent(Gmenu.this, game.class);


        startActivity(intent2);

    }


    public void setting(View view) {
        intent2 = new Intent(Gmenu.this, setting.class);

        startActivity(intent2);


    }

    public void rank(View view) {
        intent2 = new Intent(Gmenu.this, ranking.class);


        startActivity(intent2);
    }
}