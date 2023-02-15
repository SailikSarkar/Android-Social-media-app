package com.sailiksarkar.freshchatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashscreenActivity extends AppCompatActivity {


    private static int  SPLASH_SCREEN = 5000;

    Animation  topanim, buttomamin, leftanim, rightanim, heartbeat;

    ImageView image;
    TextView co, de, textap, tagline ;

    ProgressBar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);


        topanim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        buttomamin = AnimationUtils.loadAnimation(this,R.anim.buttom_animation);
        leftanim = AnimationUtils.loadAnimation(this,R.anim.left_anim);
        rightanim = AnimationUtils.loadAnimation(this,R.anim.right_anim);
        heartbeat = AnimationUtils.loadAnimation(this,R.anim.heart_beat);





        // hooks


        image= findViewById(R.id.imageView99);
        co= findViewById(R.id.textView44);
        de= findViewById(R.id.textView55);
        textap= findViewById(R.id.textView66);
        tagline= findViewById(R.id.textView77);
        loading= findViewById(R.id.progressBar777);


        image.setAnimation(buttomamin);
        tagline.setAnimation(heartbeat);
        co.setAnimation(leftanim);
        de.setAnimation(rightanim);
        textap.setAnimation(topanim);







        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashscreenActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

            }
        },SPLASH_SCREEN);


    }



}