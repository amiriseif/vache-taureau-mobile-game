package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {
    EditText mail,passwd,username;
    Button bregister;
    dbhelper db ;
    private FirebaseAuth auth;

    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        mail=findViewById(R.id.email);
        passwd=findViewById(R.id.password);
        username=findViewById(R.id.username);
        bregister=findViewById(R.id.register);
        db=new dbhelper(this);
        auth= FirebaseAuth.getInstance();
        bregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usern=username.getText().toString();
                String pass=passwd.getText().toString();
                String email=mail.getText().toString();


                Boolean insert;


                if(usern.equals("") ||pass.equals("")||email.equals("") || pass.length()<8 ){
                    if(pass.length()<8) {
                        Toast.makeText(register.this, "password should be at least 8 character!", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(register.this, "all fields are required", Toast.LENGTH_SHORT).show();
                    }
                }
                else{


                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (db.insertData(usern, pass)) {
                                    Toast.makeText(register.this, "Account successfully created", Toast.LENGTH_SHORT).show();
                                    db.creatfirestordoc(usern);
                                    db.insertScore(usern, 0);
                                }

                                startActivity(new Intent(getApplicationContext(), login.class));
                                finish();
                            }

                        else{
                                Toast.makeText(register.this, "error occured while creating account", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }});
    }




    public void login(View view) {
        startActivity(new Intent(this, login.class));
        finish();
    }

}