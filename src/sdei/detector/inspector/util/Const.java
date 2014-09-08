package sdei.detector.inspector.util;

import java.util.ArrayList;

import android.os.Environment;

public class Const {

	public static class ACTIVITY_NAME 
	{
		public static String LOGIN_ACTIVITY            = "sdei.detector.inspector.DetectorInspector_LoginActivity";
		public static String HOME_ACTIVITY             = "sdei.detector.inspector.DetectorInspector_HomeActivity";
		public static String START_INSPECTION_ACTIVITY = "sdei.detector.inspector.home.Detector_Inspector_StartInspectionActivity";

	}

	public static class TIMER {
		public static int SHORT_TIME = 5;
		public static int LONG_TIME = 30;
	}

	public static class DETECTOR_TYPE {
		public static final ArrayList<String> mStrings = new ArrayList<String>();
		static {
			mStrings. add("Detachable");
			mStrings. add("Detachable Recharge");
			mStrings. add("Mains");
			mStrings. add("Mains Recharge");
			mStrings. add("Security");
		}
	}

	public static class IS_NEED_TO_SYNC {
		public static final int NO_NEED_TO_SYNC = 0;
		public static final int NEED_TO_SYNC = 1;
		public static final int PROGRESS_NEED_TO_SYNC = 2;
		public static final int COMPLETLY_NEED_TO_SYNC = 3;

	}

	public static class REPORT_SYNC_STATUS_DESP {
		public static final String SYNC_NOT_STARTED = "Sync has not started";
		public static final String SYNC_IN_PROGRESS = "Syncing is in progress";
		public static final String SYNC_FINISHED = "Syncing is completed";
		public static final String SYNC_FAILED = "Syncing has failed";
	}

	public static class REPORT_SYNC_STATUS_CODES {
		public static final int SYNC_NOT_STARTED = 10;
		public static final int SYNC_IN_PROGRESS = 20;
		public static final int SYNC_FINISHED = 30;
		public static final int SYNC_FAILED = 40;
		public static final int SYNC_HALF = 50;
	}

	public static class ACTIVITY_CODES          {
		public static final int EDIT_ACTIVITY = 10;
		public static final int START_CAPTURE_IMAGE = 20;
		protected static final int CROP_FROM_CAMERA = 30;
	}

	public static class EXIF_ORIENTATION_CODES  {
		public static final int ORIENTATION_ROTATE_90 = 6;
		public static final int ORIENTATION_ROTATE_180 = 3;
		public static final int ORIENTATION_ROTATE_270 = 8;
	}

	public static class GALLERY_PHOTO_SIZE      {
		public static final int PHOTO_WIDTH = 320;
		public static final int PHOTO_HEIGHT = 480;
	}

	public static class PHOTO_QUALITY_CODES     {
		public static final int LOW_QUALITY = 0;
		public static final int HIGH_QUALITY = 1;
	}

	/*
	 * Start activity request code
	 */
	public static class PHOTO_QUALITY_DESP      {
		public static final String LOW_QUALITY = "Low Quality";
		public static final String HIGH_QUALITY = "High Quality";
	}

	/*
	 * Start activity request code
	 */
	public static class PHOTO_LAUNCHER_RETURN_CODES {
		public static final int UPDATE_INSPECTION = 10;
		public static final int DELETE_PHOTO = 20;
	}

	public static class ENV_SETTINGS {

		public static final String DETECTOR_PHOTO_DIR       = Environment
				.getExternalStorageDirectory().toString() + "/detector/photo/";
		
		public static final String DETECTOR_CACHE_DIR       = Environment
				.getExternalStorageDirectory().toString() + "/detector/cache/";
		
		public static final String DETECTOR_REPORT_DIR      = Environment
				.getExternalStorageDirectory().toString() + "/detector/report/";		

		public static final String DETECTOR_THUMB_PHOTO_DIR = Environment
				.getExternalStorageDirectory().toString()+ "/detector/thumbphoto/";		

		public static final String DETECTOR_LARGE_PHOTO_DIR = Environment.getExternalStorageDirectory().toString()+ "/detector/largephoto/";

	}

	/*
	 *   Report field names
	 */
	public static class REPORT_FIELD_SETTINGS    {
		public static final String COMMENTS = "Comments";
		public static final String COMMENT_SPLIT = "#";
		public static final String YES = "Y";
		public static final String NO = "N";
	}

	/*
	 * Report photo size
	 */
	public static class REPORT_PHOTO_SIZE_VALUES {
		public static final int SMALL_BMP_WIDTH = 240;
		public static final int SMALL_BMP_HEIGHT = 180;
		public static final int LARGE_BMP_WIDTH = 480;
		public static final int LARGE_BMP_HEIGHT = 320;
		/*
		 * PHOTO_GALLERY_WIDTH and PHOTO_GALLERY_HEIGHT should match the
		 * dimension value item_gallery_photo_width and
		 * item_gallery_photo_height
		 */
		public static final int PHOTO_GALLERY_WIDTH = 240;
		public static final int PHOTO_GALLERY_HEIGHT = 180;
	}

	/*
	 * HTTP method code
	 */
	public static class HTTP_METHOD_CODES {
		public static final int HTTP_GET = 10;
		public static final int HTTP_PUT = 20;
		public static final int HTTP_POST = 30;
		public static final int HTTP_DELETE = 40;
	}

	/*
	 * HTTP response status code
	 */
	public static class HTTP_STATUS_CODES {
		public static final int NONE = 0;
		public static final int SOCKET_ERROR = 1;
		public static final int OK = 201;
		public static final int BAD_REQUEST = 400;
		public static final int FORBIDDEN = 403;
		public static final int INTERNAL_SERVER_ERROR = 500;
		public static final int SERVICE_UNAVAILABLE = 503;
	}

	/*
	 *    Sync action string
	 */
	public static class WS_RESPONSE_ACTIONS {
		public static final String WS_NETWORK_ERROR = "sdei.detector.inspector.sync.service.WS_NETWORK_ERROR";
		public static final String WS_NETWORK_BOOKING_ERROR = "sdei.detector.inspector.sync.service.WS_NETWORK_BOOKING_ERROR";
		public static final String WS_SYNC_SUCCESS = "sdei.detector.inspector.sync.service.WS_SYNC_SUCCESS";
		public static final String WS_SYNC_FAIL = "sdei.detector.inspector.sync.service.WS_SYNC_FAIL";
		public static final String WS_SYNC_IN_PROGRESS = "sdei.detector.inspector.sync.service.WS_SYNC_IN_PROGRESS";
		public static final String WS_SYNC_FINISHED = "sdei.detector.inspector.sync.service.WS_SYNC_FINISHED";
		public static final String WS_SYNC_TASK_CANCELLED = "sdei.detector.inspector.sync.service.WS_SYNC_TASK_CANCELLED";
		public static final String WS_SYNC_TASK_GROUP_CANCELLED = "sdei.detector.inspector.sync.service.WS_SYNC_TASK_GROUP_CANCELLED";
		public static final String WS_ADD_REPORT_SUCCESS = "sdei.detector.inspector.sync.service.WS_ADD_REPORT_SUCCESS";
		public static final String WS_UPLOAD_PHOTO_SUCCESS = "sdei.detector.inspector.sync.service.WS_UPLOAD_PHOTO_SUCCESS";
		public static final String WS_SYNCH_NETWORK_ERROR = "sdei.detector.inspector.sync.service.WS_SYNCH_NETWORK_ERROR";
		public static final String WS_LOAD_TRACK_LOCATION_SUCESS = "sdei.detector.inspector.sync.service.WS_LOAD_TRACK_LOCATION_SUCESS";
		public static final String WS_LOAD_TRACK_LOCATION_SERVER_SUCESS = "sdei.detector.inspector.sync.service.WS_LOAD_TRACK_LOCATION_SERVER_SUCESS";
		public static String       WS_LOGIN_SUCCESS = "sdei.detector.inspector.sync.service.WS_LOGIN_SUCCESS";
		public static String       WS_FORGOT_PASS_SUCCESS = "sdei.detector.inspector.sync.service.WS_FORGOT_PASS_SUCCESS";
		public static String       WS_LOAD_PROPERTY_SUCCESS = "sdei.detector.inspector.sync.service.WS_LOAD_PROPERTY_SUCCESS";
		public static String       WS_LOAD_PROPERTY_BOOKING_SUCCESS = "sdei.detector.inspector.sync.service.WS_LOAD_PROPERTY_BOOKING_SUCCESS";
		public static String       WS_LOAD_PROPERTY_HISTORY_SUCCESS = "sdei.detector.inspector.sync.service.WS_LOAD_PROPERTY_HISTORY_SUCCESS";
	}

	/*
	 * WS request type
	 */
	public static class WS_REQUEST_TYPES {

		public static final int LOGIN = 1;
		public static final int RESET_PASS = 2;
		public static final int FORGOT_PASS = 3;
		public static final int LOAD_PROPERTY = 4;
		public static final int ADD_REPORT = 5;
		public static final int UPLOAD_PHOTO = 6;
		public static final int UN_ALLOCATED = 7;
		public static final int sendmailNotification = 8;
		public static final int SAVE_LOCATION = 9;
		public static final int LOAD_PROPERTY_HISTORY = 10;
	}

	/*
	 * WS request urls
	 */
	public static class WS_URLS {

		//public static String WS_SERVICE_BASE_URL = "http://android.detectorinspector.com.au/";//"http://108.168.203.227/DetectorService/";//"http://androidtest.detectorinspector.com.au/";//"http://androidtest.detectorinspector.com.au/DetectorService/";//// //"http://androidtas.detectorinspector.com.au/";//

		//new test url
		
		public static String WS_SERVICE_BASE_URL ="http://androidtest.detectorinspector.com.au/";
		//Local url 
		//public static String WS_SERVICE_BASE_URL ="http://108.168.203.227/DetectorService/";
		
		// LIVE_URL =
	    // public static String WS_SERVICE_BASE_URL = "http://android.detectorinspector.com.au/";
				
				
	//public static String WS_SERVICE_BASE_URL = "http://androidtas.detectorinspector.com.au/";

		// public static String WS_SERVICE_SYNCH_REPORT_URL =
		// "http://localhost:51683/TechnicianSync.svc/DedectionComplite";

		public static String WS_SERVICE_BASE_LOGIN_URL        =      WS_SERVICE_BASE_URL + "login.svc/ValidateTechnician";

		public static String WS_SERVICE_BASE_RESET_URL        =      WS_SERVICE_BASE_URL + "ForgotPassword.svc/ForgetPassword";

		public static String WS_SERVICE_BASE_BOOKING_URL      =      WS_SERVICE_BASE_URL + "Booking.svc/BookingDetails";

		public static String WS_SERVICE_BASE_SYNCH_REPORT_URL =  WS_SERVICE_BASE_URL+ "TechnicianSync.svc/DedectionComplite";

		public static String WS_SERVICE_UNALLOCATED_URL       =  WS_SERVICE_BASE_URL
				+ "Booking.svc/UnAllowcateBooking";

		public static String WS_SERVICE_SEND_EMAIL_NOTIFICATION_URL = WS_SERVICE_BASE_URL + "ForgotPassword.svc/SendNotification";

		public static String WS_SAVE_LOCATION_URL = WS_SERVICE_BASE_URL
				+ "TechnicianSync.svc/SaveLocation";

		public static String WS_UPLOAD_SERVICE_BASE_URL = WS_SERVICE_BASE_URL
				+ "ImageUpload.ashx?guid=";

		public static String WS_SERVICE_BASE_HISTORY_URL = WS_SERVICE_BASE_URL
				+ "Booking.svc/LastBookingDetails";

	}

	/*
	 *   Sync status code
	 */
	public static class BIND_SERVICE_STATUS_CODES  {
		public static final int BIND_SERVICE_SUCCESS = 0;
		public static final int BIND_SERVICE_FAIL = 1;
	}

	/*
	 *   Inspection status code
	 */
	public static class PROPERTY_STATUS_CODES      
	{
		public static final int ALL        = -1;
		public static final int PENDING    = 1;
		public static final int INPROGRESS = 2;
		public static final int COMPLETED  = 3;

	}

	public static final boolean DEBUG = false;

}


