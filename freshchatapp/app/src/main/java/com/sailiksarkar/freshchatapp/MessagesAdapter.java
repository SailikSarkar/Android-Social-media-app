package com.sailiksarkar.freshchatapp;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.Messageviewholder>



{
    private List<Messages>  userMessagelist;
    private FirebaseAuth mAuth;
    private DatabaseReference  usersdatabaseref;



    public  MessagesAdapter (List<Messages> usermessagelist)
    {
        this.userMessagelist = usermessagelist;
    }

    public class Messageviewholder  extends  RecyclerView.ViewHolder
    {


        public TextView sendermessagetext, recievermessagetext ;
        public CircleImageView recieverprofileimage;

        public Messageviewholder(View itemView)

        {
            super(itemView);

            sendermessagetext = (TextView) itemView.findViewById(R.id.sender_message_text );
            recievermessagetext = (TextView) itemView.findViewById(R.id.reciever_message_text );
            recieverprofileimage = (CircleImageView) itemView.findViewById(R.id.reciever_profile_image);
        }
    }

    @NonNull

    @Override
    public Messageviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)

    {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout_user, parent,false);


        mAuth= FirebaseAuth.getInstance();

        return new Messageviewholder(v);

    }


    @Override
    public void onBindViewHolder(@NonNull Messageviewholder holder, int position)

    {

        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagelist.get(position);

        String fromuserId =  messages.getFrom() ;
        String Frommessagetype = messages.getType();

        usersdatabaseref = FirebaseDatabase.getInstance().getReference().child("Users").child(fromuserId);
        usersdatabaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot)

            {

                if (snapshot.exists())

                {
                    String image =  snapshot.child("profileimage").getValue().toString();

                    Picasso.get().load(image).placeholder(R.drawable.profile).into(holder.recieverprofileimage);

                }

            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });

        if (Frommessagetype.equals("text"))
        {

           holder.recievermessagetext.setVisibility(View.INVISIBLE);
            holder.recieverprofileimage.setVisibility(View.INVISIBLE);


            if (fromuserId.equals(messageSenderId))

            {

                holder.sendermessagetext.setBackgroundResource(R.drawable.sender_message_text_bg);
                holder.sendermessagetext.setTextColor(Color.WHITE);
                holder.sendermessagetext.setGravity(Gravity.LEFT);
                holder.sendermessagetext.setText(messages.getMessage());

            }
            else

            {

                holder.sendermessagetext.setVisibility(View.INVISIBLE);

                holder.recievermessagetext.setVisibility(View.VISIBLE);
                holder.recieverprofileimage.setVisibility(View.VISIBLE);



                holder.recievermessagetext.setBackgroundResource(R.drawable.reciever_text_bg);
                holder.recievermessagetext.setTextColor(Color.WHITE);
                holder.recievermessagetext.setGravity(Gravity.LEFT);
                holder.recievermessagetext.setText(messages.getMessage());


            }
        }


    }

    @Override
    public int getItemCount()

    {
        return userMessagelist.size();
    }
}
