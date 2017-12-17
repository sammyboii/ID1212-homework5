package io.smartin.id1212.hw5.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.smartin.id1212.hw5.R;
import io.smartin.id1212.hw5.controller.ChatController;
import io.smartin.id1212.hw5.exceptions.IllegalMessageException;
import io.smartin.id1212.hw5.exceptions.NotLoggedInException;
import io.smartin.id1212.hw5.dto.Message;

public class ChatActivity extends ActivityWithToastAlert {
    private ChatController controller;
    private TextView messageList;
    private EditText newMessage;
    private Button sendMessage;
    private Button quitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new ChatController(getIntent().getStringExtra("username"));
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
        sendMessage.setOnClickListener(view -> {
            String message = newMessage.getText().toString();
            try {
                controller.sendMessage(message);
                clearInput();
            } catch (NotLoggedInException | IllegalMessageException e) {
                alert(e.getMessage());
            }
        });
    }

    private void setUpSendButtonHandler() {
        newMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                allowPressingSend(!messageFieldIsEmpty());
            }
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) {}
        });
    }

    private boolean messageFieldIsEmpty() {
        return newMessage.getText().toString().length() == 0;
    }

    private void setUpQuitButtonHandler() {
        quitButton.setOnClickListener(view -> {
            try {
                controller.logOut();
            } catch (NotLoggedInException e) {
                alert("You were never logged in to begin with!");
            }
            Intent goToLogin = new Intent(ChatActivity.this, LogInActivity.class);
            startActivity(goToLogin);
            alert("Logged out");
        });
    }

    private void listenForMessages() {
        try {
            controller.listenForMessages(this::renderMessage);
        } catch (NotLoggedInException e) {
            alert("You are not logged in!");
        }
    }

    private void allowPressingSend(boolean allow) {
        runOnUiThread(() -> {
            int color = allow ? R.color.colorAccent : R.color.disabledButton;
            int textcolor = allow ? Color.WHITE : Color.GRAY;
            sendMessage.setEnabled(allow);
            sendMessage.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(color)));
            sendMessage.setTextColor(textcolor);
        });
    }

    private void renderMessage(Message message) {
        String format = "(" + message.getFormattedTime() + ") " + message.getFrom() + ": " + message.getMessage() + "\n";
        runOnUiThread(() -> messageList.append(format));
    }

    private void clearInput() {
        runOnUiThread(() -> {
            newMessage.setText("");
            allowPressingSend(false);
        });

    }
}
