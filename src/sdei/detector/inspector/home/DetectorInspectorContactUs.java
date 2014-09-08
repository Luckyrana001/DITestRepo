package sdei.detector.inspector.home;

import sdei.detector.inspector.R;
import sdei.detector.inspector.util.Utils;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetectorInspectorContactUs extends sdei.detector.inspector.BaseActivity {

	public static final String ACTION = "sdei.detector.inspector.home.DetectorInspectorContactUs";

	TextView fbTextView, twTextView, mailTextView, callTextView1,
			callTextView2, callTextView3;
	ImageView fbImageView, twImageView, mailImageView, callImageView1,
			callImageView2, callImageView3;
	Button contactus_cancel_btnButton;

	private ImageView mBackBtn;

	@Override
	public void onBeforeCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onBeforeCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_us);
	}

	@Override
	public void getView() {
		TextView mTiTextView = (TextView) findViewById(R.id.title_txt);
		mTiTextView.setText(getResources().getString(R.string.contact_us));

		findViewById(R.id.home_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();
			}
		});

		fbTextView = (TextView) findViewById(R.id.linkedin_txt);
		fbTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = "http://www.linkedin.com/company/detector-inspector-pty-ltd/";
				Utils.GoToUrl(url, DetectorInspectorContactUs.this);
				// Bundle bundle = new Bundle();
				// bundle.putString("url", url);
				//
				// Intent i = new Intent();
				// i.setAction(DetectorInspection_WebLinkActivity.ACTION);
				// i.putExtra("data", bundle);
				// i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(i);

			}
		});
		fbImageView = (ImageView) findViewById(R.id.linkedin_image);
		fbImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = "http://www.linkedin.com/company/detector-inspector-pty-ltd/";
				Utils.GoToUrl(url, DetectorInspectorContactUs.this);
				// Bundle bundle = new Bundle();
				// bundle.putString("url", url);
				//
				// Intent i = new Intent();
				// i.setAction(DetectorInspection_WebLinkActivity.ACTION);
				// i.putExtra("data", bundle);
				// i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(i);

			}
		});
		twTextView = (TextView) findViewById(R.id.twitterinspectionmanger);
		twTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stubsa

				String url = "https://twitter.com/DetectorInspect";
				Utils.GoToUrl(url, DetectorInspectorContactUs.this);
				// Bundle bundle = new Bundle();
				// bundle.putString("url", url);
				//
				// Intent i = new Intent();
				// i.setAction(DetectorInspection_WebLinkActivity.ACTION);
				// i.putExtra("data", bundle);
				// i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(i);

			}
		});
		twImageView = (ImageView) findViewById(R.id.twitterinspectionmangerimage);
		twImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = "https://twitter.com/DetectorInspect";

				Utils.GoToUrl(url, DetectorInspectorContactUs.this);
				// Bundle bundle = new Bundle();
				// bundle.putString("url", url);
				//
				// Intent i = new Intent();
				// i.setAction(DetectorInspection_WebLinkActivity.ACTION);
				// i.putExtra("data", bundle);
				// i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(i);
			}
		});

		mailTextView = (TextView) findViewById(R.id.mail_txt);
		mailTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.sendMail(DetectorInspectorContactUs.this);

			}
		});
		mailImageView = (ImageView) findViewById(R.id.mail_image);
		mailImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.sendMail(DetectorInspectorContactUs.this);

			}
		});

		callTextView1 = (TextView) findViewById(R.id.call_detector_inspection);
		callTextView1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				callNumber("1300134563");
			}
		});

		findViewById(R.id.call_technical_support).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						callNumber("+61431723904");
					}
				});
		findViewById(R.id.call_technical_support_one).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						callNumber("+61431431615");
					}
				});
		findViewById(R.id.call_general_support).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						callNumber("+61488301544");
					}
				});
		findViewById(R.id.fax_call).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						callNumber("0395230672");
					}
				});
	}

	protected void callNumber(String number) {
		String num = "tel:" + number;
		startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(num)));
	}

	
}
