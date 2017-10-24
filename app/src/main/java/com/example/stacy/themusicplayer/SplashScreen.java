package com.example.stacy.themusicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread background=new Thread(){
            public void run(){
                try{
                    sleep(2*1000);
                    Intent ob=new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(ob);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        background.start(); 
    }
}
