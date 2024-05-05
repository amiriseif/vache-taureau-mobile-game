package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class setting extends AppCompatActivity {

    Button save;
    Intent intent;
    int durée, oldduree;
    int nbattempt, oldnbattempt;
    int Glength, oldlength;
    String username;
    String email;
    RadioGroup time,attempt,length;
    RadioButton checked;
    Intent intent2;
    dbhelper db ;
    RadioButton rb1,rb2,rb3;
    Button delete;
    FirebaseAuth auth;
    FirebaseFirestore fstore;
    String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        auth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        intent2 = getIntent();
        username = intent2.getStringExtra("username");
        email=intent2.getStringExtra("email");
        db = new dbhelper(this);

        time = findViewById(R.id.radiogroup1);
        attempt = findViewById(R.id.radiogroup2);
        length = findViewById(R.id.radiogroup3);
        save = findViewById(R.id.save);
        delete=findViewById(R.id.Daccount);
        oldduree =db.getoldtime(email);
        oldnbattempt =db.getatt(email);
        oldlength =db.getlen(email);



        for(int j=0;j<4;++j){
            rb1 = (RadioButton) time.getChildAt(j);
            if(Integer.parseInt(rb1.getText().toString())== oldduree){
                rb1.setChecked(true);
            }
            rb2 = (RadioButton) attempt.getChildAt(j);
            if(Integer.parseInt(rb2.getText().toString())== oldnbattempt){
                rb2.setChecked(true);
            }
            rb3 = (RadioButton) length.getChildAt(j);
            if(Integer.parseInt(rb3.getText().toString())== oldlength){
                rb3.setChecked(true);
            }
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int Tclickedbutton = time.getCheckedRadioButtonId();
                int Aclickedbutton = attempt.getCheckedRadioButtonId();
                int Lclickedbutton = length.getCheckedRadioButtonId();
                if (Tclickedbutton != -1) {
                    checked = findViewById(Tclickedbutton);
                    durée = Integer.parseInt(checked.getText().toString());
                    db.updatetime(durée, email);
                }
                if (Aclickedbutton != -1) {
                    checked = findViewById(Aclickedbutton);
                    nbattempt = Integer.parseInt(checked.getText().toString());
                    db.updatenbatt(nbattempt, email);
                }
                if (Lclickedbutton != -1) {
                    checked = findViewById(Lclickedbutton);
                    Glength = Integer.parseInt(checked.getText().toString());
                    db.updatelen(Glength, email);
                }
                finish();

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(setting.this);
                builder.setMessage("Are you sure you want to delete your account?");
                builder.setTitle("Delete Account");
                builder.setCancelable(true);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseUser user=auth.getCurrentUser();
                        userid=user.getUid();
                        fstore.collection("users").document(userid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(setting.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                                db.deleteaccount(username);
                                                finishAffinity();
                                                intent=new Intent(setting.this, MainActivity.class);
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

}

