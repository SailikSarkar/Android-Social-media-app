package com.sailiksarkar.freshchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestActivity extends AppCompatActivity

{

    private RecyclerView myrequestlist;

    private View requestsview;

    private DatabaseReference requestRef, usersref;
    private FirebaseAuth mAuth;
    private String currentuserid;
    private Button  acceptbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        acceptbtn = (Button) findViewById(R.id.recq_accept_btn);

        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();


        usersref = FirebaseDatabase.getInstance().getReference().child("Users");
        requestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");


        myrequestlist = (RecyclerView)  findViewById(R.id.request_LIST);
        myrequestlist.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myrequestlist.setLayoutManager(linearLayoutManager);


        DisplayAllrequest();

    }



    private void DisplayAllrequest()
    {
        super.onStart();


        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(requestRef.child(currentuserid), Post.class)
                .build();



        FirebaseRecyclerAdapter<Post, requestviewholder>  adapter
                = new FirebaseRecyclerAdapter<Post, requestviewholder>(options) {
            @Override
            protected void onBindViewHolder( @NotNull RequestActivity.requestviewholder holder, int position, @NonNull  Post model)

            {


                holder.itemView.findViewById(R.id.recq_accept_btn).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.recq_cancel_btn).setVisibility(View.VISIBLE);



                final String  list_user_id = getRef(position).getKey();


                DatabaseReference gettyperef = getRef(position).child("request_type");
                gettyperef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot)

                    {

                        if (snapshot.exists())

                        {

                            String type = snapshot.getValue().toString();

                            if (type.equals("recieved"))

                            {
                                usersref.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull  DataSnapshot snapshot)

                                    {

                                       if (snapshot.hasChild("profileimage"))
                                       {
                                           final  String requestusername = snapshot.child("fullname").getValue().toString();
                                           final  String requestuserstatus = snapshot.child("status").getValue().toString();
                                           final  String requestuserprofileimage = snapshot.child("profileimage").getValue().toString();



                                           holder.username.setText(requestusername);
                                           holder.userstatus.setText(requestuserstatus);


                                           Picasso.get().load(requestuserprofileimage).into(holder.profileimage);


                                       }

                                       else
                                       {


                                           final  String requestusername = snapshot.child("fullname").getValue().toString();
                                           final  String requestuserstatus = snapshot.child("status").getValue().toString();


                                           holder.username.setText(requestusername);
                                           holder.userstatus.setText(requestuserstatus);

                                       }










                                       holder.itemView.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v)

                                           {

                                               String visit_user_id = getRef(position).getKey();

                                               Intent Profileintent = new Intent(RequestActivity.this,PersonProfileActivity.class);
                                               Profileintent.putExtra("visit_user_id", visit_user_id);

                                               startActivity(Profileintent);

                                           }
                                       });


                                    }

                                    @Override
                                    public void onCancelled(@NonNull  DatabaseError error) {

                                    }
                                });

                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }


                });




            }

            @NonNull

            @Override
            public requestviewholder onCreateViewHolder( @NotNull ViewGroup parent, int viewType)
            {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_request_display_layout, parent, false);
                requestviewholder  holder = new requestviewholder(view);
                return  holder;

            }

        };



        myrequestlist.setAdapter(adapter);
        adapter.startListening();


    }

    public static class  requestviewholder extends  RecyclerView.ViewHolder

    {


        View mView ;


        TextView username, userstatus;
        CircleImageView profileimage;
        Button  acceptbtn,cancelbtn;


        public requestviewholder( View itemView)

        {


            super(itemView);
            mView = itemView;

            username = itemView.findViewById(R.id.request_profilefullname);
            userstatus = itemView.findViewById(R.id.request_status);
            profileimage = itemView.findViewById(R.id.request_pro_image);
            acceptbtn = itemView.findViewById(R.id.recq_accept_btn);
            cancelbtn = itemView.findViewById(R.id.recq_cancel_btn);


        }
    }
}