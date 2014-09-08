package sdei.detector.inspector.home;

import java.util.Calendar;

import sdei.detector.inspector.util.Const;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class LocationTrackerBroadcastReceiver extends BroadcastReceiver {

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
			
			Intent resp_intent = new Intent();
			resp_intent.putExtra("response", true);
			resp_intent
					.setAction(Const.WS_RESPONSE_ACTIONS.WS_LOAD_TRACK_LOCATION_SUCESS);
			context.sendBroadcast(resp_intent);
			
			wl.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, LocationTrackerBroadcastReceiver.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context,786, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}



	public void setOnetimeTimer(Context context,int minutes) {
		Log.e("Location",
				"AlarmManagerBroadcastReceiver==============================Location"
						);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
	
		calendar.add(Calendar.MINUTE, minutes);
		
		Intent intent = new Intent(context, LocationTrackerBroadcastReceiver.class);
		intent.putExtra(ONE_TIME, Boolean.TRUE);
		
		PendingIntent pi = PendingIntent.getBroadcast(context,786,
				intent, 0);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);

	}

	

}