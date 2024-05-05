package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    EditText email,password;
    Button login;
    dbhelper db ;

    String mail,pass;
    Intent intent;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth=FirebaseAuth.getInstance();
        email =findViewById(R.id.email);
        password=findViewById(R.id.logpass);
        login=findViewById(R.id.login);

        db=new dbhelper(this);
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mail= email.getText().toString();
                pass=password.getText().toString();
                if(mail.equals("") || pass.equals("")){
                    Toast.makeText(login.this, "all fields are required!", Toast.LENGTH_SHORT).show();}
                else {
                    auth.signInWithEmailAndPassword(mail,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(login.this, "login succeed!", Toast.LENGTH_SHORT).show();
                            intent = new Intent(login.this, Gmenu.class);
                            intent.putExtra("email", mail);
                            if(!db.checkusername(mail)){
                            db.insertData(mail,pass);}
                            startActivity(intent);
                            finish();

                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(login.this, "login failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    }
            }
        });




    }

    public void signin(View view) {
        startActivity(new Intent(this, register.class));
        finish();
    }


    public void resetpassword(View view) {
        mail= email.getText().toString();
        if(mail.equals("")){
            Toast.makeText(login.this, "write your mail in email field!", Toast.LENGTH_SHORT).show();
        }else{
            auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(login.this, "if email with this account is set an email will be received!", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(login.this, "email not found!", Toast.LENGTH_SHORT).show();

                }
            });
        }


    }
}