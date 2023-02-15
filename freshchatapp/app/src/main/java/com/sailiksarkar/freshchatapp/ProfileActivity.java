package com.sailiksarkar.freshchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity


{

    private TextView userName, userfullname, userstatus, usercountry, usergender, userrelation, userdob;
    private CircleImageView Userprofileimage;
    private DatabaseReference profileUserRef, friendsref, postref;
    private FirebaseAuth mAUTH;
    private Button mypost, myfriends;

    private String currentuserid;
    private int countfriends = 0, countpost = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAUTH = FirebaseAuth.getInstance();
        currentuserid = mAUTH.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserid);
        friendsref = FirebaseDatabase.getInstance().getReference().child("Friends");
        postref = FirebaseDatabase.getInstance().getReference().child("Posts");

        userName = (TextView) findViewById(R.id.my_profile_username);
        userfullname = (TextView) findViewById(R.id.my_profile_name);
        userstatus = (TextView) findViewById(R.id.my_profile_status);
        usercountry = (TextView) findViewById(R.id.my_profile_country);
        usergender = (TextView) findViewById(R.id.my_profile_gender);
        userrelation = (TextView) findViewById(R.id.my_profile_relationship);
        userdob = (TextView) findViewById(R.id.my_profile_dob);
        Userprofileimage = (CircleImageView) findViewById(R.id.my_profile_pic);


        myfriends = (Button) findViewById(R.id.my_friends_btn);
        mypost = (Button) findViewById(R.id.my_post_btn);


        myfriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {

                SendUserTofriendActivity();


            }
        });

        mypost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {

                SendUserToMypostActivity();


            }
        });


        postref.orderByChild("uid")
                .startAt(currentuserid).endAt(currentuserid  +  "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot)

                    {

                        if (snapshot.exists())

                        {

                            countpost = (int ) snapshot.getChildrenCount();
                            mypost.setText(Integer.toString(countpost) + "  Posts");

                        }

                        else

                        {

                            mypost.setText("0  Posts ");

                        }

                    }

                    @Override
                    public void onCancelled( DatabaseError error) {

                    }
                });


    friendsref.child(currentuserid).addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange( DataSnapshot snapshot)

    {
        if (snapshot.exists())
        {

            countfriends = (int) snapshot.getChildrenCount();
            myfriends.setText(Integer.toString(countfriends ) + "  Friends");

        }

        else
        {
            myfriends.setText(" 0 Friends");



        }

    }

    @Override
    public void onCancelled(DatabaseError error) {

    }
});


        profileUserRef.addValueEventListener(new ValueEventListener() {
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

                    Picasso.get().load(myProfleimage).placeholder(R.drawable.man).into(Userprofileimage);

                    userName .setText( "@ : " +    myusername);
                    userfullname .setText("FULL NAME : "   +myfullname);
                    userstatus .setText(mystatus);
                    usercountry .setText("COUNTRY : "   +mycountry);
                    usergender .setText("GENDER : "   +mygender);
                    userrelation .setText("RELATION : "   +myrelation);

                    userdob .setText( "DOB : " + mydob);


                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void SendUserTofriendActivity() {

        Intent friendIntent = new Intent ( ProfileActivity.this,FriendsActivity.class);

        startActivity(friendIntent);

    }

    private void SendUserToMypostActivity() {

        Intent friendIntent = new Intent ( ProfileActivity.this,MyPostActivity.class);

        startActivity(friendIntent);

    }
}