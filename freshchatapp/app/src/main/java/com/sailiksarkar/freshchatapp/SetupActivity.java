package com.sailiksarkar.freshchatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.bumptech.glide.Glide;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity

{

    private EditText UserName, FullName, CountryName;
    private Button SaveInmormationbutton;
    private CircleImageView ProfileImage;

   private FirebaseAuth  mAuth;
    private DatabaseReference UserRef;

    private StorageReference UserProfileImageRef;


    String currentUserID;
    final static int Gallery_Pick=1;

    private ProgressDialog Loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

mAuth = FirebaseAuth.getInstance();
currentUserID = mAuth.getCurrentUser().getUid();
UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        Loadingbar = new ProgressDialog(this);

        UserProfileImageRef = FirebaseStorage.getInstance().getReference(). child("Profile Images");

        UserName = (EditText) findViewById(R.id.setup_username);
     FullName = (EditText) findViewById(R.id.setup_full_name);
        CountryName = (EditText) findViewById(R.id.setup_country_name);

        SaveInmormationbutton = (Button) findViewById(R.id.setup_information_button);

        ProfileImage = (CircleImageView) findViewById(R.id.setup_profile_image);



        SaveInmormationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)

            {

                SaveAccountSetupInformation();

            }
        });



        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);

            }
        });


        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(  DataSnapshot dataSnapshot)
            {

                if (dataSnapshot.exists())
                {
                    String images = dataSnapshot.child("profileimage").getValue().toString();



                    Picasso.get().load(images).placeholder(R.drawable.profile).into(ProfileImage);

                }
                else {

                    Toast.makeText(SetupActivity.this, "please provide a image", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

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

                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg" );


                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)

                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(SetupActivity.this,"Profile image stored successfully ...", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult() .getStorage().getDownloadUrl().toString();


                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri)

                                {

                                    DatabaseReference imagestore = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

                                    HashMap<String,String> hashMap = new HashMap<>();
                                    hashMap.put("profileimage", String.valueOf(uri));


                                    imagestore.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Toast.makeText(SetupActivity.this,"finally uploaded..", Toast.LENGTH_SHORT).show();


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
                Toast.makeText(SetupActivity.this,"Error occoured:  Image cant be cropped try again ..." , Toast.LENGTH_SHORT).show();

                Loadingbar.dismiss();
            }


        }

    }

    private void SaveAccountSetupInformation()
    {
        String username = UserName.getText().toString();
        String fullname = FullName.getText().toString();
        String country = CountryName.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Please write your username ...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this,"Please write your fullname ...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(country))
    {
        Toast.makeText(this,"Please write your country ...", Toast.LENGTH_SHORT).show();
    }
        else {

            Loadingbar.setTitle("Saving data");
            Loadingbar.setMessage("Please wait , while we are saving your data");
            Loadingbar.show();
            Loadingbar.setCanceledOnTouchOutside(true);


            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("country", country);
            userMap.put("status", "HEY I AM USING SAILIK SARKAR'S  FRESHCHAT APP ");
            userMap.put("gender", "none");
            userMap.put("dob", "none");
            userMap.put("relatonshipstatus", "none");

            UserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful())

                    {
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Your account has been created successfully  ...", Toast.LENGTH_LONG).show();
                        Loadingbar.dismiss();
                    } else {

                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error occoured  ..." + message, Toast.LENGTH_LONG).show();

                        Loadingbar.dismiss();

                    }

                }
            });

        }



    }

    private void SendUserToMainActivity()

    {


        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
}