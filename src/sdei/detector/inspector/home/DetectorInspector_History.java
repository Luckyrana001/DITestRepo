package sdei.detector.inspector.home;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.R;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.home.DetectorInspector_HistoryAdapter.DetectorInspector_HistoryAdapterCallback;
import sdei.detector.inspector.sync.service.SyncService;
import sdei.detector.inspector.sync.service.engine.ISyncEngine;
import sdei.detector.inspector.sync.service.ws.WSLoadPropertyResult;
import sdei.detector.inspector.sync.service.ws.WSRequest;
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
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.detector.inspector.common.view.DateInterface;
import com.detector.inspector.lib.model.Inspection;
import com.detector.inspector.lib.util.Const;
import com.detector.inspector.lib.widget.CompleteDateDialogActivity;
import com.detector.inspector.lib.widget.DataParseResult;
import com.google.myjson.Gson;
import com.google.myjson.JsonParseException;

public class DetectorInspector_History extends sdei.detector.inspector.BaseActivity implements		DetectorInspector_HistoryAdapterCallback {
	public static final int DATE_DIALOG_ID = 0;
	public static final String ACTION = "sdei.detector.inspector.home.DetectorInspector_History";
	private ListView mListView;
	List<Inspection> mList;
	private DetectorInspector_HistoryAdapter mAdapter;
	private Calendar mCalendarNow;
	private int    mYear;
	private int    mMonth;
	private int    mDay;
	private String mDate;

	
	//===============================================================================
	private static ISyncEngine mSyncEngine;  // aidl 
	private SyncReceiver mSyncReceiver = new SyncReceiver(); // broadcast reciever
	private ServiceConnection mConnection = new ServiceConnection()  {
		// Called when the connection with the service is established
		public void onServiceConnected(ComponentName className, IBinder service) {
			mSyncEngine = ISyncEngine.Stub.asInterface(service);
			LoadProperty();
		}

		// Called when the connection with the service disconnects unexpectedly
		public void onServiceDisconnected(ComponentName className) {
			Log.d("TEST", "Service has unexpectedly disconnected");
			mSyncEngine  =  null;
		}

	};
	
	///=======================================================   =====================
	private UserProfile mProfile;
	private String mLoginTaskId;

	@Override
	public void onBeforeCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onBeforeCreate(savedInstanceState);

		mProfile = DetectorInspectorApplication.getInstance();
		// mProfile = UserProfile.getInstance();

		mCalendarNow = Calendar.getInstance();
		mYear = mCalendarNow.get(Calendar.YEAR);
		mMonth = mCalendarNow.get(Calendar.MONTH);
		mDay = mCalendarNow.get(Calendar.DAY_OF_MONTH);
		setContentView(R.layout.activity_history);

		// Register service broadcast receiver
		IntentFilter filter = new IntentFilter(	sdei.detector.inspector.util.Const.WS_RESPONSE_ACTIONS.WS_LOAD_PROPERTY_HISTORY_SUCCESS);
		filter.addAction(sdei.detector.inspector.util.Const.WS_RESPONSE_ACTIONS.WS_NETWORK_ERROR);
		Log.i("","history broadcast registered.");
		registerReceiver(mSyncReceiver, filter);

		// bind service
		bindService(new Intent(DetectorInspector_History.this,SyncService.class), mConnection, Context.BIND_AUTO_CREATE);

	}

	class SyncReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(sdei.detector.inspector.util.Const.WS_RESPONSE_ACTIONS.WS_NETWORK_ERROR)) {
				hideProgress();
				Log.d("TEST", "Load Property success");
				mDate = mYear + Utils.twoDigitNo(mMonth + 1) + Utils.twoDigitNo(mDay);
				updateDateButtonValues(mDate.trim(), false);

			} else if (intent.getAction().equals(sdei.detector.inspector.util.Const.WS_RESPONSE_ACTIONS.WS_LOAD_PROPERTY_HISTORY_SUCCESS)) {
				Log.d("TEST", "Load Property success");
				WSResponse response = intent.getParcelableExtra("response");
				DataParseTask data_task = new DataParseTask(sdei.detector.inspector.util.Const.WS_REQUEST_TYPES.LOAD_PROPERTY_HISTORY);
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
				if (mType == sdei.detector.inspector.util.Const.WS_REQUEST_TYPES.LOAD_PROPERTY_HISTORY) {
					WSLoadPropertyResult wsLoadPropertyResult = gson.fromJson(
							mProfile.getResponse(), WSLoadPropertyResult.class);

					if (wsLoadPropertyResult == null
							|| wsLoadPropertyResult.getStatus() == 0) {
						result.isSuccess = false;
						mProfile.setResponseMessage(wsLoadPropertyResult
								.getMessage());
					} else {

						List<Inspection> inspectioList = wsLoadPropertyResult
								.getInspectionList();
						DatabaseUtil.importHistoryProperty(
								DetectorInspector_History.this, inspectioList);

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
				if (mType == sdei.detector.inspector.util.Const.WS_REQUEST_TYPES.LOAD_PROPERTY_HISTORY) {
					hideProgress();
					if (result.isSuccess) {
						mDate = mYear + Utils.twoDigitNo(mMonth + 1)
								+ Utils.twoDigitNo(mDay);
						updateDateButtonValues(mDate.trim(), false);
					} else {

						Utils.showToastAlert(DetectorInspector_History.this,"Not Got the result,Please try again!");

						// Utils.showAlert(DetectorInspector_History.this,
						// "Message", mProfile.getMessage());
					}
				 }
			 } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
		 }

	}

	private void LoadProperty() {
		 showProgress();
		WSRequest request = new WSRequest(
				
sdei.detector.inspector.util.Const.WS_URLS.WS_SERVICE_BASE_HISTORY_URL,
Const.HTTP_METHOD_CODES.HTTP_POST,
sdei.detector.inspector.util.Const.WS_REQUEST_TYPES.LOAD_PROPERTY_HISTORY);

		try {
			mLoginTaskId = mSyncEngine.getHistoryProperty(	request, mProfile.getTechnicianId());
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

	@Override
	public void getView() {
		TextView mTiTextView = (TextView) findViewById(R.id.title_txt);
		mTiTextView.setText(getResources().getString(R.string.previous_inspe));
		mListView = (ListView) findViewById(R.id.inspection_list);

	}

	public void buttonClick(View v) {
		switch (v.getId()) {
		case R.id.home_btn:
			finish();
			break;
		case R.id.select_date_button:
			Log.i("inside date click >>", "clicked ");

			new CompleteDateDialogActivity(new DateInterface() {

				@Override
				public void onDateSet(DatePicker view, int year, int month,
						int day) {
					mYear = year;
					mMonth = month - 1;
					mDay = day;
					mDate = String.valueOf(year) + Utils.twoDigitNo(month)
							+ Utils.twoDigitNo(day);
					updateDateButtonValues(mDate, true);

				}
			}, DetectorInspector_History.this).showDateDialog(mYear, mMonth,
					mDay).show();

			break;
		default:
			break;
		}
	}

	private void updateDateButtonValues(String date, boolean b) {

		showProgress();

		mDate = Utils.getConvertedDateinfullFormat(date);
		if (b) {
			mList.clear();
		}

		mList = getDataToDataBase(date, b);
		hideProgress();

		if (mList.size() > 0) {
			// Nothing happen here
		} else {
			Utils.showToastAlert(DetectorInspector_History.this,
					"No Inspection List Found!");
		}
		setListAdapterValue(mList, b);
	}

	private List<Inspection> getDataToDataBase(String date, Boolean b) {
		List<Inspection> mInspectionLists = DatabaseUtil.getHistoryListByDate(DetectorInspector_History.this, date, b);
		return mInspectionLists;
	}

	private void setListAdapterValue(List<Inspection> list, boolean b) {
		if (mAdapter != null) {
			updatePropertyAdapter(list, b);
		} else {
			mAdapter = createPropertyAdapter(list, b);
			mAdapter.setViewBinder(mAdapter);
			mAdapter.setPropertyCallback(DetectorInspector_History.this);
			mListView.setAdapter(mAdapter);
		}
	}

	private void updatePropertyAdapter(List<Inspection> list, boolean b) {
		mAdapter.setData(createData(list, b));
		mAdapter.notifyDataSetChanged();
	}

	private DetectorInspector_HistoryAdapter createPropertyAdapter(
			List<Inspection> list, Boolean b) {

		String[] from = new String[] { "inspection_date", "inspection_date_bg",
				"inspection_address", "inspection_stutus", "inspection_da",
				"inspection_cancel_txt" };
		int[] to = new int[] { R.id.inspection_date, R.id.inspection_date_bg,
				R.id.inspection_address, R.id.inspection_stutus,
				R.id.inspection_da, R.id.inspection_cancel_txt };

		return new DetectorInspector_HistoryAdapter(
				DetectorInspector_History.this, createData(list, b),
				R.layout.activity_history_item, from, to);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<? extends Map<String, ?>> createData(List<Inspection> list,Boolean b) {

		List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();
		String mTempDate = "";
		int i = 0;
		for (Inspection p : mList) {
			Map data_map = new HashMap();
			String date = p.getDate();
			data_map.put("inspection_cancel_txt", b);

			data_map.put("inspection_date",	Utils.getConvertedDateinfullFormat(date));

			if (mTempDate.toString().trim().equalsIgnoreCase(date.trim())) {
				data_map.put("inspection_date_bg", false);
			} else {
				data_map.put("inspection_date_bg", true);
			}

			String mAddress = p.getStreetNumber() + " " + p.getStreetName()
					+ "," + p.getSuburb() + ", " + p.getState() + " "
					+ p.getPostCode();

			// String mAddress = "Street No "+p.getStreetNumber() +
			// ", Street Name "
			// + p.getStreetName() + ", " + p.getSuburb() + ", "
			// + p.getState()+" "+p.getPostCode();
			//

			// String mAddress = p.getStreetNumber() + "," + p.getStreetName()
			// + "," + p.getSuburb();
			data_map.put("inspection_address", mAddress);

			switch (p.getStatus()) {
			case Const.IS_NEED_TO_SYNC.NO_NEED_TO_SYNC:
				data_map.put("inspection_stutus", " Status : Pending");
				break;
			case Const.IS_NEED_TO_SYNC.COMPLETLY_NEED_TO_SYNC:
				data_map.put("inspection_stutus", " Status : Completed");
				break;
			case Const.IS_NEED_TO_SYNC.NEED_TO_SYNC:
				data_map.put("inspection_stutus", " Status :  Pending");
				break;
			case Const.IS_NEED_TO_SYNC.PROGRESS_NEED_TO_SYNC:
				data_map.put("inspection_stutus", " Status : In Progress ");
				break;

			default:
				break;
			}

			mTempDate = date;

			data_map.put("inspection_da", null);

			data.add(data_map);

			i++;
		}
		return data;

	}

	@Override
	public void RefreshAdapter() {
		mCalendarNow = Calendar.getInstance();
		mYear = mCalendarNow.get(Calendar.YEAR);
		mMonth = mCalendarNow.get(Calendar.MONTH);
		mDay = mCalendarNow.get(Calendar.DAY_OF_MONTH);
		mDate = mYear + Utils.twoDigitNo(mMonth + 1) + Utils.twoDigitNo(mDay);
		updateDateButtonValues(mDate.trim(), false);

	}

}
