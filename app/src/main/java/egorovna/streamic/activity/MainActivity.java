package egorovna.streamic.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import egorovna.streamic.R;

public class MainActivity extends AppCompatActivity {
    private CardView postNotificationsCard;
    private CardView recordAudioCard;
    private Button allowPostNotifications;
    private Button allowRecordAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        postNotificationsCard = findViewById(R.id.post_notifications_card);
        recordAudioCard = findViewById(R.id.record_audio_card);
        allowPostNotifications = findViewById(R.id.allow_post_notifications);
        allowRecordAudio = findViewById(R.id.allow_record_audio);
        checkPermissions();
        allowPostNotifications.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        });
        allowRecordAudio.setOnClickListener(v ->
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermissions();
    }

    private void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            postNotificationsCard.setVisibility(View.GONE);
        } else {
            postNotificationsCard.setVisibility(View.VISIBLE);
        }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            recordAudioCard.setVisibility(View.GONE);
        } else {
            recordAudioCard.setVisibility(View.VISIBLE);
        }
    }
}