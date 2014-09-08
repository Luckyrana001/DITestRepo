package sdei.detector.inspector.sync.service;

import java.util.ArrayList;
import java.util.List;

import sdei.detector.inspector.DetectorInspectorApplication;
import sdei.detector.inspector.UserProfile;
import sdei.detector.inspector.sync.service.engine.ISyncEngine;
import sdei.detector.inspector.sync.service.ws.AddReportSyncTask;
import sdei.detector.inspector.sync.service.ws.ForgotPassTask;
import sdei.detector.inspector.sync.service.ws.LoadHistoryDetailTask;
import sdei.detector.inspector.sync.service.ws.LoadPropertyBookingTask;
import sdei.detector.inspector.sync.service.ws.LoadPropertyTask;
import sdei.detector.inspector.sync.service.ws.LoginTask;
import sdei.detector.inspector.sync.service.ws.SaveLocation;
import sdei.detector.inspector.sync.service.ws.SendNotification;
import sdei.detector.inspector.sync.service.ws.UnAllocatedProperty;
import sdei.detector.inspector.sync.service.ws.UploadPhotoSyncTask;
import sdei.detector.inspector.sync.service.ws.WSClientSyncTask;
import sdei.detector.inspector.sync.service.ws.WSRequest;
import sdei.detector.inspector.util.Const;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

public class SyncService extends Service implements WSClientSyncTask.WSClientSyncCallback {
	
	private static final String tag = SyncService.class.getSimpleName();	
	private static final int MSG_SYNC = 1000;	
	private List<WSClientSyncTask> mSyncTaskList = new ArrayList<WSClientSyncTask>();

	private Handler mHandler = new Handler()    {

		@Override
		public void handleMessage(Message msg)  {
			switch (msg.what)  {
			
			case MSG_SYNC:
				// Log.d("TEST","current sync task list");
				
				for (WSClientSyncTask task : mSyncTaskList)    
				{
					// Log.d("TEST", "group id: " + task.getSyncGroupId() +
					// " sync id: "
					// + task.getSyncId());
				}				
				if (mSyncTaskList.size() > 0) 
				{
					WSClientSyncTask sync_task = mSyncTaskList.get(0);
					if (sync_task.getStatus() == Status.PENDING) 
					{
						sync_task.execute(sync_task.getRequest());
					}
				}
				break;
			}
		}
	};

	    private final ISyncEngine.Stub m_binder = new ISyncEngine.Stub() {

		@Override
		public void cancelSync(String sync_id) throws RemoteException    {
			// TODO Auto-generated method stub
			for (WSClientSyncTask task : mSyncTaskList)                  {
			
				Log.d("TEST", "check task: " + task.getSyncId());
				
				 if (task.getSyncId().equals(sync_id))                   {
					 Log.d("TEST", "trying to cancel sync task: " + sync_id);
					if (task.getStatus() == Status.PENDING) 
					{
						mSyncTaskList.remove(task);
					} else {
						task.cancel(true);
						if (task.isCancelled())  {
							
							Log.d("TEST", "sync task: " + sync_id + " is canceled");
							mSyncTaskList.remove(task);
							Intent resp_intent = new Intent();
							resp_intent.putExtra("sync_id", sync_id);
							resp_intent.setAction(Const.WS_RESPONSE_ACTIONS.WS_SYNC_TASK_CANCELLED);
							DetectorInspectorApplication detectorInspectorApplication = 
									(DetectorInspectorApplication) ((Activity) getApplicationContext())	.getApplication();

							DetectorInspectorApplication.getInstance().getContext().sendBroadcast(resp_intent);
							// UserProfile.getInstance().getContext()
							// .sendBroadcast(resp_intent);

					} else {
							 Log.d("TEST", "sync task already start and can't be cancelled: "	 + sync_id);
						   }
					}
				} 
				 else 
				{					
					Log.d("TEST","sync task already start and can't be cancelled: "	+ sync_id);
				}
			}
		}

		@Override
		public String login(WSRequest request, String email, String pass)
				throws RemoteException {
            Log.i("inside login ", "sync service class login method");
			try {

				WSClientSyncTask client_task  =  new LoginTask(request, email, pass);
				addSyncTask(client_task);
				return client_task.getSyncId();
				
			} catch (IllegalStateException ie) {
				ie.printStackTrace();
			}
			return null;
		}

		@Override
		public String forgotPass(WSRequest request, String email)
				throws RemoteException {
		

			try {
				WSClientSyncTask client_task = new ForgotPassTask(request,
						email);
				addSyncTask(client_task);
				return client_task.getSyncId();
			} catch (IllegalStateException ie) {
				ie.printStackTrace();
			}

			return null;
		}

		@Override
		public String getProperty(WSRequest request, String technicianId)	throws RemoteException {
			try {
				
				WSClientSyncTask client_task = new LoadPropertyTask(request,technicianId);
				addSyncTask(client_task);
				
				return client_task.getSyncId();
			} catch (IllegalStateException ie) {
				ie.printStackTrace();
			}

			return null;
		}

		@Override
		public String[] uploadPhoto(WSRequest request, String report_uuid,String photo_uuid, String report_id) throws RemoteException {
			
			
			try {
				String group_id = "sync_report_" + report_uuid;
				WSClientSyncTask client_task = new UploadPhotoSyncTask(request,	group_id, report_uuid, report_id, photo_uuid,SyncService.this);
				addSyncTask(client_task);
				
				return new String[] { client_task.getSyncGroupId(),	client_task.getSyncId() };
			
			}   catch (IllegalStateException ie) 
			{
				ie.printStackTrace();
			}
			
			return null;
		}

		/*
		 * @params
		 *  WSRequest = a getter setter of parcelable type which contain 
				a url, http method type and reponse type
			WSResponse= a getter setter of parcelable type which contains
			   http response and json result
			   
			 WsClient is the json parser class 
		 */
		@Override
		public String[] addReport (	WSRequest request, 	String report_uuid,	String path, String lat, 	String lng, 
				
				String bookingId,
				String technicianId, 
				String servString) throws RemoteException 
				{
			try {
				String group_id = "sync_report_" + report_uuid;
				
				/*
				 * WSClientSyncTask is the base class of all async task used in this application 
				 * */
				WSClientSyncTask client_task = new AddReportSyncTask(
						request,
						group_id, 
						report_uuid, 
						path, 
						lat, 
						lng, 
						bookingId,
						technicianId, 
						servString);
				
				addSyncTask(client_task);
				
				return new String[] 
						{ 
						client_task.getSyncGroupId(),
						client_task.getSyncId() 
						};
			} catch (IllegalStateException ie) {
				ie.printStackTrace();
			}
			return null;
		}

		@Override
		public void cancelSyncGroup(String sync_group_id)  throws RemoteException {

			// TODO Auto-generated method stub
			for (WSClientSyncTask task : mSyncTaskList) {
				String sync_id = task.getSyncId();
				// Log.d("TEST", "check task: " + sync_id);
				if (task.getSyncGroupId().equals(sync_group_id)) {
					// Log.d("TEST", "trying to cancel sync task: " + sync_id);
					if (task.getStatus() == Status.PENDING) 
					{
						mSyncTaskList.remove(task);
					} else {
						task.cancel(true);
						if (task.isCancelled()) {
							// Log.d("TEST", "sync task: " + sync_id
							// + " is canceled");
							// mSyncTaskList.remove(task);
							
							Intent resp_intent = new Intent();
							resp_intent.putExtra("sync_id", sync_id);
							resp_intent.setAction(Const.WS_RESPONSE_ACTIONS.WS_SYNC_TASK_CANCELLED);
							DetectorInspectorApplication.getInstance().getContext().sendBroadcast(resp_intent);
							// UserProfile.getInstance().getContext().sendBroadcast(resp_intent);

						} 
					   	       else 
						{
							// Log.d("TEST",
							// "sync task already start and can't be cancelled: "
							// + sync_id);
						}
					}
				} else {
					// Log.d("TEST",
					// "sync task already start and can't be cancelled: "
					// + sync_id);
				}
			}

		}

		@Override
		public  String getBookingProperty(WSRequest request, String technicianId)	throws RemoteException  {
			try {
				Log.i("Technician id",technicianId);
				WSClientSyncTask client_task  =  new LoadPropertyBookingTask(request, technicianId);
				addSyncTask(client_task);
				return client_task.getSyncId();	
			} 
			catch (IllegalStateException ie)
			{
				ie.printStackTrace();
			}
			return null;
		}

		@Override
		public String unAllocatedBooking(WSRequest request,
				String technicianId, String bookingId, String reason,
				String propertyId, String key) throws RemoteException {
			try {
				WSClientSyncTask client_task = new UnAllocatedProperty(request,
						technicianId, bookingId, reason, propertyId, key);
				addSyncTask(client_task);
				return client_task.getSyncId();
			} catch (IllegalStateException ie) {
				ie.printStackTrace();
			}

			return null;
		}

		@Override
		public String sendNotificationMessage(WSRequest request, String message)
				throws RemoteException {
			try {
				WSClientSyncTask client_task = new SendNotification(request,
						message);
				addSyncTask(client_task);
				return client_task.getSyncId();
			} catch (IllegalStateException ie) {
				ie.printStackTrace();
			}
			return null;
		}

		@Override
		public String saveLocation(WSRequest request, String lat, String lng,
				String technicianId) throws RemoteException {
			try {
				WSClientSyncTask client_task = new SaveLocation(request, lat,
						lng, technicianId);
				addSyncTask(client_task);
				return client_task.getSyncId();
			} catch (IllegalStateException ie) {
				ie.printStackTrace();
			}
			return null;
		}

		@Override
		public String getHistoryProperty(WSRequest request, String technicianId)
				throws RemoteException {
			try {
				WSClientSyncTask client_task = new LoadHistoryDetailTask(
						request, technicianId);
				addSyncTask(client_task);
				return client_task.getSyncId();
			} catch (IllegalStateException ie) {
				ie.printStackTrace();
			}
			return null;
		}

	};

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		startService(new Intent(getApplicationContext(), SyncService.class));
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		 Log.d("TEST", "return binder");
		return m_binder;
	}

	private void addSyncTask(WSClientSyncTask client_task) {
		Log.d("TEST","add sync task: " + client_task.getSyncId());

		mSyncTaskList.add(client_task);

		client_task.setCallback(this);

		mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SYNC), 1000);
	}

	@Override
	public void syncFinished(String sync_id)  {
		// Log.d("TEST","sync task: " + sync_id + " finished");
		if (mSyncTaskList.size() > 0) {
			if (mSyncTaskList.get(0).getSyncId().equals(sync_id)) 
			{
				Log.d("TEST on SyncService", "sync task: " + sync_id + " removed from list");
				mSyncTaskList.remove(0);
			}
		}
		mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SYNC), 1000);
	}

}
