package egorovna.streamic.service;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import egorovna.streamic.R;
import egorovna.streamic.activity.MainActivity;
import egorovna.streamic.server.AudioServer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AudioService extends Service {
    private static final int SAMPLE_RATE = 44100;
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
    );

    private final LocalBinder localBinder = new LocalBinder();
    private final AudioServer audioServer = new AudioServer();

    private boolean active = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isActive()) {
            return START_STICKY;
        }
        boolean recordAudio = checkSelfPermission(RECORD_AUDIO) == PERMISSION_GRANTED;
        if (!recordAudio) {
            stopService();
        }
        AudioRecord audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, MIN_BUFFER_SIZE);
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            stopService();
        }
        audioServer.setAudioRecord(audioRecord);
        audioServer.setBuffer(new byte[MIN_BUFFER_SIZE]);
        audioServer.startServer();
        Notification notification = new NotificationCompat.Builder(this, getString(R.string.notification_channel_name))
                .setContentTitle("Active Server")
                .setSmallIcon(R.drawable.mic)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .setContentIntent(PendingIntent.getActivity(this, 0,
                        new Intent(this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE))
                .build();
        int type = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            type = ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE;
        }
        ServiceCompat.startForeground(this, 100, notification, type);
        setActive(true);
        sendBroadcast(new Intent("SERVICE_STARTED"));
        return START_STICKY;
    }

    public void stopService() {
        setActive(false);
        sendBroadcast(new Intent("SERVICE_STOPPED"));
        audioServer.stopServer();
        stopForeground(true);
        stopSelf();
    }

    public class LocalBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }
}
