package sdei.detector.inspector.sync.service.ws;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.util.Const;
import sdei.detector.inspector.util.Utils;
import android.content.Intent;
import android.util.Log;

    public class LoadPropertyBookingTask extends WSClientSyncTask  {
	private String technicianId;

	// upload photo
	public    LoadPropertyBookingTask(WSRequest request, String technicianId) {
		super(request);
		this.technicianId = technicianId;
	}

	@Override
	protected WSResponse doInBackground(WSRequest... request)  {
		// TODO Auto-generated method stub
		WSResponse response = null;
		WSClient client = new WSClient();

		try {
			String post = "{\"technicianId\" : \"" + technicianId + "\" }";

			Log.d("TESTing ", post);
			if (Const.DEBUG) {
				Log.d("TEST debug" , post);
			}

			StringEntity se = new StringEntity(post);
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
			// HttpEntity http_entity = new StringEntity(post);
			response = client.sendHttpRequest(request[0], se);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("response ", String.valueOf(response));
		return response;
	}

	@Override
	protected void onPostExecute(WSResponse result) {

		Intent resp_intent = new Intent();

		if (Const.DEBUG) {
			String localResult = Utils.readXMLinString("booking_new.txt");
			DetectorInspectorApplication.getInstance().setResponse(localResult);
			// UserProfile.getInstance().setResponse(localResult);
			resp_intent	.setAction(Const.WS_RESPONSE_ACTIONS.WS_LOAD_PROPERTY_BOOKING_SUCCESS);

		} else {
			if (result == null) {
				resp_intent.putExtra("response", result);
				resp_intent	.setAction(Const.WS_RESPONSE_ACTIONS.WS_NETWORK_BOOKING_ERROR);
			} else {
				
				DetectorInspectorApplication.getInstance().setResponse(	result.getHttpResult());
				// UserProfile.getInstance().setResponse(result.getHttpResult());
				resp_intent	.setAction(Const.WS_RESPONSE_ACTIONS.WS_LOAD_PROPERTY_BOOKING_SUCCESS);
				// resp_intent.putExtra("response", result);
			}
		}
		
		DetectorInspectorApplication.getInstance().getContext()	.sendBroadcast(resp_intent);
		// UserProfile.getInstance().getContext().sendBroadcast(resp_intent);
		super.onPostExecute(result);

	}

}
