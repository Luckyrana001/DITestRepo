package sdei.detector.inspector.home;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.R;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.sync.report.ReportPhoto;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.detector.inspector.lib.photo.ImageUtil;
import com.detector.inspector.lib.util.Const;
import com.detector.inspector.lib.util.Util;

public class DetectorInspector_PhotoViewActivity extends sdei.detector.inspector.BaseActivity {

	public static final String ACTION = "sdei.detector.inspector.home.DetectorInspector_PhotoViewActivity";
	private UserProfile mProfile;
	private Button mHomeBtn;
	Uri mImageCaptureUri;
	ReportPhoto mPhoto;
	private ImageView mImage;
	private Bitmap bmp = null;
	String filename;
	String path;

	@Override
	public void onBeforeCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onBeforeCreate(savedInstanceState);

		mProfile = DetectorInspectorApplication.getInstance();
		// mProfile = UserProfile.getInstance();
		mPhoto = mProfile.getReportPhoto();
		setContentView(R.layout.activity_photo_view);

	}

	@Override
	public void getView() {
		TextView mTiTextView = (TextView) findViewById(R.id.title_txt);
		mTiTextView.setText("Photo View");
		mHomeBtn = (Button) findViewById(R.id.home_btn);
		mHomeBtn.setBackgroundResource(R.drawable.button_selector_save_btn);
		mHomeBtn.setText("Done");

		mImage = (ImageView) findViewById(R.id.image_view);
		showProgress();
		setImageToImageView();
		hideProgress();
	}

	private void setImageToImageView() {

		filename = mPhoto.getPhotoId();
		path = Const.ENV_SETTINGS.DETECTOR_PHOTO_DIR + filename + ".jpg";

		int width = Math
				.round(Const.REPORT_PHOTO_SIZE_VALUES.LARGE_BMP_WIDTH
						* Util.getScreenScale(DetectorInspector_PhotoViewActivity.this));
		int height = Math
				.round(Const.REPORT_PHOTO_SIZE_VALUES.LARGE_BMP_HEIGHT
						* Util.getScreenScale(DetectorInspector_PhotoViewActivity.this));

		// int width = Math
		// .round(Const.REPORT_PHOTO_SIZE_VALUES.UPLOAD_BMP_WIDTH
		// * Util.getScreenScale(DetectorInspector_PhotoViewActivity.this));
		// int height = Math
		// .round(Const.REPORT_PHOTO_SIZE_VALUES.UPLOAD_BMP_HEIGHT
		// * Util.getScreenScale(DetectorInspector_PhotoViewActivity.this));
		bmp = ImageUtil.decodePhoto(filename, width, height);
		mImage.setImageBitmap(bmp);
		// new ZoomActivity(mImage);
	}

	public void buttonClick(View v) {
		switch (v.getId()) {
		case R.id.home_btn:
			finish();
			break;
		default:
			break;
		}
	}

}
