package com.sailiksarkar.freshchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity
{


    private Button Resetpasswordsendmailbtn;
    private EditText Resetemailinput;

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.forgot_password_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reset Password");


        Resetpasswordsendmailbtn = (Button)  findViewById(R.id.reset_password_btn);
        Resetemailinput = (EditText)  findViewById(R.id.enter_reset_email);



        Resetpasswordsendmailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String  useremail = Resetemailinput.getText().toString();

                if (TextUtils.isEmpty(useremail))
                {
                    Toast.makeText(ResetPasswordActivity.this, "Please write your valid email address", Toast.LENGTH_SHORT).show();
                }

                else
                {

                    mAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)

                        {

                            if (task.isSuccessful())
                                
                            {
                                Toast.makeText(ResetPasswordActivity.this, "Email has been sent , Please check your mail  ", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));


                            }

                            else
                            {

                                String message =  task.getException().getMessage();

                                Toast.makeText(ResetPasswordActivity.this, "Error occoured " + message, Toast.LENGTH_SHORT).show();


                            }
                                

                        }
                    });
                }


            }
        });

    }
}
