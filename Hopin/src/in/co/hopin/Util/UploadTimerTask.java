package in.co.hopin.Util;

import in.co.hopin.HelperClasses.Event;
import in.co.hopin.HelperClasses.SBConnectivity;
import in.co.hopin.HttpClient.SBHttpClient;
import in.co.hopin.HttpClient.UploadEventsRequest;

import java.util.List;
import java.util.TimerTask;

public class UploadTimerTask extends TimerTask {

	private static final String TAG = "in.co.hopin.Util.UploadTimerTask";
	
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
            HopinTracker.sendEvent("UploadEvents", "UploadEventTimer", "uploadeventtimer:uploadingevents", 1L);
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