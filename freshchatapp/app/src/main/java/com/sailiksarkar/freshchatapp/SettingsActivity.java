package com.sailiksarkar.freshchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsActivity extends AppCompatActivity


{


    private Toolbar mToolbar;
    private EditText userName, userfullname, userstatus, usercountry, usergender, userrelation, userdob;
    private Button updateaccsettingsbtn;
    private CircleImageView userprofileimagesettings;
    private StorageReference UserProfileImageRef;

    private DatabaseReference settingsRef;
    private FirebaseAuth mAuth;
    private  String currentUserId;
    final static int Gallery_Pick=1;
    private ProgressDialog Loadingbar;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference(). child("Profile Images");

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        settingsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);




        mToolbar =  (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar() .setTitle("");
        getSupportActionBar() .setDisplayHomeAsUpEnabled(true);
        Loadingbar = new ProgressDialog(this);




        userName = (EditText) findViewById(R.id.settings_username);
        userfullname = (EditText) findViewById(R.id.settings_fullname);
        userstatus = (EditText) findViewById(R.id.settings_status);
        usercountry = (EditText) findViewById(R.id.settings_country);
        usergender = (EditText) findViewById(R.id.settings_gender);
        userrelation = (EditText) findViewById(R.id.settings_relationship);
        userdob = (EditText) findViewById(R.id.settings_dob);
        updateaccsettingsbtn = (Button) findViewById(R.id.settings_update_btn) ;

        userprofileimagesettings = (CircleImageView) findViewById(R.id.settings_profile_image);


        settingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot)

            {

                if (snapshot.exists())
                {
                    String myProfleimage = snapshot.child("profileimage").getValue().toString();
                    String myfullname = snapshot.child("fullname").getValue().toString();
                    String myusername = snapshot.child("username").getValue().toString();
                    String mycountry= snapshot.child("country").getValue().toString();
                    String mygender = snapshot.child("gender").getValue().toString();
                    String myrelation = snapshot.child("relatonshipstatus").getValue().toString();
                    String mydob = snapshot.child("dob").getValue().toString();
                    String mystatus = snapshot.child("status").getValue().toString();

                    Picasso.get().load(myProfleimage).placeholder(R.drawable.man).into(userprofileimagesettings);

                    userName .setText(myusername);
                    userfullname .setText(myfullname);
                    userstatus .setText(mystatus);
                    usercountry .setText(mycountry);
                    usergender .setText(mygender);
                    userrelation .setText(myrelation);

                    userdob .setText(mydob);


                }

            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });

        updateaccsettingsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                ValidateAccountInfo();
            }
        });


        userprofileimagesettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);


            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)

    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);


        }

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {


                Loadingbar.setTitle("Profile image");
                Loadingbar.setMessage("Please wait , while we are Updating your profileimage");
                Loadingbar.show();
                Loadingbar.setCanceledOnTouchOutside(true);


                Uri resultUri =  result.getUri();

                StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg" );


                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)

                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(SettingsActivity.this,"Profile image stored successfully ...", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult() .getStorage().getDownloadUrl().toString();


                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri)

                                {

                                    DatabaseReference imagestore = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

                                    HashMap<String,String> hashMap = new HashMap<>();
                                    hashMap.put("profileimage", String.valueOf(uri));


                                    imagestore.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Toast.makeText(SettingsActivity.this,"finally uploaded..", Toast.LENGTH_SHORT).show();


                                        }
                                    });



                                }
                            });



                        }
                    }
                });


            }
            else
            {
                Toast.makeText(SettingsActivity.this,"Error occoured:  Image cant be cropped try again ..." , Toast.LENGTH_SHORT).show();

                Loadingbar.dismiss();
            }


        }

    }



    private void ValidateAccountInfo()
    {

        String username =  userName.getText().toString();
        String usefullnamee =  userfullname.getText().toString();
        String usersstatus =  userstatus.getText().toString();
        String userscountry =  usercountry.getText().toString();
        String usersgender =  usergender.getText().toString();
        String usersrelation =  userrelation.getText().toString();
        String usersdob =  userdob.getText().toString();


        if (TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "please write your username ", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(usefullnamee))
        {
            Toast.makeText(this, "please write your full name ", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(usersstatus))
        {
            Toast.makeText(this, "please write your status  ", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(userscountry))
        {
            Toast.makeText(this, "please write your country ", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(usersgender))
        {
            Toast.makeText(this, "please write your gender ", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(usersrelation))
        {
            Toast.makeText(this, "please write your relationship status ", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(usersdob))
        {
            Toast.makeText(this, "please write your DOB ", Toast.LENGTH_SHORT).show();
        }
        else
        {
            UpdateAccInfo(username, usefullnamee, usersstatus, userscountry, usersgender, usersrelation, usersdob);

        }
    }

    private void UpdateAccInfo(String username, String usefullnamee, String usersstatus, String userscountry, String usersgender, String usersrelation, String usersdob)
    {
        HashMap UserMap = new HashMap();

        UserMap.put("username", username );
        UserMap.put("fullname:", usefullnamee );
        UserMap.put("status", usersstatus );
        UserMap.put("country", userscountry );
        UserMap.put("gender", usersgender );
        UserMap.put("relatonshipstatus", usersrelation );
        UserMap.put("dob", usersdob );

        settingsRef.updateChildren(UserMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete( Task task) 
            
            {
                if (task.isSuccessful())
                {
                    SendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this, "Account Settings Updated successfully ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(SettingsActivity.this, "Error occoured while updating information . please check your internet connection" , Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    private void SendUserToMainActivity()
    {

        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
}