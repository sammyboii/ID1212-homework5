package io.smartin.id1212.hw5.controller;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import io.smartin.id1212.hw5.exceptions.IllegalUsernameException;
import io.smartin.id1212.hw5.interfaces.UsernameRequest;

public class LoginController {
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private String storedUsername;

    public void logIn (final String requestedUsername, final UsernameRequest request) throws IllegalUsernameException {
        String username = sanitize(requestedUsername);
        if (storedUsername != null) {
            request.usernameAccepted(username);
            return;
        }
        dbRef.child("users").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    return Transaction.success(mutableData);
                }
                if (mutableData.hasChild(username)) {
                    request.usernameDenied(username,
                            "That username is already taken");
                    return Transaction.abort();
                }
                mutableData.child(username).setValue(true);
                storedUsername = username;
                request.usernameAccepted(username);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
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
