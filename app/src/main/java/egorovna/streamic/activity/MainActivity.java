package egorovna.streamic.activity;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.icu.text.MessageFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import egorovna.streamic.R;
import egorovna.streamic.service.AudioService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainActivity extends AppCompatActivity {
    private BroadcastReceiver broadcastReceiver;
    private ServiceConnection serviceConnection;
    private AudioService audioService;
    private boolean serviceBound = false;

    private CardView postNotificationsCard;
    private CardView recordAudioCard;
    private CardView serviceStart;
    private CardView serviceStatus;
    private Button serviceStop;

    private TextView deviceName;
    private TextView deviceIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkPermissions();
            }
        };
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AudioService.LocalBinder localBinder = (AudioService.LocalBinder) service;
                setAudioService(localBinder.getService());
                setServiceBound(true);
                checkPermissions();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                setServiceBound(false);
            }
        };
        postNotificationsCard = findViewById(R.id.post_notifications_card);
        recordAudioCard = findViewById(R.id.record_audio_card);
        Button allowPostNotifications = findViewById(R.id.allow_post_notifications);
        Button allowRecordAudio = findViewById(R.id.allow_record_audio);
        allowPostNotifications.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(new String[]{POST_NOTIFICATIONS}, 1);
            }
        });
        allowRecordAudio.setOnClickListener(v ->
                requestPermissions(new String[]{RECORD_AUDIO}, 1));
        serviceStart = findViewById(R.id.service_start);
        serviceStart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AudioService.class);
            startForegroundService(intent);
        });
        serviceStatus = findViewById(R.id.service_status);
        serviceStop = findViewById(R.id.service_stop);
        serviceStop.setOnClickListener(v -> audioService.stopService());
        deviceName = findViewById(R.id.device_name);
        deviceName.setText(MessageFormat.format("{0} {1}", Build.MANUFACTURER, Build.MODEL));
        deviceIp = findViewById(R.id.device_ip);
        deviceIp.setText(ip());
        checkPermissions();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, AudioService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("SERVICE_STARTED");
        intentFilter.addAction("SERVICE_STOPPED");
        registerReceiver(broadcastReceiver, intentFilter, RECEIVER_EXPORTED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
        setServiceBound(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermissions();
    }

    private String ip() {
        StringBuilder builder = new StringBuilder();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface != null) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if (inetAddress.isLoopbackAddress()) {
                            continue;
                        }
                        if (inetAddress instanceof Inet4Address) {
                            builder.append(inetAddress.getHostAddress());
                            builder.append("\n");
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(null, null, e);
        }
        return builder.toString().trim();
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
            if (serviceBound && audioService.isActive()) {
                serviceStart.setVisibility(GONE);
                serviceStatus.setVisibility(VISIBLE);
            } else {
                serviceStart.setVisibility(VISIBLE);
                serviceStatus.setVisibility(GONE);
            }
        } else {
            serviceStart.setVisibility(GONE);
            serviceStatus.setVisibility(GONE);
        }
    }
}