package com.sailiksarkar.freshchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity

{


    private ImageButton postcommentbtn;
    private EditText commentinput;
    private RecyclerView commentlist;

    private DatabaseReference Usersref,  postref , commentRef;



    private String post_key, current_user_id;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Usersref = FirebaseDatabase.getInstance().getReference().child("Users");

        post_key = getIntent().getExtras().get("PostKey").toString();
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        postref = FirebaseDatabase.getInstance().getReference().child("Posts").child(post_key).child("comments");
        commentRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(post_key).child("comments");




        commentlist = (RecyclerView) findViewById(R.id.comment_list);
        commentlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentlist.setLayoutManager(linearLayoutManager);


        commentinput = (EditText)  findViewById(R.id.enter_comment);
        postcommentbtn = (ImageButton)  findViewById(R.id.post_comment_btn);


        postcommentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {

                Usersref.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot)

                    {
                        if (snapshot.exists())
                        {

                            String username = snapshot.child("username").getValue().toString();

                            validatecomment(username);
                            commentinput.setText("");
                            

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error)

                    {

                    }
                });

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Comments> options =
                new FirebaseRecyclerOptions.Builder<Comments>()
                        .setQuery(commentRef, Comments.class)
                        .build();


        FirebaseRecyclerAdapter<Comments, commentsViewholder>  firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comments, commentsViewholder>(options) {
            @Override
            protected void onBindViewHolder( CommentsActivity.commentsViewholder holder, int position, Comments model)
            {

                holder.setUsername(model.getUsername());
                holder.setComment(model.getComment());
                holder.setTime(model.getTime());
                holder.setDate(model.getDate());



            }

            @NonNull

            @Override
            public commentsViewholder onCreateViewHolder( ViewGroup parent, int viewType)

            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_comments_layout, parent, false);
                commentsViewholder viewholder = new commentsViewholder(view);
                return viewholder;

            }
        };
        commentlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();


    }

    public static class  commentsViewholder extends  RecyclerView.ViewHolder
    {

        View mView;
        public commentsViewholder(View itemView)

        {
            super(itemView);

            mView = itemView;
        }

        public void setUsername(String username)
        {

            TextView myUsername =  (TextView) mView.findViewById(R.id.comment_username);
            myUsername.setText("@ " +  username+ "  ");

        }
        public void setComment(String comment)
        {

            TextView mycomment =  (TextView) mView.findViewById(R.id.comment_text);
            mycomment.setText(comment);

        }
        public void setDate(String date)
        {


            TextView mydate =  (TextView) mView.findViewById(R.id.comment_date);
            mydate.setText("   Date " + date);
        }
        public void setTime(String time)
        {
            TextView mytime =  (TextView) mView.findViewById(R.id.comment_time);
            mytime.setText( "  Time " +  time);

        }

    }


    private void validatecomment(String username)

    {
        String commentText = commentinput.getText().toString();

        if (TextUtils.isEmpty(commentText))
        {
            Toast.makeText(this, "Please enter any text or emogi to comment ..... ", Toast.LENGTH_SHORT).show();
        }
        else
        {

            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM=yyy");

           final String saveCurrentDate = currentDate.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");

            final String saveCurrentTime = currentTime.format(calForDate.getTime());


            final  String Randomkey = current_user_id +saveCurrentDate +saveCurrentTime;

            HashMap commentsmap = new HashMap();
            commentsmap.put("uid", current_user_id);
            commentsmap.put("comment", commentText);
            commentsmap.put("date", saveCurrentDate);
            commentsmap.put("time", saveCurrentTime);
            commentsmap.put("username", username);


            postref.child(Randomkey).updateChildren(commentsmap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull  Task task)

                        {

                            if (task.isSuccessful())
                            {
                                Toast.makeText(CommentsActivity.this, "You have commented successfully ....", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                Toast.makeText(CommentsActivity.this, "Please check your internet connection ..", Toast.LENGTH_SHORT).show();

                            }
                        }


                    });

        }


    }
}