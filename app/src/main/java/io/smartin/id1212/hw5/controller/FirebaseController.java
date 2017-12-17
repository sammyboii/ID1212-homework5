package io.smartin.id1212.hw5.controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.smartin.id1212.hw5.exceptions.IllegalMessageException;
import io.smartin.id1212.hw5.exceptions.NotLoggedInException;
import io.smartin.id1212.hw5.dto.Message;

/**
 * Created by sam on 2017-12-17.
 */

public class FirebaseController {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String username;

    public FirebaseController(String username) {
        this.username = username;
    }

    public DatabaseReference getMessagesReference () {
        return database.getReference().child("messages");
    }

    public void sendMessage (String message) throws NotLoggedInException, IllegalMessageException {
        if (!loggedIn()) throw new NotLoggedInException("You are not logged in");
        if (message.length() == 0) throw new IllegalMessageException("Message is too short");
        final DatabaseReference ref = database.getReference().child("messages");
        ref.push().setValue(new Message(username, message));
    }

    public void logOut () throws NotLoggedInException {
        if (!loggedIn()) throw new NotLoggedInException("You are not logged in");
        final DatabaseReference ref = database.getReference().child("users").child(username);
        ref.removeValue();
    }

    private boolean loggedIn() {
        return username != null;
    }
}
