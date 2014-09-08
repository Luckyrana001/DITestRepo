package sdei.detector.inspector.sync;

import java.util.HashMap;
import java.util.List;

import sdei.detector.inspector.util.Const;

public class SyncReport {

	public static int cccc;

	private int     mInspectionId;
	private String  mReportUUID;
	private String  mSuburb;
	private String  mStreet;
	private int     mSyncStatus;       // values in REPORT_SYNC_STATUS_CODES
	private List<SyncPhoto> mPhotos;
	private int     mUploadedPhotoCount;
	private boolean mNeedToSync;
	private boolean mReportAdded;
	private String  mSyncTaskGroupId;
	private String  mSyncTaskId;
	private int     mStatus;
	private String  mReportId;
	private int     inspectionTypeID;
	private String  completeDateString;
	private String  localCompleteDateString;
	private int     mUpdateStatus;
	private String  mServiceSheetId;
	private String  bookingId;
	private String  inspectionId;
	private String  lat;
	private String  lng;

	private HashMap<String, Integer> yPhotoCountSize = new HashMap<String, Integer>();

	public SyncReport(
			Integer id, 
			String report_id, 
			String r_uuid, 
			String st,
			int s_status, 
			int status, 
			String Suburb,
			List<SyncPhoto> photo_list, 
			int inspectionTypeId,
			String completeDate, 
			String localcompleteDate, int i) {
		mReportId = report_id;
		mSuburb = Suburb;
		mReportUUID = r_uuid;
		mStreet = st;
		mSyncStatus = s_status;
		mStatus = status;
		mPhotos = photo_list;
		mUploadedPhotoCount = 0;
		mNeedToSync = (i == 1) ? true : false;
		mReportAdded = false;
		mSyncTaskGroupId = "";
		mSyncTaskId = "";
		inspectionTypeID   = inspectionTypeId;
		completeDateString = completeDate;
		localCompleteDateString = localcompleteDate;
		mInspectionId = id;
		mUpdateStatus = i;
	}

	public SyncReport(Integer id, String report_id, String r_uuid, String st,
			int s_status, int status, String Suburb, List<SyncPhoto> photo_list) {
		mReportId = report_id;
		mSuburb = Suburb;
		mReportUUID = r_uuid;
		mStreet = st;
		mSyncStatus = s_status;
		mStatus = status;
		mPhotos = photo_list;
		mUploadedPhotoCount = 0;
		mNeedToSync = true;
	}

	public SyncReport(String propertyId, String bookingId, String reportId,
			String report_uuid, String address, int sync_status, int status,
			List<SyncPhoto> photoListByReportUUID, String Suburb,
			List<String> list,String mServiceSheetId) {
		// TODO Auto-generated constructor stub
		this.inspectionId = propertyId;
		this.bookingId = bookingId;
		mReportId = reportId;
		mSuburb = Suburb;
		mReportUUID = report_uuid;
		mStreet = address;
		mSyncStatus = sync_status;
		mStatus = status;
		mPhotos = photoListByReportUUID;
		mUploadedPhotoCount = 0;
		
		if(sync_status== Const.REPORT_SYNC_STATUS_CODES.SYNC_FINISHED){
			mNeedToSync = false;
			
		}else{
			mNeedToSync = true;
				
		}
		
		mReportAdded = false;
		mSyncTaskGroupId = "";
		mSyncTaskId = "";

		lat = list.get(0).toString();
		lng = list.get(1).toString();
		this.mServiceSheetId=mServiceSheetId;

	}

	public void setSuburb(String suburb) {
		mSuburb = suburb;
	}

	public String getSuburb() {
		return mSuburb;
	}

	public Integer getUpdateStatus() {
		return mUpdateStatus;
	}

	public void setUpdateStatus(Integer id) {
		this.mUpdateStatus = id;
	}

	public Integer getId() {
		return mInspectionId;
	}

	public void setId(Integer id) {
		this.mInspectionId = id;
	}

	public String getReportId() {
		return mReportId;
	}

	public void setReportId(String mReportId) {
		this.mReportId = mReportId;
	}

	public int getStatus() {
		return mStatus;
	}

	public String getcompleteDateString() {
		return completeDateString;
	}

	public void setcompleteDateString(String in) {
		this.completeDateString = in;
	}

	public String getlocalCompleteDateString() {
		return localCompleteDateString;
	}

	public void setlocalCompleteDateString(String in) {
		this.localCompleteDateString = in;
	}

	public int getinspectionTypeID() {
		return inspectionTypeID;
	}

	public void setinspectionTypeID(int in) {
		this.inspectionTypeID = in;
	}

	public void setSyncTaskGroupId(String sync_group_id) {
		mSyncTaskGroupId = sync_group_id;
	}

	public void setSyncTaskId(String sync_id) {
		mSyncTaskId = sync_id;
	}

	public boolean isReportAdded() {
		return mReportAdded;
	}

	public void setReportAdded(boolean b) {
		mReportAdded = b;
	}

	public boolean isNeedToSync() {
		return mNeedToSync;
	}

	public void setNeedToSync(boolean b) {
		mNeedToSync = b;
	}

	public String getStreet() {
		return mStreet;
	}

	public int getSyncStatus() {
		return mSyncStatus;
	}

	public void setSyncStatus(int i) {
		mSyncStatus = i;
	}

	public List<SyncPhoto> getPhotos() {
		return mPhotos;
	}

	public int getUploadedPhotoCount() {
		return mUploadedPhotoCount;

	}

	public void setUploadedPhotoCount(int i) {
		mUploadedPhotoCount = i;
	}

	public String getReportUUID() {
		return mReportUUID;
	}

	public void increaseUploadPhotoCount() {
		mUploadedPhotoCount++;

	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getInspectionId() {
		return inspectionId;
	}

	public void setInspectionId(String inspectionId) {
		this.inspectionId = inspectionId;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getmServiceSheetId() {
		return mServiceSheetId;
	}

	public void setmServiceSheetId(String mServiceSheetId) {
		this.mServiceSheetId = mServiceSheetId;
	}

}
