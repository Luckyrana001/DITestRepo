package sdei.detector.inspector;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity implements IViewActivity {
	public static boolean isAppWentToBg = false;
	public static boolean isWindowFocused = false;
	public static boolean isMenuOpened = false;
	public static boolean isBackPressed = false;
	private static final int DIALOG_ID_PROGRESS_DEFAULT = 0x174980;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		onBeforeCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		getView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		getActivtyResult(requestCode, resultCode, data);
	}

	@Override
	public void onBeforeCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showProgress() {
		showDialog(DIALOG_ID_PROGRESS_DEFAULT);
	}

	public void hideProgress() {
		try {
			removeDialog(DIALOG_ID_PROGRESS_DEFAULT);
		} catch (IllegalArgumentException iae) {
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ID_PROGRESS_DEFAULT:
			ProgressDialog dlg = new ProgressDialog(this);
			// dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// dlg.setTitle("Detector Inspector");
			dlg.setMessage("loading...");
			dlg.setCancelable(true);
			dlg.setCanceledOnTouchOutside(false);

			return dlg;
		default:
			return super.onCreateDialog(id);

		}
	}

	@Override
	public void getView() {

	}

	public void getActivtyResult(int requestCode, int resultCode, Intent data) {

	}

	@Override
	protected void onStart() {
		super.onStart();
		UserProfile userProfile = DetectorInspectorApplication.getInstance();
		if (userProfile.getContext() == null) {
			Log.e("GOT IN CONDITION", "GOT IN CONDITION");
			Log.e("GOT IN CONDITION", "GOT IN CONDITION");
			Log.e("GOT IN CONDITION", "GOT IN CONDITION");
			Log.e("GOT IN CONDITION", "GOT IN CONDITION");
			Log.e("GOT IN CONDITION", "GOT IN CONDITION");
			Intent intent = new Intent(BaseActivity.this,DetectorInspector_LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			// return;
		}

		applicationWillEnterForeground();

	}

	private void applicationWillEnterForeground() {
		if (isAppWentToBg) {
			isAppWentToBg = false;
			// Toast.makeText(getApplicationContext(), "App is in foreground",
			// Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		applicationdidenterbackground();
	}

	public void applicationdidenterbackground() {
		if (!isWindowFocused) {
			isAppWentToBg = true;
			// Toast.makeText(getApplicationContext(),
			// "App is Going to Background", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {

		if (this instanceof DetectorInspector_LoginActivity) {

		} else {
			isBackPressed = true;
		}
		super.onBackPressed();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		isWindowFocused = hasFocus;

		if (isBackPressed && !hasFocus) {
			isBackPressed = false;
			isWindowFocused = true;
		}

		super.onWindowFocusChanged(hasFocus);
	}

}