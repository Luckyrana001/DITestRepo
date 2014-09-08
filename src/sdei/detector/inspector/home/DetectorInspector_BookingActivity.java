package sdei.detector.inspector.home;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.DetectorInspector_LoginActivity;
import sdei.detector.inspector.R;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.sync.ParseJsonResult;
import sdei.detector.inspector.sync.ReadJsonTask;
import sdei.detector.inspector.sync.report.Report;
import sdei.detector.inspector.sync.report.ReportSection;
import sdei.detector.inspector.sync.service.SyncService;
import sdei.detector.inspector.sync.service.engine.ISyncEngine;
import sdei.detector.inspector.sync.service.ws.WSLoadPropertyResult;
import sdei.detector.inspector.sync.service.ws.WSRequest;
import sdei.detector.inspector.sync.service.ws.WSResponse;
import sdei.detector.inspector.util.DatabaseUtil;
import sdei.detector.inspector.util.Utils;
import android.app.Dialog;
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
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.detector.inspector.common.view.DateInterface;
import com.detector.inspector.lib.json.JsonUtil;
import com.detector.inspector.lib.model.Inspection;
import com.detector.inspector.lib.util.Const;
import com.detector.inspector.lib.util.Util;
import com.detector.inspector.lib.widget.CompleteDateDialogActivity;
import com.detector.inspector.lib.widget.DataParseResult;
import com.detector.inspector.lib.widget.DateDialogActivity;
import com.detector.inspector.lib.widget.PullToRefreshBase;
import com.detector.inspector.lib.widget.PullToRefreshBase.OnLastItemVisibleListener;
import com.detector.inspector.lib.widget.PullToRefreshBase.OnRefreshListener;
import com.detector.inspector.lib.widget.PullToRefreshListView;
import com.detector.inspector.synch.report.ReportItem;
import com.google.myjson.Gson;
import com.google.myjson.JsonParseException;

public class DetectorInspector_BookingActivity extends	sdei.detector.inspector.BaseActivity implements	BookingAdapter.AdapterCallback {

	public static final String ACTION = "sdei.detector.inspector.home.DetectorInspector_BookingActivity";
	public static final int DATE_DIALOG_ID = 0;
	private Button mDateDialog;
	private Calendar mCalendarNow;
	private int mYear;
	private int mMonth;
	private int mDay;
	private String mDate;
	int selectedValue = -1;
	boolean flag = false;
	List<Inspection> mInspectionLists;
	DateDialogActivity mDateDial;
	private UserProfile mProfile;
	private BookingAdapter mPropertyLayoutAdapter;
	private PullToRefreshListView mPropertyLayoutListView;
	
	private EditText noteEditWork;
	private ImageView writeNoteTextView;
	private ImageView noKeyTextView;
	private ImageView tenantNotTextView;
	private ImageView notWork;
	private ImageView outOfTimeTextView;
	private Dialog mLoginDlg;
	private Report mReport;
	private String report_uuid;

	static final int MENU_MANUAL_REFRESH = 0;
	static final int MENU_DISABLE_SCROLL = 1;
	static final int MENU_SET_MODE = 2;
	static final int MENU_DEMO = 3;

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
		public void onServiceDisconnected(ComponentName className) 
		{
			Log.d("TEST", "Service has unexpectedly disconnected");
			mSyncEngine  = null;
		}
	};
	private String mLoginTaskId;
	private Dialog aLoginDlg;

	class SyncReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent)  {
			if (intent.getAction().equals(Const.WS_RESPONSE_ACTIONS.WS_NETWORK_BOOKING_ERROR))       {
				Log.d("TEST", "inside WS_NETWORK_BOOKING_ERROR");
				hideProgress();
				Utils.showAlert(DetectorInspector_BookingActivity.this,	"Error", "Please try again!");

			} else if (intent.getAction().equals(Const.WS_RESPONSE_ACTIONS.WS_LOAD_PROPERTY_BOOKING_SUCCESS)) 
			{
				Log.d("TEST", "inside WS_LOAD_PROPERTY_BOOKING_SUCCESS");
				Log.d("TEST", "Load Property success WS_LOAD_PROPERTY_BOOKING_SUCCESS");
				WSResponse response = intent.getParcelableExtra("response");
				DataParseTask data_task = new DataParseTask( Const.WS_REQUEST_TYPES.LOAD_PROPERTY);
				data_task.execute(response); 

			} else if (intent.getAction().equals(Const.WS_RESPONSE_ACTIONS.WS_UNALLOCATED_BOOKING_SUCCESS)) {
				Log.d("TEST", "inside WS_UNALLOCATED_BOOKING_SUCCESS");
				Log.d("TEST", "Load Property success" + mProfile.getResponse());
				WSResponse response = intent.getParcelableExtra("response");
				DataParseTask data_task = new DataParseTask(Const.WS_REQUEST_TYPES.UN_ALLOCATED);
				data_task.execute(response);

			}
		}
	}

	class DataParseTask extends AsyncTask<WSResponse, Void, DataParseResult> {

		private int mType;

		public DataParseTask(int type) 
		{
			mType = type;
		}

		@Override
		protected DataParseResult doInBackground(WSResponse... response) {
			// TODO Auto-generated method stub
			// JsonUtil.parseJsonText(response[0].getHttpResult());
			
			Log.i("TEST", "inside data parse task");
			Gson gson = new Gson();
			DataParseResult result = new DataParseResult();
			try {
				if (mType == Const.WS_REQUEST_TYPES.LOAD_PROPERTY) {
					WSLoadPropertyResult wsLoadPropertyResult = gson.fromJson(mProfile.getResponse(), WSLoadPropertyResult.class);

					if (wsLoadPropertyResult == null || wsLoadPropertyResult.getStatus() == 0)  
					{
						result.isSuccess = false;
					    Log.i("Load Property Akhil Log ------>>>>>>"	, wsLoadPropertyResult.getMessage().toString());
						mProfile.setResponseMessage(wsLoadPropertyResult.getMessage());
						
					} else {

						List<Inspection> inspectioList = wsLoadPropertyResult.getInspectionList();

						// List<Inspection> old_prop_list=new
						// ArrayList<Inspection>();
						List<Inspection> old_prop_list = DatabaseUtil.getPropertyListByStatus (DetectorInspector_BookingActivity.this,
										sdei.detector.inspector.util.Const.PROPERTY_STATUS_CODES.ALL);

						List<Inspection> new_prop_list;
						new_prop_list = Util.mergeInspectionList(inspectioList,	old_prop_list,	mProfile.getTechnicianValidation());
						//

						mProfile.setInspectionListSynch(new_prop_list);

						mProfile.setInspectionList(new_prop_list);
						DatabaseUtil.importProperty(DetectorInspector_BookingActivity.this,	new_prop_list);
					}

				} else if (mType == Const.WS_REQUEST_TYPES.UN_ALLOCATED) {
					mProfile.setResponseMessage(mProfile.getResponse());
				}
			} catch (JsonParseException e) {
				result.isSuccess = false;
			}
			return result;
		}

		@Override
		protected void onPostExecute(DataParseResult result) {
			try {
				if (mType == Const.WS_REQUEST_TYPES.LOAD_PROPERTY) {
					// hideProgress();
					if (result.isSuccess) {
						Utils.showToastAlert(DetectorInspector_BookingActivity.this,	"Update successfully");

						// List<String> date = DatabaseUtil
						// .largestDateToCurrentDate(
						// DetectorInspector_BookingActivity.this,
						// Util.todayDateOne());
						// int size = date.size();
						// if (size > 0) {
						// mProfile.largeDate(date.get(0));
						// Log.e("check date from web ", date.get(0));
						// } else {
						// mProfile.largeDate(Util.todayDateOne());
						// }
						// Log.d("current Date", mProfile.getSortDate()
						// + "+++++++++++++++++++++");

						getDataNew();
						// updateDateButtonValues(Util.todayDateOne(), false);

					} else {
						Utils.showAlert(DetectorInspector_BookingActivity.this,	"Message", mProfile.getMessage());
					}
				} else if (mType == Const.WS_REQUEST_TYPES.UN_ALLOCATED) {
					if (mProfile.getMessage().trim().equalsIgnoreCase("true")) {
						Utils.showToastAlert(DetectorInspector_BookingActivity.this,"Update successfully");
						UpdateReasonToDataBase(	mProfile.getInspectionForStartInspect(), "",mLoginDlg);

						getDataNew();
						// updateDateButtonValues(Util.todayDateOne(), false);
						// LoadProperty();
					} else {
						hideProgress();
						Utils.showAlert(DetectorInspector_BookingActivity.this,
								"Message", "Not updated sucessfully");
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void getDataNew()  {
			if (mProfile.getSettings().use_pre_text) {
				updateDateButtonValues(mProfile.getSettings().date, true);
			} else {
				updateDateButtonValues(Util.todayDateOne(), true);
			}
		}

	}

	private List<Inspection> getDataToDataBase(String date, Boolean b) {
		List<Inspection> mInspectionLists = DatabaseUtil.getPropertyListByDate(
				DetectorInspector_BookingActivity.this, date);
		return mInspectionLists;
	}

	private void updateDate(String date, boolean b) {

		showProgress();

		//mDate = Utils.getConvertedDateinfullFormat(date);.b.....current date not showing without login
		
		
//		DateFormat dateFormat = new SimpleDateFormat("MM/yyyy/dd");
//		   //get current date time with Date()
//		   Date mdate1 = new Date();
//		   System.out.println(dateFormat.format(mdate1));// don't print it, but save it!
//		   mDate = dateFormat.format(mdate1);

	     // mDate = Utils.getConvertedDateinfullFormat(String.valueOf(lDateTime));
	     // Log.i("mdate", mDate);
	      
		mDate = Utils.getConvertedDateinfullFormat(date);
		List<Inspection> mList = getDataToDataBase(date);
		Log.e("date test===========", mList.size() + ", yogesh selected date" + Utils.getConvertedDateinfullFormat(date));

		hideProgress();

		if (date == null)
			date = "";

		if (mList.size() > 0) {
			// Nothing happen here
			mProfile.getSettings().date = date;
			mProfile.getSettings().use_pre_text = b;
			Utils.savePreference(DetectorInspector_BookingActivity.this,mProfile.getSettings());
			
		} 
		else 
		{
			mProfile.getSettings().date = date;
			mProfile.getSettings().use_pre_text = false;
			Utils.savePreference(DetectorInspector_BookingActivity.this,mProfile.getSettings());
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
	
	

		IntentFilter filter = new IntentFilter(	Const.WS_RESPONSE_ACTIONS.WS_LOAD_PROPERTY_BOOKING_SUCCESS);
		
		filter.addAction(Const.WS_RESPONSE_ACTIONS.WS_UNALLOCATED_BOOKING_SUCCESS);
		filter.addAction(Const.WS_RESPONSE_ACTIONS.WS_NETWORK_BOOKING_ERROR);		
		registerReceiver(mSyncReceiver, filter);

		// bind service (SyncService.class is main  class which will do all web-services  processing task)
		//==============================================================================================================
		bindService(new Intent(DetectorInspector_BookingActivity.this,  SyncService.class), mConnection, Context.BIND_AUTO_CREATE);

		// fetching data from global class
		mProfile = DetectorInspectorApplication.getInstance();
		// mProfile = UserProfile.getInstance();

		mCalendarNow  = Calendar.getInstance();
		mYear         = mCalendarNow.get(Calendar.YEAR);
		mMonth        = mCalendarNow.get(Calendar.MONTH);
		mDay          = mCalendarNow.get(Calendar.DAY_OF_MONTH);
		
		setContentView(R.layout.activity_booking);

	}

	@Override
	public void getView() {  		TextView mTiTextView = (TextView) findViewById(R.id.title_txt);
		mTiTextView.setText(getResources().getString(R.string.booking_list));
		mDateDialog = (Button) findViewById(R.id.booking_date_picker);
		mPropertyLayoutListView = (PullToRefreshListView) findViewById(R.id.inspection_list);

		// Set a listener to be invoked when the list should be refreshed.
		mPropertyLayoutListView	.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh (
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(getApplicationContext(),System.currentTimeMillis(),  	DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()	.setLastUpdatedLabel(label);

						// Do work to refresh the list here.
						new GetDataTask().execute();
					}
				});

		// Add an end-of-list listener
		mPropertyLayoutListView	.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						Toast.makeText(DetectorInspector_BookingActivity.this,	"End of List!", Toast.LENGTH_SHORT).show();
					}
				});

	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			LoadProperty();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				;
			}
			return null;

		}

		@Override
		protected void onPostExecute(String[] result) {
			// mListItems.addFirst("Added after refresh...");

			// Call onRefreshComplete when the list has been refreshed.

			mPropertyLayoutListView.onRefreshComplete();

			// ((PullToRefreshListView) mPropertyLayoutListView)
			// .onRefreshComplete();

			super.onPostExecute(result);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		List<String> dates = DatabaseUtil.largestDateToCurrentDate (DetectorInspector_BookingActivity.this,	Util.todayDateOne());
		int size = dates.size();
		if (size > 0) 
		{
			mProfile.largeDate(dates.get(0));
			Log.e("check date from web ", dates.get(0));
		} else 
		{
			mProfile.largeDate(Util.todayDateOne());
		}
		String date = mProfile.getSortDate();
		// updateDate(date);
		 Log.d("current Date", mProfile.getSortDate());

		if (date != null && date.equalsIgnoreCase(Util.todayDateOne())) {
			Log.d("current Date", mProfile.getSortDate());
			updateDate(date, false);
		} else 
		{
			updateDate(date, true);
		}

		// Retrieving date value and showing the popup accordingly
		getData();

	}

	private void getData() {
		if (mProfile.getSettings().use_pre_text) {
			Utils.showAlert(DetectorInspector_BookingActivity.this,
					"Message","Please ensure you complete all bookings for the "
							+ Utils.getConvertedDateinfullFormat(mProfile.getSettings().date) + " before proceeding");
		}
        // updating date value   
		updateDateButtonValues(mProfile.getSettings().date,	mProfile.getSettings().use_pre_text);
	}

	private void updateDateButtonValues(String date, boolean use_pre_text) 
	{
		showProgress();
		mDate = Utils.getConvertedDateinfullFormat(date);
		mDateDialog.setText(mDate);
		mDateDialog.append(" (Select Date)");
		mInspectionLists = getDataToDataBase(date);
		hideProgress();
		updatePropertyLayout(mInspectionLists);

	}

	public void buttonClick(View v) {
		switch (v.getId()) {
		case R.id.home_btn:
			finish();
			break;
		case R.id.booking_date_picker:

			new CompleteDateDialogActivity(new DateInterface() {

				@Override
				public void onDateSet(DatePicker view, int year, int month,	int day) {
					mYear  = year;
					mMonth = month - 1;
					mDay   = day;
					mDate  = String.valueOf(year) + Utils.twoDigitNo(month)+ Utils.twoDigitNo(day);
					updateDateButtonValues(mDate, true);

				}

			}, DetectorInspector_BookingActivity.this).showDateDialog(mYear, mMonth, mDay).show();

			break;

		case R.id.booking_reorder_list:
			if (mInspectionLists.size() > 0) {
				String date = mDateDialog.getText().toString().replace(" (Select Date)", "");
				date = Utils.getDatefullFormat(date.trim());
				mProfile.setDate(date);
				mProfile.setInspectionList(mInspectionLists);
				Utils.GoToReOrderScreen(DetectorInspector_BookingActivity.this);
			}
			break;
		default:
			break;
		}
	}

	private List<Inspection> getDataToDataBase(String date) {
		List<Inspection> mInspectionLists = DatabaseUtil.getPropertyListByDate(DetectorInspector_BookingActivity.this, date);
		if (mInspectionLists.size() > 0) {
			Collections.sort(mInspectionLists, new Comparator<Inspection>() {
				public int compare(Inspection s1, Inspection s2) {
					return (s1.getDisplayRank() < s2.getDisplayRank() ? -1
							: (s1.getDisplayRank() == s2.getDisplayRank() ? 0
									: 1));

				}
			});
		}
		return mInspectionLists;

	}

	
	/*horizontal list Booking adapter populated using this method */
	private void updatePropertyLayout(List<Inspection> list) {    
		if (mPropertyLayoutAdapter != null) {
			updatePropertyAdapter(list);
		} 
		else 
		{			
			mPropertyLayoutAdapter = createAdapter(list);
			mPropertyLayoutAdapter .setViewBinder(mPropertyLayoutAdapter);
			mPropertyLayoutAdapter .setAdapterCallback(DetectorInspector_BookingActivity.this);
			mPropertyLayoutListView.setAdapter(mPropertyLayoutAdapter);
		}
	}

	private void updatePropertyAdapter(List<Inspection> list) 
	{
		mPropertyLayoutAdapter.setData(createGroupData(list));
		mPropertyLayoutAdapter.notifyDataSetChanged();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private BookingAdapter createAdapter(List<Inspection> list) {

		String[] group_from = new String[] { 
				"section_name",
				"appointment_time",
				"arrow_mark",
				"large_ladder",
				"inspection_note", 
				"inspection_not_complete",				
				"booking_top_relative_layout",
				"section_suburb" };
		int[] group_to = new int[] { 
				R.id.section_name, 
				R.id.appointment_time,
				R.id.arrow_mark, 
				R.id.large_ladder, 
				R.id.inspection_note,
				R.id.inspection_not_complete,
				R.id.booking_top_relative_layout,
				R.id.section_suburb };

		return new   BookingAdapter (this, createGroupData(list),R.layout.booking_layout_item, group_from, group_to);

	}

	@SuppressWarnings({ "rawtypes" })
	private List<Map<String, ?>> createGroupData(List<Inspection> list) {
		// initialize group item data map
		List<Map<String, ?>> group_list = new ArrayList<Map<String, ?>>();
		for (Inspection type : list) {
			// set group item value

			Map group_data_map = new HashMap();
			group_data_map.put("large_ladder", type);
			group_data_map.put("inspection_note", type);

			group_data_map.put("appointment_time", type);

			String address = type.getStreetNumber() + " "
					+ type.getStreetName();// + "," + type.getSuburb()

			// String address = "Street No " + type.getStreetNumber()
			// + ", Street Name " + type.getStreetName() + ", "
			// + type.getSuburb() + ", " + type.getState() + " "
			// + type.getPostCode();

			group_data_map.put("section_suburb", type.getSuburb());
			group_data_map.put("section_name", address);
			group_data_map.put("arrow_mark", true);
			group_data_map.put("inspection_not_complete", type);
			group_data_map.put("booking_top_relative_layout", type);
			group_list.add(group_data_map);
		}
		return group_list;
	}

	public void approvalCode(final Inspection inspection, final String reason) {

		final String propertyId = inspection.getPropertyId();
		aLoginDlg = new Dialog(this, R.style.CustomDialogTheme);
		aLoginDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		aLoginDlg.setContentView(R.layout.approval_code);

		TextView mTiTextView = (TextView) aLoginDlg	.findViewById(R.id.title_txt);
		String address = inspection.getStreetNumber() + ", "+ inspection.getStreetName() + ", " 
		+ inspection.getSuburb()
				+ ", " + inspection.getState();

		mTiTextView.setText(address);
		// getResources().getString(R.string.aproval_code)
		final EditText mAEditText = (EditText) aLoginDlg
				.findViewById(R.id.approval_code_edit_text);

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
						// TODO Auto-generated method stub
						String text = mAEditText.getText().toString();
						if (text.length() > 0) {
							if (text.trim().equalsIgnoreCase(propertyId.trim())) {
								aLoginDlg.dismiss();
								Utils.hideKeyboard(
										DetectorInspector_BookingActivity.this,
										mAEditText);
								Log.e("Tag", reason);
								unAllocatedBooking(inspection, reason);
							} else {
								Utils.showAlert(
										DetectorInspector_BookingActivity.this,
										"Message",
										"Please enter the correct approval code");
							}
						} else {
							Utils.showToastAlert(
									DetectorInspector_BookingActivity.this,
									"Please enter the approval code first");
						}

					}
				});
		aLoginDlg.show();

	}

	@Override
	public void ClickOnNotCompleted(int position, final Inspection inspection) {
		if (mProfile.getSettings().date.equalsIgnoreCase(inspection.getDate())) {
			workingdataNotComplete(inspection);
		}

	}

	private void workingdataNotComplete(final Inspection inspection) {
		mLoginDlg = new Dialog(this, R.style.CustomDialogTheme);
		mLoginDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mLoginDlg.setContentView(R.layout.activity_reason_why);

		TextView mTiTextView = (TextView) mLoginDlg
				.findViewById(R.id.title_txt);
		mTiTextView.setText(getResources().getString(
				R.string.why_it_has_not_been_completed_));
		mLoginDlg.findViewById(R.id.home_btn).setVisibility(View.GONE);
		noteEditWork = (EditText) mLoginDlg.findViewById(R.id.note_edit_text);
		noteEditWork.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					Utils.hideKeyboard(DetectorInspector_BookingActivity.this,
							noteEditWork);

					return true;
				}
				return false;
			}
		});

		mLoginDlg.findViewById(R.id.cancel_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mLoginDlg.dismiss();
					}
				});
		mLoginDlg.findViewById(R.id.submit_button).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String reason = null;
						switch (selectedValue) {
						case Const.WHY_INSPECTION_NOT_COMPLETE_REASON.KEY_NOT_WORK:
							reason = getResources().getString(
									R.string.key_didn_t_work);
							// UpdateReasonToDataBase(inspection, reason,
							// mLoginDlg);

							// unAllocatedBooking(inspection,reason);

							break;
						case Const.WHY_INSPECTION_NOT_COMPLETE_REASON.NOT_HOME:
							reason = getResources().getString(
									R.string.tenant_not_home);
							// UpdateReasonToDataBase(inspection, reason,
							// mLoginDlg);

							break;
						case Const.WHY_INSPECTION_NOT_COMPLETE_REASON.NO_KEY:
							reason = getResources().getString(
									R.string.no_key_or_problem);
							// UpdateReasonToDataBase(inspection, reason,
							// mLoginDlg);
							break;
						case Const.WHY_INSPECTION_NOT_COMPLETE_REASON.OUT_OF_TIME:
							reason = getResources().getString(
									R.string.ran_out_of_time);
							// UpdateReasonToDataBase(inspection, reason,
							// mLoginDlg);
							break;

						case Const.WHY_INSPECTION_NOT_COMPLETE_REASON.NOTE:
							reason = noteEditWork.getText().toString().trim();
							Utils.hideKeyboard(
									DetectorInspector_BookingActivity.this,
									noteEditWork);

							break;

						default:
							break;
						}

						if (reason.length() > 0) {
							mProfile.setInspectionForStartInspect(inspection);
							reason = mProfile.getTechnicianName() + " "
									+ Util.todayDate() + " "
									+ Util.currentDateAsString() + " " + reason;

							approvalCode(inspection, reason);

						} else {
							Utils.showToastAlert(
									DetectorInspector_BookingActivity.this,
									"Please enter the note");
						}

					}

				});

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

		setButtonVisibility(R.id.key_not_working);
		mLoginDlg.show();

	}

	protected void UpdateReasonToDataBase(Inspection inspection, String reason,
			Dialog mLoginDlg) {
		Utils.showToastAlert(DetectorInspector_BookingActivity.this,
				"Update Sucessfully!");
		DatabaseUtil.updatePropertyNotInspectReason(
				DetectorInspector_BookingActivity.this, inspection, reason);
		mLoginDlg.dismiss();
	}

	private void unAllocatedBooking(Inspection inspection, String reason) {
		showProgress();
		WSRequest request = new WSRequest(
				Const.WS_URLS.WS_SERVICE_UNALLOCATED_URL,
				Const.HTTP_METHOD_CODES.HTTP_POST,
				Const.WS_REQUEST_TYPES.UN_ALLOCATED);

		String key = inspection.getKeyNumber();
		if (key != null && key.length() > 0) {

		} else {
			key = "";
		}

		try {
			mLoginTaskId = mSyncEngine.unAllocatedBooking(request,mProfile.getTechnicianId(), inspection.getBookingId(),reason, inspection.getPropertyId(), key);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void setButtonVisibility(int keyNotWorking) {
		switch (keyNotWorking) {
		case R.id.key_not_working:

			selectedValue = Const.WHY_INSPECTION_NOT_COMPLETE_REASON.KEY_NOT_WORK;
			noteEditWork.setVisibility(View.GONE);

			notWork.setBackgroundResource(R.drawable.radio_sel);
			tenantNotTextView.setBackgroundResource(R.drawable.radio_desel);
			noKeyTextView.setBackgroundResource(R.drawable.radio_desel);
			writeNoteTextView.setBackgroundResource(R.drawable.radio_desel);
			outOfTimeTextView.setBackgroundResource(R.drawable.radio_desel);

			break;
		case R.id.tenant_home:
			selectedValue = Const.WHY_INSPECTION_NOT_COMPLETE_REASON.NOT_HOME;
			noteEditWork.setVisibility(View.GONE);

			notWork.setBackgroundResource(R.drawable.radio_desel);
			tenantNotTextView.setBackgroundResource(R.drawable.radio_sel);
			noKeyTextView.setBackgroundResource(R.drawable.radio_desel);
			writeNoteTextView.setBackgroundResource(R.drawable.radio_desel);
			outOfTimeTextView.setBackgroundResource(R.drawable.radio_desel);
			break;
		case R.id.out_of_time:
			selectedValue = Const.WHY_INSPECTION_NOT_COMPLETE_REASON.OUT_OF_TIME;
			noteEditWork.setVisibility(View.GONE);

			notWork.setBackgroundResource(R.drawable.radio_desel);
			tenantNotTextView.setBackgroundResource(R.drawable.radio_desel);
			noKeyTextView.setBackgroundResource(R.drawable.radio_desel);
			writeNoteTextView.setBackgroundResource(R.drawable.radio_desel);
			outOfTimeTextView.setBackgroundResource(R.drawable.radio_sel);

			break;

		case R.id.no_key:
			selectedValue = Const.WHY_INSPECTION_NOT_COMPLETE_REASON.NO_KEY;
			noteEditWork.setVisibility(View.GONE);

			notWork.setBackgroundResource(R.drawable.radio_desel);
			tenantNotTextView.setBackgroundResource(R.drawable.radio_desel);
			noKeyTextView.setBackgroundResource(R.drawable.radio_sel);
			writeNoteTextView.setBackgroundResource(R.drawable.radio_desel);
			outOfTimeTextView.setBackgroundResource(R.drawable.radio_desel);
			break;

		case R.id.note:
			selectedValue = Const.WHY_INSPECTION_NOT_COMPLETE_REASON.NOTE;
			noteEditWork.setVisibility(View.VISIBLE);

			notWork.setBackgroundResource(R.drawable.radio_desel);
			tenantNotTextView.setBackgroundResource(R.drawable.radio_desel);
			noKeyTextView.setBackgroundResource(R.drawable.radio_desel);
			writeNoteTextView.setBackgroundResource(R.drawable.radio_sel);
			outOfTimeTextView.setBackgroundResource(R.drawable.radio_desel);
			break;

		default:
			break;
		}
	}

	private boolean createReport(Inspection data) {
		try {

			String reportUUID = data.getReport_uuid();

			Report report = new Report();
			report.setReportId("0");
			report.setInspectionId(data.getPropertyId());
			report.setLeftCard(false);
			report.setSignature(false);
			report.setServiceNote("");
			report.setProblemNote("");

			String address = data.getStreetNumber() + ", "
					+ data.getStreetName() + ", " + data.getSuburb() + ", "
					+ data.getState();

			report.setName(address);// Report name on the basis
									// of address
			report.setReportUUID(reportUUID);
			mProfile.setReport(report);
			mProfile.getReportMap().put(data.getReport_uuid(), report);

			Log.d("TEST", "write report json file");
			JsonUtil.writeJSONFile(this, reportUUID + ".json", report);
			Log.d("TEST", "write report json file ended.");

			// previousHistory(data);

			return true;
		} catch (Exception e) {
			Log.d("TEST", "create report exception" + e.toString());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void OpenStartInspectionScreen(int position, Inspection data) {
		Log.d("TEST", "load report date" + data.getDate());
		if (mProfile.getSettings().date.equalsIgnoreCase(data.getDate())) 
		{
			workingdata(data);
		}

	}

	private void workingdata(Inspection data) {

		DetectorInspector_BookingActivity.this.showProgress();
		// load report
		report_uuid = data.getReport_uuid();
		// mReport = mProfile.getReportMap().get(report_uuid);
		// check if need to load report
		File file = new File(Util.getReportDir(this), report_uuid + ".json");
		
		if (file.exists()) {
			ReadReportTask read_report_task = new ReadReportTask(DetectorInspector_BookingActivity.this, data);
			read_report_task.execute(report_uuid);		
		} else 
		{
			CreateReportTask create_report_task = new CreateReportTask(data);
			create_report_task.execute();
		}
	}

	class CreateReportTask extends AsyncTask<Void, Void, Boolean>   {

		Inspection data;
		public CreateReportTask(Inspection data) 
		{
			this.data = data;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			DetectorInspector_BookingActivity.this.hideProgress();
			if (result) 
			{
				Log.d("TEST", "create report success");
				moveToStartInspection(data);
			} else 
			{
				Log.d("TEST", "create report failed");
				Utils.showToastAlert(DetectorInspector_BookingActivity.this,
						"create report failed");
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return createReport(data);
		}
	}

	class ReadReportTask extends ReadJsonTask {

		private Report mReport;
		private Inspection data;

		public ReadReportTask(Context context, Inspection data) {
			super(context);
			this.data = data;
		}

		@Override
		protected void onPostExecute(ParseJsonResult result) {
			try {

				DetectorInspector_BookingActivity.this.hideProgress();
				if (result.isSuccess) {

					Log.d("TEST", "load report success");
					mReport = result.report;
					/*
					 * update report item to set section name, set comment_uuid,
					 * section name and item name for report item comment
					 */
					mReport.updateReportItem();
					mReport.setReportUUID(data.getReport_uuid());
					mProfile.setReport(mReport);
					mProfile.getReportMap().put(data.getReport_uuid(), mReport);
					moveToStartInspection(data);
				} else 
				{
					Utils.showToastAlert(DetectorInspector_BookingActivity.this,"Load report failed");
				}
			} catch (Exception e) 
			{				
				e.printStackTrace();
				Log.d("TEST", e.toString());
			}
		}
	}

	public void moveToStartInspection(Inspection data) {
		mProfile.setInspectionForStartInspect(data);
		// Utils.GoToStartNewInspectionScreen(DetectorInspector_BookingActivity.this);
		// Utils.GoToStartInspectionScreen(DetectorInspector_BookingActivity.this);
		Utils.GoToStartNewInspectionHistoryScreen(DetectorInspector_BookingActivity.this);

	}

	private void previousHistory(Inspection data) {
		boolean status = true;
		Inspection prevInspection = null;
		for (Inspection mNewInspection : mProfile.getInspectionListSynch()) {
			if (data.getPropertyId().trim()
					.equals(mNewInspection.getPropertyId())) {
				if (mNewInspection.getPreviousHistory().size() > 0) {
					status = false;
					prevInspection = mNewInspection;
				}
			}
		}

		if (status) {

			} else {
			Report mReport = mProfile.getReport();
			updateSectionName(mReport, prevInspection);
		}
	}

	private void updateSectionName(Report newReport, String mSectionName,
			int type, String exYear, String newYear) {
		ReportSection add_section = new ReportSection();
		add_section.setSectionId(0);
		add_section.setHistoryDetail(true);
		add_section.setReportId(newReport.getReportId());
		add_section.setName(mSectionName);
		add_section.setEditLocation(false);
		add_section.setItems(new ReportItem (newReport.getReportId(), 0,
					mSectionName, 0, type, exYear, newYear));
		newReport.getReportSections().add(add_section);

		mProfile.setReport(newReport);
	}

	private void updateSectionName(Report mReport, Inspection prevInspection) 
	{
		for (ReportItem pr : prevInspection.getPreviousHistory())	
		{
			updateSectionName(mReport,       pr.getLocationName()  ,pr.getDetectorType(),   pr.getExpiryYear()
					,pr.getNewExpiryYear());
		}
	}

	@Override
	public void getActivtyResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Const.ACTIVITY_CODES.EDIT_ACTIVITY:
			if (resultCode == RESULT_OK) {
				updatePropertyLayout(mProfile.getInspectionList());
			}

			break;

		case Const.ACTIVITY_CODES.START_CAPTURE_IMAGE:
			Log.d("Test", "onACtivity Result");
			break;

		default:
			break;
		}
	}

	private void LoadProperty()  {

		WSRequest request = new WSRequest (
				
				Const.WS_URLS.WS_SERVICE_BASE_BOOKING_URL,
				Const.HTTP_METHOD_CODES.HTTP_POST,
				Const.WS_REQUEST_TYPES.LOAD_PROPERTY);

		try {
			// calling async task to load new booking 
			mLoginTaskId = mSyncEngine.getBookingProperty (request, mProfile.getTechnicianId());
		
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}