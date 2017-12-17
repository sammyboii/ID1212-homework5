package io.smartin.id1212.hw5.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import io.smartin.id1212.hw5.R;
import io.smartin.id1212.hw5.controller.FirebaseController;
import io.smartin.id1212.hw5.exceptions.IllegalMessageException;
import io.smartin.id1212.hw5.exceptions.NotLoggedInException;
import io.smartin.id1212.hw5.dto.Message;

public class ChatActivity extends ActivityWithToastAlert {
    private FirebaseController firebase;
    private TextView messageList;
    private EditText newMessage;
    private Button sendMessage;
    private Button quitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebase = new FirebaseController(getIntent().getStringExtra("username"));
        setContentView(R.layout.activity_chat);
        initComponents();
        listenForMessages();
    }

    private void initComponents() {
        final TextView usernameTitle = findViewById(R.id.usernameTitle);
        sendMessage = findViewById(R.id.sendMessage);
        newMessage = findViewById(R.id.newMessage);
        quitButton = findViewById(R.id.quitButton);
        messageList = findViewById(R.id.messageList);
        messageList.setMovementMethod(new ScrollingMovementMethod());
        usernameTitle.setText(getIntent().getStringExtra("username"));
        setUpQuitButtonHandler();
        setUpSendMessageHandler();
        setUpSendButtonHandler();
        allowPressingSend(false);
    }

    private void setUpSendMessageHandler() {
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = newMessage.getText().toString();
                try {
                    firebase.sendMessage(message);
                    clearInput();
                } catch (NotLoggedInException | IllegalMessageException e) {
                    alert(e.getMessage());
                }
            }
        });
    }

    private void setUpSendButtonHandler() {
        newMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (newMessage.getText().toString().length() == 0) {
                    allowPressingSend(false);
                } else {
                    allowPressingSend(true);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void setUpQuitButtonHandler() {
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    firebase.logOut();
                } catch (NotLoggedInException e) {
                    alert("You were never logged in to begin with!");
                }
                Intent goToLogin = new Intent(ChatActivity.this, LogInActivity.class);
                startActivity(goToLogin);
                alert("Logged out");
            }
        });
    }

    private void listenForMessages() {
        DatabaseReference ref = firebase.getMessagesReference();
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                renderMessage(dataSnapshot.getValue(Message.class));
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void allowPressingSend(boolean allow) {
        int color = allow ? R.color.colorAccent : R.color.disabledButton;
        int textcolor = allow ? Color.WHITE : Color.GRAY;
        sendMessage.setEnabled(allow);
        sendMessage.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(color)));
        sendMessage.setTextColor(textcolor);
    }

    private void renderMessage(Message message) {
        String format = "(" + message.getFormattedTime() + ") " + message.getFrom() + ": " + message.getMessage() + "\n";
        messageList.append(format);
    }

    private void clearInput() {
        newMessage.setText("");
    }
}
