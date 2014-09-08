package sdei.detector.inspector.sync.service.ws;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.DetectorInspector_LoginActivity;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.util.Const;
import android.content.Intent;
import android.util.Log;

import com.detector.inspector.lib.util.Util;
import com.google.gson.JsonObject;
import com.google.myjson.Gson;

public class AddReportSyncTask extends WSClientSyncTask {

	private String mReportUUID;
	private String mPath;
	private String lat;
	private String lng;
	private String bookingId;
	private String technicianId;
	private String serviceSheetId;
	WSResponse response = null;
	// upload photo
	public AddReportSyncTask(WSRequest request, String sync_grp_id,
			String report_uuid, String path, String lat, String lng,
			String bookingId, String technicianId, String serviceSheetId) {
		super(request, sync_grp_id);

		mReportUUID = report_uuid;
		mPath = path;
		this.lat = lat;
		this.lng = lng;
		this.bookingId = bookingId;
		this.technicianId = technicianId;
		this.serviceSheetId = serviceSheetId;
	}
	/*
	 * @params
	 *  WSRequest = a getter setter of parcelable type which contain 
			a url, http method type and reponse type
		WSResponse= a getter setter of parcelable type which contains
		    http response and json result
		   
		 WsClient is the json parser class 
	 */
	@Override
	protected WSResponse doInBackground(WSRequest... request) {
		
	
		WSClient client = new WSClient();

		// Get the report json string from local file
		Log.d("TEST", "read json file " + mReportUUID + ".json"	+ "and path is >>" + mPath);
		String report_json_string = Util.readJSONFile(mPath, mReportUUID);

		Log.i("report from function is  >>><><><><><><><>", report_json_string);
		try {
			
			Gson gson = new Gson();

			String content = "{\"syncData\":{\"technicianId\":\""
					+ technicianId + "\",\"bookingId\":\"" + bookingId
					+ "\",\"lat\":\"" + lat + "\",\"lng\":\"" + lng
					+ "\",\"oldserviceSheetid\":\"" + serviceSheetId
					+ "\",\"report\":" + report_json_string + "}}";
			
			//Log.i("Sync Json data from AddReportSyncTask", content);
			Log.i("Sync Url","http://108.168.203.227/DetectorService/TechnicianSync.svc/DedectionComplite="+content);
			//-----------------------------------------------------------------------------------------------
			StringEntity se = new StringEntity(content, "UTF-8");
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,	"application/json"));
			HttpEntity http_entity = new StringEntity(content, "UTF-8");
			response = client.sendHttpRequest(request[0], se);
			
		} catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		return response;
	}

	@Override
	protected void onPostExecute(WSResponse result) {
	
		String message=""; String status =""; //String propertyId="";
		try {
			JSONObject jsonObject = new JSONObject(result.getHttpResult());
			message     = jsonObject.getString("message");
			status      = jsonObject.getString("status");
			//propertyId  = jsonObject.getString("propertyId");			
			Log.i("message----status---","Message = "+message+"---status--"+status);
			
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		/*   
		 * 
		 * String ResultValue = result.getHttpResult();
		Log.i("Sync Result Response",ResultValue);    
		 */
		
		Intent resp_intent = new Intent();	    
		Log.i(" response",response.getHttpResult());
		
		if(response == null)
		{
			Log.i(" inside WS_SYNCH_NETWORK_ERROR"," WS_SYNCH_NETWORK_ERROR");
			resp_intent.putExtra("response", response);
			resp_intent.putExtra("report_uuid", mReportUUID);
			resp_intent	.setAction(Const.WS_RESPONSE_ACTIONS.WS_SYNCH_NETWORK_ERROR);
		}
		
		//else if (message.equals("")) {
			else if (message.equals("successful") && status.equals("1"))  
			{			
			  Log.i(" inside WS_ADD_REPORT_SUCCESS"," WS_ADD_REPORT_SUCCESS");
			  resp_intent	.setAction(Const.WS_RESPONSE_ACTIONS.WS_ADD_REPORT_SUCCESS);
			  resp_intent.putExtra ("report_uuid",   mReportUUID);
			  resp_intent.putExtra ("sync_group_id", mSyncGroupId);
			  resp_intent.putExtra ("response", result);
	 	    }
			else if (!message.equals("successful") || !status.equals("1")) 
			{
				Log.i(" inside WS_SYNCH_NETWORK_ERROR"," WS_SYNCH_NETWORK_ERROR");
				resp_intent .putExtra("response", result);
				resp_intent .putExtra("report_uuid", mReportUUID);
				resp_intent	.setAction(Const.WS_RESPONSE_ACTIONS.WS_SYNCH_NETWORK_ERROR);
			} 
			else
			{
				 Log. i(" inside final else WS_SYNCH_NETWORK_ERROR"," WS_SYNCH_NETWORK_ERROR");
				 resp_intent.putExtra("response", result);
				 resp_intent.putExtra("report_uuid", mReportUUID);
				 resp_intent.setAction(Const.WS_RESPONSE_ACTIONS.WS_SYNCH_NETWORK_ERROR);
			} 
		
		DetectorInspectorApplication.getInstance().getContext()	.sendBroadcast(resp_intent);
		// UserProfile.getInstance().getContext().sendBroadcast(resp_intent);
		super.onPostExecute(result);

	}

}
