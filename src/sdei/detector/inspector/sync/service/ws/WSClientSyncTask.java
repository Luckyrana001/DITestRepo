package sdei.detector.inspector.sync.service.ws;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.util.Const;
import sdei.detector.inspector.util.Utils;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Base class for Client Sync background tasks.
 * 
 * 
 * @author luckyrana
 * 
 */
public class WSClientSyncTask extends AsyncTask<WSRequest, Integer, WSResponse> {

	
	/**
	 *   WebService Client Sync Callback
	 */
	public static interface WSClientSyncCallback {

		/**
		 * Called when a background sync task is completed to broadcast the
		 * finished event.
		 * 
		 * @param sync_id
		 */
		public void syncFinished(String sync_id);
	}

	protected String mSyncGroupId;
	protected String mSyncId;
	protected WSRequest mRequest;
	protected WSClientSyncCallback mCallback;

	// general load property list, report template
	public WSClientSyncTask(WSRequest request, String sync_grp_id) {
		mRequest     = request;
		mSyncId      = Utils.getUUID();
		mSyncGroupId = sync_grp_id;
	}

	// general load property list, report template
	public WSClientSyncTask (WSRequest request)          {
		mRequest = request;
		mSyncId = Utils.getUUID();
		mSyncGroupId = "";
	}

	public String getSyncId() {
		Log.d("getSyncId", "Step-11..syncId"+String.valueOf(mSyncId));
		Log.d("mSyncId", "mSyncId....."+String.valueOf(mSyncId));
		return mSyncId;
	}

	public String getSyncGroupId() 
	{
		Log.d("getSyncGroupId", "getSyncGroupId....."+String.valueOf(mSyncGroupId));
		return mSyncGroupId;
	}

	public WSRequest getRequest() {
		Log.d("WsClient Sync", "Step-9");
		Log.d("getRequest", "mRequest....."+String.valueOf(mRequest));
		return mRequest;
	}

	public void setCallback(WSClientSyncCallback callback) {
		Log.d("WsClient Sync", "Step-5");
		Log.d("setCallback", "setCallback....."+String.valueOf(callback));
		mCallback = callback;

	}

	@Override
	protected WSResponse doInBackground(WSRequest... request) {
		return null;
	}

	@Override
	protected void onPostExecute(WSResponse result) {
		// Broadcast the response
		Log.d("onPostExecute on WSClientSyncTask", "SyncService broadcast response");
		Log.d("mCallback.syncFinished(mSyncId)", "mSyncId....."+String.valueOf(mSyncId));
		mCallback.syncFinished(mSyncId);
	}

	/**
	 *   Called on main thread to update sync progress
	 */
	@Override
	protected void onProgressUpdate(Integer... progress) {

		Log.d("TEST on WSClientSyncTask", "SyncService broadcast progress");

		Intent resp_intent = new Intent();
		resp_intent.setAction(Const.WS_RESPONSE_ACTIONS.WS_SYNC_IN_PROGRESS);
		resp_intent.putExtra("progress", progress[0].intValue());

		
		Log.i("WSBrobroadcast sent", "broadcast id "+String.valueOf(resp_intent));
		// Use user profile singleton to send progress broadcasts.
		DetectorInspectorApplication.getInstance().getContext()	.sendBroadcast(resp_intent);
		

		// UserProfile.getInstance().getContext().sendBroadcast(resp_intent);

	}

}
