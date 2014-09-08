package sdei.detector.inspector.sync.service.ws;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.entity.FileEntity;
import org.json.JSONException;
import org.json.JSONObject;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.util.Const;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.detector.inspector.lib.util.Util;
import com.google.gson.JsonObject;
import com.google.myjson.Gson;

public class UploadPhotoSyncTask extends WSClientSyncTask {

	private String mPhotoUUID;
	private String mReportUUID;
	private Context mContext;
	private String mReportId;String resultString;

	// upload photo
	public UploadPhotoSyncTask(WSRequest request, String sync_grp_id,String report_uuid, String report_id, String photo_uuid,Context context) {
		super(request, sync_grp_id);

		mReportUUID = report_uuid;
		mPhotoUUID  = photo_uuid;
		mContext    = context;
		mReportId   = report_id;
	}

	@Override
	protected WSResponse doInBackground(WSRequest... request) {
		// TODO Auto-generated method stub
		WSResponse response = null;
		WSClient client = new WSClient();
		
		
		try {
			Gson gson = new Gson();
			String photo_uuid = mPhotoUUID;
			// create photo jpeg file with comments on it
			Util.createUploadedPhoto(mContext, mContext.getResources(),	photo_uuid);
			
			// add the new photo picture in the http post body
			HttpEntity http_entity = new FileEntity(new File(Const.ENV_SETTINGS.DETECTOR_LARGE_PHOTO_DIR + photo_uuid+ ".jpg"), "image/jpeg");
			String url = request[0].getUrl();
			url += photo_uuid;
			url += "&rid=" + mReportId;
			request[0].setUrl(url);

			Log.i("TEST", "Image upload url is: " + request[0].getUrl());
			Log.i("TEST", "Image upload url is: " + http_entity.getContent().toString());

			if   (Const.DEBUG) 
			{ 
				 Log.i("TEST", "IMage upload url is: " + request[0].getUrl());
			}
			response = client.sendHttpRequest(request[0], http_entity);

		} catch (Exception e) 
		{
			e.printStackTrace();
		}

		return response;
	}

	@SuppressWarnings("unused")
	@Override
	protected void onPostExecute(WSResponse result)     {

		
			resultString = String.valueOf(result.getHttpResult());
			Log.i("resultString","resultString = "+resultString);

			Intent resp_intent = new Intent();

		if (result == null)  
		{
			resp_intent.putExtra("response", result);
			resp_intent.putExtra("report_uuid", mReportUUID);
			resp_intent.setAction(Const.WS_RESPONSE_ACTIONS.WS_SYNCH_NETWORK_ERROR);
		
		} 
		
		else if(resultString.startsWith("OK"))
		{
			resp_intent.setAction(Const.WS_RESPONSE_ACTIONS.WS_UPLOAD_PHOTO_SUCCESS);
			resp_intent.putExtra("report_uuid", mReportUUID);
			resp_intent.putExtra("photo_uuid" , mPhotoUUID);
			resp_intent.putExtra("sync_group_id", mSyncGroupId);
			resp_intent.putExtra("response", result);
		}
		else if(!resultString.startsWith("OK"))
		{
			resp_intent.putExtra("response", result);
			resp_intent.putExtra("report_uuid", mReportUUID);
			resp_intent.setAction(Const.WS_RESPONSE_ACTIONS.WS_SYNCH_NETWORK_ERROR);
		
		}
		else
		{			
			resp_intent.putExtra("response", result);
			resp_intent.putExtra("report_uuid", mReportUUID);
			resp_intent.setAction(Const.WS_RESPONSE_ACTIONS.WS_SYNCH_NETWORK_ERROR);
		}
		
		DetectorInspectorApplication.getInstance().getContext()	.sendBroadcast (resp_intent);
		// UserProfile.getInstance().getContext().sendBroadcast(resp_intent);
		super.onPostExecute(result);

	}

}
