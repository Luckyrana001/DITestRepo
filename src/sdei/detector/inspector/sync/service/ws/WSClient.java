package sdei.detector.inspector.sync.service.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import sdei.detector.inspector.util.Const;
import android.util.Log;

public class WSClient extends DefaultHttpClient {

	/*
	 * Constructor
	 */
	public WSClient() {
		super();

		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 30000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,	timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 30000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		this.setParams(httpParameters);

	}

	/*
	 * Send the request to server
	 */
	public WSResponse sendHttpRequest(WSRequest request, HttpEntity http_entity) {
		try {
			Log.d("TEST", "WSClient send request " + request.getUrl());

			URI uri;
			HttpResponse http_response;
			InputStream data;
			WSResponse ws_response = new WSResponse();

			uri = new URI(request.getUrl());

			switch (request.getHttpMethod()) {
			case Const.HTTP_METHOD_CODES.HTTP_GET:
				
				try {
					HttpGet get_method = new HttpGet(uri);
					http_response = execute(get_method);
					ws_response.setHttpStatus(http_response.getStatusLine().getStatusCode());
					data = http_response.getEntity().getContent();
					ws_response.setHttpResult(generateString(data));
					Log.i("TEST","WSClient GET response: "+ ws_response.getHttpStatus());
					Log.i("TEST","WSClient GET Result: "+ ws_response.getHttpResult());
					
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
				}
				return ws_response;
				
			case Const.HTTP_METHOD_CODES.HTTP_POST:
				Log.d("TEST", "WSClient POST request " + http_entity.toString());
				try {
					HttpPost post_method = new HttpPost(uri);
					post_method.setEntity(http_entity);					
					http_response = execute(post_method);
					
					//System.out.print(http_response.getStatusLine()	.getStatusCode());
					//System.out.print(http_response.getEntity().getContent());
					
					ws_response.setHttpStatus(http_response.getStatusLine()	.getStatusCode());
					data = http_response.getEntity().getContent();
					ws_response.setHttpResult(generateString(data));
					
					Log.i("TEST","WSClient GET Status: "+ ws_response.getHttpStatus());
					Log.i("TEST","WSClient GET Result: "+ ws_response.getHttpResult());
				} 
				
				catch (SocketTimeoutException e) {
					
					e.printStackTrace();
					// Log.d("TEST","socket time out");
					ws_response = null;
				}
				return ws_response;
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Parse the HTTP response to string
	 */
	private String generateString(InputStream in) {
		// Log.d("TEST", "WSClient parsing response");
		InputStreamReader data = new InputStreamReader(in);
		StringBuffer builder = new StringBuffer();
		char[] buffer = new char[1024];
		int len = 0;
		int total = 0;
		try {
			while ((len = data.read(buffer)) > 0) {
				builder.append(buffer, 0, len);
				total += len;
			}
			data.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return builder.toString();
	}
}
