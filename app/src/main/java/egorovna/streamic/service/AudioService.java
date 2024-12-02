package egorovna.streamic.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class AudioService extends Service {
    private final LocalBinder localBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    public class LocalBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }
}
