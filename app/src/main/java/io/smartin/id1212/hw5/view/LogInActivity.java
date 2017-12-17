package io.smartin.id1212.hw5.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import io.smartin.id1212.hw5.R;
import io.smartin.id1212.hw5.controller.LoginController;
import io.smartin.id1212.hw5.exceptions.IllegalUsernameException;
import io.smartin.id1212.hw5.interfaces.UsernameRequest;

public class LogInActivity extends ActivityWithToastAlert {
    private LoginController loginController = new LoginController();
    private EditText usernameField;
    private Button letsGoButton;
    private ProgressBar loadingWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadingWheel = findViewById(R.id.loadingWheel);
        usernameField = findViewById(R.id.username);
        letsGoButton = findViewById(R.id.letsGo);
        setUpInputDetectionHandler();
        setUpButtonClickHandler();
        setLoading(false);
        if (!usernameHasMinChars())
            allowClickingButtonIf(false);
    }

    private void setUpButtonClickHandler() {
        letsGoButton.setOnClickListener(view -> {
            setLoading(true);
            final String username = usernameField.getText().toString();
            try {
                logIn(username);
            } catch (IllegalUsernameException e) {
                alert(e.getMessage());
            }
        });
    }

    private void logIn(String username) throws IllegalUsernameException {
        loginController.logIn(username, new UsernameRequest() {
            @Override
            public void usernameAccepted(String acceptedUsername) {
                setLoading(false);
                Intent chatView = new Intent(LogInActivity.this, ChatActivity.class);
                chatView.putExtra("username", acceptedUsername);
                startActivity(chatView);
                alert("Logged in as " + acceptedUsername);
            }
            @Override
            public void usernameDenied(String deniedUsername, String error) {
                setLoading(false);
                clearUsernameField();
                alert("Username was denied: " + error);
            }
        });
    }

    private void clearUsernameField() {
        runOnUiThread(() -> {
            usernameField.setText("");
            allowClickingButtonIf(false);
        });
    }

    private void setUpInputDetectionHandler() {
        usernameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                allowClickingButtonIf(usernameHasMinChars());
            }
            @Override
            public void afterTextChanged(Editable editable) {}
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });
    }

    private boolean usernameHasMinChars() {
        return usernameField.getText().toString().length() >= 3;
    }

    private void allowClickingButtonIf(boolean allow) {
        runOnUiThread(() -> {
            int color = allow ? R.color.colorAccent : R.color.disabledButton;
            int textColor = allow ? Color.WHITE : Color.GRAY;
            letsGoButton.setEnabled(allow);
            letsGoButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(color)));
            letsGoButton.setTextColor(textColor);
        });
    }

    private void setLoading(boolean loading) {
        allowClickingButtonIf(!loading);
        runOnUiThread(() -> loadingWheel.setVisibility(loading ? View.VISIBLE : View.INVISIBLE));
    }
}
