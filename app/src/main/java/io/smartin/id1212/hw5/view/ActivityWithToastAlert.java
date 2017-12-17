package io.smartin.id1212.hw5.view;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class ActivityWithToastAlert extends AppCompatActivity {
    protected void alert(String text) {
        runOnUiThread(() -> {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(ActivityWithToastAlert.this, text, duration);
            toast.show();
        });
    }
}
