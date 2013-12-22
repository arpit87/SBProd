package in.co.hopin.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import in.co.hopin.HelperClasses.Event;
import in.co.hopin.HelperClasses.SBConnectivity;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.UploadEventsRequest;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UploadEventService extends Service {
    public static final String TAG = "in.co.hopin.service.UploadEventService";
    private static final int UPLOAD_FREQUENCY = 60 * 60 * 1000;

    private Timer timer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = new Timer();
        timer.schedule(new UploadTask(), UPLOAD_FREQUENCY, UPLOAD_FREQUENCY);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    class UploadTask extends TimerTask {

        @Override
        public void run() {
            Logger.d(TAG, "I am in upload TimerTask");
            if (!SBConnectivity.isConnected()) {
                return;
            }

            List<Event> events = Event.getEvents();
            if (events.isEmpty()) {
                return;
            } else {
                HopinTracker.sendEvent("UploadEventService", "UploadEvents", "uploadeventservice:uploadingevents", 1L);
                events = Event.getEvents();
            }

            StringBuilder sb = new StringBuilder();
            sb.append("{\"common\":");
            sb.append(HopinTracker.createCommonInfoJSON().toString());
            sb.append(",\"rows\":[");

            long maxTimestamp = 0;
            for (int i = 0; i < events.size(); i++) {
                Logger.d(TAG, events.get(i).getJsonDescription());
                sb.append(events.get(i).getJsonDescription());
                if (i != events.size() - 1) {
                    sb.append(",");
                }
                maxTimestamp = Math.max(maxTimestamp, events.get(i).getTime());
            }
            sb.append("]}");

            UploadEventsRequest request = new UploadEventsRequest(sb.toString(), maxTimestamp);
            SBHttpClient.getInstance().executeRequest(request);
        }
    }
}
