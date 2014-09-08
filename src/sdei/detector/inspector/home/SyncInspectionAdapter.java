package sdei.detector.inspector.home;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sdei.detector.inspector.R;
import sdei.detector.inspector.sync.SyncReport;
import sdei.detector.inspector.util.Const;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.detector.inspector.lib.widget.SimpleListAdapter;

public class SyncInspectionAdapter extends SimpleListAdapter implements	SimpleListAdapter.ViewBinder 
		{
	private static final int MAX = 10;
	private static final int PADDING_LEFT = -10;
	private WeakReference<Context> mContext;
	private AdapterCallback mCallback;

	public SyncInspectionAdapter(Context context,List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)    
	{

		super(context, data, resource, from, to);
		mContext = new WeakReference<Context>(context);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean setViewValue(View view, Object data, final int position)                {
		final View v = view;
		if (data == null) 
		{
			v.setVisibility(View.GONE);
			return true;
		}
		
		v.setVisibility(View.VISIBLE);
		if (v instanceof TextView) 
		{
			if (data instanceof String) {
				if (v.getId() == R.id.sync_report_street) 
				{
					((TextView) v).setText((String) data);					
				} 				
				else if (v.getId() == R.id.sync_statucomplete_date)				
				{
					((TextView) v).setText((String) data);
				}

			} else if (v.getId() == R.id.sync_report_finished_photo_desp)        
			{
				if (data instanceof HashMap) {
					final HashMap map = (HashMap) data;
					final int isNeed = (Integer) map.get("status");
					final SyncReport sync_report = (SyncReport) map	.get("synch_report");
					if (isNeed == Const.IS_NEED_TO_SYNC.COMPLETLY_NEED_TO_SYNC)  
					{
						String count = String.valueOf(sync_report.getPhotos().size());
						((TextView) v).setText(count);
					} 
					else 
					{
						String count = String.valueOf(sync_report.getUploadedPhotoCount());
						((TextView) v).setText(count);
					}

				} else {
					// Nothing
				}
			} else if (v.getId() == R.id.sync_report_finished_status) {
				if (data instanceof HashMap) {
					final HashMap map = (HashMap) data;
					final int isNeed = (Integer) map.get("status");
					final SyncReport sync_report = (SyncReport) map	.get("synch_report");

					switch (sync_report.getSyncStatus()) {
					
					case Const.REPORT_SYNC_STATUS_CODES.SYNC_NOT_STARTED:

						if (isNeed == Const.IS_NEED_TO_SYNC.COMPLETLY_NEED_TO_SYNC) 
						{
							((TextView) v).setText(Const.REPORT_SYNC_STATUS_DESP.SYNC_FINISHED);
							((TextView) v).setTextColor(0xFF00FF00);
						}
					else
						{
							((TextView) v).setText(Const.REPORT_SYNC_STATUS_DESP.SYNC_NOT_STARTED);
							((TextView) v).setTextColor(0xFF8AA2AA);
						}
						break;
					case Const.REPORT_SYNC_STATUS_CODES.SYNC_IN_PROGRESS:
						   ((TextView) v).setText(Const.REPORT_SYNC_STATUS_DESP.SYNC_IN_PROGRESS);
						   ((TextView) v).setTextColor(0xFF00FF00);
						break;
					case Const.REPORT_SYNC_STATUS_CODES.SYNC_FINISHED:
						((TextView) v).setText(Const.REPORT_SYNC_STATUS_DESP.SYNC_FINISHED);
						((TextView) v).setTextColor(0xFF00FF00);
						break;
					case Const.REPORT_SYNC_STATUS_CODES.SYNC_FAILED:
						((TextView) v).setText(Const.REPORT_SYNC_STATUS_DESP.SYNC_FAILED);
						((TextView) v).setTextColor(0xFFFF0000);
						//
						break;
					}

				} else {
					// Nothing
				}
			} else if (data instanceof SyncReport) {
				final SyncReport sync_report = (SyncReport) data;

				if (v.getId() == R.id.sync_report_finished_photo_desp1) 
				{
					((TextView) v).setText(" / "+ sync_report.getPhotos().size());
					
				} else if (v.getId() == R.id.sync_report_finished_status) {
					switch (sync_report.getSyncStatus()) {
					case Const.REPORT_SYNC_STATUS_CODES.SYNC_NOT_STARTED:
						((TextView) v).setText(Const.REPORT_SYNC_STATUS_DESP.SYNC_NOT_STARTED);
						((TextView) v).setTextColor(0xFF8AA2AA);
						break;
					case Const.REPORT_SYNC_STATUS_CODES.SYNC_IN_PROGRESS:
						((TextView) v) .setText(Const.REPORT_SYNC_STATUS_DESP.SYNC_IN_PROGRESS);
						((TextView) v) .setTextColor(0xFF00FF00);
						break;
					case Const.REPORT_SYNC_STATUS_CODES.SYNC_FINISHED:
						((TextView) v).setText(Const.REPORT_SYNC_STATUS_DESP.SYNC_FINISHED);
						((TextView) v).setTextColor(0xFF00FF00);
						break;
					case Const.REPORT_SYNC_STATUS_CODES.SYNC_FAILED:
						((TextView) v).setText(Const.REPORT_SYNC_STATUS_DESP.SYNC_FAILED);
						((TextView) v).setTextColor(0xFFFF0000);
						//
						break;
					}
				} else if (v.getId() == R.id.sync_restart_btn) {
					v.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							mCallback.restartSync(sync_report);
						}
					});
				} else if (v.getId() == R.id.sync_status_complete) {
					((TextView) v).setText("Completed on ");
				}
			}
		} else if (v instanceof LinearLayout) {
			if (v.getId() == R.id.sync_report_finished_background) {
				// create the report section item finished percentage bar
				final LinearLayout box = (LinearLayout) v;
				box.removeAllViews();

				if (data instanceof HashMap) {
					final HashMap map = (HashMap) data;
					final int isNeed = (Integer) map.get("status");
					final SyncReport sync_report = (SyncReport) map
							.get("synch_report");

					for (int i = 0; i < MAX; i++) {
						final ImageView iv = new ImageView(mContext.get());
						if (i > 0) {
							iv.setPadding(PADDING_LEFT, 0, 0, 0);
						}
						if (i < MAX - 1) {
							if (isNeed == Const.IS_NEED_TO_SYNC.COMPLETLY_NEED_TO_SYNC) {
								iv.setImageResource(R.drawable.im_synching_meter_green_left);
							} else {
								iv.setImageResource(R.drawable.im_synching_meter_black_left);
							}
						} else {
							if (isNeed == Const.IS_NEED_TO_SYNC.COMPLETLY_NEED_TO_SYNC) {
								iv.setImageResource(R.drawable.im_synching_meter_green_right);
							} else {
								iv.setImageResource(R.drawable.im_synching_meter_black_right);
							}
						}
						box.addView(iv);
					}

				} else {
					// Nothing
				}

			} else if (v.getId() == R.id.sync_report_finished_percentage) {
				
				final SyncReport sync_report = (SyncReport) data;
				
				// The additional one is for load report
				int total = sync_report.getPhotos().size() + 1;
				int count = sync_report.getUploadedPhotoCount();
				count = (sync_report.isReportAdded()) ? count + 1 : count;

				final LinearLayout finish_box = (LinearLayout) v;
				finish_box.removeAllViews();
				
				float percent = 0.0f;
				if(total > 0){
					percent = 1.0f * count / total * MAX;
				}
				Log.d("TEST","percent is: " + percent);
				int sum = Math.round(percent);
				Log.d("TEST","sum is: " + sum);
				for (int i = 0; i < sum; i++) {
					final ImageView iv = new ImageView(mContext.get());
					if (i > 0) {
						iv.setPadding(PADDING_LEFT, 0, 0, 0);
					}
					if (i < sum - 1) {
						switch(sync_report.getSyncStatus()){
						case Const.REPORT_SYNC_STATUS_CODES.SYNC_FINISHED:
						case Const.REPORT_SYNC_STATUS_CODES.SYNC_IN_PROGRESS:
							iv.setImageResource(R.drawable.im_synching_meter_green_left);
							break;
						case Const.REPORT_SYNC_STATUS_CODES.SYNC_FAILED:
							iv.setImageResource(R.drawable.im_synching_meter_red_left);
							break;
						}
					} else {
						switch(sync_report.getSyncStatus()){
						case Const.REPORT_SYNC_STATUS_CODES.SYNC_FINISHED:
						case Const.REPORT_SYNC_STATUS_CODES.SYNC_IN_PROGRESS:
							iv.setImageResource(R.drawable.im_synching_meter_green_right);
							break;
						case Const.REPORT_SYNC_STATUS_CODES.SYNC_FAILED:
							iv.setImageResource(R.drawable.im_synching_meter_red_right);
							break;
						}
					}
					finish_box.addView(iv);
				}
			}
		} else if (v instanceof ImageView) {
			if (data instanceof Integer) {
				((ImageView) v).setImageResource(((Integer) data).intValue());
			} else if (data instanceof SyncReport) {
				final SyncReport report = (SyncReport) data;
				if (v.getId() == R.id.sync_report_check) {
					
					/*need to sync enteries check */
					if (report.isNeedToSync()) {
						v.setBackgroundResource(R.drawable.im_select_check_active);
					} else {
						v.setBackgroundResource(R.drawable.im_select_check_inactive);
					}
					v.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) 
						{
							mCallback.addOrRemoveSyncReport(report, position);
						}
					});
				}
			}
		} else if (v instanceof ProgressBar) {
			if (v.getId() == R.id.sync_progressBar) {
				if (data instanceof Boolean) {
					if ((Boolean) data) 
					{
						((ProgressBar) v).setVisibility(View.VISIBLE);
					} 
					
					else 
					{
						// Nothing happen
						((ProgressBar) v).setVisibility(View.GONE);
					}
				}
			}
		}
		return true;
	}

	public void setAdapterCallback(AdapterCallback callback) 
	{
		mCallback = callback;
	}

	public static interface AdapterCallback 
	{
		public void addOrRemoveSyncReport(SyncReport report, int pos);

		public void truevalue();

		public void restartSync(SyncReport report);
	}

}
