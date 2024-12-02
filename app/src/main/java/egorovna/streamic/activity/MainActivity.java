package egorovna.streamic.activity;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import egorovna.streamic.R;

public class MainActivity extends AppCompatActivity {
    private CardView postNotificationsCard;
    private CardView recordAudioCard;
    private CardView serviceStart;
    private CardView serviceStatus;
    private Button serviceStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        postNotificationsCard = findViewById(R.id.post_notifications_card);
        recordAudioCard = findViewById(R.id.record_audio_card);
        Button allowPostNotifications = findViewById(R.id.allow_post_notifications);
        Button allowRecordAudio = findViewById(R.id.allow_record_audio);
        checkPermissions();
        allowPostNotifications.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(new String[]{POST_NOTIFICATIONS}, 1);
            }
        });
        allowRecordAudio.setOnClickListener(v ->
                requestPermissions(new String[]{RECORD_AUDIO}, 1));
        serviceStart = findViewById(R.id.service_start);
        serviceStatus = findViewById(R.id.service_status);
        serviceStop = findViewById(R.id.service_stop);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermissions();
    }

    private void checkPermissions() {
        boolean postNotifications = checkSelfPermission(POST_NOTIFICATIONS) == PERMISSION_GRANTED;
        boolean recordAudio = checkSelfPermission(RECORD_AUDIO) == PERMISSION_GRANTED;
        if (postNotifications) {
            postNotificationsCard.setVisibility(GONE);
        } else {
            postNotificationsCard.setVisibility(VISIBLE);
        }
        if (recordAudio) {
            recordAudioCard.setVisibility(GONE);
        } else {
            recordAudioCard.setVisibility(VISIBLE);
        }
        if (postNotifications && recordAudio) {
            serviceStart.setVisibility(VISIBLE);
            serviceStatus.setVisibility(VISIBLE);
        } else {
            serviceStart.setVisibility(GONE);
            serviceStatus.setVisibility(GONE);
        }
    }
}