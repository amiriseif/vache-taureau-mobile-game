package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class profilepicture extends AppCompatActivity {
    Intent intent,intent2;
    dbhelper db;
    String email;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepicture);
        db=new dbhelper(this);
        preferences = getSharedPreferences("checkbox", MODE_PRIVATE);

//        email=intent.getStringExtra("username");
        email = preferences.getString("email", "");



    }

    public void changeicon(View view) {



            int imgsrc;
            ImageView profileImage = (ImageView) view;

            int imageViewId = profileImage.getId();

            switch (imageViewId) {
                case R.id.icon1:
                    imgsrc = R.drawable.icon6;
                    break;
                case R.id.icon2:
                    imgsrc = R.drawable.icon2;
                    break;
                case R.id.icon3:
                    imgsrc = R.drawable.icon3;
                    break;
                case R.id.icon4:
                    imgsrc = R.drawable.icon1;
                    break;
                case R.id.icon5:
                    imgsrc = R.drawable.icon4;
                    break;
                case R.id.icon6:
                    imgsrc = R.drawable.icon5;
                    break;
                case R.id.icon7:
                    imgsrc = R.drawable.icon8;
                    break;
                case R.id.icon8:
                    imgsrc = R.drawable.icon7;
                    break;
                default:
                    imgsrc = R.drawable.icon1;
                    break;
            }
            intent2=new Intent(profilepicture.this,profile.class);
            db.updateicon(email,imgsrc);

            startActivity(intent2);
            finish();
        }





}