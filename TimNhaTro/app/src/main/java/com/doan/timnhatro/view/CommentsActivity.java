package com.doan.timnhatro.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doan.timnhatro.R;
import com.doan.timnhatro.adapter.CommentAdapter;
import com.doan.timnhatro.model.Comments;
import com.doan.timnhatro.utils.AccountUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    private CommentAdapter commentAdapter;
    private ArrayList<Comments> arrayComments = new ArrayList<>();
    private RecyclerView CommentsList;
    private ImageButton PostCommentButton;
    private EditText CommentInputText;
    private DatabaseReference UserRef, RoomRef;

    private String Post_Key, current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Post_Key = getIntent().getExtras().get("PostKey").toString();
        current_user_id = AccountUtils.getInstance().getAccount().getPhoneNumber();

        UserRef = FirebaseDatabase.getInstance().getReference().child("Account");
        RoomRef = FirebaseDatabase.getInstance().getReference().child("MotelRoom").child(Post_Key).child("Comments");

        CommentsList = (RecyclerView) findViewById(R.id.comments_list);
        CommentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);

        //setUpRecyclerView();

        CommentInputText = (EditText) findViewById(R.id.comment_input);
        PostCommentButton = (ImageButton) findViewById(R.id.post_comment_btn);

        PostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String userName = snapshot.child("name").getValue().toString();

                            ValidateComment(userName);

                            CommentInputText.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

  /*  private void setUpRecyclerView() {
        //arrayComments.clear();
        CommentsList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        CommentsList.setLayoutManager(layoutManager);
        RoomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Comments comments = dataSnapshot.getValue(Comments.class);
                    arrayComments.add(comments);
                }

                commentAdapter = new CommentAdapter(arrayComments);
                CommentsList.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
      *//*  RoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Comments comments = dataSnapshot.getValue(Comments.class);
                        arrayComments.add(comments);
                    }

                    commentAdapter = new CommentAdapter(arrayComments);
                    CommentsList.setAdapter(commentAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*//*
    }*/

    private void ValidateComment(final String userName) {

        final String commentText = CommentInputText.getText().toString();
        if(TextUtils.isEmpty(commentText)){
            Toast.makeText(this, "Hãy viết cái gì đó!", Toast.LENGTH_SHORT).show();
        }else {

            Calendar calFordDate=Calendar.getInstance();
            SimpleDateFormat currentDate= new SimpleDateFormat("dd-MM-yyyy");
            final String saveCurrentDate=currentDate.format(calFordDate.getTime());

            Calendar calFordTime=Calendar.getInstance();
            SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss");
            final String saveCurrentTime=currentTime.format(calFordTime.getTime());
            final String RandomKey = current_user_id + saveCurrentDate + saveCurrentTime;

            UserRef.child(current_user_id).child("avatar").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String avatar = snapshot.getValue().toString();

                        HashMap commentsMap= new HashMap();
                        commentsMap.put("avatar", avatar);
                        commentsMap.put("uid", current_user_id);
                        commentsMap.put("comment", commentText);
                        commentsMap.put("date", saveCurrentDate);
                        commentsMap.put("time", saveCurrentTime);
                        commentsMap.put("username", userName);

                        RoomRef.child(RandomKey).updateChildren(commentsMap)
                                .addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if(task.isSuccessful()){
                                          //  arrayComments.clear();
                                            Toast.makeText(CommentsActivity.this, "Bình luận thành công", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(CommentsActivity.this, "Lỗi rồi! Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> fbRecycleAdapter
                = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(
                Comments.class,
                R.layout.row_commets,
                CommentsViewHolder.class,
                RoomRef
        )
        {
            @Override
            protected void populateViewHolder(CommentsViewHolder viewHolder, Comments model, int position) {
                viewHolder.setAvatar(model.getAvatar());
                viewHolder.setUser(model.getUsername());
                viewHolder.setComment(model.getComment());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());

            }
        };
        CommentsList.setAdapter(fbRecycleAdapter);
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;



        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            //imgAvatar = itemView.findViewById(R.id.imgAvatarCmt);
        }
        public void setUser(String user) {
            TextView myUsername = (TextView) mView.findViewById(R.id.comment_username);
            myUsername.setText(user+"  ");
        }
        public void setComment(String comment) {
            TextView myComment = (TextView) mView.findViewById(R.id.comment_text);
            myComment.setText(comment);
        }
        public void setDate(String date) {
            TextView myDate = (TextView) mView.findViewById(R.id.comment_date);
            myDate.setText("  Date: " + date);
        }
        public void setTime(String time) {
            TextView myTime = (TextView) mView.findViewById(R.id.comment_time);
            myTime.setText("  Time: "+time);
        }

        public void setAvatar(String avartar) {
            CircleImageView imgAvatar;
            imgAvatar = (CircleImageView) mView.findViewById(R.id.imgAvatarCmt);
            Glide.with(imgAvatar.getContext())
                    .load(avartar).placeholder(R.drawable.ic_user)
                    .into(imgAvatar);
            //Toast.makeText(imgAvatar.getContext(), avartar, Toast.LENGTH_SHORT).show();
        }
    }
}