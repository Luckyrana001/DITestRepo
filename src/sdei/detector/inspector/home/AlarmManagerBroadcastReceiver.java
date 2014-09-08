package sdei.detector.inspector.home;

import java.util.Calendar;

import com.detector.inspector.lib.model.Inspection;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.DetectorInspector_LoginActivity;
import sdei.detector.inspector.R;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.util.DatabaseUtil;
import sdei.detector.inspector.util.Utils;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

	final public static String ONE_TIME = "onetime";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {

			PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			PowerManager.WakeLock wl = pm.newWakeLock(
					PowerManager.PARTIAL_WAKE_LOCK, "Detector Inspector");
			// Acquire the lock
			wl.acquire();

			String uniqueCode = intent.getStringExtra("uniqueCode");

			if (uniqueCode != null) {
				Log.e("AlarmManagerBroadcastReceiver",
						"AlarmManagerBroadcastReceiver++++++++++++++++++++++++"
								+ uniqueCode);

				DetectorInspectorApplication.getInstance().setUniqueCode(
						uniqueCode);
				// UserProfile.getInstance();

				// UserProfile.getInstance().setUniqueCode(uniqueCode);
			}
			String message = getDataToDataBase(context, uniqueCode + "");
			UserProfile mProfile = DetectorInspectorApplication.getInstance();

			final String sendMessage = intent.getStringExtra("name") + " - "
					+ message
					+ " has not been completed within the appointment time.";
			Log.e("AlarmManagerBroadcastReceiver",
					"AlarmManagerBroadcastReceiver=============================="
							+ uniqueCode);
			Utils.sendMailToServerASYNC(context, sendMessage, uniqueCode);

			Utils.GoToNotificationActivity(context, message);

			wl.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

	public void CancelAlarm(Context context, String uniqueID) {
		Log.e("Alarm Cancelled", "ID is ---->>>>" + uniqueID);
		int propertyId = Integer.parseInt(uniqueID);
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, propertyId,
				intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

	public void setOnetimeTimer(Context context, int secs, int uniqueId,
			String uuid) {
		Log.e("AlarmManagerBroadcastReceiver",
				"AlarmManagerBroadcastReceiver=============================="
						+ uniqueId);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		calendar.add(Calendar.SECOND, secs);
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		intent.putExtra(ONE_TIME, Boolean.TRUE);
		intent.putExtra("uniqueCode", uuid);
		PendingIntent pi = PendingIntent.getBroadcast(context, uniqueId,
				intent, 0);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
	}

	public void setOnetimeTimer(Context context, int seconds, int hours,
			int minutes, int uniqueCode, String uuid) {
		Log.e("AlarmManagerBroadcastReceiver",
				"AlarmManagerBroadcastReceiver=============================="
						+ uniqueCode);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.HOUR, hours);
		calendar.add(Calendar.MINUTE, minutes);
		calendar.add(Calendar.SECOND, seconds);
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		intent.putExtra(ONE_TIME, Boolean.TRUE);
		intent.putExtra("uniqueCode", uuid);
		PendingIntent pi = PendingIntent.getBroadcast(context, uniqueCode,
				intent, 0);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);

	}

	public void setOnetimeTimerMillis(Context context, int seconds, int hours,
			int minutes, int uniqueCode, String uuid, long timeMillis) {
		UserProfile mProfile = DetectorInspectorApplication.getInstance();
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		calendar.add(Calendar.MINUTE, 20);
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		intent.putExtra(ONE_TIME, Boolean.TRUE);
		intent.putExtra("uniqueCode", uuid);
		intent.putExtra("name", mProfile.getTechnicianName());
		PendingIntent pi = PendingIntent.getBroadcast(context, uniqueCode,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
	}

	String getDataToDataBase(Context context, String uniqueCode) {
		Inspection inspection = DatabaseUtil.getAddressUsingUniqueCode(context,
				uniqueCode);

		
		String keytime,getStreetNumber,getStreetName,getSuburb,getState,getPostCode;
		
		if(inspection.getKeytime().equalsIgnoreCase(null)){
			keytime="";
		}else{
			keytime=inspection.getKeytime();
		}
		if(inspection.getStreetNumber().equalsIgnoreCase(null)){
			getStreetNumber="";
		}else{
			getStreetNumber=inspection.getStreetNumber();
		}
		if(inspection.getStreetName().equalsIgnoreCase(null)){
			getStreetName="";
		}else{
			getStreetName=inspection.getStreetName();
		}
		if(inspection.getSuburb().equalsIgnoreCase(null)){
			getSuburb="";
		}else{
			getSuburb=inspection.getSuburb();
		}
		if(inspection.getState().equalsIgnoreCase(null)){
			getState="";
		}else{
			getState=inspection.getState();
		}
		if(inspection.getPostCode().equalsIgnoreCase(null)){
			getPostCode="";
		}else{
			getPostCode=inspection.getPostCode();
		}
		
		//String msg = inspection.getKeytime() + " - "
	//			+ inspection.getStreetNumber() + " "
		//		+ inspection.getStreetName() + ", " + inspection.getSuburb()
		//		+ ", " + inspection.getState() + " " + inspection.getPostCode();
		
 
		String msg = keytime+ " - "
				+ getStreetNumber+ " "
				+ getStreetName+ ", " +getSuburb
				+ ", " + getState+ " " +getPostCode;
		
		msg = msg.replace("null,", "");
		msg = msg.replace(",,", ",");
		
		
		
		return msg;

	}
}