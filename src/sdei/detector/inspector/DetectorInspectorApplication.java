package sdei.detector.inspector;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import sdei.detector.pref.PreferenceConnector;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;

@ReportsCrashes(formUri = "http://www.bugsense.com/api/acra?api_key=ba17a1ce", formKey = "dEhaZWFuQVV4WHA5OHZvbHBlSm1CMXc6MQ")
public class DetectorInspectorApplication extends Application {
	public static UserProfile instance;
	public static Context mContext;
	protected static final Object DOUBLE_LINE_SEP = "========================================================================";
	protected static final Object SINGLE_LINE_SEP = "------------------------------------------------------------------------";
	protected static final String EXTRA_CRASHED_FLAG = "Error Message";
	public DetectorInspectorApplication() {
		// TODO Auto-generated constructor stub

	}

	public static UserProfile getInstance() {
		if (instance == null) {
			instance = new UserProfile();
			// getDataFromPref();
		} else {

			// saveDataToPref();
		}
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);
		mContext = DetectorInspectorApplication.this;
		instance = new UserProfile();
		// instance.setContext(mContext);
		instance = getInstance();
		
		//Handle Uncaught Exception
	//	Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
		//		@Override
//				public void uncaughtException(Thread thread, final Throwable ex) {    
//					StackTraceElement[] arr = ex.getStackTrace();
//					final StringBuffer report = new StringBuffer(ex.toString());
//					final String lineSeperator = "-------------------------------\n\n";
//					report.append(DOUBLE_LINE_SEP);
//					report.append("--------- Stack trace ---------\n\n");
//					for (int i = 0; i < arr.length; i++) {
//						report.append("    ");
//						report.append(arr[i].toString());
//						report.append(SINGLE_LINE_SEP);    
//					}
//					report.append(lineSeperator);  
//					// If the exception was thrown in a background thread inside
//					// AsyncTask, then the actual exception can be found with getCause
//					report.append("--------- Cause ---------\n\n");   
//					Throwable cause = ex.getCause();
//					if (cause != null) {
//						report.append(cause.toString());
//						report.append(DOUBLE_LINE_SEP);           
//						arr = cause.getStackTrace();
//						for (int i = 0; i < arr.length; i++) {
//							report.append("    ");
//							report.append(arr[i].toString());
//							report.append(SINGLE_LINE_SEP);                                                                                
//						}
//					}
//					// Getting the Device brand,model and sdk verion details.
//					report.append(lineSeperator);
//					report.append(Build.BRAND);
//					report.
//					append(SINGLE_LINE_SEP);
//					report.append(Build.DEVICE);
//					report.append(SINGLE_LINE_SEP);      
//					report.append(Build.MODEL);
//					report.append(SINGLE_LINE_SEP);
//					report.append(Build.ID);
//					report.append(SINGLE_LINE_SEP);
//					report.append(Build.PRODUCT);   
//					report.append(SINGLE_LINE_SEP);  
//					report.append(lineSeperator);
//					report.append(Build.VERSION.SDK);
//					report.append(SINGLE_LINE_SEP);      
//					report.append(Build.VERSION.RELEASE);
//					report.append(SINGLE_LINE_SEP);
//					report.append(Build.VERSION.INCREMENTAL);
//					report.append(SINGLE_LINE_SEP);
//					report.append(lineSeperator);         
//
//					Log.e("Report ::", report.toString());                          
//				
//					Intent crashedIntent = new Intent(DetectorInspectorApplication.this, DetectorInspector_LoginActivity.class);
//					crashedIntent.putExtra(EXTRA_CRASHED_FLAG,  "Unexpected Error occurred.");
//					crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//					
//					crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					Log.e("Before startting app ","before");
//					startActivity(crashedIntent);         
//	      
//	                        	 System.exit(1);
//					// If you don't kill the VM here the app goes into limbo        
//				} 
	//		});
		
		
		
		
//		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//			@Override
//			public void uncaughtException(Thread thread, Throwable ex) {
//				Log.wtf("Got in exception", "Got in exception");
//			
//				
//
//				
//				
//				
//				
//				Intent crashedIntent = new Intent(
//						DetectorInspectorApplication.this,
//						DetectorInspector_LoginActivity.class);
//				crashedIntent.putExtra("is_crashed",
//						"Unexpected Error occurred.");
//				crashedIntent
//						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//				crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			
//				Log.wtf("Got in exception", "Before Restart");
//				mContext.startActivity(crashedIntent);
//				
//			}
//		});
	}

	static void saveDataToPref() {
		Gson gson = new Gson();
		final String json = gson.toJson(instance);
		Log.e("Save data to pref called", json);
		new Thread(new Runnable() {
			@Override
			public void run() 
			{	
				Log.e("PreferenceConnector.USER_OBJ", json);
				PreferenceConnector.writeString(mContext,PreferenceConnector.USER_OBJ, json); 
			}
		}).start();
	}

	static void getDataFromPref() 
	{
		Gson gson = new Gson();
		String json = PreferenceConnector.readString(mContext,PreferenceConnector.USER_OBJ, gson.toJson(new UserProfile()));
		Log.e("Get data from pref called", json);
		Log.wtf("Get data from pref called", json);
		instance = gson.fromJson(json, UserProfile.class);
		instance.setContext(mContext);
	}
}