package com.sailiksarkar.freshchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.PrimitiveIterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity
{

    private RecyclerView myFriendlist;
    private DatabaseReference friendsRef, userRef;
    private FirebaseAuth mAuth;
    private String  online_user_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");





        myFriendlist = (RecyclerView)  findViewById(R.id.friend_list);

        myFriendlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendlist.setLayoutManager(linearLayoutManager);


        DisplayAllfriends();




    }

    private void DisplayAllfriends()
    {
        super.onStart();
        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(friendsRef, Friends.class)
                        .build();




        FirebaseRecyclerAdapter<Friends, friendsViewholder>  firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Friends, friendsViewholder>(options) {
            @Override
            protected void onBindViewHolder( FriendsActivity.friendsViewholder holder, int position,  Friends model)

            {

                holder.setDate(model.getDate());



                        final String UserIds = getRef(position).getKey();
                userRef.child(UserIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot snapshot)

                    {
                        if (snapshot.exists())
                        {
                            final String userName = snapshot.child("fullname").getValue().toString();
                            final String profileImage = snapshot.child("profileimage").getValue().toString();

                            holder.setFullname(userName);
                            holder.setProfileimage(getApplicationContext(), profileImage);


                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)

                                {

                                    CharSequence  options[] = new CharSequence[]
                                            {

                                                    userName + "'s Profile",
                                                    "Send Message"

                                            };

                                    AlertDialog.Builder  builder = new AlertDialog.Builder(FriendsActivity.this);
                                    builder.setTitle("Select Option");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)

                                        {
                                            if(which == 0)
                                            {

                                                Intent profileIntent =  new Intent(FriendsActivity.this, PersonProfileActivity.class);
                                                profileIntent.putExtra("visit_user_id", UserIds);
                                                startActivity(profileIntent);

                                            }

                                            if (which == 1)

                                            {

                                                Intent chatIntent =  new Intent(FriendsActivity.this, ChatActivity.class);
                                                chatIntent.putExtra("visit_user_id", UserIds);
                                                chatIntent.putExtra("userName", userName);
                                                startActivity(chatIntent);

                                            }

                                        }
                                    });

                                    builder.show();


                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled( DatabaseError error) {

                    }
                });




            }

            @NonNull

            @Override
            public friendsViewholder onCreateViewHolder( ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_user_search_display_layout, parent, false);
                FriendsActivity.friendsViewholder viewHolder = new FriendsActivity.friendsViewholder(view);
                return viewHolder;
            }
        };

        myFriendlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public static class   friendsViewholder extends RecyclerView.ViewHolder
    {

        View mView;

        public friendsViewholder(View itemView)

        {
            super(itemView);

            mView = itemView;
        }


        public void setProfileimage(Context applicationContext, String profileimage)
        {

            CircleImageView myimagee = (CircleImageView) mView.findViewById(R.id.all_user_pro_image);

            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(myimagee);
        }



        public void setFullname(String fullname)
        {
            TextView myname = (TextView) mView.findViewById(R.id.all_user_profilefullname);
            myname.setText(fullname);

        }

        public void setDate(String date)
        {
            TextView friendsdate = (TextView) mView.findViewById(R.id.all_user_statussearch);
            friendsdate.setText("Friends since : "  + date);

        }
    }

}