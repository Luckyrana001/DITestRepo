package sdei.detector.inspector.sync.service.ws;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.util.Utils;
import android.content.Intent;
import android.util.Log;

import com.detector.inspector.lib.util.Const;

public class UnAllocatedProperty extends WSClientSyncTask {

	private String technicianId;
	private String bookingId;
	private String reason;
	private String propertyId;
	private String key;

	// upload photo
	public UnAllocatedProperty(WSRequest request, String technicianId,
			String bookingId, String reason, String propertyId, String key) {
		super(request);
		this.technicianId = technicianId;
		this.bookingId = bookingId;
		this.reason = reason;
		this.propertyId = propertyId;
		this.key = key;
	}

	@Override
	protected WSResponse doInBackground(WSRequest... request) {
		// TODO Auto-generated method stub
		WSResponse response = null;
		WSClient client = new WSClient();

		try {
			String post = "{\"BookingId_\" : " + bookingId + ", \"Key_\" : \""
					+ key + "\" , \"Notes_\" : \"" + reason
					+ "\" , \"propertyId\" : \"" + propertyId

					+ "\" }";

			Log.d("TEST", post);
			if (Const.DEBUG) {
				Log.d("TEST", post);
			}

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

		if (Const.DEBUG) {
			String localResult = Utils.readXMLinString("booking_new.txt");
			DetectorInspectorApplication.getInstance().setResponse(localResult);

			// UserProfile.getInstance().setResponse(localResult);
			resp_intent
					.setAction(Const.WS_RESPONSE_ACTIONS.WS_UNALLOCATED_BOOKING_SUCCESS);

		} else {
			if (result == null) {
				resp_intent.putExtra("response", result);
				resp_intent
						.setAction(Const.WS_RESPONSE_ACTIONS.WS_NETWORK_BOOKING_ERROR);
			} else {
				DetectorInspectorApplication.getInstance().setResponse(
						result.getHttpResult());
				// UserProfile.getInstance().setResponse(result.getHttpResult());
				resp_intent
						.setAction(Const.WS_RESPONSE_ACTIONS.WS_UNALLOCATED_BOOKING_SUCCESS);
			}
		}
		DetectorInspectorApplication.getInstance().getContext()
				.sendBroadcast(resp_intent);

		// UserProfile.getInstance().getContext().sendBroadcast(resp_intent);
		super.onPostExecute(result);

	}

}
