package sdei.detector.inspector.util;

import java.util.ArrayList;
import java.util.List;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.provider.DetectorInspector;
import sdei.detector.inspector.provider.DetectorInspector.HistoryTable;
import sdei.detector.inspector.provider.DetectorInspector.PropertyTable;
import sdei.detector.inspector.sync.SyncPhoto;
import sdei.detector.inspector.sync.report.ReportPhoto;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.detector.inspector.lib.model.Agency;
import com.detector.inspector.lib.model.Contact;
import com.detector.inspector.lib.model.Inspection;
import com.detector.inspector.lib.util.Util;
import com.detector.inspector.synch.report.ReportItem;

public class DatabaseUtil {

	/*
	 * Import property list JSON object
	 */
	public static void importProperty(Activity activity,List<Inspection> property_list) {

		Context context = activity;
		context.getContentResolver().delete( DetectorInspector.PropertyTable.CONTENT_URI,
				DetectorInspector.PropertyTable.SYNC_STATUS + " < ?",
				new String[] { Const.REPORT_SYNC_STATUS_CODES.SYNC_FINISHED	+ "" });
		ContentValues[] values = new ContentValues[property_list.size()];

		int count = 0;
		for (Inspection property : property_list) {

			ContentValues value = new ContentValues();
			value.put(PropertyTable.REPORT_ID, property.getReportId());
			
			// double check report UUID
			if (property.getReport_uuid() == null) 
			{
				property.setReport_uuid(Util.getUUID());
			}

			value.put(PropertyTable.REPORT_UUID,     property.getReport_uuid());
			value.put(PropertyTable.PROPERTY_ID,     property.getPropertyId());
			value.put(PropertyTable.UNIT_SHOP_NUMBER,property.getUnitShopNumber());
			value.put(PropertyTable.REPORT_TIME,     property.getKeytime());
			value.put(PropertyTable.REPORT_DATE,     property.getDate());

			value.put(PropertyTable.SYNC_STATUS, property.getSync_status());
			value.put(PropertyTable.STATUS,      property.getStatus());

			String na = "";
			String unitShopNumber = property.getUnitShopNumber() != null ? property	.getUnitShopNumber() : "";
			String streetNu = property.getStreetNumber() != null ? property
					.getStreetNumber() : "";
			if (unitShopNumber.length() > 0) {
				na = unitShopNumber;
			}
			if (streetNu.length() > 0) {
				if (na.length() > 0) {
					na = na + "/" + streetNu;
				} else {
					na = streetNu;
				}
			}

			value.put(PropertyTable.STREET_NUMBER, na.length() > 0 ? na : "");
			value.put(PropertyTable.STREET_NAME, property.getStreetName());
			value.put(PropertyTable.SUBURB, property.getSuburb());
			value.put(PropertyTable.POSTCODE, property.getPostCode());
			value.put(PropertyTable.STATE, property.getState());
			value.put(PropertyTable.KEY_NUMBER, property.getKeyNumber());
			value.put(PropertyTable.NOTES, property.getNotes());
			value.put(PropertyTable.HAS_LARGE_LADDER,
					property.isHasLargeLadder());
			value.put(PropertyTable.HAS_SEND_NOTIFICATION,
					property.isHasSendNotification());
			value.put(PropertyTable.OCCUPANT_NAME, property.getOccupantName());
			value.put(PropertyTable.OCCUPANT_EMAIL, property.getOccupantEmail());

			if (property.getContact() != null
					&& property.getContact().size() > 0) {
				String number = Utils.getContactNumber(property.getContact());
				value.put(PropertyTable.OCCUPANT_TELEPHONE_NO, number);
				value.put(PropertyTable.OCCUPANT_MOBILE_NO, "");
				value.put(PropertyTable.OCCUPANT_BUSINESS_NO, "");
				value.put(PropertyTable.OCCUPANT_HOME_NO, "");

			} else {
				value.put(PropertyTable.OCCUPANT_TELEPHONE_NO, "");
				value.put(PropertyTable.OCCUPANT_MOBILE_NO, "");
				value.put(PropertyTable.OCCUPANT_BUSINESS_NO, "");
				value.put(PropertyTable.OCCUPANT_HOME_NO, "");

			}

			value.put(PropertyTable.POSTAL_ADDRESS, property.getPostalAddress());
			value.put(PropertyTable.POSTAL_SUBURB, property.getPostalSuburb());
			value.put(PropertyTable.POSTAL_POST_CODE,
					property.getPostalPostCode());
			value.put(PropertyTable.POSTAL_STATE_ID, property.getPostalState());
			value.put(PropertyTable.POSTAL_COUNTRY, property.getPostalCountry());

			value.put(PropertyTable.AGENCY_NAME, property.getAgency()
					.getAgencyName());
			value.put(PropertyTable.AGENCY_ID, property.getAgency()
					.getAgencyId());

			value.put(PropertyTable.NO_OF_ALARAM, property.getNoOfAlaram());
			value.put(PropertyTable.REASON, "");

			value.put(PropertyTable.DISPLAY_RANK, property.getDisplayRank());
			value.put(PropertyTable.START_DISPLAY_RANK,
					property.getStartDisplayRank());
			value.put(PropertyTable.LATITUTE, property.getLat());
			value.put(PropertyTable.LONGITUTE, property.getLng());
			value.put(PropertyTable.BOOKING_ID, property.getBookingId());
			value.put(PropertyTable.INSPECTION_DATE,
					property.getInspectionDate());

			int i = 0;
			String previousExpiryYear = null;
			String previousNewExpiryYear = null;
			String previousDetectorType = null;
			String previousServiceSheetId = null;

			for (ReportItem mReportItem : property.getPreviousHistory()) {
				if (i == 0) {
					previousExpiryYear = mReportItem.getExpiryYear();
					previousDetectorType = mReportItem.getDetectorType() + "";
					previousNewExpiryYear = mReportItem.getNewExpiryYear();
				} else {
					previousExpiryYear = previousExpiryYear + ","
							+ mReportItem.getExpiryYear();
					previousDetectorType = previousDetectorType + ","
							+ mReportItem.getDetectorType() + "";
					previousNewExpiryYear = previousNewExpiryYear + ","
							+ mReportItem.getNewExpiryYear();

				}

				previousServiceSheetId = mReportItem.getServiceSheetId();
				i++;
			}

			if (i > 0) {
				value.put(PropertyTable.PREVIOUS_EXPIRY_YEAR,
						previousExpiryYear);
				value.put(PropertyTable.PREVIOUS_NEW_EXPIRY_YEAR,
						previousNewExpiryYear);
				value.put(PropertyTable.PREVIOUS_DETECTOR_TYPE,
						previousDetectorType);
				value.put(PropertyTable.SERVICE_SHEET_ID,
						previousServiceSheetId);
			} else {
				value.put(PropertyTable.PREVIOUS_NEW_EXPIRY_YEAR, "");
				value.put(PropertyTable.PREVIOUS_EXPIRY_YEAR, "");
				value.put(PropertyTable.PREVIOUS_DETECTOR_TYPE, "");
				value.put(PropertyTable.SERVICE_SHEET_ID, 0);
			}
			value.put(PropertyTable.REPORT_COMPLETED_DATE,
					property.getReportCompletedDate());

			value.put(PropertyTable.VALIDATION_OFF, property.getValidationOff());

			switch (property.getSendBroadCast()) {
			case Const.IS_NEED_TO_SYNC.NO_NEED_TO_SYNC:
				// not set

				if (property.getSync_status() == Const.REPORT_SYNC_STATUS_CODES.SYNC_FINISHED) {
					value.put(PropertyTable.SEND_BROADCAST,
							property.getSendBroadCast());
				} else {

					Utils.setAlaram(activity, property.getKeytime(),
							property.getInspectionDate(),
							property.getPropertyId(),
							property.getReport_uuid(), count, true);
					value.put(PropertyTable.SEND_BROADCAST,
							Const.IS_NEED_TO_SYNC.NEED_TO_SYNC);
				}
				break;
			case Const.IS_NEED_TO_SYNC.NEED_TO_SYNC:
				// already set Broadcast
				value.put(PropertyTable.SEND_BROADCAST,
						property.getSendBroadCast());
				break;
			case Const.IS_NEED_TO_SYNC.PROGRESS_NEED_TO_SYNC:
				// complete broadcast
				value.put(PropertyTable.SEND_BROADCAST,	property.getSendBroadCast());
				break;

			default:
				break;
			}

			// if(property.getUpdateValue()==10){
			// Log.v("Test  here","Value is 10 "+property.getUpdateValue() );
			// String where = DetectorInspector.PropertyTable.REPORT_UUID +
			// "=?";
			// String[] selectionArgs = new String[] {
			// String.valueOf(property.getReport_uuid()) };
			// context.getContentResolver().update(DetectorInspector.PropertyTable.CONTENT_URI,
			// value, where, selectionArgs);
			// }else{
			// Log.v("Test  here","Value is not 10 "+property.getUpdateValue()
			// );
			values[count] = value;

			count++;

			// }
			//

		}
		//
		// Log.v("Test  here","++++++++++Value is "+va.size() );
		// if(va.size()>0){
		context.getContentResolver().bulkInsert(DetectorInspector.PropertyTable.CONTENT_URI, values);

		// }

	}

	/*
	 * Get property list by report id
	 */
	public static Inspection getPropertyByReportId(Context context,
			int report_id) {

		String[] projection = { DetectorInspector.PropertyTable.REPORT_ID,
				DetectorInspector.PropertyTable.REPORT_DATE,
				DetectorInspector.PropertyTable.REPORT_TIME,
				DetectorInspector.PropertyTable.STATUS,
				DetectorInspector.PropertyTable.BOOKING_ID,
				DetectorInspector.PropertyTable.PROPERTY_ID };
		String selection = DetectorInspector.PropertyTable.REPORT_ID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(report_id) };
		Inspection p = new Inspection();
		Cursor cur = context.getContentResolver().query(
				DetectorInspector.PropertyTable.CONTENT_URI, projection,
				selection, selectionArgs, null);
		try {
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {
					p.setReportId(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.REPORT_ID)));
					p.setDate(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.REPORT_DATE)));
					p.setTime(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.REPORT_TIME)));
					p.setStatus(cur.getInt(cur
							.getColumnIndex(DetectorInspector.PropertyTable.STATUS)));
					p.setBookingId(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.BOOKING_ID)));
					p.setPropertyId(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.PROPERTY_ID)));
				}
			}
		} finally {
			cur.close();
		}
		return p;
	}

	/*
	 * Get property list by inspection status
	 */
	public static List<Inspection> getPropertyListByStatus(Context context,
			int status) {

		List<Inspection> property_list = new ArrayList<Inspection>();
		String[] projection = {

		DetectorInspector.PropertyTable.PROPERTY_ID,
				DetectorInspector.PropertyTable.REPORT_UUID,
				DetectorInspector.PropertyTable.SYNC_STATUS,
				DetectorInspector.PropertyTable.STATUS,
				DetectorInspector.PropertyTable.BOOKING_ID,
				DetectorInspector.PropertyTable.REPORT_ID,
				DetectorInspector.PropertyTable.DISPLAY_RANK,
				DetectorInspector.PropertyTable.START_DISPLAY_RANK,
				DetectorInspector.PropertyTable.LATITUTE,
				DetectorInspector.PropertyTable.LONGITUTE,
				DetectorInspector.PropertyTable.REPORT_COMPLETED_DATE,
				DetectorInspector.PropertyTable.VALIDATION_OFF,
				DetectorInspector.PropertyTable.SEND_BROADCAST,

				DetectorInspector.PropertyTable.REPORT_TIME,
				DetectorInspector.PropertyTable.REPORT_DATE,
				DetectorInspector.PropertyTable.INSPECTION_DATE,
				DetectorInspector.PropertyTable.UNIT_SHOP_NUMBER,
				DetectorInspector.PropertyTable.STREET_NUMBER,
				DetectorInspector.PropertyTable.STREET_NAME,
				DetectorInspector.PropertyTable.POSTCODE,
				DetectorInspector.PropertyTable.SUBURB,
				DetectorInspector.PropertyTable.STATE,
				DetectorInspector.PropertyTable.KEY_NUMBER,
				DetectorInspector.PropertyTable.NOTES,
				DetectorInspector.PropertyTable.HAS_LARGE_LADDER,
				DetectorInspector.PropertyTable.HAS_SEND_NOTIFICATION,
				DetectorInspector.PropertyTable.OCCUPANT_NAME,
				DetectorInspector.PropertyTable.OCCUPANT_EMAIL,
				DetectorInspector.PropertyTable.OCCUPANT_TELEPHONE_NO,

				DetectorInspector.PropertyTable.POSTAL_ADDRESS,
				DetectorInspector.PropertyTable.POSTAL_SUBURB,
				DetectorInspector.PropertyTable.POSTAL_POST_CODE,
				DetectorInspector.PropertyTable.POSTAL_STATE_ID,
				DetectorInspector.PropertyTable.POSTAL_COUNTRY,
				DetectorInspector.PropertyTable.NO_OF_ALARAM,

				DetectorInspector.PropertyTable.AGENCY_NAME,
				DetectorInspector.PropertyTable.AGENCY_ID,

				DetectorInspector.PropertyTable.SERVICE_SHEET_ID,
				DetectorInspector.PropertyTable.PREVIOUS_EXPIRY_YEAR,
				DetectorInspector.PropertyTable.PREVIOUS_NEW_EXPIRY_YEAR,
				DetectorInspector.PropertyTable.PREVIOUS_DETECTOR_TYPE

		};

		String selection = (status == -1) ? null
				: (DetectorInspector.PropertyTable.STATUS + "=?");
		String[] selectionArgs = (status == -1) ? null : new String[] { String
				.valueOf(status) };
		String sortOrder = DetectorInspector.PropertyTable.REPORT_DATE
				+ " DESC";

		Cursor cur = context.getContentResolver().query(
				DetectorInspector.PropertyTable.CONTENT_URI, projection,
				selection, selectionArgs, sortOrder);

		// raw query for fetching list in which status in not completed . . . .

		String sqlQuery = "select * from " + PropertyTable.TABLE_NAME
				+ "where SYNC_STATUS !='completed' ";

		// Cursor mSearchCursor = db.rawQuery(sqlQuery, null);

		try {
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {
					Inspection p = new Inspection();
					p.setReport_uuid(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.REPORT_UUID)));
					p.setPropertyId(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.PROPERTY_ID)));
					p.setSync_status(cur.getInt(cur
							.getColumnIndex(DetectorInspector.PropertyTable.SYNC_STATUS)));
					p.setStatus(cur.getInt(cur
							.getColumnIndex(DetectorInspector.PropertyTable.STATUS)));
					p.setBookingId(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.BOOKING_ID)));

					p.setReportId(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.REPORT_ID)));

					p.setDisplayRank(cur.getInt(cur
							.getColumnIndex(DetectorInspector.PropertyTable.DISPLAY_RANK)));
					p.setStartDisplayRank(cur.getInt(cur
							.getColumnIndex(DetectorInspector.PropertyTable.START_DISPLAY_RANK)));

					p.setLat(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.LATITUTE)));

					p.setLng(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.LONGITUTE)));

					p.setReportCompletedDate(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.REPORT_COMPLETED_DATE)));

					p.setValidationOff(cur.getInt(cur
							.getColumnIndex(DetectorInspector.PropertyTable.VALIDATION_OFF)));

					p.setSendBroadCast(cur.getInt(cur
							.getColumnIndex(DetectorInspector.PropertyTable.SEND_BROADCAST)));

					p.setKeytime(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.REPORT_TIME)));
					p.setDate(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.REPORT_DATE)));
					p.setInspectionDate(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.INSPECTION_DATE)));

					String na = "";
					String unitShopNumber = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.UNIT_SHOP_NUMBER));
					String streetNu = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.STREET_NUMBER));
					if (unitShopNumber.length() > 0) {
						na = unitShopNumber;
					}
					if (streetNu.length() > 0) {

						streetNu = streetNu.replace(unitShopNumber, "");
						Log.v("Test", streetNu);
						if (streetNu.startsWith("/")) {
							streetNu = streetNu.replace("/", "");
						}
					}

					p.setUnitShopNumber(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.UNIT_SHOP_NUMBER)));

					p.setStreetNumber(streetNu);
					p.setStreetName(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.STREET_NAME)));
					p.setSuburb(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.SUBURB)));
					p.setPostCode(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.POSTCODE)));
					p.setState(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.STATE)));
					p.setKeyNumber(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.KEY_NUMBER)));

					p.setNotes(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.NOTES)));

					p.setHasLargeLadder(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.HAS_LARGE_LADDER)));
					p.setHasSendNotification(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.HAS_SEND_NOTIFICATION)));
					p.setOccupantEmail(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.OCCUPANT_EMAIL)));
					p.setOccupantName(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.OCCUPANT_NAME)));

					String number = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.OCCUPANT_TELEPHONE_NO));
					List<Contact> mList = Utils.getContactDetail(number);
					p.setContact(mList);
					p.setPostalAddress(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.POSTAL_ADDRESS)));
					p.setPostalSuburb(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.POSTAL_SUBURB)));
					p.setPostalPostCode(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.POSTAL_POST_CODE)));
					p.setPostalState(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.POSTAL_STATE_ID)));
					p.setPostalCountry(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.POSTAL_COUNTRY)));

					p.setNoOfAlaram(cur.getInt(cur
							.getColumnIndex(DetectorInspector.PropertyTable.NO_OF_ALARAM)));

					String agencyId = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.AGENCY_ID));
					String agencyName = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.AGENCY_NAME));

					Agency ag = new Agency(agencyId, agencyName);
					p.setAgency(ag);

					String previousExpiryYear = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.PREVIOUS_EXPIRY_YEAR));
					String previousNewExpiryYear = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.PREVIOUS_NEW_EXPIRY_YEAR));

					String previousDetectorType = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.PREVIOUS_DETECTOR_TYPE));
					String previousServiceSheetId = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.SERVICE_SHEET_ID));

					List<ReportItem> mReportItem = Utils.getReportDetail(
							previousExpiryYear, previousNewExpiryYear,
							previousDetectorType, previousServiceSheetId);

					p.setPreviousHistory(mReportItem);

					property_list.add(p);
				}
			}
		} finally {
			cur.close();
		}
		return property_list;
	}

	/*
	 * Get property list by inspection date
	 */
	public static List<Inspection> getPropertyListByDate(Context context,
			String date) {

		List<Inspection> property_list = new ArrayList<Inspection>();

		String[] projection = {

		DetectorInspector.PropertyTable.PROPERTY_ID,
				DetectorInspector.PropertyTable.REPORT_UUID,
				DetectorInspector.PropertyTable.REPORT_ID,
				DetectorInspector.PropertyTable.HAS_LARGE_LADDER,
				DetectorInspector.PropertyTable.NOTES,
				DetectorInspector.PropertyTable.REPORT_TIME,
				DetectorInspector.PropertyTable.REPORT_DATE,
				DetectorInspector.PropertyTable.DISPLAY_RANK,
				DetectorInspector.PropertyTable.AGENCY_NAME,
				DetectorInspector.PropertyTable.OCCUPANT_NAME,
				DetectorInspector.PropertyTable.STREET_NUMBER,
				DetectorInspector.PropertyTable.STREET_NAME,
				DetectorInspector.PropertyTable.SUBURB,
				DetectorInspector.PropertyTable.POSTCODE,
				DetectorInspector.PropertyTable.STATE,
				DetectorInspector.PropertyTable.OCCUPANT_TELEPHONE_NO,
				DetectorInspector.PropertyTable.OCCUPANT_MOBILE_NO,
				DetectorInspector.PropertyTable.OCCUPANT_BUSINESS_NO,
				DetectorInspector.PropertyTable.OCCUPANT_HOME_NO,
				DetectorInspector.PropertyTable.BOOKING_ID,
				DetectorInspector.PropertyTable.KEY_NUMBER,
				DetectorInspector.PropertyTable.INSPECTION_DATE

		};

		String selection = DetectorInspector.PropertyTable.REPORT_DATE
				+ "=? AND " + DetectorInspector.PropertyTable.STATUS + "<"
				+ Const.PROPERTY_STATUS_CODES.COMPLETED;
		String[] selectionArgs = new String[] { String.valueOf(date) };
		String sortOrder = DetectorInspector.PropertyTable.DISPLAY_RANK
				+ " DESC";

		Cursor cur = context.getContentResolver().query(
				DetectorInspector.PropertyTable.CONTENT_URI, projection,
				selection, selectionArgs, sortOrder);
		try {
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {
					Inspection p = new Inspection();

					p.setPropertyId(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.PROPERTY_ID)));
					p.setReport_uuid(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.REPORT_UUID)));
					p.setReportId(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.REPORT_ID)));
					p.setHasLargeLadder(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.HAS_LARGE_LADDER)));

					p.setNotes(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.NOTES)));

					String time = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.REPORT_TIME));

					p.setTime(time);
					p.setKeytime(time);
					p.setDate(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.REPORT_DATE)));

					p.setDisplayRank(cur.getInt(cur
							.getColumnIndex(DetectorInspector.PropertyTable.DISPLAY_RANK)));

					String aName = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.AGENCY_NAME)) == null ? ""
							: cur.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.AGENCY_NAME));

					p.setAgencyName(aName);

					String occupanName = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.OCCUPANT_NAME)) == null ? ""
							: cur.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.OCCUPANT_NAME));

					p.setOccupantName(occupanName);

					p.setStreetNumber(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.STREET_NUMBER)));
					p.setStreetName(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.STREET_NAME)));
					p.setSuburb(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.SUBURB)));
					p.setPostCode(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.POSTCODE)));
					p.setState(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.STATE)));

					List<Contact> mContacts = new ArrayList<Contact>();
					mContacts
							.add(new Contact(
									cur.getString(cur
											.getColumnIndex(DetectorInspector.PropertyTable.OCCUPANT_TELEPHONE_NO)),
									null));
					mContacts
							.add(new Contact(
									cur.getString(cur
											.getColumnIndex(DetectorInspector.PropertyTable.OCCUPANT_MOBILE_NO)),
									null));
					mContacts
							.add(new Contact(
									cur.getString(cur
											.getColumnIndex(DetectorInspector.PropertyTable.OCCUPANT_BUSINESS_NO)),
									null));
					mContacts
							.add(new Contact(
									cur.getString(cur
											.getColumnIndex(DetectorInspector.PropertyTable.OCCUPANT_HOME_NO

											)), null));

					p.setContact(mContacts);

					p.setBookingId(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.BOOKING_ID)));
					p.setKeyNumber(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.KEY_NUMBER)));
					p.setInspectionDate(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.INSPECTION_DATE)));

					property_list.add(p);
				}
			}
		} finally {
			cur.close();
		}
		return property_list;
	}

	public static void updatePropertyNotInspectReason(Context context,
			Inspection inspection, String reason) {
		ContentValues values = new ContentValues();

		values.put(DetectorInspector.PropertyTable.REASON, reason);

		String where = DetectorInspector.PropertyTable.REPORT_UUID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(inspection
				.getReport_uuid()) };
		context.getContentResolver().delete(
				DetectorInspector.PropertyTable.CONTENT_URI, where,
				selectionArgs);
		// context.getContentResolver().update(
		// DetectorInspector.PropertyTable.CONTENT_URI, values, where,
		// selectionArgs);
	}

	/*
	 * Get property history list by date
	 */
	public static List<Inspection> getSYnchHistoryDate(Context context,
			String date, Boolean b) {

		List<Inspection> property_list = new ArrayList<Inspection>();
		String[] projection = { 
				DetectorInspector.PropertyTable.PROPERTY_ID,
				DetectorInspector.PropertyTable.REPORT_TIME,
				DetectorInspector.PropertyTable.REPORT_DATE,
				DetectorInspector.PropertyTable.STREET_NUMBER,
				DetectorInspector.PropertyTable.STREET_NAME,
				DetectorInspector.PropertyTable.SUBURB,
				DetectorInspector.PropertyTable.POSTCODE,
				DetectorInspector.PropertyTable.STATE,
				DetectorInspector.PropertyTable.STATUS,
				DetectorInspector.PropertyTable.REPORT_ID,
				DetectorInspector.PropertyTable.BOOKING_ID,
				DetectorInspector.PropertyTable.SYNC_STATUS,
				DetectorInspector.PropertyTable.LATITUTE,
				DetectorInspector.PropertyTable.LONGITUTE,
				DetectorInspector.PropertyTable.REPORT_UUID,
				DetectorInspector.PropertyTable.SERVICE_SHEET_ID };

		String selection = DetectorInspector.PropertyTable.REPORT_COMPLETED_DATE
				+ "=? OR ("
				+ DetectorInspector.PropertyTable.STATUS
				+ "= "
				+ Const.PROPERTY_STATUS_CODES.COMPLETED
				+ " AND "
				+ DetectorInspector.PropertyTable.SYNC_STATUS
				+ "="
				+ Const.REPORT_SYNC_STATUS_CODES.SYNC_NOT_STARTED + ")";

		// String selection = DetectorInspector.PropertyTable.REPORT_DATE +
		// ">?";
		String[] selectionArgs = new String[] { String.valueOf(date) };
		
		String sortOrder = DetectorInspector.PropertyTable.REPORT_DATE	+ " DESC";
		
		Cursor cur = context.getContentResolver().query(DetectorInspector.PropertyTable.CONTENT_URI, projection,selection, selectionArgs, sortOrder);
		try  {
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {
					Inspection p = new Inspection();
					p.setPropertyId(cur.getString(cur.getColumnIndex(DetectorInspector.PropertyTable.PROPERTY_ID)));
					p.setTime(cur.getString(cur.getColumnIndex(DetectorInspector.PropertyTable.REPORT_TIME)));
					p.setDate(cur.getString(cur.getColumnIndex(DetectorInspector.PropertyTable.REPORT_DATE)));
					p.setStreetNumber(cur.getString(cur.getColumnIndex(DetectorInspector.PropertyTable.STREET_NUMBER)));
					p.setStreetName(cur.getString(cur.getColumnIndex(DetectorInspector.PropertyTable.STREET_NAME)));
					p.setSuburb(cur.getString(cur	.getColumnIndex(DetectorInspector.PropertyTable.SUBURB)));
					p.setPostCode(cur.getString(cur	.getColumnIndex(DetectorInspector.PropertyTable.POSTCODE)));
					p.setState(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.STATE)));
					p.setStatus(cur.getInt(cur
							.getColumnIndex(DetectorInspector.PropertyTable.STATUS)));

					p.setReportId(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.REPORT_ID)));
					p.setBookingId(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.BOOKING_ID)));
					p.setSync_status(cur.getInt(cur
							.getColumnIndex(DetectorInspector.PropertyTable.SYNC_STATUS)));
					p.setReport_uuid(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.REPORT_UUID)));
					p.setServiceSheetId(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.SERVICE_SHEET_ID)));
					property_list.add(p);
				}
			}
		} finally {
			cur.close();
		}
		return property_list;
	}

	/*
	 * Get property history list by date
	 */
	public static List<Inspection> getHistoryListByDate(Context context,String date, Boolean b) {

		List<Inspection> property_list = new ArrayList<Inspection>();
		String[] projection = { 
				DetectorInspector.HistoryTable.PROPERTY_ID,
				DetectorInspector.HistoryTable.REPORT_TIME,
				DetectorInspector.HistoryTable.REPORT_DATE,
				DetectorInspector.HistoryTable.STREET_NUMBER,
				DetectorInspector.HistoryTable.STREET_NAME,
				DetectorInspector.HistoryTable.SUBURB,
				DetectorInspector.HistoryTable.POSTCODE,
				DetectorInspector.HistoryTable.STATE,
				DetectorInspector.HistoryTable.STATUS,
				DetectorInspector.HistoryTable.REPORT_ID,
				DetectorInspector.HistoryTable.BOOKING_ID,
				DetectorInspector.HistoryTable.SYNC_STATUS,
				DetectorInspector.HistoryTable.LATITUTE,
				DetectorInspector.HistoryTable.LONGITUTE,
				DetectorInspector.HistoryTable.REPORT_UUID, };

		String selection = "";
		if (b) {
			selection = DetectorInspector.HistoryTable.REPORT_DATE + "=? AND "
					+ DetectorInspector.HistoryTable.SYNC_STATUS + "="
					+ Const.REPORT_SYNC_STATUS_CODES.SYNC_FINISHED;
		} else {
			selection = DetectorInspector.HistoryTable.REPORT_DATE + "<=? AND "
					+ DetectorInspector.HistoryTable.SYNC_STATUS + "="
					+ Const.REPORT_SYNC_STATUS_CODES.SYNC_FINISHED;

		}

		Log.e("", date + " Selection Time  " + selection);

		// String selection = DetectorInspector.HistoryTable.REPORT_DATE +
		// ">?";
		String[] selectionArgs = new String[] { String.valueOf(date) };
		String sortOrder = DetectorInspector.HistoryTable.REPORT_DATE + " DESC";
		Cursor cur = context.getContentResolver().query(DetectorInspector.HistoryTable.CONTENT_URI, projection,	selection, selectionArgs, sortOrder);

		try {
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {
					Inspection p = new Inspection();
					p.setPropertyId(cur.getString(cur.getColumnIndex(DetectorInspector.HistoryTable.PROPERTY_ID)));
					p.setTime(cur.getString(cur	.getColumnIndex(DetectorInspector.HistoryTable.REPORT_TIME)));
					p.setDate(cur.getString(cur	.getColumnIndex(DetectorInspector.HistoryTable.REPORT_DATE)));
					p.setStreetNumber(cur.getString(cur	.getColumnIndex(DetectorInspector.HistoryTable.STREET_NUMBER)));
					p.setStreetName(cur.getString(cur.getColumnIndex(DetectorInspector.HistoryTable.STREET_NAME)));
					p.setSuburb(cur.getString(cur.getColumnIndex(DetectorInspector.HistoryTable.SUBURB)));
					p.setPostCode(cur.getString(cur	.getColumnIndex(DetectorInspector.HistoryTable.POSTCODE)));
					p.setState(cur.getString(cur.getColumnIndex(DetectorInspector.HistoryTable.STATE)));
					p.setStatus(cur.getInt(cur.getColumnIndex(DetectorInspector.HistoryTable.STATUS)));

					p.setReportId(cur.getString(cur
							.getColumnIndex(DetectorInspector.HistoryTable.REPORT_ID)));
					p.setBookingId(cur.getString(cur
							.getColumnIndex(DetectorInspector.HistoryTable.BOOKING_ID)));
					p.setSync_status(cur.getInt(cur
							.getColumnIndex(DetectorInspector.HistoryTable.SYNC_STATUS)));
					p.setReport_uuid(cur.getString(cur
							.getColumnIndex(DetectorInspector.HistoryTable.REPORT_UUID)));

					property_list.add(p);
				}
			}
		} finally {
			cur.close();
		}
		return property_list;
	}

	// display list on basis of display rank
	public static void updatePropertyInspectOnTheBasisOfDisplayRank(
			Context context, Inspection inspection, String reason) {
		ContentValues values = new ContentValues();

		values.put(DetectorInspector.PropertyTable.REASON, reason);

		String where = DetectorInspector.PropertyTable.REPORT_UUID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(inspection
				.getReport_uuid()) };
		context.getContentResolver().update(
				DetectorInspector.PropertyTable.CONTENT_URI, values, where,
				selectionArgs);
	}

	public static void updateLatLongValue(double[] latLng,Inspection mProperty, Context context, String mDate) {
		// TODO Auto-generated method stub

		ContentValues values = new ContentValues();

		values.put(DetectorInspector.PropertyTable.LATITUTE, latLng[0]);
		values.put(DetectorInspector.PropertyTable.LONGITUTE, latLng[1]);
		values.put(DetectorInspector.PropertyTable.REPORT_COMPLETED_DATE, mDate);
		values.put(DetectorInspector.PropertyTable.STATUS,
				Const.PROPERTY_STATUS_CODES.COMPLETED);

		String where = DetectorInspector.PropertyTable.REPORT_UUID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(mProperty
				.getReport_uuid()) };
		context.getContentResolver().update(
				DetectorInspector.PropertyTable.CONTENT_URI, values, where,
				selectionArgs);

	}

	public static List<String> getLatLongValue(Context context,
			String report_uuid) {
		List<String> latLng = new ArrayList<String>();

		String[] projection = { DetectorInspector.PropertyTable.LATITUTE,
				DetectorInspector.PropertyTable.LONGITUTE };

		String selection = DetectorInspector.PropertyTable.REPORT_UUID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(report_uuid) };
		String sortOrder = DetectorInspector.PropertyTable.REPORT_DATE
				+ " DESC";
		Cursor cur = context.getContentResolver().query(
				DetectorInspector.PropertyTable.CONTENT_URI, projection,
				selection, selectionArgs, sortOrder);
		try {
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {

					latLng.add(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.LATITUTE)));
					latLng.add(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.LONGITUTE)));

				}
			}
		} finally {
			cur.close();
		}
		return latLng;
	}

	/*
	 * Add photo
	 */
	public static void addPhotoId(Context context, String report_id,
			int section_id, int item_id, String photo_uuid, int photo_quality,
			String date, String report_uuid) {

		Log.e("TEST", "insert photo id for " + report_id + "_" + section_id
				+ "_" + item_id + "_" + photo_uuid);

		ContentValues values = new ContentValues();

		values.put(DetectorInspector.ReportPhotoTable.REPORT_ID, report_id);
		values.put(DetectorInspector.ReportPhotoTable.SECTION_ID, section_id);
		values.put(DetectorInspector.ReportPhotoTable.QUALITY, photo_quality);
		values.put(DetectorInspector.ReportPhotoTable.PHOTO_ID, photo_uuid);
		values.put(DetectorInspector.ReportPhotoTable.CREATED_DATE, date);
		values.put(DetectorInspector.ReportPhotoTable.REPORT_UUID, report_uuid);

		context.getContentResolver().insert(
				DetectorInspector.ReportPhotoTable.CONTENT_URI, values);
	}

	/*
	 * Get photo file name list by internal report UUID
	 */
	public static List<SyncPhoto> getPhotoListByReportUUID(Context context,	String report_uuid) {
		
		List<SyncPhoto> photo_list = new ArrayList<SyncPhoto>();

		String[] projection = new String[] { DetectorInspector.ReportPhotoTable.PHOTO_ID };
		String   selection = DetectorInspector.ReportPhotoTable.REPORT_UUID+ "=?";
		String[] selectionArgs = new String[] { report_uuid };

		Cursor cur = context.getContentResolver().query(DetectorInspector.ReportPhotoTable.CONTENT_URI, projection,	selection, selectionArgs, null);
		try {
			if (cur.getCount() > 0)    {
				while (cur.moveToNext()) {

					String file_name = cur.getString(cur.getColumnIndex(DetectorInspector.ReportPhotoTable.PHOTO_ID));
					SyncPhoto photo = new SyncPhoto();
					photo.setPhotoUUID(file_name);
					photo.setSynced(false);
					photo_list.add(photo);
				}

			}
		} finally {
			cur.close();
		}
		return photo_list;
	}

	/*
	 * Update the property status
	 */
	public static void updatePropertyReportId(Context context,
			String report_uuid, String value) {

		// Log.d("TEST","updatePropertyReportId start");
		ContentValues values = new ContentValues();
		values.put(DetectorInspector.PropertyTable.REPORT_ID, value);

		String where = DetectorInspector.PropertyTable.REPORT_UUID + "=?";
		String[] selectionArgs = new String[] { report_uuid };
		context.getContentResolver().update(
				DetectorInspector.PropertyTable.CONTENT_URI, values, where,
				selectionArgs);
		// Log.d("TEST","updatePropertyReportId end");
	}

	public static void updateIsNeedToSynchStatus(Context context,
			String reportUUID, int completlyNeedToSync) {
		// TODO Auto-generated method stub
		Log.d("TEST", "updatePropertySyncStatus start");
		ContentValues values = new ContentValues();
		values.put(DetectorInspector.PropertyTable.SYNC_STATUS,
				completlyNeedToSync);

		String where = DetectorInspector.PropertyTable.REPORT_UUID + "=?";
		String[] selectionArgs = new String[] { reportUUID };
		context.getContentResolver().update(
				DetectorInspector.PropertyTable.CONTENT_URI, values, where,
				selectionArgs);

	}

	// display list on basis of display rank
	public static void updateDisplayRank(Context context,
			List<Inspection> mInspectionsList, String date) {
		int i = 1;

		for (Inspection mInspection : mInspectionsList) {
			ContentValues values = new ContentValues();
			// Utils.showToastAlert(context, mInspection.getReport_uuid());
			values.put(DetectorInspector.PropertyTable.DISPLAY_RANK, i);

			String where = DetectorInspector.PropertyTable.REPORT_UUID + "=?";
			String[] selectionArgs = new String[] { String.valueOf(mInspection
					.getReport_uuid()) };
			context.getContentResolver().update(
					DetectorInspector.PropertyTable.CONTENT_URI, values, where,
					selectionArgs);
			i++;
		}

	}

	/*
	 * Update the property sync status
	 */
	public static void updatePropertySyncStatus(Context context,String report_uuid, int value) {

		// Log.d("TEST","updatePropertySyncStatus start");
		ContentValues values = new ContentValues();
		values.put(DetectorInspector.PropertyTable.SYNC_STATUS, value);

		String where = DetectorInspector.PropertyTable.REPORT_UUID + "=?";
		String[] selectionArgs = new String[] { report_uuid };
		context.getContentResolver().update(DetectorInspector.PropertyTable.CONTENT_URI, values, where,	selectionArgs);
		// Log.d("TEST","updatePropertySyncStatus end");
	}

	// Update Photo Synch Status
	public static void updatePhotoSynchStatus(Context context,
			String report_uuid, int value) {

		// Log.d("TEST","updatePropertySyncStatus start");
		ContentValues values = new ContentValues();
		// values.put(DetectorInspector.PropertyTable.PHOTOTOSYNC, value);

		String where = DetectorInspector.PropertyTable.REPORT_UUID + "=?";
		String[] selectionArgs = new String[] { report_uuid };
		context.getContentResolver().update(
				DetectorInspector.PropertyTable.CONTENT_URI, values, where,
				selectionArgs);
		Log.d(report_uuid + "<<TEST in Data Util Class",
				"updatePropertySyncStatus end" + value);
	}

	// getPreviousHistory

	public static boolean getPreviousHistory(Context context, String report_uuid) {

		String previousExpiryYear = null;
		String previousDetectorType = null;
		String previousNewExpiryYear = null;

		String[] projection = {
				DetectorInspector.PropertyTable.PREVIOUS_EXPIRY_YEAR,
				DetectorInspector.PropertyTable.PREVIOUS_NEW_EXPIRY_YEAR,
				DetectorInspector.PropertyTable.PREVIOUS_DETECTOR_TYPE };

		String selection = DetectorInspector.PropertyTable.REPORT_UUID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(report_uuid) };
		String sortOrder = DetectorInspector.PropertyTable.REPORT_DATE
				+ " DESC";
		Cursor cur = context.getContentResolver().query(
				DetectorInspector.PropertyTable.CONTENT_URI, projection,
				selection, selectionArgs, sortOrder);
		try {
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {

					previousExpiryYear = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.PREVIOUS_EXPIRY_YEAR));
					previousNewExpiryYear = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.PREVIOUS_NEW_EXPIRY_YEAR));
					previousDetectorType = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.PREVIOUS_DETECTOR_TYPE));

				}
			}
		} finally {
			cur.close();
		}

		if (previousDetectorType.length() > 0) {
			List<ReportItem> mItems = Utils.getPreviousHistory(
					previousExpiryYear, previousDetectorType,
					previousNewExpiryYear);
			DetectorInspectorApplication.getInstance().setHistoryData(mItems);
			// UserProfile.getInstance().setHistoryData(mItems);
			return true;

		} else {
			DetectorInspectorApplication.getInstance().setHistoryData(null);

			// UserProfile.getInstance().setHistoryData(null);
			return false;
		}

	}

	public static void deletePhoto(List<ReportPhoto> reportPhotos,
			Context context) {
		String where = DetectorInspector.ReportPhotoTable.PHOTO_ID + "=?";

		for (ReportPhoto mPhoto : reportPhotos) {
			String[] selectionArgs = new String[] { String.valueOf(mPhoto
					.getPhotoId()) };
			context.getContentResolver().delete(
					DetectorInspector.ReportPhotoTable.CONTENT_URI, where,
					selectionArgs);
		}

	}

	public static void deletePhotoOnLong(ReportPhoto reportPhotos,
			Context context) {
		String where = DetectorInspector.ReportPhotoTable.PHOTO_ID + "=?";

		String[] selectionArgs = new String[] { String.valueOf(reportPhotos
				.getPhotoId()) };
		context.getContentResolver().delete(
				DetectorInspector.ReportPhotoTable.CONTENT_URI, where,
				selectionArgs);

	}

	public static List<String> largestDateToCurrentDate(Context context,
			String date) {

		List<String> property_list = new ArrayList<String>();
		String[] projection = { DetectorInspector.PropertyTable.REPORT_DATE,
				DetectorInspector.PropertyTable.BOOKING_ID };

		String selection = DetectorInspector.PropertyTable.REPORT_DATE + "<?"
				+ " AND " + DetectorInspector.PropertyTable.SYNC_STATUS + "<?";
		Log.e("", date + " Selection date  " + date);

		String[] selectionArgs = new String[] { String.valueOf(date),
				Const.REPORT_SYNC_STATUS_CODES.SYNC_FINISHED + "" };
		String sortOrder = DetectorInspector.PropertyTable.REPORT_DATE
				+ " DESC";
		Cursor cur = context.getContentResolver().query(
				DetectorInspector.PropertyTable.CONTENT_URI, projection,
				selection, selectionArgs, sortOrder);

		try {
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {

					property_list
							.add(cur.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.REPORT_DATE)));
				}
			}
		} finally {
			cur.close();
		}
		return property_list;

	}

	public static boolean getValidation(Context context, String report_uuid) {
		int previousDetectorType = 0;

		String[] projection = { DetectorInspector.PropertyTable.VALIDATION_OFF };

		String selection = DetectorInspector.PropertyTable.REPORT_UUID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(report_uuid) };
		String sortOrder = DetectorInspector.PropertyTable.REPORT_DATE
				+ " DESC";
		Cursor cur = context.getContentResolver().query(
				DetectorInspector.PropertyTable.CONTENT_URI, projection,
				selection, selectionArgs, sortOrder);
		try {
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {
					previousDetectorType = cur
							.getInt(cur
									.getColumnIndex(DetectorInspector.PropertyTable.VALIDATION_OFF));
				}
			}
		} finally {
			cur.close();
		}

		if (previousDetectorType == 1) {
			return true;
		} else {
			return false;
		}

	}

	public static Inspection getAddressUsingUniqueCode(Context context,
			String uniqueCode) {

		String[] projection = { DetectorInspector.PropertyTable.STREET_NUMBER,
				DetectorInspector.PropertyTable.STREET_NAME,
				DetectorInspector.PropertyTable.SUBURB,
				DetectorInspector.PropertyTable.POSTCODE,
				DetectorInspector.PropertyTable.STATE,

				DetectorInspector.PropertyTable.BOOKING_ID,

				DetectorInspector.PropertyTable.REPORT_UUID,
				DetectorInspector.PropertyTable.REPORT_TIME };

		String selection = DetectorInspector.PropertyTable.REPORT_UUID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(uniqueCode) };
		String sortOrder = DetectorInspector.PropertyTable.REPORT_DATE
				+ " DESC";
		Cursor cur = context.getContentResolver().query(
				DetectorInspector.PropertyTable.CONTENT_URI, projection,
				selection, selectionArgs, sortOrder);
		Inspection p = new Inspection();
		try {

			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {

					p.setStreetNumber(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.STREET_NUMBER)));
					p.setStreetName(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.STREET_NAME)));
					p.setSuburb(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.SUBURB)));
					p.setPostCode(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.POSTCODE)));
					p.setState(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.STATE)));

					p.setBookingId(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.BOOKING_ID)));

					p.setReport_uuid(cur.getString(cur
							.getColumnIndex(DetectorInspector.PropertyTable.REPORT_UUID)));

					String time = cur
							.getString(cur
									.getColumnIndex(DetectorInspector.PropertyTable.REPORT_TIME));

					p.setTime(time);
					p.setKeytime(time);

				}
			}
		} finally {
			cur.close();
		}
		return p;
	}

	public static void updateSendBroadCastStatus(Context activity, String uuid) {
		// TODO Auto-generated method stub
		Context context = activity;
		ContentValues values = new ContentValues();

		values.put(DetectorInspector.PropertyTable.SEND_BROADCAST,
				Const.IS_NEED_TO_SYNC.PROGRESS_NEED_TO_SYNC);

		String where = DetectorInspector.PropertyTable.REPORT_UUID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(uuid) };
		context.getContentResolver().update(
				DetectorInspector.PropertyTable.CONTENT_URI, values, where,
				selectionArgs);

	}

	public static void importHistoryProperty(Activity activity,
			List<Inspection> property_list) {

		Context context = activity;
		context.getContentResolver().delete(
				DetectorInspector.HistoryTable.CONTENT_URI, null, null);
		ContentValues[] values = new ContentValues[property_list.size()];
		List<ContentValues> va = new ArrayList<ContentValues>();
		int count = 0;
		for (Inspection property : property_list) {

			ContentValues value = new ContentValues();

			String date = property.getInspectionDate();
			String inspectionDate = date.substring(0, 10);

			value.put(HistoryTable.REPORT_ID, property.getReportId());
			// double check report UUID
			if (property.getReport_uuid() == null) {
				property.setReport_uuid(Util.getUUID());
			}

			value.put(HistoryTable.REPORT_UUID, property.getReport_uuid());
			value.put(HistoryTable.PROPERTY_ID, property.getPropertyId());
			value.put(HistoryTable.UNIT_SHOP_NUMBER,
					property.getUnitShopNumber());
			value.put(HistoryTable.REPORT_TIME, property.getKeytime());
			value.put(HistoryTable.REPORT_DATE, Util.parseReportDate(date));

			value.put(HistoryTable.SYNC_STATUS, 30);
			value.put(HistoryTable.STATUS,
					Const.IS_NEED_TO_SYNC.COMPLETLY_NEED_TO_SYNC);

			String na = "";
			String unitShopNumber = property.getUnitShopNumber() != null ? property
					.getUnitShopNumber() : "";
			String streetNu = property.getStreetNumber() != null ? property
					.getStreetNumber() : "";
			if (unitShopNumber.length() > 0) {
				na = unitShopNumber;
			}
			if (streetNu.length() > 0) {
				if (na.length() > 0) {
					na = na + "/" + streetNu;
				} else {
					na = streetNu;
				}
			}

			value.put(HistoryTable.STREET_NUMBER, na.length() > 0 ? na : "");
			value.put(HistoryTable.STREET_NAME, property.getStreetName());
			value.put(HistoryTable.SUBURB, property.getSuburb());
			value.put(HistoryTable.POSTCODE, property.getPostCode());
			value.put(HistoryTable.STATE, property.getState());
			value.put(HistoryTable.KEY_NUMBER, property.getKeyNumber());
			value.put(HistoryTable.NOTES, property.getNotes());
			value.put(HistoryTable.HAS_LARGE_LADDER,
					property.isHasLargeLadder());
			value.put(HistoryTable.HAS_SEND_NOTIFICATION,
					property.isHasSendNotification());
			value.put(HistoryTable.OCCUPANT_NAME, property.getOccupantName());
			value.put(HistoryTable.OCCUPANT_EMAIL, property.getOccupantEmail());

			if (property.getContact() != null
					&& property.getContact().size() > 0) {
				String number = Utils.getContactNumber(property.getContact());
				value.put(HistoryTable.OCCUPANT_TELEPHONE_NO, number);
				value.put(HistoryTable.OCCUPANT_MOBILE_NO, "");
				value.put(HistoryTable.OCCUPANT_BUSINESS_NO, "");
				value.put(HistoryTable.OCCUPANT_HOME_NO, "");

			} else {
				value.put(HistoryTable.OCCUPANT_TELEPHONE_NO, "");
				value.put(HistoryTable.OCCUPANT_MOBILE_NO, "");
				value.put(HistoryTable.OCCUPANT_BUSINESS_NO, "");
				value.put(HistoryTable.OCCUPANT_HOME_NO, "");

			}

			value.put(HistoryTable.POSTAL_ADDRESS, property.getPostalAddress());
			value.put(HistoryTable.POSTAL_SUBURB, property.getPostalSuburb());
			value.put(HistoryTable.POSTAL_POST_CODE,
					property.getPostalPostCode());
			value.put(HistoryTable.POSTAL_STATE_ID, property.getPostalState());
			value.put(HistoryTable.POSTAL_COUNTRY, property.getPostalCountry());

			value.put(HistoryTable.AGENCY_NAME, property.getAgency()
					.getAgencyName());
			value.put(HistoryTable.AGENCY_ID, property.getAgency()
					.getAgencyId());

			value.put(HistoryTable.NO_OF_ALARAM, property.getNoOfAlaram());
			value.put(HistoryTable.REASON, "");

			value.put(HistoryTable.DISPLAY_RANK, property.getDisplayRank());
			value.put(HistoryTable.START_DISPLAY_RANK,
					property.getStartDisplayRank());
			value.put(HistoryTable.LATITUTE, property.getLat());
			value.put(HistoryTable.LONGITUTE, property.getLng());
			value.put(HistoryTable.BOOKING_ID, property.getBookingId());
			value.put(HistoryTable.INSPECTION_DATE, inspectionDate);

			int i = 0;
			String previousExpiryYear = null;
			String previousNewExpiryYear = null;
			String previousDetectorType = null;
			String previousServiceSheetId = null;

			for (ReportItem mReportItem : property.getPreviousHistory()) {
				if (i == 0) {
					previousExpiryYear = mReportItem.getExpiryYear();
					previousDetectorType = mReportItem.getDetectorType() + "";
					previousNewExpiryYear = mReportItem.getNewExpiryYear();
				} else {
					previousExpiryYear = previousExpiryYear + ","
							+ mReportItem.getExpiryYear();
					previousDetectorType = previousDetectorType + ","
							+ mReportItem.getDetectorType() + "";
					previousNewExpiryYear = previousNewExpiryYear + ","
							+ mReportItem.getNewExpiryYear();

				}

				previousServiceSheetId = mReportItem.getServiceSheetId();
				i++;
			}

			values[count] = value;
			va.add(value);
			count++;

		}

		Log.v("Test", values.length + "==============================");
		context.getContentResolver().bulkInsert(
				DetectorInspector.HistoryTable.CONTENT_URI, values);

	}

}
