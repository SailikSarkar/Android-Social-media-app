package com.sailiksarkar.freshchatapp;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity
{

    private Toolbar mToolbar;

    private ImageButton Searchbuttn;
    private EditText  Searchimputtext;

    private RecyclerView  searchresultlist;
    private DatabaseReference alluseradatabaseRef ,searchbynameee;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        alluseradatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");





        mToolbar = (Toolbar) findViewById(R.id.find_friends_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");




        searchresultlist = (RecyclerView) findViewById(R.id.search_result);
        searchresultlist.setHasFixedSize(true);
        searchresultlist.setLayoutManager(new LinearLayoutManager(this));

        Searchbuttn = (ImageButton) findViewById(R.id.searchButtonfrnd);
        Searchimputtext = (EditText) findViewById(R.id.search_box);



        Searchbuttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {

                String Searchboxinput = Searchimputtext.getText().toString();

                Searchpeopleandfriends(Searchboxinput);

            }
        });


    }

    private void Searchpeopleandfriends(String searchboxinput)

    { Toast.makeText(this, "SEARCHING...", Toast.LENGTH_LONG).show();
        Query searchpooplequary = alluseradatabaseRef.orderByChild("fullname")
                .startAt(searchboxinput).endAt(searchboxinput + "\uf8ff");

        onStart();

        FirebaseRecyclerOptions<FindFriends> options =
                new FirebaseRecyclerOptions.Builder<FindFriends>()
                        .setQuery(searchpooplequary, FindFriends.class)
                        .build();




        FirebaseRecyclerAdapter<FindFriends, Findfriendsviewholder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindFriends, Findfriendsviewholder>(options) {
            @Override
            protected void onBindViewHolder( @NonNull  Findfriendsviewholder holder, int position,  FindFriends model)

            {


                holder.setFullname(model.getFullname());
                holder.setStatus(model.getStatus());
                holder.setProfileimage(getApplicationContext(),model.getProfileimage());


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)

                    {
                        String visit_user_id = getRef(position).getKey();

                        Intent Profileintent = new Intent(FindFriendsActivity.this,PersonProfileActivity.class);
                        Profileintent.putExtra("visit_user_id", visit_user_id);

                        startActivity(Profileintent);

                    }
                });


            }

            @NonNull

            @Override
            public Findfriendsviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_user_search_display_layout, parent, false);
                Findfriendsviewholder viewHolder = new Findfriendsviewholder(view);
                return viewHolder;




            }
        };

        searchresultlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }


    public  static class Findfriendsviewholder extends RecyclerView.ViewHolder
    {
        View mView;

        public  Findfriendsviewholder (View itemView)
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

        public void setStatus(String status)
        {
            TextView mystatus = (TextView) mView.findViewById(R.id.all_user_statussearch);
            mystatus.setText(status);

        }

    }
}