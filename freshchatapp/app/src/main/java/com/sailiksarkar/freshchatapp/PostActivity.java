
        package com.sailiksarkar.freshchatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


        public class PostActivity extends AppCompatActivity



{


    private Toolbar mToolbar;
    private ProgressDialog loadingbar;

    private ImageButton  SelectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;


    public static final int Gallery_Pick=1;
    private Uri ImageUri;
    String currentUserID;

    private  String Description;
    private StorageReference PostImagesReference,UserPostImageRef;

    private DatabaseReference UsersRef, PostRef;
    private FirebaseAuth mAuth;

    private  String saveCurrentDate,saveCurrentTime,PostRandomName,downloadUrl, current_uder_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        mAuth = FirebaseAuth.getInstance();
        current_uder_id = mAuth.getCurrentUser().getUid();
        UserPostImageRef = FirebaseStorage.getInstance().getReference(). child("Profile Images");


        PostImagesReference = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");


        SelectPostImage = (ImageButton) findViewById(R.id.select_post_image);
        UpdatePostButton = (Button )  findViewById(R.id.update_post_button);
        PostDescription = (EditText) findViewById(R.id.post_desctiption);
        loadingbar = new ProgressDialog(this);



        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");


        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            
            {
                
                OpenGallery();
                
            }
        });



        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            
            {
                
                ValidatePostInfo();
            }
        });

    }

    private void ValidatePostInfo() 
    
    {
        
         Description = PostDescription.getText().toString();
        
        if (ImageUri == null)
        {
            Toast.makeText(this, "Please select a image ..", Toast.LENGTH_SHORT).show();
        }

       else if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Please write something about the image ..", Toast.LENGTH_SHORT).show();
        }

       else
        {

            loadingbar.setTitle("Add new post");
            loadingbar.setMessage("Please wait , while we are Updating your new post");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);

            StoringImageToFirebaseStorage();
        }
        
    }

    private void StoringImageToFirebaseStorage()

    {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM=yyy");

        saveCurrentDate = currentDate.format(calForDate.getTime());




        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");

        saveCurrentTime = currentTime.format(calForDate.getTime());


        PostRandomName = saveCurrentDate + saveCurrentTime;





        StorageReference filepath = PostImagesReference.child("Post Images").child(ImageUri.getLastPathSegment() + PostRandomName + ".jpg");

        filepath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull  Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            DatabaseReference postimagestore =FirebaseDatabase.getInstance().getReference().child("Posts").child(current_uder_id + PostRandomName);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("postimage", String.valueOf(uri));


                            postimagestore.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    if(task.isSuccessful())

                                    {
                                        Toast.makeText(PostActivity.this, "finally post uploaded..", Toast.LENGTH_SHORT).show();
                                        SavingPostinformationtoDatabase();
                                    }
                                    else {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(PostActivity.this, "Error Occoured..." + message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });

                }
            }

        });


    }

    private void SavingPostinformationtoDatabase()
    {

        UsersRef.child(current_uder_id) .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot)

            {

                if(dataSnapshot.exists())
                {
                    String userFullname  = dataSnapshot.child("fullname").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileimage").getValue().toString();

                    HashMap postMap = new HashMap();
                    postMap.put("uid", current_uder_id);
                    postMap.put("date", saveCurrentDate);
                    postMap.put("time", saveCurrentTime);
                    postMap.put("desctiption",Description );

                    postMap.put("profileimage", userProfileImage);
                    postMap.put("fullname", userFullname);

                    PostRef.child(current_uder_id + PostRandomName).updateChildren(postMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull  Task task)
                                {



                                    if(task.isSuccessful())
                                    {

                                        SendUserToMainActivity();
                                        Toast.makeText(PostActivity.this, "New post is updated successfully", Toast.LENGTH_SHORT).show();
                                    loadingbar.dismiss();

                                    }

                                    else
                                    {
                                        Toast.makeText(PostActivity.this, "Error occoured...", Toast.LENGTH_SHORT).show();

                                        loadingbar.dismiss();
                                    }
                                }
                            });





                }
            }

            @Override
            public void onCancelled( DatabaseError daraError) {

            }
        });

    }

    private void OpenGallery()
    {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);

        }



        }






    @Override
    public boolean onOptionsItemSelected(MenuItem item)

    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            SendUserToMainActivity();
        }



        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity()

    {
        Intent mainIntent = new Intent(PostActivity.this,MainActivity.class);
        startActivity(mainIntent);


    }


}