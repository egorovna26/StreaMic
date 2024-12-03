package egorovna.streamic.server;

import android.media.AudioRecord;
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
    private boolean working = false;
    private WebSocketServer webSocketServer;
    private AudioRecord audioRecord;
    private byte[] buffer;

    @Override
    public void run() {
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
