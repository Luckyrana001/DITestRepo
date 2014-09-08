package sdei.detector.inspector.sync.service.ws;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.util.Const;
import android.content.Intent;
import android.util.Log;

public class SaveLocation extends WSClientSyncTask {

	private String technicianId;
	private String lat;
	private String lng;

	public SaveLocation(WSRequest request, String lat, String lng,
			String technicianId) {
		super(request);
		this.technicianId = technicianId;
		this.lat = lat;
		this.lng = lng;
	}

	@Override
	protected WSResponse doInBackground(WSRequest... request) {
		// TODO Auto-generated method stub
		WSResponse response = null;
		WSClient client = new WSClient();

		try {

			int techId = Integer.parseInt(technicianId);

			String post = "{\"technicianId\" : " + techId + " , \"lat\" : \""
					+ lat + "\" , \"lng\" : \"" + lng + "\"}";

			Log.d("TEST", post);

			StringEntity se = new StringEntity(post);
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			// HttpEntity http_entity = new StringEntity(post);
			response = client.sendHttpRequest(request[0], se);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	@Override
	protected void onPostExecute(WSResponse result) {

		Intent resp_intent = new Intent();

		if (result == null) {
			resp_intent.putExtra("response", result);
			resp_intent.setAction(Const.WS_RESPONSE_ACTIONS.WS_NETWORK_ERROR);
		} else {
			DetectorInspectorApplication.getInstance().setResponse(
					result.getHttpResult());

			// UserProfile.getInstance().setResponse(result.getHttpResult());
			resp_intent
					.setAction(Const.WS_RESPONSE_ACTIONS.WS_LOAD_TRACK_LOCATION_SERVER_SUCESS);
		}

		DetectorInspectorApplication.getInstance().getContext()
				.sendBroadcast(resp_intent);
		// UserProfile.getInstance().getContext().sendBroadcast(resp_intent);
		super.onPostExecute(result);

	}
}