package egorovna.streamic.server;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AudioServer implements Runnable {
    private static final int SAMPLE_RATE = 44100;
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
    );

    private boolean working = false;
    private WebSocketServer webSocketServer;
    private AudioRecord audioRecord;

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, MIN_BUFFER_SIZE);
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            return;
        }
        audioRecord.startRecording();
        webSocketServer = new WebSocketServer(new InetSocketAddress(8888)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {

            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {

            }

            @Override
            public void onMessage(WebSocket conn, String message) {

            }

            @Override
            public void onError(WebSocket conn, Exception ex) {

            }

            @Override
            public void onStart() {

            }
        };
        webSocketServer.setReuseAddr(true);
        webSocketServer.start();
        byte[] buffer = new byte[MIN_BUFFER_SIZE];
        setWorking(true);
        while (working) {
            audioRecord.read(buffer, 0, buffer.length);
            webSocketServer.broadcast(buffer);
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
        try {
            webSocketServer.stop();
        } catch (Exception e) {
            Log.e(null, null, e);
        }
    }

    public void startServer() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stopServer() {
        setWorking(false);
    }
}
