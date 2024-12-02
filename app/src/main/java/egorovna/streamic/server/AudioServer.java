package egorovna.streamic.server;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AudioServer implements Runnable {
    private boolean working = false;

    @Override
    public void run() {
        while (working) {

        }
    }

    public void startServer() {
        setWorking(true);
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stopServer() {
        setWorking(false);
    }
}
