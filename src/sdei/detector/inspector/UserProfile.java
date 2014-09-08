package sdei.detector.inspector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

import sdei.detector.inspector.home.LocationTrackerBroadcastReceiver;
import sdei.detector.inspector.sync.Settings;
import sdei.detector.inspector.sync.report.Report;
import sdei.detector.inspector.sync.report.ReportPhoto;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.detector.inspector.lib.model.Inspection;
import com.detector.inspector.synch.report.ReportItem;

public class UserProfile {

	private static UserProfile instance = null;
	private static Context mContext;
	private ArrayList<String> mStrings;
	private String response;
	private String technicianId;
	private String message;
	private List<Inspection> inspectionList;
	private Report report;
	private Inspection data;
	private HashMap<String, String> yPhotoPool = new HashMap<String, String>();

	private WeakHashMap<String, Bitmap> mWeakHashMap = new WeakHashMap<String, Bitmap>();
	private ReportPhoto photo;
	private HashMap<String, Report> mReportMap = new HashMap<String, Report>();
	private List<Inspection> inspectionSynch;
	private String date;
	private boolean historyDetail;
	private String technicianName;
	private List<ReportItem> mItems;
	private Settings settings;
	private String todayDate;
	private int validationOff = 0;
	private String uniqueCode;
	private boolean turnOffExpiryYear;
	private boolean turnOffAlarmType;
	private boolean turnOffNoOfAlarms;
	private boolean isLogin;
	private boolean objectIsNull = true;

	UserProfile() {

	};

	// public static synchronized UserProfile getInstance() {
	// if (instance == null) {
	// instance = new UserProfile();
	// }
	// return instance;
	// }

	public void setTTL(int session_ttl, boolean b,	LocationTrackerBroadcastReceiver alarmManager) {

		if (alarmManager != null) 
		{
			alarmManager.setOnetimeTimer(getContext(), session_ttl);
		} 
		else 
		{
			Toast.makeText(getContext(), "Alarm is null", Toast.LENGTH_SHORT).show();
		}
	}

	public boolean isObjectIsNull() 
	{
		return objectIsNull;
	}

	public void setObjectIsNull(boolean objectIsNull) {
		this.objectIsNull = objectIsNull;
	}

	public void setCancelTimer(LocationTrackerBroadcastReceiver alarmManager) {
		if (alarmManager != null) 
		{
			alarmManager.CancelAlarm(getContext());
		} else {
			Toast.makeText(getContext(), "Alarm is null", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/*
	 * Set context
	 */
	public void setContext(Context context) {
		mContext = context;
	}

	/*
	 * Get context
	 */
	public Context getContext() {
		return mContext;
	}

	public void setArrayListValues(ArrayList<String> mStrings) {
		this.mStrings = mStrings;
	}

	public ArrayList<String> getArrayListValues() {
		return mStrings;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getResponse() {
		return response;

	}

	public void setTechnicianId(String technicianId) {
		this.technicianId = technicianId;
	}

	public String getTechnicianId() {
		return technicianId;
	}

	public void setResponseMessage(String message) {
		this.message = message;

	}

	public String getMessage() {
		return message;

	}

	public List<Inspection> getInspectionList() {
		return inspectionList;
	}

	public void setInspectionList(List<Inspection> inspectionList) {
		this.inspectionList = inspectionList;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public Report getReport() {
		return report;
	}

	public void setInspectionForStartInspect(Inspection data) {
		this.data = data;
	}

	public Inspection getInspectionForStartInspect() {
		return data;
	}

	public HashMap<String, String> getPhotoToPoolString() {
		return yPhotoPool;
	}

	public void addPhotoToPool(String key, String bm) {
		yPhotoPool.put(key, bm);
	}

	public void removePhotoFromPoolString(String key, String bm) {
		yPhotoPool.remove(key);

	}

	public WeakHashMap<String, Bitmap> getWeakPhotoToPoolString() {
		return mWeakHashMap;
	}

	public void addWeakPhotoToPool(String key, Bitmap bm) {
		mWeakHashMap.put(key, bm);
	}

	public void removeWEakPhotoFromPoolString(String key, Bitmap bm) {
		mWeakHashMap.remove(key);

	}

	public void setReportPhoto(ReportPhoto photo) {
		this.photo = photo;

	}

	public ReportPhoto getReportPhoto() {
		return photo;
	}

	public HashMap<String, Report> getReportMap() {
		return mReportMap;
	}

	public void updatePropertyReportId(String report_uuid, String report_id) {

		for (Inspection p : inspectionSynch) {
			if (p.getReport_uuid().equals(report_uuid)) {
				p.setReportId(report_id);
				return;
			}
		}
	}

	public void setInspectionListSynch(List<Inspection> new_prop_list) {
		this.inspectionSynch = new_prop_list;
	}

	public List<Inspection> getInspectionListSynch() {
		return inspectionSynch;

	}

	public void setDate(String date) {
		this.date = date;

	}

	public String getDate() {
		return date;
	}

	public void setHistoryDetail(boolean historyDetail) {
		this.historyDetail = historyDetail;

	}

	public boolean isHistoryDetail() {
		return historyDetail;
	}

	public void setTechnicianName(String technicianName) {
		this.technicianName = technicianName;
	}

	public String getTechnicianName() {
		return technicianName;
	}

	public void setHistoryData(List<ReportItem> mItems) {
		// TODO Auto-generated method stub
		if (mItems != null)
			this.mItems = mItems;
	}

	public List<ReportItem> getHistoryData() {
		return mItems;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public Settings getSettings() {
		return settings;
	}

	public void largeDate(String d) {
		this.todayDate = d;

	}

	public String getSortDate() {
		return todayDate;

	}

	public void setTechnicianValidation(String validationOff) {
		if (validationOff.trim().equalsIgnoreCase("true")) {
			this.validationOff = 0;// false
		} else {
			this.validationOff = 1;// true
		}
	}

	public int getTechnicianValidation() {
		return validationOff;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;

	}

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setTurnOffExpiryYear(boolean turnOffExpiryYear) {
		this.turnOffExpiryYear = turnOffExpiryYear;

	}

	public void setTurnOffAlarmType(boolean turnOffAlarmType) {
		this.turnOffAlarmType = turnOffAlarmType;
	}

	public void setTurnOffNoOfAlarms(boolean turnOffNoOfAlarms) {
		this.turnOffNoOfAlarms = turnOffNoOfAlarms;

	}

	public boolean isTurnOffExpiryYear() {
		return turnOffExpiryYear;

	}

	public boolean isTurnOffAlarmType() {
		return turnOffAlarmType;
	}

	public boolean isTurnOffNoOfAlarms() {
		return turnOffNoOfAlarms;

	}

	public void setLoginSucess(boolean b) {
		isLogin = b;
	}

	public boolean isLoginSucess() {
		return isLogin;

	}

}
