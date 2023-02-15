package com.sailiksarkar.freshchatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity
{

    private Toolbar chaattoolbar;
    private ImageButton sendmessagebtn, sendimagefilebtn;
    private EditText usermessageinput;


    private RecyclerView usermessagelist;


    private final List<Messages> messageslist = new ArrayList<>();
    private LinearLayoutManager  linearLayoutManager;
    private MessagesAdapter messageAdapter;

    private  String messagerecieverid, messagerecievername, messagesenderID, saveCurrentDate, saveCurrentTime;
    private TextView recievername;
    private CircleImageView recieverprofileimage;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rootRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        messagesenderID = mAuth.getCurrentUser().getUid();




        messagerecieverid = getIntent().getExtras().get("visit_user_id").toString();
        messagerecievername = getIntent().getExtras().get("userName").toString();


        Initializefields();

        displayrecieverinfo();

        sendmessagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendmessage();

            }
        });



        Fetchmessages();

        
    }

    private void Fetchmessages()

    {

        rootRef.child("Messages").child(messagesenderID).child(messagerecieverid)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded( DataSnapshot snapshot,  String s)

                    {

                        if (snapshot.exists())

                        {
                            Messages messages = snapshot.getValue(Messages.class);
                            messageslist.add(messages);
                            messageAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot snapshot,String s) {

                    }

                    @Override
                    public void onChildRemoved( DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved( DataSnapshot snapshot,  String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }

    private void sendmessage()

    {
        String messagetext = usermessageinput.getText().toString();

        if (TextUtils.isEmpty(messagetext))

        {
            Toast.makeText(this, "Please enter a message first ....", Toast.LENGTH_SHORT).show();

        }

        else
        {

            String message_send_ref = "Messages/"  +  messagesenderID + "/" + messagerecieverid;
            String message_reciever_ref = "Messages/"  +  messagerecieverid + "/" + messagesenderID;

            DatabaseReference user_message_key = rootRef.child("Messages").child(messagesenderID).child(messagerecieverid)
                    .push();

            String message_push_id = user_message_key.getKey();


            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM=yyy");

            saveCurrentDate = currentDate.format(calForDate.getTime());




            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");

            saveCurrentTime = currentTime.format(calForDate.getTime());


            Map messagetext_body = new HashMap();

            messagetext_body.put("message", messagetext);
            messagetext_body.put("time", saveCurrentTime);
            messagetext_body.put("date", saveCurrentDate);
            messagetext_body.put("type", "text");
            messagetext_body.put("from", messagesenderID);


            Map messagebodydetails = new HashMap();

            messagebodydetails.put(message_send_ref +  "/" +  message_push_id , messagetext_body);
            messagebodydetails.put(message_reciever_ref +  "/" +  message_push_id , messagetext_body);


            rootRef.updateChildren(messagebodydetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)

                {
                    if (task.isSuccessful())

                    {
                        Toast.makeText(ChatActivity.this, "Message sent successfully ...", Toast.LENGTH_SHORT).show();

                        usermessageinput.setText("");

                    }

                    else


                        {

                        String message = task.getException().getMessage();
                        Toast.makeText(ChatActivity.this, "Error occoured ...." + message, Toast.LENGTH_SHORT).show();

                            usermessageinput.setText("");
                    }


                }
            });


        }

    }

    private void displayrecieverinfo()

    {
        recievername.setText(messagerecievername);

        rootRef.child("Users").child(messagerecieverid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot)
            {

                if (snapshot.exists())
                {
                    final String profileimage = snapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(recieverprofileimage);

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    private void Initializefields()

    {

        chaattoolbar = (Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chaattoolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_barr, null);
        actionBar.setCustomView(action_bar_view);



        recievername = (TextView) findViewById(R.id.custom_profile_name);
        recieverprofileimage = (CircleImageView) findViewById(R.id.custom_profile_image);


        sendmessagebtn = (ImageButton) findViewById(R.id.send_message_btn);
        sendimagefilebtn = (ImageButton) findViewById(R.id.send_image_file_btn);
        usermessageinput = (EditText) findViewById(R.id.chat_input_message);


        messageAdapter = new MessagesAdapter(messageslist);
        usermessagelist = (RecyclerView) findViewById(R.id.masseges_list_user);

        linearLayoutManager = new LinearLayoutManager(this);
        usermessagelist.setHasFixedSize(true);
        usermessagelist.setLayoutManager(linearLayoutManager);
        usermessagelist.setAdapter(messageAdapter);

    }
}