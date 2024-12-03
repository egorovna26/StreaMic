package egorovna.streamic.server;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AudioServer implements Runnable {
    private static final int SAMPLE_RATE = 44100;
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int RAW_AUDIO_SOURCE = MediaRecorder.AudioSource.UNPROCESSED;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
    );

    private boolean working = false;
    private AudioRecord audioRecord;

    @Override
    public void run() {
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            return;
        }
        audioRecord.startRecording();
        byte[] buffer = new byte[MIN_BUFFER_SIZE];
        while (working) {
            audioRecord.read(buffer, 0, buffer.length);
        }
        audioRecord.stop();
        audioRecord.release();
        setAudioRecord(null);
    }

    public void startServer() {
        setWorking(true);
        setAudioRecord(new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, MIN_BUFFER_SIZE));
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stopServer() {
        setWorking(false);
    }
}
