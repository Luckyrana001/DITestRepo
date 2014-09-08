package sdei.detector.inspector.provider;

import java.util.HashMap;

import sdei.detector.inspector.provider.DetectorInspector.HistoryTable;
import sdei.detector.inspector.provider.DetectorInspector.PropertyTable;
import sdei.detector.inspector.provider.DetectorInspector.ReportPhotoCommentTable;
import sdei.detector.inspector.provider.DetectorInspector.ReportPhotoTable;
import sdei.detector.inspector.provider.DetectorInspector.ReportSectionTable;
import sdei.detector.inspector.provider.DetectorInspector.ReportTable;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.detector.inspector.lib.util.Const;

public class DetectorInspectorProvider extends ContentProvider {

	private static final String TAG = "TEST";

	private static final String DATABASE_NAME = "detectorinspector.db";
	private static final int DATABASE_VERSION = 1;

	private static final String PROPERTY_TABLE_NAME = PropertyTable.TABLE_NAME;
	private static final String REPORT_TABLE_NAME = ReportTable.TABLE_NAME;
	private static final String REPORT_SECTION_TABLE_NAME = ReportSectionTable.TABLE_NAME;
	private static final String REPORT_PHOTO_COMMENT_TABLE_NAME = ReportPhotoCommentTable.TABLE_NAME;
	private static final String REPORT_PHOTO_TABLE_NAME = ReportPhotoTable.TABLE_NAME;

	private static final String HISTORY_TABLE_NAME = HistoryTable.TABLE_NAME;

	private static HashMap<String, String> sPropertyProjectionMap;
	private static HashMap<String, String> sReportProjectionMap;
	private static HashMap<String, String> sReportSectionProjectionMap;
	private static HashMap<String, String> sReportPhotoCommentProjectionMap;
	private static HashMap<String, String> sReportPhotoProjectionMap;
	private static HashMap<String, String> sHistoryProjectionMap;

	private static final int PROPERTIES = 1;
	private static final int PROPERTY_ID = 2;
	private static final int REPORTS = 3;
	private static final int REPORT_ID = 4;
	private static final int REPORT_SECTIONS = 5;
	private static final int REPORT_SECTION_ID = 6;
	private static final int REPORT_PHOTOS = 7;
	private static final int REPORT_PHOTO_ID = 8;
	private static final int REPORT_PHOTO_COMMENTS = 9;
	private static final int REPORT_PHOTO_COMMENT_ID = 10;

	private static final int HISTORY = 11;
	private static final int HISTORY_ID = 12;

	private static final UriMatcher sUriMatcher;

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Log.d(TAG, "create db");
			db.execSQL("CREATE TABLE " + PROPERTY_TABLE_NAME + " ("
					+ PropertyTable._ID + " INTEGER PRIMARY KEY,"
					+ PropertyTable.NAME + " TEXT," + PropertyTable.REPORT_ID
					+ " Text," + PropertyTable.REPORT_UUID + " TEXT,"
					+ PropertyTable.PROPERTY_ID + " TEXT,"
					+ PropertyTable.UNIT_SHOP_NUMBER + " Text,"
					+ PropertyTable.REPORT_TIME + " TEXT,"
					+ PropertyTable.REPORT_DATE + " TEXT,"
					+ PropertyTable.SYNC_STATUS + " INTEGER DEFAULT "
					+ Const.REPORT_SYNC_STATUS_CODES.SYNC_NOT_STARTED + ","
					+ PropertyTable.STATUS + " INTEGER,"
					+ PropertyTable.STREET_NUMBER + " TEXT,"
					+ PropertyTable.STREET_NAME + " TEXT,"
					+ PropertyTable.SUBURB + " TEXT," + PropertyTable.POSTCODE
					+ " TEXT," + PropertyTable.STATE + " TEXT,"
					+ PropertyTable.KEY_NUMBER + " TEXT," + PropertyTable.NOTES
					+ " TEXT," + PropertyTable.HAS_LARGE_LADDER + " TEXT,"
					+ PropertyTable.HAS_SEND_NOTIFICATION + " TEXT,"
					+ PropertyTable.OCCUPANT_NAME + " TEXT,"
					+ PropertyTable.OCCUPANT_EMAIL + " TEXT,"
					+ PropertyTable.OCCUPANT_TELEPHONE_NO + " TEXT,"
					+ PropertyTable.OCCUPANT_MOBILE_NO + " TEXT,"
					+ PropertyTable.OCCUPANT_BUSINESS_NO + " TEXT,"
					+ PropertyTable.OCCUPANT_HOME_NO + " TEXT,"
					+ PropertyTable.POSTAL_ADDRESS + " TEXT,"
					+ PropertyTable.POSTAL_SUBURB + " TEXT,"
					+ PropertyTable.POSTAL_POST_CODE + " TEXT,"
					+ PropertyTable.POSTAL_STATE_ID + " TEXT,"
					+ PropertyTable.POSTAL_COUNTRY + " TEXT,"
					+ PropertyTable.AGENCY_NAME + " TEXT,"
					+ PropertyTable.AGENCY_ID + " Text,"
					+ PropertyTable.NO_OF_ALARAM + " INTEGER,"
					+ PropertyTable.REASON + " TEXT,"
					+ PropertyTable.DISPLAY_RANK + " INTEGER,"
					+ PropertyTable.START_DISPLAY_RANK + " INTEGER,"
					+ PropertyTable.LATITUTE + " TEXT,"
					+ PropertyTable.LONGITUTE + " TEXT,"
					+ PropertyTable.BOOKING_ID + " TEXT,"
					+ PropertyTable.INSPECTION_DATE + " TEXT,"
					+ PropertyTable.PREVIOUS_EXPIRY_YEAR + " TEXT,"
					+ PropertyTable.PREVIOUS_NEW_EXPIRY_YEAR + " TEXT,"
					+ PropertyTable.PREVIOUS_DETECTOR_TYPE + " TEXT,"
					+ PropertyTable.REPORT_COMPLETED_DATE + " TEXT,"
					+ PropertyTable.SERVICE_SHEET_ID + " TEXT,"
					+ PropertyTable.VALIDATION_OFF + " INTEGER,"
					+ PropertyTable.SEND_BROADCAST + " INTEGER" + ");");

			db.execSQL("CREATE TABLE " + REPORT_TABLE_NAME + " ("
					+ ReportTable._ID + " INTEGER PRIMARY KEY,"
					+ ReportTable.NAME + " TEXT," + ReportTable.REPORT_ID
					+ " TEXT," + ReportTable.CREATED_DATE + " TEXT,"
					+ ReportTable.MODIFIED_DATE + " TEXT" + ");");

			db.execSQL("CREATE TABLE " + REPORT_SECTION_TABLE_NAME + " ("
					+ ReportSectionTable._ID + " INTEGER PRIMARY KEY,"
					+ ReportSectionTable.NAME + " TEXT,"
					+ ReportSectionTable.REPORT_ID + " TEXT,"
					+ ReportSectionTable.SECTION_ID + " INTEGER,"
					+ ReportSectionTable.SECTION_TYPE + " TEXT,"
					+ ReportSectionTable.IS_DELETED + " BOOLEAN,"
					+ ReportSectionTable.PHOTO_ID + " TEXT,"
					+ ReportSectionTable.CREATED_DATE + " TEXT,"
					+ ReportSectionTable.MODIFIED_DATE + " TEXT" + ");");

			db.execSQL("CREATE TABLE " + REPORT_PHOTO_COMMENT_TABLE_NAME + " ("
					+ ReportPhotoCommentTable._ID + " INTEGER PRIMARY KEY,"
					+ ReportPhotoCommentTable.NAME + " INTEGER,"
					+ ReportPhotoCommentTable.PHOTO_ID + " TEXT,"
					+ ReportPhotoCommentTable.COMMENT_ID + " TEXT,"
					+ ReportPhotoCommentTable.X + " INTEGER,"
					+ ReportPhotoCommentTable.Y + " INTEGER,"
					+ ReportPhotoCommentTable.VALUE + " TEXT" + ");");

			db.execSQL("CREATE TABLE " + REPORT_PHOTO_TABLE_NAME + " ("
					+ ReportPhotoTable._ID + " INTEGER PRIMARY KEY,"
					+ ReportPhotoTable.REPORT_ID + " TEXT,"
					+ ReportPhotoTable.REPORT_UUID + " TEXT,"
					+ ReportPhotoTable.SECTION_ID + " INTEGER,"
					+ ReportPhotoTable.QUALITY + " INTEGER,"
					+ ReportPhotoTable.CREATED_DATE + " TEXT,"
					+ ReportPhotoTable.PHOTO_ID + " TEXT" + ");");

			db.execSQL("CREATE TABLE " + HISTORY_TABLE_NAME + " ("
					+ HistoryTable._ID + " INTEGER PRIMARY KEY,"
					+ HistoryTable.NAME + " TEXT," + HistoryTable.REPORT_ID
					+ " Text," + HistoryTable.REPORT_UUID + " TEXT,"
					+ HistoryTable.PROPERTY_ID + " TEXT,"
					+ HistoryTable.UNIT_SHOP_NUMBER + " Text,"
					+ HistoryTable.REPORT_TIME + " TEXT,"
					+ HistoryTable.REPORT_DATE + " TEXT,"
					+ HistoryTable.SYNC_STATUS + " INTEGER DEFAULT "
					+ Const.REPORT_SYNC_STATUS_CODES.SYNC_NOT_STARTED + ","
					+ HistoryTable.STATUS + " INTEGER,"
					+ HistoryTable.STREET_NUMBER + " TEXT,"
					+ HistoryTable.STREET_NAME + " TEXT," + HistoryTable.SUBURB
					+ " TEXT," + HistoryTable.POSTCODE + " TEXT,"
					+ HistoryTable.STATE + " TEXT," + HistoryTable.KEY_NUMBER
					+ " TEXT," + HistoryTable.NOTES + " TEXT,"
					+ HistoryTable.HAS_LARGE_LADDER + " TEXT,"
					+ HistoryTable.HAS_SEND_NOTIFICATION + " TEXT,"
					+ HistoryTable.OCCUPANT_NAME + " TEXT,"
					+ HistoryTable.OCCUPANT_EMAIL + " TEXT,"
					+ HistoryTable.OCCUPANT_TELEPHONE_NO + " TEXT,"
					+ HistoryTable.OCCUPANT_MOBILE_NO + " TEXT,"
					+ HistoryTable.OCCUPANT_BUSINESS_NO + " TEXT,"
					+ HistoryTable.OCCUPANT_HOME_NO + " TEXT,"
					+ HistoryTable.POSTAL_ADDRESS + " TEXT,"
					+ HistoryTable.POSTAL_SUBURB + " TEXT,"
					+ HistoryTable.POSTAL_POST_CODE + " TEXT,"
					+ HistoryTable.POSTAL_STATE_ID + " TEXT,"
					+ HistoryTable.POSTAL_COUNTRY + " TEXT,"
					+ HistoryTable.AGENCY_NAME + " TEXT,"
					+ HistoryTable.AGENCY_ID + " Text,"
					+ HistoryTable.NO_OF_ALARAM + " INTEGER,"
					+ HistoryTable.REASON + " TEXT,"
					+ HistoryTable.DISPLAY_RANK + " INTEGER,"
					+ HistoryTable.START_DISPLAY_RANK + " INTEGER,"
					+ HistoryTable.LATITUTE + " TEXT," + HistoryTable.LONGITUTE
					+ " TEXT," + HistoryTable.BOOKING_ID + " TEXT,"
					+ HistoryTable.INSPECTION_DATE + " TEXT"

					+ ");");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS " + PropertyTable.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + ReportTable.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + ReportSectionTable.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "
					+ ReportPhotoCommentTable.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + ReportPhotoTable.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + HistoryTable.TABLE_NAME);
			
			onCreate(db);
		}
	}

	private DatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case PROPERTIES:
			qb.setTables(PROPERTY_TABLE_NAME);
			qb.setProjectionMap(sPropertyProjectionMap);
			break;

		case PROPERTY_ID:
			qb.setTables(PROPERTY_TABLE_NAME);
			qb.setProjectionMap(sPropertyProjectionMap);
			qb.appendWhere(PropertyTable._ID + "="
					+ uri.getPathSegments().get(1));
			break;

		case REPORTS:
			qb.setTables(REPORT_TABLE_NAME);
			qb.setProjectionMap(sReportProjectionMap);
			break;

		case REPORT_ID:
			qb.setTables(REPORT_TABLE_NAME);
			qb.setProjectionMap(sReportProjectionMap);
			qb.appendWhere(ReportTable._ID + "=" + uri.getPathSegments().get(1));
			break;

		case REPORT_SECTIONS:
			qb.setTables(REPORT_SECTION_TABLE_NAME);
			qb.setProjectionMap(sReportSectionProjectionMap);
			break;

		case REPORT_SECTION_ID:
			qb.setTables(REPORT_SECTION_TABLE_NAME);
			qb.setProjectionMap(sReportSectionProjectionMap);
			qb.appendWhere(ReportSectionTable._ID + "="
					+ uri.getPathSegments().get(1));
			break;

		case REPORT_PHOTO_COMMENTS:
			qb.setTables(REPORT_PHOTO_COMMENT_TABLE_NAME);
			qb.setProjectionMap(sReportPhotoCommentProjectionMap);
			break;

		case REPORT_PHOTO_COMMENT_ID:
			qb.setTables(REPORT_PHOTO_COMMENT_TABLE_NAME);
			qb.setProjectionMap(sReportPhotoCommentProjectionMap);
			qb.appendWhere(ReportPhotoCommentTable._ID + "="
					+ uri.getPathSegments().get(1));
			break;

		case REPORT_PHOTOS:
			qb.setTables(REPORT_PHOTO_TABLE_NAME);
			qb.setProjectionMap(sReportPhotoProjectionMap);
			break;

		case REPORT_PHOTO_ID:
			qb.setTables(REPORT_PHOTO_TABLE_NAME);
			qb.setProjectionMap(sReportPhotoProjectionMap);
			qb.appendWhere(ReportPhotoTable._ID + "="
					+ uri.getPathSegments().get(1));
			break;
		

		case HISTORY:
			qb.setTables(HISTORY_TABLE_NAME);
			qb.setProjectionMap(sHistoryProjectionMap);
			break;

		case HISTORY_ID:
			qb.setTables(HISTORY_TABLE_NAME);
			qb.setProjectionMap(sHistoryProjectionMap);
			qb.appendWhere(HistoryTable._ID + "="
					+ uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			switch (sUriMatcher.match(uri)) {
			case PROPERTIES:
			case PROPERTY_ID:
				orderBy = DetectorInspector.PropertyTable.DEFAULT_SORT_ORDER;
				break;
			case REPORTS:
			case REPORT_ID:
				orderBy = DetectorInspector.ReportTable.DEFAULT_SORT_ORDER;
				break;
			case REPORT_SECTIONS:
			case REPORT_SECTION_ID:
				orderBy = DetectorInspector.ReportSectionTable.DEFAULT_SORT_ORDER;
				break;

			case REPORT_PHOTO_COMMENTS:
			case REPORT_PHOTO_COMMENT_ID:
				orderBy = DetectorInspector.ReportPhotoCommentTable.DEFAULT_SORT_ORDER;
				break;
			case REPORT_PHOTOS:
			case REPORT_PHOTO_ID:
				orderBy = DetectorInspector.ReportPhotoTable.DEFAULT_SORT_ORDER;
				break;
				
		
				

			case HISTORY:
			case HISTORY_ID:
				orderBy = DetectorInspector.HistoryTable.DEFAULT_SORT_ORDER;
				break;

			default:
				orderBy = BaseColumns._ID;
				break;
			}
		} else {
			orderBy = sortOrder;
		}

		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;

	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case PROPERTIES:
			return PropertyTable.CONTENT_TYPE;
		case PROPERTY_ID:
			return PropertyTable.CONTENT_ITEM_TYPE;

		case REPORTS:
			return ReportTable.CONTENT_TYPE;
		case REPORT_ID:
			return ReportTable.CONTENT_ITEM_TYPE;
		case REPORT_SECTIONS:
			return ReportSectionTable.CONTENT_TYPE;
		case REPORT_SECTION_ID:
			return ReportSectionTable.CONTENT_ITEM_TYPE;

		case REPORT_PHOTO_COMMENTS:
			return ReportPhotoCommentTable.CONTENT_TYPE;
		case REPORT_PHOTO_COMMENT_ID:
			return ReportPhotoCommentTable.CONTENT_ITEM_TYPE;
		case REPORT_PHOTOS:
			return ReportPhotoTable.CONTENT_TYPE;
		case REPORT_PHOTO_ID:
			return ReportPhotoTable.CONTENT_ITEM_TYPE;
			
			
	
			
			

		case HISTORY:
			return HistoryTable.CONTENT_TYPE;
		case HISTORY_ID:
			return HistoryTable.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		if (sUriMatcher.match(uri) != PROPERTIES
				&& sUriMatcher.match(uri) != HISTORY) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case PROPERTIES:
			db.beginTransaction();
			try {

				String sql = "insert into "
						+ DetectorInspector.PropertyTable.TABLE_NAME
						+ " ("
						+ DetectorInspector.PropertyTable.REPORT_ID
						+ ","
						+ DetectorInspector.PropertyTable.REPORT_UUID
						+ ","
						+ DetectorInspector.PropertyTable.PROPERTY_ID
						+ ","
						+ DetectorInspector.PropertyTable.UNIT_SHOP_NUMBER
						+ ","
						+ DetectorInspector.PropertyTable.REPORT_TIME
						+ ","
						+ DetectorInspector.PropertyTable.REPORT_DATE
						+ ","
						+ DetectorInspector.PropertyTable.SYNC_STATUS
						+ ","
						+ DetectorInspector.PropertyTable.STATUS
						+ ","
						+ DetectorInspector.PropertyTable.STREET_NUMBER
						+ ","
						+ DetectorInspector.PropertyTable.STREET_NAME
						+ ","
						+ DetectorInspector.PropertyTable.SUBURB
						+ ","
						+ DetectorInspector.PropertyTable.POSTCODE
						+ ","
						+ DetectorInspector.PropertyTable.STATE
						+ ","
						+ DetectorInspector.PropertyTable.KEY_NUMBER
						+ ","
						+ DetectorInspector.PropertyTable.NOTES
						+ ","
						+ DetectorInspector.PropertyTable.HAS_LARGE_LADDER
						+ ","
						+ DetectorInspector.PropertyTable.HAS_SEND_NOTIFICATION
						+ ","
						+ DetectorInspector.PropertyTable.OCCUPANT_NAME
						+ ","
						+ DetectorInspector.PropertyTable.OCCUPANT_EMAIL
						+ ","
						+ DetectorInspector.PropertyTable.OCCUPANT_TELEPHONE_NO
						+ ","
						+ DetectorInspector.PropertyTable.OCCUPANT_MOBILE_NO
						+ ","
						+ DetectorInspector.PropertyTable.OCCUPANT_BUSINESS_NO
						+ ","
						+ DetectorInspector.PropertyTable.OCCUPANT_HOME_NO
						+ ","
						+ DetectorInspector.PropertyTable.POSTAL_ADDRESS
						+ ","
						+ DetectorInspector.PropertyTable.POSTAL_SUBURB
						+ ","
						+ DetectorInspector.PropertyTable.POSTAL_POST_CODE
						+ ","
						+ DetectorInspector.PropertyTable.POSTAL_STATE_ID
						+ ","
						+ DetectorInspector.PropertyTable.POSTAL_COUNTRY
						+ ","
						+ DetectorInspector.PropertyTable.AGENCY_NAME
						+ ","
						+ DetectorInspector.PropertyTable.AGENCY_ID
						+ ","
						+ DetectorInspector.PropertyTable.NO_OF_ALARAM
						+ ","
						+ DetectorInspector.PropertyTable.REASON
						+ ","
						+ DetectorInspector.PropertyTable.DISPLAY_RANK
						+ ","
						+ DetectorInspector.PropertyTable.START_DISPLAY_RANK
						+ ","
						+ DetectorInspector.PropertyTable.LATITUTE
						+ ","
						+ DetectorInspector.PropertyTable.LONGITUTE
						+ ","
						+ DetectorInspector.PropertyTable.BOOKING_ID
						+ ","
						+ DetectorInspector.PropertyTable.INSPECTION_DATE
						+ ","
						+ DetectorInspector.PropertyTable.PREVIOUS_EXPIRY_YEAR
						+ ","
						+ DetectorInspector.PropertyTable.PREVIOUS_NEW_EXPIRY_YEAR
						+ ","
						+ DetectorInspector.PropertyTable.PREVIOUS_DETECTOR_TYPE
						+ ","
						+ DetectorInspector.PropertyTable.REPORT_COMPLETED_DATE
						+ ","
						+ DetectorInspector.PropertyTable.SERVICE_SHEET_ID
						+ ","
						+ DetectorInspector.PropertyTable.VALIDATION_OFF
						+ ","
						+ DetectorInspector.PropertyTable.SEND_BROADCAST
						+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

				SQLiteStatement insert = db.compileStatement(sql);

				for (ContentValues value : values) {

					insert.bindString(
							1,
							value.getAsString(DetectorInspector.PropertyTable.REPORT_ID));
					insert.bindString(
							2,
							value.getAsString(DetectorInspector.PropertyTable.REPORT_UUID));
					insert.bindString(
							3,
							value.getAsString(DetectorInspector.PropertyTable.PROPERTY_ID));
					insert.bindString(
							4,
							value.getAsString(DetectorInspector.PropertyTable.UNIT_SHOP_NUMBER));
					insert.bindString(
							5,
							value.getAsString(DetectorInspector.PropertyTable.REPORT_TIME));
					insert.bindString(
							6,
							value.getAsString(DetectorInspector.PropertyTable.REPORT_DATE));
					insert.bindLong(
							7,
							value.getAsLong(DetectorInspector.PropertyTable.SYNC_STATUS));
					insert.bindLong(8, value
							.getAsLong(DetectorInspector.PropertyTable.STATUS));
					insert.bindString(
							9,
							value.getAsString(DetectorInspector.PropertyTable.STREET_NUMBER));
					insert.bindString(
							10,
							value.getAsString(DetectorInspector.PropertyTable.STREET_NAME));
					insert.bindString(
							11,
							value.getAsString(DetectorInspector.PropertyTable.SUBURB));
					insert.bindString(
							12,
							value.getAsString(DetectorInspector.PropertyTable.POSTCODE));
					insert.bindString(13, value
							.getAsString(DetectorInspector.PropertyTable.STATE));
					insert.bindString(
							14,
							value.getAsString(DetectorInspector.PropertyTable.KEY_NUMBER));
					insert.bindString(15, value
							.getAsString(DetectorInspector.PropertyTable.NOTES));
					insert.bindString(
							16,
							value.getAsString(DetectorInspector.PropertyTable.HAS_LARGE_LADDER));
					insert.bindString(
							17,
							value.getAsString(DetectorInspector.PropertyTable.HAS_SEND_NOTIFICATION));
					insert.bindString(
							18,
							value.getAsString(DetectorInspector.PropertyTable.OCCUPANT_NAME));
					insert.bindString(
							19,
							value.getAsString(DetectorInspector.PropertyTable.OCCUPANT_EMAIL));
					insert.bindString(
							20,
							value.getAsString(DetectorInspector.PropertyTable.OCCUPANT_TELEPHONE_NO));
					insert.bindString(
							21,
							value.getAsString(DetectorInspector.PropertyTable.OCCUPANT_MOBILE_NO));
					insert.bindString(
							22,
							value.getAsString(DetectorInspector.PropertyTable.OCCUPANT_BUSINESS_NO));
					insert.bindString(
							23,
							value.getAsString(DetectorInspector.PropertyTable.OCCUPANT_HOME_NO));

					insert.bindString(
							24,
							value.getAsString(DetectorInspector.PropertyTable.POSTAL_ADDRESS));
					insert.bindString(
							25,
							value.getAsString(DetectorInspector.PropertyTable.POSTAL_SUBURB));
					insert.bindString(
							26,
							value.getAsString(DetectorInspector.PropertyTable.POSTAL_POST_CODE));
					insert.bindString(
							27,
							value.getAsString(DetectorInspector.PropertyTable.POSTAL_STATE_ID));
					insert.bindString(
							28,
							value.getAsString(DetectorInspector.PropertyTable.POSTAL_COUNTRY));
					insert.bindString(
							29,
							value.getAsString(DetectorInspector.PropertyTable.AGENCY_NAME));
					insert.bindString(
							30,
							value.getAsString(DetectorInspector.PropertyTable.AGENCY_ID));
					insert.bindLong(
							31,
							value.getAsLong(DetectorInspector.PropertyTable.NO_OF_ALARAM));
					insert.bindString(
							32,
							value.getAsString(DetectorInspector.PropertyTable.REASON));

					insert.bindLong(
							33,
							value.getAsLong(DetectorInspector.PropertyTable.DISPLAY_RANK));
					insert.bindLong(
							34,
							value.getAsLong(DetectorInspector.PropertyTable.START_DISPLAY_RANK));

					insert.bindString(
							35,
							value.getAsString(DetectorInspector.PropertyTable.LATITUTE));

					insert.bindString(
							36,
							value.getAsString(DetectorInspector.PropertyTable.LONGITUTE));
					insert.bindString(
							37,
							value.getAsString(DetectorInspector.PropertyTable.BOOKING_ID));
					insert.bindString(
							38,
							value.getAsString(DetectorInspector.PropertyTable.INSPECTION_DATE));
					insert.bindString(
							39,
							value.getAsString(DetectorInspector.PropertyTable.PREVIOUS_EXPIRY_YEAR));
					insert.bindString(
							40,
							value.getAsString(DetectorInspector.PropertyTable.PREVIOUS_NEW_EXPIRY_YEAR));
					insert.bindString(
							41,
							value.getAsString(DetectorInspector.PropertyTable.PREVIOUS_DETECTOR_TYPE));
					insert.bindString(
							42,
							value.getAsString(DetectorInspector.PropertyTable.REPORT_COMPLETED_DATE));
					insert.bindString(
							43,
							value.getAsString(DetectorInspector.PropertyTable.SERVICE_SHEET_ID));
					insert.bindLong(
							44,
							value.getAsLong(DetectorInspector.PropertyTable.VALIDATION_OFF));
					insert.bindLong(
							45,
							value.getAsLong(DetectorInspector.PropertyTable.SEND_BROADCAST));
					insert.executeInsert();
				}
				insert.close();
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			break;

		case REPORTS:
			break;
		case REPORT_SECTIONS:
			db.beginTransaction();
			try {
				String sql = "insert into "
						+ DetectorInspector.ReportSectionTable.TABLE_NAME
						+ " (" + DetectorInspector.ReportSectionTable.REPORT_ID
						+ "," + DetectorInspector.ReportSectionTable.NAME + ","
						+ DetectorInspector.ReportSectionTable.SECTION_ID + ","
						+ DetectorInspector.ReportSectionTable.SECTION_TYPE
						+ "," + DetectorInspector.ReportSectionTable.IS_DELETED
						+ ","
						+ DetectorInspector.ReportSectionTable.CREATED_DATE
						+ ","
						+ DetectorInspector.ReportSectionTable.MODIFIED_DATE
						+ ") values (?,?,?,?,?,?,?)";
				SQLiteStatement insert = db.compileStatement(sql);

				for (ContentValues value : values) {
					insert.bindString(
							1,
							value.getAsString(DetectorInspector.ReportSectionTable.REPORT_ID));
					insert.bindString(
							2,
							value.getAsString(DetectorInspector.ReportSectionTable.NAME));
					insert.bindLong(
							3,
							value.getAsLong(DetectorInspector.ReportSectionTable.SECTION_ID));

					insert.bindString(
							4,
							value.getAsString(DetectorInspector.ReportSectionTable.SECTION_TYPE));

					insert.bindString(
							5,
							value.getAsString(DetectorInspector.ReportSectionTable.IS_DELETED));
					insert.bindString(
							6,
							value.getAsString(DetectorInspector.ReportSectionTable.CREATED_DATE));
					insert.bindString(
							7,
							value.getAsString(DetectorInspector.ReportSectionTable.MODIFIED_DATE));

					insert.executeInsert();
				}
				insert.close();
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
				db.close();
			}
			break;

		case REPORT_PHOTO_COMMENTS:
		case REPORT_PHOTOS:
			break;

		case HISTORY:
			db.beginTransaction();
			try {

				String sql = "insert into "
						+ DetectorInspector.HistoryTable.TABLE_NAME
						+ " ("
						+ DetectorInspector.HistoryTable.REPORT_ID
						+ ","
						+ DetectorInspector.HistoryTable.REPORT_UUID
						+ ","
						+ DetectorInspector.HistoryTable.PROPERTY_ID
						+ ","
						+ DetectorInspector.HistoryTable.UNIT_SHOP_NUMBER
						+ ","
						+ DetectorInspector.HistoryTable.REPORT_TIME
						+ ","
						+ DetectorInspector.HistoryTable.REPORT_DATE
						+ ","
						+ DetectorInspector.HistoryTable.SYNC_STATUS
						+ ","
						+ DetectorInspector.HistoryTable.STATUS
						+ ","
						+ DetectorInspector.HistoryTable.STREET_NUMBER
						+ ","
						+ DetectorInspector.HistoryTable.STREET_NAME
						+ ","
						+ DetectorInspector.HistoryTable.SUBURB
						+ ","
						+ DetectorInspector.HistoryTable.POSTCODE
						+ ","
						+ DetectorInspector.HistoryTable.STATE
						+ ","
						+ DetectorInspector.HistoryTable.KEY_NUMBER
						+ ","
						+ DetectorInspector.HistoryTable.NOTES
						+ ","
						+ DetectorInspector.HistoryTable.HAS_LARGE_LADDER
						+ ","
						+ DetectorInspector.HistoryTable.HAS_SEND_NOTIFICATION
						+ ","
						+ DetectorInspector.HistoryTable.OCCUPANT_NAME
						+ ","
						+ DetectorInspector.HistoryTable.OCCUPANT_EMAIL
						+ ","
						+ DetectorInspector.HistoryTable.OCCUPANT_TELEPHONE_NO
						+ ","
						+ DetectorInspector.HistoryTable.OCCUPANT_MOBILE_NO
						+ ","
						+ DetectorInspector.HistoryTable.OCCUPANT_BUSINESS_NO
						+ ","
						+ DetectorInspector.HistoryTable.OCCUPANT_HOME_NO
						+ ","
						+ DetectorInspector.HistoryTable.POSTAL_ADDRESS
						+ ","
						+ DetectorInspector.HistoryTable.POSTAL_SUBURB
						+ ","
						+ DetectorInspector.HistoryTable.POSTAL_POST_CODE
						+ ","
						+ DetectorInspector.HistoryTable.POSTAL_STATE_ID
						+ ","
						+ DetectorInspector.HistoryTable.POSTAL_COUNTRY
						+ ","
						+ DetectorInspector.HistoryTable.AGENCY_NAME
						+ ","
						+ DetectorInspector.HistoryTable.AGENCY_ID
						+ ","
						+ DetectorInspector.HistoryTable.NO_OF_ALARAM
						+ ","
						+ DetectorInspector.HistoryTable.REASON
						+ ","
						+ DetectorInspector.HistoryTable.DISPLAY_RANK
						+ ","
						+ DetectorInspector.HistoryTable.START_DISPLAY_RANK
						+ ","
						+ DetectorInspector.HistoryTable.LATITUTE
						+ ","
						+ DetectorInspector.HistoryTable.LONGITUTE
						+ ","
						+ DetectorInspector.HistoryTable.BOOKING_ID
						+ ","
						+ DetectorInspector.HistoryTable.INSPECTION_DATE

						+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

				SQLiteStatement insert = db.compileStatement(sql);

				
				for (ContentValues value : values) {

					insert.bindString(
							1,
							value.getAsString(DetectorInspector.HistoryTable.REPORT_ID));
					insert.bindString(
							2,
							value.getAsString(DetectorInspector.HistoryTable.REPORT_UUID));
					insert.bindString(
							3,
							value.getAsString(DetectorInspector.HistoryTable.PROPERTY_ID));
					insert.bindString(
							4,
							value.getAsString(DetectorInspector.HistoryTable.UNIT_SHOP_NUMBER));
					insert.bindString(
							5,
							value.getAsString(DetectorInspector.HistoryTable.REPORT_TIME));
					insert.bindString(
							6,
							value.getAsString(DetectorInspector.HistoryTable.REPORT_DATE));
					insert.bindLong(
							7,
							value.getAsLong(DetectorInspector.HistoryTable.SYNC_STATUS));
					insert.bindLong(8, value
							.getAsLong(DetectorInspector.HistoryTable.STATUS));
					insert.bindString(
							9,
							value.getAsString(DetectorInspector.HistoryTable.STREET_NUMBER));
					insert.bindString(
							10,
							value.getAsString(DetectorInspector.HistoryTable.STREET_NAME));
					insert.bindString(11, value
							.getAsString(DetectorInspector.HistoryTable.SUBURB));
					insert.bindString(
							12,
							value.getAsString(DetectorInspector.HistoryTable.POSTCODE));
					insert.bindString(13, value
							.getAsString(DetectorInspector.HistoryTable.STATE));
					insert.bindString(
							14,
							value.getAsString(DetectorInspector.HistoryTable.KEY_NUMBER));
					insert.bindString(15, value
							.getAsString(DetectorInspector.HistoryTable.NOTES));
					insert.bindString(
							16,
							value.getAsString(DetectorInspector.HistoryTable.HAS_LARGE_LADDER));
					insert.bindString(
							17,
							value.getAsString(DetectorInspector.HistoryTable.HAS_SEND_NOTIFICATION));
					insert.bindString(
							18,
							value.getAsString(DetectorInspector.HistoryTable.OCCUPANT_NAME));
					insert.bindString(
							19,
							value.getAsString(DetectorInspector.HistoryTable.OCCUPANT_EMAIL));
					insert.bindString(
							20,
							value.getAsString(DetectorInspector.HistoryTable.OCCUPANT_TELEPHONE_NO));
					insert.bindString(
							21,
							value.getAsString(DetectorInspector.HistoryTable.OCCUPANT_MOBILE_NO));
					insert.bindString(
							22,
							value.getAsString(DetectorInspector.HistoryTable.OCCUPANT_BUSINESS_NO));
					insert.bindString(
							23,
							value.getAsString(DetectorInspector.HistoryTable.OCCUPANT_HOME_NO));

					insert.bindString(
							24,
							value.getAsString(DetectorInspector.HistoryTable.POSTAL_ADDRESS));
					insert.bindString(
							25,
							value.getAsString(DetectorInspector.HistoryTable.POSTAL_SUBURB));
					insert.bindString(
							26,
							value.getAsString(DetectorInspector.HistoryTable.POSTAL_POST_CODE));
					insert.bindString(
							27,
							value.getAsString(DetectorInspector.HistoryTable.POSTAL_STATE_ID));
					insert.bindString(
							28,
							value.getAsString(DetectorInspector.HistoryTable.POSTAL_COUNTRY));
					insert.bindString(
							29,
							value.getAsString(DetectorInspector.HistoryTable.AGENCY_NAME));
					insert.bindString(
							30,
							value.getAsString(DetectorInspector.HistoryTable.AGENCY_ID));
					insert.bindLong(
							31,
							value.getAsLong(DetectorInspector.HistoryTable.NO_OF_ALARAM));
					insert.bindString(32, value
							.getAsString(DetectorInspector.HistoryTable.REASON));

					insert.bindLong(
							33,
							value.getAsLong(DetectorInspector.HistoryTable.DISPLAY_RANK));
					insert.bindLong(
							34,
							value.getAsLong(DetectorInspector.HistoryTable.START_DISPLAY_RANK));

					insert.bindString(
							35,
							value.getAsString(DetectorInspector.HistoryTable.LATITUTE));

					insert.bindString(
							36,
							value.getAsString(DetectorInspector.HistoryTable.LONGITUTE));
					insert.bindString(
							37,
							value.getAsString(DetectorInspector.HistoryTable.BOOKING_ID));
					insert.bindString(
							38,
							value.getAsString(DetectorInspector.HistoryTable.INSPECTION_DATE));
					
					Log.v("Test", "values-------------------" + values.length);
					insert.executeInsert();
				
				}
				insert.close();
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
				db.close();
			}
			break;

		default:
			throw new SQLException("Failed to insert row into " + uri);
		}

		return 0;

	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {

		// Validate the requested uri
		if (sUriMatcher.match(uri) != PROPERTIES
				&& sUriMatcher.match(uri) != REPORTS
				&& sUriMatcher.match(uri) != REPORT_SECTIONS
				&& sUriMatcher.match(uri) != REPORT_PHOTO_COMMENTS
				&& sUriMatcher.match(uri) != REPORT_PHOTOS
				&& sUriMatcher.match(uri) != HISTORY) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		// Validate the requested uri
		switch (sUriMatcher.match(uri)) {
		case PROPERTIES:
			long rowId = db.insert(PROPERTY_TABLE_NAME, PropertyTable.NAME,
					values);
			if (rowId > 0) {
				Uri insert_uri = ContentUris.withAppendedId(
						DetectorInspector.PropertyTable.CONTENT_URI, rowId);
				getContext().getContentResolver()
						.notifyChange(insert_uri, null);
				return insert_uri;
			}

		case REPORTS:
			long rowId1 = db
					.insert(REPORT_TABLE_NAME, ReportTable.NAME, values);
			if (rowId1 > 0) {
				Uri insert_uri = ContentUris.withAppendedId(
						DetectorInspector.ReportTable.CONTENT_URI, rowId1);
				getContext().getContentResolver()
						.notifyChange(insert_uri, null);
				return insert_uri;
			}
		case REPORT_SECTIONS:
			long rowId2 = db.insert(REPORT_SECTION_TABLE_NAME,
					ReportSectionTable.NAME, values);
			if (rowId2 > 0) {
				Uri insert_uri = ContentUris.withAppendedId(
						DetectorInspector.ReportSectionTable.CONTENT_URI,
						rowId2);
				getContext().getContentResolver()
						.notifyChange(insert_uri, null);
				return insert_uri;
			}

		case REPORT_PHOTO_COMMENTS:
			long rowId3 = db.insert(REPORT_PHOTO_COMMENT_TABLE_NAME,
					ReportPhotoCommentTable.VALUE, values);
			if (rowId3 > 0) {
				Uri insert_uri = ContentUris.withAppendedId(
						DetectorInspector.ReportPhotoCommentTable.CONTENT_URI,
						rowId3);
				getContext().getContentResolver()
						.notifyChange(insert_uri, null);
				return insert_uri;
			}
		case REPORT_PHOTOS:
			long rowId4 = db.insert(REPORT_PHOTO_TABLE_NAME,
					ReportPhotoTable.PHOTO_ID, values);
			if (rowId4 > 0) {
				Uri insert_uri = ContentUris.withAppendedId(
						DetectorInspector.ReportPhotoTable.CONTENT_URI, rowId4);
				getContext().getContentResolver()
						.notifyChange(insert_uri, null);
				return insert_uri;
			}

		case HISTORY:
			long rowId5 = db.insert(HISTORY_TABLE_NAME, HistoryTable.NAME,
					values);
			Log.v("Test", "values-------------------" + rowId5);
			if (rowId5 > 0) {
				Uri insert_uri = ContentUris.withAppendedId(
						DetectorInspector.HistoryTable.CONTENT_URI, rowId5);
				getContext().getContentResolver()
						.notifyChange(insert_uri, null);
				return insert_uri;
			}

		default:
			throw new SQLException("Failed to insert row into " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case PROPERTIES:
			count = db.delete(PROPERTY_TABLE_NAME, where, whereArgs);
			break;
		case PROPERTY_ID:
			String id1 = uri.getPathSegments().get(1);
			count = db.delete(PROPERTY_TABLE_NAME,
					PropertyTable._ID
							+ "="
							+ id1
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		case REPORTS:
			count = db.delete(REPORT_TABLE_NAME, where, whereArgs);
			break;
		case REPORT_ID:
			String id2 = uri.getPathSegments().get(1);
			count = db.delete(REPORT_TABLE_NAME,
					ReportTable._ID
							+ "="
							+ id2
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case REPORT_SECTIONS:
			count = db.delete(REPORT_SECTION_TABLE_NAME, where, whereArgs);
			break;
		case REPORT_SECTION_ID:
			String id3 = uri.getPathSegments().get(1);
			count = db.delete(
					REPORT_SECTION_TABLE_NAME,
					ReportSectionTable._ID
							+ "="
							+ id3
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		case REPORT_PHOTO_COMMENTS:
			count = db
					.delete(REPORT_PHOTO_COMMENT_TABLE_NAME, where, whereArgs);
			break;
		case REPORT_PHOTO_COMMENT_ID:
			String id10 = uri.getPathSegments().get(1);
			count = db.delete(
					REPORT_PHOTO_COMMENT_TABLE_NAME,
					ReportPhotoCommentTable._ID
							+ "="
							+ id10
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case REPORT_PHOTOS:
			count = db.delete(REPORT_PHOTO_TABLE_NAME, where, whereArgs);
			break;
		case REPORT_PHOTO_ID:
			String id11 = uri.getPathSegments().get(1);
			count = db.delete(
					REPORT_PHOTO_TABLE_NAME,
					ReportPhotoTable._ID
							+ "="
							+ id11
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		case HISTORY:
			count = db.delete(HISTORY_TABLE_NAME, where, whereArgs);
			break;
		case HISTORY_ID:
			String id13 = uri.getPathSegments().get(1);
			count = db.delete(HISTORY_TABLE_NAME,
					HistoryTable._ID
							+ "="
							+ id13
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case PROPERTIES:
			count = db.update(PROPERTY_TABLE_NAME, values, where, whereArgs);
			break;
		case PROPERTY_ID:
			String id1 = uri.getPathSegments().get(1);
			count = db.update(PROPERTY_TABLE_NAME, values,
					PropertyTable._ID
							+ "="
							+ id1
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
		case REPORTS:
			count = db.update(REPORT_TABLE_NAME, values, where, whereArgs);
			break;
		case REPORT_ID:
			String id2 = uri.getPathSegments().get(1);
			count = db.update(REPORT_TABLE_NAME, values,
					ReportTable._ID
							+ "="
							+ id2
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case REPORT_SECTIONS:
			count = db.update(REPORT_SECTION_TABLE_NAME, values, where,
					whereArgs);
			break;
		case REPORT_SECTION_ID:
			String id3 = uri.getPathSegments().get(1);
			count = db.update(
					REPORT_SECTION_TABLE_NAME,
					values,
					ReportSectionTable._ID
							+ "="
							+ id3
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case REPORT_PHOTO_COMMENTS:
			count = db.update(REPORT_PHOTO_COMMENT_TABLE_NAME, values, where,
					whereArgs);
			break;
		case REPORT_PHOTO_COMMENT_ID:
			String id10 = uri.getPathSegments().get(1);
			count = db.update(
					REPORT_PHOTO_COMMENT_TABLE_NAME,
					values,
					ReportPhotoCommentTable._ID
							+ "="
							+ id10
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case REPORT_PHOTOS:
			count = db
					.update(REPORT_PHOTO_TABLE_NAME, values, where, whereArgs);
			break;
		case REPORT_PHOTO_ID:
			String id11 = uri.getPathSegments().get(1);
			count = db.update(
					REPORT_PHOTO_TABLE_NAME,
					values,
					ReportPhotoTable._ID
							+ "="
							+ id11
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case HISTORY:
			count = db.update(HISTORY_TABLE_NAME, values, where, whereArgs);
			break;
		case HISTORY_ID:
			String id13 = uri.getPathSegments().get(1);
			count = db.update(HISTORY_TABLE_NAME, values,
					HistoryTable._ID
							+ "="
							+ id13
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(DetectorInspector.AUTHORITY,
				PropertyTable.TABLE_NAME, PROPERTIES);
		sUriMatcher.addURI(DetectorInspector.AUTHORITY,
				PropertyTable.TABLE_NAME + "/#", PROPERTY_ID);

		sUriMatcher.addURI(DetectorInspector.AUTHORITY, ReportTable.TABLE_NAME,
				REPORTS);
		sUriMatcher.addURI(DetectorInspector.AUTHORITY, ReportTable.TABLE_NAME
				+ "/#", REPORT_ID);
		sUriMatcher.addURI(DetectorInspector.AUTHORITY,
				ReportSectionTable.TABLE_NAME, REPORT_SECTIONS);
		sUriMatcher.addURI(DetectorInspector.AUTHORITY,
				ReportSectionTable.TABLE_NAME + "/#", REPORT_SECTION_ID);
		sUriMatcher.addURI(DetectorInspector.AUTHORITY,
				ReportPhotoCommentTable.TABLE_NAME, REPORT_PHOTO_COMMENTS);
		sUriMatcher.addURI(DetectorInspector.AUTHORITY,
				ReportPhotoCommentTable.TABLE_NAME + "/#",
				REPORT_PHOTO_COMMENT_ID);
		sUriMatcher.addURI(DetectorInspector.AUTHORITY,
				ReportPhotoTable.TABLE_NAME, REPORT_PHOTOS);
		sUriMatcher.addURI(DetectorInspector.AUTHORITY,
				ReportPhotoTable.TABLE_NAME + "/#", REPORT_PHOTO_ID);
		
		sUriMatcher.addURI(DetectorInspector.AUTHORITY,
				HistoryTable.TABLE_NAME, HISTORY);
		sUriMatcher.addURI(DetectorInspector.AUTHORITY, HistoryTable.TABLE_NAME
				+ "/#", HISTORY_ID);

		sPropertyProjectionMap = new HashMap<String, String>();

		sPropertyProjectionMap.put(PropertyTable._ID, PropertyTable._ID);
		sPropertyProjectionMap.put(PropertyTable.NAME, PropertyTable.NAME);
		sPropertyProjectionMap.put(PropertyTable.REPORT_ID,
				PropertyTable.REPORT_ID);
		sPropertyProjectionMap.put(PropertyTable.REPORT_UUID,
				PropertyTable.REPORT_UUID);
		sPropertyProjectionMap.put(PropertyTable.PROPERTY_ID,
				PropertyTable.PROPERTY_ID);
		sPropertyProjectionMap.put(PropertyTable.UNIT_SHOP_NUMBER,
				PropertyTable.UNIT_SHOP_NUMBER);
		sPropertyProjectionMap.put(PropertyTable.REPORT_TIME,
				PropertyTable.REPORT_TIME);
		sPropertyProjectionMap.put(PropertyTable.REPORT_DATE,
				PropertyTable.REPORT_DATE);
		sPropertyProjectionMap.put(PropertyTable.SYNC_STATUS,
				PropertyTable.SYNC_STATUS);
		sPropertyProjectionMap.put(PropertyTable.STATUS, PropertyTable.STATUS);
		sPropertyProjectionMap.put(PropertyTable.STREET_NUMBER,
				PropertyTable.STREET_NUMBER);
		sPropertyProjectionMap.put(PropertyTable.STREET_NAME,
				PropertyTable.STREET_NAME);
		sPropertyProjectionMap.put(PropertyTable.SUBURB, PropertyTable.SUBURB);
		sPropertyProjectionMap.put(PropertyTable.POSTCODE,
				PropertyTable.POSTCODE);
		sPropertyProjectionMap.put(PropertyTable.STATE, PropertyTable.STATE);
		sPropertyProjectionMap.put(PropertyTable.KEY_NUMBER,
				PropertyTable.KEY_NUMBER);
		sPropertyProjectionMap.put(PropertyTable.NOTES, PropertyTable.NOTES);
		sPropertyProjectionMap.put(PropertyTable.HAS_LARGE_LADDER,
				PropertyTable.HAS_LARGE_LADDER);
		sPropertyProjectionMap.put(PropertyTable.HAS_SEND_NOTIFICATION,
				PropertyTable.HAS_SEND_NOTIFICATION);
		sPropertyProjectionMap.put(PropertyTable.OCCUPANT_NAME,
				PropertyTable.OCCUPANT_NAME);
		sPropertyProjectionMap.put(PropertyTable.OCCUPANT_EMAIL,
				PropertyTable.OCCUPANT_EMAIL);
		sPropertyProjectionMap.put(PropertyTable.OCCUPANT_TELEPHONE_NO,
				PropertyTable.OCCUPANT_TELEPHONE_NO);
		sPropertyProjectionMap.put(PropertyTable.OCCUPANT_MOBILE_NO,
				PropertyTable.OCCUPANT_MOBILE_NO);

		sPropertyProjectionMap.put(PropertyTable.OCCUPANT_BUSINESS_NO,
				PropertyTable.OCCUPANT_BUSINESS_NO);
		sPropertyProjectionMap.put(PropertyTable.OCCUPANT_HOME_NO,
				PropertyTable.OCCUPANT_HOME_NO);

		sPropertyProjectionMap.put(PropertyTable.POSTAL_ADDRESS,
				PropertyTable.POSTAL_ADDRESS);
		sPropertyProjectionMap.put(PropertyTable.POSTAL_SUBURB,
				PropertyTable.POSTAL_SUBURB);
		sPropertyProjectionMap.put(PropertyTable.POSTAL_POST_CODE,
				PropertyTable.POSTAL_POST_CODE);
		sPropertyProjectionMap.put(PropertyTable.POSTAL_STATE_ID,
				PropertyTable.POSTAL_STATE_ID);
		sPropertyProjectionMap.put(PropertyTable.POSTAL_COUNTRY,
				PropertyTable.POSTAL_COUNTRY);
		sPropertyProjectionMap.put(PropertyTable.AGENCY_NAME,
				PropertyTable.AGENCY_NAME);
		sPropertyProjectionMap.put(PropertyTable.AGENCY_ID,
				PropertyTable.AGENCY_ID);
		sPropertyProjectionMap.put(PropertyTable.NO_OF_ALARAM,
				PropertyTable.NO_OF_ALARAM);
		sPropertyProjectionMap.put(PropertyTable.REASON, PropertyTable.REASON);

		sPropertyProjectionMap.put(PropertyTable.DISPLAY_RANK,
				PropertyTable.DISPLAY_RANK);
		sPropertyProjectionMap.put(PropertyTable.START_DISPLAY_RANK,
				PropertyTable.START_DISPLAY_RANK);

		sPropertyProjectionMap.put(PropertyTable.LATITUTE,
				PropertyTable.LATITUTE);
		sPropertyProjectionMap.put(PropertyTable.LONGITUTE,
				PropertyTable.LONGITUTE);
		sPropertyProjectionMap.put(PropertyTable.BOOKING_ID,
				PropertyTable.BOOKING_ID);
		sPropertyProjectionMap.put(PropertyTable.INSPECTION_DATE,
				PropertyTable.INSPECTION_DATE);
		sPropertyProjectionMap.put(PropertyTable.PREVIOUS_EXPIRY_YEAR,
				PropertyTable.PREVIOUS_EXPIRY_YEAR);
		sPropertyProjectionMap.put(PropertyTable.PREVIOUS_NEW_EXPIRY_YEAR,
				PropertyTable.PREVIOUS_NEW_EXPIRY_YEAR);
		sPropertyProjectionMap.put(PropertyTable.PREVIOUS_DETECTOR_TYPE,
				PropertyTable.PREVIOUS_DETECTOR_TYPE);
		sPropertyProjectionMap.put(PropertyTable.REPORT_COMPLETED_DATE,
				PropertyTable.REPORT_COMPLETED_DATE);
		sPropertyProjectionMap.put(PropertyTable.SERVICE_SHEET_ID,
				PropertyTable.SERVICE_SHEET_ID);
		sPropertyProjectionMap.put(PropertyTable.VALIDATION_OFF,
				PropertyTable.VALIDATION_OFF);
		sPropertyProjectionMap.put(PropertyTable.SEND_BROADCAST,
				PropertyTable.SEND_BROADCAST);

		sReportProjectionMap = new HashMap<String, String>();
		sReportProjectionMap.put(ReportTable._ID, ReportTable._ID);
		sReportProjectionMap.put(ReportTable.NAME, ReportTable.NAME);
		sReportProjectionMap.put(ReportTable.REPORT_ID, ReportTable.REPORT_ID);
		sReportProjectionMap.put(ReportTable.CREATED_DATE,
				ReportTable.CREATED_DATE);
		sReportProjectionMap.put(ReportTable.MODIFIED_DATE,
				ReportTable.MODIFIED_DATE);

		sReportSectionProjectionMap = new HashMap<String, String>();
		sReportSectionProjectionMap.put(ReportSectionTable._ID,
				ReportSectionTable._ID);
		sReportSectionProjectionMap.put(ReportSectionTable.NAME,
				ReportSectionTable.NAME);
		sReportSectionProjectionMap.put(ReportSectionTable.REPORT_ID,
				ReportSectionTable.REPORT_ID);
		sReportSectionProjectionMap.put(ReportSectionTable.SECTION_ID,
				ReportSectionTable.SECTION_ID);
		sReportSectionProjectionMap.put(ReportSectionTable.SECTION_TYPE,
				ReportSectionTable.SECTION_TYPE);
		sReportSectionProjectionMap.put(ReportSectionTable.IS_DELETED,
				ReportSectionTable.IS_DELETED);
		sReportSectionProjectionMap.put(ReportSectionTable.PHOTO_ID,
				ReportSectionTable.PHOTO_ID);
		sReportSectionProjectionMap.put(ReportSectionTable.CREATED_DATE,
				ReportSectionTable.CREATED_DATE);
		sReportSectionProjectionMap.put(ReportSectionTable.MODIFIED_DATE,
				ReportSectionTable.MODIFIED_DATE);

		sReportPhotoCommentProjectionMap = new HashMap<String, String>();
		sReportPhotoCommentProjectionMap.put(ReportPhotoCommentTable._ID,
				ReportPhotoCommentTable._ID);
		sReportPhotoCommentProjectionMap.put(ReportPhotoCommentTable.PHOTO_ID,
				ReportPhotoCommentTable.PHOTO_ID);
		sReportPhotoCommentProjectionMap.put(
				ReportPhotoCommentTable.COMMENT_ID,
				ReportPhotoCommentTable.COMMENT_ID);
		sReportPhotoCommentProjectionMap.put(ReportPhotoCommentTable.NAME,
				ReportPhotoCommentTable.NAME);
		sReportPhotoCommentProjectionMap.put(ReportPhotoCommentTable.VALUE,
				ReportPhotoCommentTable.VALUE);
		sReportPhotoCommentProjectionMap.put(ReportPhotoCommentTable.X,
				ReportPhotoCommentTable.X);
		sReportPhotoCommentProjectionMap.put(ReportPhotoCommentTable.Y,
				ReportPhotoCommentTable.Y);

		sReportPhotoProjectionMap = new HashMap<String, String>();
		sReportPhotoProjectionMap.put(ReportPhotoTable._ID,
				ReportPhotoTable._ID);
		sReportPhotoProjectionMap.put(ReportPhotoTable.REPORT_ID,
				ReportPhotoTable.REPORT_ID);
		sReportPhotoProjectionMap.put(ReportPhotoTable.REPORT_UUID,
				ReportPhotoTable.REPORT_UUID);
		sReportPhotoProjectionMap.put(ReportPhotoTable.SECTION_ID,
				ReportPhotoTable.SECTION_ID);
		sReportPhotoProjectionMap.put(ReportPhotoTable.PHOTO_ID,
				ReportPhotoTable.PHOTO_ID);
		sReportPhotoProjectionMap.put(ReportPhotoTable.QUALITY,
				ReportPhotoTable.QUALITY);
		sReportPhotoProjectionMap.put(ReportPhotoTable.CREATED_DATE,
				ReportPhotoTable.CREATED_DATE);

		sHistoryProjectionMap = new HashMap<String, String>();

		sHistoryProjectionMap.put(HistoryTable._ID, HistoryTable._ID);
		sHistoryProjectionMap.put(HistoryTable.NAME, HistoryTable.NAME);
		sHistoryProjectionMap.put(HistoryTable.REPORT_ID,
				HistoryTable.REPORT_ID);
		sHistoryProjectionMap.put(HistoryTable.REPORT_UUID,
				HistoryTable.REPORT_UUID);
		sHistoryProjectionMap.put(HistoryTable.PROPERTY_ID,
				HistoryTable.PROPERTY_ID);
		sHistoryProjectionMap.put(HistoryTable.UNIT_SHOP_NUMBER,
				HistoryTable.UNIT_SHOP_NUMBER);
		sHistoryProjectionMap.put(HistoryTable.REPORT_TIME,
				HistoryTable.REPORT_TIME);
		sHistoryProjectionMap.put(HistoryTable.REPORT_DATE,
				HistoryTable.REPORT_DATE);
		sHistoryProjectionMap.put(HistoryTable.SYNC_STATUS,
				HistoryTable.SYNC_STATUS);
		sHistoryProjectionMap.put(HistoryTable.STATUS, HistoryTable.STATUS);
		sHistoryProjectionMap.put(HistoryTable.STREET_NUMBER,
				HistoryTable.STREET_NUMBER);
		sHistoryProjectionMap.put(HistoryTable.STREET_NAME,
				HistoryTable.STREET_NAME);
		sHistoryProjectionMap.put(HistoryTable.SUBURB, HistoryTable.SUBURB);
		sHistoryProjectionMap.put(HistoryTable.POSTCODE, HistoryTable.POSTCODE);
		sHistoryProjectionMap.put(HistoryTable.STATE, HistoryTable.STATE);
		sHistoryProjectionMap.put(HistoryTable.KEY_NUMBER,
				HistoryTable.KEY_NUMBER);
		sHistoryProjectionMap.put(HistoryTable.NOTES, HistoryTable.NOTES);
		sHistoryProjectionMap.put(HistoryTable.HAS_LARGE_LADDER,
				HistoryTable.HAS_LARGE_LADDER);
		sHistoryProjectionMap.put(HistoryTable.HAS_SEND_NOTIFICATION,
				HistoryTable.HAS_SEND_NOTIFICATION);
		sHistoryProjectionMap.put(HistoryTable.OCCUPANT_NAME,
				HistoryTable.OCCUPANT_NAME);
		sHistoryProjectionMap.put(HistoryTable.OCCUPANT_EMAIL,
				HistoryTable.OCCUPANT_EMAIL);
		sHistoryProjectionMap.put(HistoryTable.OCCUPANT_TELEPHONE_NO,
				HistoryTable.OCCUPANT_TELEPHONE_NO);
		sHistoryProjectionMap.put(HistoryTable.OCCUPANT_MOBILE_NO,
				HistoryTable.OCCUPANT_MOBILE_NO);

		sHistoryProjectionMap.put(HistoryTable.OCCUPANT_BUSINESS_NO,
				HistoryTable.OCCUPANT_BUSINESS_NO);
		sHistoryProjectionMap.put(HistoryTable.OCCUPANT_HOME_NO,
				HistoryTable.OCCUPANT_HOME_NO);

		sHistoryProjectionMap.put(HistoryTable.POSTAL_ADDRESS,
				HistoryTable.POSTAL_ADDRESS);
		sHistoryProjectionMap.put(HistoryTable.POSTAL_SUBURB,
				HistoryTable.POSTAL_SUBURB);
		sHistoryProjectionMap.put(HistoryTable.POSTAL_POST_CODE,
				HistoryTable.POSTAL_POST_CODE);
		sHistoryProjectionMap.put(HistoryTable.POSTAL_STATE_ID,
				HistoryTable.POSTAL_STATE_ID);
		sHistoryProjectionMap.put(HistoryTable.POSTAL_COUNTRY,
				HistoryTable.POSTAL_COUNTRY);
		sHistoryProjectionMap.put(HistoryTable.AGENCY_NAME,
				HistoryTable.AGENCY_NAME);
		sHistoryProjectionMap.put(HistoryTable.AGENCY_ID,
				HistoryTable.AGENCY_ID);
		sHistoryProjectionMap.put(HistoryTable.NO_OF_ALARAM,
				HistoryTable.NO_OF_ALARAM);
		sHistoryProjectionMap.put(HistoryTable.REASON, HistoryTable.REASON);

		sHistoryProjectionMap.put(HistoryTable.DISPLAY_RANK,
				HistoryTable.DISPLAY_RANK);
		sHistoryProjectionMap.put(HistoryTable.START_DISPLAY_RANK,
				HistoryTable.START_DISPLAY_RANK);

		sHistoryProjectionMap.put(HistoryTable.LATITUTE, HistoryTable.LATITUTE);
		sHistoryProjectionMap.put(HistoryTable.LONGITUTE,
				HistoryTable.LONGITUTE);
		sHistoryProjectionMap.put(HistoryTable.BOOKING_ID,
				HistoryTable.BOOKING_ID);
		sHistoryProjectionMap.put(HistoryTable.INSPECTION_DATE,
				HistoryTable.INSPECTION_DATE);
		sHistoryProjectionMap.put(HistoryTable.PREVIOUS_EXPIRY_YEAR,
				HistoryTable.PREVIOUS_EXPIRY_YEAR);
		sHistoryProjectionMap.put(HistoryTable.PREVIOUS_NEW_EXPIRY_YEAR,
				HistoryTable.PREVIOUS_NEW_EXPIRY_YEAR);
		sHistoryProjectionMap.put(HistoryTable.PREVIOUS_DETECTOR_TYPE,
				HistoryTable.PREVIOUS_DETECTOR_TYPE);
		sHistoryProjectionMap.put(HistoryTable.REPORT_COMPLETED_DATE,
				HistoryTable.REPORT_COMPLETED_DATE);
		sHistoryProjectionMap.put(HistoryTable.SERVICE_SHEET_ID,
				HistoryTable.SERVICE_SHEET_ID);
		sHistoryProjectionMap.put(HistoryTable.VALIDATION_OFF,
				HistoryTable.VALIDATION_OFF);
		sHistoryProjectionMap.put(HistoryTable.SEND_BROADCAST,
				HistoryTable.SEND_BROADCAST);

	}

}
