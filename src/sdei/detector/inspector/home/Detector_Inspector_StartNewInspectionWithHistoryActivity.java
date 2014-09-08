package sdei.detector.inspector.home;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.R;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.sync.report.Report;
import sdei.detector.inspector.sync.report.ReportPhoto;
import sdei.detector.inspector.sync.report.ReportSection;
import sdei.detector.inspector.sync.service.SyncService;
import sdei.detector.inspector.sync.service.engine.ISyncEngine;
import sdei.detector.inspector.sync.service.ws.WSResetPassResult;
import sdei.detector.inspector.sync.service.ws.WSResponse;
import sdei.detector.inspector.util.DatabaseUtil;
import sdei.detector.inspector.util.Utils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.detector.inspector.common.view.DateInterface;
import com.detector.inspector.lib.json.JsonUtil;
import com.detector.inspector.lib.location.EnableGpsDialogFragment;
import com.detector.inspector.lib.location.LocationActivity;
import com.detector.inspector.lib.model.Inspection;
import com.detector.inspector.lib.photo.ImageUtil;
import com.detector.inspector.lib.util.Const;
import com.detector.inspector.lib.widget.DataParseResult;
import com.detector.inspector.lib.widget.YearDialogActivity;
import com.detector.inspector.synch.report.ReportItem;
import com.google.myjson.Gson;
import com.google.myjson.JsonParseException;

public class Detector_Inspector_StartNewInspectionWithHistoryActivity extends
		sdei.detector.inspector.BaseActivity implements
		PhotoAdapter.PhotoAdapterCallback {

	public static final String ACTION = "sdei.detector.inspector.home.Detector_Inspector_StartNewInspectionWithHistoryActivity";
    private boolean mBack = true;
    private UserProfile mProfile;
	private TextView mPropertyStreet;
	private TextView mPropertyPostcode;
	private TextView mPropertyProvince;
	private TextView mPropertyDate;
	private TextView mPropertyTime;

	private ReportPhoto  mReportPhoto;
	private LinearLayout mLeftCardLinear;
	private LinearLayout mSignatureLinear;
	private TextView     mLeftCardTextView;
	private TextView     mSignatureTextView;
	private TextView     mServiceNoteTextView;
	private TextView mElectricalNoteTextView;
	private TextView mProblemNoteTextView;
	private EditText mServiceNoteEditText;
	private EditText mElectricalNoteEditText;
	private EditText mProblemNoteEditText;
	private Report mReport;

	private Inspection mProperty;

	private TextView mPropertyAgencieName;

	private TextView mPropertyNote;

	private TextView mPropertyContactNo;

	private Dialog mLoginDlg;

	private ImageView notWork;

	private ImageView tenantNotTextView;

	private ImageView noKeyTextView;

	private ImageView outOfTimeTextView;

	private ImageView writeNoteTextView;

	protected int selectedValue;

	public int selectedReportSection = -1;

	private Calendar mCalendarNow;

	private int mYear;
	private int mMonth;
	private int mDay;

	private LocationActivity mLocationActivity;
	private LocationManager mLocationManager;
	private double[] latLng;

	private LinearLayout multipleViewAdd;

	private LayoutInflater inflater;

	private ISyncEngine mSyncEngine;
	private SyncReceiver mSyncReceiver = new SyncReceiver();
	
	private ServiceConnection mConnection = new ServiceConnection() {
		// Called when the connection with the service is established
		public void onServiceConnected(ComponentName className, IBinder service) {
			// Following the example above for an AIDL interface,
			// this gets an instance of the IRemoteInterface, which we can use
			// to call on the service
			Log.d("TEST", "Service has connected");
			mSyncEngine = ISyncEngine.Stub.asInterface(service);
		}

		// Called when the connection with the service disconnects unexpectedly
		public void onServiceDisconnected(ComponentName className) {
			Log.d("TEST", "Service has unexpectedly disconnected");
			mSyncEngine = null;
		}

	};

	private AlarmManagerBroadcastReceiver alarm;

	public boolean mHistory = false;
	public boolean mValidationOff = false;

	public boolean turnOffExpiryYear = false;
	public boolean turnOffAlarmType = false;
	public boolean turnOffNoOfAlarms = false;

	private List<ReportItem> mPreviousHistory = null;
	private Report tempReport;

	private Dialog aLoginDlg;

	private String locationName = "";

	class SyncReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			try {
				if (intent
						.getAction()
						.equals(Const.WS_RESPONSE_ACTIONS.WS_SEND_TIME_DIFFERENCE_SUCESS)) {
					Log.d(getLocalClassName(), "BroadCast Recieve");
					if (alarm != null) {
						// Commented by Akhil Malik
						// alarm.CancelAlarm(Detector_Inspector_StartNewInspectionWithHistoryActivity.this);

					}

					String report_Name = mReport.getReportName();

					// Appointment time � Address � Appointment not completed.
					// Please complete as soon as possible as it has passed the
					// inspection time.

					Utils.showAlertWithMail(
							Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
							"Warning ",	report_Name	+ " Appointment not completed. Please complete as soon as possible as it has passed the inspection time.",
							mSyncEngine, mReport.getReportUUID());

				} else if (intent.getAction().equals(
						Const.WS_RESPONSE_ACTIONS.WS_SEND_NOTIFICATION_SUCESS)) {
					WSResponse response = intent.getParcelableExtra("response");
					DataParseTask data_task = new DataParseTask(
							Const.WS_REQUEST_TYPES.sendmailNotification);
					data_task.execute(response);
				} else if (intent.getAction().equals(Const.WS_RESPONSE_ACTIONS.WS_NETWORK_START_INSPECTION_ERROR)) {
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class DataParseTask extends AsyncTask<WSResponse, Void, DataParseResult> {

		private int mType;
		int status;

		public DataParseTask(int type) {
			mType = type;
		}

		@Override
		protected DataParseResult doInBackground(WSResponse... response) {
			// TODO Auto-generated method stub
			// JsonUtil.parseJsonText(response[0].getHttpResult());
			Gson gson = new Gson();
			DataParseResult result = new DataParseResult();
			try {
				if (mType == Const.WS_REQUEST_TYPES.sendmailNotification) {
					WSResetPassResult ws_reset_pass_result = gson.fromJson(
							mProfile.getResponse(), WSResetPassResult.class);
					if (ws_reset_pass_result == null) {
						result.isSuccess = false;
					} else {
						status = ws_reset_pass_result.getStatus();
					}
					mProfile.setResponseMessage(ws_reset_pass_result
							.getMessage());
				}
			} catch (JsonParseException e) {
				result.isSuccess = false;
			}
			return result;
		}

		@Override
		protected void onPostExecute(DataParseResult result) {
			try {
				if (mType == Const.WS_REQUEST_TYPES.sendmailNotification) {
					Utils.showToastAlert(
							Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
							mProfile.getMessage());
					if (result.isSuccess) {

						if (status == 0) {

						}
					} else {

					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		doUnbindService();
		unregisterReceiver(mSyncReceiver);
	}

	private void doUnbindService() {
		if (mSyncEngine != null) {
			unbindService(mConnection);
		}
	}

	@Override
	public void onBeforeCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onBeforeCreate(savedInstanceState);

		mProfile = DetectorInspectorApplication.getInstance();
		// mProfile = UserProfile.getInstance();
		mReport = mProfile.getReport();

		alarm = new AlarmManagerBroadcastReceiver();
		IntentFilter filter = new IntentFilter(	Const.WS_RESPONSE_ACTIONS.WS_SEND_TIME_DIFFERENCE_SUCESS);
		filter.addAction(Const.WS_RESPONSE_ACTIONS.WS_SEND_NOTIFICATION_SUCESS);
		filter.addAction(Const.WS_RESPONSE_ACTIONS.WS_NETWORK_START_INSPECTION_ERROR);
		registerReceiver(mSyncReceiver, filter);

		// bind service
		bindService(new Intent(Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
				SyncService.class), mConnection, Context.BIND_AUTO_CREATE);

		mProperty = mProfile.getInspectionForStartInspect();
		mCalendarNow = Calendar.getInstance();
		mYear = mCalendarNow.get(Calendar.YEAR);
		mMonth = mCalendarNow.get(Calendar.MONTH);
		mDay = mCalendarNow.get(Calendar.DAY_OF_MONTH);
		setContentView(R.layout.activity_start_inspection_new);
	}

	public void inspectionButton(View v)  {
		switch (v.getId()) {
		
		case R.id.cancel_inspection_btn:
			finish();
			break;

		case R.id.submit_inspection_btn:
			mBack = true;
			int noOfLocation = mReport.getReportSections().size();
			boolean leftCardFlag = mReport.isLeftCard();
			String leftCardStatus = (String) mLeftCardTextView.getTag();
			Log.i("left card status",leftCardStatus);
			// Gson gson = new Gson();
			// String json = gson.toJson(mProfile);
			// System.out.println(json);
			if (noOfLocation > 0) {
				// json = gson.toJson(mProfile);
				int noOfPhoto = noOfLocation * 2;
				int totalImage = 0;
				
				
				for (ReportSection mSection : mReport.getReportSections()) {
					totalImage = totalImage + mSection.getReportPhotos().size();
				}

				Log.d("Report Section", totalImage + "==" + noOfPhoto);
				if (noOfPhoto == totalImage	&& leftCardStatus.equalsIgnoreCase("on")) {
					mReport.setLeftCard(true);
					boolean voltBoolean = getVoltStatus(mReport);
					
					
					if (voltBoolean) {
						try {
	latLng = mLocationActivity.setup(mLocationManager,	Detector_Inspector_StartNewInspectionWithHistoryActivity.this);
	Utils.showToastAlert(Detector_Inspector_StartNewInspectionWithHistoryActivity.this,latLng[0] + "lat and Long" + latLng[1]);
						} catch (Exception e) {
							latLng = mLocationActivity.setup(mLocationManager,Detector_Inspector_StartNewInspectionWithHistoryActivity.this);
						}

						
						
						SaveReportTask task = new SaveReportTask();
						task.execute(mReport);

					} else 
					{
						Utils.showAlert(Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
								"Message",
								"Please enter the electrical notes on location " + locationName);
					}
				} else if (noOfPhoto != totalImage) {// totalImage = 0 // static value
					Utils.showAlert(					
							Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
							"Message", "Please take two pic of each location");
				} else 
				{
					Utils.showAlert(
							 Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
							"Message", "Please select the left card option");
				}

			} else {
				Utils.showAlert(
						Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
						"Message", "Please inspect the property first.");
			}

			break;

		case R.id.edit_inspection_btn:
			mProfile.setReport(mReport);
			Utils.GoToEditAreaScreen(Detector_Inspector_StartNewInspectionWithHistoryActivity.this);
			break;

		default:
			break;
		}
	}

	private boolean getVoltStatus(Report mReport) {
		// location Name
		int count = 0;

		for (ReportSection mReportSection : mReport.getReportSections()) {
			boolean voltBoolean = mReportSection.getItems().isVoltProblem();
			int length = mReportSection.getItems().getElectricalNote().length();
			if (voltBoolean) {
				if (length > 0) {

				} else {
					if (count == 0) {
						locationName = mReportSection.getName();
					} else {
						locationName = locationName + mReportSection.getName();
					}
					count++;
					break;
				}
			}
		}

		if (count == 0) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		mBack = false;
		SaveReportTask task = new SaveReportTask();
		task.execute(mReport);
	}

	
	@Override
	public void getView() {
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final boolean gpsEnabled = mLocationManager	.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!gpsEnabled) {
			new EnableGpsDialogFragment().show(getSupportFragmentManager(),	"enableGpsDialog");
		}

		mLocationActivity = new LocationActivity(
				Detector_Inspector_StartNewInspectionWithHistoryActivity.this);

		TextView mTiTextView = (TextView) findViewById(R.id.title_txt);
		mTiTextView.setText(getResources().getString(R.string.app_name));
		findViewById(R.id.home_btn).setVisibility(View.GONE);

		inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mPropertyStreet = (TextView) findViewById(R.id.start_inspection_street);

		if (mProperty != null) {
			String mStreetnumber = "";
			if (mProperty.getStreetNumber() != null) {
				mStreetnumber = (mProperty.getStreetNumber()).toString().trim();
			}
			if (mProperty.getStreetName() != null) {
				mStreetnumber = (mStreetnumber + "," + mProperty
						.getStreetName()).toString().trim();
			}
			if (mStreetnumber.toString().equalsIgnoreCase("")) {
				mPropertyStreet.setText("N/A");
			} else {
				mPropertyStreet.setText(mStreetnumber);
			}
			// mPropertyStreet.setText(mProperty.getStreetNumber() + ", "
			// + mProperty.getStreetName());

			mPropertyPostcode = (TextView) findViewById(R.id.start_inspection_postcode);

			String mAddress = "";
			if (mProperty.getSuburb() != null) {
				mAddress = (mProperty.getSuburb()).toString().trim();
			}
			if (mProperty.getState() != null) {
				mAddress = (mAddress + "," + mProperty.getState()).toString()
						.trim();
			}
			if (mProperty.getPostCode() != null) {
				mAddress = (mAddress + "," + mProperty.getPostCode())
						.toString().trim();
			}
			if (mAddress.toString().equalsIgnoreCase("")) {
				mPropertyPostcode.setText("N/A");
			} else {
				mPropertyPostcode.setText(mAddress);
			}

			// mPropertyPostcode.setText(mProperty.getSuburb() + ", "
			// + mProperty.getState() + ", " + mProperty.getPostCode());

			mPropertyProvince = (TextView) findViewById(R.id.start_inspection_province);

			if (mProperty.getOccupantName() != null)
				mPropertyProvince.setText(mProperty.getOccupantName());

			mPropertyDate = (TextView) findViewById(R.id.start_inspection_date);
			if (mProperty.getDate() != null)
				mPropertyDate.setText(Utils
						.getConvertedDateinfullFormat(mProperty.getDate()));

			mPropertyTime = (TextView) findViewById(R.id.start_inspection_time);
			String keyTime = "";
			if (mProperty.getKeytime() != null)
				keyTime = mProperty.getKeytime();// "02:00 PM-04:10 PM";

			if (keyTime.trim().equalsIgnoreCase("00:00 AM")) {
				keyTime = mProperty.getKeyNumber();
				if (keyTime != null && keyTime.length() > 1) {

				} else {
					keyTime = "";
				}
			} else {
				Utils.setAlaram(Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
						keyTime, mProperty.getInspectionDate(),
						mProperty.getPropertyId(), mProperty.getReport_uuid(),
						1, false);
			}

			mPropertyTime.setText("Key/Time - " + keyTime);

			mPropertyContactNo = (TextView) findViewById(R.id.start_inspection_contact_info);
			int Size = 0;
			if (mProperty.getContact() != null) {
				Size = mProperty.getContact().size();
			}

			String number = null;
			if (Size > 0) {
				number = getContactNumber(Size);
				if (number != null)
					mPropertyContactNo.setText(" : " + number);
				else
					mPropertyContactNo.setText(" : ");
			} else {
				mPropertyContactNo.setText(" : N/A");
			}

			mPropertyAgencieName = (TextView) findViewById(R.id.start_inspection_agencie_name);
			if (mProperty.getAgencyName() != null)
				mPropertyAgencieName.setText("Agency Name : "
						+ mProperty.getAgencyName());

			mPropertyNote = (TextView) findViewById(R.id.start_inspection_note);
			if (mProperty.getNotes() != null)
				mPropertyNote.setText("Note : " + mProperty.getNotes());

		}
		// mPropertyStreet.setText(mProperty.getStreetNumber() + ", "
		// + mProperty.getStreetName());

		// mPropertyPostcode = (TextView)
		// findViewById(R.id.start_inspection_postcode);
		// mPropertyPostcode.setText(mProperty.getSuburb() + ", "
		// + mProperty.getState() + ", " + mProperty.getPostCode());

		// mPropertyProvince = (TextView)
		// findViewById(R.id.start_inspection_province);
		// mPropertyProvince.setText(mProperty.getOccupantName());

		// mPropertyDate = (TextView) findViewById(R.id.start_inspection_date);
		// mPropertyDate.setText(Utils.getConvertedDateinfullFormat(mProperty
		// .getDate()));

		// mPropertyTime = (TextView) findViewById(R.id.start_inspection_time);
		// String keyTime = mProperty.getKeytime();// "02:00 PM-04:10 PM";

		// if (keyTime.trim().equalsIgnoreCase("00:00 AM")) {
		// keyTime = mProperty.getKeyNumber();
		// if (keyTime != null && keyTime.length() > 1) {

		// } else {
		// keyTime = "";
		// }
		// } else {
		// Utils.setAlaram(
		// Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
		// keyTime, mProperty.getInspectionDate(),
		// mProperty.getPropertyId(), mProperty.getReport_uuid(), 1,
		// false);
		// }

		// mPropertyTime.setText("Key/Time - " + keyTime);

		// mPropertyContactNo = (TextView)
		// findViewById(R.id.start_inspection_contact_info);

		// int Size = mProperty.getContact().size();
		// String number = null;
		// if (Size > 0) {
		// number = getContactNumber(Size);
		// if (number != null)
		// mPropertyContactNo.setText(" : " + number);
		// else
		// mPropertyContactNo.setText(" : ");
		// } else {
		// mPropertyContactNo.setText(" : N/A");
		// }

		// mPropertyAgencieName = (TextView)
		// findViewById(R.id.start_inspection_agencie_name);
		// mPropertyAgencieName.setText("Agency Name : "
		// + mProperty.getAgencyName());

		// mPropertyNote = (TextView) findViewById(R.id.start_inspection_note);
		// mPropertyNote.setText("Note : " + mProperty.getNotes());

		/* Footer Values */
		mLeftCardLinear = (LinearLayout) findViewById(R.id.left_card_linear);
		mSignatureLinear = (LinearLayout) findViewById(R.id.signature_linear);

		mLeftCardTextView = (TextView) findViewById(R.id.left_card);
		mLeftCardTextView.setTag("no");
		// Added by Akhil Malik
		// mLeftCardTextView.setClickable(false);
		// mLeftCardTextView.setFocusable(false);
		// mLeftCardTextView.setFocusableInTouchMode(false);
		// mReport.setLeftCard(true);
		// setBottomListView(R.id.left_card, mReport.isLeftCard());
		mLeftCardTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mReport.setLeftCard(!mReport.isLeftCard());
				setBottomListView(R.id.left_card, mReport.isLeftCard());
			}

		});
		mSignatureTextView = (TextView) findViewById(R.id.signature);
		setBottomListView(R.id.signature, mReport.isSignature());
		mSignatureTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mReport.setSignature(!mReport.isSignature());
				setBottomListView(R.id.signature, mReport.isSignature());
			}
		});

		mServiceNoteTextView = (TextView) findViewById(R.id.service_note_txt);
		mProblemNoteTextView = (TextView) findViewById(R.id.problem_note_txt);

		mServiceNoteEditText = (EditText) findViewById(R.id.services_note_edit_txt);
		mServiceNoteEditText.setText(mReport.getServiceNote());
		mServiceNoteEditText
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH
								|| actionId == EditorInfo.IME_ACTION_DONE
								|| event.getAction() == KeyEvent.ACTION_DOWN
								&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
							Utils.hideKeyboard(
									Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
									mServiceNoteEditText);
							String text = mServiceNoteEditText.getText()
									.toString().trim();

							mReport.setServiceNote(text);

							return true;
						}
						return false;
					}
				});

		mServiceNoteEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				mReport.setServiceNote(s.toString());
			}

		});

		mProblemNoteEditText = (EditText) findViewById(R.id.problem_notes_edit_txt);
		mProblemNoteEditText.setText(mReport.getProblemNote());
		mProblemNoteEditText
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH
								|| actionId == EditorInfo.IME_ACTION_DONE
								|| event.getAction() == KeyEvent.ACTION_DOWN
								&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
							Utils.hideKeyboard(
									Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
									mProblemNoteEditText);
							String text = mProblemNoteEditText.getText()
									.toString().trim();
							mReport.setProblemNote(text);

							return true;
						}
						return false;
					}
				});

		mProblemNoteEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				mReport.setProblemNote(s.toString());
			}

		});

		multipleViewAdd = (LinearLayout) findViewById(R.id.add_multiple_view_inside_scroll);

		mHistory = DatabaseUtil.getPreviousHistory(	Detector_Inspector_StartNewInspectionWithHistoryActivity.this,	mProperty.getReport_uuid());

		mValidationOff = DatabaseUtil.getValidation(Detector_Inspector_StartNewInspectionWithHistoryActivity.this,	mProperty.getReport_uuid());

		turnOffExpiryYear = mProfile.isTurnOffExpiryYear();
		turnOffAlarmType = mProfile.isTurnOffAlarmType();
		turnOffNoOfAlarms = mProfile.isTurnOffNoOfAlarms();

		Utils.showToastAlert(
				Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
				mHistory + "===" + turnOffExpiryYear + "===" + turnOffAlarmType
						+ "===" + turnOffNoOfAlarms);

		if (mHistory) {
			mPreviousHistory = mProfile.getHistoryData();
			for (ReportItem mItem : mPreviousHistory) {
				Log.d("Test",
						mItem.getDetectorType() + "===="
								+ mItem.getExpiryYear());
			}
		}

		// mPreviousHistory = previousHistory(mProperty);
		// if (mPreviousHistory.size() > 0) {
		// // mPreviousHistory = previousHistory(mProperty);
		// mHistory = true;
		// } else {
		// mHistory = false;
		// }
		if (mReport.getReportSections() != null
				&& mReport.getReportSections().size() > 0) {
			visibleValues();

		} else {
			inVisibleValues();

		}

		addMulipleView(mReport, selectedReportSection);

	}

	private List<ReportItem> previousHistory(Inspection data) {

		List<ReportItem> pList = null;
		for (Inspection mNewInspection : mProfile.getInspectionListSynch()) {
			if (data.getPropertyId().trim()
					.equals(mNewInspection.getPropertyId())) {
				pList = mNewInspection.getPreviousHistory();
				mProfile.setHistoryData(pList);
				break;

			}
		}
		return pList;

	}

	private void addMulipleView(final Report mReport, int selectedReport) {
		multipleViewAdd.removeAllViews();
		for (int i = 0; i < mReport.getReportSections().size(); i++) {

			View mView = inflater.inflate(
					R.layout.start_inspection_child_item_new, null);

			final ReportItem mReportItem = mReport.getReportSections().get(i)
					.getItems();

			final ReportSection item = mReport.getReportSections().get(i);

			TextView mSection = (TextView) mView
					.findViewById(R.id.section_name);
			mSection.setText(mReport.getReportSections().get(i).getName());

			final RelativeLayout sectionRelative = (RelativeLayout) mView
					.findViewById(R.id.section_name_relative);

			final TextView arrTextView = (TextView) mView
					.findViewById(R.id.arrow_mark);
			final LinearLayout toBeHiddenLayout = (LinearLayout) mView
					.findViewById(R.id.to_be_hidden_layout);

			if (selectedReport == i) {
				toBeHiddenLayout.setVisibility(View.VISIBLE);
				arrTextView
						.setBackgroundResource(R.drawable.im_inspectioncollasped);

			} else {
				toBeHiddenLayout.setVisibility(View.GONE);
				arrTextView
						.setBackgroundResource(R.drawable.im_inspectioncollasped_allow);
			}

			final int j = i;

			sectionRelative.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					if (selectedReportSection == j
							&& toBeHiddenLayout.getVisibility() == View.VISIBLE) {
						arrTextView
								.setBackgroundResource(R.drawable.im_inspectioncollasped_allow);
						toBeHiddenLayout.setVisibility(View.GONE);
					} else {

						selectedReportSection = j;

						addMulipleView(mReport, selectedReportSection);

					}
				}
			});

			arrTextView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (selectedReportSection == j
							&& toBeHiddenLayout.getVisibility() == View.VISIBLE) {
						arrTextView
								.setBackgroundResource(R.drawable.im_inspectioncollasped_allow);
						toBeHiddenLayout.setVisibility(View.GONE);
					} else {
						selectedReportSection = j;
						addMulipleView(mReport, selectedReportSection);
					}
				}
			});

			TextView detectorType = (TextView) mView
					.findViewById(R.id.detector_type_spinner);
			String value = getDetectorType(mReport.getReportSections().get(i)
					.getItems().getDetectorType());
			detectorType.setText(value);
			detectorType.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					chooseDetectorType(selectedReportSection,
							mReportItem.getDetectorType());
				}
			});

			final TextView batteryReplace = (TextView) mView
					.findViewById(R.id.battery_replaced);

			RadioButton(mReportItem.isBatteryReplaced(), batteryReplace,
					mReportItem);

			batteryReplace.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setRadioButton(batteryReplace, mReportItem,
							mReportItem.isBatteryReplaced());
				}
			});

			final TextView cleanedTxtView = (TextView) mView
					.findViewById(R.id.cleaned_check_box);

			RadioButton(mReportItem.isCleaned(), cleanedTxtView, mReportItem);

			cleanedTxtView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setRadioButton(cleanedTxtView, mReportItem,
							mReportItem.isCleaned());
				}
			});

			final TextView strickedApplied = (TextView) mView
					.findViewById(R.id.stricked_applied);

			RadioButton(mReportItem.isStickedApplied(), strickedApplied,
					mReportItem);

			strickedApplied.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setRadioButton(strickedApplied, mReportItem,
							mReportItem.isStickedApplied());
				}
			});

			final TextView descibelTest = (TextView) mView
					.findViewById(R.id.decibel_test);

			RadioButton(mReportItem.isDecibelTest(), descibelTest, mReportItem);

			descibelTest.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setRadioButton(descibelTest, mReportItem,
							mReportItem.isDecibelTest());
				}
			});

			final TextView notRequired = (TextView) mView
					.findViewById(R.id.not_required);

			RadioButton(mReportItem.isNotRequired(), notRequired, mReportItem);

			notRequired.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setRadioButton(notRequired, mReportItem,
							mReportItem.isNotRequired());
				}
			});

			final TextView voltProblem = (TextView) mView
					.findViewById(R.id.volt_problem);

			RadioButton(mReportItem.isVoltProblem(), voltProblem, mReportItem);

			voltProblem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					setRadioButton(voltProblem, mReportItem,
							mReportItem.isVoltProblem());

				}
			});

			final TextView expiryCancelTextView = (TextView) mView
					.findViewById(R.id.expiry_cancel_text);

			final EditText expiryEditText = (EditText) mView
					.findViewById(R.id.expiry_edit_txt);

			expiryEditText.setText(mReportItem.getExpiryYear());
			expiryEditText.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					setDate(mReportItem, selectedReportSection,
							R.id.expiry_edit_txt, expiryCancelTextView);

				}
			});

			final TextView newExpiryCancelTextView = (TextView) mView
					.findViewById(R.id.new_expiry_cancel_text);

			final EditText newExpiryEditText = (EditText) mView
					.findViewById(R.id.new_expiry_year_edit_txt);

			newExpiryEditText.setText(mReportItem.getNewExpiryYear());
			newExpiryEditText.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					setDate(mReportItem, selectedReportSection,
							R.id.new_expiry_year_edit_txt,
							newExpiryCancelTextView);
				}
			});

			setCancelVisibility(expiryCancelTextView,
					mReportItem.getExpiryYear());

			expiryCancelTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mReportItem.setExpiryYear("");
					expiryCancelTextView.setVisibility(View.GONE);

					addMulipleView(mReport, selectedReportSection);
				}
			});

			setCancelVisibility(newExpiryCancelTextView,
					mReportItem.getNewExpiryYear());

			newExpiryCancelTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mReportItem.setNewExpiryYear("");
					newExpiryCancelTextView.setVisibility(View.GONE);

					addMulipleView(mReport, selectedReportSection);

				}
			});

			final EditText manifacture = (EditText) mView
					.findViewById(R.id.manufacture_edit_txt);
			manifacture.setText(mReportItem.getManufacturer());
			manifacture.setInputType(InputType.TYPE_CLASS_TEXT);
			manifacture.setImeOptions(EditorInfo.IME_ACTION_DONE);
			final ReportItem input_field1 = mReportItem;
			manifacture
					.setOnEditorActionListener(new TextView.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if (actionId == EditorInfo.IME_ACTION_DONE) {
								Utils.hideKeyboard(
										Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
										manifacture);
								input_field1.setManufacturer(v.getText()
										.toString());

							}
							return false;
						}
					});

			manifacture.addTextChangedListener(new TextWatcher() {

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					input_field1.setManufacturer(s.toString());
				}

			});

			final TextView electricalTextView = (TextView) mView
					.findViewById(R.id.electrical_note_txt);
			final EditText electricalNoteEditText = (EditText) mView
					.findViewById(R.id.electrical_notes_edit_txt);
			if (mReportItem.isVoltProblem()) {
				electricalNoteEditText.setVisibility(View.VISIBLE);
				electricalTextView.setVisibility(View.VISIBLE);
			} else {
				electricalNoteEditText.setVisibility(View.GONE);
				electricalTextView.setVisibility(View.GONE);
			}

			electricalNoteEditText.setText(mReportItem.getElectricalNote());
			electricalNoteEditText.setInputType(InputType.TYPE_CLASS_TEXT);
			electricalNoteEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
			final ReportItem input_field = mReportItem;
			electricalNoteEditText
					.setOnEditorActionListener(new TextView.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if (actionId == EditorInfo.IME_ACTION_DONE) {
								Utils.hideKeyboard(
										Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
										electricalNoteEditText);
								input_field.setElectricalNote(v.getText()
										.toString());

							}
							return false;
						}
					});

			electricalNoteEditText.addTextChangedListener(new TextWatcher() {

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					input_field.setElectricalNote(s.toString());
				}

			});

			final TextView photoNo = (TextView) mView
					.findViewById(R.id.report_item_photo_number);
			photoNo.setText(item.getReportPhotos().size() + "");

			Button mPhotoButton = (Button) mView
					.findViewById(R.id.report_item_item_camera_btn);
			mPhotoButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					capturePhoto(item);
				}
			});

			Gallery mGallery = (Gallery) mView
					.findViewById(R.id.report_item_item_detail_photo_gallery);

			final List<Map<String, ?>> data_list = new ArrayList<Map<String, ?>>();

			Log.d("TEST", "item photos: " + item.getReportPhotos().size());
			if (item.getReportPhotos().size() == 0) {
				mGallery.setVisibility(View.GONE);
			} else {
				Log.d("TEST", "create gallery");
				mGallery.setVisibility(View.VISIBLE);
				// add photos
				for (ReportPhoto photo : item.getReportPhotos()) {
					Map gallery_data_map = new HashMap();
					gallery_data_map.put("item_photo", photo);
					gallery_data_map.put("report_item_zoom_icon", photo);
					gallery_data_map.put("history_imagew_cancel_btn", photo);
					data_list.add(gallery_data_map);
				}

				if (data_list.size() > 0) {
					final String[] from = new String[] { "item_photo",
							"report_item_zoom_icon",
							"history_imagew_cancel_btn" };

					final int[] to = new int[] { R.id.report_item_photo,
							R.id.report_item_zoom_icon,
							R.id.history_imagew_cancel_btn };

					final PhotoAdapter adapter = new PhotoAdapter(
							Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
							data_list,
							R.layout.start_inspection_item_gallery_item_new,
							from, to);
					setPhotoAdapterCallback(adapter);
					adapter.setViewBinder(adapter);
					mGallery.setAdapter(adapter);
				}

			}

			multipleViewAdd.addView(mView);

		}
	}

	private void setCancelVisibility(View v, String string) {
		if (string.length() > 0) {
			v.setVisibility(View.VISIBLE);
		} else {
			v.setVisibility(View.GONE);
		}

	}

	private void RadioButton(final boolean b, View v,
			final ReportItem mReportItem) {
		if (b) {
			v.setBackgroundResource(R.drawable.im_select_check_active);
		} else {
			v.setBackgroundResource(R.drawable.im_select_check_inactive);
		}
	}

	private String getContactNumber(int size) {
		String num = null;
		switch (size) {
		case 4:
			String telephoneNo = mProperty.getContact().get(0)
					.getContactNumber();
			String mobileNo = mProperty.getContact().get(1).getContactNumber();
			String businessNo = mProperty.getContact().get(2)
					.getContactNumber();
			String homeNo = mProperty.getContact().get(3).getContactNumber();
			if (telephoneNo.length() > 0) {
				num = telephoneNo;
			}
			if (mobileNo.length() > 0) {
				if (num.length() > 0) {
					num = num + "/" + mobileNo;
				} else {
					num = mobileNo;
				}
			}
			if (businessNo.length() > 0) {

				if (num.length() > 0) {
					num = num + "/" + businessNo;
				} else {
					num = businessNo;
				}

			}
			if (homeNo.length() > 0) {
				if (num.length() > 0) {
					num = num + "/" + homeNo;
				} else {
					num = homeNo;
				}
			}
			break;

		default:
			break;
		}

		return num;
	}

	private void setBottomListView(int leftCard, boolean b) {
		switch (leftCard) {
		case R.id.left_card:
			if (mReport.isLeftCard()) {
				mLeftCardTextView
						.setBackgroundResource(R.drawable.im_select_check_active);
				mLeftCardTextView.setTag("on");
			} else {
				mLeftCardTextView
						.setBackgroundResource(R.drawable.im_select_check_inactive);
				mLeftCardTextView.setTag("off");
			}

			break;
		case R.id.signature:
			if (mReport.isSignature()) {
				mSignatureTextView
						.setBackgroundResource(R.drawable.im_select_check_active);
			} else {
				mSignatureTextView
						.setBackgroundResource(R.drawable.im_select_check_inactive);
			}

			break;
		default:
			break;
		}

	}

	private void visibleValues() {
		mLeftCardLinear.setVisibility(View.VISIBLE);
		mSignatureLinear.setVisibility(View.VISIBLE);
		mServiceNoteTextView.setVisibility(View.VISIBLE);
		// mElectricalNoteTextView.setVisibility(View.VISIBLE);
		mProblemNoteTextView.setVisibility(View.VISIBLE);

		mServiceNoteEditText.setVisibility(View.VISIBLE);
		// mElectricalNoteEditText.setVisibility(View.VISIBLE);
		mProblemNoteEditText.setVisibility(View.VISIBLE);

	}

	private void inVisibleValues() {
		mLeftCardLinear.setVisibility(View.GONE);
		mSignatureLinear.setVisibility(View.GONE);
		mServiceNoteTextView.setVisibility(View.GONE);
		// mElectricalNoteTextView.setVisibility(View.GONE);
		mProblemNoteTextView.setVisibility(View.GONE);

		mServiceNoteEditText.setVisibility(View.GONE);
		// mElectricalNoteEditText.setVisibility(View.GONE);
		mProblemNoteEditText.setVisibility(View.GONE);

	}

	private String getDetectorType(int detectorType) {
		String reason = null;
		switch (detectorType) {
		case Const.WHY_INSPECTION_NOT_COMPLETE_REASON.KEY_NOT_WORK:
			reason = getResources().getString(R.string.mains);

			break;
		case Const.WHY_INSPECTION_NOT_COMPLETE_REASON.NOT_HOME:
			reason = getResources().getString(R.string.mains_recharge);

			break;
		case Const.WHY_INSPECTION_NOT_COMPLETE_REASON.NO_KEY:
			reason = getResources().getString(R.string.detachable);

			break;
		case Const.WHY_INSPECTION_NOT_COMPLETE_REASON.NOTE:
			reason = getResources().getString(R.string.detechable_recharge);

			break;

		case Const.WHY_INSPECTION_NOT_COMPLETE_REASON.OUT_OF_TIME:
			reason = getResources().getString(R.string.security);
			break;

		default:
			break;
		}

		return reason;
	}

	public void getActivtyResult(int requestCode, int resultCode, Intent data) {
		// Log.e(getLocalClassName(), requestCode + "");
		switch (requestCode) {
		case Const.ACTIVITY_CODES.START_CAPTURE_IMAGE:
			if (resultCode == RESULT_OK) {
				try {
					int[] size = ImageUtil.decodePhotoSize(mReportPhoto
							.getPhotoId());
					mReportPhoto.setWidth(size[0]);
					mReportPhoto.setHeight(size[1]);
					mReportPhoto.setAngle(ImageUtil
							.getExifOrientation(mReportPhoto.getPhotoId()));
					mReport.addPhoto(mReportPhoto);
					mProfile.setReport(mReport);

					DatabaseUtil.addPhotoId(this, mReport.getReportId(), 0, 0,
							mReportPhoto.getPhotoId(),
							mReportPhoto.getQuality(), mReportPhoto.getDate(),
							mReport.getReportUUID());

					addMulipleView(mReport, selectedReportSection);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		case Const.ACTIVITY_CODES.CROP_FROM_CAMERA:
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				if (photo != null) {
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					byte[] byteArray = stream.toByteArray();
					String image_path = Const.ENV_SETTINGS.DETECTOR_PHOTO_DIR;
					String file_name = mReportPhoto.getPhotoId() + ".jpg";
					File pictureFile = new File(image_path, file_name);

					ImageUtil.storeImage(pictureFile, byteArray, 0, -1, true);

					int[] size = ImageUtil.decodePhotoSize(mReportPhoto
							.getPhotoId());
					mReportPhoto.setWidth(size[0]);
					mReportPhoto.setHeight(size[1]);
					mReportPhoto.setAngle(ImageUtil
							.getExifOrientation(mReportPhoto.getPhotoId()));
					mReport.addPhoto(mReportPhoto);
					mProfile.setReport(mReport);

					DatabaseUtil.addPhotoId(this, mReport.getReportId(), 0, 0,
							mReportPhoto.getPhotoId(),
							mReportPhoto.getQuality(), mReportPhoto.getDate(),
							mReport.getReportUUID());
					updateReport(mReport, selectedReportSection);
				} else {

				}

			}

			break;

		case Const.ACTIVITY_CODES.EDIT_ACTIVITY:
			if (resultCode == RESULT_OK) {
				if (mProfile.getReport() != null) {
					mReport = mProfile.getReport();
					if (mReport.getReportSections().size() > 0) {
						visibleValues();
					} else {
						inVisibleValues();

						mReport.setServiceNote("");
						mReport.setProblemNote("");

						mReport.setLeftCard(false);
						mReport.setSignature(false);

						mServiceNoteEditText.setText("");
						mProblemNoteEditText.setText("");

						setBottomListView(R.id.left_card, false);
						setBottomListView(R.id.signature, false);

					}
					addMulipleView(mReport, selectedReportSection);
					// updatePropertyLayout(mReport);
				} else {

				}

			}
			break;

		}

	}

	class SaveReportTask extends AsyncTask<Report, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Detector_Inspector_StartNewInspectionWithHistoryActivity.this.showProgress();
		}

		@Override
		protected Boolean doInBackground(Report... params) {
			try {
				Report report = params[0];

				// write report json file
				Log.d("Save Report", "Save Report UUID: " + report.getReportUUID());
				
				mProfile.setReport(mReport);
				JsonUtil.writeJSONFile(Detector_Inspector_StartNewInspectionWithHistoryActivity.this,	report.getReportUUID() + ".json", report);
				mProfile.getReportMap().put(report.getReportUUID(), report);

			} catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			Detector_Inspector_StartNewInspectionWithHistoryActivity.this
					.hideProgress();
			if (result) {

				if (mBack) {
					tempReport = new Report(mProfile.getReport());

					if (mHistory) {
						if (turnOffExpiryYear && turnOffAlarmType	&& turnOffNoOfAlarms) {

							checkhistoryData(mPreviousHistory, tempReport);

						} else if (turnOffExpiryYear && turnOffAlarmType) {
							checkhistoryData(mPreviousHistory, tempReport);
						} else if (turnOffExpiryYear && turnOffNoOfAlarms) {
							boolean sizeBoolean = noOfAlaram(mPreviousHistory,
									tempReport);
							if (sizeBoolean) {
								checkhistoryDataWithYear(mPreviousHistory,
										tempReport, false);
							} else {
								CheckSizeOfALaram(AlaramSize(mPreviousHistory,
										tempReport));
							}

						} else if (turnOffAlarmType && turnOffNoOfAlarms) {

							boolean sizeBoolean = noOfAlaram(mPreviousHistory,
									tempReport);
							if (sizeBoolean) {
								checkhistoryDataWithYear(mPreviousHistory,
										tempReport, true);
							} else {
								CheckSizeOfALaram(AlaramSize(mPreviousHistory,
										tempReport));
							}

						} else if (turnOffExpiryYear) {
							checkhistoryDataWithYear(mPreviousHistory,
									tempReport, false);
						} else if (turnOffAlarmType) {
							checkhistoryDataWithYear(mPreviousHistory,
									tempReport, true);
						} else if (turnOffNoOfAlarms) {
							boolean sizeBoolean = noOfAlaram(mPreviousHistory,
									tempReport);
							if (sizeBoolean) {
								MoveToSubmitClass(mBack);
							} else {
								CheckSizeOfALaram(AlaramSize(mPreviousHistory,
										tempReport));
							}
						} else {
							MoveToSubmitClass(mBack);
						}
					} else {
						MoveToSubmitClass(mBack);
					}

				} else {
					if (alarm != null) {
						// Commented by Akhil Malik
						// alarm.CancelAlarm(Detector_Inspector_StartNewInspectionWithHistoryActivity.this);
					}

				}

			} else {
				Utils.showToastAlert(
						Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
						"Got Exception,Please try again!");
			}
		}

		private void CheckSizeOfALaram(int i) {
			switch (i) {
			case 0:
				// previous history large then current SIze
				approvalCode(mProperty);
				break;
			case 1:
				// current size is greater then previous history
				ContinueCode(mProperty);
				break;
			default:
				break;
			}
		}

		// ALl data compare
		private void checkhistoryData(List<ReportItem> mPreviousHistory,
				Report tempReport) {
			boolean flag = false;
			for (ReportItem pReport : mPreviousHistory) {
				boolean b = CompareTheList(pReport.getDetectorType(),
						pReport.getExpiryYear());
				if (b) {
					flag = false;
				} else {
					flag = true;
					break;
				}
			}
			if (flag) {
				approvalCode(mProperty);
			} else {
				int size = tempReport.getReportSections().size();
				if (size > 0) {
					// approvalCode(mProperty);
					ContinueCode(mProperty);

				} else {

					MoveToSubmitClass(mBack);
				}
			}

		}
	}

	public boolean noOfAlaram(List<ReportItem> mPreviousHistory,
			Report tempReport) {
		int previousHistorySize = mPreviousHistory.size();
		int currentHistorySize = tempReport.getReportSections().size();

		if (previousHistorySize == currentHistorySize) {
			return true;
		} else {
			return false;
		}

	}

	public int AlaramSize(List<ReportItem> mPreviousHistory, Report tempReport) {
		int previousHistorySize = mPreviousHistory.size();
		int currentHistorySize = tempReport.getReportSections().size();
		if (previousHistorySize > currentHistorySize) {
			// previous history large then current SIze
			return 0;
		} else {
			// current size is greater then previous history
			return 1;
		}
	}

	public boolean CompareTheList(int detectorType, String expiryYear) {
		boolean flag = false;
		int i = 0;
		Log.d("Test", detectorType + " && " + expiryYear.trim());

		for (ReportSection cuReportSection : tempReport.getReportSections()) {
			int nDetectorType = cuReportSection.getItems().getDetectorType();
			String nExpiryYear = cuReportSection.getItems().getExpiryYear();

			Log.e("Test", nDetectorType + " == " + detectorType + " && "
					+ expiryYear.trim() + " == " + nExpiryYear.trim());
			if (nDetectorType == detectorType
					&& expiryYear.trim().equalsIgnoreCase(nExpiryYear.trim())) {

				flag = true;
				break;
			}
			i++;
		}
		if (flag) {
			tempReport.getReportSections().remove(i);
			return true;
		} else {
			return false;
		}

	}

	// expiryYear
	private void checkhistoryDataWithYear(List<ReportItem> mPreviousHistory,
			Report tempReport, boolean status) {
		boolean flag = false;
		for (ReportItem pReport : mPreviousHistory) {
			boolean b = CompareTheList(pReport.getDetectorType(),
					pReport.getExpiryYear(), status);
			if (b) {
				flag = false;
			} else {
				flag = true;
				break;
			}
		}
		if (flag) {
			approvalCode(mProperty);
		} else {
			int size = tempReport.getReportSections().size();
			if (size > 0) {
				// approvalCode(mProperty);
				ContinueCode(mProperty);

			} else {

				MoveToSubmitClass(mBack);
			}
		}

	}

	public boolean CompareTheList(int detectorType, String expiryYear,
			boolean status) {
		boolean flag = false;
		int i = 0;
		Log.d("Test", detectorType + " && " + expiryYear.trim());

		for (ReportSection cuReportSection : tempReport.getReportSections()) {
			int nDetectorType = cuReportSection.getItems().getDetectorType();
			String nExpiryYear = cuReportSection.getItems().getExpiryYear();

			Log.e("Test", nDetectorType + " == " + detectorType + " && "
					+ expiryYear.trim() + " == " + nExpiryYear.trim());

			if (status) {
				if (nDetectorType == detectorType) {
					flag = true;
					break;
				}
			} else {
				if (expiryYear.trim().equalsIgnoreCase(nExpiryYear.trim())) {
					flag = true;
					break;
				}
			}

			i++;
		}
		if (flag) {
			tempReport.getReportSections().remove(i);
			return true;
		} else {
			return false;
		}

	}

	public void ContinueCode(final Inspection inspection) {

		aLoginDlg = new Dialog(this, R.style.CustomDialogTheme);
		aLoginDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		aLoginDlg.setContentView(R.layout.continue_code);

		TextView mTiTextView = (TextView) aLoginDlg
				.findViewById(R.id.title_txt);
		// mTiTextView.setText(getResources().getString(R.string.aproval_code));

		String address = inspection.getStreetNumber() + ", "
				+ inspection.getStreetName() + ", " + inspection.getSuburb()
				+ ", " + inspection.getState();

		mTiTextView.setText(address);

		TextView mtextView = (TextView) aLoginDlg.findViewById(R.id.text_view);
		// mtextView
		// .setText("Service Sheet items do not match with the previous history. Please double check the property or contact our office for an approval code.");

		aLoginDlg.findViewById(R.id.home_btn).setVisibility(View.GONE);
		aLoginDlg.findViewById(R.id.cancel_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						aLoginDlg.dismiss();
					}
				});
		aLoginDlg.findViewById(R.id.submit_button).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						aLoginDlg.dismiss();
						MoveToSubmitClass(mBack);
					}
				});
		aLoginDlg.show();

	}

	public void approvalCode(final Inspection inspection) {

		final String propertyId = inspection.getPropertyId();
		aLoginDlg = new Dialog(this, R.style.CustomDialogTheme);
		aLoginDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		aLoginDlg.setContentView(R.layout.approval_code);

		TextView mTiTextView = (TextView) aLoginDlg
				.findViewById(R.id.title_txt);
		// mTiTextView.setText(getResources().getString(R.string.aproval_code));

		String address = inspection.getStreetNumber() + ", "
				+ inspection.getStreetName() + ", " + inspection.getSuburb()
				+ ", " + inspection.getState();

		mTiTextView.setText(address);

		final EditText mAEditText = (EditText) aLoginDlg
				.findViewById(R.id.approval_code_edit_text);

		TextView mtextView = (TextView) aLoginDlg.findViewById(R.id.text_view);
		mtextView
				.setText("Service Sheet items do not match with the previous history. Please double check the property or contact our office for an approval code.");

		aLoginDlg.findViewById(R.id.home_btn).setVisibility(View.GONE);
		aLoginDlg.findViewById(R.id.cancel_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						Utils.hideKeyboard(
								Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
								mAEditText);
						aLoginDlg.dismiss();
					}
				});
		aLoginDlg.findViewById(R.id.submit_button).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String text = mAEditText.getText().toString();
						if (text.length() > 0) {
							if (text.trim().equalsIgnoreCase(propertyId.trim())) {

								Utils.hideKeyboard(
										Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
										mAEditText);
								aLoginDlg.dismiss();
								MoveToSubmitClass(mBack);

							} else {
								Utils.showAlert(
										Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
										"Message",
										"Please enter the correct approval code");
							}
						} else {
							Utils.showToastAlert(
									Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
									"Please enter the approval code first");
						}

					}
				});
		aLoginDlg.show();

	}

	public void capturePhoto(ReportSection reportSection) {

		int size = reportSection.getReportPhotos().size();
		if (size > 1) {
			Utils.showToastAlert(this, "Reached to Limit!");
		} else {
			String image_path = Const.ENV_SETTINGS.DETECTOR_PHOTO_DIR;
			String date = Utils.currentDateAsString();
			int quality = Const.PHOTO_QUALITY_CODES.HIGH_QUALITY;
			String photo_id = Utils.getUUID();
			String file_name = photo_id + ".jpg";
			Log.d("TEST", "photo file name is: " + file_name);

			mReportPhoto = new ReportPhoto(photo_id, null,
					reportSection.getItemUUID(), quality, date, "", 0, "", 0,
					0, 0);

			File out = new File(image_path);
			if (!out.exists()) {
				out.mkdirs();
			}

			out = new File(image_path, file_name);
			image_path = image_path + file_name;
			Uri uri = Uri.fromFile(out);

			Intent ci = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			ci.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			// Log.d("TEST","capture photo with quality: " +
			// mProfile.getSettings().quality);
			ci.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, quality);

			// ci.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,
			// mProfile.getSettings().quality);
			ci.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
					ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			startActivityForResult(ci, Const.ACTIVITY_CODES.START_CAPTURE_IMAGE);

		}

	}

	public void MoveToSubmitClass(Boolean b) {
		if (b) {
			Log.i("inside move to submit method of Detector_Inspector_StartNewInspectionWithHistoryActivity", "inside move to submit class");
			Utils.showToastAlert(Detector_Inspector_StartNewInspectionWithHistoryActivity.this,	"Sucessfully submit!");

			mYear = mCalendarNow.get(Calendar.YEAR);
			mMonth = mCalendarNow.get(Calendar.MONTH);
			mDay = mCalendarNow.get(Calendar.DAY_OF_MONTH);
			String mDate = mYear + Utils.twoDigitNo(mMonth + 1)+ Utils.twoDigitNo(mDay);

			Utils.showInspectionSubmitAlert(Detector_Inspector_StartNewInspectionWithHistoryActivity.this,latLng, mProperty, alarm, mDate);
		} /*else {

		}*/

	}

	public void setPhotoAdapterCallback(PhotoAdapter adapter) {
		// TODO Auto-generated method stub
		adapter.setPhotoAdpaterCallback(this);

	}

	public void setRadioButton(View v, ReportItem mReportItem, boolean b) {
		switch (v.getId()) {

		case R.id.battery_replaced:
			mReportItem.setBatteryReplaced(!b);
			break;
		case R.id.cleaned_check_box:
			mReportItem.setCleaned(!b);
			break;

		case R.id.stricked_applied:
			mReportItem.setStickedApplied(!b);

			break;

		case R.id.decibel_test:
			mReportItem.setDecibelTest(!b);
			break;

		case R.id.not_required:
			mReportItem.setNotRequired(!b);
			break;

		case R.id.volt_problem:
			if (b) {

			} else {
				mReportItem.setElectricalNote("");
			}
			mReportItem.setVoltProblem(!b);
			break;
		default:
			break;
		}
		updateReport(mReport, selectedReportSection);

	}

	public void chooseDetectorType(final int group, int pos) {
		mLoginDlg = new Dialog(this, R.style.CustomDialogTheme);
		mLoginDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mLoginDlg.setContentView(R.layout.activity_reason_why);

		TextView mTiTextView = (TextView) mLoginDlg
				.findViewById(R.id.title_txt);
		mTiTextView.setText(getResources().getString(R.string.detector_type));
		mLoginDlg.findViewById(R.id.home_btn).setVisibility(View.GONE);
		mLoginDlg.findViewById(R.id.note_edit_text).setVisibility(View.GONE);

		mLoginDlg.findViewById(R.id.cancel_button).setVisibility(View.GONE);
		mLoginDlg.findViewById(R.id.submit_button).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String reason = null;
						int value = 0;
						switch (selectedValue) {
						case Const.WHY_INSPECTION_NOT_COMPLETE_REASON.KEY_NOT_WORK:
							reason = getResources().getString(
									R.string.deteachable);
							value = 3;
							break;
						case Const.WHY_INSPECTION_NOT_COMPLETE_REASON.NOT_HOME:
							reason = getResources().getString(R.string.mains);
							value = 1;
							break;
						case Const.WHY_INSPECTION_NOT_COMPLETE_REASON.NO_KEY:
							reason = getResources().getString(
									R.string.detechable_recharge);
							value = 4;
							break;
						case Const.WHY_INSPECTION_NOT_COMPLETE_REASON.NOTE:
							reason = getResources()
									.getString(R.string.security);
							value = 5;

							break;

						case Const.WHY_INSPECTION_NOT_COMPLETE_REASON.OUT_OF_TIME:
							reason = getResources().getString(
									R.string.mains_recharge);
							value = 2;
							break;

						default:
							break;
						}
						mLoginDlg.dismiss();
						mReport.getReportSections().get(group).getItems()
								.setDetectorType(value);
						// mReportItem.setDetectorType(value);
						updateReport(mReport, selectedReportSection);
					}

				});

		((TextView) mLoginDlg.findViewById(R.id.key_not_working_txt_view))
				.setText(getResources().getString(R.string.deteachable));
		((TextView) mLoginDlg.findViewById(R.id.no_key_text_view))
				.setText(getResources().getString(R.string.detechable_recharge));
		((TextView) mLoginDlg.findViewById(R.id.tenant_home_text_view))
				.setText(getResources().getString(R.string.mains));
		((TextView) mLoginDlg.findViewById(R.id.out_of_time_txt_view))
				.setText(getResources().getString(R.string.mains_recharge));
		((TextView) mLoginDlg.findViewById(R.id.note_txt_view))
				.setText(getResources().getString(R.string.security));

		notWork = (ImageView) mLoginDlg.findViewById(R.id.key_not_working);

		notWork.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonVisibility(R.id.key_not_working);
			}
		});
		tenantNotTextView = (ImageView) mLoginDlg
				.findViewById(R.id.tenant_home);
		tenantNotTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonVisibility(R.id.tenant_home);
			}
		});
		noKeyTextView = (ImageView) mLoginDlg.findViewById(R.id.no_key);
		noKeyTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonVisibility(R.id.no_key);
			}
		});

		outOfTimeTextView = (ImageView) mLoginDlg
				.findViewById(R.id.out_of_time);
		outOfTimeTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonVisibility(R.id.out_of_time);
			}
		});

		writeNoteTextView = (ImageView) mLoginDlg.findViewById(R.id.note);
		writeNoteTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonVisibility(R.id.note);

			}
		});

		switch (pos) {
		case 1:
			setButtonVisibility(R.id.tenant_home);
			break;
		case 2:
			setButtonVisibility(R.id.out_of_time);
			break;
		case 3:
			setButtonVisibility(R.id.key_not_working);
			break;
		case 4:
			setButtonVisibility(R.id.no_key);
			break;
		case 5:
			setButtonVisibility(R.id.note);
			break;

		default:
			break;
		}

		mLoginDlg.show();
	}

	protected void updateReport(Report mReport, int selectedReportSection) {
		mProfile.setReport(mReport);

		addMulipleView(mReport, selectedReportSection);

		// updatePropertyLayout(mReport);
	}

	private void setButtonVisibility(int keyNotWorking) {
		switch (keyNotWorking) {
		case R.id.key_not_working:

			selectedValue = Const.WHY_INSPECTION_NOT_COMPLETE_REASON.KEY_NOT_WORK;

			notWork.setBackgroundResource(R.drawable.radio_sel);
			tenantNotTextView.setBackgroundResource(R.drawable.radio_desel);
			noKeyTextView.setBackgroundResource(R.drawable.radio_desel);
			writeNoteTextView.setBackgroundResource(R.drawable.radio_desel);
			outOfTimeTextView.setBackgroundResource(R.drawable.radio_desel);

			break;
		case R.id.tenant_home:
			selectedValue = Const.WHY_INSPECTION_NOT_COMPLETE_REASON.NOT_HOME;

			notWork.setBackgroundResource(R.drawable.radio_desel);
			tenantNotTextView.setBackgroundResource(R.drawable.radio_sel);
			noKeyTextView.setBackgroundResource(R.drawable.radio_desel);
			writeNoteTextView.setBackgroundResource(R.drawable.radio_desel);
			outOfTimeTextView.setBackgroundResource(R.drawable.radio_desel);
			break;
		case R.id.no_key:
			selectedValue = Const.WHY_INSPECTION_NOT_COMPLETE_REASON.NO_KEY;

			notWork.setBackgroundResource(R.drawable.radio_desel);
			tenantNotTextView.setBackgroundResource(R.drawable.radio_desel);
			noKeyTextView.setBackgroundResource(R.drawable.radio_sel);
			writeNoteTextView.setBackgroundResource(R.drawable.radio_desel);
			outOfTimeTextView.setBackgroundResource(R.drawable.radio_desel);
			break;

		case R.id.note:
			selectedValue = Const.WHY_INSPECTION_NOT_COMPLETE_REASON.NOTE;

			notWork.setBackgroundResource(R.drawable.radio_desel);
			tenantNotTextView.setBackgroundResource(R.drawable.radio_desel);
			noKeyTextView.setBackgroundResource(R.drawable.radio_desel);
			writeNoteTextView.setBackgroundResource(R.drawable.radio_sel);
			outOfTimeTextView.setBackgroundResource(R.drawable.radio_desel);
			break;

		case R.id.out_of_time:
			selectedValue = Const.WHY_INSPECTION_NOT_COMPLETE_REASON.OUT_OF_TIME;

			notWork.setBackgroundResource(R.drawable.radio_desel);
			tenantNotTextView.setBackgroundResource(R.drawable.radio_desel);
			noKeyTextView.setBackgroundResource(R.drawable.radio_desel);
			writeNoteTextView.setBackgroundResource(R.drawable.radio_desel);
			outOfTimeTextView.setBackgroundResource(R.drawable.radio_sel);

			break;

		default:
			break;
		}
	}

	public void setDate(final ReportItem mReportItem, int groupPosition,
			final int id, final View v) {

		String newExpiryYear = mReportItem.getNewExpiryYear();
		String expiryYear = mReportItem.getExpiryYear();
		mYear = mCalendarNow.get(Calendar.YEAR);

		switch (id) {
		case R.id.new_expiry_year_edit_txt:

			if (expiryYear.length() > 1) {
				mYear = Integer.parseInt(expiryYear) + 10;
			}
			new YearDialogActivity(new DateInterface() {

				@Override
				public void onDateSet(DatePicker view, int year, int month,
						int day) {
					mYear = year;
					mMonth = month - 1;
					mDay = day;
					updateDateOnReport(year + "", mReportItem, id, v);

				}
			}, Detector_Inspector_StartNewInspectionWithHistoryActivity.this)
					.showDateDialog(mYear, mMonth, mDay).show();
			break;

		case R.id.expiry_edit_txt:
			if (newExpiryYear.length() > 1) {
				mYear = Integer.parseInt(newExpiryYear) - 10;
			}
			new YearDialogActivity(new DateInterface() {

				@Override
				public void onDateSet(DatePicker view, int year, int month,
						int day) {
					mYear = year;
					mMonth = month - 1;
					mDay = day;
					updateDateOnReport(year + "", mReportItem, id, v);

				}
			}, Detector_Inspector_StartNewInspectionWithHistoryActivity.this)
					.showDateDialog(mYear, mMonth, mDay).show();

			break;
		}

	}

	protected void updateDateOnReport(String mDate, ReportItem mReportItem,
			int id, View v) {
		// mDate = Utils.getConvertedDateinfullFormat(mDate);

		switch (id) {
		case R.id.manufacture_edit_txt:
			mReportItem.setManufacturer(mDate);
			break;
		case R.id.expiry_edit_txt:

			String expiryDate1 = mReportItem.getNewExpiryYear();
			if (expiryDate1.length() > 1) {
				int expryDate = Integer.parseInt(expiryDate1);
				int newExpryDate = Integer.parseInt(mDate);
				if (newExpryDate <= expryDate) {
					mReportItem.setExpiryYear(mDate);
					setCancelVisibility(v, mReportItem.getExpiryYear());
				} else {
					Utils.showAlert(
							Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
							"Message",
							"Expiry year always less than or equal to new expiry year");
				}
			} else {
				mReportItem.setExpiryYear(mDate);
			}

			break;

		case R.id.new_expiry_year_edit_txt:

			String expiryDate = mReportItem.getExpiryYear();
			if (expiryDate.length() > 1) {
				int expryDate = Integer.parseInt(expiryDate);
				int newExpryDate = Integer.parseInt(mDate);
				if (newExpryDate >= expryDate) {
					mReportItem.setNewExpiryYear(mDate);
					setCancelVisibility(v, mReportItem.getNewExpiryYear());
				} else {
					Utils.showAlert(
							Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
							"Message",
							"New expiry year always greater than or equal to expiry year");
				}
			} else {
				mReportItem.setNewExpiryYear(mDate);
			}

			break;

		default:
			break;
		}
		updateReport(mReport, selectedReportSection);
	}

	@Override
	public void startPhotoViewer(ReportPhoto photo) {
		mProfile.setReport(mReport);
		mProfile.setReportPhoto(photo);
		Utils.GoToPhotoViewActivity(Detector_Inspector_StartNewInspectionWithHistoryActivity.this);

	}

	@Override
	public void setDeleteImage(ReportPhoto photo, int position)      {

		Log.d("Delete here", "delete here " + position);
		showAlert(
				Detector_Inspector_StartNewInspectionWithHistoryActivity.this,
				"Confirmation Message",
				"Are you sure you wish to delete this picture?", position,
				photo);

	}

	public void showAlert(final Context context, String title, String message,
			final int position, ReportPhoto photo) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								dialog.dismiss();

								DatabaseUtil.deletePhotoOnLong(
										mReport.getReportSections()
												.get(selectedReportSection)
												.getReportPhotos()
												.get(position), context);
								mReport.getReportSections()
										.get(selectedReportSection)
										.getReportPhotos().remove(position);
								updateReport(mReport, selectedReportSection);
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();

					}
				})

				.create().show();
	}

	public static void saveBase64OnSDCard(String base64) {

		String mString = "";

		mString = base64;

		File newFolder = new File(Environment.getExternalStorageDirectory(),
				"Base64");
		if (!newFolder.exists()) {
			newFolder.mkdir();
		}
		try {
			File file = new File(newFolder, ("Base64") + ".txt");
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			FileOutputStream fos;
			byte[] data = mString.getBytes();
			try {
				fos = new FileOutputStream(file);
				fos.write(data);
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		} catch (Exception ex) {
			System.out.println("ex: " + ex);
		}
	}
}
