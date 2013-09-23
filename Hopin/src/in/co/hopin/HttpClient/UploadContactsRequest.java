package in.co.hopin.HttpClient;

import in.co.hopin.HelperClasses.ThisAppConfig;
import in.co.hopin.HelperClasses.ThisUserConfig;
import in.co.hopin.Server.ServerConstants;
import in.co.hopin.Server.ServerResponseBase;
import in.co.hopin.Server.UploadContactsResponse;
import in.co.hopin.Util.HopinTracker;
import in.co.hopin.Util.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class UploadContactsRequest extends SBHttpRequest {
    private static final String TAG = "in.co.hopin.HttpClient.UploadContactsRequest";

    private static String RESTAPI = "saveContacts";
    public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.USERDETAILSSERVICE + "/" + RESTAPI + "/";

    private final HttpPost httpQuery;
    private final HttpClient httpclient = new DefaultHttpClient();
    private JSONArray jsonArray;

    public UploadContactsRequest(JSONArray contactsJsonArray) {
        super(URL,RESTAPI);
        Logger.d(TAG, "Length:" + contactsJsonArray.length());
        queryMethod = QueryMethod.Post;
        httpQuery =  new HttpPost(URL);
        this.jsonArray = contactsJsonArray;

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair(ThisAppConfig.APPUUID, ThisAppConfig.getInstance().getString(ThisAppConfig.APPUUID)));
            nameValuePairs.add(new BasicNameValuePair(ThisUserConfig.USERID, ThisUserConfig.getInstance().getString(ThisUserConfig.USERID)));
            nameValuePairs.add(new BasicNameValuePair("contacts", contactsJsonArray.toString()));
            Logger.d(TAG, "calling server:" + nameValuePairs);
            httpQuery.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ServerResponseBase execute() {
        try {
            response = httpclient.execute(httpQuery);
        } catch (Exception e) {
            HopinTracker.sendEvent("HttpRequest","UploadContacts","httprequest:uploadcontacts:execut:executeeexception",1L);
            Logger.e(TAG, e.getMessage());
            Logger.d(TAG, jsonArray.toString());
            return null;
        }

        try {
            String jsonStr = responseHandler.handleResponse(response);
            UploadContactsResponse uploadRes = new UploadContactsResponse(response, jsonStr, RESTAPI, jsonArray);
            uploadRes.setReqTimeStamp(this.reqTimeStamp);
            return uploadRes;
        } catch (Exception e) {
            HopinTracker.sendEvent("HttpRequest","UploadContacts","httprequest:uploadcontacts:execute:responseexception",1L);
            Logger.e(TAG, e.getMessage());
        }

        return null;
    }
}
