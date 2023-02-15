package com.sailiksarkar.freshchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.icu.text.Transliterator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

private NavigationView navigationView;
private DrawerLayout drawerLayout;
private ActionBarDrawerToggle actionBarDrawerToggle;
private RecyclerView postlist;
private Toolbar  mToolbar;



    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private ImageButton AddNewPostButton;
    Boolean likechecker = false;

private FirebaseAuth mAuth;

    String currentUserID;
private DatabaseReference UserRef, PostRef, LikesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth =  FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");


        mToolbar = (Toolbar)  findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("                        HOME");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        AddNewPostButton = (ImageButton ) findViewById(R.id.add_new_post_button);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_Close);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View  navView = navigationView.inflateHeaderView(R.layout.navigation_header);


        postlist = (RecyclerView) findViewById(R.id.all_users_post_list);
        postlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postlist.setLayoutManager(linearLayoutManager);

        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);






        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot datasnapshot)

            {

                if(datasnapshot.exists())
                {
                    if(datasnapshot.hasChild("fullname"))
                    {
                        String fullname = datasnapshot.child("fullname").getValue().toString();
                        NavProfileUserName.setText(fullname);
                    }

                    if (datasnapshot.hasChild("profileimage"))
                    {


                        String image = datasnapshot.child("profileimage").getValue().toString();

                        Picasso.get().load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                    }

                    else
                    {
                        Toast.makeText(MainActivity.this, "Profile name do not exists", Toast.LENGTH_SHORT).show();
                    }

                    

                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError databaseerror) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                UserMenuSelector(item);
                return false;

            }
        });

        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToPostActivity();

            }
        });

DisplayAllUsersPost();


    }

    private void DisplayAllUsersPost()

    {
        super.onStart();
        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(PostRef, Post.class)
                .build();

        FirebaseRecyclerAdapter<Post, PostViewHolder> adapter =
             new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
                 @Override
                 protected void onBindViewHolder(@NonNull  MainActivity.PostViewHolder holder, int position, @NonNull  Post model)
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

                             Intent clickPostIntent = new Intent(MainActivity.this,ClickPostActivity.class);
                             clickPostIntent.putExtra("PostKey", PostKey);
                             startActivity(clickPostIntent);

                         }
                     });


                     holder.commentpostbtn.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v)

                         {
                             Intent commentsIntent = new Intent(MainActivity.this,CommentsActivity.class);
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
                                        if (snapshot.child(PostKey).hasChild(currentUserID))
                                        {
                                            LikesRef.child(PostKey).child(currentUserID).removeValue();
                                            likechecker = false;
                                        }
                                        else
                                        {
                                            LikesRef.child(PostKey).child(currentUserID).setValue(true);
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
                 public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                     View view = LayoutInflater.from(parent.getContext())
                             .inflate(R.layout.all_post_layout, parent, false);
                     PostViewHolder viewHolder = new PostViewHolder(view);
                     return viewHolder;


                 }
             };
        postlist.setAdapter(adapter);

        adapter.startListening();


    }

    public  static  class  PostViewHolder extends  RecyclerView.ViewHolder
    {
        View mView;


        ImageButton likepostbtn , commentpostbtn ;
        TextView Displaynooflikes;
        int countlikes;
        String currentuserId;
        DatabaseReference Likesref;



        public PostViewHolder( View itemView) {
            super(itemView);
            mView =  itemView;

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



    private void SendUserToPostActivity()

    {
        Intent addNewPostIntent = new Intent(MainActivity.this,PostActivity.class);
        startActivity(addNewPostIntent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)

        {
           SendUserToLoginActivity();
        }


        else
        {
            CheckUserExistance();
            
        }

    }

    private void CheckUserExistance()

    {
        final String  current_user_id = mAuth.getCurrentUser().getUid();

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)

            {
                if(!dataSnapshot.hasChild(current_user_id))
                {
                    SendUserToSetupActivity();
                }

            }





            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });

    }

    private void SendUserToSetupActivity()

    {

        Intent setupIntent = new Intent ( MainActivity.this,SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToLoginActivity()


    {

        Intent loginIntent = new Intent ( MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {

        if (actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item)
            {
                switch  (item.getItemId())

                {
                    case R.id.nav_profile:


                        SendusertoprofileActivity();

                        break;


                    case R.id.nav_home:
                        Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                        break;


                    case R.id.nav_friends:

                        SendUserTofriendActivity();
                        Toast.makeText(this, "Find List", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_videocall:


                        SendUserTovideocallActivity();
                        Toast.makeText(this, "Video call", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_request:


                        SendUserTofriendrequestActivity();
                        Toast.makeText(this, "Friend request", Toast.LENGTH_SHORT).show();
                        break;



                    case R.id.nav_find_friends:
                        SendUserToFindfriendActivity();


                        Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_post:
                        SendUserToPostActivity();
                        break;


                    case R.id.nav_messages:

                        SendUserTofriendActivity();
                        Toast.makeText(this, "Message", Toast.LENGTH_SHORT).show();
                        break;



                    case R.id.nav_settings:
                        SendUserToSettingsActivity();





                        break;


                    case R.id.nav_Logout:


                        mAuth.signOut();
                        SendUserToLoginActivity();


                        break;


                }
            }


    private void SendUserTofriendActivity() {

        Intent friendIntent = new Intent ( MainActivity.this,FriendsActivity.class);

        startActivity(friendIntent);

    }

    private void SendUserTovideocallActivity() {

        Intent friendIntent = new Intent ( MainActivity.this,DashboardActivity.class);

        startActivity(friendIntent);

    }

    private void SendUserTofriendrequestActivity() {

        Intent friendIntent = new Intent ( MainActivity.this,RequestActivity.class);

        startActivity(friendIntent);

    }


    private void SendUserToFindfriendActivity() {

        Intent loginIntent = new Intent ( MainActivity.this,FindFriendsActivity.class);

        startActivity(loginIntent);

    }

    private void SendusertoprofileActivity()
    {

        Intent loginIntent = new Intent ( MainActivity.this,ProfileActivity.class);

        startActivity(loginIntent);

    }





    private void SendUserToSettingsActivity()


    {

        Intent loginIntent = new Intent ( MainActivity.this,SettingsActivity.class);

        startActivity(loginIntent);

    }



}