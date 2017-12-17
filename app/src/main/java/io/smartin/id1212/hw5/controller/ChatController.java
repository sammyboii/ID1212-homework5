package io.smartin.id1212.hw5.controller;

import android.os.AsyncTask;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.smartin.id1212.hw5.exceptions.IllegalMessageException;
import io.smartin.id1212.hw5.exceptions.NotLoggedInException;
import io.smartin.id1212.hw5.dto.Message;
import io.smartin.id1212.hw5.interfaces.MessageListener;

public class ChatController {
    private DatabaseReference dbRef;
    private String username;

    public ChatController(String username) {
        this.username = username;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    public void sendMessage (String message) throws NotLoggedInException, IllegalMessageException {
        if (!loggedIn()) throw new NotLoggedInException("You are not logged in");
        if (message.length() == 0) throw new IllegalMessageException("Message is too short");
        dbRef.child("messages").push().setValue(new Message(username, message));
    }

    public void logOut () throws NotLoggedInException {
        if (!loggedIn()) throw new NotLoggedInException("You are not logged in");
        dbRef.child("users").child(username).removeValue();
    }

    public void listenForMessages(MessageListener messageListener) throws NotLoggedInException {
        if (!loggedIn()) throw new NotLoggedInException("You are not logged in");
        dbRef.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messageListener.onMessage(message);
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private boolean loggedIn() {
        return username != null;
    }
}
