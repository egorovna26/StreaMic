package egorovna.streamic.activity;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.MessageFormat;

import egorovna.streamic.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        TextView device = findViewById(R.id.device);
        device.setText(MessageFormat.format("{0} {1}", Build.MANUFACTURER, Build.MODEL));
    }
}