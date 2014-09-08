package sdei.detector.inspector.home;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.R;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.sync.service.SyncService;
import sdei.detector.inspector.sync.service.engine.ISyncEngine;
import sdei.detector.inspector.sync.service.ws.WSResetPassResult;
import sdei.detector.inspector.sync.service.ws.WSResponse;
import sdei.detector.inspector.util.DatabaseUtil;
import sdei.detector.inspector.util.Utils;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.detector.inspector.common.view.BaseActivityNew;
import com.detector.inspector.lib.model.Inspection;
import com.detector.inspector.lib.util.Const;
import com.detector.inspector.lib.widget.DataParseResult;
import com.google.myjson.Gson;
import com.google.myjson.JsonParseException;

public class SendNotificationActivity extends BaseActivityNew {
	public static final String ACTION = "sdei.detector.inspector.home.SendNotificationActivity";

	UserProfile mProfile;
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

	private TextView mTextView;

	private String message;

	class SyncReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			try {
				if (intent.getAction().equals(
						Const.WS_RESPONSE_ACTIONS.WS_SEND_NOTIFICATION_SUCESS)) {

					WSResponse response = intent.getParcelableExtra("response");
					DataParseTask data_task = new DataParseTask(
							Const.WS_REQUEST_TYPES.sendmailNotification);
					data_task.execute(response);
				} else if (intent
						.getAction()
						.equals(Const.WS_RESPONSE_ACTIONS.WS_NETWORK_START_INSPECTION_ERROR)) {
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onBeforeCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onBeforeCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(
				Const.WS_RESPONSE_ACTIONS.WS_SEND_NOTIFICATION_SUCESS);
		filter.addAction(Const.WS_RESPONSE_ACTIONS.WS_NETWORK_START_INSPECTION_ERROR);
		registerReceiver(mSyncReceiver, filter);
		// bind service
		bindService(
				new Intent(SendNotificationActivity.this, SyncService.class),
				mConnection, Context.BIND_AUTO_CREATE);


		mProfile = DetectorInspectorApplication.getInstance();
		// mProfile = UserProfile.getInstance();
		setContentView(R.layout.send_notification);
	}

	@Override
	public void getView() {
		((TextView) findViewById(R.id.title_txt)).setText("Warning");
		findViewById(R.id.home_btn).setVisibility(View.GONE);

		Log.d("Test", "service stop there");

		message = getIntent().getStringExtra("message");//getDataToDataBase();

		mTextView = (TextView) findViewById(R.id.text_view);

		final String sendMessage = mProfile.getTechnicianName() + " - "
				+ message
				+ " has not been completed within the appointment time.";
		// appointment time � Address � Appointment not completed, please
		// complete as soon as possible as it has passed the inspection time.

		message = message
				+ " - Appointment not completed, please complete as soon as possible as it has passed the inspection time.";

		mTextView.setText(message);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
//				Utils.sendMailToServer(SendNotificationActivity.this,
//						sendMessage, mSyncEngine, mProfile.getUniqueCode());
			}
		}, 5000);
		findViewById(R.id.cancel_button).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// stopService(new Intent()
						// .setAction("sdei.detector.inspector.sync.service.NotificationService"));
						// Utils.sendMailToServer(SendNotificationActivity.this,
						// sendMessage,
						// mSyncEngine, mProfile.getUniqueCode());
						finish();

					}
				});

	}

	private String getDataToDataBase() {
		showProgress();
		Inspection inspection = DatabaseUtil.getAddressUsingUniqueCode(
				SendNotificationActivity.this, mProfile.getUniqueCode());

		String msg = inspection.getKeytime() + " - "
				+ inspection.getStreetNumber() + " "
				+ inspection.getStreetName() + ", " + inspection.getSuburb()
				+ ", " + inspection.getState() + " " + inspection.getPostCode();

		hideProgress();
		return msg;

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
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
					Utils.showToastAlert(SendNotificationActivity.this,
							mProfile.getMessage());

					if (result.isSuccess) {
						if (status == 0) {
							// not sucess

						} else {
							// sucessfully sent

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

}
