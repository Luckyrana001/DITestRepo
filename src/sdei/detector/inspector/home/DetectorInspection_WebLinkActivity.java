package sdei.detector.inspector.home;

import sdei.detector.inspector.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class DetectorInspection_WebLinkActivity extends Activity {
	/** Called when the activity is first created. */
	public static final String ACTION = "sdei.detector.inspector.home.DetectorInspection_WebLinkActivity";

	WebView web;
	ProgressBar progressBar;

	ProgressDialog progressDialog;
	private String urlString;

	private int comingFromInt;

	private ImageView headerImageView;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString("url", urlString);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		if (savedInstanceState == null) {
			urlString = intent.getBundleExtra("data").getString("url");

		} else {
			urlString = savedInstanceState.getString("url");

		}
		setContentView(R.layout.activity_contact_us);

		web = (WebView) findViewById(R.id.webview);

		progressDialog = ProgressDialog.show(
				DetectorInspection_WebLinkActivity.this, "", "Loading...");
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setBuiltInZoomControls(true);
		web.getSettings().setSupportZoom(true);

		web.getSettings().setDomStorageEnabled(true);

		if (urlString != null && urlString.length() > 0) {
			if (urlString.equalsIgnoreCase("--")) {
				// Display alert here
				AlertDialog alertDialog = new AlertDialog.Builder(
						DetectorInspection_WebLinkActivity.this).create();
				alertDialog.setTitle("Message");
				alertDialog.setMessage("Site not published");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								// here you can add functions
								dialog.dismiss();
							}
						});
				// alertDialog.setIcon(R.drawable.icon);
				alertDialog.show();
			} else {
				// open webview
				web.setWebViewClient(new myWebClient());
				web.loadUrl(urlString);
			}

		}
	}

	class myWebClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub

			view.loadUrl(url);
			return true;

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			// super.onPageFinished(view, url);
			if (progressDialog.isShowing()) {
				progressBar.setVisibility(View.GONE);
				progressDialog.dismiss();
			}

		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
}
