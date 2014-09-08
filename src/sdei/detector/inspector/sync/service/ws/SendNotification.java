package sdei.detector.inspector.sync.service.ws;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.UserProfile;
import android.content.Intent;
import android.util.Log;

import com.detector.inspector.lib.util.Const;

public class SendNotification extends WSClientSyncTask {

	private String message;

	public SendNotification(WSRequest request, String message) {
		super(request, message);
		this.message = message;
	}

	@Override
	protected WSResponse doInBackground(WSRequest... request) {
		// TODO Auto-generated method stub
		WSResponse response = null;
		WSClient client = new WSClient();

		try {

		String mail = "bookings@detectorinspector.com.au";
			//String mail = "Akhilma@smartdatainc.net";
			String subject = "inspection not complete at time bracket Notifaction";
			String toName = "Detector Inspector";

			String post = "{\"toEmail\" : \"" + mail + "\",\"toName\": \""
					+ mail + "\",\"_Subject\": \"" + subject
					+ "\",\"emailBody\": \"" + message + "\" }";

			Log.d("Post", post);

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
			resp_intent
					.setAction(Const.WS_RESPONSE_ACTIONS.WS_NETWORK_START_INSPECTION_ERROR);
		} else {
			DetectorInspectorApplication.getInstance().setResponse(
					result.getHttpResult());
			// UserProfile.getInstance().setResponse(result.getHttpResult());
			resp_intent
					.setAction(Const.WS_RESPONSE_ACTIONS.WS_SEND_NOTIFICATION_SUCESS);
			// resp_intent.putExtra("response", result);
		}

		DetectorInspectorApplication.getInstance().getContext()
				.sendBroadcast(resp_intent);
		// UserProfile.getInstance().getContext().sendBroadcast(resp_intent);
		super.onPostExecute(result);
	}

}