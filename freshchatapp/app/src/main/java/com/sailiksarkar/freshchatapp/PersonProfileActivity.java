package com.sailiksarkar.freshchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {


    private TextView userName, userfullname, userstatus, usercountry, usergender, userrelation, userdob;
    private CircleImageView Userprofileimage;
    private Button SendFriendRequestButton, DeclineFriendRequestButton ;

    private DatabaseReference profileUserRef, friendreqRef, usersRef, FriendsRef;
    private FirebaseAuth mAuth;


    private String senderuserid, recieveruserid, CURRENT_STATUS, saveCurrentDate;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();
        senderuserid = mAuth.getCurrentUser().getUid();
        friendreqRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");



        recieveruserid = getIntent().getExtras().get("visit_user_id").toString();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        Initializefields();


        usersRef.child(recieveruserid).addValueEventListener(new ValueEventListener() {
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


                    maintananceofbutton();


                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
        DeclineFriendRequestButton.setEnabled(false);

        if (!senderuserid.equals(recieveruserid))
        {

            SendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)

                {
                    SendFriendRequestButton.setEnabled(false);
                    
                    if (CURRENT_STATUS.equals("not_friends"))
                    {
                        sendfriendrequesttoperson();
                    }

                    if (CURRENT_STATUS.equals("request_sent"))
                    {
                        cancelfriendreq();

                    }
                    if (CURRENT_STATUS.equals("request_recieved"))
                        
                    {
                        Acceptfriendrequest();
                        
                        
                        
                    }

                    if (CURRENT_STATUS.equals("friends"))
                    {
                        unfriendexixtingfriend();
                        
                    }

                }
            });

        }
        else
        {
            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
        }


    }

    private void unfriendexixtingfriend()

    {

        FriendsRef.child(senderuserid).child(recieveruserid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull  Task<Void> task)

                    {

                        if (task.isSuccessful())

                        {
                            FriendsRef.child(recieveruserid).child(senderuserid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull  Task<Void> task)

                                        {

                                            if (task.isSuccessful())

                                            {

                                                SendFriendRequestButton.setEnabled(true);
                                                CURRENT_STATUS = "not_friends";
                                                SendFriendRequestButton.setText("Send Friend Request");

                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);
                                            }

                                        }
                                    });
                        }

                    }
                });




    }

    private void Acceptfriendrequest()
    {



        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM=yyy");

        saveCurrentDate = currentDate.format(calForDate.getTime());


        FriendsRef.child(senderuserid).child(recieveruserid).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull  Task<Void> task)

                    {

                        if (task.isSuccessful())

                        {

                            FriendsRef.child(recieveruserid).child(senderuserid).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull  Task<Void> task)

                                        {

                                            if (task.isSuccessful())
                                            {

                                                friendreqRef.child(senderuserid).child(recieveruserid)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull  Task<Void> task)

                                                            {

                                                                if (task.isSuccessful())

                                                                {
                                                                    friendreqRef.child(recieveruserid).child(senderuserid)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull  Task<Void> task)

                                                                                {

                                                                                    if (task.isSuccessful())

                                                                                    {

                                                                                        SendFriendRequestButton.setEnabled(true);
                                                                                        CURRENT_STATUS = "friends";
                                                                                        SendFriendRequestButton.setText("Unfriend");

                                                                                        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                                                        DeclineFriendRequestButton.setEnabled(false);
                                                                                    }

                                                                                }
                                                                            });
                                                                }

                                                            }
                                                        });


                                            }


                                        }
                                    });

                        }


                    }
                });


    }

    private void cancelfriendreq()

    {

        friendreqRef.child(senderuserid).child(recieveruserid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull  Task<Void> task)

                    {

                        if (task.isSuccessful())

                        {
                            friendreqRef.child(recieveruserid).child(senderuserid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull  Task<Void> task)

                                        {

                                            if (task.isSuccessful())

                                            {

                                                SendFriendRequestButton.setEnabled(true);
                                                CURRENT_STATUS = "not_friends";
                                                SendFriendRequestButton.setText("Send Friend Request");

                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);
                                            }

                                        }
                                    });
                        }

                    }
                });



    }

    private void maintananceofbutton()

    {





        friendreqRef.child(senderuserid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot snapshot)

                    {
                        if (snapshot.hasChild(recieveruserid))
                        {
                            String request_type = snapshot.child(recieveruserid).child("request_type").getValue().toString();

                            if (request_type.equals("sent"))
                            {
                                CURRENT_STATUS = "request_sent";
                                SendFriendRequestButton.setText("Cancel Friend request");


                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestButton.setEnabled(false);
                            }

                            else  if (request_type.equals("recieved"))

                            {
                                CURRENT_STATUS = "request_recieved";
                                SendFriendRequestButton.setText("Accept Friend Request ");

                                DeclineFriendRequestButton.setVisibility(View.VISIBLE);
                                DeclineFriendRequestButton.setEnabled(true);


                                DeclineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)

                                    {

                                        cancelfriendreq();  

                                    }
                                });

                            }

                        }

                        else
                        {
                            FriendsRef.child(senderuserid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange( DataSnapshot snapshot)

                                        {
                                            if (snapshot.hasChild(recieveruserid))

                                            {
                                                CURRENT_STATUS = "friends";
                                                SendFriendRequestButton.setText("Unfriend ");

                                                DeclineFriendRequestButton.setVisibility(View.VISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);

                                            }

                                        }

                                        @Override
                                        public void onCancelled( DatabaseError error) {

                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error)

                    {

                    }
                });

    }

    private void sendfriendrequesttoperson()

    {

        friendreqRef.child(senderuserid).child(recieveruserid)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull  Task<Void> task)

                    {

                        if (task.isSuccessful())

                        {
                            friendreqRef.child(recieveruserid).child(senderuserid)
                                    .child("request_type").setValue("recieved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull  Task<Void> task)

                                        {

                                            if (task.isSuccessful())

                                            {

                                                SendFriendRequestButton.setEnabled(true);
                                                CURRENT_STATUS = "request_sent";
                                                SendFriendRequestButton.setText("Cancel Friend Request");

                                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRequestButton.setEnabled(false);
                                            }

                                        }
                                    });
                        }

                    }
                });
    }

    private void Initializefields()
    {


        userName = (TextView) findViewById(R.id.person_profile_username);
        userfullname = (TextView) findViewById(R.id.person_full_name);
        userstatus = (TextView) findViewById(R.id.person_profile_status);
        usercountry = (TextView) findViewById(R.id.person_profile_country);
        usergender = (TextView) findViewById(R.id.person_profile_gender);
        userrelation = (TextView) findViewById(R.id.person_relationship);
        userdob = (TextView) findViewById(R.id.person_profile_dob);
        Userprofileimage = (CircleImageView) findViewById(R.id.person_profile_pic);

        SendFriendRequestButton = (Button) findViewById(R.id.person_add_friend);
        DeclineFriendRequestButton = (Button) findViewById(R.id.person_cancel_friend);

        CURRENT_STATUS = "not_friends";
    }
}