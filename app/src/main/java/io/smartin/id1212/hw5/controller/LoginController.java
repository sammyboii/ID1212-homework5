package io.smartin.id1212.hw5.controller;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import io.smartin.id1212.hw5.exceptions.IllegalUsernameException;
import io.smartin.id1212.hw5.interfaces.UsernameRequest;

public class LoginController {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String storedUsername;

    public void logIn (final String requestedUsername, final UsernameRequest request) throws IllegalUsernameException {
        String username = sanitize(requestedUsername);
        if (storedUsername != null) {
            request.usernameAccepted(username);
            return;
        }
        final DatabaseReference ref = database.getReference().child("users");
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData == null) {
                    return Transaction.success(mutableData);
                }
                if (mutableData.hasChild(username)) {
                    request.usernameDenied(username, "The username already exists");
                } else {
                    mutableData.child(username).setValue(true);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError == null) {
                    storedUsername = username;
                    request.usernameAccepted(username);
                }
            }
        });
    }

    private String sanitize(String requestedUsername) throws IllegalUsernameException {
        if (requestedUsername.length() < 3)
            throw new IllegalUsernameException("Username is too short");
        int newLength = (requestedUsername.length() > 15) ? 15 : requestedUsername.length();
        return requestedUsername.replace(" ", "").toLowerCase().substring(0, newLength);
    }
}
