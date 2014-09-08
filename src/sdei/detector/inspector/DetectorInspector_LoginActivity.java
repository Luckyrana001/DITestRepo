package sdei.detector.inspector;

import java.util.List;

import sdei.detector.inspector.home.LocationTrackerBroadcastReceiver;
import sdei.detector.inspector.sync.Settings;
import sdei.detector.inspector.sync.service.SyncService;
import sdei.detector.inspector.sync.service.engine.ISyncEngine;
import sdei.detector.inspector.sync.service.ws.Technician;
import sdei.detector.inspector.sync.service.ws.WSLoadPropertyResult;
import sdei.detector.inspector.sync.service.ws.WSLoginResult;
import sdei.detector.inspector.sync.service.ws.WSRequest;
import sdei.detector.inspector.sync.service.ws.WSResetPassResult;
import sdei.detector.inspector.sync.service.ws.WSResponse;
import sdei.detector.inspector.util.Const;
import sdei.detector.inspector.util.DatabaseUtil;
import sdei.detector.inspector.util.Utils;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.detector.inspector.common.view.BaseActivityNew;
import com.detector.inspector.lib.location.LocationActivity;
import com.detector.inspector.lib.model.Inspection;
import com.detector.inspector.lib.util.Util;
import com.detector.inspector.lib.widget.DataParseResult;
import com.google.myjson.Gson;
import com.google.myjson.JsonParseException;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class DetectorInspector_LoginActivity extends BaseActivity {

	public static final String TAG = DetectorInspector_LoginActivity.class.getSimpleName();
	public static final String ACTION = Const.ACTIVITY_NAME.LOGIN_ACTIVITY;
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
	private static final int MSG_SHOW_MAINSCREEN = 0;

	// UI references.
	private EditText    mEmailView;
	private EditText    mPasswordView;
	private UserProfile mProfile;

	private static ISyncEngine mSyncEngine;
	private SyncReceiver       mSyncReceiver = new SyncReceiver();
	
	/**
	   * This is the class which represents the actual service-connection.
	   * It type casts the bound-stub implementation of the service class to our AIDL interface.
	   */
	private ServiceConnection  mConnection = new ServiceConnection()                  {
		// Called when the connection with the service is established
		public void onServiceConnected(ComponentName className, IBinder service)      {
			// Following the example above for an AIDL interface,			
			// this gets an instance of the IRemoteInterface, which we can use			
			// to call on the service
			
			Log.d("TEST", "Login Service has connected");			
			mSyncEngine = ISyncEngine.Stub.asInterface(service);			
			mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SHOW_MAINSCREEN), 3000);
			
		}

		// Called when the connection with the service disconnects unexpectedly
		public void onServiceDisconnected(ComponentName className) 
		{
			Log.d("TEST", "Service has unexpectedly disconnected");
			mSyncEngine = null;
		 }

	};

	private Handler mHandler = new Handler()     {
		@Override
		public void handleMessage(Message msg)   {
			switch (msg.what) {
			
			case MSG_SHOW_MAINSCREEN:				
				 setupViews();
				 break;
			}
		}
	};

	private String           mEmail;
	private String           mLoginTaskId;
	private boolean          isFirstTime = true;
	private LocationActivity mLocationActivity;

	public static LocationManager mLocationManager;
	public static LocationTrackerBroadcastReceiver alaram;

	@Override
	public void onBeforeCreate(Bundle savedInstanceState) {

		mProfile = DetectorInspectorApplication.getInstance();
		// UserProfile.getInstance();

		// Show splash screen
		ImageView splash = new ImageView(DetectorInspector_LoginActivity.this);
		splash.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
		splash.setImageResource(R.drawable.splash_screen);
		splash.setScaleType(ImageView.ScaleType.FIT_XY);
		setContentView(splash);

		// Set profile context
		mProfile.setContext(this.getApplicationContext());
		// load settings
		Settings settings    =   Utils.loadPreference(this);
		mProfile.setSettings(settings);
		mProfile.setLoginSucess(true);

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		alaram = new LocationTrackerBroadcastReceiver();

		// Register service broadcast receiver
		IntentFilter filter = new IntentFilter (Const.WS_RESPONSE_ACTIONS.WS_LOGIN_SUCCESS);
		filter.addAction                       (Const.WS_RESPONSE_ACTIONS.WS_FORGOT_PASS_SUCCESS);
		filter.addAction                       (Const.WS_RESPONSE_ACTIONS.WS_LOAD_PROPERTY_SUCCESS);
		filter.addAction                       (Const.WS_RESPONSE_ACTIONS.WS_LOAD_TRACK_LOCATION_SUCESS);
		filter.addAction                       (Const.WS_RESPONSE_ACTIONS.WS_LOAD_TRACK_LOCATION_SERVER_SUCESS);
		filter.addAction                       (Const.WS_RESPONSE_ACTIONS.WS_NETWORK_ERROR);
		registerReceiver(mSyncReceiver, filter);

		// bind service
		bindService(new Intent(DetectorInspector_LoginActivity.this,SyncService.class), mConnection, Context.BIND_AUTO_CREATE);
		super.onBeforeCreate(savedInstanceState);
	}

	protected void setupViews() {
		setContentView(R.layout.activity_detector_inspector__login);
		Log.e("isFirstTime", isFirstTime + "======");
		
		if (isFirstTime) {
			try {
				final boolean gpsEnabled = mLocationManager	.isProviderEnabled(LocationManager.GPS_PROVIDER);

				if (!gpsEnabled) {
					new com.detector.inspector.lib.location.EnableGpsDialogFragment()
							.show(getSupportFragmentManager(),
									       "enableGpsDialog");
				} 
				else  
				{
					isFirstTime = false;
				}
				mLocationActivity = new LocationActivity(DetectorInspector_LoginActivity.this);
				isFirstTime       = false;
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);

		mEmailView    =   (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);//=============================have need to uncomment it in final build development

		mPasswordView  =   (EditText) findViewById(R.id.password);
		mPasswordView	   .setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							if (Utils.attemptLogin( mEmailView,  mPasswordView,DetectorInspector_LoginActivity.this)) {
								showProgress();
								login(mEmailView.getText().toString().trim(),mPasswordView.getText().toString().trim());

							}
							return true;
						}
						return false;
					}
				});
	}

	
	
	private void login(String mEmail, String mPassword)   {

		WSRequest request = new WSRequest(
				Const.WS_URLS.WS_SERVICE_BASE_LOGIN_URL,
				Const.HTTP_METHOD_CODES.HTTP_POST, 
				Const.WS_REQUEST_TYPES.LOGIN);

		if (Const.DEBUG) 
		{
			mEmail = "yogeshc@smartdatainc.net";
			mPassword = "123456";
		}
		try {
			
		   /*mSyncEngine.login method call "login" method of SyncService class and which further post the 
			WSRequest + email id+ password
			to the LOGIN ASYNC TASK
			then LOGIN ASYNC TASK consume the WSRequest + email + password data and in post execute it broadcast result to the
			login activity reciever which then process the  WSResponse data which includes "request status and request result"*/
			
			mLoginTaskId = mSyncEngine.login(request, mEmail, mPassword);			
			Log.i("mLoginTaskId",mLoginTaskId);
			
		} catch (RemoteException e) 
		{
			e.printStackTrace();
		}
	}

	class SyncReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(Const.WS_RESPONSE_ACTIONS.WS_NETWORK_ERROR)) {
				hideProgress();

				if (mProfile.isLoginSucess()) {
					Utils.showAlert(DetectorInspector_LoginActivity.this,
							"Error",
							"Couldn't connect to the server. Please check your network settings");

				} else {
					mProfile.setCancelTimer(alaram);
					Log.d   ("test",	"Network is not available so it is delay for 25 sec");
					mProfile.setTTL(Const.TIMER.SHORT_TIME, true, alaram);
				}

			} else if (intent.getAction().equals(
					Const.WS_RESPONSE_ACTIONS.WS_FORGOT_PASS_SUCCESS)) {
				// Log.d("TEST", "login success");
				WSResponse response = intent.getParcelableExtra("response");
				DataParseTask data_task = new DataParseTask(Const.WS_REQUEST_TYPES.RESET_PASS);
				data_task.execute(response);

			} else if (intent.getAction().equals(Const.WS_RESPONSE_ACTIONS.WS_LOGIN_SUCCESS)) {				
				Log.d("TEST", "login success");
				WSResponse response = intent.getParcelableExtra("response");
				DataParseTask data_task = new DataParseTask (Const.WS_REQUEST_TYPES.LOGIN);
				data_task.execute(response);
			} else if (intent.getAction().equals(
					Const.WS_RESPONSE_ACTIONS.WS_LOAD_PROPERTY_SUCCESS)) {
				Log.d("TEST", "Load Property success");
				WSResponse response = intent.getParcelableExtra("response");
				DataParseTask data_task = new DataParseTask(
						Const.WS_REQUEST_TYPES.LOAD_PROPERTY);
				data_task.execute(response);

			} else if (intent.getAction().equals(
					Const.WS_RESPONSE_ACTIONS.WS_LOAD_TRACK_LOCATION_SUCESS)) {
				Log.d("TEST", "Track Location Broadcast Recieve success");

				mProfile.setCancelTimer(alaram);
				Log.e("Location Manager Broad cast",
						"Location Manager Broad cast-----------------");
             if(mLocationManager!=null){
				double latLng[] = mLocationActivity.setup(mLocationManager,
						DetectorInspector_LoginActivity.this);
        

				if (latLng == null) {
					// got the exception
					mProfile.setCancelTimer(alaram);
					// Log.d("TEST",
					// "Track Location Broadcast Recieve success but lat lng null");
					mProfile.setTTL(Const.TIMER.SHORT_TIME, true, alaram);

				} else {
					// Log.e("Test here", latLng[0]
					// + "lat and Long values on Reciever" + latLng[1]);
					LoadSaveLocation(latLng[0], latLng[1]);
				}
             }
			} else if (intent
					.getAction()
					.equals(Const.WS_RESPONSE_ACTIONS.WS_LOAD_TRACK_LOCATION_SERVER_SUCESS)) {
				Log.d("TEST", "Track Location Server success");
				WSResponse response = intent.getParcelableExtra("response");
				DataParseTask data_task = new DataParseTask(Const.WS_REQUEST_TYPES.SAVE_LOCATION);
				data_task.execute(response);
			}

		}
	}

	class DataParseTask extends AsyncTask<WSResponse, Void, DataParseResult> {

		private int mType;

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
				if (mType == Const.WS_REQUEST_TYPES.RESET_PASS) {
					WSResetPassResult ws_reset_pass_result = gson.fromJson(
							mProfile.getResponse(), WSResetPassResult.class);
					if (ws_reset_pass_result == null) {
						result.isSuccess = false;
					} else {
						// result.isSuccess = ws_reset_pass_result.isReset();
					}

					String value = ws_reset_pass_result.getMessage();
					if (value == null)   {
						mProfile.setResponseMessage("");
					} else {
						mProfile.setResponseMessage(value);
					}
				} else if (mType == Const.WS_REQUEST_TYPES.LOAD_PROPERTY) {
					WSLoadPropertyResult wsLoadPropertyResult = gson.fromJson(
							mProfile.getResponse(), WSLoadPropertyResult.class);

					if (wsLoadPropertyResult == null
							|| wsLoadPropertyResult.getStatus() == 0) {
						result.isSuccess = false;
						mProfile.setResponseMessage(wsLoadPropertyResult.getMessage());
					} else {

						List<Inspection> inspectioList = wsLoadPropertyResult.getInspectionList();

						// List<Inspection> old_prop_list=new
						// ArrayList<Inspection>();
						List<Inspection> old_prop_list = DatabaseUtil
								.getPropertyListByStatus(
										DetectorInspector_LoginActivity.this,
										Const.PROPERTY_STATUS_CODES.ALL);

						List<Inspection> new_prop_list;
						new_prop_list = Util.mergeInspectionList(inspectioList,	old_prop_list,	mProfile.getTechnicianValidation());

						Log.v(TAG, new_prop_list.size() + "new and old"	+ old_prop_list.size());

						mProfile.setInspectionListSynch(new_prop_list);
						mProfile.setInspectionList(new_prop_list);
						DatabaseUtil.importProperty(DetectorInspector_LoginActivity.this,new_prop_list);

					}

				} else if (mType == Const.WS_REQUEST_TYPES.LOGIN) {
					// Log.d("TEST", "parse login result");
					String result_str = mProfile.getResponse();
					// Log.d("TEST", "result is: " + result_str);
					if (result_str == null) {
						result.isSuccess = false;
					} 
					else 
					{
						WSLoginResult http_result = gson.fromJson(result_str,	WSLoginResult.class);
						if (http_result == null) {
							result.isSuccess = false;
						} else {
							Technician technicianId = http_result
									.getTechnicianId();
							if (http_result.getStatus() == Const.BIND_SERVICE_STATUS_CODES.BIND_SERVICE_SUCCESS
									|| technicianId == null
									|| technicianId.technicianId == null) {
								result.isSuccess = false;
							} else {
								mProfile.setTechnicianId(technicianId.technicianId);
								mProfile.setTechnicianName(technicianId.technicianName);
								// mProfile.setTechnicianValidation(technicianId.validationOff);

								mProfile.setTurnOffExpiryYear(!technicianId.turnOffExpiryYear);
								mProfile.setTurnOffAlarmType(!technicianId.turnOffAlarmType);
								mProfile.setTurnOffNoOfAlarms(!technicianId.turnOffNoOfAlarms);

							}
							mProfile.setResponseMessage(http_result
									.getMessage());
						}
					}
				} else if (mType == Const.WS_REQUEST_TYPES.SAVE_LOCATION) {
					WSResetPassResult ws_reset_pass_result = gson.fromJson(
							mProfile.getResponse(), WSResetPassResult.class);
					if (ws_reset_pass_result == null) {
						result.isSuccess = false;
					} else {
						// result.isSuccess = ws_reset_pass_result.isReset();
					}

					String value = ws_reset_pass_result.getMessage();
					if (value == null) {
						mProfile.setResponseMessage("");
					} else {
						mProfile.setResponseMessage(value);
					}
				}
			} catch (JsonParseException e) {
				result.isSuccess = false;
			}
			return result;
		}

		@Override
		protected void onPostExecute(DataParseResult result) {
			try {
				if (mType == Const.WS_REQUEST_TYPES.LOGIN) {
					if (result.isSuccess) {
						LoadProperty();
					}
					
					else 
					
					{
						hideProgress();
						Utils.showAlert(DetectorInspector_LoginActivity.this,	"Message", "Login Failed");
					}
				} else if (mType == Const.WS_REQUEST_TYPES.RESET_PASS) {
					hideProgress();
					Utils.showAlert(DetectorInspector_LoginActivity.this,"Message", mProfile.getMessage());
				} else if (mType == Const.WS_REQUEST_TYPES.LOAD_PROPERTY) {

					if (result.isSuccess) {
						Utils.showToastAlert(
								DetectorInspector_LoginActivity.this,
								"Login successfully");

						List<String> date = DatabaseUtil
								.largestDateToCurrentDate(
										DetectorInspector_LoginActivity.this,
										Util.todayDateOne());
						int size = date.size();
						if (size > 0) {
							mProfile.largeDate(date.get(0));
							Log.e("check date from web ", date.get(0));
						} else {
							mProfile.largeDate(Util.todayDateOne());
						}
						Log.d("current Date", mProfile.getSortDate()
								+ "+++++++++++++++++++++");
			          if(mLocationManager!=null){
						double latLng[] = mLocationActivity.setup(
								mLocationManager,
								DetectorInspector_LoginActivity.this);
						if (latLng == null) {
							// got the exception

							// Log.d("Test",
							// "Lat long null on login screen in load property post execute");

							mProfile.setTTL(Const.TIMER.SHORT_TIME, true,alaram);
							hideProgress();
							Utils.GoToHomeScreen(DetectorInspector_LoginActivity.this);
							
						} else {
							// Log.e("Test here", latLng[0] + "lat and Long"
							// + latLng[1]);
							LoadSaveLocation(latLng[0], latLng[1]);
						}
			          }
					} else {
						hideProgress();
						Utils.showAlert(DetectorInspector_LoginActivity.this,
								"Message", mProfile.getMessage());
					}
				} else if (mType == Const.WS_REQUEST_TYPES.SAVE_LOCATION) {
					hideProgress();
					mProfile.setCancelTimer(alaram);
					if (result.isSuccess) {
						Log.d("Test", "next broadcast on 60 sec on save location");
						mProfile.setTTL(Const.TIMER.LONG_TIME, true, alaram);
					} else {
						Log.d("Test", "Lat long null on save location");
						mProfile.setTTL(Const.TIMER.SHORT_TIME, true, alaram);

					}
					if (mProfile.isLoginSucess()) {
						Utils.GoToHomeScreen(DetectorInspector_LoginActivity.this);
					}
					Utils.showToastAlert(DetectorInspector_LoginActivity.this,
							mProfile.getMessage());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void LoadSaveLocation(double latLng, double latLng2) {
		WSRequest request = new WSRequest(Const.WS_URLS.WS_SAVE_LOCATION_URL,
				Const.HTTP_METHOD_CODES.HTTP_POST,
				Const.WS_REQUEST_TYPES.SAVE_LOCATION);
		try {

			mLoginTaskId = mSyncEngine.saveLocation(request, latLng + "",
					latLng2 + "", mProfile.getTechnicianId());
		} catch (RemoteException e) 
		{
			e.printStackTrace();
		}
	}

	private void LoadProperty() {

		WSRequest request = new WSRequest(
				Const.WS_URLS.WS_SERVICE_BASE_BOOKING_URL,
				Const.HTTP_METHOD_CODES.HTTP_POST,
				Const.WS_REQUEST_TYPES.LOAD_PROPERTY);

		try {
			mLoginTaskId = mSyncEngine.getProperty(request,
					mProfile.getTechnicianId());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public void loginButton(View v) {
		Utils.hideKeyboard(DetectorInspector_LoginActivity.this, v);
		switch (v.getId()) {
		case R.id.sign_in_button:
			if (Utils.attemptLogin(mEmailView, mPasswordView,
					DetectorInspector_LoginActivity.this)) {
				showProgress();

				// clear user profile report map
				mProfile.getReportMap().clear();

				login(mEmailView.getText().toString().trim(), mPasswordView	.getText().toString().trim());

			}
			break;
		case R.id.forgot_button:
			if (Utils.attemptLogin(mEmailView, null,
					DetectorInspector_LoginActivity.this)) {
				showProgress();
				forgotPassword(mEmailView.getText().toString().trim());
			}
			break;
		default:
			break;
		}
	}

	private void forgotPassword(String trim) {
		WSRequest request = new WSRequest(
				Const.WS_URLS.WS_SERVICE_BASE_RESET_URL,
				Const.HTTP_METHOD_CODES.HTTP_POST,
				Const.WS_REQUEST_TYPES.RESET_PASS);

		mEmail = mEmailView.getText().toString().trim();
		if (Const.DEBUG) {
			mEmail = "yogeshc@smartdatainc.net";
		}
		try {
			mLoginTaskId = mSyncEngine.forgotPass(request, mEmail);
		} catch (RemoteException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
