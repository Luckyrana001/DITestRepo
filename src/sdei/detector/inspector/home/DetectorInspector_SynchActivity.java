package sdei.detector.inspector.home;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.R;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.sync.SyncPhoto;
import sdei.detector.inspector.sync.SyncReport;
import sdei.detector.inspector.sync.service.SyncService;
import sdei.detector.inspector.sync.service.engine.ISyncEngine;
import sdei.detector.inspector.sync.service.ws.WSAddReportResult;
import sdei.detector.inspector.sync.service.ws.WSRequest;
import sdei.detector.inspector.sync.service.ws.WSResponse;
import sdei.detector.inspector.util.Const;
import sdei.detector.inspector.util.DatabaseUtil;
import sdei.detector.inspector.util.Utils;
import android.app.Activity;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.detector.inspector.lib.model.Inspection;
import com.detector.inspector.lib.util.Util;
import com.detector.inspector.lib.widget.DataParseResult;
import com.google.myjson.Gson;

public class DetectorInspector_SynchActivity extends sdei.detector.inspector.BaseActivity implements	SyncInspectionAdapter.AdapterCallback {

	@Override
	protected void onPause() 
	{		
		super.onPause();
		wl.release();
	}

	@Override
	public void onBackPressed()   
	{
		// countDownTimer.cancel();
		Utils. GoToHomeScreenBackGround (DetectorInspector_SynchActivity.this);
	}

	public static final String ACTION = "sdei.detector.inspector.home.DetectorInspector_SynchActivity";
	private ListView              mSyncInspectionList;
	private SyncInspectionAdapter mAdapter;
	private List<SyncReport>      mSyncReportList;
	private List<SyncReport>      oldSyncReports = new ArrayList<SyncReport>();
	private List<Inspection>      mInspectionsList;

	// private List<SyncReport> mNeedSyncReportList;
	private UserProfile    mProfile;
	private Button         mSyncBtn;
	private boolean        mFirstLaunch = true;
	// private boolean mSyncStarted = false;
	private boolean        syncboolean = false;

	private PowerManager.WakeLock wl;
	public KeyguardLock lock;

	
	
	
	
	/*sync service connection established here*/
	
	
	private ISyncEngine mSyncEngine;
	private SyncReceiver mSyncReceiver = new SyncReceiver();
	
	
	/**
	   * This is the class which represents the actual service-connection.
	   * It type casts the bound-stub implementation of the service class to our AIDL interface.
	   */
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

	private Calendar mCalendarNow;

	private int mYear;

	private int mMonth;

	private int mDay;

	private String mDate;

	private String report_uuid;

	private CountDownTimer countDownTimer;

	class SyncReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent)                                       {
			// TODO Auto-generated method stub
			Log.d("TEST", "sync receive broadcast" + intent.getAction());
			if (intent.getAction().equals(Const.WS_RESPONSE_ACTIONS.WS_SYNCH_NETWORK_ERROR))    
			{
				String reportUUID = intent.getExtras().getString("report_uuid");

				changeSyncStatusToFail(mSyncReportList, reportUUID);
				mAdapter.setData(createData(mSyncReportList));
				mAdapter.notifyDataSetChanged();
				Utils.showAlert(DetectorInspector_SynchActivity.this,"Error","Couldn't connect to the server. Please check your network settings or contact your office manager.");

			} else if (intent.getAction().equals(Const.WS_RESPONSE_ACTIONS.WS_ADD_REPORT_SUCCESS))   {

				Log.i("TEST", "Sync Report uploaded successfully");

				String report_uuid = intent.getExtras()	.getString("report_uuid");
				String sync_grp_id = intent.getExtras().getString("sync_group_id");

				WSResponse response = intent.getParcelableExtra("response");
				DataParseTask data_task = new DataParseTask(Const.WS_REQUEST_TYPES.ADD_REPORT, report_uuid,	sync_grp_id);
				data_task.execute(response);
				
				
			} else if (intent.getAction().equals(Const.WS_RESPONSE_ACTIONS.WS_UPLOAD_PHOTO_SUCCESS))  {
				
				Log.d("TEST", "upload photo success");
				String report_uuid = intent.getExtras()	.getString("report_uuid");
				String photo_uuid  = intent.getExtras() .getString("photo_uuid");
				String sync_grp_id = intent.getExtras() .getString(	"sync_group_id");
				Log.d("TEST", "report uuid: " + report_uuid + " photo_uuid: "+ photo_uuid);

				WSResponse response = intent.getParcelableExtra("response");
				DataParseTask data_task = new DataParseTask(Const.WS_REQUEST_TYPES.UPLOAD_PHOTO, report_uuid,photo_uuid, sync_grp_id);
				data_task.execute(response);
			}
		}
	}

	class DataParseTask extends AsyncTask<WSResponse, Void, DataParseResult>       {

		private int mType;
		private String mReportUUID;
		private String mPhotoUUID;
		private String mSyncGrpId;

		public DataParseTask(int type, String report_uuid, String sync_grp_id)    
		{
			mType       = type;
			mReportUUID = report_uuid;
			mSyncGrpId  = sync_grp_id;
		}

		public DataParseTask(int type, String report_uuid, String photo_uuid,String sync_grp_id) 
		{
			mType       = type;
			mReportUUID = report_uuid;
			mPhotoUUID  = photo_uuid;
			mSyncGrpId  = sync_grp_id;
		}

		@Override
		protected DataParseResult doInBackground(WSResponse... response)           {
		
			// JsonUtil.parseJsonText(response[0].getHttpResult());
			Gson gson = new Gson();
			DataParseResult result = new DataParseResult();
			result.report_uuid     = mReportUUID;
			
			switch (mType)        {
			
			case Const.WS_REQUEST_TYPES.ADD_REPORT:
				
				WSAddReportResult http_result = gson.fromJson(response[0].getHttpResult(), WSAddReportResult.class);
				if (http_result == null || http_result.getStatus() == 0) 
				{
					result.isSuccess = false;
					changeSyncStatusToFail(mSyncReportList, mReportUUID);
					try 
					{
					  mSyncEngine.cancelSyncGroup(mSyncGrpId);
					} catch (RemoteException e) 
					{						
						e.printStackTrace();
					}
				} else 
				{
					String propertyId = http_result.getAddedPropertyId();
					
					// update report id
					Utils.updateReportId(propertyId, mReportUUID, DetectorInspector_SynchActivity.this);
					
					// mark report added flag to true
					markReportAdded       (mSyncReportList, mReportUUID);
					
					// update sync status
					updateSyncReportStatus(mSyncReportList, mReportUUID);
					
					// upload photo
					uploadReportPhoto     (mSyncReportList, mReportUUID, propertyId);
					// TEST
					// changeSyncStatusToFail(mNeedSyncReportList,
					// result.report_uuid);
				}
				break;
				
				 
			case Const.WS_REQUEST_TYPES.UPLOAD_PHOTO:
				
				String upload_result = response[0].getHttpResult();
				
				if (upload_result.startsWith("OK,"))  {
					
					// update the finished photo number in sync report
					increaseUploadPhoto(mSyncReportList, mReportUUID, mPhotoUUID);
					
					// update report sync status
					updateSyncReportStatus(mSyncReportList, result.report_uuid);
					
				} else {
					
					result.isSuccess = false;					
					changeSyncStatusToFail(mSyncReportList, mReportUUID);
					try 
					    {
						   mSyncEngine.cancelSyncGroup(mSyncGrpId);
					    } 
					catch (RemoteException e) 
					{						
						e.printStackTrace();
					}
				}
				break;
			}
			return result;
		}

		@Override
		protected void onPostExecute(DataParseResult result) {

			mAdapter.setData(createData(mSyncReportList));
			mAdapter.notifyDataSetChanged();

		}

	}

	private void uploadReportPhoto(List<SyncReport> list, String report_uuid,	String report_id) {
		SyncReport report = null;
		for (SyncReport r : list) {
			if (r.getReportUUID().equals(report_uuid)) 
			{
				report = r;
				break;
			}
		}
		if (report != null) 
		{
			report.setReportId(report_id);
			syncPhoto(report);

		}
	}

	private void markReportAdded(List<SyncReport> list, String report_uuid) {
		Log.d("TEST", "mark report added");
		SyncReport report_to_mark = null;
		for (SyncReport r : list) {
			if (r.getReportUUID().equals(report_uuid))   {
				report_to_mark = r;
				break;
			}
		}
		if (report_to_mark != null)                      {
			
			report_to_mark.setReportAdded(true);
			Log.d("TEST", "report " + report_to_mark.getReportUUID() + " is added");
		}
	}

	private void syncPhoto(SyncReport sync_report)       {
		
		String report_uuid = sync_report.getReportUUID();
		Log.d("TEST", "sync report " + report_uuid);
				
		try {
			String report_id = sync_report.getReportId();			
			if (report_id != null)     {
				
				List<SyncPhoto> photo_list = sync_report.getPhotos();
				Set<SyncPhoto> set = new HashSet<SyncPhoto>();
				set.addAll(photo_list);
				photo_list.clear();
				photo_list.addAll(set);

				
				/*here we are retrieving photoUUid from Photo_list*/
				for (SyncPhoto photo : photo_list)         {
					
					mSyncEngine.uploadPhoto(new WSRequest  (
							
							Const.WS_URLS.WS_UPLOAD_SERVICE_BASE_URL,
							Const.HTTP_METHOD_CODES.HTTP_POST,
							Const.WS_REQUEST_TYPES.UPLOAD_PHOTO), 
							report_uuid,	
							photo.getPhotoUUID(), 
							report_id);   
				}
			}
		}   
		    catch (RemoteException e) 
		    {
			e.printStackTrace();
		    }

	}

	private void increaseUploadPhoto(List<SyncReport> list, String report_uuid,	String photo_uuid) {

		for (SyncReport sync_report : list) {
			if (sync_report.getReportUUID().equals(report_uuid)) {
				for (SyncPhoto photo : sync_report.getPhotos()) {
					if (photo.getPhotoUUID().equals(photo_uuid)) {
						sync_report.increaseUploadPhotoCount();
					}
				}
			}
		}
	}

	private void changeSyncStatusToFail(List<SyncReport> list,
			String report_uuid) {
		SyncReport report_to_mark = null;
		for (SyncReport r : list) {
			if (r.getReportUUID().equals(report_uuid)) {
				report_to_mark = r;
				break;
			}
		}
		if (report_to_mark != null) {
			report_to_mark
					.setSyncStatus(Const.REPORT_SYNC_STATUS_CODES.SYNC_FAILED);
		}
	}

	private void updateSyncReportStatus(List<SyncReport> list,	String report_uuid) {
		Log.d("TEST", "update sync report status"); 
		/*Sync Report is a getter setter class */
		SyncReport report_to_mark = null;
		for (SyncReport r : list) {
			if (r.getReportUUID().equals(report_uuid)) {
				report_to_mark = r;
				break;
			}
		}
		if (report_to_mark != null)     
		    {			
			List<SyncPhoto> photo_list = report_to_mark.getPhotos();

			Set<SyncPhoto> set = new HashSet<SyncPhoto>();			
			set.addAll(photo_list);			
			photo_list.clear();			
			photo_list.addAll(set);

			if (report_to_mark.isReportAdded()	&& (report_to_mark.getUploadedPhotoCount() == photo_list.size())) 
			{
				report_to_mark.setSyncStatus(Const.REPORT_SYNC_STATUS_CODES.SYNC_FINISHED);				
				/* updating database with sync status */
				DatabaseUtil.updatePropertySyncStatus(DetectorInspector_SynchActivity.this,	report_to_mark.getReportUUID(),	Const.REPORT_SYNC_STATUS_CODES.SYNC_FINISHED);
				
				Log.d("TEST", "report " + report_to_mark.getReportUUID() + " sync finished");
			} 
			else if (report_to_mark.isReportAdded()	&& (report_to_mark.getUploadedPhotoCount() == 0)) 
			{
				DatabaseUtil.updatePropertySyncStatus (DetectorInspector_SynchActivity.this,report_to_mark.getReportUUID(),Const.REPORT_SYNC_STATUS_CODES.SYNC_HALF);
			}
		}
	}

	@Override
	public void onBeforeCreate(Bundle savedInstanceState) {

		IntentFilter filter = new IntentFilter(	Const.WS_RESPONSE_ACTIONS.WS_ADD_REPORT_SUCCESS);
		filter.addAction(Const.WS_RESPONSE_ACTIONS.WS_UPLOAD_PHOTO_SUCCESS);
		filter.addAction(Const.WS_RESPONSE_ACTIONS.WS_SYNCH_NETWORK_ERROR);

		registerReceiver(mSyncReceiver, filter);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		mProfile = DetectorInspectorApplication.getInstance();

		// if (mProfile.getReport() != null)
		// mProfile.getReport().setLeftCard(true);
		// mProfile = UserProfile.getInstance();

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

		mCalendarNow = Calendar.getInstance();
		mYear = mCalendarNow.get(Calendar.YEAR);
		mMonth = mCalendarNow.get(Calendar.MONTH);
		mDay = mCalendarNow.get(Calendar.DAY_OF_MONTH);

		setContentView(R.layout.activity_sync_inspection);
		// TODO Auto-generated method stub
		super.onBeforeCreate(savedInstanceState);
		 
		/** bindService which binds our activity(DetectorInspector_SynchActivity) to our service(SyncService). */
		// bind service
		bindService(new Intent(DetectorInspector_SynchActivity.this,SyncService.class), mConnection, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void getView() 
	{
		TextView mTiTextView = (TextView) findViewById(R.id.title_txt);
		mTiTextView.setText(getResources().getString(R.string.sync_inspection));
		findViewById(R.id.home_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.GoToHomeScreenBackGround(DetectorInspector_SynchActivity.this);
			}
		});

		mSyncInspectionList = (ListView) findViewById(R.id.sync_inspection_list);
		mSyncBtn = (Button) findViewById(R.id.sync_now_btn);

	}

	protected void startSynch(boolean b) {

		int count = 0;
		// if (Util.checkWiFi(DetectorInspector_SynchActivity.this))
		if (mSyncReportList != null) {
			for (SyncReport r : mSyncReportList) {

				Log.i("inside startSynch method", "Inside startSynch Method");
				
				if (r.isNeedToSync()) {
					// mSyncStarted = true;
					// DatabaseUtil.updateIsNeedToSynchStatus(
					// DetectorInspector_SynchActivity.this,
					// r.getReportUUID(),
					// Const.IS_NEED_TO_SYNC.PROGRESS_NEED_TO_SYNC);
					
					r.setSyncStatus(Const.REPORT_SYNC_STATUS_CODES.SYNC_IN_PROGRESS);
					r.setUploadedPhotoCount(0);
					r.setNeedToSync(false);
					syncReportOnly(r);
					count++;

				}
			}
			if (count == 0) {
				if (b) {
					Utils.showAlert(DetectorInspector_SynchActivity.this,"Sync","Please select at least one inspection to sync.");
				} else {}
			} else {

				setSynchAdapter(mSyncReportList);
				syncboolean = true;

			}

		}
	}

	protected void syncReportOnly(SyncReport sync_report)        {
		
		report_uuid = sync_report.getReportUUID();

		Log.d("TEST", mSyncEngine + "sync report " + report_uuid);
		try {
			// add report first and upload the photo when get the add report
			// feedback.
			
			/*
			 * @params
			 *  WSRequest = a getter setter of parcelable type which contain 
					a url, http method type and reponse type
				WSResponse= a getter setter of parcelable type which contains
				   http response and json result
			 */
			String[] sync_ids = mSyncEngine.addReport 
					(
							
					new WSRequest
					(
							Const.WS_URLS.WS_SERVICE_BASE_SYNCH_REPORT_URL,
					        Const.HTTP_METHOD_CODES.HTTP_POST,
					        Const.WS_REQUEST_TYPES.ADD_REPORT),
					        report_uuid, 
					        Util.getReportDir(DetectorInspector_SynchActivity.this
					),
					sync_report.getLat(), 
					sync_report.getLng(), 
					sync_report.getBookingId(), 
					mProfile.getTechnicianId(),
					sync_report.getmServiceSheetId()
					
					);
			
			sync_report.setSyncTaskGroupId(sync_ids[0]);
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private SyncInspectionAdapter createAdapter(List<SyncReport> report_list) {

		String[] from = new String[] {

		        "sync_report_street", 
		        "sync_report_finish_status",
				"sync_report_finished_background",
				"sync_report_finished_percentage",
				"sync_report_finished_photo_desp",
				"sync_report_finished_photo_desp1", 
				"sync_report_check",
				"sync_success_indicator", 
				"sync_fail_indicator",
				"sync_restart_btn", 
				"sync_status_complete",
				"sync_report_statusyogesh", 
				"sync_statucomplete_date",
				"sync_progressBar"

		};

		int[] to = new int[] {

		R.id.sync_report_street,                         // text view
				R.id.sync_report_finished_status,        // text view
				R.id.sync_report_finished_background,    // image view
				R.id.sync_report_finished_percentage,    // linear layout
				R.id.sync_report_finished_photo_desp,    // text view
				R.id.sync_report_finished_photo_desp1,   // text view
				R.id.sync_report_check,      // image view
				R.id.sync_success_indicator, // image view
				R.id.sync_fail_indicator, // image view
				R.id.sync_restart_btn, // text view
				R.id.sync_status_complete,// textView
				R.id.sync_report_statusyogesh,// textView
				R.id.sync_statucomplete_date, // textView
				R.id.sync_progressBar // ProgressBar

		};

		return new SyncInspectionAdapter(this, createData(report_list),	R.layout.sync_inspection_item, from, to);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	
	private List<? extends Map<String, ?>>        createData(List<SyncReport> report_list) {

		List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();
		for (SyncReport sync_report : report_list) {

			Map data_map = new HashMap();

			int isNeed;
			if (sync_report.getSyncStatus() == Const.REPORT_SYNC_STATUS_CODES.SYNC_FINISHED) {
				isNeed = Const.IS_NEED_TO_SYNC.COMPLETLY_NEED_TO_SYNC;
			} else {
				isNeed = Const.IS_NEED_TO_SYNC.NEED_TO_SYNC;
			}

			// Log.e("Value of IsNeed :", isNeed + ": value is");
			data_map.put("sync_report_street", sync_report.getStreet());
			data_map.put("sync_report_finished_percentage", sync_report);

			Map btn_data = new HashMap();
			btn_data.put("status", isNeed);
			btn_data.put("synch_report", sync_report);

			data_map.put("sync_report_finished_background", btn_data);
			data_map.put("sync_report_finish_status", btn_data);
			data_map.put("sync_report_finished_photo_desp", btn_data);
			data_map.put("sync_report_finished_photo_desp1", sync_report);

			data_map.put("sync_status_complete", sync_report);
			// data_map.put("sync_statucomplete_date", Utils
			// .getConvertedDateinfullFormat(mDate));
			data_map.put("sync_statucomplete_date", Utils.getConvertedDateinfullFormat(sync_report.getcompleteDateString()));

			switch (sync_report.getSyncStatus()) {
			case Const.REPORT_SYNC_STATUS_CODES.SYNC_NOT_STARTED:

				Log.e("Is Need to Synch", isNeed + "");
				if (isNeed == Const.IS_NEED_TO_SYNC.COMPLETLY_NEED_TO_SYNC) {
					data_map.put("sync_report_check", null);
					data_map.put("sync_success_indicator", true);
					data_map.put("sync_progressBar", false);
				} else if (isNeed == Const.IS_NEED_TO_SYNC.PROGRESS_NEED_TO_SYNC) {

					data_map.put("sync_report_check", null);
					data_map.put("sync_success_indicator", null);
					data_map.put("sync_progressBar", true);
				} else {
					data_map.put("sync_report_check", sync_report);
					data_map.put("sync_success_indicator", null);
					data_map.put("sync_progressBar", false);
				}

				data_map.put("sync_fail_indicator", null);
				data_map.put("sync_restart_btn", null);

				break;
			case Const.REPORT_SYNC_STATUS_CODES.SYNC_IN_PROGRESS:
				data_map.put("sync_report_check", null);
				data_map.put("sync_success_indicator", null);
				data_map.put("sync_fail_indicator", null);
				data_map.put("sync_restart_btn", null);
				data_map.put("sync_progressBar", true);
				break;
			case Const.REPORT_SYNC_STATUS_CODES.SYNC_FINISHED:
				data_map.put("sync_report_check", null);
				data_map.put("sync_success_indicator", true);
				data_map.put("sync_fail_indicator", null);
				data_map.put("sync_restart_btn", null);
				data_map.put("sync_progressBar", false);
				break;
			case Const.REPORT_SYNC_STATUS_CODES.SYNC_FAILED:

				if (syncboolean && report_list.size() < 0) {

					Utils.showAlert(DetectorInspector_SynchActivity.this,
							"Report Missing",
							"No report exists for this property.Please complete the inspection first");
					syncboolean = false;
				}

				data_map.put("sync_report_check", null);
				data_map.put("sync_success_indicator", null);
				data_map.put("sync_fail_indicator", true);
				data_map.put("sync_restart_btn", sync_report);
				data_map.put("sync_progressBar", false);
				break;
			}

			if (sync_report.getSyncStatus() == Const.REPORT_SYNC_STATUS_CODES.SYNC_FAILED) {
				data_map.put("sync_report_statusyogesh", true);
			} else {
				data_map.put("sync_report_statusyogesh", false);
			}
			data.add(data_map);
		}
		return data;

	}

	@Override
	protected void onStart() {
	
		super.onStart();                                 Log.d("", "on start");

		mDate = mYear + Utils.twoDigitNo(mMonth) + Utils.twoDigitNo(mDay);

		// Log.e("Date", mDate);
		/*
		 * mInspectionsList is Array list of all Inspection which are of today's date or pending inspection list which have need to sync  with server.
		 * */
		mInspectionsList = getDataToDataBase(Util.todayDateOne(), false);
		
		if (mInspectionsList.size() > 0)  {			
			// Nothing happen here
			/*	
			 * mSyncReportList is Arraylist of SyncReport getter setter
			 * SyncReport hold information about each  inspection place data with seperate array list for each inspection photos
			 * 
			 *  */
			if (mSyncReportList != null)  
			{              
				Log.e("Test", "mSYnchReportList is not null");				
			    oldSyncReports.clear();
				oldSyncReports = mSyncReportList;
			}

			   /* Generating sync listing here */
			   mSyncReportList = getSyncReportList (mInspectionsList,oldSyncReports, DetectorInspector_SynchActivity.this);			
			   setSynchAdapter(mSyncReportList);
			
			
			mSyncBtn.setOnClickListener(new View.OnClickListener()        
			{
				@Override
				public void onClick(View v) 
				{
					startSynch(true);
				}

			});

		} else 
		{
//			Utils.showToastAlert(DetectorInspector_SynchActivity.this,
//					"No Synch Inspection List Found!");
		}
	}

	private List<Inspection> getDataToDataBase(String date, boolean b) 
	{
		showProgress();
		List<Inspection> mInspectionLists = DatabaseUtil.getSYnchHistoryDate(DetectorInspector_SynchActivity.this, date, b);
		hideProgress();
		return mInspectionLists;
	}

	private void setSynchAdapter(List<SyncReport> mSyncReportList) {
		
		if (mAdapter != null) 
		{
			updateAdapterVales(mSyncReportList);
		} else 
		{
			mAdapter = createAdapter(mSyncReportList);
			mAdapter   .setViewBinder(mAdapter);
			mAdapter   .setAdapterCallback(DetectorInspector_SynchActivity.this);
			mSyncInspectionList.setAdapter(mAdapter);
		}
	}

	private void updateAdapterVales(List<SyncReport> mSyncReportList) 	
	{
		mAdapter.setData(createData(mSyncReportList));
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		super.onResume();
		wl.acquire();

		Log.d("TEST", "SyncInspection onResume" + mFirstLaunch);
		if (mFirstLaunch) 
		{
			mFirstLaunch = false;
		} else
		{}
		if (Util.isOnline(DetectorInspector_SynchActivity.this)) {
			// AutoSynch
			countDownTimer = new CountDownTimer(500, 1000) {

				@Override
				public void onTick(long millisUntilFinished) 
				{}

				@Override
				public void onFinish() 
				{					
					startSynch(false);
				}
			};
			countDownTimer.start();
		}
         else 
		{
			Utils.showToastAlert(DetectorInspector_SynchActivity.this,	"Network is not available");
		}

	}

	private List<SyncReport> getSyncReportList(List<Inspection> mInspectionsList, List<SyncReport> oldSyncReports,	Activity activity) {

		List<SyncReport> list = new ArrayList<SyncReport>();
		boolean flag = true;
		
		for (Inspection inspection : mInspectionsList) {
			flag = true;			
			if (inspection.getStatus() == Const.PROPERTY_STATUS_CODES.COMPLETED)   {
				
				for (SyncReport mSyncReport : oldSyncReports) {					
					Log.e("Test",mSyncReport.getBookingId()	+ " mSYnchReportList is not null "	+ inspection.getBookingId());					

					if (mSyncReport.getBookingId().trim()	.equalsIgnoreCase(inspection.getBookingId().trim())	&& mSyncReport .getInspectionId()	.trim()	.equalsIgnoreCase(inspection.getPropertyId().trim())) 
					{
						list.add(mSyncReport);
						flag = false;
						break;
					}
				}
				    Log.d("BackGround", "Flag Value" + flag);
				    
				if (flag)        
				{
					// String address = inspection.getStreetNumber() + ", "
					// + inspection.getStreetName() + ", "
					// + inspection.getSuburb() + ", " + inspection.getState();
					//
					String address = 
							inspection.getStreetNumber() + " "	+ 
					        inspection.getStreetName() + ", "	+ 
							inspection.getSuburb() + ", "
							+ inspection.getState() + ", "
							+ inspection.getPostCode();

					List<SyncPhoto> mSyncPhotos = DatabaseUtil.getPhotoListByReportUUID(this, inspection.getReport_uuid());
					Log.d("TEST", mSyncPhotos.size() + "create sync report for report_uuid: "	+ inspection.getReport_uuid());
					
					SyncReport sync_report = new SyncReport(
							inspection.getPropertyId(),
							inspection.getBookingId(),
							inspection.getReportId(),
							inspection.getReport_uuid(), 
							address,
							inspection.getSync_status(),
							inspection.getStatus(),
							mSyncPhotos,
							inspection.getSuburb(),
							DatabaseUtil.getLatLongValue(this,	inspection.getReport_uuid()),inspection.getServiceSheetId());
				
					
					sync_report.setcompleteDateString(inspection.getDate());
					list.add(sync_report);
				}
			}

		}

		return list;
	}

	@Override
	public void addOrRemoveSyncReport(SyncReport report, int pos) {

		Log.d("TEST", "addOrRemoveSyncReport");
		if (report.getStatus() != 3) 
		{
			Utils.showAlert(DetectorInspector_SynchActivity.this, "",	"Please complete the inspection before sync");
		} else {
			
			SyncReport add_sync_report = null;
			for (SyncReport r : mSyncReportList) 
			{
				if (r.getReportUUID().equals(report.getReportUUID())) 
				{
					add_sync_report = r;
					break;
				}
			}

			if (add_sync_report != null) {				                 Log.d("TEST","update sync report: "	+ add_sync_report.getReportUUID());
				
				if (add_sync_report.isNeedToSync())        {             Log.d("TEST", "remove from need to sync list");
					
					add_sync_report.setNeedToSync(false);
					
				} else 
				{                                                        Log.d("TEST", "add to need to sync list");					
					add_sync_report.setNeedToSync(true);
				}

				setSynchAdapter(mSyncReportList);

			}
		}

	}

	@Override
	public void truevalue() 
	{
		// TODO Auto-generated method stub
	}

	/*A single booking inspection data sync will be restarted here*/
	@Override
	public void restartSync (SyncReport report) 
	{
		SyncReport add_sync_report = null;
		
	/*	
	 * mSyncReportList is Arraylist of SyncReport getter setter
	 * SyncReport hold information about each  inspection place data with seperate arraylist for each inspection photos
	 * 
	 *  */
		for (SyncReport r : mSyncReportList)
		{
			if (r.getReportUUID().equals(report.getReportUUID())) 
			{
				add_sync_report = r;
				break;
			}
		}

		add_sync_report	.setSyncStatus(Const.REPORT_SYNC_STATUS_CODES.SYNC_IN_PROGRESS);
		add_sync_report .setUploadedPhotoCount(0);
		
		syncReportOnly (add_sync_report);  /* This method made changes to the sync report getter setter to change its status flag values */
		setSynchAdapter(mSyncReportList);
		
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		doUnbindService();
		unregisterReceiver(mSyncReceiver);		
	}

	private void doUnbindService() 
	{
		if (mSyncEngine != null) 
		{
			unbindService(mConnection);
		}
	}

}
