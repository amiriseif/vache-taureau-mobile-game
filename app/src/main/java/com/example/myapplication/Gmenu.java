package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    Intent intent2,intent3;
    String username,email;
    Intent intent;
    TextView userlabel;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userid;
    ImageView profile;
    int imagesrc;
    dbhelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmenu);

        intent = getIntent();
        email = intent.getStringExtra("email");

        db = new dbhelper(this);
        profile = findViewById(R.id.profilepic);
        userlabel = findViewById(R.id.userlabel);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userid = fauth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("users").document(userid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    username = value.getString("username");
                    userlabel.setText(username);
                    //profile.setImageResource(R.drawable.icon2);
                    Toast.makeText(Gmenu.this, String.valueOf(db.geticonsrc(email)), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Gmenu.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        profile.setImageResource(db.geticonsrc(email));
    }

    public void profilpicture(View view) {

        intent=new Intent(Gmenu.this,profilepicture.class);
        intent.putExtra("username", email);
        startActivity(intent);
        finish();

    }

    public void finish(View view) {
        finishAffinity();
        intent=new Intent(Gmenu.this,MainActivity.class);
        startActivity(intent);
    }

    public void begame(View view) {
        intent2 = new Intent(Gmenu.this, game.class);
        intent2.putExtra("username", username);
        intent2.putExtra("email",email);

        startActivity(intent2);

    }


    public void setting(View view) {
        intent2 = new Intent(Gmenu.this, setting.class);
        intent2.putExtra("username", username);
        intent2.putExtra("email", email);
        startActivity(intent2);


    }

    public void rank(View view) {
        intent2 = new Intent(Gmenu.this, ranking.class);
        intent2.putExtra("USERNAME", username);

        startActivity(intent2);
    }
}