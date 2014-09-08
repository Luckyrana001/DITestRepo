package sdei.detector.inspector.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.DetectorInspector_LoginActivity;
import sdei.detector.inspector.R;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.home.AlarmManagerBroadcastReceiver;
import sdei.detector.inspector.home.DetectorInspector_BookingActivity;
import sdei.detector.inspector.home.DetectorInspector_EditActivity;
import sdei.detector.inspector.home.DetectorInspector_HomeActivity;
import sdei.detector.inspector.home.DetectorInspector_PhotoViewActivity;
import sdei.detector.inspector.home.DetectorInspector_ReorderActivity;
import sdei.detector.inspector.home.DetectorInspector_SynchActivity;
import sdei.detector.inspector.home.Detector_Inspector_StartInspectionActivity;
import sdei.detector.inspector.home.Detector_Inspector_StartNewInspectionActivity;
import sdei.detector.inspector.home.Detector_Inspector_StartNewInspectionWithHistoryActivity;
import sdei.detector.inspector.home.SendNotificationActivity;
import sdei.detector.inspector.sync.Settings;
import sdei.detector.inspector.sync.report.Report;
import sdei.detector.inspector.sync.report.ReportSection;
import sdei.detector.inspector.sync.service.engine.ISyncEngine;
import sdei.detector.inspector.sync.service.ws.WSRequest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.detector.inspector.lib.json.JsonUtil;
import com.detector.inspector.lib.model.Contact;
import com.detector.inspector.lib.model.Inspection;
import com.detector.inspector.lib.photo.CropOption;
import com.detector.inspector.lib.photo.CropOptionAdapter;
import com.detector.inspector.lib.util.Const;
import com.detector.inspector.lib.util.Util;
import com.detector.inspector.lib.widget.Mail;
import com.detector.inspector.synch.report.ReportItem;
import com.google.myjson.Gson;
import com.myandroidlib.interfaces.ServiceHitInterface;
import com.myandroidlib.server.communication.WebServiceHit;

public class Utils {

	// UserProfile mProfile = UserProfile.getInstance();
	UserProfile mProfile = DetectorInspectorApplication.getInstance();

	private static Uri mImageCaptureUri;
	static boolean flag = false;
	private static AlarmManagerBroadcastReceiver alarm;

	public static long Date_to_MilliSeconds(int day, int month, int year, int hour, int minute)  
	{
		Calendar c = Calendar.getInstance(Locale.getDefault());
		c.set(year, month - 1, day, hour, minute, 00);
		return c.getTimeInMillis();
	}

	public static float getScreenScale() 
	{
		// return UserProfile.getInstance().getContext().getResources()
		// .getDisplayMetrics().density;
		return DetectorInspectorApplication.getInstance().getContext().getResources().getDisplayMetrics().density;
	}

	public static float getScreenWidth() {

		// return UserProfile.getInstance().getContext().getResources()
		// .getDisplayMetrics().widthPixels;
		return DetectorInspectorApplication.getInstance().getContext().getResources().getDisplayMetrics().widthPixels;
	}

	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	public static String parseNullString(String s) {
		String result = (s == null) ? "null" : s;
		return result;
	}

	public static String getTypeFromSectionName(String name) {
		String section_type = name;

		String regEx = "^([a-zA-Z\\s/]+)[0-9]+$";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(name);
		if (m.find()) 
		{
			section_type = m.group(1);
		}
		return section_type;
	}

	public static int getNumberFromSectionName(String name) {

		int num = 0;
		String regEx = "^([a-zA-Z\\s]+)([0-9]+)$";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(name);
		if (m.find()) {
			num = Integer.parseInt(m.group(2));
		}
		return num;
	}

	public static void hideKeyboard(Activity activity, View v) {
		InputMethodManager imm;
		imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
	}

	public static Settings loadPreference(Activity activity) {
		Settings settings = new Settings();
		SharedPreferences sp = activity.getSharedPreferences(
				"detector_settings", 0);
		settings.date = sp.getString("quality", "date");
		settings.use_pre_text = sp.getBoolean("use_pre_text", true);
		return settings;
	}

	public static void savePreference(Activity activity, Settings sd) {
		SharedPreferences sp = activity.getSharedPreferences(
				"detector_settings", 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("quality", sd.date);
		editor.putBoolean("use_pre_text", sd.use_pre_text);
		editor.commit();
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 * 
	 * @param login
	 * @param activity
	 * @param mPasswordView
	 * @param mEmailView
	 */
	public static boolean attemptLogin(EditText mEmailView,	EditText mPasswordView, Activity activity) {

		// Reset errors.
		mEmailView.setError(null);
		if (mPasswordView != null)
			mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String mEmail = mEmailView.getText().toString();
		String mPassword = null;
		if (mPasswordView != null)
			mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (mPasswordView != null) {
			if (TextUtils.isEmpty(mPassword)) {
				mPasswordView.setError(activity	.getString(R.string.error_field_required));
				focusView = mPasswordView;
				cancel = true;
			} else if (mPassword.length() < 4) {
				mPasswordView.setError(activity	.getString(R.string.error_invalid_password));
				focusView = mPasswordView;
				cancel = true;
			}
		}
		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(activity.getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} /*
		 * else if (mEmail.contains("@")) { mEmailView.setError(activity
		 * .getString(R.string.error_invalid_email)); focusView = mEmailView;
		 * cancel = true; }
		 */

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
			return false;
		} else {
			return true;

		}
	}

	public static int[] calculateTextSize(String text, int font_size,
			boolean isBold) {

		Paint paint = new Paint();
		Rect bounds = new Rect();
		int text_height = 0;
		int text_width = 0;
		if (isBold) {
			paint.setTypeface(Typeface.DEFAULT_BOLD);
		}
		paint.setTextSize(font_size);
		paint.getTextBounds(text, 0, text.length(), bounds);
		text_height = bounds.height();
		text_width = bounds.width();

		return new int[] { text_width, text_height };
	}

	public static String todayDate() {

		Time time = new Time();
		time.set(System.currentTimeMillis());

		return time.format("%d/%m/%Y");

	}

	public static String currentDateAsString() {

		Time time = new Time();
		time.set(System.currentTimeMillis());
		String str = time.format("%Y-%m-%dT%T.0000000%z");
		String str1 = str.substring(0, str.length() - 2);
		String str2 = str.substring(str.length() - 2, str.length());
		str = str1 + ":" + str2;

		return str;
	}

	public static void showToastAlert(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static void showAlert(Context context, String title, String message) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();

					}
				}).create().show();
	}

	// public static boolean showAlertInspection(Context context, String title,
	// String message) {
	// // TODO Auto-generated method stub
	//
	// new AlertDialog.Builder(context).setTitle(title).setMessage(message)
	// .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// // TODO Auto-generated method stub
	// dialog.dismiss();
	// flag=true;
	// return true;
	// }
	// })
	// .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// // TODO Auto-generated method stub
	// dialog.dismiss();
	// flag=false;
	//
	// }
	// }).create().show();
	// // return flag;
	// }

	public static void showAlertWithMail(final Activity activity, String title,
			final String message, final ISyncEngine mSyncEngine,
			final String uuid) {
		// TODO Auto-generated method stub
		Context context = activity;
		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						sendMailToServer(activity, message, mSyncEngine, uuid);

					}

				}).create().show();
	}

	public static void sendMailToServer(Activity activity,
			final String message, ISyncEngine mSyncEngine, String uuid) {

		DatabaseUtil.updateSendBroadCastStatus(activity, uuid);

		WSRequest request = new WSRequest(
				Const.WS_URLS.WS_SERVICE_SEND_EMAIL_NOTIFICATION_URL,
				Const.HTTP_METHOD_CODES.HTTP_POST,
				Const.WS_REQUEST_TYPES.sendmailNotification);
		try {
			String mLoginTaskId = mSyncEngine.sendNotificationMessage(request,
					message);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendMailToServerASYNC(Context activity,
			final String message, String uuid) {
		System.out.println("Akhil Malik");
		DatabaseUtil.updateSendBroadCastStatus(activity, uuid);

		String mail = "bookings@detectorinspector.com.au";
		//String mail = "Akhilma@smartdatainc.net";
		String subject = "inspection not complete at time bracket Notifaction";
		String toName = "Detector Inspector";

		String post = "{\"toEmail\" : \"" + mail + "\",\"toName\": \"" + toName
				+ "\",\"_Subject\": \"" + subject + "\",\"emailBody\": \""
				+ message + "\" }";
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(post);
			System.out.println("Json Object is ----->>>>"
					+ jsonObject.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new WebServiceHit(Const.WS_URLS.WS_SERVICE_SEND_EMAIL_NOTIFICATION_URL,
				jsonObject, new ServiceHitInterface() {

					@Override
					public void beforeExecute() {
						// TODO Auto-generated method stub

						Log.e("Sending Email Akhil", "Sending Email Akhil");

					}

					@Override
					public void afterExecute(String arg0) {
						// TODO Auto-generated method stub
						Log.e("SENT Email Akhil", "Sent Email Akhil");
					}
				}, 0);

	}

	public static class Test extends AsyncTask<Void, Void, Void> {
		Mail m;
		String message;
		Activity activity;

		public Test(String message, Activity activity) {
			this.message = message;
			this.activity = activity;
		}

		@Override
		protected Void doInBackground(Void... params) {
			m = new Mail("enggsdei2@gmail.com", "enggsdei2");
			String[] toArr = { "yogeshc@smartdatainc.net",
					"sumeet3100@gmail.com", "bookings@detectorinspector.com.au" };
			// m.setTo(toArr);
			// m.setFrom("enggsdei2@gmail.com");
			// m.setSubject("Detector Inspector");
			// m.setBody(message);
			try {
				if (m.send()) {
					Log.e("Email was sent successfully.", "test1");
				} else {
					Log.e("Email was not sent.", "test2");
				}
			} catch (Exception e) {

				Log.e("MailApp", "Could not send email", e);
			}
			return null;
		}

	}

	public static void sendMail(Activity activity) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_EMAIL,
				new String[] { "info@detectorinspector.com.au" });
		i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
		i.putExtra(Intent.EXTRA_TEXT, "body of email");
		try {
			activity.startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(activity, "There are no email clients installed.",
					Toast.LENGTH_SHORT).show();
		}
	}

	public static void showInspectionSubmitAlert(final Activity activity,
			final double[] latLng, final Inspection mProperty,
			final AlarmManagerBroadcastReceiver alarm, final String mDate) {
		// TODO Auto-generated method stub
		Context context = activity;

		new AlertDialog.Builder(context)
				.setTitle("Confirmation Message")
				.setMessage("Are you sure you wish to submit?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,	int which) {
								// TODO Auto-generated method stub
								if (alarm != null) {
									alarm.CancelAlarm(activity,	mProperty.getPropertyId());
								}
								DatabaseUtil.updateLatLongValue(latLng,	mProperty, activity, mDate);
								new AlarmManagerBroadcastReceiver().CancelAlarm(activity,	mProperty.getPropertyId());
								Intent sync_intent = new Intent();

								if (Util.isOnline(activity)) 
								{
									sync_intent.setAction(DetectorInspector_SynchActivity.ACTION);
								} 
								
								else 
								{
									sync_intent.setAction(DetectorInspector_BookingActivity.ACTION);
								}
								sync_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								activity.startActivity(sync_intent);
								dialog.dismiss();

							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();

					}
				}).create().show();
	}

	public static void GoToUrl(String url, Activity activity) {
		activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url
				.trim())));
	}

	public static void GoToHomeScreen(Activity activity) {

		Intent i = new Intent();
		i.setAction(DetectorInspector_HomeActivity.ACTION);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(i);

	}

	public static void GoToHomeScreenBackGround(Activity activity) {

		Intent i = new Intent();
		i.setAction(DetectorInspector_HomeActivity.ACTION);
		i.setFlags(Intent.FLAG_FROM_BACKGROUND);
		activity.startActivity(i);

	}

	public static void GoToLoginScreen(Activity activity) 
	{

		Intent i = new Intent();
		i.setAction(DetectorInspector_LoginActivity.ACTION);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(i);
	}

	/*public static void GoToStartInspectionScreen(Activity activity) {

		Intent i = new Intent();
		i.setAction(Detector_Inspector_StartInspectionActivity.ACTION);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivityForResult(i,
				Const.ACTIVITY_CODES.START_CAPTURE_IMAGE);

	}
*/
	/*public static void GoToStartNewInspectionScreen(Activity activity) {

		Intent i = new Intent();
		i.setAction(Detector_Inspector_StartNewInspectionActivity.ACTION);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivityForResult(i,
				Const.ACTIVITY_CODES.START_CAPTURE_IMAGE);

	}*/

	
	/*Only this method is used in the booking class*/
	public static void GoToStartNewInspectionHistoryScreen(Activity activity) {

		Intent i = new Intent();
		i.setAction(Detector_Inspector_StartNewInspectionWithHistoryActivity.ACTION);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivityForResult(i,	Const.ACTIVITY_CODES.START_CAPTURE_IMAGE);

	}

	public static void GoToEditAreaScreen(Activity activity) {

		Intent i = new Intent();
		i.setAction(DetectorInspector_EditActivity.ACTION);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivityForResult(i, Const.ACTIVITY_CODES.EDIT_ACTIVITY);

	}

	public static void GoToReOrderScreen(Activity activity) 
	{
		Intent i = new Intent();
		i.setAction(DetectorInspector_ReorderActivity.ACTION);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivityForResult(i, Const.ACTIVITY_CODES.EDIT_ACTIVITY);
	}

	public static String getConvertedDateinfullFormat(String mDate) 
	{
		SimpleDateFormat mOriginalDateSource = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat mChangedDateSource = new SimpleDateFormat(	"dd MMM, yyyy");

		Date mTimeIs;
		String mChangedTime = "dd MMM";

		try {
			mTimeIs = mOriginalDateSource.parse(mDate);
			mChangedTime = mChangedDateSource.format(mTimeIs);
			// Log.e("changed date is", "" + mChangedTime);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mChangedTime;
	}

	public static String getDatefullFormat(String mDate) {

		SimpleDateFormat mOriginalDateSource = new SimpleDateFormat(
				"dd MMM, yyyy");
		SimpleDateFormat mChangedDateSource = new SimpleDateFormat("yyyyMMdd");

		Date mTimeIs;
		String mChangedTime = "dd MMM";

		try {
			mTimeIs = mOriginalDateSource.parse(mDate);
			mChangedTime = mChangedDateSource.format(mTimeIs);
			// Log.e("changed date is", "" + mChangedTime);

		} catch (Exception e) {

			e.printStackTrace();

		}

		return mChangedTime;
	}

	public static void cacelTask(ISyncEngine mSyncEngine, String taskid) {
		if (taskid != null) {
			try {
				mSyncEngine.cancelSync(taskid);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static String readXMLinString(String fileName) {
		try {
			InputStream is = DetectorInspectorApplication.getInstance()
					.getContext().getAssets().open(fileName);
			// InputStream is =
			// UserProfile.getInstance().getContext().getAssets()
			// .open(fileName);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String text = new String(buffer);
			return text;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static String twoDigitNo(int value) {
		String n;
		if (value < 10) {
			n = "0" + value;
		} else {
			n = value + "";
		}
		return n;

	}

	public static String getSectioName(String text, Report mReport) {
		String newText = null;
		int i = 0;
		boolean flag = false;

		int t = Utils.getNumberFromSectionName(text);
		text = Utils.getTypeFromSectionName(text);
		for (ReportSection mReportSection : mReport.getReportSections()) {
			String sectionName = mReportSection.getName();
			if (text.equals(Utils.getTypeFromSectionName(sectionName))) {

				flag = true;
			}
		}
		if (flag) {
			i = i - t;
			newText = text + i;

		} else {
			newText = text;

		}

		return newText;
	}

	public static void updateReportId(String report_id, String report_uuid,
			Context context) {
		UserProfile profile = DetectorInspectorApplication.getInstance();
		// UserProfile profile = UserProfile.getInstance();
		Report report = null;

		// update report map in user profile and update saved json file
		if (profile.getReportMap() == null
				|| profile.getReportMap().get(report_uuid) == null) {

			Gson gson = new Gson();
			report = gson.fromJson(Util.readJSONFile(context, report_uuid),Report.class);

		} else {
			report = profile.getReportMap().get(report_uuid);
		}
		report.setReportId(report_id);
		profile.getReportMap().put(report.getReportUUID(), report);
		JsonUtil.writeJSONFile(context, report_uuid + ".json", report);

		// update property list in user profile
		profile.updatePropertyReportId(report_uuid, report_id);
		// update property table
		DatabaseUtil.updatePropertyReportId(context, report_uuid, report_id);

	}

	public static void GoToPhotoViewActivity(Activity activity) {
		Intent i = new Intent();
		i.setAction(DetectorInspector_PhotoViewActivity.ACTION);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivityForResult(i, Const.WS_REQUEST_TYPES.ADD_REPORT);
	}

	public static void doCrop(String filename, final Activity activity) {

		String path = Const.ENV_SETTINGS.DETECTOR_PHOTO_DIR + filename + ".jpg";

		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = activity.getPackageManager()
				.queryIntentActivities(intent, 0);

		int size = list.size();

		if (size == 0) {
			Toast.makeText(activity, "Can not find image crop app",
					Toast.LENGTH_SHORT).show();

			return;
		} else {
			mImageCaptureUri = Uri.fromFile(new File(path));
			intent.setData(mImageCaptureUri);

			intent.putExtra("outputX", 400);
			intent.putExtra("outputY", 400);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);

			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName,
						res.activityInfo.name));

				activity.startActivityForResult(i,
						Const.ACTIVITY_CODES.CROP_FROM_CAMERA);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = activity.getPackageManager()
							.getApplicationLabel(
									res.activityInfo.applicationInfo);
					co.icon = activity.getPackageManager().getApplicationIcon(
							res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);

					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));
					co.appIntent.putExtra("path", mImageCaptureUri.getPath());

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(
						activity.getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("Crop Image");
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								//

								//
								// Bundle data = new Bundle();
								// data.put
								//

								Intent intent = new Intent();
								intent.setAction(Detector_Inspector_StartInspectionActivity.ACTION);
								activity.setResult(Activity.RESULT_OK);
								activity.startActivityForResult(intent,
										Const.ACTIVITY_CODES.CROP_FROM_CAMERA);

								// activity.startActivityForResult(
								// cropOptions.get(item).appIntent,
								// Const.ACTIVITY_CODES.CROP_FROM_CAMERA);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

						if (mImageCaptureUri != null) {
							activity.getContentResolver().delete(
									mImageCaptureUri, null, null);
							mImageCaptureUri = null;
						}
					}
				});

				AlertDialog alert = builder.create();

				alert.show();
			}
		}
	}

	public static String getContactNumber(List<Contact> list) {
		String number = "";
		int i = 0;
		for (Contact contact : list) {
			if (i == 0) {
				number = contact.getContactNumber();
			} else {
				number = number + "/" + contact.getContactNumber();
			}
			i++;
		}

		return number;
	}

	public static List<Contact> getContactDetail(String number) {
		String[] nur = number.split("/");
		List<Contact> coList = new ArrayList<Contact>();

		for (String n : nur) {
			Contact nCon = new Contact(n, "");
			coList.add(nCon);
		}

		return coList;
	}

	public static List<ReportItem> getPreviousHistory(
			String previousExpiryYear, String previousDetectorType,
			String previousNewExpiryYear) {
		List<ReportItem> reportItems = new ArrayList<ReportItem>();
		String[] expiryYear = previousExpiryYear.split(",");
		String[] detectorType = previousDetectorType.split(",");
		String[] newExpiryYear = previousNewExpiryYear.split(",");
		int i = 0;
		for (String string : expiryYear) {

			ReportItem mreRepor = new ReportItem();
			mreRepor.setDetectorType(Integer.parseInt(detectorType[i].trim()));
			mreRepor.setExpiryYear(expiryYear[i].trim());
			mreRepor.setNewExpiryYear(newExpiryYear[i].trim());
			reportItems.add(mreRepor);
			i++;
		}
		return reportItems;
	}

	public static void GoToBookingActivity(Activity activity) {

		Intent calendar_intent = new Intent();
		calendar_intent.setAction(DetectorInspector_BookingActivity.ACTION);
		calendar_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(calendar_intent);
	}

	public static void GoToNotificationActivity(Context context, String message) {

		Intent calendar_intent = new Intent();
		calendar_intent.setAction(SendNotificationActivity.ACTION);
		calendar_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		calendar_intent.putExtra("message", message);
		context.startActivity(calendar_intent);
	}

	public static void setAlaram(Activity activity, String keyTime,
			String date, String proId, String uuid, int count, boolean b) {
		alarm = new AlarmManagerBroadcastReceiver();
		int propertyId = Integer.parseInt(proId);
		Context context = activity;

		Log.e("Multiple alaram Test", keyTime + "===" + date + "=="
				+ propertyId);

		if (keyTime.trim().equalsIgnoreCase("00:00 AM")) {
		} else {
			Log.e("Multiple alaram Test", keyTime + "===" + date + "=="
					+ propertyId);
			if (alarm != null) {
				// String time[] = keyTime.split("-");
				// SimpleDateFormat outputFormat = new SimpleDateFormat(
				// "yyyy-MM-dd HH:mm:ss");

				// String date = "2013-10-22";
				int da = Integer.parseInt(date.substring(8, 10));
				int mo = Integer.parseInt(date.substring(5, 7));
				int ye = Integer.parseInt(date.substring(0, 4));

				Log.e("Test", da + "= date & month =" + mo + " & year + " + ye);

				int hr = Integer.parseInt(keyTime.substring(9, 11));
				int mins = Integer.parseInt(keyTime.substring(12, 14));

				String val = keyTime.substring(15, 17);

				if (val.equalsIgnoreCase("PM")) {
					if (hr < 12) {
						hr = hr + 12;
					} else if (hr == 12) {
						//hr = 00;
					}
				}

				Log.e("Test", "time = " + hr + " mins: " + mins);

				long milsec = Utils.Date_to_MilliSeconds(da, mo, ye, hr, mins);
				Log.e("Test", " = millisec = " + milsec);
				long difference = milsec - System.currentTimeMillis();
				Log.e("difference", difference + "+++++++++++++");
				if (difference > 0) {
					long hrs = TimeUnit.MILLISECONDS.toHours(difference);
					long minute = TimeUnit.MILLISECONDS.toMinutes(difference);
					long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);

					long diffrencehrs = hrs - hr;

					// setOnetimeTimer(Context context, int seconds, int hours,
					// int minutes, int uniqueCode, String uuid)

					Log.e("time", hrs + "=hrs && minute=" + minute + "="
							+ seconds + "=");
					alarm.CancelAlarm(activity.getApplicationContext(),
							propertyId + "");
					alarm.setOnetimeTimerMillis(context, (int) 0, (int) hrs,
							(int) mins, propertyId, uuid, milsec);// 12sec
				} else {
					// count = count + 5;
					// if (b) {
					// alarm.CancelAlarm(activity.getApplicationContext(),
					// propertyId + "");
					// alarm.setOnetimeTimer(context, count, propertyId,
					// uuid);// 12sec
					// } else {
					// //
					// // setOnetimeTimer(Context context, int seconds, int
					// // hours,
					// // int minutes, int uniqueCode, String uuid)
					// alarm.CancelAlarm(activity.getApplicationContext(),
					// propertyId + "");
					// alarm.setOnetimeTimer(context, (int) count, (int) 0,
					// (int) 0, propertyId, uuid);// 12sec
					// }
				}
			} else {
				Log.d("ALaram", "Alaram is null " + keyTime);
			}
		}
	}

	public static List<ReportItem> getReportDetail(String previousExpiryYear,
			String previousNewExpiryYear, String previousDetectorType,
			String previousServiceSheetId) {
		List<ReportItem> mReportItems = new ArrayList<ReportItem>();
		String[] pEY = previousExpiryYear.split(",");
		String[] pNEY = previousNewExpiryYear.split(",");
		String[] pDetType = previousDetectorType.split(",");
		int i = 0;
		for (String dt : pDetType) {
			ReportItem item = new ReportItem();
			dt = ((dt.equals("")) ? "0" : dt);
			int ty = Integer.parseInt(dt);
			item.setDetectorType(ty);
			item.setExpiryYear(pEY[i]);
			item.setNewExpiryYear(pNEY[i]);
			item.setServiceSheetId(previousServiceSheetId);
			mReportItems.add(item);

			i++;
		}
		return mReportItems;
	}
}
