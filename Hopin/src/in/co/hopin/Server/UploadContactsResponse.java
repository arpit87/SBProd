package in.co.hopin.Server;

import in.co.hopin.Util.Logger;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UploadContactsResponse extends ServerResponseBase {
    public static final String TAG = "in.co.hopin.Server.UploadContactsResponse";
    private JSONArray jsonArray;

    public UploadContactsResponse(HttpResponse httpResponse, String jObjStr,String api, JSONArray jsonArray) {
        super(httpResponse, jObjStr,api);
        this.jsonArray = jsonArray;
    }

    @Override
    public void process() {
        Logger.i(TAG, "Processing UploadContactsResponse response. status:" + this.getStatus());
        Logger.i(TAG, "Got json : " + jobj.toString());
        try {
            if (jobj.has("error")) {
                JSONObject errorJson = jobj.getJSONObject("error");
                Logger.e(TAG, "Upload failed with error: " + errorJson.getString("error"));
                Logger.e(TAG, jsonArray.toString());
                logServererror();
            }
        } catch (JSONException e) {
            logServererror();
            Logger.e(TAG, "Error returned by server on UploadContactssRequest", e);
        }
    }
}
