package com.sailiksarkar.freshchatapp;

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
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostActivity extends AppCompatActivity
{

    private Toolbar mToolbar;
    private RecyclerView mypostlist;
    private FirebaseAuth mAuth;
    private DatabaseReference postref , UserRef, LikesRef;
    private String currentuserid;

    Boolean likechecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        mAuth = FirebaseAuth .getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();

        postref = FirebaseDatabase.getInstance().getReference().child("Posts");
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");


        mToolbar = (Toolbar) findViewById(R.id.my_post_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Posts");



        mypostlist = (RecyclerView)  findViewById(R.id.my_all_post_list);

        mypostlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mypostlist.setLayoutManager(linearLayoutManager);



        displaymyownpost();

    }

    private void displaymyownpost()

    {
        Query mypostquery = postref.orderByChild("uid")
            .startAt(currentuserid).endAt(currentuserid  +  "\uf8ff");


        super.onStart();
        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setQuery(mypostquery, Post.class)
                        .build();


        FirebaseRecyclerAdapter<Post, mypostviewHolder > firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Post, mypostviewHolder>(options) {
            @Override
            protected void onBindViewHolder( MyPostActivity.mypostviewHolder holder, int position,  Post model)

            {

                final   String PostKey = getRef(position).getKey();

                holder.setFullname(model.getFullname());
                holder.setTime(model.getTime());
                holder.setDate(model.getDate());
                holder.setDescription(model.getDescription());
                holder.setProfileimage(getApplicationContext(), model.getProfileimage());
                holder.setPostimage(getApplicationContext(), model.getPostimage());



                holder.setlikebuttonstatus(PostKey);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)

                    {

                        Intent clickPostIntent = new Intent(MyPostActivity.this,ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey", PostKey);
                        startActivity(clickPostIntent);

                    }
                });


                holder.commentpostbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)

                    {
                        Intent commentsIntent = new Intent(MyPostActivity.this,CommentsActivity.class);
                        commentsIntent.putExtra("PostKey", PostKey);
                        startActivity(commentsIntent);

                    }
                });

                holder.likepostbtn.setOnClickListener(new View.OnClickListener()

                {
                    @Override
                    public void onClick(View v)
                    {

                        likechecker = true;
                        LikesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange( DataSnapshot snapshot)
                            {
                                if (likechecker.equals(true))
                                {
                                    if (snapshot.child(PostKey).hasChild(currentuserid))
                                    {
                                        LikesRef.child(PostKey).child(currentuserid).removeValue();
                                        likechecker = false;
                                    }
                                    else
                                    {
                                        LikesRef.child(PostKey).child(currentuserid).setValue(true);
                                        likechecker = false;

                                    }
                                }

                            }

                            @Override
                            public void onCancelled( DatabaseError error) {

                            }
                        });


                    }
                });





            }

            @NonNull

            @Override
            public mypostviewHolder onCreateViewHolder( ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_post_layout, parent, false);
                MyPostActivity.mypostviewHolder viewHolder = new MyPostActivity.mypostviewHolder(view);
                return viewHolder;

            }
        };

        mypostlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }


    public static class  mypostviewHolder extends RecyclerView.ViewHolder

    {

        View mView;

        ImageButton likepostbtn , commentpostbtn ;
        TextView Displaynooflikes;
        int countlikes;
        String currentuserId;
        DatabaseReference Likesref;


        public mypostviewHolder( View itemView)

        {
            super(itemView);
            mView = itemView;

            likepostbtn = (ImageButton) mView.findViewById(R.id.like_btn);
            commentpostbtn = (ImageButton) mView.findViewById(R.id.comment_btn);
            Displaynooflikes = (TextView) mView.findViewById(R.id.count_like);

            Likesref = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentuserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }


        public void  setlikebuttonstatus(final String PostKey)
        {
            Likesref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot snapshot)
                {

                    if (snapshot.child(PostKey).hasChild(currentuserId))
                    {
                        countlikes = (int) snapshot.child(PostKey).getChildrenCount();
                        likepostbtn.setImageResource(R.drawable.heart);
                        Displaynooflikes.setText((Integer.toString(countlikes)+(" Likes")));
                    }

                    else
                    {
                        countlikes = (int) snapshot.child(PostKey).getChildrenCount();
                        likepostbtn.setImageResource(R.drawable.whitedislike);
                        Displaynooflikes.setText((Integer.toString(countlikes)+(" Likes")));

                    }
                }

                @Override
                public void onCancelled( DatabaseError error) {

                }
            });
        }


        public void setFullname(String fullname)
        {
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setProfileimage(Context applicationContext, String profileimage)
        {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(image);
        }

        public void setTime(String time)
        {

            TextView postTime = (TextView) mView.findViewById(R.id.post_time);
            postTime.setText("  " + time);
        }

        public void setDate(String date)
        {
            TextView postDate = (TextView) mView.findViewById(R.id.post_date);
            postDate.setText("  " + date);
        }


        public void setDescription(String description)
        {
            TextView postDescription = (TextView) mView.findViewById(R.id.post_description);
            postDescription.setText(description);

        }

        public void setPostimage(Context ctx, String postimage)
        {

            RoundedImageView imagePost = (RoundedImageView) mView.findViewById(R.id.post_IMAGE);
            Picasso.get().load(postimage).placeholder(R.drawable.profile).into(imagePost);


        }
    }
}