package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class profile extends AppCompatActivity {
    Intent intent, intent2;
    String email, username, fullnam;
    EditText emaillab, userlab;
    TextView fnlab; // Assuming fnlab is a TextView
    ImageView profpic;
    dbhelper db;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userid;
    String newEmail;
    String newUsername;
    SharedPreferences preferences;
    Button delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        delete=findViewById(R.id.delete);

        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        db = new dbhelper(this);
        username = preferences.getString("username", "");
        email = preferences.getString("email", "");
        emaillab = findViewById(R.id.email);
        userlab = findViewById(R.id.username);
        fnlab = findViewById(R.id.scoreprof); // Initialize fnlab
        profpic = findViewById(R.id.profile_picture);
        profpic.setImageResource(db.geticonsrc(email));
        userlab.setHint(username); // Set username directly since it's editable
        emaillab.setHint(email); // Set email as hint to prevent editing
        emaillab.setEnabled(false); // Disable the email EditText
        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userid = fauth.getCurrentUser().getUid();
        newUsername=username;
        newEmail=email;
        DocumentReference documentReference = fstore.collection("users").document(userid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    Long userScore = value.getLong("score");
                    fnlab.setText(String.valueOf(userScore));
                    // Update username in SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("userdata", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", username);
                    editor.apply();
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(profile.this);
                builder.setMessage("Are you sure you want to delete your account?");
                builder.setTitle("Delete Account");
                builder.setCancelable(true);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseUser user=fauth.getCurrentUser();
                        userid=user.getUid();
                        fstore.collection("users").document(userid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(profile.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                        db.deleteaccount(username);
                                        finishAffinity();
                                        intent=new Intent(profile.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });

                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            }
        });
    }



    public void changeicon(View view) {
        intent = new Intent(profile.this, profilepicture.class);

        startActivity(intent);
        finish();
    }

    public void gmenu(View view) {
        intent = new Intent(profile.this, Gmenu.class);

        startActivity(intent);
        finish();
    }

    public void saveProfile(View view) {
         newEmail = emaillab.getText().toString();
         newUsername = userlab.getText().toString();

        Map<String, Object> updates = new HashMap<>();
        if (!newEmail.isEmpty()) {
            updates.put("email", newEmail);
            Toast.makeText(profile.this, "email changed successfully!", Toast.LENGTH_SHORT).show();


        } else {
        }
        if (!newUsername.isEmpty()) {
            updates.put("username", newUsername);
            Toast.makeText(profile.this, "username changed successfully!", Toast.LENGTH_SHORT).show();


        } else {
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("remember", "true");
        editor.putString("username", newUsername);
        editor.putString("email", newEmail);
        editor.apply();
        DocumentReference documentReference = fstore.collection("users").document(userid);
        documentReference.set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Toast.makeText(profile.this, "Profile Updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(profile.this, "Error updating profile", Toast.LENGTH_SHORT).show());
        intent=new Intent(profile.this,Gmenu.class);
        db.updateUsername(email,newEmail);
        startActivity(intent);
        finish();
    }

}
