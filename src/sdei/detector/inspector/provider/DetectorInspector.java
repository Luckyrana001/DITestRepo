package sdei.detector.inspector.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class DetectorInspector {

	public static final String AUTHORITY = "sdei.detector.inspector.provider.DetectorInspector";

	// This class cannot be instantiated
	public DetectorInspector() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Property table
	 */
	public static final class PropertyTable implements BaseColumns {
	
		// This class cannot be instantiated
		private PropertyTable() {
		}

		public static final String TABLE_NAME = "property";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.detectorinspector.property";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.detectorinspector.property";
		public static final String NAME = "name";
		public static final String REPORT_TIME = "report_time";// key/time
		public static final String REPORT_DATE = "report_date";
		public static final String INSPECTION_DATE = "inspection_date";// inspectionDate
		public static final String REPORT_ID = "report_id";
		public static final String REPORT_UUID = "report_uuid";
		public static final String SYNC_STATUS = "sync_status";
		public static final String STATUS = "status";

		public static final String PROPERTY_ID = "property_id";
		public static final String UNIT_SHOP_NUMBER = "unit_shop_number";

		public static final String STREET_NUMBER = "street_number";
		public static final String STREET_NAME = "street_name";
		public static final String SUBURB = "suburb";
		public static final String POSTCODE = "postcode";
		public static final String STATE = "state";

		public static final String KEY_NUMBER = "key_number";
		public static final String NOTES = "note";
		public static final String HAS_LARGE_LADDER = "has_large_ladder";
		public static final String HAS_SEND_NOTIFICATION = "has_send_notification";

		public static final String OCCUPANT_NAME = "occupant_name";
		public static final String OCCUPANT_EMAIL = "occupant_email";
		public static final String OCCUPANT_TELEPHONE_NO = "occupant_telephone_no";
		public static final String OCCUPANT_MOBILE_NO = "occupant_mobile_no";
		public static final String OCCUPANT_BUSINESS_NO = "occupant_business_no";
		public static final String OCCUPANT_HOME_NO = "occupant_home_no";

		public static final String POSTAL_ADDRESS = "postal_address";
		public static final String POSTAL_SUBURB = "postal_suburb";
		public static final String POSTAL_POST_CODE = "postal_post_code";
		public static final String POSTAL_STATE_ID = "postal_state_id";
		public static final String POSTAL_COUNTRY = "postal_country";

		public static final String NO_OF_ALARAM = "no_of_alaram";
		public static final String REASON = "reason";

		public static final String AGENCY_NAME = "agency_name";
		public static final String AGENCY_ID = "agency_id";

		public static final String DISPLAY_RANK = "display_rank";
		public static final String START_DISPLAY_RANK = "start_display_rank";
		
		public static final String LATITUTE = "latitute";
		public static final String LONGITUTE = "longitute";   
		public static final String BOOKING_ID = "bookingId";   
		
		public static final String VALIDATION_OFF = "validationOff";   
		public static final String SERVICE_SHEET_ID = "serviceSheetId";   
		
		public static final String PREVIOUS_EXPIRY_YEAR = "previous_expiry_year";   
		public static final String PREVIOUS_NEW_EXPIRY_YEAR = "previous_new_expiry_year";   
		public static final String PREVIOUS_DETECTOR_TYPE = "previous_detector_type";   
		public static final String REPORT_COMPLETED_DATE = "report_completed_date";
		
		public static final String SEND_BROADCAST="send_broadcast";
                                                                                                                                                                                               
		public static final String DEFAULT_SORT_ORDER = "date DESC";
	}

	/**
	 * Report table
	 */
	public static final class ReportTable implements BaseColumns {
		// This class cannot be instantiated
		private ReportTable() {
		}

		public static final String TABLE_NAME = "report";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.detectorinspector.report";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.detectorinspector.report";
		public static final String NAME = "name";
		public static final String REPORT_ID = "report_id";
		public static final String CREATED_DATE = "date_created";
		public static final String MODIFIED_DATE = "date_modified";
		public static final String DEFAULT_SORT_ORDER = "date_modified DESC";
	}

	/**
	 * Report section table
	 */
	public static final class ReportSectionTable implements BaseColumns {
		// This class cannot be instantiated
		private ReportSectionTable() {
		}

		public static final String TABLE_NAME = "report_section";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.detectorinspector.report_section";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.detectorinspector.report_section";
		public static final String NAME = "name";
		public static final String REPORT_ID = "report_id";
		public static final String SECTION_ID = "section_id";
		public static final String SECTION_TYPE = "section_type";
		public static final String IS_DELETED = "is_deleted";
		public static final String PHOTO_ID = "photo_id";
		public static final String CREATED_DATE = "date_created";
		public static final String MODIFIED_DATE = "date_modified";
		public static final String DEFAULT_SORT_ORDER = "name DESC";
	}

	/**
	 * Report section item field table
	 */
	public static final class ReportPhotoTable implements BaseColumns {
		// This class cannot be instantiated
		private ReportPhotoTable() {
		}

		public static final String TABLE_NAME = "report_photo";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.detectorinspector.report_photo";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.detectorinspector.report_photo";
		public static final String REPORT_ID = "report_id";
		public static final String REPORT_UUID = "report_uuid";
		public static final String SECTION_ID = "section_id";
		public static final String PHOTO_ID = "photo_id";
		public static final String QUALITY = "quality";
		public static final String CREATED_DATE = "date_created";
		public static final String DEFAULT_SORT_ORDER = "report_id ASC";
	}

	/**
	 * Report section item field table
	 */
	public static final class ReportPhotoCommentTable implements BaseColumns {
		// This class cannot be instantiated
		private ReportPhotoCommentTable() {
		}

		public static final String TABLE_NAME = "report_photo_comment";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.detectorinspector.report_photo_comment";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.detectorinspector.report_photo_comment";
		public static final String PHOTO_ID = "photo_id";
		public static final String COMMENT_ID = "comment_id";
		public static final String X = "x";
		public static final String Y = "y";
		public static final String NAME = "name";
		public static final String VALUE = "v   alue";
		public static final String DEFAULT_SORT_ORDER = "photo_id ASC";
	}

	/**
	 * Sync table
	 */
	public static final class SyncTable implements BaseColumns {
		// This class cannot be instantiated
		private SyncTable() {
		}

		public static final String TABLE_NAME = "sync";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.acutech.sync";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.acutech.sync";
		public static final String REPORT_ID = "report_id";
		public static final String SYNC_DATE = "sync_date";
		public static final String SYNC_STATUS = "sync_status";
		public static final String DEFAULT_SORT_ORDER = "report_id ASC";
	}
	
	
	
	
	public static final class HistoryTable implements BaseColumns {
		
		// This class cannot be instantiated
		private HistoryTable() {
		}

		public static final String TABLE_NAME = "history";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.detectorinspector.history";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.detectorinspector.history";
		public static final String NAME = "name";
		public static final String REPORT_TIME = "report_time";// key/time
		public static final String REPORT_DATE = "report_date";
		public static final String INSPECTION_DATE = "inspection_date";// inspectionDate
		public static final String REPORT_ID = "report_id";
		public static final String REPORT_UUID = "report_uuid";
		public static final String SYNC_STATUS = "sync_status";
		public static final String STATUS = "status";
		public static final String PROPERTY_ID = "property_id";
		public static final String UNIT_SHOP_NUMBER = "unit_shop_number";
		public static final String STREET_NUMBER = "street_number";
		public static final String STREET_NAME = "street_name";
		public static final String SUBURB = "suburb";
		public static final String POSTCODE = "postcode";
		public static final String STATE = "state";

		public static final String KEY_NUMBER = "key_number";
		public static final String NOTES = "note";
		public static final String HAS_LARGE_LADDER = "has_large_ladder";
		public static final String HAS_SEND_NOTIFICATION = "has_send_notification";

		public static final String OCCUPANT_NAME = "occupant_name";
		public static final String OCCUPANT_EMAIL = "occupant_email";
		public static final String OCCUPANT_TELEPHONE_NO = "occupant_telephone_no";
		public static final String OCCUPANT_MOBILE_NO = "occupant_mobile_no";
		public static final String OCCUPANT_BUSINESS_NO = "occupant_business_no";
		public static final String OCCUPANT_HOME_NO = "occupant_home_no";

		public static final String POSTAL_ADDRESS = "postal_address";
		public static final String POSTAL_SUBURB = "postal_suburb";
		public static final String POSTAL_POST_CODE = "postal_post_code";
		public static final String POSTAL_STATE_ID = "postal_state_id";
		public static final String POSTAL_COUNTRY = "postal_country";

		public static final String NO_OF_ALARAM = "no_of_alaram";
		public static final String REASON = "reason";

		public static final String AGENCY_NAME = "agency_name";
		public static final String AGENCY_ID = "agency_id";

		public static final String DISPLAY_RANK = "display_rank";
		public static final String START_DISPLAY_RANK = "start_display_rank";
		
		public static final String LATITUTE = "latitute";
		public static final String LONGITUTE = "longitute";   
		public static final String BOOKING_ID = "bookingId";   
		
		public static final String VALIDATION_OFF = "validationOff";   
		public static final String SERVICE_SHEET_ID = "serviceSheetId";   
		
		public static final String PREVIOUS_EXPIRY_YEAR = "previous_expiry_year";   
		public static final String PREVIOUS_NEW_EXPIRY_YEAR = "previous_new_expiry_year";   
		public static final String PREVIOUS_DETECTOR_TYPE = "previous_detector_type";   
		public static final String REPORT_COMPLETED_DATE = "report_completed_date";
		
		public static final String SEND_BROADCAST="send_broadcast";
                                                                                                                                                                                               
		public static final String DEFAULT_SORT_ORDER = "date DESC";
	}
	
	
	
	
	
	
	

}
