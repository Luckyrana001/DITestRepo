package sdei.detector.inspector.sync.service.ws;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.DetectorInspector_LoginActivity;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.util.Const;
import sdei.detector.inspector.util.Utils;
import android.content.Intent;
import android.util.Log;

public class LoginTask extends WSClientSyncTask {

	private String mEmail;
	private String mPass;

	// upload photo
	public LoginTask(WSRequest request, String email, String pass) {
		super(request);
		// Log.d("TEST on LoginTask","Step-2");
		mEmail = email;
		mPass  = pass;

	}

	@Override
	protected WSResponse doInBackground(WSRequest... request) {
		// TODO Auto-generated method stub
		WSResponse response = null;
		WSClient client = new WSClient();

		try {

			String post = "{\"emailId\" : \"" + mEmail	+ "\" , \"password\" : \"" + mPass + "\"}";
			Log.d("Login TEST", post);

			Log.d("TEST on LoginTask", post);

			StringEntity se = new StringEntity(post);
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			// HttpEntity http_entity = new StringEntity(se);

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
			String localResult = Utils.readXMLinString("login.txt");
			DetectorInspectorApplication.getInstance().setResponse(localResult);

			// UserProfile.getInstance().setResponse(localResult);
			resp_intent.setAction(Const.WS_RESPONSE_ACTIONS.WS_LOGIN_SUCCESS);

		} else {
			if (result == null) {
				resp_intent.putExtra("response", result);
				resp_intent	.setAction(Const.WS_RESPONSE_ACTIONS.WS_NETWORK_ERROR);
			}   else  {
				// resp_intent.putExtra("response", result);
				DetectorInspectorApplication.getInstance().setResponse(	result.getHttpResult());
				// UserProfile.getInstance().setResponse(result.getHttpResult());
				resp_intent.setAction(Const.WS_RESPONSE_ACTIONS.WS_LOGIN_SUCCESS);
			}
		}

		
		/* Broadcasting value so that these value can be accessed by  reciever in need */
		DetectorInspectorApplication.getInstance().getContext()	.sendBroadcast(resp_intent);
		// UserProfile.getInstance().getContext().sendBroadcast(resp_intent);
		super.onPostExecute(result);

	}

}
